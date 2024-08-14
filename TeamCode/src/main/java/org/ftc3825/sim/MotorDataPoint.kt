package org.ftc3825.sim


data class MotorDataPoint(
    val voltage: Double,
    val velocity: Double,
    val acceleration: Double
) : FunctionOutput(
    inputs = doubleArrayOf(
        voltage,
        velocity
    ),
    output = acceleration
){
    override fun toString(): String {
        return "$voltage, $velocity, $acceleration"
    }
}