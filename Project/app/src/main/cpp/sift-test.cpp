//
// Created by Zsolt on 2017. 05. 13..
//

#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/nonfree/features2d.hpp>
#include "opencv2/calib3d/calib3d.hpp"
#include "android/log.h"

#define  LOG_TAG    "openCVTag"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

using namespace cv;
using namespace std;

extern "C" {
JNIEXPORT void JNICALL Java_com_biro_zsolt_android_cardrecognizer_MainActivity_findFeatures(JNIEnv*, jobject,
                                                                                            jlong addrVideoFrame,
                                                                                            jlong addrCard);
};

JNIEXPORT void JNICALL Java_com_biro_zsolt_android_cardrecognizer_MainActivity_findFeatures(
        JNIEnv *, jobject, jlong addrVideoFrame, jlong addrCard)
{
    Mat &mVideoFrame = *(Mat *) addrVideoFrame;
    Mat &mCard = *(Mat *) addrCard;

    // Detect the keypoints using SURF Detector
    vector<KeyPoint> keypointsCard, keypointsVideoFrame;
    int minHessian = 1000;
    SurfFeatureDetector detector(minHessian);
    detector.detect(mCard, keypointsCard);
    detector.detect(mVideoFrame, keypointsVideoFrame);

    // Calculate descriptors (feature vectors)
    SurfDescriptorExtractor extractor;
    Mat descriptorCard, descriptorVideoFrame;
    extractor.compute(mCard, keypointsCard, descriptorCard);
    extractor.compute(mVideoFrame, keypointsVideoFrame, descriptorVideoFrame);

    if (descriptorCard.type() != CV_32F) //CV_32F CV_8U
        descriptorCard.convertTo(descriptorCard, CV_32F);
    if (descriptorVideoFrame.type() != CV_32F)
        descriptorVideoFrame.convertTo(descriptorVideoFrame, CV_32F);

    if (descriptorCard.empty())
        LOGI("Object descriptor empty");
    if (descriptorVideoFrame.empty()) {
        LOGI("Scene descriptor empty");
    } else {
        // Matching descriptor vectors using FLANN matcher
        BFMatcher matcher(NORM_L2);
        //FlannBasedMatcher matcher(new flann::KDTreeIndexParams(5));
        vector<vector<DMatch>> matches;
        if ((descriptorCard.type() == descriptorVideoFrame.type()) &&
            (descriptorCard.cols == descriptorVideoFrame.cols)) {
            matcher.knnMatch(descriptorCard, descriptorVideoFrame, matches, 2);
        }

        vector<DMatch> goodMatches;
        goodMatches.reserve(matches.size());
        int nndrRatio = 2; // Nearest Neighbor Distance Ratio
        for (size_t i = 0; i < matches.size(); ++i) {
            if (matches[i].size() < 2)
                continue;
            const DMatch &m1 = matches[i][0];
            const DMatch &m2 = matches[i][1];

            if (m1.distance <= nndrRatio * m2.distance)
                goodMatches.push_back(m1);
        }
        vector<Point2f> obj;
        vector<Point2f> scene;

        for (unsigned int i = 0; i < goodMatches.size(); i++) {
            //-- Get the keypoints from the good matches
            obj.push_back(keypointsCard[goodMatches[i].queryIdx].pt);
            scene.push_back(keypointsVideoFrame[goodMatches[i].trainIdx].pt);
            circle(mVideoFrame, Point((int) scene[i].x, (int) scene[i].y), 4,
                   Scalar(255, 0, 0, 255));
        }
        if (goodMatches.size() > 4) {
            Mat H = findHomography(obj, scene, CV_RANSAC);

            //-- Get the corners from the image_1 ( the object to be "detected" )
            vector<Point2f> obj_corners(4);
            obj_corners[0] = cvPoint(0, 0);
            obj_corners[1] = cvPoint(mCard.cols, 0);
            obj_corners[2] = cvPoint(mCard.cols, mCard.rows);
            obj_corners[3] = cvPoint(0, mCard.rows);
            vector<Point2f> scene_corners(4);


            perspectiveTransform(obj_corners, scene_corners, H);

            //-- Draw lines between the corners (the mapped object in the scene - img_camera )
            line(mVideoFrame, scene_corners[0], scene_corners[1], Scalar(0, 255, 0), 2);
            line(mVideoFrame, scene_corners[1], scene_corners[2], Scalar(0, 255, 0), 2);
            line(mVideoFrame, scene_corners[2], scene_corners[3], Scalar(0, 255, 0), 2);
            line(mVideoFrame, scene_corners[3], scene_corners[0], Scalar(0, 255, 0), 2);

        }
//    // Draw keypoints on detected object
//    for (int i = 0; i < goodMatches.size()/*keypointsVideoFrame.size()*/; ++i) {
//        const KeyPoint &kp = /*keypointsVideoFrame[i];*/keypointsVideoFrame[goodMatches[i].trainIdx];
//        circle(mVideoFrame, Point(kp.pt.x, kp.pt.y), 10, Scalar(255, 0, 0, 255));
//    }
    }
}