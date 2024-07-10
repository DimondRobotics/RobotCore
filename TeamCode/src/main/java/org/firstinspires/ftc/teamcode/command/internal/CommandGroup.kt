package org.firstinspires.ftc.teamcode.command.internal

import org.firstinspires.ftc.teamcode.command.internal.Command

class CommandGroup(vararg var commands: Command): Command() {
    var unpacked: Array<Command> = unpack().toArray() as Array<Command>


    var index = 0
    val current: Command
        get() = unpacked[index]

    override fun initialize() {
        unpacked[0].initialize()
    }

    override fun execute() {
        current.execute()
        if(current.isFinished()){
            current.end(false)
            index ++
        }
    }

    override fun isFinished() = index >= commands.size

    override fun end(interrupted: Boolean){
        if(interrupted) current.end(true)
    }

    fun unpack(): ArrayList<Command>{
        val output = arrayListOf<Command>()
        for( command in commands ){
            if(command is CommandGroup) {
                output.addAll(command.unpack())
            }
            else{
                output.add(command)
            }
        }
        return output
    }
}