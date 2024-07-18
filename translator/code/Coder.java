package code;

import parse.CommandType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static code.ALCommand.*;
import static parse.CommandType.*;
import static code.Segment.*;

/**
 * @Author: pangs
 * @Date: 2024/7/12
 * @description: coder
 */
public class Coder {

    public static int STATIC_CNT = 0;

    @SafeVarargs
    private static List<String> of(List<String>... lists){
        List<String> result = new ArrayList<>();
        for(List<String> list : lists) result.addAll(list);
        return result;
    }

    private static List<String> of(String... strings){
        return Arrays.asList(strings);
    }


    //SP = SP - 1, [SP] -> dest
    private static List<String> pop(String dest){
        return of("@SP", "M=M-1", "A=M", "D=M", "@" + dest, "M=D");
    }

    private static List<String> popD(){
        return of("@SP", "M=M-1", "A=M", "D=M");
    }

    //src -> [SP] , SP = SP + 1
    private static List<String> push(String src){
        return of("@" + src, "D=M", "@SP", "A=M", "M=D", "@SP", "M=M+1");
    }

    private static List<String> pushD(){
        return of("@SP", "A=M", "M=D", "@SP", "M=M+1");
    }

    //pop R13, pop R14, D = R14 + R13, push D
    private static List<String> add(){
        return of(pop("R13"), pop("R14"), of("@R13", "D=M", "@R14", "D=D+M"), pushD());
    }

    //pop R13, pop R14, D = R14 - R13, push D
    private static List<String> sub(){
        return of(pop("R13"), pop("R14"), of("@R13", "D=M", "@R14", "D=M-D"), pushD());
    }

    //pop R13, D = -R13, push D
    private static List<String> neg(){
        return of(pop("R13"), of("@R13", "D=-M"), pushD());
    }

    // pop R13, pop R14, D = R13 & R14, push D
    private static List<String> and(){
        return of(pop("R13"), pop("R14"), of("@R13", "D=M", "@R14", "D=M&D"), pushD());
    }

    // pop R13, pop R14, D = R13 | R14, push D
    private static List<String> or(){
        return of(pop("R13"), pop("R14"), of("@R13", "D=M", "@R14", "D=M|D"), pushD());
    }

    // pop R13, D = !R13, push D
    private static List<String> not(){
        return of(pop("R13"), of("@R13", "D=!M"), pushD());
    }

    private static int eq_index = 0;

    //pop R13, pop R14, D = if R13 == R14 then 0xFFFF else 0x0000, push D
    private static List<String> eq(){
        String branch = "eq" + eq_index;
        String end = "eq_end" + eq_index;
        eq_index++;
        return of(pop("R13"), pop("R14"),
                of("@R13", "D=M", "@R14", "D=M-D", "@" + branch, "D;JEQ",
                        "D=0", "@" + end, "0;JMP",
                        "(" + branch + ")", "@0", "D=!A",
                        "(" + end + ")"),
                pushD()
                );
    }

    private static int gt_index = 0;

    //pop R13, pop R14, D = if R14 > R13 then 0xFFFF else 0x0000, push D
    private static List<String> gt(){
        String branch = "gt" + gt_index;
        String end = "gt_end" + gt_index;
        gt_index++;
        return of(pop("R13"), pop("R14"),
                of("@R13", "D=M", "@R14", "D=M-D", "@" + branch, "D;JGT",
                        "D=0", "@" + end, "0;JMP",
                        "(" + branch + ")", "@0", "D=!A",
                        "(" + end + ")"),
                pushD()
        );
    }

    private static int lt_index = 0;

    //pop R13, pop R14, D = if R14 < R13 then 0xFFFF else 0x0000, push D
    private static List<String> lt(){
        String branch = "lt" + lt_index;
        String end = "lt_end" + lt_index;
        lt_index++;
        return of(pop("R13"), pop("R14"),
                of("@R13", "D=M", "@R14", "D=M-D", "@" + branch, "D;JLT",
                        "D=0", "@" + end, "0;JMP",
                        "(" + branch + ")", "@0", "D=!A",
                        "(" + end + ")"),
                pushD()
        );
    }

    public static List<String> arithmetic(String command){
        switch (command){
            case ADD: return add();
            case SUB: return sub();
            case NEG: return neg();
            case EQ: return eq();
            case GT: return gt();
            case LT: return lt();
            case AND: return and();
            case OR: return or();
            case NOT: return not();
            default: System.err.println("未知命令： " + command); System.exit(-1);
        }
        return null;
    }

    public static List<String> pushPop(CommandType commandType, String segment, int index){
        if(commandType == C_PUSH){
            switch (segment){
                case CONSTANT:
                    //index -> A , A -> D, push D
                    return of(of("@" + index, "D=A"), pushD());
                case LOCAL:
                    //[LCL] + index -> A, M -> D, push D
                    return of(of("@" + index, "D=A", "@LCL", "A=M+D", "D=M"), pushD());
                case ARGUMENT:
                    //[ARG] + index -> A, M -> D, push D
                    return of(of("@" + index, "D=A", "@ARG", "A=M+D", "D=M"), pushD());
                case THIS:
                    //[THIS] + index -> A, M -> D, push D
                    return of(of("@" + index, "D=A", "@THIS", "A=M+D", "D=M"), pushD());
                case THAT:
                    //[THAT] + index -> A, M -> D, push D
                    return of(of("@" + index, "D=A", "@THAT", "A=M+D", "D=M"), pushD());
                case POINTER:
                    //[3 + index] -> D, push D
                    return of(of("@" + (3 + index), "D=M"), pushD());
                case TEMP:
                    //[5 + index] -> D, push D
                    return of(of("@" + (5 + index), "D=M"), pushD());
                case STATIC:
                    return of(of("@static" + STATIC_CNT + "." + index, "D=M"), pushD());
                default:
                    System.err.println("未知segment： " + segment); System.exit(-1);
            }
        }
        if(commandType == C_POP){
            switch (segment){
                case LOCAL:
                    // pop R13 , R13 -> [LCL] + index
                    return of(pop("R13"), of("@" + index, "D=A", "@LCL", "D=M+D", "@R14", "M=D", "@R13", "D=M", "@R14", "A=M", "M=D"));
                case ARGUMENT:
                    return of(pop("R13"), of("@" + index, "D=A", "@ARG", "D=M+D", "@R14", "M=D", "@R13", "D=M", "@R14", "A=M", "M=D"));
                case THIS:
                    return of(pop("R13"), of("@" + index, "D=A", "@THIS", "D=M+D", "@R14", "M=D", "@R13", "D=M", "@R14", "A=M", "M=D"));
                case THAT:
                    return of(pop("R13"), of("@" + index, "D=A", "@THAT", "D=M+D", "@R14", "M=D", "@R13", "D=M", "@R14", "A=M", "M=D"));
                case POINTER:
                    // pop D, D -> [3 + index]
                    return of(popD(), of("@" + (3 + index), "M=D"));
                case TEMP:
                    return of(popD(), of("@" + (5 + index), "M=D"));
                case STATIC:
                    return of(popD(), of("@static" + STATIC_CNT + "." + index, "M=D"));
                default:
                    System.err.println("未知segment： " + segment); System.exit(-1);
            }
        }
       return null;
    }

    public static List<String> label(String labelName, String functionName){
        return of("(" + functionName + "$" + labelName + ")");
    }

    public static List<String> _goto(String labelName, String functionName){
        return of("@" + functionName + "$" + labelName, "0;JMP");
    }

    public static List<String> if_goto(String labelName, String functionName){
        return of(popD(), of("@" + functionName + "$" + labelName, "D;JNE"));
    }



}
