package org.firstinspires.ftc.teamcode.RobotLibs.examples.driveExample;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.RobotLibs.lib.PSEnum;
import org.firstinspires.ftc.teamcode.RobotLibs.lib.PSRobot;
import org.firstinspires.ftc.teamcode.RobotLibs.lib.hardware.MotorEx;
import org.firstinspires.ftc.teamcode.RobotLibs.lib.hardware.sensors.PSTouchSensor;

/**
 * Created by young on 8/7/2017.
 */
@TeleOp(name = "PineEx-DriveEncoder", group = "Linear Opmode")
@Disabled

public class DriveEncoderExample extends LinearOpMode {
    PSRobot robot;

    MotorEx left;
    MotorEx right;
    PSTouchSensor touch;

    @Override
    public void runOpMode() throws InterruptedException {

        robot = new PSRobot(this);

        left = robot.motorHandler.newDriveMotor("left", 1, true , true, PSEnum.MotorLoc.LEFT, 40);
        right = robot.motorHandler.newDriveMotor("right", 1, true , true, PSEnum.MotorLoc.RIGHT, 40);


        right.motorObject.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

//        robot.drive.encoderDrive(0.5, "4in",4);
//        robot.drive.setDirectPower(-1, -1);
//        Thread.sleep(1000);
    }
}
