//
// Created by Zsolt on 2017. 05. 13..
//

#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/nonfree/features2d.hpp>
#include "opencv2/calib3d/calib3d.hpp"

using namespace cv;
using namespace std;

extern "C" {
JNIEXPORT void JNICALL Java_com_biro_zsolt_android_cardrecognizer_MainActivity_findFeatures(JNIEnv*, jobject,
                                                  jlong addrGray, jlong addrRgba, jlong addrRefImg);
};

JNIEXPORT void JNICALL Java_com_biro_zsolt_android_cardrecognizer_MainActivity_findFeatures(
        JNIEnv*, jobject, jlong addrGray, jlong addrRgba, jlong addrRefImg)
{
    Mat& mGr  = *(Mat*)addrGray;
    Mat& mRgb = *(Mat*)addrRgba;
    Mat& mRefImg = *(Mat*)addrRefImg;

    // Detect the keypoints using SURF Detector
    vector<KeyPoint> keypointRef, keypointScene;
    int minHessian = 100;
    SurfFeatureDetector detector(minHessian);
    detector.detect(mRefImg, keypointRef);
    detector.detect(mRgb, keypointScene);

    // Calculate descriptors (feature vectors)
    SurfDescriptorExtractor extractor;
    Mat descriptorsRef, descriptorsScene;
    extractor.compute(mRefImg, keypointRef, descriptorsRef);
    extractor.compute(mRgb, keypointScene, descriptorsScene);

    // Matching descriptor vectors using FLANN matcher
    FlannBasedMatcher matcher;
    std::vector<DMatch> matches;
    matcher.match(descriptorsScene, descriptorsRef, matches );
    double max_dist = 0; double min_dist = 100;

    //-- Quick calculation of max and min distances between keypoints
    for( int i = 0; i < descriptorsRef.rows; i++ ){
        double dist = matches[i].distance;
        if( dist < min_dist ) min_dist = dist;
        if( dist > max_dist ) max_dist = dist;
    }

    // Draw only "good" matches (i.e. whose distance is less than 3*min_dist )
    std::vector<DMatch> goodMatches;

    for(int i = 0; i < descriptorsRef.rows; i++){
        if(matches[i].distance < 3*min_dist){
            goodMatches.push_back(matches[i]);
        }
    }
/*    Mat img_matches;
    drawMatches( mRefImg, keypointRef, mRgb, keypointScene,
                 goodMatches, img_matches, Scalar::all(-1), Scalar::all(-1),
                 vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS );
    //-- Localize the object
    std::vector<Point2f> obj;
    std::vector<Point2f> scene;
    for(int i = 0; i < goodMatches.size(); i++){
        //-- Get the keypoints from the good matches
        obj.push_back( keypointRef[ goodMatches[i].queryIdx ].pt );
        scene.push_back( keypointScene[ goodMatches[i].trainIdx ].pt );
    }
    Mat H = findHomography( obj, scene, CV_RANSAC );

    // Get the corners from the image_1 ( the object to be "detected" )
    std::vector<Point2f> obj_corners(4);
    obj_corners[0] = cvPoint(0,0); obj_corners[1] = cvPoint( mRefImg.cols, 0 );
    obj_corners[2] = cvPoint( mRefImg.cols, mRefImg.rows ); obj_corners[3] = cvPoint( 0, mRefImg.rows );
    std::vector<Point2f> scene_corners(4);

    perspectiveTransform( obj_corners, scene_corners, H);

    //-- Draw lines between the corners (the mapped object in the scene - image_2 )
    line( img_matches, scene_corners[0] + Point2f( mRefImg.cols, 0), scene_corners[1] + Point2f( mRefImg.cols, 0), Scalar(0, 255, 0), 4 );
    line( img_matches, scene_corners[1] + Point2f( mRefImg.cols, 0), scene_corners[2] + Point2f( mRefImg.cols, 0), Scalar( 0, 255, 0), 4 );
    line( img_matches, scene_corners[2] + Point2f( mRefImg.cols, 0), scene_corners[3] + Point2f( mRefImg.cols, 0), Scalar( 0, 255, 0), 4 );
    line( img_matches, scene_corners[3] + Point2f( mRefImg.cols, 0), scene_corners[0] + Point2f( mRefImg.cols, 0), Scalar( 0, 255, 0), 4 );
*/
    // Draw keypoints on detected object
    for (int i = 0; i < goodMatches.size(); ++i) {
        const KeyPoint &kp = keypointRef[goodMatches[i].queryIdx];
        circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255, 0, 0, 255));
    }
}