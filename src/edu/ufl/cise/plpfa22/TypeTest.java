package edu.ufl.cise.plpfa22;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.DisplayNameGenerator;


import edu.ufl.cise.plpfa22.ast.ASTNode;
import edu.ufl.cise.plpfa22.ast.ASTVisitor;
import edu.ufl.cise.plpfa22.ast.PrettyPrintVisitor;

@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
class TypeTest {

    static final boolean VERBOSE = true;

    ASTNode getAST(String input) throws PLPException {
        IParser parser = CompilerComponentFactory.getParser(CompilerComponentFactory.getLexer(input));
        ASTNode ast = parser.parse();
        ASTVisitor scopes = CompilerComponentFactory.getScopeVisitor();
        ast.visit(scopes, null);
        return ast;
    }

    ASTNode checkTypes(ASTNode ast) throws PLPException {
        ASTVisitor types = CompilerComponentFactory.getTypeInferenceVisitor();
        ast.visit(types, null);
        return ast;
    }

    void show(ASTNode ast) throws PLPException {
        if (VERBOSE) {
            if (ast != null) {
                System.out.println(PrettyPrintVisitor.AST2String(ast));
            } else {
                System.out.println("ast = null");
            }
        }
    }

    void show(Object obj) {
        if (VERBOSE) {
            System.out.println(obj);
        }
    }

    void makeAssertion(String actual, String expected) {
        assertEquals(actual, expected);
    }

    //Use this for tests that are successfully typed
    void runTest(String input, TestInfo testInfo) throws PLPException {
        show("\n**********" + testInfo.getDisplayName().split("[(]")[0] + "*************");
        show(input);
        ASTNode ast = getAST(input);
        checkTypes(ast);
        show(ast);
    }

    void runTest(String input, TestInfo testInfo, String expected) throws PLPException {
        show("\n**********" + testInfo.getDisplayName().split("[(]")[0] + "*************");
        show(input);
        ASTNode ast = getAST(input);
        checkTypes(ast);
        show(ast);
        assertEquals(expected, PrettyPrintVisitor.AST2String(ast) + "\n");
    }

    //Use this one for tests that should detect an error
    void runTest(String input, TestInfo testInfo, Class expectedException) {
        show("\n**********" + testInfo.getDisplayName().split("[(]")[0] + "*************");
        show(input);
        assertThrows(TypeCheckException.class, () -> {
            ASTNode ast = null;
            try {
                ast = getAST(input);
                checkTypes(ast);
            } catch (Exception e) {
                System.out.println("Exception thrown:  " + e.getClass() + " " + e.getMessage());
                show(ast);
                throw e;
            }
            show(ast);
        });
    }

    @Test
    void test_numlit(TestInfo testInfo) throws PLPException {
        String input = """
                ! 0 .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs none
                      STATEMENT
                        OUTPUT
                          NumLit 0
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void stringlit(TestInfo testInfo) throws PLPException {
        String input = """
                ! "hello" .
                """;
        String expected = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs none
                      STATEMENT
                        OUTPUT
                          StringLit "hello"
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void booleanlit(TestInfo testInfo) throws PLPException {
        String input = """
                ! TRUE .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs none
                      STATEMENT
                        OUTPUT
                          BooleanLit true
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }


    @Test
    void error_outputProcedure(TestInfo testInfo) throws PLPException {
        String input = """
                PROCEDURE p;;
                      !p //Cannot output a procedure
                      .
                      """;
        runTest(input, testInfo, TypeCheckException.class);
    }


    @Test
    void unusedVariable(TestInfo testInfo) throws PLPException {
        String input = """
                VAR abc; //never used, so this is legal
                .
                """;
        String expected = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR abc at nest level 0 type=null
                      ProcDecs none
                      STATEMENT
                        EmptyStatement
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void insufficientTypeInfo(TestInfo testInfo) throws PLPException {
        String input = """
                VAR abc; 
                ! abc
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void constants(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a = 3, b = TRUE, c = "hello";
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs\s
                        CONST a=3 at nest level 0 type=NUMBER
                        CONST b=true at nest level 0 type=BOOLEAN
                        CONST c=hello at nest level 0 type=STRING
                      VarDecs none
                      ProcDecs none
                      STATEMENT
                        EmptyStatement
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void inferVars0(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x,y,z;
                BEGIN
                x := 3;
                y := "hello";
                z := FALSE
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR x at nest level 0 type=NUMBER
                        VAR y at nest level 0 type=STRING
                        VAR z at nest level 0 type=BOOLEAN
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          ASSIGNMENT
                            Ident  x identNest=0 decNest=0 type=NUMBER
                            NumLit 3
                          ASSIGNMENT
                            Ident  y identNest=0 decNest=0 type=STRING
                            StringLit "hello"
                          ASSIGNMENT
                            Ident  z identNest=0 decNest=0 type=BOOLEAN
                            BooleanLit false
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void error_assignment0(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x,y,z;
                BEGIN
                x := 3;
                y := "hello";
                z := FALSE;
                y := x;  //attempting to assign number to string
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void error_assignment1(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x,y,z;
                BEGIN
                x := 3;
                y := "hello";
                z := FALSE;
                x := z;  //type error, x is number, z is boolean
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void any_location(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x;
                BEGIN
                ? x;     //should still type this, even if 
                x := 3;  //type-determining usage follows it
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR x at nest level 0 type=NUMBER
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          INPUT
                            Ident  x identNest=0 decNest=0 type=NUMBER
                          ASSIGNMENT
                            Ident  x identNest=0 decNest=0 type=NUMBER
                            NumLit 3
                          EmptyStatement
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }


    @Test
    void error_notEnoughTypeInfo0(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a=3;
                VAR x,y,z;
                PROCEDURE p;
                  VAR j;
                  BEGIN
                     ? x;
                     IF x = 0 THEN ! y ;  //cannot type y
                     WHILE j < 24 DO CALL z
                  END;
                ! z
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }


    @Test
    void error_notEnoughTypeInfo1(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x,y,z;
                PROCEDURE p;
                	CONST x = 3;
                	? z;
                BEGIN
                ?x  //This x has not been assigned
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void PVC0(TestInfo testInfo) throws PLPException {
        String input = """
                VAR a,b;
                PROCEDURE p;
                  CONST a = 2, b=3;
                  PROCEDURE q;
                     CONST b=5;
                     VAR a;

                     PROCEDURE r;
                        VAR b;
                        BEGIN
                        b := 3;
                        a := 2;
                        ! "a=";
                        ! a;
                        ! "b=";
                        ! b;
                        END;
                    CALL r;
                  CALL q;
                BEGIN
                   CALL p;
                   a := 0;
                   b := TRUE
                   END
                   .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR a at nest level 0 type=NUMBER
                        VAR b at nest level 0 type=BOOLEAN
                      ProcDecs
                        PROCEDURE p at nesting level 0
                          BLOCK
                            ConstDecs\s
                              CONST a=2 at nest level 1 type=NUMBER
                              CONST b=3 at nest level 1 type=NUMBER
                            VarDecs none
                            ProcDecs
                              PROCEDURE q at nesting level 1
                                BLOCK
                                  ConstDecs\s
                                    CONST b=5 at nest level 2 type=NUMBER
                                  VarDecs
                                    VAR a at nest level 2 type=NUMBER
                                  ProcDecs
                                    PROCEDURE r at nesting level 2
                                      BLOCK
                                        ConstDecs  none
                                        VarDecs
                                          VAR b at nest level 3 type=NUMBER
                                        ProcDecs none
                                        STATEMENT
                                          BEGIN
                                            ASSIGNMENT
                                              Ident  b identNest=3 decNest=3 type=NUMBER
                                              NumLit 3
                                            ASSIGNMENT
                                              Ident  a identNest=3 decNest=2 type=NUMBER
                                              NumLit 2
                                            OUTPUT
                                              StringLit "a="
                                            OUTPUT
                                              ExpressionIdent  a identNest=3 decNest=2 type=NUMBER
                                            OUTPUT
                                              StringLit "b="
                                            OUTPUT
                                              ExpressionIdent  b identNest=3 decNest=3 type=NUMBER
                                            EmptyStatement
                                          END
                                        END OF STATEMENT
                                      END OF BLOCK
                                    END OF PROCEDURE r
                                  STATEMENT
                                    CALL
                                      Ident  r identNest=2 decNest=2 type=PROCEDURE
                                  END OF STATEMENT
                                END OF BLOCK
                              END OF PROCEDURE q
                            STATEMENT
                              CALL
                                Ident  q identNest=1 decNest=1 type=PROCEDURE
                            END OF STATEMENT
                          END OF BLOCK
                        END OF PROCEDURE p
                      STATEMENT
                        BEGIN
                          CALL
                            Ident  p identNest=0 decNest=0 type=PROCEDURE
                          ASSIGNMENT
                            Ident  a identNest=0 decNest=0 type=NUMBER
                            NumLit 0
                          ASSIGNMENT
                            Ident  b identNest=0 decNest=0 type=BOOLEAN
                            BooleanLit true
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void error_cannotTypez(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a = 3;
                VAR x,y,z;
                PROCEDURE p;
                   CONST a = 4;
                   VAR y,z; //this z cannot be typed
                   PROCEDURE q;
                      CONST a=5;
                      VAR z;
                      BEGIN
                        x:=a;
                        z:=a;
                        ! "this is q";
                        !x;
                        !z
                       END;
                   BEGIN
                      x := a;
                      y := a;
                      ! "this is p";
                      !x;
                      !z;
                      CALL q;
                   END;
                BEGIN
                   !a;
                   x := a;
                   y := a*2;
                   z := a*3;
                   CALL p;
                END .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }


    @Test
    void indirectRecursiveCalls(TestInfo testInfo) throws PLPException {
        String input = """
                PROCEDURE p;
                    CALL q;
                PROCEDURE q;
                    CALL p;
                ! "done"
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs
                        PROCEDURE p at nesting level 0
                          BLOCK
                            ConstDecs  none
                            VarDecs none
                            ProcDecs none
                            STATEMENT
                              CALL
                                Ident  q identNest=1 decNest=0 type=PROCEDURE
                            END OF STATEMENT
                          END OF BLOCK
                        END OF PROCEDURE p
                        PROCEDURE q at nesting level 0
                          BLOCK
                            ConstDecs  none
                            VarDecs none
                            ProcDecs none
                            STATEMENT
                              CALL
                                Ident  p identNest=1 decNest=0 type=PROCEDURE
                            END OF STATEMENT
                          END OF BLOCK
                        END OF PROCEDURE q
                      STATEMENT
                        OUTPUT
                          StringLit "done"
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void error_assigntoconstant(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a=3;
                CONST b="String", c=TRUE;
                VAR x,y;
                PROCEDURE z;
                		x:=4
                ;
                IF a#x
                THEN
                	c:=FALSE
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void testIfThen(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a=3;
                CONST b="String";
                VAR x,y,c;
                PROCEDURE z;
                		x:=4
                ;
                IF a#x
                THEN
                	c:=FALSE
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs\s
                        CONST a=3 at nest level 0 type=NUMBER
                        CONST b=String at nest level 0 type=STRING
                      VarDecs
                        VAR x at nest level 0 type=NUMBER
                        VAR y at nest level 0 type=null
                        VAR c at nest level 0 type=BOOLEAN
                      ProcDecs
                        PROCEDURE z at nesting level 0
                          BLOCK
                            ConstDecs  none
                            VarDecs none
                            ProcDecs none
                            STATEMENT
                              ASSIGNMENT
                                Ident  x identNest=1 decNest=0 type=NUMBER
                                NumLit 4
                            END OF STATEMENT
                          END OF BLOCK
                        END OF PROCEDURE z
                      STATEMENT
                        IF
                          binary expr
                            ExpressionIdent  a identNest=0 decNest=0 type=NUMBER
                            #
                            ExpressionIdent  x identNest=0 decNest=0 type=NUMBER
                        THEN
                          ASSIGNMENT
                            Ident  c identNest=0 decNest=0 type=BOOLEAN
                            BooleanLit false
                        END OF IF
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }


    @Test
    void while_do(TestInfo testInfo) throws PLPException {
        String input = """
                VAR abc;
                PROCEDURE hello;
                BEGIN
                		WHILE abc#0
                		DO
                		abc := abc-1;
                END;.
                """;
        String expected = """
                                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR abc at nest level 0 type=NUMBER
                      ProcDecs
                        PROCEDURE hello at nesting level 0
                          BLOCK
                            ConstDecs  none
                            VarDecs none
                            ProcDecs none
                            STATEMENT
                              BEGIN
                                WHILE
                                  binary expr
                                    ExpressionIdent  abc identNest=1 decNest=0 type=NUMBER
                                    #
                                    NumLit 0
                                DO
                                  ASSIGNMENT
                                    Ident  abc identNest=1 decNest=0 type=NUMBER
                                    binary expr
                                      ExpressionIdent  abc identNest=1 decNest=0 type=NUMBER
                                      -
                                      NumLit 1
                                END OF WHILE
                                EmptyStatement
                              END
                            END OF STATEMENT
                          END OF BLOCK
                        END OF PROCEDURE hello
                      STATEMENT
                        EmptyStatement
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }


    @Test
    void error_inputToProc(TestInfo testInfo) throws PLPException {
        String input = """
                PROCEDURE p;
                	PROCEDURE q;;;

                PROCEDURE q;
                	PROCEDURE p;;;

                PROCEDURE r;
                	PROCEDURE p;
                		PROCEDURE q;
                			VAR r;
                			BEGIN
                				r:=3;
                				IF r=3
                				THEN
                					WHILE r>=0
                					DO
                						r:=r+r
                			END
                		;
                		CALL q
                	;
                	? r //r is a procedure, this is not legal.
                ;
                !p
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }


    @Test
    void call(TestInfo testInfo) throws PLPException {
        String input = """
                PROCEDURE p;
                	PROCEDURE p;
                		PROCEDURE p;
                			?p
                		;
                		CALL p
                	;
                ;
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }


    @Test
    void error_ifThenAssignToConstant(TestInfo testInfo) throws PLPException {
        String input = """
                CONST d=2 , e=34, f=34, g="TRUE";
                VAR a,b,c;
                IF b<=c
                THEN
                	IF c>=d
                	THEN
                		IF d#e
                		THEN
                			IF e=f  //attempting to assign to a constant
                			THEN
                				BEGIN
                					g:=TRUE;
                					!f
                				END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void binaryExpression0(TestInfo testInfo) throws PLPException {
        String input = """
                CONST n=42, s="this is a string", x=TRUE;
                VAR a,b,c,d,e,f,g;
                BEGIN
                a := 4;
                b := n + 4;
                c := b - a;
                d := s + s;
                e := a*b;
                f := a/b;
                g := a%b;			
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs\s
                        CONST n=42 at nest level 0 type=NUMBER
                        CONST s=this is a string at nest level 0 type=STRING
                        CONST x=true at nest level 0 type=BOOLEAN
                      VarDecs
                        VAR a at nest level 0 type=NUMBER
                        VAR b at nest level 0 type=NUMBER
                        VAR c at nest level 0 type=NUMBER
                        VAR d at nest level 0 type=STRING
                        VAR e at nest level 0 type=NUMBER
                        VAR f at nest level 0 type=NUMBER
                        VAR g at nest level 0 type=NUMBER
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          ASSIGNMENT
                            Ident  a identNest=0 decNest=0 type=NUMBER
                            NumLit 4
                          ASSIGNMENT
                            Ident  b identNest=0 decNest=0 type=NUMBER
                            binary expr
                              ExpressionIdent  n identNest=0 decNest=0 type=NUMBER
                              +
                              NumLit 4
                          ASSIGNMENT
                            Ident  c identNest=0 decNest=0 type=NUMBER
                            binary expr
                              ExpressionIdent  b identNest=0 decNest=0 type=NUMBER
                              -
                              ExpressionIdent  a identNest=0 decNest=0 type=NUMBER
                          ASSIGNMENT
                            Ident  d identNest=0 decNest=0 type=STRING
                            binary expr
                              ExpressionIdent  s identNest=0 decNest=0 type=STRING
                              +
                              ExpressionIdent  s identNest=0 decNest=0 type=STRING
                          ASSIGNMENT
                            Ident  e identNest=0 decNest=0 type=NUMBER
                            binary expr
                              ExpressionIdent  a identNest=0 decNest=0 type=NUMBER
                              *
                              ExpressionIdent  b identNest=0 decNest=0 type=NUMBER
                          ASSIGNMENT
                            Ident  f identNest=0 decNest=0 type=NUMBER
                            binary expr
                              ExpressionIdent  a identNest=0 decNest=0 type=NUMBER
                              /
                              ExpressionIdent  b identNest=0 decNest=0 type=NUMBER
                          ASSIGNMENT
                            Ident  g identNest=0 decNest=0 type=NUMBER
                            binary expr
                              ExpressionIdent  a identNest=0 decNest=0 type=NUMBER
                              %
                              ExpressionIdent  b identNest=0 decNest=0 type=NUMBER
                          EmptyStatement
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void binaryExpression1(TestInfo testInfo) throws PLPException {
        String input = """
                CONST d=2 , e=34, f=34, g="TRUE";
                VAR a,b,c;
                PROCEDURE whilen;
                	VAR a,b,c;
                	WHILE ((a+b)=c)
                	DO
                		WHILE ((c-d)#e)
                		DO
                			WHILE ((e%2)#(c/2))
                			DO
                				BEGIN
                					?a;
                					!b;
                					c:=0
                				END
                	;
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs\s
                        CONST d=2 at nest level 0 type=NUMBER
                        CONST e=34 at nest level 0 type=NUMBER
                        CONST f=34 at nest level 0 type=NUMBER
                        CONST g=TRUE at nest level 0 type=STRING
                      VarDecs
                        VAR a at nest level 0 type=null
                        VAR b at nest level 0 type=null
                        VAR c at nest level 0 type=null
                      ProcDecs
                        PROCEDURE whilen at nesting level 0
                          BLOCK
                            ConstDecs  none
                            VarDecs
                              VAR a at nest level 1 type=NUMBER
                              VAR b at nest level 1 type=NUMBER
                              VAR c at nest level 1 type=NUMBER
                            ProcDecs none
                            STATEMENT
                              WHILE
                                binary expr
                                  binary expr
                                    ExpressionIdent  a identNest=1 decNest=1 type=NUMBER
                                    +
                                    ExpressionIdent  b identNest=1 decNest=1 type=NUMBER
                                  =
                                  ExpressionIdent  c identNest=1 decNest=1 type=NUMBER
                              DO
                                WHILE
                                  binary expr
                                    binary expr
                                      ExpressionIdent  c identNest=1 decNest=1 type=NUMBER
                                      -
                                      ExpressionIdent  d identNest=1 decNest=0 type=NUMBER
                                    #
                                    ExpressionIdent  e identNest=1 decNest=0 type=NUMBER
                                DO
                                  WHILE
                                    binary expr
                                      binary expr
                                        ExpressionIdent  e identNest=1 decNest=0 type=NUMBER
                                        %
                                        NumLit 2
                                      #
                                      binary expr
                                        ExpressionIdent  c identNest=1 decNest=1 type=NUMBER
                                        /
                                        NumLit 2
                                  DO
                                    BEGIN
                                      INPUT
                                        Ident  a identNest=1 decNest=1 type=NUMBER
                                      OUTPUT
                                        ExpressionIdent  b identNest=1 decNest=1 type=NUMBER
                                      ASSIGNMENT
                                        Ident  c identNest=1 decNest=1 type=NUMBER
                                        NumLit 0
                                    END
                                  END OF WHILE
                                END OF WHILE
                              END OF WHILE
                            END OF STATEMENT
                          END OF BLOCK
                        END OF PROCEDURE whilen
                      STATEMENT
                        EmptyStatement
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }


    @Test
    void binaryExpression2(TestInfo testInfo) throws PLPException {
        String input = """
                CONST n="42", s="this is a string", x="TRUE";
                VAR a,b,c,d,e,f,g;
                BEGIN
                a := "4";
                b := n + "4";
                d := s + s;			
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs\s
                        CONST n=42 at nest level 0 type=STRING
                        CONST s=this is a string at nest level 0 type=STRING
                        CONST x=TRUE at nest level 0 type=STRING
                      VarDecs
                        VAR a at nest level 0 type=STRING
                        VAR b at nest level 0 type=STRING
                        VAR c at nest level 0 type=null
                        VAR d at nest level 0 type=STRING
                        VAR e at nest level 0 type=null
                        VAR f at nest level 0 type=null
                        VAR g at nest level 0 type=null
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          ASSIGNMENT
                            Ident  a identNest=0 decNest=0 type=STRING
                            StringLit "4"
                          ASSIGNMENT
                            Ident  b identNest=0 decNest=0 type=STRING
                            binary expr
                              ExpressionIdent  n identNest=0 decNest=0 type=STRING
                              +
                              StringLit "4"
                          ASSIGNMENT
                            Ident  d identNest=0 decNest=0 type=STRING
                            binary expr
                              ExpressionIdent  s identNest=0 decNest=0 type=STRING
                              +
                              ExpressionIdent  s identNest=0 decNest=0 type=STRING
                          EmptyStatement
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }


    @Test
    void expressions0(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x,y,z;
                BEGIN
                x := 0;
                y := 1;
                z := FALSE;
                ! (x = y) * z
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR x at nest level 0 type=NUMBER
                        VAR y at nest level 0 type=NUMBER
                        VAR z at nest level 0 type=BOOLEAN
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          ASSIGNMENT
                            Ident  x identNest=0 decNest=0 type=NUMBER
                            NumLit 0
                          ASSIGNMENT
                            Ident  y identNest=0 decNest=0 type=NUMBER
                            NumLit 1
                          ASSIGNMENT
                            Ident  z identNest=0 decNest=0 type=BOOLEAN
                            BooleanLit false
                          OUTPUT
                            binary expr
                              binary expr
                                ExpressionIdent  x identNest=0 decNest=0 type=NUMBER
                                =
                                ExpressionIdent  y identNest=0 decNest=0 type=NUMBER
                              *
                              ExpressionIdent  z identNest=0 decNest=0 type=BOOLEAN
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void expressions2(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x,y,z;
                BEGIN
                ! (x = y) * z;
                x := 0;
                z := FALSE
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR x at nest level 0 type=NUMBER
                        VAR y at nest level 0 type=NUMBER
                        VAR z at nest level 0 type=BOOLEAN
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          OUTPUT
                            binary expr
                              binary expr
                                ExpressionIdent  x identNest=0 decNest=0 type=NUMBER
                                =
                                ExpressionIdent  y identNest=0 decNest=0 type=NUMBER
                              *
                              ExpressionIdent  z identNest=0 decNest=0 type=BOOLEAN
                          ASSIGNMENT
                            Ident  x identNest=0 decNest=0 type=NUMBER
                            NumLit 0
                          ASSIGNMENT
                            Ident  z identNest=0 decNest=0 type=BOOLEAN
                            BooleanLit false
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void inferxzunusedy(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x,y,z;
                BEGIN
                z := "hello";
                ! z;
                z := x;
                ! z
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR x at nest level 0 type=STRING
                        VAR y at nest level 0 type=null
                        VAR z at nest level 0 type=STRING
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          ASSIGNMENT
                            Ident  z identNest=0 decNest=0 type=STRING
                            StringLit "hello"
                          OUTPUT
                            ExpressionIdent  z identNest=0 decNest=0 type=STRING
                          ASSIGNMENT
                            Ident  z identNest=0 decNest=0 type=STRING
                            ExpressionIdent  x identNest=0 decNest=0 type=STRING
                          OUTPUT
                            ExpressionIdent  z identNest=0 decNest=0 type=STRING
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);

    }

    @Test
    void expressionsOnStrings0(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a = "hello";
                VAR x,y,z;
                BEGIN
                x := a;
                y := x+y+z
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs\s
                        CONST a=hello at nest level 0 type=STRING
                      VarDecs
                        VAR x at nest level 0 type=STRING
                        VAR y at nest level 0 type=STRING
                        VAR z at nest level 0 type=STRING
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          ASSIGNMENT
                            Ident  x identNest=0 decNest=0 type=STRING
                            ExpressionIdent  a identNest=0 decNest=0 type=STRING
                          ASSIGNMENT
                            Ident  y identNest=0 decNest=0 type=STRING
                            binary expr
                              binary expr
                                ExpressionIdent  x identNest=0 decNest=0 type=STRING
                                +
                                ExpressionIdent  y identNest=0 decNest=0 type=STRING
                              +
                              ExpressionIdent  z identNest=0 decNest=0 type=STRING
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void expressionsOnStrings1(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a = "hello";
                VAR x,y,z;
                BEGIN
                !y;
                x := a;
                y := x+y+z
                END
                .
                """;
        String expected = """
                                
                  PROGRAM
                    BLOCK
                      ConstDecs\s
                        CONST a=hello at nest level 0 type=STRING
                      VarDecs
                        VAR x at nest level 0 type=STRING
                        VAR y at nest level 0 type=STRING
                        VAR z at nest level 0 type=STRING
                      ProcDecs none
                      STATEMENT
                        BEGIN
                          OUTPUT
                            ExpressionIdent  y identNest=0 decNest=0 type=STRING
                          ASSIGNMENT
                            Ident  x identNest=0 decNest=0 type=STRING
                            ExpressionIdent  a identNest=0 decNest=0 type=STRING
                          ASSIGNMENT
                            Ident  y identNest=0 decNest=0 type=STRING
                            binary expr
                              binary expr
                                ExpressionIdent  x identNest=0 decNest=0 type=STRING
                                +
                                ExpressionIdent  y identNest=0 decNest=0 type=STRING
                              +
                              ExpressionIdent  z identNest=0 decNest=0 type=STRING
                        END
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expected);
    }

    @Test
    void test_proc1(TestInfo testInfo) throws PLPException {
        String input = """
                PROCEDURE p; ! "Hi";
                .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs
                        PROCEDURE p at nesting level 0
                          BLOCK
                            ConstDecs  none
                            VarDecs none
                            ProcDecs none
                            STATEMENT
                              OUTPUT
                                StringLit "Hi"
                            END OF STATEMENT
                          END OF BLOCK
                        END OF PROCEDURE p
                      STATEMENT
                        EmptyStatement
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void test1(TestInfo testInfo) throws PLPException {
        String input = """
                PROCEDURE p; ! "Hi";
                .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs
                        PROCEDURE p at nesting level 0
                          BLOCK
                            ConstDecs  none
                            VarDecs none
                            ProcDecs none
                            STATEMENT
                              OUTPUT
                                StringLit "Hi"
                            END OF STATEMENT
                          END OF BLOCK
                        END OF PROCEDURE p
                      STATEMENT
                        EmptyStatement
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void test2(TestInfo testInfo) throws PLPException {
        String input = """
                CONST c = "Hi";
                .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs\s
                        CONST c=Hi at nest level 0 type=STRING
                      VarDecs none
                      ProcDecs none
                      STATEMENT
                        EmptyStatement
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void test3(TestInfo testInfo) throws PLPException {
        String input = """
                VAR v;
                .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR v at nest level 0 type=null
                      ProcDecs none
                      STATEMENT
                        EmptyStatement
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void test4(TestInfo testInfo) throws PLPException {
        String input = """
                VAR v;
                v := "Hi"
                .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs
                        VAR v at nest level 0 type=STRING
                      ProcDecs none
                      STATEMENT
                        ASSIGNMENT
                          Ident  v identNest=0 decNest=0 type=STRING
                          StringLit "Hi"
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void test5(TestInfo testInfo) throws PLPException {
        String input = """
                ! 5
                .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs none
                      STATEMENT
                        OUTPUT
                          NumLit 5
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void test6(TestInfo testInfo) throws PLPException {
        String input = """
                ! "Hi"
                .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs none
                      STATEMENT
                        OUTPUT
                          StringLit "Hi"
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void test7(TestInfo testInfo) throws PLPException {
        String input = """
                ! TRUE
                .""";
        String expeccted = """
                  
                  PROGRAM
                    BLOCK
                      ConstDecs  none
                      VarDecs none
                      ProcDecs none
                      STATEMENT
                        OUTPUT
                          BooleanLit true
                      END OF STATEMENT
                    END OF BLOCK
                  END OF PROGRAM
                """;
        runTest(input, testInfo, expeccted);
    }

    @Test
    void test8(TestInfo testInfo) throws PLPException {
        String input = """
                CONST c = "Hi";
                c := "Bye" // cannot reassign const
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test9(TestInfo testInfo) throws PLPException {
        String input = """
                VAR v;
                BEGIN
                  v := 0;
                  v := "Hi" // cannot assign string to type number
                END
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test10(TestInfo testInfo) throws PLPException {
        String input = """
                VAR v;
                BEGIN
                  v := 0;
                  CALL v // can only call procedures
                END
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test11(TestInfo testInfo) throws PLPException {
        String input = """
                PROCEDURE p; ! "Hi";
                ? p // cannot be procedure
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test12(TestInfo testInfo) throws PLPException {
        String input = """
                PROCEDURE p; ! "Hi";
                ! p // cannot be procedure
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test13(TestInfo testInfo) throws PLPException {
        String input = """
                CONST x = 1;
                IF (x) THEN ! x // condition must be boolean
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test14(TestInfo testInfo) throws PLPException {
        String input = """
                CONST x = 1;
                WHILE (x) DO ! x // condition must be boolean
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test15(TestInfo testInfo) throws PLPException {
        String input = """
                ! 1 + "Hi" // incompatible types
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test16(TestInfo testInfo) throws PLPException {
        String input = """
                ! 1 - "Hi" // incompatible types
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test17(TestInfo testInfo) throws PLPException {
        String input = """
                ! 1 * "Hi" // incompatible types
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test18(TestInfo testInfo) throws PLPException {
        String input = """
                ! 1 >= "Hi" // incompatible types
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test19(TestInfo testInfo) throws PLPException {
        String input = """
                ! "Bye" - "Hi" // must be numbers
                .""";
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void ss_testCannotInferType(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x, y, z;
                BEGIN
                z:= FALSE;
                !(x=y) * z
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void ss_testProcedureAssignment(TestInfo testInfo) {
        String input = """
                PROCEDURE A;
                ;
                      PROCEDURE B;
                      ;
                      A := B  //Cannot assign procedure to another
                      .
                      """;
        runTest(input, testInfo, TypeCheckException.class);

    }


    @Test
    void ss_testAssignIntToString(TestInfo testInfo) {
        String input = """
                VAR x;
                BEGIN
                x:= 5;
                x:= "test"
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void ss_testInferType(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x, y, z;
                BEGIN
                y := 1;
                z := FALSE;
                ! (x = y) * z // Inferred type of x
                END
                .
                """;
        runTest(input, testInfo);

    }

    @Test
    void ss_testIncorrectAssignmentComparison(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x, y, z;
                BEGIN
                x := 10;
                y := "hello";
                z := FALSE;
                ! (x = y) * z
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void ss_testIncorrectGuardCondition(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x, y, z;
                BEGIN
                x := 10;
                y := "hello";
                z := FALSE;
                IF y THEN ! y ;
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void ss_testIncorrectWhileGuardCondition(TestInfo testInfo) throws PLPException {
        String input = """
                CONST x = 5;
                BEGIN
                    WHILE x
                    DO
                    !x;
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }


    @Test
    void ss_testIncorrectTypeAfterAssign(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x, y, z;
                BEGIN
                x := 10;
                z := FALSE;
                ! (x = y) * z; //Type error
                y := "hello"
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void inputToConst(TestInfo testInfo) throws PLPException {
        String input = """
                      CONST e = 5;
                ? e
                .
                      """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void nithin0_test(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a="hello", b =1, c=TRUE;
                CONST d=0;
                VAR x,y,z;
                BEGIN
                !y;
                x := a;
                y := x+y+z;
                //z := 0
                END
                .
                """;
        runTest(input, testInfo);
    }

    @Test
    void nithin1_test(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a="hello", b =1, c=TRUE;
                CONST d=0;
                VAR x,y,z;
                BEGIN
                !y;
                x := a;
                y := x+y+z;
                z := 0  //type not compatible
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void nithin2_test(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a="hello", b =1, c=TRUE;
                CONST d=FALSE;
                VAR x,y,z;
                BEGIN
                ! ((x=y)=d) * z;  //x, y cannot be inferred
                z := TRUE
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void nithin3_test(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a="hello", b=1, c=TRUE;
                CONST d=FALSE;
                VAR x,y,z;
                PROCEDURE e;
                    VAR x;   //creating a new local variable to replace global x
                    PROCEDURE f;
                    x:=4   //type assigned to this local variable
                    ;
                CALL f
                ;
                BEGIN
                CALL e;
                ! ((x=y)=d) * z;  //x, y cannot be inferred
                z := TRUE
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void nithin4_test(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a="hello", b=1, c=TRUE;
                CONST d=FALSE;
                VAR x,y,z;
                PROCEDURE e;
                    //VAR x;      no new variable declared
                    PROCEDURE f;
                    x:=4          //Inferring the type of the global variable in this scope
                    ;
                CALL f
                ;
                BEGIN
                CALL e;
                ! ((x=y)=d) * z;  //x, y can be inferred
                z := TRUE
                END
                .
                """;
        runTest(input, testInfo);
    }

    @Test
    void nithin5_test(TestInfo testInfo) throws PLPException {
        String input = """
                CONST a="hello", b=1, c=TRUE;
                CONST d=FALSE;
                VAR x,y,z;
                PROCEDURE e;
                ;
                BEGIN
                CALL e;
                CALL x
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void error_reassignVar(TestInfo testInfo) throws PLPException {
        String input = """
                VAR x;
                BEGIN
                x:=5;
                x:="test"	// we are not allowed to reassign a var to a different type
                END
                .
                """;
        runTest(input, testInfo, TypeCheckException.class);
    }

    @Test
    void test42(TestInfo testInfo) throws PLPException {
        String input = """
            CONST a=0;
            VAR b;
            a:=b
            .
            """;
        runTest(input, testInfo, TypeCheckException.class);
    }


}


