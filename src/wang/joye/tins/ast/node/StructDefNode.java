package wang.joye.tins.ast.node;

import wang.joye.tins.type.Token;
import wang.joye.tins.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 结构体
 */
public class StructDefNode extends Node {
    public Token nameToken;
    public List<VarDefNode> fieldDefs = new ArrayList<>();

    public VarDefNode getFieldDef(String name) {
        for (VarDefNode fieldDef : fieldDefs) {
            if (fieldDef.varNameToken.name.equals(name))
                return fieldDef;
        }
        return null;
    }

    @Override
    public void check(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
