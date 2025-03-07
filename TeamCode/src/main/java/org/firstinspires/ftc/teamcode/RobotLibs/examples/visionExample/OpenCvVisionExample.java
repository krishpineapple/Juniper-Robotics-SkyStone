package org.firstinspires.ftc.teamcode.RobotLibs.examples.visionExample;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.RobotLibs.lib.PSEnum;
import org.firstinspires.ftc.teamcode.RobotLibs.lib.vision.PSFPSTracker;
import org.firstinspires.ftc.teamcode.RobotLibs.lib.vision.PSOpenCVCamera;

public class OpenCvVisionExample extends OpMode {
    private PSOpenCVCamera camera;
    private PSFPSTracker tracker;
    private BlockTrackerExample blockTrackerExample;

    @Override
    public void init() {
        camera = new PSOpenCVCamera(PSEnum.CameraDirection.BACK);
        tracker = new PSFPSTracker();
        camera.addTracker(tracker);
        camera.addTracker(blockTrackerExample);
        camera.initialize();
        tracker.disable();
    }

    @Override
    public void loop() {
        telemetry.addData("FPS:", tracker.getFPS());
        telemetry.update();
    }
}
