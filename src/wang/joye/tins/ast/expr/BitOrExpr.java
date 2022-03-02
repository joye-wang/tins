package wang.joye.tins.ast.expr;

import wang.joye.tins.ast.node.ExprNode;
import wang.joye.tins.type.ExprType;
import wang.joye.tins.util.DumpUtil;
import wang.joye.tins.visitor.ASTVisitor;

public class BitOrExpr extends ExprNode {
    public ExprNode leftExpr, rightExpr;

    public BitOrExpr(ExprNode leftExpr, ExprNode rightExpr) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    @Override
    public void dump(int level) {
        DumpUtil.dump(level, this);
    }

    @Override
    public void check(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ExprType getType() {
        int lw = leftExpr.getType().getWeight();
        int rw = rightExpr.getType().getWeight();
        return lw > rw ? leftExpr.getType() : rightExpr.getType();
    }

    @Override
    public int getLine() {
        return leftExpr.getLine();
    }
}
