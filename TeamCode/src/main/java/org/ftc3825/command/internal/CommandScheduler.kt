package org.ftc3825.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.UpdateGlobalsCommand
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.fakehardware.FakeTelemetry
import org.ftc3825.sim.SimulatedHardwareMap
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.subsystem.TelemetrySubsystem
import org.ftc3825.util.Globals

object  CommandScheduler {
    var lastTime = 0.0
    var initialized = false

    lateinit var hardwareMap: HardwareMap

    var commands = arrayListOf<Command>()
    private var triggers = arrayListOf<Trigger>()
    private var subsystemsToUpdate = arrayListOf<Subsystem<*>>()

    fun init(hardwareMap: HardwareMap){
        if(!initialized) {
            TelemetrySubsystem.init(hardwareMap)

            this.hardwareMap = hardwareMap
            schedule(UpdateGlobalsCommand())
            if(hardwareMap is FakeHardwareMap){
                TelemetrySubsystem.telemetry = FakeTelemetry()
            }
        }
        initialized = true
    }

    fun addTrigger(trigger: Trigger) = triggers.add(trigger)

    fun schedule(command: Command) {
        command.initialize()

        command.requirements.forEach { requirement ->
            requirement.init(hardwareMap)
            commands.filter { it.requirements.contains(requirement)}
                .forEach{
                    it.end(interrupted = true)
                    commands.remove(it)
                }

            if ( !(requirement in subsystemsToUpdate) ){
                subsystemsToUpdate.add(requirement)
            }
        }

        command.readOnly.forEach {
            it.init(hardwareMap)
            if ( !(it in subsystemsToUpdate) ){
                subsystemsToUpdate.add(it)
            }
        }

        commands.add(command)
    }

    private fun updateCommands(deltaTime: Double) {
        subsystemsToUpdate.forEach {
            it.update(deltaTime)
        }
        var i = 0
        while(i < commands.size){
            val command = commands[i]


            command.execute()
            
            if(command.isFinished()){
                command.end(interrupted = false)
                commands.remove(command)

                subsystemsToUpdate = arrayListOf()
                commands.forEach { command ->
                   command.requirements.forEach { requirement ->
                       if(requirement !in subsystemsToUpdate ){
                           subsystemsToUpdate.add(requirement)
                       }
                   }

                }
            }
            else{ i ++ }

        }
    }
    private fun updateTriggers() {
        triggers.forEach {
            it.update()
            if (it.isTriggered) {
                schedule(it.command)
            }
        }
    }
    fun update() {
        val deltaTime = Globals.timeSinceStart - lastTime

        if(hardwareMap is FakeHardwareMap){
            FakeHardwareMap.updateDevices()
            SimulatedHardwareMap.updateDevices()
        }

        updateTriggers()
        lastTime = Globals.timeSinceStart
        updateCommands(deltaTime)

        TelemetrySubsystem.update()

    }

    fun end() {
        commands.forEach { it.end(true) }
        commands = arrayListOf<Command>()
    }

    fun end(command: Command){
        val toRemove = commands.filter { it == command }.firstOrNull()
        if(toRemove != null ){
            toRemove.end(true)
            commands.remove(toRemove)
        }
    }

    fun status(): String {
        var output = "running commands: ["
        commands.forEach { output += "$it, " }
        output += "]\ntriggers: ["
        triggers.forEach { output += "$it, " }
        output += "]\nsubsystems: ["
        subsystemsToUpdate.forEach { output += "$it, " }
        output += "]\n time: ${Globals.timeSinceStart}"

        return output
    }

}