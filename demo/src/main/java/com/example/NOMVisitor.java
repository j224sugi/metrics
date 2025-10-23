package com.example;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class NOMVisitor extends VoidVisitorAdapter<String>{
    int NOM;
    @Override
    public void visit(ClassOrInterfaceDeclaration node,String arg){
        this.NOM=0;
        System.out.println("クラス名 : "+node.getFullyQualifiedName());
        System.out.print("メソッド名 : ");
        super.visit(node,arg);
        System.out.println("メソッド数 : "+this.NOM+"\n");
    }

    @Override
    public void visit(MethodDeclaration method,String arg){
        System.out.print(method.getNameAsString()+" ");
        this.NOM=this.NOM+1;
    }
}
