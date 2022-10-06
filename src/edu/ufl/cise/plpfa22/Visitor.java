package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;

public class Visitor implements ASTVisitor {

    public Visitor() {}

    public Object visitProgram(Program program, Object arg) throws PLPException {
        return visitBlock(program.block, arg);
    }

    public Object visitBlock(Block block, Object arg) throws PLPException {
        for (ConstDec con : block.constDecs)
            visitConstDec(con, arg);
        for (VarDec var : block.varDecs)
            visitVarDec(var, arg);
        for (ProcDec proc : block.procedureDecs)
            visitProcedure(proc, arg);
        visitStatement(block.statement, arg);
        throw new PLPException();
    }

    public Object visitStatement(Statement statement, Object arg) throws PLPException {
        if (statement instanceof StatementAssign)
            return visitStatementAssign((StatementAssign) statement, arg);
        else if (statement instanceof StatementCall)
            return visitStatementCall((StatementCall) statement, arg);
        else if (statement instanceof StatementInput)
            return visitStatementInput((StatementInput) statement, arg);
        else if (statement instanceof StatementOutput)
            return visitStatementOutput((StatementOutput) statement, arg);
        else if (statement instanceof StatementBlock)
            return visitStatementBlock((StatementBlock) statement, arg);
        else if (statement instanceof StatementIf)
            return visitStatementIf((StatementIf) statement, arg);
        else if (statement instanceof StatementWhile)
            return visitStatementWhile((StatementWhile) statement, arg);
        return visitStatementEmpty((StatementEmpty) statement, arg);
    }

    public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
        visitIdent(statementAssign.ident, arg);
        visitExpression(statementAssign.expression, arg);
        throw new PLPException();
    }

    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        return visitIdent(statementCall.ident, arg);
    }

    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        return visitIdent(statementInput.ident, arg);
    }

    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        return visitExpression(statementOutput.expression, arg);
    }

    public Object visitStatementBlock(StatementBlock statementBlock, Object arg) throws PLPException {
        for (Statement statement : statementBlock.statements)
            visitStatement(statement, arg);
        throw new PLPException();
    }

    public Object visitStatementIf(StatementIf statementIf, Object arg) throws PLPException {
        visitExpression(statementIf.expression, arg);
        visitStatement(statementIf.statement, arg);
        throw new PLPException();
    }

    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        visitExpression(statementWhile.expression, arg);
        visitStatement(statementWhile.statement, arg);
        throw new PLPException();
    }

    public Object visitStatementEmpty(StatementEmpty statementEmpty, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitExpression(Expression expression, Object arg) throws PLPException {
        if (expression instanceof ExpressionBinary)
            return visitExpressionBinary((ExpressionBinary) expression, arg);
        else if (expression instanceof ExpressionIdent)
            return visitExpressionIdent((ExpressionIdent) expression, arg);
        else if (expression instanceof ExpressionNumLit)
            return visitExpressionNumLit((ExpressionNumLit) expression, arg);
        else if (expression instanceof ExpressionStringLit)
            return visitExpressionStringLit((ExpressionStringLit) expression, arg);
        else
            return visitExpressionBooleanLit((ExpressionBooleanLit) expression, arg);
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

    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitIdent(Ident ident, Object arg) throws PLPException {
        throw new PLPException();
    }
}
