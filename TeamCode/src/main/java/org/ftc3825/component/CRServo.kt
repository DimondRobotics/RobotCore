package org.ftc3825.component

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.component.CRServo.Direction.FORWARD
import org.ftc3825.component.CRServo.Direction.REVERSE
import org.ftc3825.util.isWithin
import org.ftc3825.util.of

class CRServo(val name: String, val hardwareMap: HardwareMap) {
    var lastWrite = 0.0
    private val servo = hardwareMap.get(CRServo::class.java, name)

    var power: Double
        get() = lastWrite
        set(pos) {
            if ( pos isWithin org.ftc3825.component.CRServo.Companion.EPSILON of lastWrite) {
                return
            }
            servo.power = pos
            lastWrite = pos
        }
    var direction: org.ftc3825.component.CRServo.Direction
        get() = if( servo.direction.equals(DcMotorSimple.Direction.FORWARD) ) FORWARD else REVERSE
        set(newDirection) {
            servo.direction =
                if (newDirection == FORWARD){ DcMotorSimple.Direction.FORWARD }
                else { DcMotorSimple.Direction.REVERSE }
        }

    enum class Direction {
        FORWARD, REVERSE
    }

    companion object {
        const val EPSILON = 0.005
    }
}