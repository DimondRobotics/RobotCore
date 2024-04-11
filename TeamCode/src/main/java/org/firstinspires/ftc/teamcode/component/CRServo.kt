package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class CRServo(val name: String, val hardwareMap: HardwareMap, val min: Double = 0.0, val max: Double = 0.0) {
    var lastWrite = 0.0
    var servo: com.qualcomm.robotcore.hardware.CRServo

    init {
        servo = hardwareMap.get(CRServo::class.java, name)
    }

    fun setAngle(angle: Double) {
        if (angle >= min && angle <= max) {
            val pos = (angle - min) / max //lerp from min to max
            position = pos
        }
    }

    var position: Double
        get() = lastWrite
        set(pos) {
            if (Math.abs(pos - lastWrite) <= epsilon) {
                return
            }
            servo.power = position
            lastWrite = pos
        }
    var direction: Int
        get() = if( servo.direction.equals(DcMotorSimple.Direction.FORWARD) ) FORWARD else REVERSE
        set(newDirection: Int):Unit {
            servo.direction =
                if (newDirection == FORWARD){ DcMotorSimple.Direction.FORWARD }
                else { DcMotorSimple.Direction.REVERSE }
        }

    companion object {
        const val epsilon = 0.005
        const val FORWARD = 1
        const val REVERSE = -1
    }
}
