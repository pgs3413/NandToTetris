package main;

import parse.Parser;
import sym.Enter;
import sym.Scope;

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

    public Compiler(List<Path> jackFiles){
        this.jackFiles = jackFiles;
    }

    public void compile() throws IOException {

        Scope rootScope = new Scope(null);

        //符号收集第一阶段
        Enter enter = new Enter(rootScope);
        for(Path file: jackFiles){
            Parser parser = new Parser(file);
            enter.classEnter(parser.parse());
        }

        //符号收集第二阶段


    }

}
