package com.example.node;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class ClassMetrics extends NodeMetrics {

    ClassOrInterfaceDeclaration declaration;
    List<MethodMetrics> methodsMetrics;

    public ClassMetrics(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
        this.methodsMetrics = new ArrayList<>();
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(ClassOrInterfaceDeclaration declaration) {
        this.declaration = declaration;
    }

    public List<MethodMetrics> getMethodsMetrics() {
        return methodsMetrics;
    }

    public void setMethodsMetrics(List<MethodMetrics> methodsMetrics) {
        this.methodsMetrics = methodsMetrics;
    }

}
