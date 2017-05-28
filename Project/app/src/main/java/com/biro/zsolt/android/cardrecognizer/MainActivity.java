package com.biro.zsolt.android.cardrecognizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.biro.zsolt.android.cardrecognizer.Config.descriptor;
import static com.biro.zsolt.android.cardrecognizer.Config.detector;
import static com.biro.zsolt.android.cardrecognizer.Config.matcher;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    // Used to load the native libs on application startup.
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("nonfree");
        System.loadLibrary("sift-test");
    }

    Mat mRgba, mAceOfClubs, mAceOfDiamonds, mBackOfCard;
    Mat descriptorScene, descriptorAceOfClubs, descriptorAceOfDiamonds;
    MatOfKeyPoint keypointScene, keypointAceOfClubs, keypointAceOfDiamonds;
    Scalar RED = new Scalar(255, 0, 0);
    Scalar GREEN = new Scalar(0, 255, 0);

    private int counter = 0;
    private JavaCameraView javaCameraView;
    private ImageView recognizedCard, recognizedCard2;
    private Cards cards;
    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    javaCameraView.enableView();
                    initializeOpenCvDependencies();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    public void initializeOpenCvDependencies() {
        cards = new Cards(this);
        mAceOfClubs = cards.getCard(CardNames.AceOfClubs);
        mAceOfDiamonds = cards.getCard(CardNames.AceOfDiamonds);
        mBackOfCard = cards.getCard(CardNames.BackOfCards);
        descriptorAceOfClubs = new Mat();
        descriptorAceOfDiamonds = new Mat();
        keypointAceOfClubs = new MatOfKeyPoint();
        keypointAceOfDiamonds = new MatOfKeyPoint();
        detector.detect(mAceOfClubs, keypointAceOfClubs);
        descriptor.compute(mAceOfClubs, keypointAceOfClubs, descriptorAceOfClubs);
        detector.detect(mAceOfDiamonds, keypointAceOfDiamonds);
        descriptor.compute(mAceOfDiamonds, keypointAceOfDiamonds, descriptorAceOfDiamonds);
    }
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Remove the title bar.
     */
    public void setNoTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /*
        public void bitmapToMat() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmapOrig = BitmapFactory.decodeResource(this.getResources(), R.drawable.ace_of_clubs, options);
            mRefImg = new Mat();
            Utils.bitmapToMat(bitmapOrig, mRefImg);
            recognizedCard.setImageBitmap(bitmapOrig);
        }
        */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        setNoTitle();
        disableScreenTurnOff();
        setContentView(R.layout.show_camera);
        javaCameraView = (JavaCameraView) findViewById(R.id.cameraView);
        if (javaCameraView != null) {
            javaCameraView.setVisibility(SurfaceView.VISIBLE);
            javaCameraView.setCvCameraViewListener(this);
            javaCameraView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        recognizedCard = (ImageView) findViewById(R.id.imageView);
        recognizedCard2 = (ImageView) findViewById(R.id.imageView2);
        /*if (recognizedCard != null) {
            recognizedCard.setImageBitmap(bitmapOrig);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
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
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mRgba.release();
        mAceOfClubs.release();
        mAceOfDiamonds.release();
        mBackOfCard.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Size sizeTemp = mRgba.size();
        Core.transpose(mRgba, mRgba);
        Core.flip(mRgba, mRgba, 1);
        Imgproc.resize(mRgba, mRgba, sizeTemp);
        //findFeatures(mRgba.getNativeObjAddr(), mRefImg.getNativeObjAddr());
        //return mRgba;
        return recognize(mRgba);
    }

    public Mat recognize(Mat aInputFrame) {

        descriptorScene = new Mat();
        keypointScene = new MatOfKeyPoint();
        detector.detect(aInputFrame, keypointScene);
        descriptor.compute(aInputFrame, keypointScene, descriptorScene);

        // Matching
        MatOfDMatch matches = new MatOfDMatch();
        MatOfDMatch matches2 = new MatOfDMatch();
        if (mAceOfClubs.type() == aInputFrame.type() && descriptorAceOfClubs.type() == descriptorScene.type()) {
            matcher.match(descriptorAceOfClubs, descriptorScene, matches);
        } else {
            return aInputFrame;
        }
        if (mAceOfDiamonds.type() == aInputFrame.type() && descriptorAceOfDiamonds.type() == descriptorScene.type()) {
            matcher.match(descriptorAceOfDiamonds, descriptorScene, matches2);
        } else {
            return aInputFrame;
        }
        List<DMatch> matchesList = matches.toList();
        List<DMatch> matchesList2 = matches2.toList();
        double max_dist = 0.0;
        double min_dist = 100.0;
        for (int i = 0; i < matchesList.size(); i++) {
            double dist = (double) matchesList.get(i).distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }
        LinkedList<DMatch> good_matches = new LinkedList<>();
        for (int i = 0; i < matchesList.size(); i++) {
            if (matchesList.get(i).distance <= (2 * min_dist))
                good_matches.addLast(matchesList.get(i));
        }
        max_dist = 0.0;
        min_dist = 100.0;
        for (int i = 0; i < matchesList2.size(); i++) {
            double dist = (double) matchesList2.get(i).distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }
        LinkedList<DMatch> good_matches2 = new LinkedList<>();
        for (int i = 0; i < matchesList2.size(); i++) {
            if (matchesList2.get(i).distance <= (2 * min_dist))
                good_matches2.addLast(matchesList2.get(i));
        }

        Log.i("ACE OF CLUBS", String.valueOf(good_matches.size()));
        Log.i("ACE OF DIAMONDS", String.valueOf(good_matches2.size()));

        MatOfDMatch goodMatches2 = new MatOfDMatch();
        goodMatches2.fromList(good_matches2);

        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(good_matches);
        MatOfByte drawnMatches = new MatOfByte();
        if (aInputFrame.empty() || aInputFrame.cols() < 1 || aInputFrame.rows() < 1) {
            return aInputFrame;
        }

        List<Point> objListGoodMatches = new ArrayList<>();
        List<Point> sceneListGoodMatches = new ArrayList<>();
        List<Point> sceneListGoodMatches2 = new ArrayList<>();

        List<KeyPoint> keypoint1List = keypointAceOfClubs.toList();
        List<KeyPoint> keypoint2List = keypointScene.toList();
        List<KeyPoint> keypoint3List = keypointAceOfDiamonds.toList();

        for (int i = 0; i < good_matches.size(); i++) {
            // -- Get the keypoints from the good matches
            objListGoodMatches.add(keypoint1List.get(good_matches.get(i).queryIdx).pt);
            sceneListGoodMatches.add(keypoint2List.get(good_matches.get(i).trainIdx).pt);
            Core.circle(aInputFrame, new Point(sceneListGoodMatches.get(i).x, sceneListGoodMatches.get(i).y), 3, RED);
        }
        for (int i = 0; i < good_matches2.size(); i++) {
            // -- Get the keypoints from the good matches
            //objListGoodMatches.add(keypoint3List.get(good_matches2.get(i).queryIdx).pt);
            sceneListGoodMatches2.add(keypoint2List.get(good_matches2.get(i).trainIdx).pt);
            Core.circle(aInputFrame, new Point(sceneListGoodMatches2.get(i).x, sceneListGoodMatches2.get(i).y), 3, GREEN);
        }
/*
        MatOfPoint2f objListGoodMatchesMat = new MatOfPoint2f();
        objListGoodMatchesMat.fromList(objListGoodMatches);
        MatOfPoint2f sceneListGoodMatchesMat = new MatOfPoint2f();
        sceneListGoodMatchesMat.fromList(sceneListGoodMatches);
*/
        // findHomography needs 4 corresponding points
        if (good_matches.size() > 100) {
            //  Mat H = Calib3d.findHomography(objListGoodMatchesMat, sceneListGoodMatchesMat, Calib3d.RANSAC, 5 /* RansacTreshold */);
            /*
            Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

            obj_corners.put(0, 0, 0, 0);
            obj_corners.put(1, 0, mAceOfClubs.cols(), 0);
            obj_corners.put(2, 0, mAceOfClubs.cols(), mAceOfClubs.rows());
            obj_corners.put(3, 0, 0, mAceOfClubs.rows());

            Core.perspectiveTransform(obj_corners, scene_corners, H);

            Core.line(aInputFrame, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 2);
            Core.line(aInputFrame, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 2);
            Core.line(aInputFrame, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 2);
            Core.line(aInputFrame, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 2);
            */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bitmapOrig = Bitmap.createBitmap(mAceOfClubs.width(), mAceOfClubs.height(), conf);
                    Utils.matToBitmap(mAceOfClubs, bitmapOrig);
                    recognizedCard.setImageBitmap(bitmapOrig);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bitmapOrig = Bitmap.createBitmap(mBackOfCard.width(), mBackOfCard.height(), conf);
                    Utils.matToBitmap(mBackOfCard, bitmapOrig);
                    recognizedCard.setImageBitmap(bitmapOrig);
                }
            });
        }
        if (good_matches2.size() > 100) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bitmapOrig = Bitmap.createBitmap(mAceOfDiamonds.width(), mAceOfDiamonds.height(), conf);
                    Utils.matToBitmap(mAceOfDiamonds, bitmapOrig);
                    recognizedCard2.setImageBitmap(bitmapOrig);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bitmapOrig = Bitmap.createBitmap(mBackOfCard.width(), mBackOfCard.height(), conf);
                    Utils.matToBitmap(mBackOfCard, bitmapOrig);
                    recognizedCard2.setImageBitmap(bitmapOrig);
                }
            });
        }
        return aInputFrame;
    }

    /**
     * A native method that is implemented by the 'sift-test' native library,
     * which is packaged with this application.
     */
    public native void findFeatures(long matAddrRgba, long matAddrRefImg);
}

