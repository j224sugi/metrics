package com.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

public class RefusedBequest extends VoidVisitorAdapter<String> {

    private List<ResolvedMethodDeclaration> ParentMethods = new ArrayList<>();
    private List<ResolvedFieldDeclaration> ParentFields = new ArrayList<>();
    private List<ResolvedMethodDeclaration> ParentProtMehotds = new ArrayList<>();
    private List<ResolvedFieldDeclaration> ParentProtFields = new ArrayList<>();
    private Set<ResolvedMethodDeclaration> ClassMethods = new HashSet<>();
    private int overrideNum;

    @Override
    public void visit(ClassOrInterfaceDeclaration n, String arg) {
        
        System.out.println("class or interface > " + n.getFullyQualifiedName());
        ClassOrInterfaceDeclaration clazz = n;
                
        ResolvedReferenceTypeDeclaration resolve = clazz.resolve();                   //resolve.getAncestors()にすると継承関係がないと例外が渡される．また，クラスとインターフェイスが渡される
        ClassMethods=resolve.getDeclaredMethods();

        List<ResolvedReferenceType> ancestors = resolve.getAllAncestors().stream() //streamにすることでfilterを使用し要素に対して処理できる
                .filter(a -> a.getTypeDeclaration().isPresent() && a.getTypeDeclaration().get().isClass() && a.describe().startsWith("org")) //gettypedeclaration()で宣言部分を返す.ispresent()で型解決できるか.isClass()でクラスであるか確認
                .toList();

        if (!ancestors.isEmpty()) {
            for (ResolvedReferenceType ancestor : ancestors) {
                System.out.println("親クラス > " + ancestor.describe());
                ListProtMethods(ancestor);
                ListProtFields(ancestor);
            }
        } else {
            System.out.println("親クラスなし");
        }
        overrideNum=0;
        System.out.print("オーバーライドメソッド : ");
        for(ResolvedMethodDeclaration method:ClassMethods){
            if(isOverride(method, ParentMethods)){
                overrideNum+=1;
                System.out.print(method.getName()+" ");
            }
        }
        
        //System.out.println("\nNProtM : "+ParentProtMehotds);
        System.out.print("\n");
        ParentProtMehotds.forEach(a->System.out.print(a.getName()+" "));

        if(!ClassMethods.isEmpty()){
            System.out.println("\nBOvR : "+((double)overrideNum/ClassMethods.size()));       
        }else{
            System.out.println("\nBOvR : "+null);
        }
        System.out.println("\n");
        super.visit(n,arg);
    }

    public void ListProtMethods(ResolvedReferenceType ancestor) {
        if (ancestor.getDeclaredMethods() != null) {
            for (MethodUsage method : ancestor.getDeclaredMethods()) {
                ResolvedMethodDeclaration resolveMethod = method.getDeclaration();
                if (!isOverride(resolveMethod, ParentMethods)) {
                    ParentMethods.add(resolveMethod);
                    if (resolveMethod.accessSpecifier() == AccessSpecifier.PROTECTED) {
                        ParentProtMehotds.add(resolveMethod);
                    }
                }
            }
        }
    }

    public void ListProtFields(ResolvedReferenceType ancestor){
        if(ancestor.getDeclaredFields()!=null){
            for(ResolvedFieldDeclaration field : ancestor.getDeclaredFields()){
                if(!ParentFields.contains(field)){
                    ParentFields.add(field);
                    if(field.accessSpecifier()==AccessSpecifier.PROTECTED){
                        ParentProtFields.add(field);
                    }
                }
            }
        }
    }

    public boolean  isOverride(ResolvedMethodDeclaration newMethod,List<ResolvedMethodDeclaration> Methods){
        for(ResolvedMethodDeclaration method:Methods){
            if(isOverride(newMethod,method)){
                return true;
            }
        }
        return false;
    }

    public boolean isOverride(ResolvedMethodDeclaration childMethod,ResolvedMethodDeclaration ancestorMethod){
        return childMethod.getName().equals(ancestorMethod.getName())&&childMethod.getTypeParameters().equals(ancestorMethod.getTypeParameters());
    }
}
