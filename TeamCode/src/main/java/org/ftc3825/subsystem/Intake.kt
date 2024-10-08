package org.ftc3825.subsystem

import com.qualcomm.robotcore.hardware.HardwareMap
import org.ftc3825.command.internal.InstantCommand
import org.ftc3825.component.Motor
import org.ftc3825.component.Servo
import org.ftc3825.component.CRServo
import org.ftc3825.stateMachine.State
import org.ftc3825.stateMachine.StateMachine
import org.ftc3825.subsystem.Subsystem
import org.ftc3825.util.IntakeIntakeServoName
import org.ftc3825.util.IntakePivotServoName
import org.ftc3825.util.degrees

object Intake : Subsystem<Intake> {
    override var initialized = false
    override val motors = arrayListOf<Motor>()

    lateinit var pivotServo: Servo
    lateinit var intakeServo: CRServo

    val minAngle = degrees(0)
    val maxAngle = degrees(90.0)

    override fun init(hardwareMap: HardwareMap) {
        if(!initialized) {
            pivotServo = Servo(IntakePivotServoName, hardwareMap)
            intakeServo = CRServo(IntakeIntakeServoName, hardwareMap)
        }
        initialized = true
    }
    override fun update(deltaTime: Double) { }

    fun setAngle(angle: Double) {
        angle.coerceIn(minAngle, maxAngle)
        pivotServo.setAngle(minAngle + angle)

    }
    fun retract() = InstantCommand { setAngle(degrees(90.0)) }
    fun open()    = InstantCommand { setAngle(0.0)           }

    fun intake() = InstantCommand  { intakeServo.power =   1.0; Unit}
    fun outtake() = InstantCommand { intakeServo.power = - 1.0; Unit}
}