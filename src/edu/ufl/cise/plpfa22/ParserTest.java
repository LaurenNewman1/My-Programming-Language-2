package edu.ufl.cise.plpfa22;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import edu.ufl.cise.plpfa22.IToken.Kind;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plpfa22.ast.ASTNode;
import edu.ufl.cise.plpfa22.ast.Block;
import edu.ufl.cise.plpfa22.ast.ConstDec;
import edu.ufl.cise.plpfa22.ast.Expression;
import edu.ufl.cise.plpfa22.ast.ExpressionBinary;
import edu.ufl.cise.plpfa22.ast.ExpressionBooleanLit;
import edu.ufl.cise.plpfa22.ast.ExpressionIdent;
import edu.ufl.cise.plpfa22.ast.ExpressionNumLit;
import edu.ufl.cise.plpfa22.ast.ExpressionStringLit;
import edu.ufl.cise.plpfa22.ast.Ident;
import edu.ufl.cise.plpfa22.ast.ProcDec;
import edu.ufl.cise.plpfa22.ast.Program;
import edu.ufl.cise.plpfa22.ast.Statement;
import edu.ufl.cise.plpfa22.ast.StatementAssign;
import edu.ufl.cise.plpfa22.ast.StatementBlock;
import edu.ufl.cise.plpfa22.ast.StatementCall;
import edu.ufl.cise.plpfa22.ast.StatementEmpty;
import edu.ufl.cise.plpfa22.ast.StatementIf;
import edu.ufl.cise.plpfa22.ast.StatementInput;
import edu.ufl.cise.plpfa22.ast.StatementOutput;
import edu.ufl.cise.plpfa22.ast.StatementWhile;
import edu.ufl.cise.plpfa22.ast.VarDec;

class ParserTest {

	ASTNode getAST(String input) throws PLPException {
		IParser parser = CompilerComponentFactory.getParser(CompilerComponentFactory.getLexer(input));
		return parser.parse();
	}

	void checkToken(IToken t, Kind expectedKind, int expectedLine, int expectedColumn){
		assertEquals(expectedKind, t.getKind());
		assertEquals(new IToken.SourceLocation(expectedLine,expectedColumn), t.getSourceLocation());
	}

	void checkAST(ASTNode ast, Class type, Token first) {
		assertThat(ast, instanceOf(type));
		checkToken(ast.firstToken, first.getKind(), first.getSourceLocation().line(), first.getSourceLocation().column());
	}

//	@Test
//	void testExpression() throws PLPException {
//		String input = """
//						a * b
//						""";
//		ASTNode ast = getAST(input);
//		assertThat(ast, instanceOf(ExpressionBinary.class));
//		checkToken(ast.firstToken, Kind.IDENT,1, 1);
//		assertThat(((ExpressionBinary)ast).e0, instanceOf(ExpressionIdent.class));
//		checkToken(((ExpressionBinary)ast).op, Kind.TIMES,1, 3);
//		assertThat(((ExpressionBinary)ast).e1, instanceOf(ExpressionIdent.class));
//	}

	@Test
//shortest legal program
	void test0() throws PLPException {
		String input = """
				.""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementEmpty.class));
	}

	@Test
	void test1() throws PLPException {
		String input = """
				! 0 .""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementOutput.class));
		Expression v5 = ((StatementOutput) v4).expression;
		assertThat("", v5, instanceOf(ExpressionNumLit.class));
		IToken v6 = ((ExpressionNumLit) v5).firstToken;
		assertEquals("0", String.valueOf(v6.getText()));
	}

	@Test
	void test2() throws PLPException {
		String input = """
				! "hello" .""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementOutput.class));
		Expression v5 = ((StatementOutput) v4).expression;
		assertThat("", v5, instanceOf(ExpressionStringLit.class));
		IToken v6 = ((ExpressionStringLit) v5).firstToken;
		assertEquals("hello", v6.getStringValue());
	}

	@Test
	void test3() throws PLPException {
		String input = """
				! TRUE .""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementOutput.class));
		Expression v5 = ((StatementOutput) v4).expression;
		assertThat("", v5, instanceOf(ExpressionBooleanLit.class));
		IToken v6 = ((ExpressionBooleanLit) v5).firstToken;
		assertEquals("TRUE", String.valueOf(v6.getText()));
	}

	@Test
	void test4() throws PLPException {
		String input = """
				! abc
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementOutput.class));
		Expression v5 = ((StatementOutput) v4).expression;
		assertThat("", v5, instanceOf(ExpressionIdent.class));
		IToken v6 = ((ExpressionIdent) v5).firstToken;
		assertEquals("abc", String.valueOf(v6.getText()));
	}

	@Test
	void test5() throws PLPException {
		String input = """
				VAR abc;
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(1, v2.size());
		assertThat("", v2.get(0), instanceOf(VarDec.class));
		IToken v3 = ((VarDec) v2.get(0)).ident;
		assertEquals("abc", String.valueOf(v3.getText()));
		List<ProcDec> v4 = ((Block) v0).procedureDecs;
		assertEquals(0, v4.size());
		Statement v5 = ((Block) v0).statement;
		assertThat("", v5, instanceOf(StatementEmpty.class));
	}

	@Test
	void test6() throws PLPException {
		String input = """
				BEGIN
				! "hello";
				! TRUE;
				!  33 ;
				! variable
				END
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementBlock.class));
		List<Statement> v5 = ((StatementBlock) v4).statements;
		assertThat("", v5.get(0), instanceOf(StatementOutput.class));
		Expression v6 = ((StatementOutput) v5.get(0)).expression;
		assertThat("", v6, instanceOf(ExpressionStringLit.class));
		IToken v7 = ((ExpressionStringLit) v6).firstToken;
		assertEquals("hello", v7.getStringValue());
		assertThat("", v5.get(1), instanceOf(StatementOutput.class));
		Expression v8 = ((StatementOutput) v5.get(1)).expression;
		assertThat("", v8, instanceOf(ExpressionBooleanLit.class));
		IToken v9 = ((ExpressionBooleanLit) v8).firstToken;
		assertEquals("TRUE", String.valueOf(v9.getText()));
		assertThat("", v5.get(2), instanceOf(StatementOutput.class));
		Expression v10 = ((StatementOutput) v5.get(2)).expression;
		assertThat("", v10, instanceOf(ExpressionNumLit.class));
		IToken v11 = ((ExpressionNumLit) v10).firstToken;
		assertEquals("33", String.valueOf(v11.getText()));
		assertThat("", v5.get(3), instanceOf(StatementOutput.class));
		Expression v12 = ((StatementOutput) v5.get(3)).expression;
		assertThat("", v12, instanceOf(ExpressionIdent.class));
		IToken v13 = ((ExpressionIdent) v12).firstToken;
		assertEquals("variable", String.valueOf(v13.getText()));
	}

	@Test
	void test7() throws PLPException {
		String input = """
				BEGIN
				? abc;
				! variable
				END
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementBlock.class));
		List<Statement> v5 = ((StatementBlock) v4).statements;
		assertThat("", v5.get(0), instanceOf(StatementInput.class));
		Ident v6 = ((StatementInput) v5.get(0)).ident;
		assertEquals("abc", String.valueOf(v6.getText()));
		assertThat("", v5.get(1), instanceOf(StatementOutput.class));
		Expression v7 = ((StatementOutput) v5.get(1)).expression;
		assertThat("", v7, instanceOf(ExpressionIdent.class));
		IToken v8 = ((ExpressionIdent) v7).firstToken;
		assertEquals("variable", String.valueOf(v8.getText()));
	}

	@Test
	void test8() throws PLPException {
		String input = """
				CONST a = 3, b = TRUE, c = "hello";
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(3, v1.size());
		assertThat("", v1.get(0), instanceOf(ConstDec.class));
		IToken v2 = ((ConstDec) v1.get(0)).ident;
		assertEquals("a", String.valueOf(v2.getText()));
		Integer v3 = (Integer) ((ConstDec) v1.get(0)).val;
		assertEquals(3, v3);
		assertThat("", v1.get(1), instanceOf(ConstDec.class));
		IToken v4 = ((ConstDec) v1.get(1)).ident;
		assertEquals("b", String.valueOf(v4.getText()));
		Boolean v5 = (Boolean) ((ConstDec) v1.get(1)).val;
		assertEquals(true, v5);
		assertThat("", v1.get(2), instanceOf(ConstDec.class));
		IToken v6 = ((ConstDec) v1.get(2)).ident;
		assertEquals("c", String.valueOf(v6.getText()));
		String v7 = (String) ((ConstDec) v1.get(2)).val;
		assertEquals("hello", v7);
		List<VarDec> v8 = ((Block) v0).varDecs;
		assertEquals(0, v8.size());
		List<ProcDec> v9 = ((Block) v0).procedureDecs;
		assertEquals(0, v9.size());
		Statement v10 = ((Block) v0).statement;
		assertThat("", v10, instanceOf(StatementEmpty.class));
	}

	@Test
	void test9() throws PLPException {
		String input = """
				BEGIN
				x := 3;
				y := "hello";
				b := FALSE
				END
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementBlock.class));
		List<Statement> v5 = ((StatementBlock) v4).statements;
		assertThat("", v5.get(0), instanceOf(StatementAssign.class));
		Ident v6 = ((StatementAssign) v5.get(0)).ident;
		assertEquals("x", String.valueOf(v6.getText()));
		Expression v7 = ((StatementAssign) v5.get(0)).expression;
		assertThat("", v7, instanceOf(ExpressionNumLit.class));
		IToken v8 = ((ExpressionNumLit) v7).firstToken;
		assertEquals("3", String.valueOf(v8.getText()));
		assertThat("", v5.get(1), instanceOf(StatementAssign.class));
		Ident v9 = ((StatementAssign) v5.get(1)).ident;
		assertEquals("y", String.valueOf(v9.getText()));
		Expression v10 = ((StatementAssign) v5.get(1)).expression;
		assertThat("", v10, instanceOf(ExpressionStringLit.class));
		IToken v11 = ((ExpressionStringLit) v10).firstToken;
		assertEquals("hello", v11.getStringValue());
		assertThat("", v5.get(2), instanceOf(StatementAssign.class));
		Ident v12 = ((StatementAssign) v5.get(2)).ident;
		assertEquals("b", String.valueOf(v12.getText()));
		Expression v13 = ((StatementAssign) v5.get(2)).expression;
		assertThat("", v13, instanceOf(ExpressionBooleanLit.class));
		IToken v14 = ((ExpressionBooleanLit) v13).firstToken;
		assertEquals("FALSE", String.valueOf(v14.getText()));
	}

	@Test
	void test10() throws PLPException {
		String input = """
				BEGIN
				CALL x
				END
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(0, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(0, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(0, v3.size());
		Statement v4 = ((Block) v0).statement;
		assertThat("", v4, instanceOf(StatementBlock.class));
		List<Statement> v5 = ((StatementBlock) v4).statements;
		assertThat("", v5.get(0), instanceOf(StatementCall.class));
		Ident v6 = ((StatementCall) v5.get(0)).ident;
		assertEquals("x", String.valueOf(v6.getText()));
	}

	@Test
	void test11() throws PLPException {
		String input = """
				CONST a=3;
				VAR x,y,z;
				PROCEDURE p;
				  VAR j;
				  BEGIN
				     ? x;
				     IF x = 0 THEN ! y ;
				     WHILE j < 24 DO CALL z
				  END;
				! z
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(1, v1.size());
		assertThat("", v1.get(0), instanceOf(ConstDec.class));
		IToken v2 = ((ConstDec) v1.get(0)).ident;
		assertEquals("a", String.valueOf(v2.getText()));
		Integer v3 = (Integer) ((ConstDec) v1.get(0)).val;
		assertEquals(3, v3);
		List<VarDec> v4 = ((Block) v0).varDecs;
		assertEquals(3, v4.size());
		assertThat("", v4.get(0), instanceOf(VarDec.class));
		IToken v5 = ((VarDec) v4.get(0)).ident;
		assertEquals("x", String.valueOf(v5.getText()));
		assertThat("", v4.get(1), instanceOf(VarDec.class));
		IToken v6 = ((VarDec) v4.get(1)).ident;
		assertEquals("y", String.valueOf(v6.getText()));
		assertThat("", v4.get(2), instanceOf(VarDec.class));
		IToken v7 = ((VarDec) v4.get(2)).ident;
		assertEquals("z", String.valueOf(v7.getText()));
		List<ProcDec> v8 = ((Block) v0).procedureDecs;
		assertEquals(1, v8.size());
		assertThat("", v8.get(0), instanceOf(ProcDec.class));
		IToken v9 = ((ProcDec) v8.get(0)).ident;
		assertEquals("p", String.valueOf(v9.getText()));
		Block v10 = ((ProcDec) v8.get(0)).block;
		assertThat("", v10, instanceOf(Block.class));
		List<ConstDec> v11 = ((Block) v10).constDecs;
		assertEquals(0, v11.size());
		List<VarDec> v12 = ((Block) v10).varDecs;
		assertEquals(1, v12.size());
		assertThat("", v12.get(0), instanceOf(VarDec.class));
		IToken v13 = ((VarDec) v12.get(0)).ident;
		assertEquals("j", String.valueOf(v13.getText()));
		List<ProcDec> v14 = ((Block) v10).procedureDecs;
		assertEquals(0, v14.size());
		Statement v15 = ((Block) v10).statement;
		assertThat("", v15, instanceOf(StatementBlock.class));
		List<Statement> v16 = ((StatementBlock) v15).statements;
		assertThat("", v16.get(0), instanceOf(StatementInput.class));
		Ident v17 = ((StatementInput) v16.get(0)).ident;
		assertEquals("x", String.valueOf(v17.getText()));
		assertThat("", v16.get(1), instanceOf(StatementIf.class));
		Expression v18 = ((StatementIf) v16.get(1)).expression;
		assertThat("", v18, instanceOf(ExpressionBinary.class));
		Expression v19 = ((ExpressionBinary) v18).e0;
		assertThat("", v19, instanceOf(ExpressionIdent.class));
		IToken v20 = ((ExpressionIdent) v19).firstToken;
		assertEquals("x", String.valueOf(v20.getText()));
		Expression v21 = ((ExpressionBinary) v18).e1;
		assertThat("", v21, instanceOf(ExpressionNumLit.class));
		IToken v22 = ((ExpressionNumLit) v21).firstToken;
		assertEquals("0", String.valueOf(v22.getText()));
		IToken v23 = ((ExpressionBinary) v18).op;
		assertEquals("=", String.valueOf(v23.getText()));
		Statement v24 = ((StatementIf) v16.get(1)).statement;
		assertThat("", v24, instanceOf(StatementOutput.class));
		Expression v25 = ((StatementOutput) v24).expression;
		assertThat("", v25, instanceOf(ExpressionIdent.class));
		IToken v26 = ((ExpressionIdent) v25).firstToken;
		assertEquals("y", String.valueOf(v26.getText()));
		assertThat("", v16.get(2), instanceOf(StatementWhile.class));
		Expression v27 = ((StatementWhile) v16.get(2)).expression;
		assertThat("", v27, instanceOf(ExpressionBinary.class));
		Expression v28 = ((ExpressionBinary) v27).e0;
		assertThat("", v28, instanceOf(ExpressionIdent.class));
		IToken v29 = ((ExpressionIdent) v28).firstToken;
		assertEquals("j", String.valueOf(v29.getText()));
		Expression v30 = ((ExpressionBinary) v27).e1;
		assertThat("", v30, instanceOf(ExpressionNumLit.class));
		IToken v31 = ((ExpressionNumLit) v30).firstToken;
		assertEquals("24", String.valueOf(v31.getText()));
		IToken v32 = ((ExpressionBinary) v27).op;
		assertEquals("<", String.valueOf(v32.getText()));
		Statement v33 = ((StatementWhile) v16.get(2)).statement;
		assertThat("", v33, instanceOf(StatementCall.class));
		Ident v34 = ((StatementCall) v33).ident;
		assertEquals("z", String.valueOf(v34.getText()));
		Statement v35 = ((Block) v0).statement;
		assertThat("", v35, instanceOf(StatementOutput.class));
		Expression v36 = ((StatementOutput) v35).expression;
		assertThat("", v36, instanceOf(ExpressionIdent.class));
		IToken v37 = ((ExpressionIdent) v36).firstToken;
		assertEquals("z", String.valueOf(v37.getText()));
	}

	@Test
	void test12() throws PLPException {
		String input = """
				CONST a=3;
				VAR x,y,z;
				PROCEDURE p;
				  VAR j;
				  BEGIN
				     ? x;
				     IF x = 0 THEN ! y ;
				     WHILE j < 24 DO CALL z
				  END;
				! a+b - (c/e) * 35/(3+4)
				.
				""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(1, v1.size());
		assertThat("", v1.get(0), instanceOf(ConstDec.class));
		IToken v2 = ((ConstDec) v1.get(0)).ident;
		assertEquals("a", String.valueOf(v2.getText()));
		Integer v3 = (Integer) ((ConstDec) v1.get(0)).val;
		assertEquals(3, v3);
		List<VarDec> v4 = ((Block) v0).varDecs;
		assertEquals(3, v4.size());
		assertThat("", v4.get(0), instanceOf(VarDec.class));
		IToken v5 = ((VarDec) v4.get(0)).ident;
		assertEquals("x", String.valueOf(v5.getText()));
		assertThat("", v4.get(1), instanceOf(VarDec.class));
		IToken v6 = ((VarDec) v4.get(1)).ident;
		assertEquals("y", String.valueOf(v6.getText()));
		assertThat("", v4.get(2), instanceOf(VarDec.class));
		IToken v7 = ((VarDec) v4.get(2)).ident;
		assertEquals("z", String.valueOf(v7.getText()));
		List<ProcDec> v8 = ((Block) v0).procedureDecs;
		assertEquals(1, v8.size());
		assertThat("", v8.get(0), instanceOf(ProcDec.class));
		IToken v9 = ((ProcDec) v8.get(0)).ident;
		assertEquals("p", String.valueOf(v9.getText()));
		Block v10 = ((ProcDec) v8.get(0)).block;
		assertThat("", v10, instanceOf(Block.class));
		List<ConstDec> v11 = ((Block) v10).constDecs;
		assertEquals(0, v11.size());
		List<VarDec> v12 = ((Block) v10).varDecs;
		assertEquals(1, v12.size());
		assertThat("", v12.get(0), instanceOf(VarDec.class));
		IToken v13 = ((VarDec) v12.get(0)).ident;
		assertEquals("j", String.valueOf(v13.getText()));
		List<ProcDec> v14 = ((Block) v10).procedureDecs;
		assertEquals(0, v14.size());
		Statement v15 = ((Block) v10).statement;
		assertThat("", v15, instanceOf(StatementBlock.class));
		List<Statement> v16 = ((StatementBlock) v15).statements;
		assertThat("", v16.get(0), instanceOf(StatementInput.class));
		Ident v17 = ((StatementInput) v16.get(0)).ident;
		assertEquals("x", String.valueOf(v17.getText()));
		assertThat("", v16.get(1), instanceOf(StatementIf.class));
		Expression v18 = ((StatementIf) v16.get(1)).expression;
		assertThat("", v18, instanceOf(ExpressionBinary.class));
		Expression v19 = ((ExpressionBinary) v18).e0;
		assertThat("", v19, instanceOf(ExpressionIdent.class));
		IToken v20 = ((ExpressionIdent) v19).firstToken;
		assertEquals("x", String.valueOf(v20.getText()));
		Expression v21 = ((ExpressionBinary) v18).e1;
		assertThat("", v21, instanceOf(ExpressionNumLit.class));
		IToken v22 = ((ExpressionNumLit) v21).firstToken;
		assertEquals("0", String.valueOf(v22.getText()));
		IToken v23 = ((ExpressionBinary) v18).op;
		assertEquals("=", String.valueOf(v23.getText()));
		Statement v24 = ((StatementIf) v16.get(1)).statement;
		assertThat("", v24, instanceOf(StatementOutput.class));
		Expression v25 = ((StatementOutput) v24).expression;
		assertThat("", v25, instanceOf(ExpressionIdent.class));
		IToken v26 = ((ExpressionIdent) v25).firstToken;
		assertEquals("y", String.valueOf(v26.getText()));
		assertThat("", v16.get(2), instanceOf(StatementWhile.class));
		Expression v27 = ((StatementWhile) v16.get(2)).expression;
		assertThat("", v27, instanceOf(ExpressionBinary.class));
		Expression v28 = ((ExpressionBinary) v27).e0;
		assertThat("", v28, instanceOf(ExpressionIdent.class));
		IToken v29 = ((ExpressionIdent) v28).firstToken;
		assertEquals("j", String.valueOf(v29.getText()));
		Expression v30 = ((ExpressionBinary) v27).e1;
		assertThat("", v30, instanceOf(ExpressionNumLit.class));
		IToken v31 = ((ExpressionNumLit) v30).firstToken;
		assertEquals("24", String.valueOf(v31.getText()));
		IToken v32 = ((ExpressionBinary) v27).op;
		assertEquals("<", String.valueOf(v32.getText()));
		Statement v33 = ((StatementWhile) v16.get(2)).statement;
		assertThat("", v33, instanceOf(StatementCall.class));
		Ident v34 = ((StatementCall) v33).ident;
		assertEquals("z", String.valueOf(v34.getText()));
		Statement v35 = ((Block) v0).statement;
		assertThat("", v35, instanceOf(StatementOutput.class));
		Expression v36 = ((StatementOutput) v35).expression;
		assertThat("", v36, instanceOf(ExpressionBinary.class));
		Expression v37 = ((ExpressionBinary) v36).e0;
		assertThat("", v37, instanceOf(ExpressionBinary.class));
		Expression v38 = ((ExpressionBinary) v37).e0;
		assertThat("", v38, instanceOf(ExpressionIdent.class));
		IToken v39 = ((ExpressionIdent) v38).firstToken;
		assertEquals("a", String.valueOf(v39.getText()));
		Expression v40 = ((ExpressionBinary) v37).e1;
		assertThat("", v40, instanceOf(ExpressionIdent.class));
		IToken v41 = ((ExpressionIdent) v40).firstToken;
		assertEquals("b", String.valueOf(v41.getText()));
		IToken v42 = ((ExpressionBinary) v37).op;
		assertEquals("+", String.valueOf(v42.getText()));
		Expression v43 = ((ExpressionBinary) v36).e1;
		assertThat("", v43, instanceOf(ExpressionBinary.class));
		Expression v44 = ((ExpressionBinary) v43).e0;
		assertThat("", v44, instanceOf(ExpressionBinary.class));
		Expression v45 = ((ExpressionBinary) v44).e0;
		assertThat("", v45, instanceOf(ExpressionBinary.class));
		Expression v46 = ((ExpressionBinary) v45).e0;
		assertThat("", v46, instanceOf(ExpressionIdent.class));
		IToken v47 = ((ExpressionIdent) v46).firstToken;
		assertEquals("c", String.valueOf(v47.getText()));
		Expression v48 = ((ExpressionBinary) v45).e1;
		assertThat("", v48, instanceOf(ExpressionIdent.class));
		IToken v49 = ((ExpressionIdent) v48).firstToken;
		assertEquals("e", String.valueOf(v49.getText()));
		IToken v50 = ((ExpressionBinary) v45).op;
		assertEquals("/", String.valueOf(v50.getText()));
		Expression v51 = ((ExpressionBinary) v44).e1;
		assertThat("", v51, instanceOf(ExpressionNumLit.class));
		IToken v52 = ((ExpressionNumLit) v51).firstToken;
		assertEquals("35", String.valueOf(v52.getText()));
		IToken v53 = ((ExpressionBinary) v44).op;
		assertEquals("*", String.valueOf(v53.getText()));
		Expression v54 = ((ExpressionBinary) v43).e1;
		assertThat("", v54, instanceOf(ExpressionBinary.class));
		Expression v55 = ((ExpressionBinary) v54).e0;
		assertThat("", v55, instanceOf(ExpressionNumLit.class));
		IToken v56 = ((ExpressionNumLit) v55).firstToken;
		assertEquals("3", String.valueOf(v56.getText()));
		Expression v57 = ((ExpressionBinary) v54).e1;
		assertThat("", v57, instanceOf(ExpressionNumLit.class));
		IToken v58 = ((ExpressionNumLit) v57).firstToken;
		assertEquals("4", String.valueOf(v58.getText()));
		IToken v59 = ((ExpressionBinary) v54).op;
		assertEquals("+", String.valueOf(v59.getText()));
		IToken v60 = ((ExpressionBinary) v43).op;
		assertEquals("/", String.valueOf(v60.getText()));
		IToken v61 = ((ExpressionBinary) v36).op;
		assertEquals("-", String.valueOf(v61.getText()));
	}

	@Test
	void test13() throws PLPException {
		String input = """
				CONST a * b;
				.
				""";
		assertThrows(SyntaxException.class, () -> {
			@SuppressWarnings("unused")
			ASTNode ast = getAST(input);
		});
	}

	@Test
	void test14() throws PLPException {
		String input = """
				PROCEDURE 42
				.
				""";
		assertThrows(SyntaxException.class, () -> {
			@SuppressWarnings("unused")
			ASTNode ast = getAST(input);
		});
	}

	@Test
//The error in this example should be found by the Lexer
	void test15() throws PLPException {
		String input = """
				VAR @;
				.
				""";
		assertThrows(LexicalException.class, () -> {
			@SuppressWarnings("unused")
			ASTNode ast = getAST(input);
		});
	}

	@Test
//The error in this example should be found by the Lexer
	void test16() throws PLPException {
		String input = """
				.
				.
				""";
		assertThrows(SyntaxException.class, () -> {
			@SuppressWarnings("unused")
			ASTNode ast = getAST(input);
		});
	}

	@Test
	// ExpressionBooleanLit
	void test17() throws PLPException {
		String input = """
				!TRUE.""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBooleanLit.class));
		IToken token = expr.firstToken;
		assertEquals(true, token.getBooleanValue());
	}

	@Test
	// ExpressionStringLit
	void test18() throws PLPException {
		String input = """
				!"String\\n".""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionStringLit.class));
		IToken token = expr.firstToken;
		assertEquals("String\n", token.getStringValue());
	}

	@Test
	// ExpressionNumLit
	void test19() throws PLPException {
		String input = """
				!10.""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionNumLit.class));
		IToken token = expr.firstToken;
		assertEquals(10, token.getIntValue());
	}

	@Test
	// ExpressionIdent
	void test20() throws PLPException {
		String input = """
				!Ident.""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionIdent.class));
		IToken token = expr.firstToken;
		assertEquals("Ident", String.valueOf(token.getText()));
	}

	@Test
	// Expression with Parentheses
	void test21() throws PLPException {
		String input = """
				!(simple_expr).""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionIdent.class));
		IToken token = expr.firstToken;
		assertEquals("simple_expr", String.valueOf(token.getText()));
	}

	@Test
	// Missing Right Parenthesis
	void test22() throws PLPException {
		String input = """
				!(missing_paren.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Missing Left Parenthesis
	void test23() throws PLPException {
		String input = """
				!missing_paren)
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
	// Multiplicative Expression
	void test24() throws PLPException {
		String input = """
				!1 - FALSE.""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBinary.class));
		IToken first = ((ExpressionBinary)expr).firstToken;
		Expression e0 = ((ExpressionBinary)expr).e0;
		IToken op = ((ExpressionBinary)expr).op;
		Expression e1 = ((ExpressionBinary)expr).e1;
		assertEquals(1, first.getIntValue());
		assertThat(e0, instanceOf(ExpressionNumLit.class));
		assertEquals(1, e0.firstToken.getIntValue());
		assertEquals("-", String.valueOf(op.getText()));
		assertThat(e1, instanceOf(ExpressionBooleanLit.class));
		assertEquals(false, e1.firstToken.getBooleanValue());
	}

	@Test
	// Missing second expression
	void test25() throws PLPException {
		String input = """
				!5+
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Missing second expression
	void test26() throws PLPException {
		String input = """
				!"Hello"%
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Missing first expression
	void test27() throws PLPException {
		String input = """
				!-20
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Missing first expression
	void test28() throws PLPException {
		String input = """
				!/20
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Double Additive Expression
	void test29() throws PLPException {
		String input = """
				!1 - FALSE + "Hi".""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBinary.class));
		IToken first = expr.firstToken;
		Expression e0 = ((ExpressionBinary)expr).e0;
		IToken op = ((ExpressionBinary)expr).op;
		Expression e1 = ((ExpressionBinary)expr).e1;
		assertThat(e0, instanceOf(ExpressionBinary.class));
		IToken e0_first = e0.firstToken;
		Expression e0_e0 = ((ExpressionBinary)e0).e0;
		IToken e0_op = ((ExpressionBinary)e0).op;
		Expression e0_e1 = ((ExpressionBinary)e0).e1;
		assertEquals(1, e0_first.getIntValue());
		assertThat(e0_e0, instanceOf(ExpressionNumLit.class));
		assertEquals(1, e0_e0.firstToken.getIntValue());
		assertEquals("-", String.valueOf(e0_op.getText()));
		assertThat(e0_e1, instanceOf(ExpressionBooleanLit.class));
		assertEquals(false, e0_e1.firstToken.getBooleanValue());
		assertEquals("+", String.valueOf(op.getText()));
		assertThat(e1, instanceOf(ExpressionStringLit.class));
		assertEquals("Hi", e1.firstToken.getStringValue());
	}

	@Test
		// Invalid Double Additive
	void test30() throws PLPException {
		String input = """
				!1 - FALSE + 
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Order of Operations
	void test31() throws PLPException {
		String input = """
				!1 * FALSE + "Hi".""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBinary.class));
		IToken first = expr.firstToken;
		Expression e0 = ((ExpressionBinary)expr).e0;
		IToken op = ((ExpressionBinary)expr).op;
		Expression e1 = ((ExpressionBinary)expr).e1;
		assertThat(e0, instanceOf(ExpressionBinary.class));
		IToken e0_first = e0.firstToken;
		Expression e0_e0 = ((ExpressionBinary)e0).e0;
		IToken e0_op = ((ExpressionBinary)e0).op;
		Expression e0_e1 = ((ExpressionBinary)e0).e1;
		assertEquals(1, e0_first.getIntValue());
		assertThat(e0_e0, instanceOf(ExpressionNumLit.class));
		assertEquals(1, e0_e0.firstToken.getIntValue());
		assertEquals("*", String.valueOf(e0_op.getText()));
		assertThat(e0_e1, instanceOf(ExpressionBooleanLit.class));
		assertEquals(false, e0_e1.firstToken.getBooleanValue());
		assertEquals("+", String.valueOf(op.getText()));
		assertThat(e1, instanceOf(ExpressionStringLit.class));
		assertEquals("Hi", e1.firstToken.getStringValue());
	}

	@Test
		// Order of Operations Reversed
	void test32() throws PLPException {
		String input = """
				!1 + FALSE * "Hi".""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBinary.class));
		IToken first = expr.firstToken;
		Expression e0 = ((ExpressionBinary)expr).e0;
		IToken op = ((ExpressionBinary)expr).op;
		Expression e1 = ((ExpressionBinary)expr).e1;
		assertEquals("+", String.valueOf(op.getText()));
		assertThat(e0, instanceOf(ExpressionNumLit.class));
		assertEquals(1, e0.firstToken.getIntValue());
		assertThat(e1, instanceOf(ExpressionBinary.class));
		IToken e1_first = e1.firstToken;
		Expression e1_e0 = ((ExpressionBinary)e1).e0;
		IToken e1_op = ((ExpressionBinary)e1).op;
		Expression e1_e1 = ((ExpressionBinary)e1).e1;
		assertEquals(false, e1_first.getBooleanValue());
		assertThat(e1_e0, instanceOf(ExpressionBooleanLit.class));
		assertEquals(false, e1_e0.firstToken.getBooleanValue());
		assertEquals("*", String.valueOf(e1_op.getText()));
		assertThat(e1_e1, instanceOf(ExpressionStringLit.class));
		assertEquals("Hi", e1_e1.firstToken.getStringValue());
	}

	@Test
		// Order of Operations
	void test33() throws PLPException {
		String input = """
				!1 + FALSE < "Hi".""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBinary.class));
		IToken first = expr.firstToken;
		Expression e0 = ((ExpressionBinary)expr).e0;
		IToken op = ((ExpressionBinary)expr).op;
		Expression e1 = ((ExpressionBinary)expr).e1;
		assertThat(e0, instanceOf(ExpressionBinary.class));
		IToken e0_first = e0.firstToken;
		Expression e0_e0 = ((ExpressionBinary)e0).e0;
		IToken e0_op = ((ExpressionBinary)e0).op;
		Expression e0_e1 = ((ExpressionBinary)e0).e1;
		assertEquals(1, e0_first.getIntValue());
		assertThat(e0_e0, instanceOf(ExpressionNumLit.class));
		assertEquals(1, e0_e0.firstToken.getIntValue());
		assertEquals("+", String.valueOf(e0_op.getText()));
		assertThat(e0_e1, instanceOf(ExpressionBooleanLit.class));
		assertEquals(false, e0_e1.firstToken.getBooleanValue());
		assertEquals("<", String.valueOf(op.getText()));
		assertThat(e1, instanceOf(ExpressionStringLit.class));
		assertEquals("Hi", e1.firstToken.getStringValue());
	}

	@Test
		// Order of Operations Reversed
	void test34() throws PLPException {
		String input = """
				!1 > FALSE - "Hi".""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBinary.class));
		IToken first = expr.firstToken;
		Expression e0 = ((ExpressionBinary)expr).e0;
		IToken op = ((ExpressionBinary)expr).op;
		Expression e1 = ((ExpressionBinary)expr).e1;
		assertEquals(">", String.valueOf(op.getText()));
		assertThat(e0, instanceOf(ExpressionNumLit.class));
		assertEquals(1, e0.firstToken.getIntValue());
		assertThat(e1, instanceOf(ExpressionBinary.class));
		IToken e1_first = e1.firstToken;
		Expression e1_e0 = ((ExpressionBinary)e1).e0;
		IToken e1_op = ((ExpressionBinary)e1).op;
		Expression e1_e1 = ((ExpressionBinary)e1).e1;
		assertEquals(false, e1_first.getBooleanValue());
		assertThat(e1_e0, instanceOf(ExpressionBooleanLit.class));
		assertEquals(false, e1_e0.firstToken.getBooleanValue());
		assertEquals("-", String.valueOf(e1_op.getText()));
		assertThat(e1_e1, instanceOf(ExpressionStringLit.class));
		assertEquals("Hi", e1_e1.firstToken.getStringValue());
	}

	@Test
		// Order of Operations
	void test35() throws PLPException {
		String input = """
				!1 / FALSE # "Hi".""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBinary.class));
		IToken first = expr.firstToken;
		Expression e0 = ((ExpressionBinary)expr).e0;
		IToken op = ((ExpressionBinary)expr).op;
		Expression e1 = ((ExpressionBinary)expr).e1;
		assertThat(e0, instanceOf(ExpressionBinary.class));
		IToken e0_first = e0.firstToken;
		Expression e0_e0 = ((ExpressionBinary)e0).e0;
		IToken e0_op = ((ExpressionBinary)e0).op;
		Expression e0_e1 = ((ExpressionBinary)e0).e1;
		assertEquals(1, e0_first.getIntValue());
		assertThat(e0_e0, instanceOf(ExpressionNumLit.class));
		assertEquals(1, e0_e0.firstToken.getIntValue());
		assertEquals("/", String.valueOf(e0_op.getText()));
		assertThat(e0_e1, instanceOf(ExpressionBooleanLit.class));
		assertEquals(false, e0_e1.firstToken.getBooleanValue());
		assertEquals("#", String.valueOf(op.getText()));
		assertThat(e1, instanceOf(ExpressionStringLit.class));
		assertEquals("Hi", e1.firstToken.getStringValue());
	}

	@Test
		// Order of Operations Reversed
	void test36() throws PLPException {
		String input = """
				!1 = FALSE % "Hi".""";
		ASTNode ast = getAST(input);
		Block block = ((Program) ast).block;
		Statement stmt = block.statement;
		assertThat("", stmt, instanceOf(StatementOutput.class));
		Expression expr = ((StatementOutput) stmt).expression;
		assertThat("", expr, instanceOf(ExpressionBinary.class));
		IToken first = expr.firstToken;
		Expression e0 = ((ExpressionBinary)expr).e0;
		IToken op = ((ExpressionBinary)expr).op;
		Expression e1 = ((ExpressionBinary)expr).e1;
		assertEquals("=", String.valueOf(op.getText()));
		assertThat(e0, instanceOf(ExpressionNumLit.class));
		assertEquals(1, e0.firstToken.getIntValue());
		assertThat(e1, instanceOf(ExpressionBinary.class));
		IToken e1_first = e1.firstToken;
		Expression e1_e0 = ((ExpressionBinary)e1).e0;
		IToken e1_op = ((ExpressionBinary)e1).op;
		Expression e1_e1 = ((ExpressionBinary)e1).e1;
		assertEquals(false, e1_first.getBooleanValue());
		assertThat(e1_e0, instanceOf(ExpressionBooleanLit.class));
		assertEquals(false, e1_e0.firstToken.getBooleanValue());
		assertEquals("%", String.valueOf(e1_op.getText()));
		assertThat(e1_e1, instanceOf(ExpressionStringLit.class));
		assertEquals("Hi", e1_e1.firstToken.getStringValue());
	}

	@Test
		// Invalid Multiplicative
	void test37() throws PLPException {
		String input = """
				!1 * FALSE /.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Invalid Comparative
	void test38() throws PLPException {
		String input = """
				!1 > FALSE =.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Invalid Combined Types
	void test39() throws PLPException {
		String input = """
				!1 > FALSE *.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Invalid Print
	void test40() throws PLPException {
		String input = """
				!.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Missing DOT
	void test41() throws PLPException {
		String input = """
				!"Missing Dot"
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Missing DO in WHILE
	void test42() throws PLPException {
		String input = """
				WHILE TRUE
					! x
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Missing WHILE with DO
	void test43() throws PLPException {
		String input = """
				TRUE
				DO ! x
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Invalid statement in while loop
	void test44() throws PLPException {
		String input = """
				WHILE FALSE
				DO 12
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Ending statement with ;
	void test45() throws PLPException {
		String input = """
				WHILE FALSE
				DO ? ident;
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// IF without THEN
	void test46() throws PLPException {
		String input = """
				IF x <= 2
					! x
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// THEN without IF
	void test47() throws PLPException {
		String input = """
				y >= "Hi"
				THEN ! y
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// BEGIN without END
	void test48() throws PLPException {
		String input = """
				BEGIN
					! x.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// END without BEGIN
	void test49() throws PLPException {
		String input = """
				!x END.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Invalid input
	void test50() throws PLPException {
		String input = """
				?.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Attempts to print a statement
	void test51() throws PLPException {
		String input = """
				!!.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Attempts to input into a non-ident
	void test52() throws PLPException {
		String input = """
				?3.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Attempts to call a non-ident
	void test53() throws PLPException {
		String input = """
				CALL 0.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
		// Attempts to assign an ident to a statement
	void test54() throws PLPException {
		String input = """
				x := !x.
				""";
		Exception ex = assertThrows(SyntaxException.class, () -> {
			ASTNode ast = getAST(input);
		});
		System.out.println(ex.getMessage());
	}

	@Test
	void coleTest1() throws PLPException{
		String input = """
           VAR x,y;
           PROCEDURE random;
               CALL random_int;
           BEGIN
           x := 10;
           y := random;
           IF (x > y) THEN ! \"Number 1 is larger than number 2\";
           IF (x < y) THEN ! \"Number 2 is larger than number 1\";
           IF (x = y) THEN ! \"Number 1 is equal to number 2\"
           END
           .
           """;
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<VarDec> v1 = ((Block) v0).varDecs;
		assertEquals(2, v1.size());
		IToken v2 = ((VarDec) v1.get(0)).ident;
		assertEquals("x", String.valueOf(v2.getText()));
		IToken v3 = ((VarDec) v1.get(1)).ident;
		assertEquals("y", String.valueOf(v3.getText()));
		List<ProcDec> v4 = ((Block) v0).procedureDecs;
		assertEquals(1, v4.size());
		IToken v5 = ((ProcDec) v4.get(0)).ident;
		assertEquals("random", String.valueOf(v5.getText()));
		Block v6 = ((ProcDec) v4.get(0)).block;
		Statement v7 = ((Block) v6).statement;
		assertThat("", v7, instanceOf(StatementCall.class));
		Ident v8 = ((StatementCall) v7).ident;
		assertEquals("random_int", String.valueOf(v8.getText()));
		Statement v9 = ((Block) v0).statement;
		assertThat("", v9, instanceOf(StatementBlock.class));
		Statement v10 = ((StatementBlock) v9).statements.get(0);
		assertThat("", v10, instanceOf(StatementAssign.class));
		Ident v11 = ((StatementAssign) v10).ident;
		assertEquals("x", String.valueOf(v11.getText()));
		Expression v12 = ((StatementAssign) v10).expression;
		assertThat("", v12, instanceOf(ExpressionNumLit.class));
		Statement v13 = ((StatementBlock) v9).statements.get(1);
		assertThat("", v13, instanceOf(StatementAssign.class));
		Ident v14 = ((StatementAssign) v13).ident;
		assertEquals("y", String.valueOf(v14.getText()));
		Expression v15 = ((StatementAssign) v13).expression;
		assertThat("", v15, instanceOf(ExpressionIdent.class));
		Statement v16 = ((StatementBlock) v9).statements.get(2);
		assertThat("", v16, instanceOf(StatementIf.class));
		Expression v17 = ((StatementIf) v16).expression;
		assertEquals("x", String.valueOf(((ExpressionBinary) v17).e0.getFirstToken().getText()));
		assertEquals(">", String.valueOf(((ExpressionBinary) v17).op.getText()));
		assertEquals("y", String.valueOf(((ExpressionBinary) v17).e1.getFirstToken().getText()));
		Statement v18 = ((StatementIf) v16).statement;
		assertThat("", v18, instanceOf(StatementOutput.class));
		Expression v19 = ((StatementOutput) v18).expression;
		assertThat("", v19, instanceOf(ExpressionStringLit.class));
		assertEquals("\"Number 1 is larger than number 2\"", String.valueOf(v19.getFirstToken().getText()));
		Statement v20 = ((StatementBlock) v9).statements.get(3);
		assertThat("", v20, instanceOf(StatementIf.class));
		Expression v21 = ((StatementIf) v20).expression;
		assertEquals("x", String.valueOf(((ExpressionBinary) v21).e0.getFirstToken().getText()));
		assertEquals("<", String.valueOf(((ExpressionBinary) v21).op.getText()));
		assertEquals("y", String.valueOf(((ExpressionBinary) v21).e1.getFirstToken().getText()));
		Statement v22 = ((StatementIf) v20).statement;
		assertThat("", v22, instanceOf(StatementOutput.class));
		Expression v23 = ((StatementOutput) v22).expression;
		assertThat("", v23, instanceOf(ExpressionStringLit.class));
		assertEquals("\"Number 2 is larger than number 1\"", String.valueOf(v23.getFirstToken().getText()));
		Statement v24 = ((StatementBlock) v9).statements.get(4);
		assertThat("", v24, instanceOf(StatementIf.class));
		Expression v25 = ((StatementIf) v24).expression;
		assertEquals("x", String.valueOf(((ExpressionBinary) v25).e0.getFirstToken().getText()));
		assertEquals("=", String.valueOf(((ExpressionBinary) v25).op.getText()));
		assertEquals("y", String.valueOf(((ExpressionBinary) v25).e1.getFirstToken().getText()));
		Statement v26 = ((StatementIf) v24).statement;
		assertThat("", v26, instanceOf(StatementOutput.class));
		Expression v27 = ((StatementOutput) v26).expression;
		assertThat("", v27, instanceOf(ExpressionStringLit.class));
		assertEquals("\"Number 1 is equal to number 2\"",
				String.valueOf(v27.getFirstToken().getText()));
	}

	@Test
	void coleTest2() throws PLPException{
		String input = """
               ! x = y # 10 > 7 <= 45 < 50 >= 56
               .
               """;
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		Statement v1 = ((Block) v0).statement;
		assertThat("", v1, instanceOf(StatementOutput.class));
		Expression v2 = ((StatementOutput) v1).expression;
		assertThat("", v2, instanceOf(ExpressionBinary.class));
		assertEquals(">=", String.valueOf(((ExpressionBinary) v2).op.getText()));
		assertEquals("56", String.valueOf(((ExpressionBinary) v2).e1.getFirstToken().getText()));
		Expression v3 = ((ExpressionBinary) v2).e0;
		assertThat("", v3, instanceOf(ExpressionBinary.class));
		assertEquals("<", String.valueOf(((ExpressionBinary) v3).op.getText()));
		assertEquals("50", String.valueOf(((ExpressionBinary) v3).e1.getFirstToken().getText()));
		Expression v4 = ((ExpressionBinary) v3).e0;
		assertThat("", v4, instanceOf(ExpressionBinary.class));
		assertEquals("<=", String.valueOf(((ExpressionBinary) v4).op.getText()));
		assertEquals("45", String.valueOf(((ExpressionBinary) v4).e1.getFirstToken().getText()));
		Expression v5 = ((ExpressionBinary) v4).e0;
		assertThat("", v5, instanceOf(ExpressionBinary.class));
		assertEquals(">", String.valueOf(((ExpressionBinary) v5).op.getText()));
		assertEquals("7", String.valueOf(((ExpressionBinary) v5).e1.getFirstToken().getText()));
		Expression v6 = ((ExpressionBinary) v5).e0;
		assertThat("", v6, instanceOf(ExpressionBinary.class));
		assertEquals("#", String.valueOf(((ExpressionBinary) v6).op.getText()));
		assertEquals("10", String.valueOf(((ExpressionBinary) v6).e1.getFirstToken().getText()));
		Expression v7 = ((ExpressionBinary) v6).e0;
		assertThat("", v7, instanceOf(ExpressionBinary.class));
		assertEquals("=", String.valueOf(((ExpressionBinary) v7).op.getText()));
		assertEquals("y", String.valueOf(((ExpressionBinary) v7).e1.getFirstToken().getText()));
		assertEquals("x", String.valueOf(((ExpressionBinary) v7).e0.getFirstToken().getText()));
	}

	@Test
	void failedTest14() throws PLPException {
		String input = """
			CONST a=3;
			VAR x,y,z;
			PROCEDURE p;
			  VAR j;
			  BEGIN
				? x;
				IF x = 0 THEN ! y ;
			  END;
			.
			""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ConstDec> v1 = ((Block) v0).constDecs;
		assertEquals(1, v1.size());
		List<VarDec> v2 = ((Block) v0).varDecs;
		assertEquals(3, v2.size());
		List<ProcDec> v3 = ((Block) v0).procedureDecs;
		assertEquals(1, v3.size());
		Block v4 = v3.get(0).block;
		List<ConstDec> v5 = ((Block) v4).constDecs;
		assertEquals(0, v5.size());
		List<VarDec> v6 = ((Block) v4).varDecs;
		assertEquals(1, v6.size());
		Statement v7 = ((Block) v4).statement;
		assertThat("", v7, instanceOf(StatementBlock.class));
	}

	@Test
	void failedTest25() throws PLPException {
		String input = """
			PROCEDURE x;
				 CALL x;
			PROCEDURE y;
				 !x;
			PROCEDURE z;
				 VAR y,z;
			;
			.
			""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ProcDec> v1 = ((Block) v0).procedureDecs;
		assertEquals(3, v1.size());
	}

	@Test
	void failedTest28() throws PLPException {
		String input = """
			PROCEDURE X;
				 PROCEDURE Y;
						 PROCEDURE Z;
								 CALL XYZ;
				 ;
			;
			.
			""";
		ASTNode ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Block v0 = ((Program) ast).block;
		assertThat("", v0, instanceOf(Block.class));
		List<ProcDec> v1 = ((Block) v0).procedureDecs;
		assertEquals(1, v1.size());
	}
}