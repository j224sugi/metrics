package com.example.calculate;

import java.util.ArrayList;
import java.util.List;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;

public class NumProtMembersInParent implements IAttribute {

    List<String> nameOfClasses;
    List<ResolvedMethodDeclaration> ParentMethods;
    List<ResolvedFieldDeclaration> ParentFields;
    List<ResolvedMethodDeclaration> ParentProtMethods;
    List<ResolvedFieldDeclaration> ParentProtFields;

    public NumProtMembersInParent(List<String> nameOfClasses) {
        this.nameOfClasses = nameOfClasses;
        this.ParentMethods = new ArrayList<>();
        this.ParentFields = new ArrayList<>();
        this.ParentProtMethods = new ArrayList<>();
        this.ParentProtFields = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "NProtM";
    }

    @Override
    public void calculate(MethodMetrics node) {
    }

    @Override
    public void calculate(ClassMetrics node) {
        int protMembers = 0;
        ParentMethods.clear();
        ParentFields.clear();
        ParentProtMethods.clear();
        ParentProtFields.clear();
        List<ResolvedReferenceType> superClasses = node.getDeclaration().resolve().getAllAncestors();
        if (!superClasses.isEmpty()) {
            for (ResolvedReferenceType superClass : superClasses) {
                if (nameOfClasses.contains(superClass.getQualifiedName())) {
                    ListProtMethods(superClass);
                    ListProtFields(superClass);
                }
            }
            protMembers = ParentProtMethods.size() + ParentProtFields.size();
        }
        node.setAttribute(getName(), protMembers);
        node.setAttribute("ancestorsMethods", ParentMethods);
        node.setAttribute("ancestorsFields", ParentFields);
    }

    public void ListProtFields(ResolvedReferenceType ancestor) {
        if (ancestor.getDeclaredFields() != null) {
            for (ResolvedFieldDeclaration field : ancestor.getDeclaredFields()) {
                if (!ParentFields.contains(field)) {
                    ParentFields.add(field);
                    if (field.accessSpecifier() == AccessSpecifier.PROTECTED) {
                        ParentProtFields.add(field);
                    }
                }
            }
        }
    }

    public void ListProtMethods(ResolvedReferenceType ancestor) {
        if (ancestor.getDeclaredMethods() != null) {
            for (MethodUsage method : ancestor.getDeclaredMethods()) {
                ResolvedMethodDeclaration resolveMethod = method.getDeclaration();
                if (!isOverriden(resolveMethod, ParentMethods)) {
                    ParentMethods.add(resolveMethod);
                    if (resolveMethod.accessSpecifier() == AccessSpecifier.PROTECTED) {
                        ParentProtMethods.add(resolveMethod);
                    }
                }
            }
        }
    }

    public boolean isOverriden(ResolvedMethodDeclaration newMethod, List<ResolvedMethodDeclaration> lowerLayerMethods) {
        for (ResolvedMethodDeclaration method : lowerLayerMethods) {
            if (isOverriden(newMethod, method)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOverriden(ResolvedMethodDeclaration ancestorMethod, ResolvedMethodDeclaration lowLayerMethod) {
        if (!ancestorMethod.getName().equals(lowLayerMethod.getName())) {
            return false;
        }
        if (ancestorMethod.getNumberOfParams() != lowLayerMethod.getNumberOfParams()) {
            return false;
        }
        for (int i = 0; i < ancestorMethod.getNumberOfParams(); i++) {
            if (!ancestorMethod.getParam(i).getType().describe().equals(lowLayerMethod.getParam(i).getType().describe())) {
                return false;
            }
        }
        ResolvedType ancestorMethodReturn = ancestorMethod.getReturnType();
        ResolvedType lowLayerMethodReturn = lowLayerMethod.getReturnType();
        if (!ancestorMethodReturn.describe().equals(lowLayerMethodReturn.describe())) {
            if (!ancestorMethodReturn.isAssignableBy(lowLayerMethodReturn)) {
                return false;
            }
        }
        return true;
    }
}
