package sym;

import tree.SubroutineDecl;
import tree.VarDecl;
import tree.Visitor;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: member enter
 */
public class MemberEnter extends Visitor {

    Scope rootScope;
    Scope scope;
    int fieldIndex;
    int staticIndex;
    int methodIndex;

    public MemberEnter(Scope rootScope){
        this.rootScope = rootScope;
    }

    public void memberEnter(ClassSymbol classSymbol){
        scope = classSymbol.scope;
        fieldIndex = 0;
        staticIndex = 0;
        methodIndex = 0;
        for(VarDecl tree: classSymbol.tree.varDecls){
            tree.accept(this);
        }
        for(SubroutineDecl tree: classSymbol.tree.subroutineDecls){
            tree.accept(this);
        }
    }

    @Override
    public void visitVarDecl(VarDecl that) {
        Type type;
        switch (that.typeDecl.typeKind){
            case INT: type = Type.intType; break;
            case BOOL: type = Type.boolType; break;
            case CHAR: type = Type.charType;break;
            default:
                String className = that.typeDecl.name;

        }
    }

    @Override
    public void visitSubroutineDecl(SubroutineDecl that) {

    }
}
