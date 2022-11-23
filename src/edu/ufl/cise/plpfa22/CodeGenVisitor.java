package edu.ufl.cise.plpfa22;

import edu.ufl.cise.plpfa22.ast.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
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
		MethodVisitor methodVisitor = (MethodVisitor)arg;
		methodVisitor.visitCode();
		for (VarDec varDec : block.varDecs) {
			varDec.visit(this, methodVisitor);
		}
		for (ProcDec procDec: block.procedureDecs) {
			procDec.visit(this, className);
		}
		//add instructions from statement to method
		block.statement.visit(this, arg);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(0,0);
		methodVisitor.visitEnd();
		return null;

	}

	@Override
	public Object visitProgram(Program program, Object arg) throws PLPException {
		//create a classWriter and visit it
		classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		//Hint:  if you get failures in the visitMaxs, try creating a ClassWriter with 0
		// instead of ClassWriter.COMPUTE_FRAMES.  The result will not be a valid classfile,
		// but you will be able to print it so you can see the instructions.  After fixing,
		// restore ClassWriter.COMPUTE_FRAMES
		classWriter.visit(V18, ACC_PUBLIC | ACC_SUPER, fullyQualifiedClassName, null, "java/lang/Object", null);


		//get a method visitor for the main method.		
		MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		//visit the block, passing it the methodVisitor
		program.block.visit(this, methodVisitor);
		//finish up the class
        classWriter.visitEnd();
        //return the bytes making up the classfile
		List<CodeGenUtils.GenClass> classes = new ArrayList<>();
		classes.add(new CodeGenUtils.GenClass(fullyQualifiedClassName, classWriter.toByteArray()));
		return classes;
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws PLPException {
		MethodVisitor mv = (MethodVisitor)arg;
		statementAssign.expression.visit(this, arg);
		statementAssign.ident.visit(this, arg);
		return null;
	}

	@Override
	public Object visitVarDec(VarDec varDec, Object arg) throws PLPException {
		throw new UnsupportedOperationException();
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
		for (int i = currNest; i > targetNest; i--)
			mv.visitFieldInsn(GETFIELD, className, "this$0", classDesc);
		switch (expressionIdent.getType()) {
			case NUMBER -> mv.visitFieldInsn(GETFIELD, className, expressionIdent.getFirstToken().getStringValue(), "I");
			case BOOLEAN -> mv.visitFieldInsn(GETFIELD, className, expressionIdent.getFirstToken().getStringValue(), "Z");
			case STRING -> mv.visitFieldInsn(GETFIELD, className, expressionIdent.getFirstToken().getStringValue(), "java/lang/String;");
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
		// Do nothing
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
		for (int i = currNest; i > targetNest; i--)
			mv.visitFieldInsn(GETFIELD, className, "this$0", classDesc);
		switch (ident.getDec().getType()) {
			case NUMBER -> mv.visitFieldInsn(PUTFIELD, className, ident.getFirstToken().getStringValue(), "I");
			case BOOLEAN -> mv.visitFieldInsn(PUTFIELD, className, ident.getFirstToken().getStringValue(), "Z");
			case STRING -> mv.visitFieldInsn(PUTFIELD, className, ident.getFirstToken().getStringValue(), "java/lang/String;");
		}
		return null;
	}

}
