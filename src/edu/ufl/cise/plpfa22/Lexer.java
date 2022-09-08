package edu.ufl.cise.plpfa22;

public class Lexer implements ILexer{

    private char[] input;
    private int pos;
    private int len;
    private int line;
    private int col;

    public Lexer(String input) {
        this.input = input.toCharArray();
        this.pos = 0;
        this.line = 1;
        this.col = 1;
    }

    public IToken next() throws LexicalException {
        Token tok = null;
        while (tok == null && pos <= input.length) {
            if (pos == input.length) {
                return new Token(IToken.Kind.EOF, new char[]{}, line, col);
            }
            switch (input[pos]) {
                case '\n':
                    newLine();
                    break;
                case '.':
                    tok = new Token(IToken.Kind.DOT, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case ',':
                    tok = new Token(IToken.Kind.COMMA, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case ';':
                    tok = new Token(IToken.Kind.SEMI, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '"':
                    tok = new Token(IToken.Kind.QUOTE, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '(':
                    tok = new Token(IToken.Kind.LPAREN, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case ')':
                    tok = new Token(IToken.Kind.RPAREN, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '+':
                    tok = new Token(IToken.Kind.PLUS, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '-':
                    tok = new Token(IToken.Kind.MINUS, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '*':
                    tok = new Token(IToken.Kind.TIMES, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '/':
                    tok = new Token(IToken.Kind.DIV, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '%':
                    tok = new Token(IToken.Kind.MOD, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '?':
                    tok = new Token(IToken.Kind.QUESTION, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '!':
                    tok = new Token(IToken.Kind.BANG, new char[]{input[pos]}, line, col);
                    advance();
                    break;
            }
        }
        return tok;
    }

    public IToken peek() throws LexicalException {
        int tempPos = pos;
        int tempLine = line;
        int tempCol = col;
        Token tok = null;
        while (tok == null && tempPos <= input.length) {
            if (tempPos == input.length) {
                return new Token(IToken.Kind.EOF, new char[]{}, tempLine, tempCol);
            }
            switch (input[tempPos]) {
                case '\n':
                    tempPos++; tempCol = 1; tempLine++;
                    break;
                case '.':
                    tok = new Token(IToken.Kind.DOT, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case ',':
                    tok = new Token(IToken.Kind.COMMA, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case ';':
                    tok = new Token(IToken.Kind.SEMI, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '"':
                    tok = new Token(IToken.Kind.QUOTE, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '(':
                    tok = new Token(IToken.Kind.LPAREN, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case ')':
                    tok = new Token(IToken.Kind.RPAREN, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '+':
                    tok = new Token(IToken.Kind.PLUS, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '-':
                    tok = new Token(IToken.Kind.MINUS, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '*':
                    tok = new Token(IToken.Kind.TIMES, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '/':
                    tok = new Token(IToken.Kind.DIV, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '%':
                    tok = new Token(IToken.Kind.MOD, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '?':
                    tok = new Token(IToken.Kind.QUESTION, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;
                case '!':
                    tok = new Token(IToken.Kind.BANG, new char[]{input[tempPos]}, tempLine, tempCol);
                    tempPos++; tempCol++;
                    break;

            }
        }
        return tok;
    }

    private void advance() {
        this.pos++;
        this.col++;
    }

    private void newLine() {
        this.pos++;
        this.col = 1;
        this.line++;
    }
}
