package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.IToken;
import edu.ufl.cise.plpfa22.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Scope {

    int id;
    int nest;
    List<ConstDec> consts;
    List<VarDec> vars;
    List<ProcDec> procs;


    public Scope(int id, int nest) {
        this.id = id;
        this.nest = nest;
        this.consts = new ArrayList<>();
        this.vars = new ArrayList<>();
        this.procs = new ArrayList<>();
    }

    // Copy constructor
    public Scope(Scope scope) {
        this.id = scope.id;
        this.nest = scope.nest;
        this.consts = new ArrayList<>(scope.consts);
        this.vars = new ArrayList<>(scope.vars);
        this.procs = new ArrayList<>(scope.procs);
    }

    public Declaration lookup(IToken ident) {
        for (ConstDec con : consts) {
            if (String.copyValueOf(con.ident.getText()).equals(String.copyValueOf(ident.getText())))
                return con;
        }
        for (VarDec var : vars)
            if (String.copyValueOf(var.ident.getText()).equals(String.copyValueOf(ident.getText())))
                return var;
        for (ProcDec proc : procs)
            if (String.copyValueOf(proc.ident.getText()).equals(String.copyValueOf(ident.getText())))
                return proc;
        return null;
    }

    public void addConst(ConstDec con) {
        consts.add(con);
    }

    public void addVar(VarDec var) {
        vars.add(var);
    }

    public void addProc(ProcDec proc) {
        procs.add(proc);
    }
}
