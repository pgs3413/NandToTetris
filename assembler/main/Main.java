package main;

import static parse.CommandType.*;

import code.Coder;
import parse.CommandType;
import parse.Parser;
import symbol.SymbolTable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

        Path sourceFilePath = Paths.get(args[0]);

        String targetFileName;
        if(args.length >= 2){
            targetFileName = args[1];
        }else {
            targetFileName = sourceFilePath.getParent().toString() + "/" + sourceFilePath.getFileName().toString().split("\\.")[0] + ".hack";
        }

        Path targetFilePath = Paths.get(targetFileName);

        BufferedWriter targetWriter = Files.newBufferedWriter(targetFilePath);

        Parser parser = new Parser(sourceFilePath);
        SymbolTable symbolTable = new SymbolTable();

        //第一遍遍历
        int number = 0;
        while (parser.hasMoreCommands()){
            parser.advance();
            CommandType commandType = parser.commandType();
            if(commandType == A_COMMAND || commandType == C_COMMAND){
                number++;
            }else if(commandType == L_COMMAND){
                String symbol = parser.symbol();
                if('0' <= symbol.charAt(0) && symbol.charAt(0) <= '9') {
                    System.err.println("符号不能以数字开头：" + symbol);
                    System.exit(-1);
                }
                if(symbolTable.contains(symbol)){
                    System.err.println("L-命令符号不能重复：" + symbol);
                    System.exit(-1);
                }
                symbolTable.addEntry(symbol, number);
            }else {
                System.err.println("未知类型");
                System.exit(-1);
            }
        }

        //第二遍遍历
        parser.restart();
        int startAddress = 16;
        while (parser.hasMoreCommands()){
            String command = null;
            parser.advance();
            CommandType commandType = parser.commandType();
            if(commandType == A_COMMAND){
                String symbol = parser.symbol();
                if('0' == symbol.charAt(0) && !"0".equals(symbol)){
                    System.err.println("符号或数字不能以0开头：" + symbol);
                    System.exit(-1);
                }
                if('0' <= symbol.charAt(0) && symbol.charAt(0) <= '9'){
                    int num = 0;
                    try{
                        num = Integer.parseUnsignedInt(symbol, 10);
                    }catch (Exception e){
                        System.err.println("非法的数字：" + symbol);
                        System.exit(-1);
                    }
                    command = "0" + symbolTable.convert(num);
                }else {
                    if(!symbolTable.contains(symbol)){
                        symbolTable.addEntry(symbol, startAddress++);
                    }
                    command = "0" + symbolTable.getAddress(symbol);
                }
            }else if (commandType == C_COMMAND){
                command = "111" + Coder.comp(parser.comp()) + Coder.dest(parser.destA(), parser.destD(), parser.destM()) + Coder.jump(parser.jump());
            }else{
                continue;
            }
            targetWriter.append(command);
            targetWriter.newLine();
        }

        targetWriter.close();
        System.out.println("汇编成功！");

    }

}
