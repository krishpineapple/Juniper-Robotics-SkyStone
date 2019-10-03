package org.firstinspires.ftc.teamcode.SkyStone.V2;

import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.drive.ThreeTrackingWheelLocalizer;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.RobotLibsV2.Subsystem.Subsystem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MecanumDrive extends com.acmerobotics.roadrunner.drive.MecanumDrive implements Subsystem {

/*
*    front
* 0         3
*
*
* 1         2
* */


    public OpMode opMode;
    DcMotorEx leftFront;
    DcMotorEx leftBack;
    DcMotorEx rightBack;
    DcMotorEx rightFront;
    List<DcMotorEx> driveMotors;
    //TODO add/implement Gyro
    //TODO add/implement ODO modules
    public Gamepad gamepad1;
    private Gyro gyro;

    public MecanumDrive(OpMode mode){
        super(14);//TODO find actual track width
        opMode = mode;
        leftFront = opMode.hardwareMap.get(DcMotorEx.class, "LF");
        leftBack = opMode.hardwareMap.get(DcMotorEx.class, "LB");
        rightBack = opMode.hardwareMap.get(DcMotorEx.class, "RB");
        rightFront = opMode.hardwareMap.get(DcMotorEx.class, "RF");
        driveMotors = Arrays.asList(leftFront, leftBack, rightBack, rightFront);
        gyro = new Gyro();
        this.gamepad1 = opMode.gamepad1;
        for (DcMotorEx motor : driveMotors){
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//            motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,new PIDFCoefficients(10,0,0,0));
        }
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    @Override
    public void update() {
        updateMecanumThirdPerson(gamepad1,1);
        opMode.telemetry.addData("Gyro",gyro.getHeading());
    }
    //TODO fix this function it is really bad lol
    @Deprecated
    public void setMecanum(){

        leftFront.setPower(Range.clip((gamepad1.left_stick_y - gamepad1.right_stick_x - gamepad1.left_stick_x),-1,1));
        rightFront.setPower(Range.clip((-gamepad1.left_stick_y - gamepad1.right_stick_x - gamepad1.left_stick_x),-1,1));
        leftBack.setPower(Range.clip((gamepad1.left_stick_y - gamepad1.right_stick_x + gamepad1.left_stick_x),-1,1));
        rightBack.setPower(Range.clip((-gamepad1.left_stick_y - gamepad1.right_stick_x + gamepad1.left_stick_x),-1,1));

    }
    public void setMecanum(double radAngle, double speed, double rotation){
        speed = .5*Math.pow(2*(speed-.5),3)+.5;
        rotation = .5*Math.pow(2*(rotation-.5),3)+.5;
        final double v1 = speed * Math.cos(radAngle) + rotation;
        final double v2 = speed * Math.sin(radAngle) - rotation;
        final double v3 = speed * Math.sin(radAngle) + rotation;
        final double v4 = speed * Math.cos(radAngle) - rotation;

        leftFront.setPower(v1);
        rightFront.setPower(v2);
        leftBack.setPower(v3);
        rightBack.setPower(v4);
    }
    public void updateMecanum(Gamepad gamepad, double scaling){
        setMecanum(Math.hypot(gamepad.left_stick_x, gamepad.left_stick_y),scaling*Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4, gamepad.right_stick_x*scaling);
    }
    public void updateMecanumThirdPerson(Gamepad gamepad, double scaling){
        setMecanum(Math.hypot(gamepad.left_stick_x, gamepad.left_stick_y)+Math.toRadians(gyro.getHeading()),scaling*Math.atan2(gamepad1.left_stick_y, gamepad1.left_stick_x) - Math.PI / 4, gamepad.right_stick_x*scaling);
    }
    @Override
    public double getExternalHeading() {
        return 0;
    }

    @NotNull
    @Override
    public List<Double> getWheelPositions() {
        return null;
    }

    @Override
    public void setMotorPowers(double v, double v1, double v2, double v3) {

    }

class Gyro {

    BNO055IMU gyro;
    Orientation angles;
    double cal = 0;

    //init
    public Gyro() {

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        gyro = opMode.hardwareMap.get(BNO055IMU.class, "gyro");
        gyro.initialize(parameters);
    }

    //get heading of gyro
    public double getHeading() {
        angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double angle = angles.firstAngle;
        if (gamepad1.right_stick_button){
            cal = angle;
        }
        return (-angle) + cal;
    }

    public Orientation getOrientation() {
        angles = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return angles;
    }

}
//class TrackingWheels extends Tracking {
//    public final double TICKS_PER_REV = 1;
//    public final double WHEEL_RADIUS = 2; // in
//    public final double GEAR_RATIO = 1; // output/input
//
//    public final double LATERAL_DISTANCE = 10; // in; distance between the left and right wheels
//    public final double FORWARD_OFFSET = 4; // in; offset of the lateral wheel
//
//    private DcMotor leftEncoder, rightEncoder, frontEncoder;
//
//    public TrackingWheels(HardwareMap hardwareMap) {
//        super(Arrays.asList(
//                new Vector2d(0, LATERAL_DISTANCE / 2), // left
//                new Vector2d(0, -LATERAL_DISTANCE / 2), // right
//                new Vector2d(FORWARD_OFFSET, 0) // front
//        ), Arrays.asList(0.0, 0.0, Math.PI / 2));
//
//        leftEncoder = hardwareMap.dcMotor.get("leftEncoder");
//        rightEncoder = hardwareMap.dcMotor.get("rightEncoder");
//        frontEncoder = hardwareMap.dcMotor.get("frontEncoder");
//    }
//
//    public double encoderTicksToInches(int ticks) {
//        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
//    }
//
//    @NotNull
//    @Override
//    public List<Double> getWheelPositions() {
//        return Arrays.asList(
//                encoderTicksToInches(leftEncoder.getCurrentPosition()),
//                encoderTicksToInches(rightEncoder.getCurrentPosition()),
//                encoderTicksToInches(frontEncoder.getCurrentPosition())
//        );
//    }
//}
}
