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
            tok = new Token(IToken.Kind.NUM_LIT, Arrays.copyOfRange(input, start, pos + 1), line, startCol);
        }
        // strings
        else if (pos + 1 < input.length && input[pos] == '"'
                && contains(Arrays.copyOfRange(input, pos + 1, input.length), '"')) {
            int start = col;
            int end = findIndex(input, pos + 1, '"');
            String literal = "";
            advance();
            while (pos < end) {
                if (input[pos] == '\\') {
                    advance();
                    if (pos == end || (input[pos] != 'b' && input[pos] != 't' && input[pos] != 'n'
                        && input[pos] != 'f' && input[pos] != 'r' && input[pos] != '"'
                        && input[pos] != '\'' && input[pos] != '\\')) {
                        throw new LexicalException("Slash followed by invalid character", line, col);
                    }
                    literal += createEscape(input[pos]);
                }
                else {
                    literal += input[pos];
                }
                advance();
            }
            advance();
            tok = new Token(IToken.Kind.STRING_LIT, literal.toCharArray(), line, start);
        }
        // booleans
        else if (pos + 3 < input.length && input[pos] == 'T'
                && input[pos + 1] == 'R' && input[pos + 2] == 'U' && input[pos + 3] == 'E') {
            tok = new Token(IToken.Kind.BOOLEAN_LIT, Arrays.copyOfRange(input, pos, pos + 4), line, col);
            advance(4);
        }
        else if (pos + 3 < input.length && input[pos] == 'F' && input[pos + 1] == 'A'
                && input[pos + 2] == 'L' && input[pos + 3] == 'S' && input[pos + 4] == 'E') {
            tok = new Token(IToken.Kind.BOOLEAN_LIT, Arrays.copyOfRange(input, pos, pos + 5), line, col);
            advance(5);
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
                    tok = new Token(IToken.Kind.ASSIGN, Arrays.copyOfRange(input, pos, pos + 2), line, col);
                    advance(2);
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
                        tok = new Token(IToken.Kind.LE, Arrays.copyOfRange(input, pos, pos + 2), line, col);
                        advance(2);
                    } else {
                        tok = new Token(IToken.Kind.LT, new char[]{input[pos]}, line, col);
                        advance();
                    }
                    break;
                case '>':
                    if (pos != input.length - 1 && input[pos + 1] == '=') {
                        tok = new Token(IToken.Kind.GE, Arrays.copyOfRange(input, pos, pos + 2), line, col);
                        advance(2);
                    } else {
                        tok = new Token(IToken.Kind.GT, new char[]{input[pos]}, line, col);
                        advance();
                    }
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
                    tok = new Token(IToken.Kind.ASSIGN, Arrays.copyOfRange(input, tempPos, tempPos + 2), tempLine, tempCol);
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

    private void advance(int steps) {
        this.pos += steps;
        this.col += steps;
    }

    private void newLine() {
        this.pos++;
        this.col = 1;
        this.line++;
    }

    private boolean contains(char[] arr, char c) {
        for (int element : arr) {
            if (element == c) {
                return true;
            }
        }
        return false;
    }

    private int findIndex(char[] arr, int i, char c) {
        for (; i < arr.length; i++) {
            if (arr[i] == c) {
                return i;
            }
        }
        return -1;
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

    private char createEscape(char c) {
        switch (c) {
            case 'b':
                return '\b';
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case 'f':
                return '\f';
            case 'r':
                return '\r';
            case '"':
                return '\"';
            case '\'':
                return '\'';
            case '\\':
                return '\\';
            default:
                return ' ';
        }
    }
}
