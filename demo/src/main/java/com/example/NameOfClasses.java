package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.calculate.AccessToData;
import com.example.calculate.IAttribute;
import com.example.calculate.NumOverrideMethods;
import com.example.calculate.NumProtMembersInParent;
import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

public class NameOfClasses extends VoidVisitorAdapter<String> {

    List<String> nameOfClasses = new ArrayList<>();
    List<IAttribute> metricForClasses = new ArrayList<>();
    List<IAttribute> metricForMethods = new ArrayList<>();
    HashMap<ClassOrInterfaceDeclaration, ClassMetrics> classesMetrics = new HashMap<>();

    public NameOfClasses() {
        super();

        this.metricForClasses.add(new NumProtMembersInParent(nameOfClasses));
        this.metricForClasses.add(new NumOverrideMethods());

        this.metricForMethods.add(new AccessToData());
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
            List<ResolvedReferenceType> ancestors = resolve.getAllAncestors().stream().filter(a -> a.getTypeDeclaration().isPresent() && a.getTypeDeclaration().get().isClass()).toList();

            if (!ancestors.isEmpty()) {
                List<String> classOfAncestors = new ArrayList<>();
                for (ResolvedReferenceType ancestor : ancestors) {
                    if (nameOfClasses.contains(ancestor.getQualifiedName())) {
                        classOfAncestors.add(ancestor.getQualifiedName());
                    }
                }
                classMetrics.setAttribute("classOfAncestors", classOfAncestors);
            } else {
                classMetrics.setAttribute("classOfAncestors", null);
            }
        }
    }

    public void excuteMetrics() {
        for (ClassOrInterfaceDeclaration clazz : classesMetrics.keySet()) {
            ClassMetrics classMetrics = classesMetrics.get(clazz);
            //System.out.println("クラス名 : " + clazz.getFullyQualifiedName().get());
            List<MethodDeclaration> methods = clazz.getMethods();
            for (MethodDeclaration method : methods) {
                MethodMetrics methodMetrics = new MethodMetrics(method, classMetrics);
                for (IAttribute metric : metricForMethods) {
                    metric.calculate(methodMetrics);
                }
                classMetrics.getMethodsMetrics().add(methodMetrics);
            }

            /*for(IAttribute metric : metricForClasses){
                metric.calculate(classMetrics);
                System.out.println(metric.getName()+" : "+classMetrics.getAttribute(metric.getName()));
            }*/
        }
    }

    @SuppressWarnings("unchecked")
    public void printCSV(String arg) throws IOException {
        int allMethod=0;
        int allMethodError=0;
        int flag=0;
        int allMethodExpr = 0;
        int allMethodExprError = 0;
        try {
            FileWriter fw = new FileWriter("..\\create_data\\" + arg, false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.print("class");
            pw.print(",");
            pw.print("method");
            pw.print(",");
            pw.print("ATFD");
            pw.print(",");
            pw.print("ATLD");
            pw.print(",");
            pw.print("Error");
            pw.println();
            for (ClassOrInterfaceDeclaration clazz : classesMetrics.keySet()) {
                ClassMetrics classMetrics = classesMetrics.get(clazz);
                for (MethodMetrics method : classMetrics.getMethodsMetrics()) {
                    allMethod+=1;
                    flag=0;
                    pw.print(clazz.getFullyQualifiedName());
                    pw.print(",");
                    pw.print(method.getDeclaration().getName());
                    pw.print(",");
                    for (String ATFD : (List<String>) method.getAttribute("ListOfATFD")) {
                        pw.print(ATFD + " | ");
                        allMethodExpr += 1;
                    }
                    pw.print(",");
                    for (String ATLD : (List<String>) method.getAttribute("ListOfATLD")) {
                        pw.print(ATLD + " | ");
                        allMethodExpr += 1;

                    }
                    pw.print(",");
                    for (String Error : (List<String>) method.getAttribute("ListOfError")) {
                        if(flag==0){
                            allMethodError+=1;
                            flag=1;
                        }
                        pw.print(Error + " | ");
                        allMethodExpr += 1;
                        allMethodExprError += 1;

                    }
                    pw.println();

                }
            }
            pw.close();
            System.out.println("総メソッド数 : "+allMethod);
            System.out.println("総エラーありメソッド数 : "+allMethodError);
            System.out.println("総呼び出しメソッド数 : "+allMethodExpr);
            System.out.println("総エラー数 : "+allMethodExprError);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printAncestors() {
        for (ClassOrInterfaceDeclaration clazz : classesMetrics.keySet()) {
            System.out.println("クラス名 : " + clazz.getFullyQualifiedName());
            ClassMetrics classMetrics = classesMetrics.get(clazz);
            System.out.println("親クラス : " + classMetrics.getAttribute("classOfAncestors") + "\n");
        }
    }
}
