package edu.ufl.cise.plpfa22;

import java.util.Arrays;

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
        // remove any spaces
        handleWhitespace();
        // check for end of input
        if (pos == input.length) {
            return new Token(IToken.Kind.EOF, new char[]{}, line, col);
        }
        // numbers
        if (Character.isDigit(input[pos])) {
            int start = pos;
            int startCol = col;
            while (pos != input.length && Character.isDigit(input[pos])) {
                advance();
            }
            tok = new Token(IToken.Kind.NUM_LIT, Arrays.copyOfRange(input, start, pos), line, startCol);
        }
        else {
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
                case ':':
                    if (input[pos + 1] != '=') {
                        throw new LexicalException("Colons must be follow by =", line, col);
                    }
                    tok = new Token(IToken.Kind.ASSIGN, Arrays.copyOfRange(input, pos, pos + 1), line, col);
                    advance();
                    advance();
                    break;
                case '=':
                    tok = new Token(IToken.Kind.EQ, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '#':
                    tok = new Token(IToken.Kind.NEQ, new char[]{input[pos]}, line, col);
                    advance();
                    break;
                case '<':
                    if (pos != input.length - 1 && input[pos + 1] == '=') {
                        tok = new Token(IToken.Kind.LE, Arrays.copyOfRange(input, pos, pos + 1), line, col);
                        advance();
                    } else {
                        tok = new Token(IToken.Kind.LT, new char[]{input[pos]}, line, col);
                    }
                    advance();
                    break;
                case '>':
                    if (pos != input.length - 1 && input[pos + 1] == '=') {
                        tok = new Token(IToken.Kind.GE, Arrays.copyOfRange(input, pos, pos + 1), line, col);
                        advance();
                    } else {
                        tok = new Token(IToken.Kind.GT, new char[]{input[pos]}, line, col);
                    }
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
                case ':':
                    if (input[tempPos + 1] != '=') {
                        throw new LexicalException("Colons must be follow by =", tempLine, tempCol);
                    }
                    tok = new Token(IToken.Kind.ASSIGN, Arrays.copyOfRange(input, tempPos, tempPos + 1), tempLine, tempCol);
                    tempPos++; tempCol++; tempPos++; tempCol++;
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

    private boolean isWhitespace(char c) {
        if (c == '\t' || c == '\r' || c == '\n') {
            return true;
        }
        return false;
    }

    private void handleWhitespace() {
        while (pos != input.length
                && (input[pos] == '\t' || input[pos] == '\r' || input[pos] == '\n' || input[pos] == ' ')) {
            switch (input[pos]) {
                case '\t':
                    pos++; col += 8;
                    break;
                case '\n':
                    pos++; col = 1; line++;
                    break;
                case '\r':
                    pos += 2; col += 2;
                    break;
                case ' ':
                    pos++; col++;
                    break;
            }
        }
    }
}
