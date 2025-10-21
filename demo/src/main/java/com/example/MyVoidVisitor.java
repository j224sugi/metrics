package com.example;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;



public class MyVoidVisitor extends VoidVisitorAdapter<String> {

    @Override
    public void visit(ClassOrInterfaceDeclaration n,String arg) {
        System.out.println("arg > "+arg);
        System.out.println("class or interface > " + n.getNameAsString());
        ClassOrInterfaceDeclaration clazz = n;

        clazz.getExtendedTypes().forEach(type->{                                //clazz.getExtemdedTypes()で親クラス、インターフェースの情報を得る。
            try {
                ResolvedType resolvedType = type.resolve();                        //親クラスの情報が格納される
                System.out.println("親クラス : "+resolvedType.describe());          //親クラスの名前を表示
            } catch (Exception e) {
                System.out.println("解決できませんでした : "+e.getMessage());
            }
        });
        
        //super.visit(n, arg);                                                    //superによりフィールドやメソッド、内部クラスなどノードを検索することができる。
    }
}
