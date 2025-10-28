package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.calculate.IAttribute;
import com.example.calculate.NumOverrideMethods;
import com.example.calculate.NumProtMembersInParent;
import com.example.node.ClassMetrics;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

public class NameOfClasses extends VoidVisitorAdapter<String> {

    List<String> nameOfClasses = new ArrayList<>();
    List<IAttribute> metricForClasses = new ArrayList<>();
    List<IAttribute> metricForMethods=new ArrayList<>();
    HashMap<ClassOrInterfaceDeclaration, ClassMetrics> classesMetrics = new HashMap<>();

    public NameOfClasses(){
        super();

        this.metricForClasses.add(new NumProtMembersInParent(nameOfClasses));
        this.metricForClasses.add(new NumOverrideMethods());
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration clazz, String arg) {
        nameOfClasses.add(clazz.getFullyQualifiedName().get());
        ClassMetrics classMetrics = new ClassMetrics(clazz);
        classesMetrics.put(clazz, classMetrics);
        super.visit(clazz, arg);
    }

    public void setAncestors() {
        for (ClassOrInterfaceDeclaration clazz : classesMetrics.keySet()) {
            ClassMetrics classMetrics = classesMetrics.get(clazz);
            classMetrics.setAttribute("nameOfClasses", nameOfClasses);
            ResolvedReferenceTypeDeclaration resolve = clazz.resolve();
            List<ResolvedReferenceType> ancestors = resolve.getAllAncestors().stream().filter(a->a.getTypeDeclaration().isPresent()&&a.getTypeDeclaration().get().isClass()).toList();

            if (!ancestors.isEmpty()) {
                List<String> classOfAncestors=new ArrayList<>();
                for (ResolvedReferenceType ancestor : ancestors) {
                    if(nameOfClasses.contains(ancestor.getQualifiedName())){
                        classOfAncestors.add(ancestor.getQualifiedName());
                    }
                }
                classMetrics.setAttribute("classOfAncestors", classOfAncestors);
            }else{
                classMetrics.setAttribute("classOfAncestors",null);
            }
        }
    }
    public void excuteMetrics(){
        for(ClassOrInterfaceDeclaration clazz:classesMetrics.keySet()){
            ClassMetrics classMetrics=classesMetrics.get(clazz);
            System.out.println("クラス名 : "+clazz.getFullyQualifiedName().get());
            for(IAttribute metric : metricForClasses){
                metric.calculate(classMetrics);
                System.out.println(metric.getName()+" : "+classMetrics.getAttribute(metric.getName()));
            }
            System.err.println("");
        }
    }
    

    public void printAncestors(){
        for(ClassOrInterfaceDeclaration clazz:classesMetrics.keySet()){
            System.out.println("クラス名 : "+clazz.getFullyQualifiedName());
            ClassMetrics classMetrics=classesMetrics.get(clazz);
            System.out.println("親クラス : "+classMetrics.getAttribute("classOfAncestors")+"\n");
        }
    }
}
