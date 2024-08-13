package org.ftc3825.fakehardware

import com.qualcomm.robotcore.hardware.AccelerationSensor
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.CompassSensor
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorController
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.GyroSensor
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.I2cDevice
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.hardware.IrSeekerSensor
import com.qualcomm.robotcore.hardware.LED
import com.qualcomm.robotcore.hardware.LightSensor
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor
import com.qualcomm.robotcore.hardware.PWMOutput
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoController
import com.qualcomm.robotcore.hardware.TouchSensor
import com.qualcomm.robotcore.hardware.TouchSensorMultiplexer
import com.qualcomm.robotcore.hardware.UltrasonicSensor
import org.ftc3825.util.Globals

abstract class JVMHardwareMap: HardwareMap(null, null) {
    var lastTime = Globals.timeSinceStart
    abstract var deviceTypes: MutableMap<Class<out Any>, (String) -> HardwareDevice>
    // hardware.Gamepad does not implement hardwareDevice for some reason


    override fun<T : Any?> get(classOrInterface: Class<out T>?, deviceName: String?): T{
        val hwClass = classOrInterface!!

        allDeviceMappings.forEach {mapping ->
            if( mapping.contains(deviceName) ){
                return mapping[deviceName] as T
            }
        }


        val deviceFun: ((String) -> HardwareDevice)? = deviceTypes[hwClass]
        if (deviceFun == null) {
            throw NotImplementedError("$classOrInterface is not implemented for ${this::class.java}")
        }

        var device = deviceFun(deviceName!!)

        when(device){
            is DcMotorController      -> dcMotorController      .put(deviceName, device)
            is DcMotor                -> dcMotor                .put(deviceName, device)
            is ServoController        -> servoController        .put(deviceName, device)
            is Servo                  -> servo                  .put(deviceName, device)
            is CRServo                -> crservo                .put(deviceName, device)
            is TouchSensorMultiplexer -> touchSensorMultiplexer .put(deviceName, device)
            is AnalogInput            -> analogInput            .put(deviceName, device)
            is DigitalChannel         -> digitalChannel         .put(deviceName, device)
            is OpticalDistanceSensor  -> opticalDistanceSensor  .put(deviceName, device)
            is TouchSensor            -> touchSensor            .put(deviceName, device)
            is PWMOutput              -> pwmOutput              .put(deviceName, device)
            is I2cDevice              -> i2cDevice              .put(deviceName, device)
            is I2cDeviceSynch         -> i2cDeviceSynch         .put(deviceName, device)
            is ColorSensor            -> colorSensor            .put(deviceName, device)
            is LED                    -> led                    .put(deviceName, device)
            is AccelerationSensor     -> accelerationSensor     .put(deviceName, device)
            is CompassSensor          -> compassSensor          .put(deviceName, device)
            is GyroSensor             -> gyroSensor             .put(deviceName, device)
            is IrSeekerSensor         -> irSeekerSensor         .put(deviceName, device)
            is LightSensor            -> lightSensor            .put(deviceName, device)
            is UltrasonicSensor       -> ultrasonicSensor       .put(deviceName, device)
        }
        return device as T
    }

    fun updateDevices() {
        val deltaTime = Globals.timeSinceStart - lastTime

        size()
        allDevicesList.forEach { device ->
                (device!! as FakeHardware).update(deltaTime)
        }

        lastTime = Globals.timeSinceStart

    }
}