package org.firstinspires.ftc.teamcode.PSRobotLibs.lib.vision.UVC;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.support.annotation.NonNull;

import com.qualcomm.robotcore.util.ThreadPool;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.android.util.Size;
import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera.StateCallbackDefault;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureRequest;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSequenceId;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSession;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraException;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraFrame;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraManager;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.internal.camera.WebcamExample;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.firstinspires.ftc.robotcore.internal.system.Misc;
import org.firstinspires.ftc.robotcore.internal.ui.UILocation;
import org.firstinspires.ftc.teamcode.PSRobotLibs.lib.utils.vision.PSVisionUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class UVCCamera {
    final CameraManager cameraManager;
    final CameraName cameraName;
    final Executor threadPool = ThreadPool.newSingleThreadExecutor("UVCCamera");
    CameraCharacteristics characteristics;
    int imageFormatWanted = ImageFormat.YUY2;
    Camera camera;
    Size sizeWanted;
//    private int cameraMonitorViewId;
//    private ViewGroup cameraMonitorView;
//
//    AppUtil appUtil = AppUtil.getInstance();
//    Activity activity;

    public UVCCamera(CameraManager cameraManager, CameraName cameraName){
        this.cameraManager = cameraManager;
        this.cameraName = cameraName;
//        this.activity = appUtil.getActivity();
//        Context context = AppUtil.getDefContext();
//        cameraMonitorViewId = context.getResources().getIdentifier("cameraMonitorViewId", "id", context.getPackageName());
    }
    public void test(Telemetry telemetry){
//        if (cameraMonitorViewId == 0) {
//            cameraMonitorView = (ViewGroup) activity.findViewById(android.R.id.content);
//        } else {
//            cameraMonitorView = (LinearLayout) activity.findViewById(cameraMonitorViewId);
//        }
        //cameraMonitorView.addView(cameraView);
        Deadline deadline = new Deadline(10,TimeUnit.SECONDS);

//        Boolean value = cameraName.requestCameraPermission(deadline);

        telemetry.addLine("1");
        telemetry.update();
        cameraName.asyncRequestCameraPermission(AppUtil.getDefContext(), deadline, Continuation.create(threadPool, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) {
                telemetry.addLine("2");
                telemetry.update();
                if (value){
                    telemetry.addLine("3");
                    telemetry.update();
                    //characteristics = cameraName.getCameraCharacteristics();
                    telemetry.addLine("4");
                    telemetry.update();
                    cameraManager.asyncOpenCameraAssumingPermission(cameraName, Continuation.create(threadPool, new StateCallbackDefault(){
                        @Override public void onOpened(@NonNull final Camera camera)
                        {
                            telemetry.addLine("5");
                            telemetry.update();
                           UVCCamera.this.camera = camera;

                            if (Misc.contains(characteristics.getAndroidFormats(), imageFormatWanted))
                            {
                                telemetry.addLine("6");
                                telemetry.update();
                                sizeWanted = characteristics.getDefaultSize(imageFormatWanted);
                                try {
                                    telemetry.addLine("7");
                                    telemetry.update();
                                    camera.createCaptureSession(Continuation.create(threadPool, captureStateCallback));
                                }
                                catch (CameraException e)
                                {
                                    telemetry.addLine("8");
                                    telemetry.update();
                                }
                            }
                            else
                            {
//                                RobotLog.ee(TAG, "camera doesn't support desired format: 0x%02x", imageFormatWanted);
                            }
                        }

                        @Override public void onClosed(@NonNull Camera camera)
                        {
//                            RobotLog.vv(TAG, "camera reports closed: %s", camera);
                        }
                    }),10,TimeUnit.SECONDS);
                    telemetry.addLine("9");
                    telemetry.update();
                }
                telemetry.addLine("10");
                telemetry.update();
            }
        }));
        telemetry.addLine("20");
        telemetry.update();
    }
    CameraCaptureSession.StateCallback captureStateCallback = new CameraCaptureSession.StateCallbackDefault()
    {
        @Override public void onConfigured(@NonNull CameraCaptureSession session)
        {
            try {
                /** Indicate <em>how</em> we want to stream. */
                final CameraCaptureRequest cameraCaptureRequest = camera.createCaptureRequest(imageFormatWanted, sizeWanted, characteristics.getMaxFramesPerSecond(imageFormatWanted, sizeWanted));

                /** Start streaming! Flow continues in the captureCallback. */
                CameraCaptureSequenceId cameraCaptureSequenceId = session.startCapture(cameraCaptureRequest,
                        new UVCCamera.WebcamCaptureCallback(cameraCaptureRequest), // callback, not continuation; avoids copying frame
                        Continuation.create(threadPool, new CameraCaptureSession.StatusCallback()
                        {
                            @Override public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, CameraCaptureSequenceId cameraCaptureSequenceId, long lastFrameNumber)
                            {
//                                RobotLog.vv(TAG, "capture sequence %s reports completed: lastFrame=%d", cameraCaptureSequenceId, lastFrameNumber);
                            }
                        }));

                // Put up a dialog, streaming until the user dismisses it or we're not supposed to continue
                AppUtil.DialogParams params = new AppUtil.DialogParams(UILocation.ONLY_LOCAL, "Streaming Active", "Press OK to stop");
                try {
                    final CountDownLatch latch = new CountDownLatch(1);
                    AppUtil.DialogContext dialogContext = AppUtil.getInstance().showDialog(params, ThreadPool.getDefault(), new Consumer<AppUtil.DialogContext>()
                    {
                        @Override public void accept(AppUtil.DialogContext dialogContext)
                        {
                            latch.countDown();
                        }
                    });
                    try {
                        for (;;)
                        {
                            if (latch.await(100, TimeUnit.MILLISECONDS))
                            {
//                                RobotLog.vv(TAG, "capture termination requested");
                                break;
                            }
                        }
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                    finally
                    {
                        // Ensure the dialog is gone, even in the case where the user didn't dismiss it
                        dialogContext.dismissDialog();
                    }
                }
                finally
                {
                    // Shutdown the camera
                    session.stopCapture();
                    session.close();
                    camera.close();
                }
            }
            catch (CameraException e)
            {
//                RobotLog.ee(TAG, e, "error setting repeat capture request");
            }
        }

        @Override public void onClosed(@NonNull CameraCaptureSession session)
        {
//            RobotLog.vv(TAG, "capture session reports closed: %s", session);
        }
    };

    public class WebcamCaptureCallback implements CameraCaptureSession.CaptureCallback {
        Bitmap bitmap;
        public WebcamCaptureCallback(CameraCaptureRequest request) {
            bitmap = request.createEmptyBitmap();

        }

        @Override
        public void onNewFrame(@NonNull CameraCaptureSession session, @NonNull CameraCaptureRequest request, @NonNull CameraFrame cameraFrame) {
            cameraFrame.copyToBitmap(bitmap);
            PSVisionUtils.saveImageToFile(bitmap,"frame", "/saved_images");
        }
    }
}
