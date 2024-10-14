package org.ftc3825.command.internal

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.UpdateGlobalsCommand
import org.ftc3825.fakehardware.FakeHardwareMap
import org.ftc3825.fakehardware.FakeTelemetry
import org.ftc3825.sim.SimulatedHardwareMap
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.subsystem.Telemetry
import org.ftc3825.util.Globals

object  CommandScheduler {
    var lastTime = 0.0

    lateinit var hardwareMap: HardwareMap

    var commands = arrayListOf<Command>()
    private var triggers = arrayListOf<Trigger>()

    private var readOnlySubsystems = arrayListOf<Subsystem<*>>()

    fun reset(){
        commands = arrayListOf(UpdateGlobalsCommand())
        triggers = arrayListOf<Trigger>()
    }

    fun init(hardwareMap: HardwareMap){
        this.hardwareMap = hardwareMap
        reset()
    }

    fun addTrigger(trigger: Trigger) = triggers.add(trigger)

    fun schedule(command: Command) {
        command.initialize()

        command.requirements.forEach { subsystem ->
            commands.filter { it.requirements.contains(subsystem) }
                .forEach{
                    it.end(interrupted = true)
                    commands.remove(it)
                }
        }
        command.readOnly.forEach { subsystem ->
            if(subsystem !in readOnlySubsystems) {
                readOnlySubsystems.add(subsystem)
            }
        }

        commands.add(command)
    }

    private fun updateCommands(deltaTime: Double) {
        readOnlySubsystems.forEach { it.update(deltaTime) }

        var i = 0
        while(i < commands.size){
            val command = commands[i]

            command.requirements.forEach { it.update(deltaTime) }
            command.execute()
            
            if(command.isFinished()){
                command.end(interrupted = false)
                commands.remove(command)
            }
            else {
                i ++ 
            }
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
        output += "]\n time: ${Globals.timeSinceStart}"

        return output
    }

}
