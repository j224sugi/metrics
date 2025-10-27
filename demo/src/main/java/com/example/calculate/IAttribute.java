package com.example.calculate;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;

public interface IAttribute {
    public void calculate(ClassMetrics node);

    public void calculate(MethodMetrics node);

    public String getName();
}
