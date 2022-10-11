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

    public Declaration lookup(IToken ident) {
        for (ConstDec con : consts)
            if (con.ident.equals(ident))
                return con;
        for (VarDec var : vars)
            if (var.ident.equals(ident))
                return var;
        for (ProcDec proc : procs)
            if (proc.ident.equals(ident))
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
