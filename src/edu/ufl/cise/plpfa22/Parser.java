package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;
import edu.ufl.cise.plpfa22.IToken.Kind;

import java.text.ParseException;

public class Parser implements IParser{

    private ILexer lexer;

    public Parser(ILexer lexer) {
        this.lexer = lexer;
    }
    public ASTNode parse() throws PLPException {
        while (!lexer.peek().equals(Kind.EOF)) {
            return parseExpr();
        }
        return new ExpressionBooleanLit(new Token(Kind.EOF, new char[]{' '}, 1, 1));
    }

    public Expression parseExpr() throws PLPException {
        IToken first = lexer.peek();
        Expression e0 = parseAdditiveExpr();
        if (lexer.peek().getKind().equals(Kind.LT) || lexer.peek().getKind().equals(Kind.GT)
            || lexer.peek().getKind().equals(Kind.EQ) || lexer.peek().getKind().equals(Kind.NEQ)
            || lexer.peek().getKind().equals(Kind.LE) || lexer.peek().getKind().equals(Kind.GE)) {
            IToken op = lexer.next();
            Expression e1 = parseAdditiveExpr();
            return new ExpressionBinary(first, e0, op, e1);
        }
        return e0;
    }

    public Expression parseAdditiveExpr() throws PLPException {
        IToken first = lexer.peek();
        Expression e0 = parseMultiplicativeExpr();
        if (lexer.peek().getKind().equals(Kind.PLUS) || lexer.peek().getKind().equals(Kind.MINUS)) {
            IToken op = lexer.next();
            Expression e1 = parseMultiplicativeExpr();
            return new ExpressionBinary(first, e0, op, e1);
        }
        return e0;
    }

    public Expression parseMultiplicativeExpr() throws PLPException {
        IToken first = lexer.peek();
        Expression e0 = parsePrimaryExpr();
        if (lexer.peek().getKind().equals(Kind.TIMES) || lexer.peek().getKind().equals(Kind.DIV)
            || lexer.peek().getKind().equals(Kind.MOD)) {
            IToken op = lexer.next();
            Expression e1 = parsePrimaryExpr();
            return new ExpressionBinary(first, e0, op, e1);
        }
        return e0;
    }

    public Expression parsePrimaryExpr() throws PLPException {
        switch (lexer.peek().getKind()) {
            case IDENT:
                return parseIdent();
            case BOOLEAN_LIT:
                return parseConst();
            case STRING_LIT:
                return parseConst();
            case NUM_LIT:
                return parseConst();
            case LPAREN:
                return parseParen();
            default:
                throw new SyntaxException("Invalid primary expression.");
        }
    }

    public Expression parseConst() throws PLPException {
        switch (lexer.peek().getKind()) {
            case BOOLEAN_LIT:
                return parseBoolean();
            case STRING_LIT:
                return parseString();
            case NUM_LIT:
                return parseNumber();
        }
        return new ExpressionBooleanLit(new Token(Kind.EOF, new char[]{' '}, 1, 1));
    }

    public Expression parseParen() throws PLPException {
        // discard parenthesis
        lexer.next();
        Expression expr = parseExpr();
        if (!lexer.peek().getKind().equals(Kind.RPAREN)) {
            throw new SyntaxException("Missing closing parenthesis");
        }
        lexer.next();
        return expr;
    }

    public ExpressionIdent parseIdent() throws PLPException {
        ExpressionIdent exp = new ExpressionIdent(lexer.peek());
        exp.setDec(parseVarDec());
        return exp;
    }

    public VarDec parseVarDec() throws PLPException {
        return new VarDec(lexer.peek(), lexer.next());
    }

    public ExpressionBooleanLit parseBoolean() throws PLPException {
        return new ExpressionBooleanLit(lexer.next());
    }

    public ExpressionNumLit parseNumber() throws PLPException {
        return new ExpressionNumLit(lexer.next());
    }

    public ExpressionStringLit parseString() throws PLPException {
        return new ExpressionStringLit(lexer.next());
    }

}
