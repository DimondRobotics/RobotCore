package org.firstinspires.ftc.teamcode.test.FakeHardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.ftc3825.command.internal.CommandScheduler
import org.ftc3825.fakehardware.FakeMotor
import org.ftc3825.util.TestClass
import org.ftc3825.util.assertGreater
import org.junit.Test

class MotorTest: TestClass() {
    @Test
    fun testMotorSpeed() {

        val motor = hardwareMap.get(DcMotor::class.java, "motor")
        motor.resetDeviceConfigurationForOpMode()

        motor.power = 1.0

        for(i in 0..200){
            CommandScheduler.update()

            // println((motor as FakeMotor).speed)
        }
        val fakeMotor = motor as FakeMotor
        assertGreater(fakeMotor.speed, 0.6)
        assertGreater(1.0, fakeMotor.speed)
    }
}