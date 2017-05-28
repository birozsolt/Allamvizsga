package com.biro.zsolt.android.cardrecognizer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Zsolt on 2017. 05. 22..
 */

//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//public class CameraPreview2 extends CameraCaptureSession.CaptureCallback implements Callback {
//
//    private Camera mCamera = null;
//    private ImageView MyCameraPreview = null;
//    private Bitmap bitmap = null;
//    private int[] pixels = null;
//    private byte[] FrameData = null;
//    private int imageFormat;
//    private int PreviewSizeWidth;
//    private int PreviewSizeHeight;
//    private boolean bProcessing = false;
//
//    private Handler mHandler = new Handler(Looper.getMainLooper());
//
//    public CameraPreview2(int PreviewlayoutWidth, int PreviewlayoutHeight,
//                         ImageView CameraPreview)
//    {
//        PreviewSizeWidth = PreviewlayoutWidth;
//        PreviewSizeHeight = PreviewlayoutHeight;
//        MyCameraPreview = CameraPreview;
//        bitmap = Bitmap.createBitmap(PreviewSizeWidth, PreviewSizeHeight, Bitmap.Config.ARGB_8888);
//        pixels = new int[PreviewSizeWidth * PreviewSizeHeight];
//    }
//
//    @Override
//    public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
//        // At preview mode, the frame data will push to here.
//        if (imageFormat == ImageFormat.NV21)
//        {
//            //We only accept the NV21(YUV420) format.
//            if ( !bProcessing )
//            {
//                FrameData = data;
//                mHandler.post(DoImageProcessing);
//            }
//        }
//    }
//
//    public void onPause(){
//        mCamera.stopPreview();
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        mCamera = Camera.open();
//        try {
//            // If did not set the SurfaceHolder, the preview area will be black.
//            mCamera.setPreviewDisplay(holder);
//            mCamera.setPreviewCallback(this);
//        } catch (IOException e) {
//            mCamera.release();
//            mCamera = null;
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        android.hardware.Camera.Parameters parameters;
//        parameters = mCamera.getParameters();
//        // Set the camera preview size
//        parameters.setPreviewSize(PreviewSizeWidth, PreviewSizeHeight);
//        imageFormat = parameters.getPreviewFormat();
//        mCamera.setParameters(parameters);
//        mCamera.startPreview();
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        mCamera.setPreviewCallback(null);
//        mCamera.stopPreview();
//        mCamera.release();
//        mCamera = null;
//    }
//}
//
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CameraPreview implements Callback, android.hardware.Camera.PreviewCallback {

    private Camera mCamera = null;
    private ImageView MyCameraPreview = null;
    private Bitmap bitmap = null;
    private int[] pixels = null;
    private byte[] FrameData = null;
    private int imageFormat;
    private int PreviewSizeWidth;
    private int PreviewSizeHeight;
    private boolean bProcessing = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public CameraPreview(int PreviewLayoutWidth, int PreviewLayoutHeight, ImageView CameraPreview) {
        PreviewSizeWidth = PreviewLayoutWidth;
        PreviewSizeHeight = PreviewLayoutHeight;
        MyCameraPreview = CameraPreview;
        bitmap = Bitmap.createBitmap(PreviewSizeWidth, PreviewSizeHeight, Bitmap.Config.ARGB_8888);
        pixels = new int[PreviewSizeWidth * PreviewSizeHeight];
    }

    @Override
    public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
        // At preview mode, the frame data will push to here.
        //if (imageFormat == ImageFormat.NV21)
        // {
        //We only accept the NV21(YUV420) format.
        if (!bProcessing) {
            FrameData = data;
            // mHandler.post(DoImageProcessing);
        }
        //}
    }

    public void onPause() {
        mCamera.stopPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = android.hardware.Camera.open();
        try {
            // If did not set the SurfaceHolder, the preview area will be black.
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters;
        parameters = mCamera.getParameters();
        // Set the camera preview size
        parameters.setPreviewSize(PreviewSizeWidth, PreviewSizeHeight);
        imageFormat = parameters.getPreviewFormat();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}