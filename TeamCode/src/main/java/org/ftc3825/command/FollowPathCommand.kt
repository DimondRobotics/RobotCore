package org.ftc3825.command

import org.ftc3825.GVF.Path
import org.ftc3825.command.internal.Command
import org.ftc3825.subsystem.Drivetrain
import org.ftc3825.subsystem.ThreeDeadWheelLocalizer
import org.ftc3825.util.Pose2D
import org.ftc3825.util.Rotation2D
import org.ftc3825.util.inches

class FollowPathCommand(val localizer: ThreeDeadWheelLocalizer, val path: Path): Command() {
    init {
        addRequirement(Drivetrain)
    }

    override fun execute() {
        localizer.update()
        Drivetrain.setWeightedDrivePower(path.vector(localizer.position) + Rotation2D())
    }

    override fun isFinished(): Boolean {
        return (localizer.position.vector - path[-1].end).mag < 0.5
    }

    override fun end(interrupted: Boolean) =
        Drivetrain.setWeightedDrivePower( Pose2D(0, 0, 0) )
}