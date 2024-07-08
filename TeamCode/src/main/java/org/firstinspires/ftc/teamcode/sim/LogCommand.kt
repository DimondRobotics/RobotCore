package org.firstinspires.ftc.teamcode.sim

import org.firstinspires.ftc.teamcode.command.Command
import org.firstinspires.ftc.teamcode.subsystem.Drivetrain
import org.firstinspires.ftc.teamcode.subsystem.Robot
import org.firstinspires.ftc.teamcode.util.JsonList
import org.firstinspires.ftc.teamcode.util.JsonObject
import org.firstinspires.ftc.teamcode.util.jsonObject
import org.firstinspires.ftc.teamcode.util.nanoseconds
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Date


class LogCommand(): Command() {
    val startDate = Date().toString()
    val startTime = System.nanoTime()
    val log = JsonList<JsonObject>(arrayListOf())
    init {
        addReqirement(Drivetrain, write=false)
        addReqirement(Robot, write=false)
    }

    override fun execute() {
        log.add(
            jsonObject {
                "seconds" `is` nanoseconds(System.nanoTime() - startTime).toString()
                "voltage" `is` Robot.voltage
                "motors" `is` JsonList<JsonObject>(Drivetrain.Motors.map {
                    jsonObject {
                        "name" `is` it.name
                        "voltage" `is` it.lastWrite * Robot.voltage
                        "position" `is` it.positsion
                    }
                })
            }
        )
    }

    override fun end(interrupted: Boolean) {

        var text: String = jsonObject {
            "start time" `is` startDate
            "version" `is` "0.0.1"
            "data" `is` log

        }.toString()

        var path = Paths.get("logs/$startDate.json")

        Files.write(path, text.toByteArray(), StandardOpenOption.CREATE)
    }
}