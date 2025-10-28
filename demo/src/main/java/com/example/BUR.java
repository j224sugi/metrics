package com.example;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class BUR extends VoidVisitorAdapter<String> {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, String arg) {
        List<MethodDeclaration> MethodDec;
        String clazz = n.getFullyQualifiedName().get();
        System.out.println("クラス名 : "+clazz);
        MethodDec=n.getMethods();
        for(MethodDeclaration method:MethodDec){
            ResolvedMethodDeclaration resolveMethod=method.resolve();
            System.out.println("メソッド名 : "+resolveMethod.getName());
            System.out.println("getClassName() : "+resolveMethod.getClassName());
        }
        System.out.println();
    }
}
