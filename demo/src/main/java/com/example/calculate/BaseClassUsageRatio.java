package com.example.calculate;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;

public class BaseClassUsageRatio implements IAttribute {

    @Override
    public String getName() {
        return "BUR";
    }

    @Override
    public void calculate(MethodMetrics node) {
        // do not calculate
    }

    @Override
    public void calculate(ClassMetrics node) {
        double ratio = 1;
        int uses = 0;

        if (node.getMetric("NprotM") != null && node.getMetric("NprotM") > 0) {
            
        }
    }

}
