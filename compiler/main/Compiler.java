package main;

import code.Coder;
import parse.Parser;
import sym.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: compiler
 */
public class Compiler {

    List<Path> jackFiles;
    Path targetPath;

    public Compiler(List<Path> jackFiles, Path targetPath){
        this.jackFiles = jackFiles;
        this.targetPath = targetPath;
    }

    public void compile() throws IOException {

        Scope rootScope = new Scope(null, null);

        //符号收集第一阶段
        Enter enter = new Enter(rootScope);
        for(Path file: jackFiles){
            Parser parser = new Parser(file);
            enter.classEnter(parser.parse());
        }

        //符号收集第二阶段
        MemberEnter memberEnter = new MemberEnter(rootScope);
        for(Symbol symbol: rootScope.table.values()){
            memberEnter.memberEnter((ClassSymbol) symbol);
        }

        //VM code generate
        Coder coder = new Coder(targetPath, rootScope);
        for(Symbol symbol: rootScope.table.values()){
            coder.gen((ClassSymbol) symbol);
        }

    }

}
