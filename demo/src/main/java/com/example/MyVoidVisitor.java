package com.example;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;



public class MyVoidVisitor extends VoidVisitorAdapter<String> {
    int num=0;
    @Override
    public void visit(ClassOrInterfaceDeclaration n,String arg) {

        System.out.println("arg > "+arg);
        System.out.println("class or interface > " + n.getNameAsString());
        ClassOrInterfaceDeclaration clazz = n;

        ResolvedReferenceTypeDeclaration resolve=clazz.resolve();                   //resolve.getAncestors()にすると継承関係がないと例外が渡される．また，クラスとインターフェイスが渡される
        List<ResolvedReferenceType> ancestors=resolve.getAllAncestors().stream()       //streamにすることでfilterを使用し要素に対して処理できる
                                                                    .filter(a->a.getTypeDeclaration().isPresent()&&a.getTypeDeclaration().get().isClass()&&a.describe().startsWith("org"))       //gettypedeclaration()で宣言部分を返す.ispresent()で型解決できるか.isClass()でクラスであるか確認
                                                                    .toList();
        if(!ancestors.isEmpty()){
            System.out.print("親クラス > ");
            for(ResolvedReferenceType ancestor:ancestors){
                System.out.print(ancestor.describe()+" ");
                System.out.println(ancestor.getQualifiedName());
            }
            System.out.println();
        }else{
            System.out.println("親クラスなし");
        }
        /*clazz.forEach(type->{                                //clazz.getExtemdedTypes()で親クラス、インターフェースの情報を得る。
            try {
                ResolvedType resolvedType = type.resolve();                        //親クラスの情報が格納される
                System.out.println("親クラス : "+resolvedType.describe());          //親クラスの名前を表示
            } catch (Exception e) {
                System.out.println("解決できませんでした : "+e.getMessage());
            }
        });*/
        
        //super.visit(n, arg);                                                    //superによりフィールドやメソッド、内部クラスなどノードを検索することができる。
    }
}
