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

public class Coder extends Visitor {

    Path targetPath;
    Scope rootScope;
    ClassSymbol classSymbol;
    SubroutineSymbol symbol;
    List<String> vms;
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
        Path targetFile = Paths.get(targetPath.toString(), classSymbol.name + ".vmt");
        Files.write(targetFile, vms);
    }

    @Override
    public void visitSubroutineDecl(SubroutineDecl that) {
        symbol = that.sym;
        String functionTitle = "function " + classSymbol.name + "." + symbol.name + " "
                + (symbol.scope.table.size() - symbol.params.size());
        vms.add(functionTitle);
        isFunction = false;
        switch (symbol.subroutineType){
            case FUNCTION:
                isFunction = true;
                break;
            case METHOD:
                vms.add("push argument 0");
                vms.add("pop pointer 0");
                break;
            case CONSTRUCTOR:
                long fieldCnt = classSymbol.scope.table.values().stream().
                        filter(sym -> sym instanceof VarSymbol && ((VarSymbol)sym).varType == VarType.FIELD).count();
                vms.add("push constant " + fieldCnt);
                vms.add("call Memory.alloc 1");
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
        VarSymbol varSymbol = getVarSymbol(that.varName.name);
        if(resultType.typeKind == TypeKind.CLASS && varSymbol.type.typeKind == TypeKind.CLASS
                && varSymbol.type != resultType){
            exit("let statement type not match " + that.varName.name);
        }
        String segment = varSymbol.varType.segment;
        if(that.varName instanceof ArrayAccess){
            ArrayAccess aa = (ArrayAccess) that.varName;
            aa.index.accept(this);
            if(resultType != Type.intType){
                exit("array index must be integer");
            }
            vms.add("push " + segment + " " + varSymbol.index);
            vms.add("add");
            vms.add("pop pointer 1");
            vms.add("pop that 0");
        }else {
            vms.add("pop " + segment + " " + varSymbol.index);
        }
    }

    @Override
    public void visitReturnStatement(ReturnStatement that) {
        if(that.value == null){
            if(symbol.returnType != Type.voidType){
                exit("return type not match");
            }
            vms.add("push constant 0");
        }else {
            that.value.accept(this);
            if(resultType.typeKind == TypeKind.CLASS && symbol.returnType.typeKind == TypeKind.CLASS
                    && symbol.returnType != resultType){
                exit("return type not match");
            }
        }
        vms.add("return");
    }


    /** visit expression */

    @Override
    public void visitIntegerConstant(IntegerConstant that) {
        vms.add("push constant " + that.value.toString());
        resultType = Type.intType;
    }

    @Override
    public void visitKeyWordConstant(KeyWordConstant that) {
       switch (that.value){
           case THIS:
               if(isFunction) exit("can not use keyword this");
               vms.add("push pointer 0");
               resultType = classSymbol.type;
               break;
           case TRUE:
           case FALSE:
           case NULL: Utils.exit("TODO");
       }
    }

    @Override
    public void visitIdentifier(Identifier that) {
        VarSymbol varSymbol = getVarSymbol(that.name);
        vms.add("push " + varSymbol.varType.segment + " " + varSymbol.index);
        resultType = varSymbol.type;
    }

    @Override
    public void visitArrayAccess(ArrayAccess that) {
        VarSymbol varSymbol = getVarSymbol(that.name);
        that.index.accept(this);
        if(resultType != Type.intType){
            exit("array index must be integer");
        }
        vms.add("push " + varSymbol.varType.segment + " " + varSymbol.index);
        vms.add("add");
        vms.add("pop pointer 1");
        vms.add("push that 0");
        resultType = Type.intType;
    }

    @Override
    public void visitUnary(Unary that) {
        that.term.accept(this);
        switch (that.op){
            case NEG:
                if(resultType != Type.intType && resultType != Type.charType){
                    exit("only char/int type can neg");
                }
                break;
            case NOT:
                if(resultType != Type.intType
                        && resultType != Type.charType
                        && resultType != Type.boolType){
                    exit("only char/int/bool type can not");
                }
                break;
            default: exit("unsupported err");
        }
        vms.add(that.op.cmd);
    }

    @Override
    public void visitBinary(Binary that) {
        that.term1.accept(this);
        Type type1 = resultType;
        that.term2.accept(this);
        Type type2 = resultType;
        switch (that.op){
            case STAR: case SLASH: case PLUS:case SUB:case LT:case GT:
                if(type1 != Type.intType && type1 != Type.charType ||
                    type2 != Type.intType && type2 != Type.charType){
                    exit("only int/char can " + that.op.name);
                }
                if(that.op == Op.LT || that.op == Op.GT){
                    resultType = Type.boolType;
                }else {
                    resultType = Type.intType;
                }
                break;

            case AND:case OR:
                if(type1 == Type.intType || type1 == Type.charType){
                    if(type2 != Type.intType && type2 != Type.charType){
                        exit("type1 is int/char type2 must be int/char");
                    }
                    resultType = Type.intType;
                }else if(type1 == Type.boolType){
                    if(type2 != Type.boolType){
                        exit("type1 is bool type2 must be bool");
                    }
                    resultType = Type.boolType;
                }else {
                    exit("only int/char/bool can and/or");
                }
                break;
            case EQ:
                if(type1 == Type.intType || type1 == Type.charType){
                    if(type2 != Type.intType && type2 != Type.charType){
                        exit("type1 is int/char type2 must be int/char when eq");
                    }
                }else if(type1 == Type.boolType){
                    if(type2 != Type.boolType){
                        exit("type1 is bool type2 must be bool when eq");
                    }
                }else if(type1.typeKind == TypeKind.CLASS){
                    if(type1 != type2){
                        exit("must be same class type when eq");
                    }
                } else {
                    exit("only int/char/bool/class can eq");
                }
                resultType = Type.boolType;
                break;
            default:
                exit("unsupported err");
        }
        if(that.op == Op.STAR || that.op == Op.SLASH){
            vms.add("call " + that.op.cmd + " 2");
        }else {
            vms.add(that.op.cmd);
        }
    }

    /** others */

    private VarSymbol getVarSymbol(String name){
        VarSymbol varSymbol = (VarSymbol) symbol.scope.table.get(name);
        if(varSymbol == null){
            Symbol nextSymbol = classSymbol.scope.table.get(name);
            if(nextSymbol instanceof VarSymbol){
                VarSymbol nextVarSymbol = (VarSymbol) nextSymbol;
                if(nextVarSymbol.varType == VarType.STATIC || !isFunction){
                    varSymbol = nextVarSymbol;
                }
            }
        }
        if(varSymbol == null){
            exit("undefined identifier " + name);
        }
        return varSymbol;
    }

    private void checkCast(Type target, Type source, String msg){
//        if(target == Type.intType){
//            if(source == Type.)
//        }
    }

    private void exit(String msg){
        Utils.exit(classSymbol.tree.fileName +": subroutine " + symbol.name + ": " + msg);
    }
}
