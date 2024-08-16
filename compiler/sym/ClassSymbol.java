package sym;

import tree.ClassDecl;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: class symbol
 */
public class ClassSymbol extends Symbol{

    public Scope scope;
    public ClassDecl tree;
    public Type type;
}
