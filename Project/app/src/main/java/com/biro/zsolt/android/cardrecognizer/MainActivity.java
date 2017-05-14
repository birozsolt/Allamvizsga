package com.biro.zsolt.android.cardrecognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    // Used to load the native libs on application startup.
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("nonfree");
        System.loadLibrary("sift-test");
    }

    Mat mRgba, mRgbaF, mRgbaT, mGray, mRefImg;
    private Bitmap bitmapOrig = null;
    private CameraBridgeViewBase cameraBridgeViewBase;
    private ImageView recognizedCard;
    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    cameraBridgeViewBase.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    /**
     * Avoid that the screen get's turned off by the system.
     */
    public void disableScreenTurnOff() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Maximize the application.
     */
    public void setFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Remove the title bar.
     */
    public void setNoTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        setNoTitle();
        disableScreenTurnOff();
        setContentView(R.layout.show_camera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapOrig = BitmapFactory.decodeResource(this.getResources(), R.drawable.ace_of_clubs,options);
        mRefImg = new Mat(bitmapOrig.getHeight(), bitmapOrig.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmapOrig, mRefImg);

        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.cameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        recognizedCard = (ImageView) findViewById(R.id.imageView);
        recognizedCard.setBackgroundResource(R.drawable.ace_of_clubs);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, baseLoaderCallback);
        } else {
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
        Core.flip(mRgbaF, mRgba, 1);
        mGray = inputFrame.gray();

        findFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(), mRefImg.getNativeObjAddr());
        return mRgba;
    }

    /**
     * A native method that is implemented by the 'sift-test' native library,
     * which is packaged with this application.
     */
    public native void findFeatures(long matAddrGray, long matAddrRgba, long matAddrRefImg);
}

