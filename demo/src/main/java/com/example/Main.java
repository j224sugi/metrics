package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

class Main{
    static ParserConfiguration config=new ParserConfiguration();
    public static void main(String[] args) throws IOException {
        Main main=new Main();

        String strRootProject="C:\\Users\\syuuj\\jsoup\\src\\main\\java";
        Path rootProject=Paths.get(strRootProject);        
        config=Main.setSymbolSolver(rootProject);

        main.getJavaFiles(rootProject);
    }

    public void getJavaFiles(Path root) throws  IOException{                                //rootにあるjavaファイルに対してvisitorを適応する
        int num=0;
        List<CompilationUnit> units=new ArrayList<>();
        try (Stream<Path> paths=Files.walk(root)){
            units=paths.filter(p->p.toString().endsWith(".java")).map(Main::parse).collect(Collectors.toList());
            for(CompilationUnit unit:units){
                if(unit.getPackageDeclaration().isPresent()){
                    num+=1;
                    unit.accept(new MyVoidVisitor(),unit.getPackageDeclaration().get().toString());        
                }
            }
            System.out.println("クラスの個数 : "+num);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static CompilationUnit parse(Path path){                 //各ファイルのcompilationUnitを返す
        try{
            JavaParser parser=new JavaParser(config);
            ParseResult<CompilationUnit> result=parser.parse(path);
            if(result.getResult().isPresent()){
                CompilationUnit unit=result.getResult().get();
                return unit;
            }else{
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static ParserConfiguration setSymbolSolver(Path path) throws IOException{                  //parseの設定
        CombinedTypeSolver combinedSolver=new CombinedTypeSolver();
        combinedSolver.add(new ReflectionTypeSolver());                                                //標準ライブラリの名前解決
        //combinedSolver.add(new JarTypeSolver(path));
        combinedSolver.add(new JavaParserTypeSolver(path));                                            // ソースコード内の全ファイルにおける依存関係

        ParserConfiguration parserConfiguration=new ParserConfiguration();
        parserConfiguration.setSymbolResolver(new JavaSymbolSolver(combinedSolver));
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        parserConfiguration.setAttributeComments(true);
        parserConfiguration.setStoreTokens(true);
        return parserConfiguration;
    }

}