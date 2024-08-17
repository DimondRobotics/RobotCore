package org.ftc3825.command.internal

import java.util.function.BooleanSupplier

class Trigger(
    var supplier: BooleanSupplier,
    var conditionsMet: (Boolean, Boolean) -> Boolean = {value, lastValue -> value},
    var command: Command = Command()
) {
    constructor(supplier: BooleanSupplier) : this(supplier, {value, lastValue -> value}, Command())

    private var lastValue = supplier.asBoolean
    private var value = supplier.asBoolean

    fun update() {
        lastValue = value
        value = supplier.asBoolean
    }
    val isTriggered: Boolean
        get() = conditionsMet(value, lastValue)

    infix fun and(other: Trigger) = Trigger {  supplier.asBoolean and other.supplier.asBoolean }
    infix fun or(other: Trigger)  = Trigger {  supplier.asBoolean or  other.supplier.asBoolean }
    operator fun not()            = Trigger { !supplier.asBoolean }


    fun onTrue(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                supplier,
                { value, lastValue -> value and !lastValue},
                command
            )
        )
        return this
    }

    fun onFalse(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                supplier,
                { value, lastValue -> !value and lastValue},
                command
            )
        )
        return this
    }

    fun whileTrue(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                supplier,
                { value, lastValue -> value and !lastValue},
                command racesWith WaitUntilCommand { this.supplier.asBoolean == false }

            )
        )
        return this
    }

    fun whileFalse(command: Command): Trigger {
        CommandScheduler.addTrigger(
            Trigger(
                supplier,
                { value, lastValue -> !value and lastValue},
                command racesWith WaitUntilCommand { this.supplier.asBoolean == true }

            )
        )
        return this
    }


}