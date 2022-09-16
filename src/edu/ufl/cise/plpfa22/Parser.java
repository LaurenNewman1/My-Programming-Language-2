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
            return parseConst();
        }
        return new ExpressionBooleanLit(new Token(Kind.EOF, new char[]{' '}, 1, 1));
    }

    public Expression parseConst() throws PLPException {
        switch(lexer.peek().getKind()) {
            case BOOLEAN_LIT:
                return parseBoolean();
            case STRING_LIT:
                return parseString();
            case NUM_LIT:
                return parseNumber();
        }
        return new ExpressionBooleanLit(new Token(Kind.EOF, new char[]{' '}, 1, 1));
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
