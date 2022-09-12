package edu.ufl.cise.plpfa22;

import java.util.Arrays;

public class Token implements IToken{

    private Kind kind;
    private char[] text;
    private SourceLocation source;


    public Token(Kind kind, char[] text, int line, int col) {
        this.kind = kind;
        this.text = text;
        this.source = new SourceLocation(line, col);
    }

    public Kind getKind() {
        return kind;
    }

    public char[] getText() {
        return text;
    }

    public SourceLocation getSourceLocation() {
        return source;
    }

    public int getIntValue() {
        return Integer.parseInt(String.valueOf(text));
    }

    public boolean getBooleanValue() {
        return Boolean.parseBoolean(String.valueOf(text));
    }

    public String getStringValue() {
        String literal = "";
        for (int i = 1; i < text.length - 1; i++) {
            if (text[i] == '\\') {
                i++;
                literal += createEscape(text[i]);
            }
            else {
                literal += text[i];
            }
        }
        return String.valueOf(literal);
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
