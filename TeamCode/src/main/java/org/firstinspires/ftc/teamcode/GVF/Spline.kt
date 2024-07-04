package org.firstinspires.ftc.teamcode.GVF

import org.firstinspires.ftc.teamcode.util.Vector2D
import java.lang.reflect.Array
import kotlin.math.ceil

const val RESOLUTION = 0.001

class Spline(
    val p1: Vector2D,
    val cp1: Vector2D,
    val cp2: Vector2D,
    val p2: Vector2D
): PathSegment(p1, cp1, cp2, p2) {
    constructor(x1:Number, y1:Number, cx1:Number, cy1:Number, x2:Number, y2:Number, cx2:Number, cy2:Number)
            :this(
        Vector2D(x1, y1),
        Vector2D(cx1, cy1),
        Vector2D(cx2, cy2),
        Vector2D(x2, y2)
    )
    val coef = Array(4) {
        when (it) {
            0 ->  p1
            1 -> -p1*3.0   + cp1*3.0
            2 ->  p1*3.0   - cp1*6.0 + cp2*3.0
            3 -> -p1       + cp1*3.0 - cp2*3.0 + p2
            else -> Vector2D()
        }
    }

    val pointsLUT = Array(ceil(1 / RESOLUTION).toInt() + 1) { t: Int -> invoke(t * RESOLUTION) }
    override fun closestT(point: Vector2D) = pointsLUT.indexOf(pointsLUT.minBy { (it - point).magSq }) * RESOLUTION

    override fun invoke(t: Double): Vector2D {
        val tsq = t * t
        //return p1 + (cp1 + term3 * t + term4 * tsq) * t // TODO: optimise this line?
        return coef[0] + coef[1] * t + coef[2] * t*t + coef[3] * t*t*t
    }

    override fun derivative(t: Double): Vector2D {
        return ( coef[1] + coef[2] * t + coef[3] * (t * t) ).unit
    }
}