package sym;

import tree.*;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: member enter
 */
public class MemberEnter extends Visitor {

    Scope rootScope;
    Scope scope;
    ClassSymbol symbol;
    int fieldIndex;
    int staticIndex;
    int paramIndex;
    int varIndex;
    List<VarSymbol> params;

    public MemberEnter(Scope rootScope){
        this.rootScope = rootScope;
    }

    public void memberEnter(ClassSymbol classSymbol){
        symbol = classSymbol;
        scope = classSymbol.scope;
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
        Type type;
        switch (typeDecl.typeKind){
            case INT: type = Type.intType; break;
            case BOOL: type = Type.boolType; break;
            case CHAR: type = Type.charType;break;
            case VOID: type = Type.voidType;break;
            default:
                String className = typeDecl.name;
                ClassSymbol targetSymbol = (ClassSymbol)rootScope.table.get(className);
                if(targetSymbol == null){
                    Utils.exit(symbol.tree.fileName + ": cannot find class " + className);
                }
                assert targetSymbol != null;
                type = targetSymbol.type;
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
            boolean isVar = false;
            switch (that.varType){
                case STATIC: varSymbol.index = staticIndex++; break;
                case FIELD: varSymbol.index = fieldIndex++; break;
                case VAR: varSymbol.index = varIndex++; isVar = true; break;
                default: Utils.exit(symbol.tree.fileName + ": wrong var type " + that.varType.name);
            }
            if(scope.table.containsKey(varName)){
                String s ;
                if(isVar){
                    s = symbol.tree.fileName + ": subroutine " + scope.sym.name;
                }else {
                    s = symbol.tree.fileName;
                }
                Utils.exit(s + ": duplicated filed name " + varName);
            }
            scope.table.put(varName, varSymbol);
        }
    }

    @Override
    public void visitSubroutineDecl(SubroutineDecl that) {
        SubroutineSymbol subroutineSymbol = new SubroutineSymbol();
        subroutineSymbol.subroutineType = that.subroutineType;
        subroutineSymbol.name = that.subroutineName;
        subroutineSymbol.returnType = getType(that.returnType);
        subroutineSymbol.scope = new Scope(scope, subroutineSymbol);
        subroutineSymbol.params = new ArrayList<>();
        params = subroutineSymbol.params;
        paramIndex = 0;
        varIndex = 0;
        Scope temp = scope;
        scope = subroutineSymbol.scope;
        for(ParameterDecl tree: that.parameterDecls){
            tree.accept(this);
        }
        for(VarDecl tree: that.varDecls){
            tree.accept(this);
        }
        scope = temp;
        if(scope.table.containsKey(that.subroutineName)){
            Utils.exit(symbol.tree.fileName + ": duplicated filed name " + that.subroutineName);
        }
        scope.table.put(that.subroutineName, subroutineSymbol);
    }

    @Override
    public void visitParameterDecl(ParameterDecl that) {
        VarSymbol varSymbol = new VarSymbol();
        varSymbol.name = that.parameterName;
        varSymbol.varType = VarType.PARAM;
        varSymbol.type = getType(that.typeDecl);
        varSymbol.index = paramIndex++;
        if(scope.table.containsKey(that.parameterName)){
            Utils.exit(symbol.tree.fileName +
                    ": subroutine " + scope.sym.name + ": duplicated filed name "+ that.parameterName);
        }
        params.add(varSymbol);
        scope.table.put(that.parameterName, varSymbol);
    }
}
