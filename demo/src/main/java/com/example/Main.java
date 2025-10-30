package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

class Main {

    /*public static void main(String[] args) {
        ReflectionTypeSolver solver = new ReflectionTypeSolver(false);
        List<String> packages=new ArrayList<>();
        packages.add("java.util.stream.Stream");
        packages.add("java.util.ArrayList");
        packages.add("org.jsoup.nodes.Element");
        
        for(String pack : packages){
            try {
                ResolvedReferenceTypeDeclaration type=solver.solveType(pack);
                System.out.println("名前解決可能 : "+type.getQualifiedName());
            } catch (UnsolvedSymbolException e) {
                System.out.println("名前解決不可 : "+pack);
            }
        }
    }
}*/
    static ParserConfiguration config = new ParserConfiguration();

    public static void main(String[] args) throws IOException {
        Main main = new Main();

        String strRootProject = "C:\\Users\\sugii syuji\\data\\HikariCP-HikariCP-4.0.3";
        Path rootPath = Paths.get(strRootProject);
        String javaProject = strRootProject + "\\src\\main\\java";
        Path analyzePath;

        if (!Files.isDirectory(Paths.get(javaProject))) {
            analyzePath = Paths.get(strRootProject);
        } else {
            analyzePath = Paths.get(javaProject);

        }
        config = Main.setSymbolSolver(analyzePath, rootPath);
        main.getJavaFiles(analyzePath);
    }

    public void getJavaFiles(Path root) throws IOException {                                //rootにあるjavaファイルに対してvisitorを適応する
        int num = 0;
        NameOfClasses visitor = new NameOfClasses();
        List<CompilationUnit> units;
        try (Stream<Path> paths = Files.walk(root)) {
            units = paths.filter(p -> p.toString().endsWith(".java")).map(Main::parse).collect(Collectors.toList());
            for (CompilationUnit unit : units) {
                if (unit.getPackageDeclaration().isPresent()) {
                    num += 1;
                    unit.accept(visitor, unit.getPackageDeclaration().get().toString());
                }
            }
            visitor.excuteMetrics();
            visitor.printCSV();
            System.out.println("クラスの個数 : " + num);

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static CompilationUnit parse(Path path) {                 //各ファイルのcompilationUnitを返す
        try {
            JavaParser parser = new JavaParser(config);
            ParseResult<CompilationUnit> result = parser.parse(path);
            if (result.getResult().isPresent()) {
                CompilationUnit unit = result.getResult().get();
                return unit;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static ParserConfiguration setSymbolSolver(Path analyzePath, Path rootPath) throws IOException {                  //parseの設定
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(new ReflectionTypeSolver(true));                       //標準ライブラリの名前解決
        combinedSolver.add(new ClassLoaderTypeSolver(ParserConfiguration.class.getClassLoader()));
        addJarSourceFile(combinedSolver, rootPath);
        combinedSolver.add(new JavaParserTypeSolver(analyzePath));
        String[] packages = {"com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver", "java.io.IOException", "java.util.stream.Stream", "java.lang.StringBuilder","java.util.Map","org.jsoup.helper.Validate","org.eclipse.jdt.core.dom.ConditionalExpression","java.util.function.Function"};
        for (String name : packages) {
            try {
                System.out.println(combinedSolver.solveType(name).getQualifiedName());
            } catch (UnsolvedSymbolException e) {
                System.out.println("名前解決不可");
            }
        }

        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(new JavaSymbolSolver(combinedSolver));
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        //parserConfiguration.setAttributeComments(false);
        //parserConfiguration.setStoreTokens(false);
        return parserConfiguration;
    }

    private static void addJarSourceFile(CombinedTypeSolver solver, Path path) throws IOException {
        List<Path> JarFile;
        try (Stream<Path> paths = Files.walk(path)) {
            JarFile = paths.filter(p -> p.toString().endsWith(".jar")).toList();
            JarFile.forEach(p -> {
                try {
                    solver.add(new JarTypeSolver(p));
                } catch (IOException ex) {
                }
            });
        }
    }
}
