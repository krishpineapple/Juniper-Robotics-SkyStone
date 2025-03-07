package org.firstinspires.ftc.teamcode.RobotLibs.lib.auto;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.RobotLibs.lib.PSResources;

/**
 * Created by Brandon on 10/17/2017.
 */

public class SwitchBoard {

    private PSResources resources;

    public SwitchBoard(PSResources r) {
        resources = r;
    }


    /**
     * Simple method that reads the value of a digital switch and outputs it
     * @param name The configuration name of the sensor
     * @return The state of the digital switch
     */
    public boolean loadDigital(String name){
        DigitalChannel dig = resources.hardwareMap.digitalChannel.get(name);
        return dig.getState();
    }
    /**
     * Simple method that reads the value of an analog sensor and outputs it
     * @param name The configuration name of the sensor
     * @return The state of the value
     */
    public double loadAnalog(String name){
        AnalogInput analogInput = resources.hardwareMap.analogInput.get(name);
        return analogInput.getVoltage();
    }
}
