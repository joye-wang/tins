package wang.joye.tins.ast.expr;

import wang.joye.tins.ast.node.ExprNode;
import wang.joye.tins.type.ExprType;
import wang.joye.tins.type.Token;
import wang.joye.tins.util.DumpUtil;
import wang.joye.tins.visitor.ASTVisitor;
import wang.joye.tins.visitor.ExprTypeVisitor;

public class PrefixUnaryExpr extends ExprNode {
    public ExprNode expr;
    public Token operator;

    public PrefixUnaryExpr(Token operator,ExprNode expr) {
        this.expr = expr;
        this.operator = operator;
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
        return ExprTypeVisitor.getType(this);
    }

    @Override
    public int getLine() {
        return operator.line;
    }
}
