package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;
import edu.ufl.cise.plpfa22.Scope;

import java.util.Iterator;
import java.util.Stack;

public class Visitor implements ASTVisitor {

    Stack<Scope> scopeStack;
    int id;
    int nest;

    public Visitor() {
        scopeStack = new Stack<>();
        id = 0;
        nest = 0;
    }

    public Object visitProgram(Program program, Object arg) throws PLPException {
        scopeStack.push(new Scope(id, nest));
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
        return null;
    }

    public Object visitStatement(Statement statement, Object arg) throws PLPException {
        if (statement instanceof StatementAssign)
            visitStatementAssign((StatementAssign) statement, arg);
        else if (statement instanceof StatementCall)
            visitStatementCall((StatementCall) statement, arg);
        else if (statement instanceof StatementInput)
            visitStatementInput((StatementInput) statement, arg);
        else if (statement instanceof StatementOutput)
            visitStatementOutput((StatementOutput) statement, arg);
        else if (statement instanceof StatementBlock)
            visitStatementBlock((StatementBlock) statement, arg);
        else if (statement instanceof StatementIf)
            visitStatementIf((StatementIf) statement, arg);
        else if (statement instanceof StatementWhile)
            visitStatementWhile((StatementWhile) statement, arg);
        else
            visitStatementEmpty((StatementEmpty) statement, arg);
        return null;
    }

    public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
        visitIdent(statementAssign.ident, arg);
        visitExpression(statementAssign.expression, arg);
        return null;
    }

    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        visitIdent(statementCall.ident, arg);
        return null;
    }

    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        visitIdent(statementInput.ident, arg);
        return null;
    }

    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        visitExpression(statementOutput.expression, arg);
        return null;
    }

    public Object visitStatementBlock(StatementBlock statementBlock, Object arg) throws PLPException {
        for (Statement statement : statementBlock.statements)
            visitStatement(statement, arg);
        return null;
    }

    public Object visitStatementIf(StatementIf statementIf, Object arg) throws PLPException {
        visitExpression(statementIf.expression, arg);
        visitStatement(statementIf.statement, arg);
        return null;
    }

    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        visitExpression(statementWhile.expression, arg);
        visitStatement(statementWhile.statement, arg);
        return null;
    }

    public Object visitStatementEmpty(StatementEmpty statementEmpty, Object arg) throws PLPException {
        return null;
    }

    public Object visitExpression(Expression expression, Object arg) throws PLPException {
        if (expression instanceof ExpressionBinary)
            visitExpressionBinary((ExpressionBinary) expression, arg);
        else if (expression instanceof ExpressionIdent)
            visitExpressionIdent((ExpressionIdent) expression, arg);
        else if (expression instanceof ExpressionNumLit)
            visitExpressionNumLit((ExpressionNumLit) expression, arg);
        else if (expression instanceof ExpressionStringLit)
            visitExpressionStringLit((ExpressionStringLit) expression, arg);
        else
            visitExpressionBooleanLit((ExpressionBooleanLit) expression, arg);
        return null;
    }

    public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
        visitExpression(expressionBinary.e0, arg);
        visitExpression(expressionBinary.e1, arg);
        return null;
    }

    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        expressionIdent.setNest(nest);
        // TODO set dec
        return null;
    }

    public Object visitExpressionNumLit(ExpressionNumLit expressionNumLit, Object arg) throws PLPException {
        return null;
    }

    public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
        return null;
    }

    public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
        return null;
    }

    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        procDec.setNest(nest);
        visitBlock(procDec.block, arg);
        return null;
    }

    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
        constDec.setNest(nest);
        return null;
    }

    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        varDec.setNest(nest);
        return null;
    }

    public Object visitIdent(Ident ident, Object arg) throws PLPException {
        ident.setNest(nest);
        // TODO set dec
        return null;
    }

    public void enterScope() {
        scopeStack.push(new Scope(id, nest));
        id++;
        nest++;
    }

    public void closeScope() {
        scopeStack.pop();
        nest--;
    }

    public Declaration lookup(IToken ident) {
        Declaration dec = null;
        Iterator iter = scopeStack.iterator();
        while (iter.hasNext() && dec.equals(null)) {
            dec = ((Scope)iter.next()).lookup(ident);
        }
        return dec;
    }
}
