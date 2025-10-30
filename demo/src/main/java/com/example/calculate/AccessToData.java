package com.example.calculate;

import java.util.ArrayList;
import java.util.List;

import com.example.node.ClassMetrics;
import com.example.node.MethodMetrics;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class AccessToData extends VoidVisitorAdapter<String> implements IAttribute {

    private List<String> ListOfATFD = new ArrayList<>();
    private List<String> ListOfATLD = new ArrayList<>();
    private List<String> ListOfError = new ArrayList<>();
    private List<String> ListOfClassInvoked = new ArrayList<>();
    private List<MethodCallExpr> ListOfForeignMethodInvoked = new ArrayList<>();
    private List<MethodCallExpr> ListOfLocalMethodInvoked = new ArrayList<>();
    private List<FieldAccessExpr> ListOfLocalFieldUsed = new ArrayList<>();
    private String nameOfParentClass;
    private List<String> nameOfSuperClasses;
    private String keyOfMethod;

    @Override
    public String getName() {
        return "ATFD";
    }

    @Override
    public void calculate(ClassMetrics node) {
        float sumOfATFD = 0;
        for (MethodMetrics methodMetrics : node.getMethodsMetrics()) {
            sumOfATFD = sumOfATFD + methodMetrics.getMetric(getName());
        }
    }

    @Override
    public void calculate(MethodMetrics node) {
        ListOfError = new ArrayList<>();
        ListOfATFD = new ArrayList<>();
        ListOfATLD = new ArrayList<>();
        ListOfClassInvoked = new ArrayList<>();
        ListOfForeignMethodInvoked = new ArrayList<>();
        ListOfLocalMethodInvoked = new ArrayList<>();
        ListOfLocalFieldUsed = new ArrayList<>();

        nameOfParentClass = node.getDeclaration().resolve().declaringType().getQualifiedName(); //メソッドが所属するクラスを得る
        //System.out.println("呼び出し元 : "+nameOfParentClass);
        //System.out.println("宣言メソッド : "+node.getDeclaration().getName());
        node.getDeclaration().accept(this, "");
        node.setAttribute("ListOfATFD", ListOfATFD);
        node.setAttribute("ListOfATLD", ListOfATLD);
        node.setAttribute("ListOfError", ListOfError);
    }

    @Override
    public void visit(MethodCallExpr node, String arg) {
        try {
            //System.out.println("呼び出しメソッド名 : "+node.getName()+" , begin : "+node.getBegin());
            ResolvedMethodDeclaration resolve = node.resolve();//おそらくワイルドカードが存在するからエラーが出る．
            if (resolve != null) {
                String nameOfClass = resolve.declaringType().getQualifiedName();
                if (!nameOfClass.equals(nameOfParentClass)) {
                    //System.out.println(getName());
                    //System.out.println("外部クラス : "+nameOfClass);
                    ListOfATFD.add(node.getName() + " " + nameOfClass);
                } else {
                    //System.out.println("ATLD");
                    //System.out.println("内部クラス : "+nameOfClass);
                    ListOfATLD.add(node.getName() + " " + nameOfClass);
                }

            }
        } catch (UnsolvedSymbolException e) {

            ListOfError.add(node.getName() + " unsolvedSymbolException");
        } catch (UnsupportedOperationException e) {
            ListOfError.add(node.getName() + " unsupportedoperation");
        } catch (IllegalStateException e) {
            ListOfError.add(node.getName() + " IllegalStateException");
        } catch (Exception e) {
            ListOfError.add(node.getName() + e.getMessage());
        }
        super.visit(node, arg);

    }

}
