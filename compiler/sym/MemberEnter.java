package sym;

import tree.*;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


import static sym.Symbol.*;
import static sym.Type.*;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: member enter
 */
public class MemberEnter extends Visitor {

    Scope rootScope;
    Scope scope;

    ClassSymbol classSymbol;
    int fieldIndex;
    int staticIndex;

    int paramIndex;
    int varIndex;
    List<VarSymbol> params;

    public MemberEnter(Scope rootScope){
        this.rootScope = rootScope;
    }

    public void enter(ClassSymbol classSymbol){
        this.classSymbol = classSymbol;
        this.scope = classSymbol.scope;
        fieldIndex = 0;
        staticIndex = 0;
        for(VarDecl tree: classSymbol.tree.varDecls){
            tree.accept(this);
        }
        for(SubroutineDecl tree: classSymbol.tree.subroutineDecls){
            tree.accept(this);
        }
    }

    private Type getType(TypeDecl typeDecl){
        Type type = null;
        String name = typeDecl.name;
        switch (typeDecl.typeKind){
            case INT: type = Type.intType; break;
            case BOOL: type = Type.boolType; break;
            case VOID: type = Type.voidType; break;
            case CLASS:
                ClassSymbol targetSymbol = (ClassSymbol)rootScope.get(name,
                        () -> Utils.exit(classSymbol.tree.fileName + ": cannot find class " + name));
                type = targetSymbol.type;
                break;
            case Array:
                if(typeDecl == TypeDecl.intArrayType){
                    type = ArrayType.intArrayType;
                    break;
                }
                if(typeDecl == TypeDecl.boolArrayType){
                    type = ArrayType.boolArrayType;
                    break;
                }
                ClassSymbol itemSymbol = (ClassSymbol)rootScope.get(name,
                        () -> Utils.exit(classSymbol.tree.fileName + ": cannot find class " + name));
                type = ((ClassType)itemSymbol.type).arrayType;
                break;
        }
        return type;
    }

    @Override
    public void visitVarDecl(VarDecl that) {

        Type type = getType(that.typeDecl);

        for (String varName: that.varNames){
            VarSymbol varSymbol = new VarSymbol();
            varSymbol.name = varName;
            varSymbol.varType = that.varType;
            varSymbol.type = type;
            AtomicBoolean isVar = new AtomicBoolean(false);
            switch (that.varType){
                case STATIC: varSymbol.index = staticIndex++; break;
                case FIELD: varSymbol.index = fieldIndex++; break;
                case VAR:
                    varSymbol.index = varIndex++;
                    isVar.set(true);
                    break;
                case PARAM:
                    varSymbol.index = paramIndex++;
                    isVar.set(true);
                    params.add(varSymbol);
                    break;
            }
            scope.put(varName, varSymbol, () -> {
                if(isVar.get())
                    Utils.exit(classSymbol.tree.fileName + ": " + "subroutine " + scope.symbol.name + ": duplicated variable name: " + varName);
                else
                    Utils.exit(classSymbol.tree.fileName + ": duplicated member: " + varName);
            });
        }
    }



    @Override
    public void visitSubroutineDecl(SubroutineDecl that) {

        SubroutineSymbol subroutineSymbol = new SubroutineSymbol();
        subroutineSymbol.subroutineType = that.subroutineType;
        subroutineSymbol.name = that.subroutineName;
        subroutineSymbol.type = getType(that.returnType);
        subroutineSymbol.scope = new Scope(subroutineSymbol);
        subroutineSymbol.params = new ArrayList<>();

        params = subroutineSymbol.params;
        paramIndex = 0;
        varIndex = 0;
        Scope temp = scope;
        scope = subroutineSymbol.scope;

        if(that.subroutineType == SubroutineType.METHOD){
            thisSymbol();
        }

        for(VarDecl tree: that.parameterDecls){
            tree.accept(this);
        }

        for(VarDecl tree: that.varDecls){
            tree.accept(this);
        }

        scope = temp;
        scope.put(that.subroutineName, subroutineSymbol,
                () -> Utils.exit(classSymbol.tree.fileName + ": duplicated member: " + that.subroutineName));

        that.sym = subroutineSymbol;
    }

    private void thisSymbol(){
        paramIndex++;
    }

}
