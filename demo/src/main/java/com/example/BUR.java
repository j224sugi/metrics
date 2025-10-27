package com.example;

import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class BUR extends VoidVisitorAdapter<String> {

    private HashMap<String, List<MethodDeclaration>> ClassMethodInv = new HashMap<>();

    @Override
    public void visit(ClassOrInterfaceDeclaration n, String arg) {
        HashMap<MethodDeclaration,List<MethodCallExpr>> methodCall = new HashMap<>();
        List<MethodDeclaration> MethodDec;
        String clazz = n.getFullyQualifiedName().get();
        MethodDec=n.getMethods();
        ClassMethodInv.put(clazz, MethodDec);
        for(MethodDeclaration method:MethodDec){
            methodCall.put(method,method.findAll(MethodCallExpr.class));
        }
        super.visit(n, arg);

    }
}
