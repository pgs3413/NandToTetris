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

    //src -> [SP] , SP = SP + 1
    private static List<String> push(String src){
        return of("@" + src, "D=M", "@SP", "A=M", "M=D", "@SP", "M=M+1");
    }

    //pop R5, pop R6, R6 = R6 + R5, push R6
    private static List<String> add(){
        return of(pop("R5"), pop("R6"), of("@R5", "D=M", "@R6", "M=D+M"), push("R6"));
    }

    //pop R5, pop R6, R6 = R6 - R5, push R6
    private static List<String> sub(){
        return of(pop("R5"), pop("R6"), of("@R5", "D=M", "@R6", "M=M-D"), push("R6"));
    }

    //pop R5, R5 = -R5, push R5
    private static List<String> neg(){
        return of(pop("R5"), of("@R5", "M=-M"), push("R5"));
    }

    // pop R5, pop R6, R6 = R5 & R6, push R6
    private static List<String> and(){
        return of(pop("R5"), pop("R6"), of("@R5", "D=M", "@R6", "M=M&D"), push("R6"));
    }

    // pop R5, pop R6, R6 = R5 | R6, push R6
    private static List<String> or(){
        return of(pop("R5"), pop("R6"), of("@R5", "D=M", "@R6", "M=M|D"), push("R6"));
    }

    // pop R5, R5 = !R5, push R5
    private static List<String> not(){
        return of(pop("R5"), of("@R5", "M=!M"), push("R5"));
    }

    private static int eq_index = 0;

    //pop R5, pop R6, R7 = if R5 == R6 then 0xFFFF else 0x0000, push R7
    private static List<String> eq(){
        String branch = "eq" + eq_index;
        String end = "eq_end" + eq_index;
        eq_index++;
        return of(pop("R5"), pop("R6"),
                of("@R5", "D=M", "@R6", "D=M-D", "@" + branch, "D;JEQ",
                        "@R7", "M=0", "@" + end, "0;JMP",
                        "(" + branch + ")", "@0", "D=!A", "@R7", "M=D",
                        "(" + end + ")"),
                push("R7")
                );
    }

    private static int gt_index = 0;

    //pop R5, pop R6, R7 = if R6 > R5 then 0xFFFF else 0x0000, push R7
    private static List<String> gt(){
        String branch = "gt" + gt_index;
        String end = "gt_end" + gt_index;
        gt_index++;
        return of(pop("R5"), pop("R6"),
                of("@R5", "D=M", "@R6", "D=M-D", "@" + branch, "D;JGT",
                        "@R7", "M=0", "@" + end, "0;JMP",
                        "(" + branch + ")", "@0", "D=!A", "@R7", "M=D",
                        "(" + end + ")"),
                push("R7")
        );
    }

    private static int lt_index = 0;

    //pop R5, pop R6, R7 = if R6 < R5 then 0xFFFF else 0x0000, push R7
    private static List<String> lt(){
        String branch = "lt" + lt_index;
        String end = "lt_end" + lt_index;
        lt_index++;
        return of(pop("R5"), pop("R6"),
                of("@R5", "D=M", "@R6", "D=M-D", "@" + branch, "D;JLT",
                        "@R7", "M=0", "@" + end, "0;JMP",
                        "(" + branch + ")", "@0", "D=!A", "@R7", "M=D",
                        "(" + end + ")"),
                push("R7")
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
                    //index -> A , A -> D, D -> R5, push R5
                    return of(of("@" + index, "D=A", "@R5", "M=D"), push("R5"));
                default:
                    System.err.println("未知segment： " + segment); System.exit(-1);
            }
        }
        if(commandType == C_POP){

        }
       return null;
    }


}
