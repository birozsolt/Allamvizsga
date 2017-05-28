package com.biro.zsolt.android.cardrecognizer;

import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

/**
 * Created by Zsolt on 2017. 05. 28..
 */

class Config {
    static final FeatureDetector detector;
    static final DescriptorExtractor descriptor;
    static final DescriptorMatcher matcher;

    static {
        detector = FeatureDetector.create(FeatureDetector.SURF);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.SURF);
        matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
    }
}
