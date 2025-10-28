package com.example;

import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class NOMVisitor extends VoidVisitorAdapter<String> {

    @Override
    public void visit(ClassOrInterfaceDeclaration node, String arg) {
        HashMap<MethodDeclaration, List<MethodCallExpr>> methodCall = new HashMap<>();
        HashMap<String, List<MethodDeclaration>> ClassMethodDecl = new HashMap<>();

        List<MethodDeclaration> MethodDec;
        String clazz = node.getFullyQualifiedName().get();
        MethodDec = node.getMethods();

        ClassMethodDecl.put(clazz, MethodDec);

        for (MethodDeclaration method : MethodDec) {
            methodCall.put(method, method.findAll(MethodCallExpr.class));
        }
    }

}
