package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;
import edu.ufl.cise.plpfa22.ast.Types.Type;

public class TypeVisitor implements ASTVisitor {

    public Object visitProgram(Program program, Object arg) throws PLPException {
        visitBlock(program.block, arg);
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
        // If doesn't have a type
        if (statementAssign.ident.getDec().getType() == null) {
            statementAssign.ident.getDec().setType(statementAssign.expression.getType());
        }
        // If it does, make sure compatible
        else {
            if (!checkCompat(statementAssign.ident, statementAssign.ident)) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        return null;
    }

    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
        visitIdent(statementCall.ident, arg);
        if (statementCall.ident.getDec().getType() != Type.PROCEDURE) {
            throw new TypeCheckException("Types are not compatible");
        }
        return null;
    }

    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
        visitIdent(statementInput.ident, arg);
        if (statementInput.ident.getDec().getType() == Type.PROCEDURE) {
            throw new TypeCheckException("Types are not compatible");
        }
        return null;
    }

    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
        visitExpression(statementOutput.expression, arg);
        if (statementOutput.expression.getType() == Type.PROCEDURE) {
            throw new TypeCheckException("Types are not compatible");
        }
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
        if (statementIf.expression.getType() != Type.BOOLEAN) {
            throw new TypeCheckException("Types are not compatible");
        }
        return null;
    }

    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
        visitExpression(statementWhile.expression, arg);
        visitStatement(statementWhile.statement, arg);
        if (statementWhile.expression.getType() != Type.BOOLEAN) {
            throw new TypeCheckException("Types are not compatible");
        }
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
        IToken op = expressionBinary.op;
        // PLUS
        if (isKind(op, IToken.Kind.PLUS)) {
            if (checkCompat(expressionBinary.e0, expressionBinary.e1) && expressionBinary.e0.getType() != Type.PROCEDURE
                && expressionBinary.e1.getType() != Type.PROCEDURE) {
                expressionBinary.setType(expressionBinary.e0.getType());
            }
            else {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        // MINUS, DIV, MOD
        else if (isKind(op, IToken.Kind.MINUS) || isKind(op, IToken.Kind.DIV)
                || isKind(op, IToken.Kind.MOD)) {
            if (expressionBinary.e0.getType() == Type.NUMBER && expressionBinary.e1.getType() == Type.NUMBER) {
                expressionBinary.setType(Type.NUMBER);
            }
            else {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        // TIMES
        else if (isKind(op, IToken.Kind.TIMES)) {
            if (checkCompat(expressionBinary.e0, expressionBinary.e1) && expressionBinary.e0.getType() != Type.PROCEDURE
                    && expressionBinary.e1.getType() != Type.PROCEDURE && expressionBinary.e0.getType() != Type.STRING
                    && expressionBinary.e1.getType() != Type.STRING) {
                expressionBinary.setType(expressionBinary.e0.getType());
            }
            else {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        // EQ, NEQ, LT, LE, GT, GE
        else {
            if (checkCompat(expressionBinary.e0, expressionBinary.e1) && expressionBinary.e0.getType() != Type.PROCEDURE
                    && expressionBinary.e1.getType() != Type.PROCEDURE) {
                expressionBinary.setType(Type.BOOLEAN);
            }
            else {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        return null;
    }

    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        if (expressionIdent.getType() != expressionIdent.getDec().getType()) {
            throw new TypeCheckException("Expression ident type should be equal to dec type");
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
        procDec.setType(Type.PROCEDURE);
        visitBlock(procDec.block, arg);
        return null;
    }

    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
        if (constDec.val instanceof ExpressionNumLit) {
            constDec.setType(Type.NUMBER);
        }
        else if (constDec.val instanceof ExpressionBooleanLit) {
            constDec.setType(Type.BOOLEAN);
        }
        else if (constDec.val instanceof ExpressionStringLit) {
            constDec.setType(Type.STRING);
        }
        else {
            throw new TypeCheckException("Object is invalid type");
        }
        return null;
    }

    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        throw new PLPException();
    }

    public Object visitIdent(Ident ident, Object arg) throws PLPException {
        return null;
    }

    public boolean checkCompat(Expression a, Expression b) {
        if (a.getType() == b.getType()) {
            return true;
        }
        return false;
    }

    public boolean checkCompat(Ident a, Ident b) {
        if (a.getDec().getType() == b.getDec().getType()) {
            return true;
        }
        return false;
    }

    public boolean isKind(IToken t, IToken.Kind kind) {
        if (t.getKind().equals(kind)) {
            return true;
        }
        return false;
    }
}