package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;
import edu.ufl.cise.plpfa22.IToken.Kind;
import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser{

    private ILexer lexer;
    private List<IToken> tokens;
    private int index;

    public Parser(ILexer lexer) {
        this.lexer = lexer;
        this.tokens = new ArrayList<>();
        this.index = 0;
    }
    public ASTNode parse() throws PLPException {
        // lex
        while (!lexer.peek().getKind().equals(Kind.EOF))
            tokens.add(lexer.next());
        return parseProgram();
    }

    public Program parseProgram() throws SyntaxException {
        IToken first = peek();
        Block block = parseBlock();
        match(Kind.DOT);
        if (index < tokens.size()) {
            throw new SyntaxException("Program cannot continue after dot.");
        }
        return new Program(first, block);
    }

    public Block parseBlock() throws SyntaxException {
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

    public List<ConstDec> parseConstDec() throws SyntaxException {
        List<ConstDec> consts = new ArrayList<>();
        // discard CONST
        IToken first = match(Kind.KW_CONST);
        IToken ident = match(Kind.IDENT);
        match(Kind.EQ);
        Expression constVal = parseConstExpr();
        Object constObj = parseObject(constVal);
        consts.add(new ConstDec(first, ident, constObj));
        while (isKind(Kind.COMMA)) {
            match(Kind.COMMA);
            IToken identNext = match(Kind.IDENT);
            match(Kind.EQ);
            Expression constValNext = parseConstExpr();
            Object constObjNext = parseObject(constValNext);
            consts.add(new ConstDec(first, identNext, constObjNext));
        }
        match(Kind.SEMI);
        return consts;
    }

    public Object parseObject(Expression expr) throws SyntaxException {
        return isKind(expr.getFirstToken(), Kind.NUM_LIT) ? expr.getFirstToken().getIntValue()
                : isKind(expr.getFirstToken(), Kind.STRING_LIT)? expr.getFirstToken().getStringValue()
                : expr.getFirstToken().getBooleanValue();
    }

    public List<VarDec> parseVarDec() throws SyntaxException {
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

    public ProcDec parseProcDec() throws SyntaxException {
        IToken first = match(Kind.KW_PROCEDURE);
        IToken ident = match(Kind.IDENT);
        match(Kind.SEMI);
        Block block = parseBlock();
        match(Kind.SEMI);
        return new ProcDec(first, ident, block);
    }

    public Statement parseStmt() throws SyntaxException {
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
                return parseEmptyStmt();
            case SEMI:
                return parseEmptyStmt();
            default:
                throw new SyntaxException("Invalid statement");
        }
    }

    public StatementAssign parseAssignStmt() throws SyntaxException {
        IToken first = peek();
        Ident ident = parseIdent();
        match(Kind.ASSIGN);
        Expression expr = parseExpr();
        return new StatementAssign(first, ident, expr);
    }

    public StatementCall parseCallStmt() throws SyntaxException {
        IToken first = match(Kind.KW_CALL);
        Ident ident = parseIdent();
        return new StatementCall(first, ident);
    }

    public StatementInput parseInputStmt() throws SyntaxException {
        IToken first = match(Kind.QUESTION);
        Ident ident = parseIdent();
        return new StatementInput(first, ident);
    }

    public StatementOutput parseOutputStmt() throws SyntaxException {
        IToken first = match(Kind.BANG);
        Expression expr = parseExpr();
        return new StatementOutput(first, expr);
    }

    public StatementBlock parseBlockStmt() throws SyntaxException {
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

    public StatementIf parseIfStmt() throws SyntaxException {
        IToken first = match(Kind.KW_IF);
        Expression expr = parseExpr();
        match(Kind.KW_THEN);
        Statement stmt = parseStmt();
        return new StatementIf(first, expr, stmt);
    }

    public StatementWhile parseWhileStmt() throws SyntaxException {
        IToken first = match(Kind.KW_WHILE);
        Expression expr = parseExpr();
        match(Kind.KW_DO);
        Statement stmt = parseStmt();
        return new StatementWhile(first, expr, stmt);
    }

    public StatementEmpty parseEmptyStmt() throws SyntaxException{
        // don't remove DOT; handled later
        return new StatementEmpty(peek());
    }

    public Expression parseExpr() throws SyntaxException {
        IToken first = peek();
        Expression e0 = parseAdditiveExpr();
        Expression e1;
        while (isKind(Kind.LT) || isKind(Kind.GT) || isKind(Kind.EQ) || isKind(Kind.NEQ)
            || isKind(Kind.LE) || isKind(Kind.GE)) {
            IToken op = consume();
            e1 = parseAdditiveExpr();
            e0 = new ExpressionBinary(first, e0, op, e1);
        }
        return e0;
    }

    public Expression parseAdditiveExpr() throws SyntaxException {
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

    public Expression parseMultiplicativeExpr() throws SyntaxException {
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

    public Expression parsePrimaryExpr() throws SyntaxException {
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

    public Expression parseConstExpr() throws SyntaxException {
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

    public Expression parseParenExpr() throws SyntaxException {
        match(Kind.LPAREN);
        Expression expr = parseExpr();
        match(Kind.RPAREN);
        return expr;
    }

    public ExpressionIdent parseIdentExpr() throws SyntaxException {
        ExpressionIdent exp = new ExpressionIdent(match(Kind.IDENT));
        return exp;
    }

    public ExpressionBooleanLit parseBooleanExpr() throws SyntaxException {
        return new ExpressionBooleanLit(match(Kind.BOOLEAN_LIT));
    }

    public ExpressionNumLit parseNumberExpr() throws SyntaxException {
        return new ExpressionNumLit(match(Kind.NUM_LIT));
    }

    public ExpressionStringLit parseStringExpr() throws SyntaxException {
        return new ExpressionStringLit(match(Kind.STRING_LIT));
    }

    public Ident parseIdent() throws SyntaxException {
        return new Ident(match(Kind.IDENT));
    }

    private boolean isKind(Kind kind) throws SyntaxException {
        if (peek().getKind().equals(kind))
            return true;
        return false;
    }

    private boolean isKind(IToken token, Kind kind) throws SyntaxException {
        if (token.getKind().equals(kind))
            return true;
        return false;
    }

    private IToken consume() {
        IToken next = tokens.get(index);
        index++;
        return next;
    }

    private IToken match(Kind kind) throws SyntaxException {
        if (index >= tokens.size()) {
            throw new SyntaxException("Invalid end of statement.");
        }
        else if (!isKind(kind))
            throw new SyntaxException("Type " + kind + " required.");
        return consume();
    }

    private IToken peek() throws SyntaxException {
        if (index >= tokens.size()) {
            throw new SyntaxException("Invalid end of statement.");
        }
        return tokens.get(index);
    }

}
