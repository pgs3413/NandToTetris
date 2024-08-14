package tree;

import xml.Node;

/**
 * @Author: pangs
 * @Date: 2024/7/23
 * @description: parameter decl
 */
public class ParameterDecl implements Tree {

    public TypeDecl typeDecl;
    public String parameterName;

    public ParameterDecl(TypeDecl typeDecl, String parameterName) {
        this.typeDecl = typeDecl;
        this.parameterName = parameterName;
    }

    @Override
    public void accept(Visitor v) {
        v.visitParameterDecl(this);
    }

    @Override
    public Node toXml() {
        System.err.println("ParameterDecl.toXml 为非法操作");
        System.exit(-1);
        return null;
    }
}
