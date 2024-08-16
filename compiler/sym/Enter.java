package sym;

import tree.ClassDecl;
import tree.Tree;
import tree.TypeKind;
import tree.Visitor;
import utils.Utils;


/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: 符号收集第一阶段
 */
public class Enter extends Visitor {

    Scope scope;

    public Enter(Scope scope){
        this.scope = scope;
    }

    public void classEnter(Tree tree){
            tree.accept(this);
    }

    @Override
    public void visitClassDecl(ClassDecl that) {
       if(scope.table.containsKey(that.className)){
           Utils.exit("类名重复: " + that.className);
       }
       ClassSymbol classSymbol = new ClassSymbol();
       classSymbol.name = that.className;
       classSymbol.scope = new Scope(scope, classSymbol);
       classSymbol.tree = that;
       classSymbol.type = new Type(TypeKind.CLASS, classSymbol);
       scope.table.put(that.className, classSymbol);
    }
}
