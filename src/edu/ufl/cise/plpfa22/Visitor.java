package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;

import java.util.*;

public class Visitor implements ASTVisitor {
    SymbolTable symbolTable;
    boolean lastPass;

    public Visitor() {
        lastPass = false;
        symbolTable = new SymbolTable();
    }

    public Object visitProgram(Program program, Object arg) throws PLPException {
        symbolTable.init();
        visitBlock(program.block, arg);
        lastPass = true;
        symbolTable.init();
        visitBlock(program.block, arg);
        symbolTable.closeScope();
        return null;
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
        if (lastPass) {
            expressionIdent.setNest(symbolTable.nest);
            Declaration dec = symbolTable.lookup(expressionIdent.firstToken);
            if (dec == null)
                throw new ScopeException("Could not find declaration.");
            expressionIdent.setDec(dec);
        }
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
        if (!lastPass) {
            symbolTable.newIdentifier(procDec);
        }
        symbolTable.enterScope();
        visitBlock(procDec.block, arg);
        symbolTable.closeScope();
        return null;
    }

    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
        if (!lastPass) {
            symbolTable.newIdentifier(constDec);
        }
        return null;
    }

    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        if (!lastPass) {
            symbolTable.newIdentifier(varDec);
        }
        return null;
    }

    public Object visitIdent(Ident ident, Object arg) throws PLPException {
        if (lastPass) {
            ident.setNest(symbolTable.nest);
            Declaration dec = symbolTable.lookup(ident.firstToken);
            if (dec == null)
                throw new ScopeException("Could not find declaration.");
            ident.setDec(dec);
        }
        return null;
    }
}
