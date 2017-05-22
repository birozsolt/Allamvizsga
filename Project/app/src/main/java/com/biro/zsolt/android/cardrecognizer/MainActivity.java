package com.biro.zsolt.android.cardrecognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    // Used to load the native libs on application startup.
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("nonfree");
        System.loadLibrary("sift-test");
    }

    Mat mRgba, mRgbaF, mRgbaT, mGray, mRefImg;
    DescriptorExtractor descriptor;
    DescriptorMatcher matcher;
    Mat descriptors2, descriptors1;
    MatOfKeyPoint keypoints1, keypoints2;
    Scalar RED = new Scalar(255, 0, 0);
    Scalar GREEN = new Scalar(0, 255, 0);
    private int counter = 0;
    private Bitmap bitmapOrig = null;
    private JavaCameraView javaCameraView;
    private ImageView recognizedCard;
    private FeatureDetector detector;
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
        detector = FeatureDetector.create(FeatureDetector.SURF);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.SURF);
        matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        bitmapToMat();
        descriptors1 = new Mat();
        keypoints1 = new MatOfKeyPoint();
        detector.detect(mRefImg, keypoints1);
        descriptor.compute(mRefImg, keypoints1, descriptors1);
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

    public void bitmapToMat() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmapOrig = BitmapFactory.decodeResource(this.getResources(), R.drawable.test_card, options);
        mRefImg = new Mat();
        Utils.bitmapToMat(bitmapOrig, mRefImg);
        recognizedCard.setImageBitmap(bitmapOrig);
    }
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
        if (recognizedCard != null) {
            recognizedCard.setImageBitmap(bitmapOrig);
        }
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
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        mGray = new Mat(width, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mRgbaF.release();
        mRgbaT.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
        Core.flip(mRgbaF, mRgba, 1);
        findFeatures(mRgba.getNativeObjAddr(), mRefImg.getNativeObjAddr());
        return mRgba;
        //return recognize(mRgba);
    }

    public Mat recognize(Mat aInputFrame) {

        descriptors2 = new Mat();
        keypoints2 = new MatOfKeyPoint();
        detector.detect(aInputFrame, keypoints2);
        descriptor.compute(aInputFrame, keypoints2, descriptors2);

        // Matching
        MatOfDMatch matches = new MatOfDMatch();
        if (mRefImg.type() == aInputFrame.type() && descriptors1.type() == descriptors2.type()) {
            matcher.match(descriptors1, descriptors2, matches);
        } else {
            return aInputFrame;
        }
        List<DMatch> matchesList = matches.toList();

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

        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(good_matches);
        Mat outputImg = new Mat();
        MatOfByte drawnMatches = new MatOfByte();
        if (aInputFrame.empty() || aInputFrame.cols() < 1 || aInputFrame.rows() < 1) {
            return aInputFrame;
        }

        List<Point> objListGoodMatches = new ArrayList<>();
        List<Point> sceneListGoodMatches = new ArrayList<>();

        List<KeyPoint> keypoint1List = keypoints1.toList();
        List<KeyPoint> keypoint2List = keypoints2.toList();

        for (int i = 0; i < good_matches.size(); i++) {
            // -- Get the keypoints from the good matches
            objListGoodMatches.add(keypoint1List.get(good_matches.get(i).queryIdx).pt);
            sceneListGoodMatches.add(keypoint2List.get(good_matches.get(i).trainIdx).pt);
            Core.circle(aInputFrame, new Point(sceneListGoodMatches.get(i).x, sceneListGoodMatches.get(i).y), 3, new Scalar(255, 0, 0, 255));
        }

        MatOfPoint2f objListGoodMatchesMat = new MatOfPoint2f();
        objListGoodMatchesMat.fromList(objListGoodMatches);
        MatOfPoint2f sceneListGoodMatchesMat = new MatOfPoint2f();
        sceneListGoodMatchesMat.fromList(sceneListGoodMatches);

        // findHomography needs 4 corresponding points
        if (good_matches.size() > 3) {
            Mat H = Calib3d.findHomography(objListGoodMatchesMat, sceneListGoodMatchesMat, Calib3d.RANSAC, 5 /* RansacTreshold */);

            Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

            obj_corners.put(0, 0, 0, 0);
            obj_corners.put(1, 0, mRefImg.cols(), 0);
            obj_corners.put(2, 0, mRefImg.cols(), mRefImg.rows());
            obj_corners.put(3, 0, 0, mRefImg.rows());

            Core.perspectiveTransform(obj_corners, scene_corners, H);

            Core.line(aInputFrame, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 2);
            Core.line(aInputFrame, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 2);
            Core.line(aInputFrame, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 2);
            Core.line(aInputFrame, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 2);

        }
        /*Imgproc.cvtColor(aInputFrame,aInputFrame,Imgproc.COLOR_RGBA2RGB);
        Features2d.drawKeypoints(aInputFrame,keypoints2,aInputFrame,RED,Features2d.NOT_DRAW_SINGLE_POINTS);
        //Features2d.drawMatches(mRefImg,keypoints1,aInputFrame,keypoints2,matches,aInputFrame, GREEN, RED, drawnMatches, Features2d.DRAW_RICH_KEYPOINTS);
        Imgproc.cvtColor(aInputFrame,aInputFrame,Imgproc.COLOR_RGB2RGBA);
*/
        return aInputFrame;
    }

    /**
     * A native method that is implemented by the 'sift-test' native library,
     * which is packaged with this application.
     */
    public native void findFeatures(long matAddrRgba, long matAddrRefImg);
}

