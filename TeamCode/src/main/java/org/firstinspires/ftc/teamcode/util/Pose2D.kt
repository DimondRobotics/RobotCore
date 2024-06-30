package org.firstinspires.ftc.teamcode.util

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Pose2D(var x: Double = 0.0, var y: Double = 0.0, var heading: Double = 0.0) {
    val magSq: Double
        get() = x * x + y * y
    var mag: Double
        get() = sqrt(magSq)
        set(newMag: Double):Unit {
            val scale = (mag / newMag)
            this.x *= scale
            this.y *= scale
        }

    var vector: Vector2D
        get() = Vector2D(x, y)
        set(newVector: Vector2D) {
            x = newVector._x
            y = newVector._y
        }

    operator fun unaryPlus() = Pose2D(x, y, heading)
    operator fun plus(other: Pose2D) = Pose2D(
        x + other.x,
        y + other.y,
        heading + other.heading
    )
    operator fun plus(other: Rotation2D) = Pose2D(x, y, heading + other.theta)
    operator fun minus(other: Pose2D) = Pose2D(
        x - other.x,
        y - other.y,
        heading + other.heading
    )
    operator fun minus(other: Rotation2D) = Pose2D(x, y, heading - other.theta)
    operator fun times(scalar: Double) = Pose2D(x * scalar, y * scalar)
    operator fun div(scalar :Double) = Pose2D(x / scalar, y / scalar)
    override fun equals(other: Any?) = (other is Pose2D) && (x == other.x) && (y == other.y)

    fun unit(): Pose2D = Pose2D(x / mag, y / mag)
    fun reflect(direction: Int):Pose2D {

        return when(direction) {
            Pose2D.Xaxis -> Pose2D(-x, y, -heading)
            Pose2D.Yaxis -> Pose2D(x, -y, 180 - heading)
            else -> throw IllegalStateException("direction in Pose2D.reflect must be 0 or 1 (from the companion class)")
        }
    }
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + heading.hashCode()
        return result
    }

    fun applyToEnd(other: Pose2D) {
        other.rotate(this.heading)
        this.x += other.x
        this.y += other.y
        this.heading += other.heading
    }

    fun rotate(theta: Double) {
        this.x = x * cos(theta) - y * sin(theta)
        this.y = x * sin(theta) + y * cos(theta)
        this.heading += theta
    }

    override fun toString() = "x: $x, y: $y, heading: $heading"

    companion object{
        const val Xaxis = 0
        const val Yaxis = 1
    }
}