package com.example;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

public class MyVoidVisitor extends VoidVisitorAdapter<String> {

    private List<String> nameOfParentMethods = new ArrayList<>();
    private List<String> nameOfParentFields = new ArrayList<>();
    private List<String> nameOfMethod = new ArrayList<>();

    @Override
    public void visit(ClassOrInterfaceDeclaration n, String arg) {
        if(n.isInnerClass()){
            System.out.println("インナークラス");
        }else if(n.isLocalClassDeclaration()){
            System.out.println("ローカルクラス");
        }
        System.out.println("class or interface > " + n.getFullyQualifiedName());
        ClassOrInterfaceDeclaration clazz = n;
        
        nameOfMethod=n.getMethods().stream().map(p->p.getName().asString()).toList();
        System.out.print("メソッド名 : ");
        nameOfMethod.forEach(a->System.out.print(a+" "));
        System.out.println();
        
        ResolvedReferenceTypeDeclaration resolve = clazz.resolve();                   //resolve.getAncestors()にすると継承関係がないと例外が渡される．また，クラスとインターフェイスが渡される
        List<ResolvedReferenceType> ancestors = resolve.getAllAncestors().stream() //streamにすることでfilterを使用し要素に対して処理できる
                .filter(a -> a.getTypeDeclaration().isPresent() && a.getTypeDeclaration().get().isClass() && a.describe().startsWith("org")) //gettypedeclaration()で宣言部分を返す.ispresent()で型解決できるか.isClass()でクラスであるか確認
                .toList();

        if (!ancestors.isEmpty()) {
            for (ResolvedReferenceType ancestor : ancestors) {
                System.out.println("親クラス > " + ancestor.describe());
                printProtMethods(ancestor);
                printProtFields(ancestor);
            }
        } else {
            System.out.println("親クラスなし");
        }
        System.out.print("オーバーライドメソッド : ");
        for(String method:nameOfMethod){
            if(nameOfParentMethods.contains(method)){
                System.out.print(method+" ");
            }
        }
        System.out.println("\n");
        super.visit(n,arg);
    }

    public void printProtMethods(ResolvedReferenceType ancestor) {
        if (ancestor.getDeclaredMethods() != null) {
            System.err.print("メソッド名 > ");
            for (MethodUsage method : ancestor.getDeclaredMethods()) {
                ResolvedMethodDeclaration resolveMethod = method.getDeclaration();
                if (!nameOfParentMethods.contains(resolveMethod.getName())) {
                    nameOfParentMethods.add(resolveMethod.getName());
                    if (resolveMethod.accessSpecifier() == AccessSpecifier.PROTECTED) {
                        System.out.print(resolveMethod.getName() + " ");
                    }
                }
            }
            System.out.println();
        }
    }

    public void printProtFields(ResolvedReferenceType ancestor){
        if(ancestor.getDeclaredFields()!=null){
            System.out.print("フィールド名 > ");
            for(ResolvedFieldDeclaration field : ancestor.getDeclaredFields()){
                if(nameOfParentFields.contains(field.getName())){
                    nameOfParentFields.add(field.getName());
                    if(field.accessSpecifier()==AccessSpecifier.PROTECTED){
                        System.out.print(field.getName()+" ");
                    }
                }
            }
            System.out.println();
        }
    }
}
