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
        IToken first = lexer.peek();
        Block block = parseBlock();
        if (!lexer.peek().getKind().equals(Kind.DOT)) {
            throw new SyntaxException("Missing . at the end of program.");
        }
        // discard .
        lexer.next();
        return new Program(first, block);
    }

    public Block parseBlock() throws PLPException {
        List<ConstDec> consts = new ArrayList<>();
        List<VarDec> vars = new ArrayList<>();
        List<ProcDec> procedures = new ArrayList<>();
        IToken first = lexer.peek();
        while (lexer.peek().getKind().equals(Kind.KW_CONST)) {
            consts.addAll(parseConstDec());
        }
        while (lexer.peek().getKind().equals(Kind.KW_VAR)) {
            vars.addAll(parseVarDec());
        }
        while (lexer.peek().getKind().equals(Kind.KW_PROCEDURE)) {
            procedures.add(parseProcDec());
        }
        Statement stmt = parseStmt();
        return new Block(first, consts, vars, procedures, stmt);
    }

    public List<ConstDec> parseConstDec() throws PLPException {
        List<ConstDec> consts = new ArrayList<>();
        // discard CONST
        IToken first = lexer.next();
        if (!lexer.peek().getKind().equals(Kind.IDENT)) {
            throw new SyntaxException("Identifier type required in const declaration.");
        }
        IToken ident = lexer.next();
        if (!lexer.peek().getKind().equals(Kind.EQ)) {
            throw new SyntaxException("Missing equal in const declaration.");
        }
        // discard =
        lexer.next();
        Expression constVal = parseConstExpr();
        consts.add(new ConstDec(first, ident, constVal));
        while (lexer.peek().getKind().equals(Kind.COMMA)) {
            // discard ,
            lexer.next();
            if (!lexer.peek().getKind().equals(Kind.IDENT)) {
                throw new SyntaxException("Identifier type required in variable declaration.");
            }
            IToken identNext = lexer.next();
            if (!lexer.peek().getKind().equals(Kind.EQ)) {
                throw new SyntaxException("Missing equal in const declaration.");
            }
            // discard =
            lexer.next();
            Expression constValNext = parseConstExpr();
            consts.add(new ConstDec(first, identNext, constValNext));
        }
        if (!lexer.peek().getKind().equals(Kind.SEMI)) {
            throw new SyntaxException("Missing semicolon in const declaration.");
        }
        // discard ;
        lexer.next();
        return consts;
    }

    public List<VarDec> parseVarDec() throws PLPException {
        List<VarDec> vars = new ArrayList<>();
        // discard VAR
        IToken first = lexer.next();
        if (!lexer.peek().getKind().equals(Kind.IDENT)) {
            throw new SyntaxException("Identifier type required in variable declaration.");
        }
        vars.add(new VarDec(lexer.peek(), lexer.next()));
        while (lexer.peek().getKind().equals(Kind.COMMA)) {
            // discard ,
            lexer.next();
            if (!lexer.peek().getKind().equals(Kind.IDENT)) {
                throw new SyntaxException("Identifier type required in variable declaration.");
            }
            vars.add(new VarDec(lexer.peek(), lexer.next()));
        }
        if (!lexer.peek().getKind().equals(Kind.SEMI)) {
            throw new SyntaxException("Missing semicolon in variable declaration.");
        }
        // discard ;
        lexer.next();
        return vars;
    }

    public ProcDec parseProcDec() throws PLPException {
        // discard PROCEDURE
        IToken first = lexer.next();
        if (!lexer.peek().getKind().equals(Kind.IDENT)) {
            throw new SyntaxException("Identifier type required in procedure declaration.");
        }
        IToken ident = lexer.next();
        if (!lexer.peek().getKind().equals(Kind.SEMI)) {
            throw new SyntaxException("Missing semicolon in procedure declaration.");
        }
        // discard ;
        lexer.next();
        Block block = parseBlock();
        if (!lexer.peek().getKind().equals(Kind.SEMI)) {
            throw new SyntaxException("Missing semicolon in procedure declaration.");
        }
        // discard ;
        lexer.next();
        return new ProcDec(first, ident, block);
    }

    public Statement parseStmt() throws PLPException {
        switch (lexer.peek().getKind()) {
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
                return new StatementEmpty(lexer.peek());
            case EOF:
                return parseEmptyStmt();
            default:
                throw new SyntaxException("Invalid statement");
        }
    }

    public StatementAssign parseAssignStmt() throws PLPException {
        IToken first = lexer.peek();
        Ident ident = parseIdent();
        // discard :=
        if (!lexer.peek().getKind().equals(Kind.ASSIGN)) {
            throw new SyntaxException("Missing assignment operator");
        }
        lexer.next();
        Expression expr = parseExpr();
        return new StatementAssign(first, ident, expr);
    }

    public StatementCall parseCallStmt() throws PLPException {
        // discard CALL
        IToken first = lexer.next();
        if (!lexer.peek().getKind().equals(Kind.IDENT)) {
            throw new SyntaxException("Identifier type required in call statement.");
        }
        Ident ident = (Ident)lexer.next();
        return new StatementCall(first, ident);
    }

    public StatementInput parseInputStmt() throws PLPException {
        // discard ?
        IToken first = lexer.next();
        if (!lexer.peek().getKind().equals(Kind.IDENT)) {
            throw new SyntaxException("Identifier type required in input statement.");
        }
        Ident ident = (Ident)lexer.next();
        return new StatementInput(first, ident);
    }

    public StatementOutput parseOutputStmt() throws PLPException {
        // discard !
        IToken first = lexer.next();
        Expression expr = parseExpr();
        return new StatementOutput(first, expr);
    }

    public StatementBlock parseBlockStmt() throws PLPException {
        // discard BEGIN
        IToken first = lexer.next();
        List<Statement> stmts = new ArrayList<>();
        stmts.add(parseStmt());
        while (lexer.peek().getKind().equals(Kind.SEMI)) {
            // discard ;
            lexer.next();
            stmts.add(parseStmt());
        }
        // discard END
        IToken temp = lexer.peek();
        if (!lexer.peek().getKind().equals(Kind.KW_END)) {
            throw new SyntaxException("Missing END in BEGIN statement.");
        }
        lexer.next();
        return new StatementBlock(first, stmts);
    }

    public StatementIf parseIfStmt() throws PLPException {
        // discard IF
        IToken first = lexer.next();
        Expression expr = parseExpr();
        // discard DO
        if (!lexer.peek().getKind().equals(Kind.KW_THEN)) {
            throw new SyntaxException("Missing THEN in IF statement.");
        }
        lexer.next();
        Statement stmt = parseStmt();
        return new StatementIf(first, expr, stmt);
    }

    public StatementWhile parseWhileStmt() throws PLPException {
        // discard WHILE
        IToken first = lexer.next();
        Expression expr = parseExpr();
        // discard DO
        if (!lexer.peek().getKind().equals(Kind.KW_DO)) {
            throw new SyntaxException("Missing DO in WHILE statement.");
        }
        lexer.next();
        Statement stmt = parseStmt();
        return new StatementWhile(first, expr, stmt);
    }

    public StatementEmpty parseEmptyStmt() throws PLPException {
        return new StatementEmpty(lexer.next());
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
        switch (lexer.peek().getKind()) {
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
        // discard parenthesis
        lexer.next();
        Expression expr = parseExpr();
        if (!lexer.peek().getKind().equals(Kind.RPAREN)) {
            throw new SyntaxException("Missing closing parenthesis");
        }
        lexer.next();
        return expr;
    }

    public ExpressionIdent parseIdentExpr() throws PLPException {
        ExpressionIdent exp = new ExpressionIdent(lexer.next());
        return exp;
    }

    public ExpressionBooleanLit parseBooleanExpr() throws PLPException {
        return new ExpressionBooleanLit(lexer.next());
    }

    public ExpressionNumLit parseNumberExpr() throws PLPException {
        return new ExpressionNumLit(lexer.next());
    }

    public ExpressionStringLit parseStringExpr() throws PLPException {
        return new ExpressionStringLit(lexer.next());
    }

    public Ident parseIdent() throws PLPException {
        if (!lexer.peek().getKind().equals(Kind.IDENT)) {
            throw new SyntaxException("Required type identifier");
        }
        return new Ident(lexer.next());
    }

}
