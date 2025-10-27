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
        List<ResolvedReferenceType> superClasses = node.getDeclaration().resolve().getAllAncestors();
        if (!superClasses.isEmpty()) {
            for (ResolvedReferenceType superClass : superClasses) {
                if (nameOfClasses.contains(superClass.getQualifiedName())) {
                    ListProtMethods(superClass);
                    ListProtFields(superClass);
                }
            }
            protMembers=ParentProtMethods.size()+ParentProtFields.size();
        }
        node.setAttribute(getName(), protMembers);
    }

    public void ListProtMethods(ResolvedReferenceType ancestor) {
        if (ancestor.getDeclaredMethods() != null) {
            for (MethodUsage method : ancestor.getDeclaredMethods()) {
                ResolvedMethodDeclaration resolveMethod = method.getDeclaration();
                if (!isOverride(resolveMethod, ParentMethods)) {
                    ParentMethods.add(resolveMethod);
                    if (resolveMethod.accessSpecifier() == AccessSpecifier.PROTECTED) {
                        ParentProtMethods.add(resolveMethod);
                    }
                }
            }
        }
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
