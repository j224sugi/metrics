package com.example.node;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodMetrics extends NodeMetrics {

    private MethodDeclaration declaration;
    private ClassMetrics classMetrics;

    public MethodMetrics(MethodDeclaration declaration, ClassMetrics classMetrics) {
        this.declaration = declaration;
        this.classMetrics = classMetrics;
    }

    public MethodDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(MethodDeclaration declaration) {
        this.declaration = declaration;
    }

    public ClassOrInterfaceDeclaration getClassParent() {
        return classMetrics.getDeclaration();
    }

    public ClassMetrics getClassMetrics() {
        return classMetrics;
    }
}
