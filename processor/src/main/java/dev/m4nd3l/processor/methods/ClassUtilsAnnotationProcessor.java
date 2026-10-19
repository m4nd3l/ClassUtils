package dev.m4nd3l.processor.methods;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.processor.data.Data;
import dev.m4nd3l.utils.AccessLevel;

import java.util.Map;

public abstract class ClassUtilsAnnotationProcessor {
    public abstract List<JCTree> manage(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker, Context context);

    protected static JCTree.JCMethodDecl generateMethod(long mod, String name, JCTree.JCExpression returnExpression, Type returnType,
                                                      Map<String, List<JCTree.JCExpression>> annotationsData, List<JCTree.JCVariableDecl> params, JCTree.JCBlock body, Data data) {
        TreeMaker treeMaker = data.tm();
        Names names = data.getNames();
        JCTree.JCClassDecl classDeclaration = data.getClassDeclaration();

        Name methodName = names.fromString(name);

        List<JCTree.JCAnnotation> annotations = List.nil();
        for (Map.Entry<String, List<JCTree.JCExpression>> annotation : annotationsData.entrySet()) {
            annotations = annotations.append(
                    treeMaker.Annotation(
                            treeMaker.Ident(names.fromString(annotation.getKey())),
                            annotation.getValue()));
        }


        JCTree.JCModifiers mods = treeMaker.Modifiers(mod, annotations);

        List<Type> paramTypes = List.nil();
        for (JCTree.JCVariableDecl param : params) paramTypes = paramTypes.append(param.vartype.type);

        Symbol.MethodSymbol methodSymbol = new Symbol.MethodSymbol(
                mod,
                methodName,
                new Type.MethodType(paramTypes, returnType, List.nil(), classDeclaration.sym),
                classDeclaration.sym
        );

        for (JCTree.JCVariableDecl param : params) param.sym = new Symbol.VarSymbol(Flags.PARAMETER, param.name, param.vartype.type, methodSymbol);

        JCTree.JCMethodDecl methodDeclaration = treeMaker.MethodDef(mods, methodName, returnExpression, List.nil(), params, List.nil(), body, null);

        methodDeclaration.sym = methodSymbol;

        boolean alreadyExists = false;
        for (Symbol s : classDeclaration.sym.members().getSymbolsByName(methodName)) {
            if (s instanceof Symbol.MethodSymbol && s.type.getParameterTypes().size() == paramTypes.size()) {
                alreadyExists = true;
                break;
            }
        }

        if (!alreadyExists) classDeclaration.sym.members().enter(methodSymbol);

        return methodDeclaration;
    }

    protected static JCTree.JCExpression concatenation(String name, Names names, TreeMaker treeMaker) {
        String[] parts = name.split("\\.");
        if (parts.length == 0) return null;
        JCTree.JCExpression expression = treeMaker.Ident(names.fromString(parts[0]));
        for (int i = 1; i < parts.length; i++) expression = treeMaker.Select(expression, names.fromString(parts[i]));
        return expression;
    }

    protected static long getModifier(AccessLevel level) {
        return switch (level) {
            case PUBLIC -> Flags.PUBLIC;
            case PROTECTED -> Flags.PROTECTED;
            case PACKAGE -> 0L;
            case PRIVATE -> Flags.PRIVATE;
        };
    }
}
