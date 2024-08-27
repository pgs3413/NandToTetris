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

    int if_index;
    int while_index;

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
        if_index = 0;
        while_index = 0;
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
            VarSymbol varSymbol = getVarSymbol(target.name, true);
            checkType(varSymbol.type, resultType, "let statement");
            vms.add("pop " + varSymbol.varType.segment + " " + varSymbol.index);
        } else if(that.varName instanceof ArrayAccess) {
            Type initType = resultType;
            ArrayAccess target = (ArrayAccess) that.varName;
            target.index.accept(this);
            checkType(intType, resultType, "array index must be int type");
            target.arrayName.accept(this);
            if(resultType.typeKind != TypeKind.Array) exit("only array type can use []");
            Type itemType = ((ArrayType)resultType).itemType;
            checkType(itemType, initType, "let statement");
            vms.add("add");
            vms.add("pop pointer 1");
            vms.add("pop that 0");
        } else if(that.varName instanceof FieldAccess){
            FieldAccess target = (FieldAccess) that.varName;
            Type initType = resultType;
            target.own.accept(this);
            if(resultType.typeKind != TypeKind.CLASS) exit("only class type has field member");
            VarSymbol fieldSymbol = getVarSymbolFromClass(target.name, ((ClassType) resultType).symbol);
            Type targetType = fieldSymbol.type;
            checkType(targetType, initType, "let statement");
            vms.add("pop pointer 1");
            vms.add("pop that " + fieldSymbol.index);
        } else {
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

    @Override
    public void visitDoStatement(DoStatement that) {
        that.subroutineCall.accept(this);
        vms.add("pop temp 1");
    }

    @Override
    public void visitIfStatement(IfStatement that) {
        String if_true = "IF_TRUE_" + if_index;
        String if_false = "IF_FALSE_" + if_index;
        String if_end = "IF_END_" + if_index;
        if_index++;
        that.condition.accept(this);
        checkType(boolType, resultType, "if condition is not a bool");
        vms.add("if-goto " + if_true);
        vms.add("goto " + if_false);
        vms.add("label " + if_true);
        for(Statement tree: that.thenPart) tree.accept(this);
        vms.add("goto " + if_end);
        vms.add("label " + if_false);
        for (Statement tree: that.elsePart) tree.accept(this);
        vms.add("label " + if_end);
    }

    @Override
    public void visitWhileStatement(WhileStatement that) {
        String while_conn = "WHILE_CONN_" + while_index;
        String while_body = "WHILE_BODY_" + while_index;
        String while_end = "WHILE_END_" + while_index;
        while_index++;
        vms.add("label " + while_conn);
        that.condition.accept(this);
        checkType(boolType, resultType, "while condition is not a bool");
        vms.add("if-goto " + while_body);
        vms.add("goto " + while_end);
        vms.add("label " + while_body);
        for(Statement tree: that.body) tree.accept(this);
        vms.add("goto " + while_conn);
        vms.add("label " + while_end);
    }

    /** visit expression */

    @Override
    public void visitIntegerConstant(IntegerConstant that) {
        vms.add("push constant " + that.value.toString());
        resultType = intType;
    }

    @Override
    public void visitKeyWordConstant(KeyWordConstant that) {
       switch (that.value){
           case TRUE:
               vms.add("push constant 0");
               vms.add("not");
               resultType = boolType;
               break;
           case FALSE:
               vms.add("push constant 0");
               resultType = boolType;
               break;
           case THIS:
               if(isFunction) exit("can not use this in function");
               vms.add("push pointer 0");
               resultType = classSymbol.type;
               break;
           case NULL: Utils.exit("TODO"); // TODO
       }
    }

    @Override
    public void visitIdentifier(Identifier that) {
        VarSymbol varSymbol = getVarSymbol(that.name, true);
        vms.add("push " + varSymbol.varType.segment + " " + varSymbol.index);
        resultType = varSymbol.type;
    }

    @Override
    public void visitArrayAccess(ArrayAccess that) {
        that.index.accept(this);
        checkType(intType, resultType, "array index must be int type");
        that.arrayName.accept(this);
        if(resultType.typeKind != TypeKind.Array) exit("only array type can use []");
        resultType = ((ArrayType)resultType).itemType;
        vms.add("add");
        vms.add("pop pointer 1");
        vms.add("push that 0");
    }

    @Override
    public void visitFieldAccess(FieldAccess that) {
        that.own.accept(this);
        if(resultType.typeKind != TypeKind.CLASS) exit("only class type has field member");
        VarSymbol fieldSymbol = getVarSymbolFromClass(that.name, ((ClassType) resultType).symbol);
        vms.add("pop pointer 1");
        vms.add("push that " + fieldSymbol.index);
        resultType = fieldSymbol.type;
    }

    @Override
    public void visitUnary(Unary that) {
        that.term.accept(this);
        switch (that.op){
            case NEG: case NOT:
                checkType(intType, resultType, "neg/not only support int type");
                resultType = intType;
                break;
            case BOOLNOT:
                checkType(boolType, resultType, "bool not only support boolean type");
                resultType = boolType;
                break;
            default: exit("unsupported unary op");
        }
        vms.add(that.op.cmd);
    }

    @Override
    public void visitBinary(Binary that) {
        that.expr1.accept(this);
        Type type1 = resultType;
        that.expr2.accept(this);
        Type type2 = resultType;
        switch (that.op){
            case PLUS: case SUB: case AND: case OR:
                checkType(intType, type1, type2, that.op.name + " must int op int");
                resultType = intType;
                vms.add(that.op.cmd);
                break;
            case MUL: case DIV:
                checkType(intType, type1, type2, that.op.name + " must int op int");
                resultType = intType;
                vms.add("call " + that.op.cmd + " 2");
                break;
            case LT: case GT:
                checkType(intType, type1, type2, that.op.name + " must int op int");
                resultType = boolType;
                vms.add(that.op.cmd);
                break;
            case LTEQ: case GTEQ:
                checkType(intType, type1, type2, that.op.name + " must int op int");
                resultType = boolType;
                vms.add(that.op.cmd);
                vms.add("not");
                break;
            case EQ:
                checkType(type1, type2, "must same type eq same type");
                resultType = boolType;
                vms.add(that.op.cmd);
                break;
            case NOTEQ:
                checkType(type1, type2, "must same type noteq same type");
                resultType = boolType;
                vms.add(that.op.cmd);
                vms.add("not");
                break;
            case BOOLAND: case BOOLOR:
                checkType(boolType, type1, type2, that.op.name + " must bool op bool");
                resultType = boolType;
                vms.add(that.op.cmd);
                break;
            default:
                exit("unsupported binary op");
        }
    }

    @Override
    public void visitParens(Parens that) {
        that.expr.accept(this);
    }

    @Override
    public void visitNewArray(NewArray that) {
        that.size.accept(this);
        checkType(intType, resultType, "array size expr must be int type");
        vms.add("call Memory.alloc 1");
        switch (that.type.typeKind){
            case INT: resultType = ArrayType.intArrayType; break;
            case BOOL: resultType = ArrayType.boolArrayType;break;
            case CLASS:
                Symbol symbol
                    = rootScope.get(that.type.name, () -> exit("can not find class " + that.type.name));
                resultType = ((ClassType)symbol.type).arrayType;
                break;
            default: exit("unsupported array type");
        }
    }

    @Override
    public void visitNewClass(NewClass that) {
        ClassSymbol classSymbol = getClassSymbol(that.className);
        long fieldNum = classSymbol.scope.allSymbols()
                .stream().filter(sym -> sym instanceof VarSymbol && ((VarSymbol) sym).varType == VarType.FIELD).count();
         vms.add("push constant " + fieldNum);
         vms.add("call Memory.alloc 1");
        SubroutineSymbol constructorSymbol = (SubroutineSymbol)classSymbol.scope.get("constructor", () -> {});
        if(constructorSymbol != null){
            vms.add("pop temp 0");
            vms.add("push temp 0");
            String subroutineName = classSymbol.name + "." + constructorSymbol.name;
            visitArgs(constructorSymbol.params, that.args, subroutineName);
            vms.add("call " + subroutineName + " " + (constructorSymbol.params.size() + 1));
            vms.add("pop temp 1");
            vms.add("push temp 0");
        }
        resultType = classSymbol.type;
    }

    @Override
    public void visitSubroutineCall(SubroutineCall that) {
        if(that.subroutineName == KeyWordConstant.THIS)
            exit("can not call this()");
        else if(that.subroutineName instanceof Identifier){

            String name = ((Identifier)that.subroutineName).name;
            SubroutineSymbol symbol = getSubroutineSymbol(name, classSymbol);
            if(isFunction && symbol.subroutineType == SubroutineType.METHOD) exit("can not call method in function");
            int extra = 0;
            name = classSymbol.name + "." + name;
            if(symbol.subroutineType == SubroutineType.METHOD){
                vms.add("push pointer 0");
                extra = 1;
            }
            visitArgs(symbol.params, that.args, name);
            vms.add("call " + name + " " + (symbol.params.size() + extra));
            resultType = symbol.type;

        }
        else if(that.subroutineName instanceof FieldAccess){
            FieldAccess fa = (FieldAccess) that.subroutineName;

            if(fa.own instanceof Identifier &&
                    getVarSymbol(((Identifier)fa.own).name, false) == null
            ){
                String name = ((Identifier)fa.own).name;
                ClassSymbol classSymbol = getClassSymbol(name);
                SubroutineSymbol symbol = getSubroutineSymbol(fa.name, classSymbol);
                name = name + "." + fa.name;
                if(symbol.subroutineType != SubroutineType.FUNCTION)
                    exit(name + " is not function");
                visitArgs(symbol.params, that.args, name);
                vms.add("call " + name + " " + symbol.params.size());
                resultType = symbol.type;
            }else {
                fa.own.accept(this);
                if(resultType.typeKind != TypeKind.CLASS) exit("only class type has method member");
                ClassSymbol classSymbol = ((ClassType) resultType).symbol;
                SubroutineSymbol symbol = getSubroutineSymbol(fa.name, classSymbol);
                String name = classSymbol.name + "." + fa.name;
                if(symbol.subroutineType != SubroutineType.METHOD) exit(name + " is not method");
                visitArgs(symbol.params, that.args, name);
                vms.add("call " + name + " " + (symbol.params.size() + 1));
                resultType = symbol.type;
            }

        }
        else exit("unexpected system error");
    }

    private void visitArgs(List<VarSymbol> varSymbols, List<Expression> args, String subroutineName){
        if(varSymbols.size() != args.size()) exit("call " +  subroutineName + " wrong: args size not match");
        int size = varSymbols.size();
        for(int i = 0; i < size; i++){
            args.get(i).accept(this);
            checkType(varSymbols.get(i).type, resultType, "call " + subroutineName + " wrong: no." + i + " arg");
        }
    }

    /** others */

    private VarSymbol getVarSymbol(String name, boolean force){
        VarSymbol varSymbol = (VarSymbol) subroutineSymbol.scope.get(name, () -> {});
        if(varSymbol == null){
            Symbol nextSymbol = classSymbol.scope.get(name, () -> {});
            if(nextSymbol instanceof VarSymbol &&
                    (!isFunction || ((VarSymbol) nextSymbol).varType == VarType.STATIC)){
               varSymbol = (VarSymbol) nextSymbol;
            }
        }
        if(force && varSymbol == null){
            exit("undefined identifier " + name);
        }
        return varSymbol;
    }

    private VarSymbol getVarSymbolFromClass(String name, ClassSymbol classSymbol){
        Symbol symbol = classSymbol.scope.get(name,
                () -> exit("there is not filed " + name + " in class " + classSymbol.name));
        if(!(symbol instanceof VarSymbol) ||
                ((VarSymbol)symbol).varType != VarType.FIELD) exit(name + " is not field var type");
        assert symbol instanceof VarSymbol;
        return (VarSymbol) symbol;
    }

    private SubroutineSymbol getSubroutineSymbol(String name, ClassSymbol classSymbol){
        Symbol symbol = classSymbol.scope.get(name,
                () -> exit("there is not subroutine " + name + " in class " + classSymbol.name));
        if(!(symbol instanceof SubroutineSymbol)) exit(name + " is not subroutine");
        assert symbol instanceof SubroutineSymbol;
        return (SubroutineSymbol) symbol;
    }

    private ClassSymbol getClassSymbol(String name){
        return (ClassSymbol) rootScope.get(name, () -> exit("can not find class " + name));
    }

    private void checkType(Type target, Type source, String msg){
        if(target == ArrayType.intArrayType && source == intType) return;
        if(target != source) exit(msg + ": type not match");
    }

    private void checkType(Type target, Type source1, Type source2,  String msg){
        if(target != source1 || target != source2) exit(msg + ": type not match");
    }

    private void exit(String msg){
        Utils.exit(classSymbol.tree.fileName +": subroutine " + subroutineSymbol.name + ": " + msg);
    }
}
