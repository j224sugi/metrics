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
        float sumOfATFD=0;
        for(MethodMetrics methodMetrics : node.getMethodsMetrics()){
            sumOfATFD=sumOfATFD+methodMetrics.getMetric(getName());
        } 
    }

    @Override
    public void calculate(MethodMetrics node) {
        ListOfATFD=new ArrayList<>();
        ListOfATLD=new ArrayList<>();
        ListOfClassInvoked=new ArrayList<>();
        ListOfForeignMethodInvoked=new ArrayList<>();
        ListOfLocalMethodInvoked=new ArrayList<>();
        ListOfLocalFieldUsed=new ArrayList<>();

        nameOfParentClass=node.getDeclaration().resolve().declaringType().getQualifiedName(); //メソッドが所属するクラスを得る
        System.out.println("呼び出し元 : "+nameOfParentClass);
        System.out.println("宣言メソッド : "+node.getDeclaration().getName());
        node.getDeclaration().accept(this,"");
    }

    @Override
    public void visit(MethodCallExpr node,String arg){
        try {
            ResolvedMethodDeclaration resolve=node.resolve();
            if(resolve!=null){
                String nameOfClass = resolve.declaringType().getQualifiedName();
                String nameOfMethod=resolve.getQualifiedName();
                if(!nameOfClass.equals(nameOfParentClass)){
                    System.out.println(getName());
                    System.out.println("外部クラス : "+nameOfClass);
                    System.out.println("メソッド名 : "+nameOfMethod);
                }else{
                    System.out.println("ATLD");
                    System.out.println("内部クラス : "+nameOfClass);
                    System.err.println("メソッド名 : "+nameOfMethod);
                }

            }
        } catch (UnsolvedSymbolException e) {
            System.out.println("名前解決不可 : "+e.getName());
        } catch(UnsupportedOperationException e){
            System.out.println("ワイルドカード含み : "+e.getMessage());
        }
    }
}
