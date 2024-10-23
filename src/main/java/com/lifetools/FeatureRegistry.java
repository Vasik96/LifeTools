package com.lifetools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FeatureRegistry {
    private static final List<FeatureDetails> features = new ArrayList<>();

    public static void registerFeatureMethod(String methodName, String featureName, Method method) {
        features.add(new FeatureDetails(methodName, featureName, method));
    }

    public static List<FeatureDetails> getFeatures() {
        return features;
    }

    public static class FeatureDetails {
        public final String methodName;
        public final String featureName;
        public final Method method;

        public FeatureDetails(String methodName, String featureName, Method method) {
            this.methodName = methodName;
            this.featureName = featureName;
            this.method = method;
        }
    }
}
