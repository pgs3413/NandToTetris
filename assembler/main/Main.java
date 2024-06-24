package main;

import static parse.CommandType.*;

import parse.CommandType;
import parse.Parser;

import java.io.IOException;

/**
 * @Author: pangs
 * @Date: 2024/6/24
 * @description: Assembler Main
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length < 1){
            System.err.println("请输入源文件路径");
            System.exit(-1);
        }
        Parser parser = new Parser(args[0]);
        while (parser.hasMoreCommands()){
            parser.advance();
            CommandType commandType = parser.commandType();
            if(commandType == A_COMMAND){
                System.out.println("A命令,符号为:" + parser.symbol());
            }else if(commandType == L_COMMAND){
                System.out.println("L命令,符号为:" + parser.symbol());
            }else if(commandType == C_COMMAND){
                String dest = "";
                dest += parser.destA() ? "A" : "";
                dest += parser.destM() ? "M" : "";
                dest += parser.destD() ? "D" : "";
                System.out.println("C命令,dest:" + dest + " comp:" + parser.comp() + " jump:" + parser.jump());
            }else {
                System.err.println("未知类型");
                System.exit(-1);
            }
        }
    }
}
