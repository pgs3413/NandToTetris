package code;

import sym.*;
import tree.*;
import utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static tree.Statement.*;
import static tree.Expression.*;
import static sym.Symbol.*;
import static sym.Type.*;

public class Coder extends Visitor {

    Path targetPath;
    Scope rootScope;

    List<String> vms;
    ClassSymbol classSymbol;
    SubroutineSymbol subroutineSymbol;
    Type resultType;
    boolean isFunction;

    public Coder(Path targetPath, Scope rootScope){
        this.targetPath = targetPath;
        this.rootScope = rootScope;
    }

    public void gen(ClassSymbol classSymbol) throws IOException {
        vms = new ArrayList<>();
        this.classSymbol = classSymbol;
        for(SubroutineDecl tree: classSymbol.tree.subroutineDecls){
            tree.accept(this);
        }
        Path targetFile = Paths.get(targetPath.toString(), classSymbol.name + ".vm");
        Files.write(targetFile, vms);
    }

    @Override
    public void visitSubroutineDecl(SubroutineDecl that) {
        subroutineSymbol = that.sym;
        String functionTitle = "function " + classSymbol.name + "." + subroutineSymbol.name + " "
                + (subroutineSymbol.scope.size() - subroutineSymbol.params.size());
        vms.add(functionTitle);
        isFunction = false;
        switch (subroutineSymbol.subroutineType){
            case FUNCTION:
                isFunction = true;
                break;
            case METHOD:
                vms.add("push argument 0");
                vms.add("pop pointer 0");
                break;
        }

        for(Statement tree: that.statements){
            tree.accept(this);
        }
    }

    /** visit statement */

    @Override
    public void visitLetStatement(LetStatement that) {

        that.init.accept(this);

        if(that.varName instanceof Identifier){
            Identifier target = (Identifier) that.varName;
            VarSymbol varSymbol = getVarSymbol(target.name);
            checkType(varSymbol.type, resultType, "let statement");
            vms.add("pop " + varSymbol.varType.segment + " " + varSymbol.index);
        }else {  //TODO ArrayAccess FieldAccess
            exit("let statement target wrong");
        }
    }

    @Override
    public void visitReturnStatement(ReturnStatement that) {
        if(that.value == null){
            checkType(subroutineSymbol.type, voidType, "return statement");
            vms.add("push constant 0");
        }else {
            that.value.accept(this);
            checkType(subroutineSymbol.type, resultType, "return statement");
        }
        vms.add("return");
    }

    /** visit expression */

    @Override
    public void visitIntegerConstant(IntegerConstant that) {
        vms.add("push constant " + that.value.toString());
        resultType = Type.intType;
    }
//
//    @Override
//    public void visitKeyWordConstant(KeyWordConstant that) {
//       switch (that.value){
//           case THIS:
//               if(isFunction) exit("can not use keyword this");
//               vms.add("push pointer 0");
//               resultType = classSymbol.type;
//               break;
//           case TRUE:
//           case FALSE:
//           case NULL: Utils.exit("TODO");
//       }
//    }

    @Override
    public void visitIdentifier(Identifier that) {
        VarSymbol varSymbol = getVarSymbol(that.name);
        vms.add("push " + varSymbol.varType.segment + " " + varSymbol.index);
        resultType = varSymbol.type;
    }

//    @Override
//    public void visitArrayAccess(ArrayAccess that) {
//        VarSymbol varSymbol = getVarSymbol(that.name);
//        that.index.accept(this);
//        if(resultType != Type.intType){
//            exit("array index must be integer");
//        }
//        vms.add("push " + varSymbol.varType.segment + " " + varSymbol.index);
//        vms.add("add");
//        vms.add("pop pointer 1");
//        vms.add("push that 0");
//        resultType = Type.intType;
//    }
//
//    @Override
//    public void visitUnary(Unary that) {
//        that.term.accept(this);
//        switch (that.op){
//            case NEG:
//                if(resultType != Type.intType && resultType != Type.charType){
//                    exit("only char/int type can neg");
//                }
//                break;
//            case NOT:
//                if(resultType != Type.intType
//                        && resultType != Type.charType
//                        && resultType != Type.boolType){
//                    exit("only char/int/bool type can not");
//                }
//                break;
//            default: exit("unsupported err");
//        }
//        vms.add(that.op.cmd);
//    }
//
//    @Override
//    public void visitBinary(Binary that) {
//        that.term1.accept(this);
//        Type type1 = resultType;
//        that.term2.accept(this);
//        Type type2 = resultType;
//        switch (that.op){
//            case STAR: case SLASH: case PLUS:case SUB:case LT:case GT:
//                if(type1 != Type.intType && type1 != Type.charType ||
//                    type2 != Type.intType && type2 != Type.charType){
//                    exit("only int/char can " + that.op.name);
//                }
//                if(that.op == Op.LT || that.op == Op.GT){
//                    resultType = Type.boolType;
//                }else {
//                    resultType = Type.intType;
//                }
//                break;
//
//            case AND:case OR:
//                if(type1 == Type.intType || type1 == Type.charType){
//                    if(type2 != Type.intType && type2 != Type.charType){
//                        exit("type1 is int/char type2 must be int/char");
//                    }
//                    resultType = Type.intType;
//                }else if(type1 == Type.boolType){
//                    if(type2 != Type.boolType){
//                        exit("type1 is bool type2 must be bool");
//                    }
//                    resultType = Type.boolType;
//                }else {
//                    exit("only int/char/bool can and/or");
//                }
//                break;
//            case EQ:
//                if(type1 == Type.intType || type1 == Type.charType){
//                    if(type2 != Type.intType && type2 != Type.charType){
//                        exit("type1 is int/char type2 must be int/char when eq");
//                    }
//                }else if(type1 == Type.boolType){
//                    if(type2 != Type.boolType){
//                        exit("type1 is bool type2 must be bool when eq");
//                    }
//                }else if(type1.typeKind == TypeKind.CLASS){
//                    if(type1 != type2){
//                        exit("must be same class type when eq");
//                    }
//                } else {
//                    exit("only int/char/bool/class can eq");
//                }
//                resultType = Type.boolType;
//                break;
//            default:
//                exit("unsupported err");
//        }
//        if(that.op == Op.STAR || that.op == Op.SLASH){
//            vms.add("call " + that.op.cmd + " 2");
//        }else {
//            vms.add(that.op.cmd);
//        }
//    }

    /** others */

    private VarSymbol getVarSymbol(String name){
        VarSymbol varSymbol = (VarSymbol) subroutineSymbol.scope.get(name, () -> {});
        if(varSymbol == null){
            Symbol nextSymbol = classSymbol.scope.get(name, () -> {});
            if(nextSymbol instanceof VarSymbol &&
                    (!isFunction || ((VarSymbol) nextSymbol).varType == VarType.STATIC)){
               varSymbol = (VarSymbol) nextSymbol;
            }
        }
        if(varSymbol == null){
            exit("undefined identifier " + name);
        }
        return varSymbol;
    }


    private void checkType(Type target, Type source, String msg){
        if(target != source) exit(msg + " type not match");
    }

    private void exit(String msg){
        Utils.exit(classSymbol.tree.fileName +": subroutine " + subroutineSymbol.name + ": " + msg);
    }
}
