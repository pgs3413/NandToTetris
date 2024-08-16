package sym;

import tree.SubroutineType;

import java.util.List;

/**
 * @Author: pangs
 * @Date: 2024/8/13
 * @description: method symbol
 */
public class SubroutineSymbol extends Symbol{

    public Scope scope;
    public SubroutineType subroutineType;
    public Type returnType;
    public List<VarSymbol> params;


}
