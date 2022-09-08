package edu.ufl.cise.plpfa22;

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
        return Integer.parseInt(text.toString());
    }

    public boolean getBooleanValue() {
        return Boolean.parseBoolean(text.toString());
    }

    public String getStringValue() {
        return text.toString();
    }
}
