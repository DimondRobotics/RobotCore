package org.firstinspires.ftc.teamcode.component

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.util.radians
import kotlin.math.abs

/**
 * @param min the minimum angle of the servo, corresponding to position = 0.0
 * @param max the maximum angle of the servo, corresponding to position = 1.0
 */
class Servo(name: String, hardwareMap: HardwareMap, val min: Double = 0.0, val max: Double = radians(5.236) /* 300 degrees in radians */) {
    private var lastWrite = 0.0
    var servo: com.qualcomm.robotcore.hardware.Servo = hardwareMap.get(Servo::class.java, name)

    fun setAngle(angle: Double) {
        if (angle in min..max) {
            val pos = (angle - min) / ( max - min )//lerp from min to max
            position = pos
        }
    }

    var position: Double
        get() = lastWrite
        set(pos) {
            if (abs(pos - lastWrite) <= EPSILON) {
                return
            }
            servo.position = pos
            lastWrite = pos
        }

    companion object {
        const val EPSILON = 0.001 // goBilda torque servo deadband
    }
}
