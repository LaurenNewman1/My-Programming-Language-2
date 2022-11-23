package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;
import org.objectweb.asm.*;
import edu.ufl.cise.plpfa22.IToken.Kind;
import edu.ufl.cise.plpfa22.ast.Types.Type;

import java.util.ArrayList;
import java.util.List;


public class CodeGenVisitor implements ASTVisitor, Opcodes {

	final String packageName;
	final String className;
	final String sourceFileName;
	final String fullyQualifiedClassName; 
	final String classDesc;
	
	ClassWriter classWriter;

	
	public CodeGenVisitor(String className, String packageName, String sourceFileName) {
		super();
		this.packageName = packageName;
		this.className = className;
		this.sourceFileName = sourceFileName;
		this.fullyQualifiedClassName = packageName + "/" + className;
		this.classDesc="L"+this.fullyQualifiedClassName+';';
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws PLPException {
		ClassWriter cw = (ClassWriter)arg;
		List<Declaration> decs = new ArrayList<>(block.constDecs);
		decs.addAll(block.varDecs);
		initFields(cw, decs);
		MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		methodVisitor.visitCode();
		//get a method visitor for the main method.
		for (ConstDec constDec : block.constDecs) {
			constDec.visit(this, methodVisitor);
		}
//		for (VarDec varDec : block.varDecs) {
//			varDec.visit(this, methodVisitor);
//		}
		for (ProcDec procDec: block.procedureDecs) {
			procDec.visit(this, className);
		}
		//add instructions from statement to method
		block.statement.visit(this, methodVisitor);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(0,0);
		methodVisitor.visitEnd();
		return null;

	}

	private void init(ClassWriter cw) {
		MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		methodVisitor.visitCode();
		Label label0 = new Label();
		methodVisitor.visitLabel(label0);
		methodVisitor.visitLineNumber(9, label0);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V",
				false);
		Label label1 = new Label();
		methodVisitor.visitLabel(label1);
		methodVisitor.visitLineNumber(10, label1);
		methodVisitor.visitInsn(RETURN);
		Label label2 = new Label();
		methodVisitor.visitLabel(label2);
		methodVisitor.visitLocalVariable("this", classDesc, null, label0, label2, 0);
		methodVisitor.visitMaxs(1, 1);
		methodVisitor.visitEnd();
	}

	private void initMain(ClassWriter cw) {
		MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main",
				"([Ljava/lang/String;)V", null, null);
		methodVisitor.visitCode();
		Label label0 = new Label();
		methodVisitor.visitLabel(label0);
		methodVisitor.visitLineNumber(23, label0);
		methodVisitor.visitTypeInsn(NEW, fullyQualifiedClassName);
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, fullyQualifiedClassName, "<init>", "()V", false);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL,fullyQualifiedClassName, "run", "()V", false);
		Label label1 = new Label();
		methodVisitor.visitLabel(label1);
		methodVisitor.visitLineNumber(24, label1);
		methodVisitor.visitInsn(RETURN);
		Label label2 = new Label();
		methodVisitor.visitLabel(label2);
		methodVisitor.visitLocalVariable("args", "[Ljava/lang/String;", null, label0, label2,
				0);
		methodVisitor.visitMaxs(2, 1);
		methodVisitor.visitEnd();
	}

	private void initFields(ClassWriter cw, List<Declaration> decs) {
		for (Declaration dec : decs) {
			FieldVisitor fv = cw.visitField(ACC_PUBLIC, getVarName(dec), getDesc(dec.getType()), null, null);
			fv.visitEnd();
		}
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws PLPException {
		//create a classWriter and visit it
		classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		//Hint:  if you get failures in the visitMaxs, try creating a ClassWriter with 0
		// instead of ClassWriter.COMPUTE_FRAMES.  The result will not be a valid classfile,
		// but you will be able to print it so you can see the instructions.  After fixing,
		// restore ClassWriter.COMPUTE_FRAMES
		classWriter.visit(V18, ACC_PUBLIC | ACC_SUPER, fullyQualifiedClassName, null, "java/lang/Object", new String[] {"java/lang/Runnable" });
		classWriter.visitSource(null, null);
		init(classWriter);
		initMain(classWriter);
		//visit the block, passing it the methodVisitor
		program.block.visit(this, classWriter);
		//finish up the class
        classWriter.visitEnd();
        //return the bytes making up the classfile
		List<CodeGenUtils.GenClass> classes = new ArrayList<>();
		classes.add(new CodeGenUtils.GenClass(fullyQualifiedClassName, classWriter.toByteArray()));
		return classes;
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
		statementAssign.expression.visit(this, arg);
		statementAssign.ident.visit(this, arg);
		return null;
	}

	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
		ClassWriter cw = (ClassWriter) arg;
		FieldVisitor fv = cw.visitField(ACC_PUBLIC, getVarName(varDec), getDesc(varDec.getType()), null, null);
		fv.visitEnd();
		return null;
	}

	@Override
	public Object visitStatementCall(StatementCall statementCall, Object arg) throws PLPException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg) throws PLPException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementOutput(StatementOutput statementOutput, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor)arg;
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		statementOutput.expression.visit(this, arg);
		Type etype = statementOutput.expression.getType();
		String JVMType = (etype.equals(Type.NUMBER) ? "I" : (etype.equals(Type.BOOLEAN) ? "Z" : "Ljava/lang/String;"));
		String printlnSig = "(" + JVMType +")V";
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", printlnSig, false);
		return null;
	}

	@Override
	public Object visitStatementBlock(StatementBlock statementBlock, Object arg) throws PLPException {
		for (Statement statement : statementBlock.statements) {
			statement.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor)arg;
		statementIf.expression.visit(this, arg);
		Label labelStringEqFalseBr = new Label();
		mv.visitJumpInsn(IFEQ, labelStringEqFalseBr);
		statementIf.statement.visit(this, arg);
		Label labelPostStringEq = new Label();
		mv.visitJumpInsn(GOTO, labelPostStringEq);
		mv.visitLabel(labelStringEqFalseBr);
		mv.visitLabel(labelPostStringEq);
		return null;
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor) arg;
		Label bodyLabel = new Label();
		Label guardLabel = new Label();
		mv.visitJumpInsn(GOTO, guardLabel);
		mv.visitLabel(bodyLabel);
		statementWhile.statement.visit(this, arg);
		mv.visitLabel(guardLabel);
		statementWhile.expression.visit(this, arg);
		mv.visitJumpInsn(IFNE, bodyLabel); // if true go to loop
		return null;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor) arg;
		Type argType = expressionBinary.e0.getType();
		Kind op = expressionBinary.op.getKind();
		switch (argType) {
			case NUMBER -> {
				expressionBinary.e0.visit(this, arg);
				expressionBinary.e1.visit(this, arg);
				switch (op) {
					case PLUS -> mv.visitInsn(IADD);
					case MINUS -> mv.visitInsn(ISUB);
					case TIMES -> mv.visitInsn(IMUL);
					case DIV -> mv.visitInsn(IDIV);
					case MOD -> mv.visitInsn(IREM);
					case EQ -> {
						Label labelNumEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPNE, labelNumEqFalseBr);
						mv.visitInsn(ICONST_1);
						Label labelPostNumEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostNumEq);
						mv.visitLabel(labelNumEqFalseBr);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(labelPostNumEq);
					}
					case NEQ -> {
						Label labelNumEqTrueBr = new Label();
						mv.visitJumpInsn(IF_ICMPNE, labelNumEqTrueBr);
						mv.visitInsn(ICONST_0);
						Label labelPostNumEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostNumEq);
						mv.visitLabel(labelNumEqTrueBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostNumEq);
					}
					case LT -> {
						Label labelNumEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPLT, labelNumEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostNumEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostNumEq);
						mv.visitLabel(labelNumEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostNumEq);
					}
					case LE -> {
						Label labelNumEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPLE, labelNumEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostNumEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostNumEq);
						mv.visitLabel(labelNumEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostNumEq);
					}
					case GT -> {
						Label labelNumEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPGT, labelNumEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostNumEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostNumEq);
						mv.visitLabel(labelNumEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostNumEq);
					}
					case GE -> {
						Label labelNumEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPGE, labelNumEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostNumEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostNumEq);
						mv.visitLabel(labelNumEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostNumEq);
					}
					default -> {
						throw new IllegalStateException("code gen bug in visitExpressionBinary NUMBER");
					}
				}
			}
			case BOOLEAN -> {
				expressionBinary.e0.visit(this, arg);
				expressionBinary.e1.visit(this, arg);
				switch (op) {
					case PLUS -> mv.visitInsn(IOR);
					case TIMES -> mv.visitInsn(IAND);
					case EQ -> {
						Label labelBoolEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPNE, labelBoolEqFalseBr);
						mv.visitInsn(ICONST_1);
						Label labelPostBoolEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostBoolEq);
						mv.visitLabel(labelBoolEqFalseBr);
						mv.visitInsn(ICONST_0);
						mv.visitLabel(labelPostBoolEq);
					}
					case NEQ -> {
						Label labelBoolEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPNE, labelBoolEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostBoolEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostBoolEq);
						mv.visitLabel(labelBoolEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostBoolEq);
					}
					case LT -> {
						Label labelBoolEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPLT, labelBoolEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostBoolEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostBoolEq);
						mv.visitLabel(labelBoolEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostBoolEq);
					}
					case LE -> {
						Label labelBoolEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPLE, labelBoolEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostBoolEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostBoolEq);
						mv.visitLabel(labelBoolEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostBoolEq);
					}
					case GT -> {
						Label labelBoolEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPGT, labelBoolEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostBoolEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostBoolEq);
						mv.visitLabel(labelBoolEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostBoolEq);
					}
					case GE -> {
						Label labelBoolEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ICMPGE, labelBoolEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostBoolEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostBoolEq);
						mv.visitLabel(labelBoolEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostBoolEq);
					}
					default -> {
						throw new IllegalStateException("code gen bug in visitExpressionBinary NUMBER");
					}
				}
			}
			case STRING -> {
				switch (op) {
					case PLUS -> {
						expressionBinary.e0.visit(this, arg);
						expressionBinary.e1.visit(this, arg);
						String descriptor = "(Ljava/lang/String;)Ljava/lang/String;";
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "concat", descriptor, false);
					}
					case EQ -> {
						expressionBinary.e1.visit(this, arg);
						expressionBinary.e0.visit(this, arg);
						String descriptor = "(Ljava/lang/Object;)Z";
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", descriptor, false);
					}
					case NEQ -> {
						expressionBinary.e1.visit(this, arg);
						expressionBinary.e0.visit(this, arg);
						String descriptor = "(Ljava/lang/Object;)Z";
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", descriptor, false);
						Label labelFalse = new Label();
						mv.visitJumpInsn(IFEQ, labelFalse);
						mv.visitInsn(ICONST_0);
						Label labelTrue = new Label();
						mv.visitJumpInsn(GOTO, labelTrue);
						mv.visitLabel(labelFalse);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelTrue);
					}
					case LT -> {
						expressionBinary.e1.visit(this, arg);
						expressionBinary.e0.visit(this, arg);
						String descriptor = "(Ljava/lang/String;)Z";
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", descriptor, false);

						expressionBinary.e1.visit(this, arg);
						expressionBinary.e0.visit(this, arg);
						Label labelStringEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ACMPNE, labelStringEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostStringEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostStringEq);
						mv.visitLabel(labelStringEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostStringEq);

						mv.visitInsn(IAND);
					}
					case GT -> {
						expressionBinary.e0.visit(this, arg);
						expressionBinary.e1.visit(this, arg);
						String descriptor = "(Ljava/lang/String;)Z";
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "endsWith", descriptor, false);

						expressionBinary.e1.visit(this, arg);
						expressionBinary.e0.visit(this, arg);
						Label labelStringEqFalseBr = new Label();
						mv.visitJumpInsn(IF_ACMPNE, labelStringEqFalseBr);
						mv.visitInsn(ICONST_0);
						Label labelPostStringEq = new Label();
						mv.visitJumpInsn(GOTO, labelPostStringEq);
						mv.visitLabel(labelStringEqFalseBr);
						mv.visitInsn(ICONST_1);
						mv.visitLabel(labelPostStringEq);

						mv.visitInsn(IAND);
					}
					case LE -> {
						expressionBinary.e1.visit(this, arg);
						expressionBinary.e0.visit(this, arg);
						String descriptor = "(Ljava/lang/String;)Z";
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", descriptor, false);
					}
					case GE -> {
						expressionBinary.e0.visit(this, arg);
						expressionBinary.e1.visit(this, arg);
						String descriptor = "(Ljava/lang/String;)Z";
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "endsWith", descriptor, false);

					}
				}
			}
			default -> {
				throw new IllegalStateException("code gen bug in visitExpressionBinary");
			}
		}
		return null;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor)arg;
		int currNest = expressionIdent.getNest();
		int targetNest = expressionIdent.getDec().getNest();
		mv.visitVarInsn(ALOAD, 0);
		//mv.visitFieldInsn(GETFIELD, fullyQualifiedClassName, "this$" + targetNest, classDesc);
		switch (expressionIdent.getType()) {
			case NUMBER -> mv.visitFieldInsn(GETFIELD, fullyQualifiedClassName, getVarName(expressionIdent.getDec()), "I");
			case BOOLEAN -> mv.visitFieldInsn(GETFIELD, fullyQualifiedClassName, getVarName(expressionIdent.getDec()), "Z");
			case STRING -> mv.visitFieldInsn(GETFIELD, fullyQualifiedClassName, getVarName(expressionIdent.getDec()), "Ljava/lang/String;");
		}
		return null;
	}

	@Override
	public Object visitExpressionNumLit(ExpressionNumLit expressionNumLit, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor)arg;
		mv.visitLdcInsn(expressionNumLit.getFirstToken().getIntValue());
		return null;
	}

	@Override
	public Object visitExpressionStringLit(ExpressionStringLit expressionStringLit, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor)arg;
		mv.visitLdcInsn(expressionStringLit.getFirstToken().getStringValue());
		return null;
	}

	@Override
	public Object visitExpressionBooleanLit(ExpressionBooleanLit expressionBooleanLit, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor)arg;
		mv.visitLdcInsn(expressionBooleanLit.getFirstToken().getBooleanValue());
		return null;
	}

	@Override
	public Object visitProcedure(ProcDec procDec, Object arg) throws PLPException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitConstDec(ConstDec constDec, Object arg) throws PLPException {
		MethodVisitor methodVisitor = (MethodVisitor) arg;
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitLdcInsn(constDec.val);
		methodVisitor.visitFieldInsn(PUTFIELD, fullyQualifiedClassName, getVarName(constDec), getDesc(constDec.getType()));

		return null;
	}

	@Override
	public Object visitStatementEmpty(StatementEmpty statementEmpty, Object arg) throws PLPException {
		return null;
	}

	@Override
	public Object visitIdent(Ident ident, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor)arg;
		int currNest = ident.getNest();
		int targetNest = ident.getDec().getNest();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(SWAP);
		//mv.visitFieldInsn(GETFIELD, fullyQualifiedClassName, "this$" + targetNest, classDesc);
		switch (ident.getDec().getType()) {
			case NUMBER -> mv.visitFieldInsn(PUTFIELD, fullyQualifiedClassName, getVarName(ident.getDec()), "I");
			case BOOLEAN -> mv.visitFieldInsn(PUTFIELD, fullyQualifiedClassName, getVarName(ident.getDec()), "Z");
			case STRING -> mv.visitFieldInsn(PUTFIELD, fullyQualifiedClassName, getVarName(ident.getDec()), "Ljava/lang/String;");
		}
		return null;
	}

	private String getVarName(Declaration dec) {
		if (dec instanceof ConstDec)
			return String.copyValueOf(((ConstDec)dec).ident.getText());
		else if (dec instanceof VarDec)
			return String.copyValueOf(((VarDec)dec).ident.getText());
		return String.copyValueOf(((ProcDec)dec).ident.getText());
	}

	private String getDesc(Type type) {
		switch (type) {
			case NUMBER -> {
				return "I";
			}
			case BOOLEAN -> {
				return "Z";
			}
		}
		return "Ljava/lang/String;";
	}

}
