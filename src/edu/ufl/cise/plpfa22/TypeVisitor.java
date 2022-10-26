package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;
import edu.ufl.cise.plpfa22.ast.Types.Type;

import static java.lang.Integer.MAX_VALUE;

public class TypeVisitor implements ASTVisitor {
    int numChanges;
    int numVars;
    int numTyped;

    public TypeVisitor() {
        numChanges = MAX_VALUE;
        numVars = MAX_VALUE;
        numTyped = 0;
    }

    public Object visitProgram(Program program, Object arg) throws PLPException {
        while (numChanges > 0 && numVars > numTyped) {
            numChanges = 0; numVars = 0;
            visitBlock(program.block, arg);
        }
        if (numChanges == 0 && numVars > numTyped)
            throw new TypeCheckException("Type could not be set");
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
            assignType(statementAssign.ident.getDec(), statementAssign.expression.getType());
        }
        // If it does, make sure compatible
        else {
            if (!checkCompat(statementAssign.ident, statementAssign.expression)
                || statementAssign.ident.getDec() instanceof ConstDec) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        return null;
    }

    public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {

        visitIdent(statementCall.ident, arg);
        if (statementCall.ident.getDec().getType() != null) {
            if (statementCall.ident.getDec().getType() != Type.PROCEDURE) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        return null;
    }

    public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {

        visitIdent(statementInput.ident, arg);
        if (statementInput.ident.getDec().getType() != null) {
            if (statementInput.ident.getDec().getType() != Type.NUMBER
                    && statementInput.ident.getDec().getType() != Type.BOOLEAN
                    && statementInput.ident.getDec().getType() != Type.STRING) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        return null;
    }

    public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {

        visitExpression(statementOutput.expression, arg);
        if (statementOutput.expression.getType() != null) {
            if (statementOutput.expression.getType() != Type.NUMBER
                    && statementOutput.expression.getType() != Type.BOOLEAN
                    && statementOutput.expression.getType() != Type.STRING) {
                throw new TypeCheckException("Types are not compatible");
            }
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
        if (statementIf.expression.getType() != null) {
            if (statementIf.expression.getType() != Type.BOOLEAN) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        return null;
    }

    public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {

        visitExpression(statementWhile.expression, arg);
        visitStatement(statementWhile.statement, arg);
        if (statementWhile.expression.getType() != null) {
            if (statementWhile.expression.getType() != Type.BOOLEAN) {
                throw new TypeCheckException("Types are not compatible");
            }
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
        numVars++;

        visitExpression(expressionBinary.e0, arg);
        visitExpression(expressionBinary.e1, arg);
        IToken op = expressionBinary.op;
        handleImplicit(expressionBinary);

        // PLUS
        if (isKind(op, IToken.Kind.PLUS)) {
            if (checkCompat(expressionBinary.e0, expressionBinary.e1) && (expressionBinary.e0.getType() == Type.NUMBER
                || expressionBinary.e0.getType() == Type.BOOLEAN || expressionBinary.e0.getType() == Type.STRING)) {
                assignType(expressionBinary, expressionBinary.e0.getType());
            }
            else if (expressionBinary.e0.getType() != null && expressionBinary.e1.getType() != null) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        // MINUS, DIV, MOD
        else if (isKind(op, IToken.Kind.MINUS) || isKind(op, IToken.Kind.DIV)
                || isKind(op, IToken.Kind.MOD)) {
            if (expressionBinary.e0.getType() == Type.NUMBER && expressionBinary.e1.getType() == Type.NUMBER) {
                assignType(expressionBinary, Type.NUMBER);
            }
            else if (expressionBinary.e0.getType() != null && expressionBinary.e1.getType() != null) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        // TIMES
        else if (isKind(op, IToken.Kind.TIMES)) {
            if (checkCompat(expressionBinary.e0, expressionBinary.e1) && (expressionBinary.e0.getType() == Type.NUMBER
                    || expressionBinary.e0.getType() == Type.BOOLEAN)) {
                assignType(expressionBinary, expressionBinary.e0.getType());
            }
            else if (expressionBinary.e0.getType() != null && expressionBinary.e1.getType() != null) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        // EQ, NEQ, LT, LE, GT, GE
        else {
            if (checkCompat(expressionBinary.e0, expressionBinary.e1) && (expressionBinary.e0.getType() == Type.NUMBER
                    || expressionBinary.e0.getType() == Type.BOOLEAN || expressionBinary.e0.getType() == Type.STRING)) {
                assignType(expressionBinary, Type.BOOLEAN);
            }
            else if (expressionBinary.e0.getType() != null && expressionBinary.e1.getType() != null) {
                throw new TypeCheckException("Types are not compatible");
            }
        }
        return null;
    }

    public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
        numVars++;
        if (expressionIdent.getType() == null)
            assignType(expressionIdent, expressionIdent.getDec().getType());
        return null;
    }

    public Object visitExpressionNumLit(ExpressionNumLit expressionNumLit, Object arg) throws PLPException {
        numVars++;
        assignType(expressionNumLit, Type.NUMBER);
        return null;
    }

    public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
        numVars++;
        assignType(expressionStringLit, Type.STRING);
        return null;
    }

    public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
        numVars++;
        assignType(expressionBooleanLit, Type.BOOLEAN);
        return null;
    }

    public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
        numVars++;

        assignType(procDec, Type.PROCEDURE);
        visitBlock(procDec.block, arg);
        return null;
    }

    public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
        numVars++;
        if (constDec.val instanceof Integer) {
            assignType(constDec, Type.NUMBER);
        }
        else if (constDec.val instanceof Boolean) {
            assignType(constDec, Type.BOOLEAN);
        }
        else if (constDec.val instanceof String) {
            assignType(constDec, Type.STRING);
        }
        else {
            throw new TypeCheckException("Object is invalid type");
        }
        return null;
    }

    public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
        return null;
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

    public boolean checkCompat(Ident a, Expression b) {
        if (a.getDec().getType() == b.getType()) {
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

    public void handleImplicit(ExpressionBinary expr) {
        if (expr.e0.getType() == null  && expr.e1.getType() != null) {
            assignType(expr.e0, expr.e1.getType());
            if (expr.e0 instanceof ExpressionIdent)
                assignType(((ExpressionIdent) expr.e0).getDec(), expr.e1.getType());
        }
        else if (expr.e1.getType() == null && expr.e0.getType() != null) {
            assignType(expr.e1, expr.e0.getType());
            if (expr.e1 instanceof ExpressionIdent)
                assignType(((ExpressionIdent) expr.e1).getDec(), expr.e0.getType());
        }
//        else if (expr.e0.getType() == null && expr.e1.getType() == null && expr.getType() != null) {
//            expr.e0.setType(expr.getType());
//            if (expr.e0 instanceof ExpressionIdent)
//                ((ExpressionIdent) expr.e0).getDec().setType(expr.e1.getType());
//            expr.e1.setType(expr.getType());
//            if (expr.e1 instanceof ExpressionIdent)
//                ((ExpressionIdent) expr.e1).getDec().setType(expr.e0.getType());
//        }
    }

    public void assignType(Declaration dec, Type type) {
        if (dec.getType() == null && type != null) {
            dec.setType(type);
            numChanges++;
            numTyped++;
        }
    }

    public void assignType(Expression expr, Type type) {
        if (expr.getType() == null && type != null) {
            expr.setType(type);
            numChanges++;
            numTyped++;
        }
    }
}
