package com.example.calculate;

import java.util.List;
import java.util.Set;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

public class NumOverrideMethods implements IAttribute {

    //List<String> nameOfClasses;

    /*public NumOverrideMethods(List<String> nameOfClasses) {
        this.nameOfClasses = nameOfClasses;
    }*/

    @Override
    public String getName() {
        return "BOvR";
    }

    @Override
    public void calculate(ClassMetrics node) {
        int overrideNum = 0;
        double ration = 0;
        ResolvedReferenceTypeDeclaration resolve = node.getDeclaration().resolve();
        @SuppressWarnings("unchecked")
        List<ResolvedMethodDeclaration> ancestorsMethods = (List<ResolvedMethodDeclaration>) node.getAttribute("ancestorsMethods");
        Set<ResolvedMethodDeclaration> ClassMethods = resolve.getDeclaredMethods();
        if (!ClassMethods.isEmpty() && !ancestorsMethods.isEmpty()) {
            for (ResolvedMethodDeclaration method : ClassMethods) {
                if (isOverride(method, ancestorsMethods)) {
                    System.out.print(method.getName()+" ");
                    overrideNum += 1;
                }
            }
            System.err.println("");
            ration = (double) overrideNum / ClassMethods.size();
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

    public boolean isOverride(ResolvedMethodDeclaration childMethod, ResolvedMethodDeclaration parentMethod) {
        if (!childMethod.getName().equals(parentMethod.getName())) {
            return false;
        }
        if (childMethod.getNumberOfParams() != parentMethod.getNumberOfParams()) {
            return false;
        }
        for (int i = 0; i < childMethod.getNumberOfParams(); i++) {
            
            if (!parentMethod.getParam(i).getType().isAssignableBy(childMethod.getParam(i).getType())) {
                return false;
            }
        }
        ResolvedType childMethodReturn = childMethod.getReturnType();
        ResolvedType parentMethodReturn = parentMethod.getReturnType();
        if (!childMethodReturn.describe().equals(parentMethodReturn.describe())) {
            if (!parentMethodReturn.isAssignableBy(childMethodReturn)) {
                return false;
            }
        }
        return true;
    }
}
