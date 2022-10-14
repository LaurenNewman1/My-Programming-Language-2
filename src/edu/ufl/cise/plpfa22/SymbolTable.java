package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.ConstDec;
import edu.ufl.cise.plpfa22.ast.Declaration;
import edu.ufl.cise.plpfa22.ast.ProcDec;
import edu.ufl.cise.plpfa22.ast.VarDec;

import java.util.*;

public class SymbolTable {
    List<SymbolNode> nodes;
    Stack<Integer> scopeStack;
    int scopeId;
    int nest;

    public SymbolTable() {
        nodes = new ArrayList<>();
        scopeStack = new Stack<>();
        scopeId = 0;
        nest = 0;
    }

    public void init() {
        scopeId = 0;
        scopeStack.clear();
        scopeStack.push(scopeId);
        nest = 0;
    }

    public void enterScope() {
        scopeId++;
        scopeStack.push(scopeId);
        nest++;
    }

    public void closeScope() {
        scopeStack.pop();
        nest--;
    }

    public void newIdentifier(Declaration dec) throws PLPException {
        if (searchCurrScope(dec)) {
            throw new ScopeException("Identifier already exists in same scope.");
        }
        dec.setNest(nest);
        nodes.add(new SymbolNode(scopeId, dec));
    }

    public Declaration lookup(IToken ident) {
        List<SymbolNode> matches = new ArrayList<>();
        // find all matching idents in symbolTable
        for (SymbolNode entry : nodes) {
            Declaration dec = entry.getDec();
            String identStr = String.copyValueOf(ident.getText());
            if (getIdentText(dec).equals(identStr)) {
                matches.add(new SymbolNode(entry.getId(), dec));
            }
        }
        // get the one in the outermost scope
        // ignore ones in adjacent scopes
        Stack<Integer> tempScope = (Stack<Integer>)scopeStack.clone();
        while (!tempScope.empty()) {
            int id = tempScope.pop();
            for (SymbolNode match : matches)
                if (match.getId() == id)
                    return match.getDec();
        }
        // procedures are fine from anywhere
        if (!matches.isEmpty()) {
            return matches.get(matches.size() - 1).getDec();
        }
        return null;
    }

    public boolean searchCurrScope(Declaration dec) {
        if (!scopeStack.empty()) {
            int currId = scopeStack.peek();
            for (SymbolNode entry : nodes) {
                if (currId == (Integer) entry.getId()
                        && getIdentText(entry.getDec()).equals(getIdentText(dec))) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getIdentText(Declaration dec) {
        if (dec instanceof ConstDec)
            return String.copyValueOf(((ConstDec)dec).ident.getText());
        else if (dec instanceof VarDec)
            return String.copyValueOf(((VarDec)dec).ident.getText());
        else
            return String.copyValueOf(((ProcDec)dec).ident.getText());
    }

}

class SymbolNode {

    int scopeId;
    Declaration dec;

    public SymbolNode(int scopeId, Declaration dec) {
        this.scopeId = scopeId;
        this.dec = dec;
    }

    public int getId() { return scopeId; }

    public Declaration getDec() { return dec; }
}
