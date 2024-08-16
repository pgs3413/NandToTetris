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
        switch (symbol.subroutineType){
            case METHOD: break;
            case CONSTRUCTOR: break;
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
        String s = null;
        switch (varSymbol.varType){
            case PARAM: s = "pop argument " + varSymbol.index; break;
            case VAR: s = "pop local " + varSymbol.index; break;
            case FIELD:
            case STATIC: Utils.exit("TODO");
        }
        vms.add(s);
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
    public void visitIdentifier(Identifier that) {
        VarSymbol varSymbol = getVarSymbol(that.name);
        String s = null;
        switch (varSymbol.varType){
            case PARAM: s = "push argument " + varSymbol.index; break;
            case VAR: s = "push local " + varSymbol.index; break;
            case FIELD:
            case STATIC: Utils.exit("TODO");
        }
        vms.add(s);
        resultType = varSymbol.type;
    }

    /** others */

    private VarSymbol getVarSymbol(String name){
        VarSymbol varSymbol = (VarSymbol) symbol.scope.table.get(name);
        if(varSymbol == null){
            exit("undefined identifier " + name);
        }
        return varSymbol;
    }

    private void exit(String msg){
        Utils.exit(classSymbol.tree.fileName +": subroutine " + symbol.name + ": " + msg);
    }
}
