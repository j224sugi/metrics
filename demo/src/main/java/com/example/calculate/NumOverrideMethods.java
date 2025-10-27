package com.example.calculate;

import java.util.List;
import java.util.Set;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

public class NumOverrideMethods implements IAttribute {

    List<String> nameOfClasses;

    public NumOverrideMethods(List<String> nameOfClasses) {
        this.nameOfClasses = nameOfClasses;
    }

    @Override
    public String getName() {
        return "BOvR";
    }

    @Override
    public void calculate(ClassMetrics node) {
        int overrideNum = 0;
        double ration=0;
        ResolvedReferenceTypeDeclaration resolve = node.getDeclaration().resolve();
        List<ResolvedMethodDeclaration> ancestorsMethods = (List<ResolvedMethodDeclaration>) node.getAttribute("ancestorsMethods");
        Set<ResolvedMethodDeclaration> ClassMethods = resolve.getDeclaredMethods();
        if (!ClassMethods.isEmpty()&&!ancestorsMethods.isEmpty()) {
            for (ResolvedMethodDeclaration method : ClassMethods) {
                if(isOverride(method,ancestorsMethods)){
                    overrideNum+=1;
                }
            }
            ration=(double)overrideNum/ClassMethods.size();
        }
        node.setAttribute(getName(), ration);
    }

    @Override
    public void calculate(MethodMetrics node) {

    }

    public boolean isOverride(ResolvedMethodDeclaration newMethod, List<ResolvedMethodDeclaration> Methods) {
        for (ResolvedMethodDeclaration method : Methods) {
            if (isOverride(newMethod, method)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOverride(ResolvedMethodDeclaration childMethod, ResolvedMethodDeclaration ancestorMethod) {
        return childMethod.getName().equals(ancestorMethod.getName()) && childMethod.getTypeParameters().equals(ancestorMethod.getTypeParameters());
    }
}
