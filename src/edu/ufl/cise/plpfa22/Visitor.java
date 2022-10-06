package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;

public class Visitor implements ASTVisitor {

    public Object visitBlock(Block block, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitProgram(Program program, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitStatementBlock(StatementBlock statementBlock, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitStatementIf(StatementIf statementIf, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitExpressionNumLit(ExpressionNumLit expressionNumLit, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitStatementEmpty(StatementEmpty statementEmpty, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitIdent(Ident ident, Object arg) throws PLPException {
        throw new PLPException();
    }
}
