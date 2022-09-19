package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;
import edu.ufl.cise.plpfa22.IToken.Kind;
import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser{

    private ILexer lexer;

    public Parser(ILexer lexer) {
        this.lexer = lexer;
    }
    public ASTNode parse() throws PLPException {
        return parseProgram();
    }

    public Program parseProgram() throws PLPException {
        IToken first = peek();
        Block block = parseBlock();
        match(Kind.DOT);
        return new Program(first, block);
    }

    public Block parseBlock() throws PLPException {
        List<ConstDec> consts = new ArrayList<>();
        List<VarDec> vars = new ArrayList<>();
        List<ProcDec> procedures = new ArrayList<>();
        IToken first = peek();
        while (isKind(Kind.KW_CONST)) {
            consts.addAll(parseConstDec());
        }
        while (isKind(Kind.KW_VAR)) {
            vars.addAll(parseVarDec());
        }
        while (isKind(Kind.KW_PROCEDURE)) {
            procedures.add(parseProcDec());
        }
        Statement stmt = parseStmt();
        return new Block(first, consts, vars, procedures, stmt);
    }

    public List<ConstDec> parseConstDec() throws PLPException {
        List<ConstDec> consts = new ArrayList<>();
        // discard CONST
        IToken first = match(Kind.KW_CONST);
        IToken ident = match(Kind.IDENT);
        match(Kind.EQ);
        Expression constVal = parseConstExpr();
        consts.add(new ConstDec(first, ident, constVal));
        while (isKind(Kind.COMMA)) {
            match(Kind.COMMA);
            IToken identNext = match(Kind.IDENT);
            match(Kind.EQ);
            Expression constValNext = parseConstExpr();
            consts.add(new ConstDec(first, identNext, constValNext));
        }
        match(Kind.SEMI);
        return consts;
    }

    public List<VarDec> parseVarDec() throws PLPException {
        List<VarDec> vars = new ArrayList<>();
        IToken first = match(Kind.KW_VAR);
        vars.add(new VarDec(first, match(Kind.IDENT)));
        while (isKind(Kind.COMMA)) {
            match(Kind.COMMA);
            vars.add(new VarDec(first, match(Kind.IDENT)));
        }
        match(Kind.SEMI);
        return vars;
    }

    public ProcDec parseProcDec() throws PLPException {
        IToken first = match(Kind.KW_PROCEDURE);
        IToken ident = match(Kind.IDENT);
        match(Kind.SEMI);
        Block block = parseBlock();
        match(Kind.SEMI);
        return new ProcDec(first, ident, block);
    }

    public Statement parseStmt() throws PLPException {
        switch (peek().getKind()) {
            case IDENT:
                return parseAssignStmt();
            case KW_CALL:
                return parseCallStmt();
            case QUESTION:
                return parseInputStmt();
            case BANG:
                return parseOutputStmt();
            case KW_BEGIN:
                return parseBlockStmt();
            case KW_IF:
                return parseIfStmt();
            case KW_WHILE:
                return parseWhileStmt();
            case DOT:
                return new StatementEmpty(peek());
            case EOF:
                return parseEmptyStmt();
            default:
                throw new SyntaxException("Invalid statement");
        }
    }

    public StatementAssign parseAssignStmt() throws PLPException {
        IToken first = peek();
        Ident ident = parseIdent();
        match(Kind.ASSIGN);
        Expression expr = parseExpr();
        return new StatementAssign(first, ident, expr);
    }

    public StatementCall parseCallStmt() throws PLPException {
        IToken first = match(Kind.KW_CALL);
        Ident ident = parseIdent();
        return new StatementCall(first, ident);
    }

    public StatementInput parseInputStmt() throws PLPException {
        IToken first = match(Kind.QUESTION);
        Ident ident = parseIdent();
        return new StatementInput(first, ident);
    }

    public StatementOutput parseOutputStmt() throws PLPException {
        IToken first = match(Kind.BANG);
        Expression expr = parseExpr();
        return new StatementOutput(first, expr);
    }

    public StatementBlock parseBlockStmt() throws PLPException {
        IToken first = match(Kind.KW_BEGIN);
        List<Statement> stmts = new ArrayList<>();
        stmts.add(parseStmt());
        while (isKind(Kind.SEMI)) {
            match(Kind.SEMI);
            stmts.add(parseStmt());
        }
        match(Kind.KW_END);
        return new StatementBlock(first, stmts);
    }

    public StatementIf parseIfStmt() throws PLPException {
        IToken first = match(Kind.KW_IF);
        Expression expr = parseExpr();
        match(Kind.KW_THEN);
        Statement stmt = parseStmt();
        return new StatementIf(first, expr, stmt);
    }

    public StatementWhile parseWhileStmt() throws PLPException {
        IToken first = match(Kind.KW_WHILE);
        Expression expr = parseExpr();
        match(Kind.KW_DO);
        Statement stmt = parseStmt();
        return new StatementWhile(first, expr, stmt);
    }

    public StatementEmpty parseEmptyStmt() throws PLPException {
        return new StatementEmpty(match(Kind.EOF));
    }

    public Expression parseExpr() throws PLPException {
        IToken first = peek();
        Expression e0 = parseAdditiveExpr();
        if (isKind(Kind.LT) || isKind(Kind.GT) || isKind(Kind.EQ) || isKind(Kind.NEQ)
            || isKind(Kind.LE) || isKind(Kind.GE)) {
            IToken op = consume();
            Expression e1 = parseAdditiveExpr();
            return new ExpressionBinary(first, e0, op, e1);
        }
        return e0;
    }

    public Expression parseAdditiveExpr() throws PLPException {
        IToken first = peek();
        Expression e0 = parseMultiplicativeExpr();
        Expression e1;
        while (isKind(Kind.PLUS) || isKind(Kind.MINUS)) {
            IToken op = consume();
            e1 = parseMultiplicativeExpr();
            e0 = new ExpressionBinary(first, e0, op, e1);
        }
        return e0;
    }

    public Expression parseMultiplicativeExpr() throws PLPException {
        IToken first = peek();
        Expression e0 = parsePrimaryExpr();
        Expression e1;
        while (isKind(Kind.TIMES) || isKind(Kind.DIV) || isKind(Kind.MOD)) {
            IToken op = consume();
            e1 = parsePrimaryExpr();
            e0 = new ExpressionBinary(first, e0, op, e1);
        }
        return e0;
    }

    public Expression parsePrimaryExpr() throws PLPException {
        switch (peek().getKind()) {
            case IDENT:
                return parseIdentExpr();
            case BOOLEAN_LIT:
                return parseConstExpr();
            case STRING_LIT:
                return parseConstExpr();
            case NUM_LIT:
                return parseConstExpr();
            case LPAREN:
                return parseParenExpr();
            default:
                throw new SyntaxException("Invalid primary expression.");
        }
    }

    public Expression parseConstExpr() throws PLPException {
        switch (peek().getKind()) {
            case BOOLEAN_LIT:
                return parseBooleanExpr();
            case STRING_LIT:
                return parseStringExpr();
            case NUM_LIT:
                return parseNumberExpr();
            default:
                throw new SyntaxException("Invalid constant");
        }
    }

    public Expression parseParenExpr() throws PLPException {
        match(Kind.LPAREN);
        Expression expr = parseExpr();
        match(Kind.RPAREN);
        return expr;
    }

    public ExpressionIdent parseIdentExpr() throws PLPException {
        ExpressionIdent exp = new ExpressionIdent(match(Kind.IDENT));
        return exp;
    }

    public ExpressionBooleanLit parseBooleanExpr() throws PLPException {
        return new ExpressionBooleanLit(match(Kind.BOOLEAN_LIT));
    }

    public ExpressionNumLit parseNumberExpr() throws PLPException {
        return new ExpressionNumLit(match(Kind.NUM_LIT));
    }

    public ExpressionStringLit parseStringExpr() throws PLPException {
        return new ExpressionStringLit(match(Kind.STRING_LIT));
    }

    public Ident parseIdent() throws PLPException {
        return new Ident(match(Kind.IDENT));
    }

    private boolean isKind(Kind kind) throws PLPException {
        if (peek().getKind().equals(kind))
            return true;
        return false;
    }

    private IToken consume() throws PLPException {
        return lexer.next();
    }

    private IToken match(Kind kind) throws PLPException {
        if (!isKind(kind))
            throw new SyntaxException("Type " + kind + " required.");
        return consume();
    }

    private IToken peek() throws PLPException {
        return lexer.peek();
    }

}
