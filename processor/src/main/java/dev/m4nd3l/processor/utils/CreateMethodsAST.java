package dev.m4nd3l.processor.utils;

import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.List;
import dev.m4nd3l.processor.data.Data;
import dev.m4nd3l.processor.data.GetterData;
import dev.m4nd3l.processor.data.SetterData;
import dev.m4nd3l.processor.data.ToStringData;
import dev.m4nd3l.utils.AccessLevel;

import java.util.*;

public class CreateMethodsAST {
    public static Set<JCTree.JCMethodDecl> buildGetterMethod(GetterData data) {
        String[] otherNames = data.getOtherNames();

        int length = otherNames != null ? otherNames.length + 1 : 1;

        Set<String> allNamesSet = new HashSet<>(length);
        allNamesSet.add(data.getField().getName().toString());

        if (otherNames != null) Collections.addAll(allNamesSet, otherNames);

        Set<JCTree.JCMethodDecl> getters = new HashSet<>(allNamesSet.size());
        java.util.List<String> allNames = new ArrayList<>(allNamesSet);

        for (int i = 0; i < allNamesSet.size(); i++) {
            String alternativeName = allNames.get(i);
            String methodName = "get" + Character.toUpperCase(alternativeName.charAt(0)) + alternativeName.substring(1);

            JCTree.JCFieldAccess selectField = data.tm().Select(data.tm().Ident(data.getNames()._this), data.getField().name); // Access of the field through "this" (this.field)
            JCTree.JCReturn returnStatement = data.tm().Return(selectField); // Return statement of the field access expression (return this.field)

            JCTree.JCBlock body = data.tm().Block(0, List.of(returnStatement)); // Block statement containing the return code ({ return this.field; })

            Type returnType = data.getField().sym != null ? data.getField().sym.type : null;

            getters.add(generateMethod(getModifier(data.getLevel()), methodName, data.getField().vartype, returnType, new HashMap<>(), List.nil(), body, data));
        }

        return getters;
    }

    public static Set<JCTree.JCMethodDecl> buildSetterMethod(SetterData data) {
        String[] otherNames = data.getOtherNames();

        int length = otherNames != null ? otherNames.length + 1 : 1;

        Set<String> allNamesSet = new HashSet<>(length);
        allNamesSet.add(data.getField().getName().toString());

        if (otherNames != null) Collections.addAll(allNamesSet, otherNames);

        Set<JCTree.JCMethodDecl> setters = new HashSet<>(allNamesSet.size());
        java.util.List<String> allNames = new ArrayList<>(allNamesSet);

        for (int i = 0; i < allNamesSet.size(); i++) {
            String alternativeName = allNames.get(i);
            String methodName = "set" + Character.toUpperCase(alternativeName.charAt(0)) + alternativeName.substring(1);

            Name parameterName = data.getNames().fromString(alternativeName);
            JCTree.JCVariableDecl parameterVariableDeclaration = data.tm().VarDef(
                    data.tm().Modifiers(Flags.PARAMETER),
                    parameterName,
                    data.getField().vartype,
                    null
            );

            JCTree.JCFieldAccess selectField = data.tm().Select(data.tm().Ident(data.getNames()._this), data.getField().name); // Access of the field through "this" (this.field)
            JCTree.JCIdent parameterIdentifier = data.tm().Ident(parameterName); // Access of the parameter (parameter)
            JCTree.JCAssign assignmentStatement = data.tm().Assign(selectField, parameterIdentifier);

            ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
            statements.append(data.tm().Exec(assignmentStatement));

            if (data.returnItself()) {
                JCTree.JCIdent thisIdentifier = data.tm().Ident(data.getNames()._this);
                JCTree.JCReturn returnStatement = data.tm().Return(thisIdentifier); // Return statement of the class (return this)

                statements.append(returnStatement);
            }

            JCTree.JCBlock body = data.tm().Block(0, statements.toList()); // Block statement containing the return code ({ this.field = field; } or { this.field = field; return this; })

            Type returnType = data.returnItself() ? data.getClassDeclaration().sym.type : new Type.JCVoidType();

            setters.add(generateMethod(getModifier(data.getLevel()), methodName,
                    data.returnItself() ? data.tm().Ident(data.getClassDeclaration().name) : data.tm().TypeIdent(TypeTag.VOID),
                    returnType, new HashMap<>(), List.of(parameterVariableDeclaration), body, data));
        }

        return setters;
    }

    public static JCTree.JCMethodDecl buildToStringMethod(ToStringData data) {
        ArrayList<JCTree.JCVariableDecl> toSerialize = data.getToSerialize();

        List<JCTree.JCExpression> fieldNameExpressions = List.nil();
        if (toSerialize != null) {
            for (JCTree.JCVariableDecl variableDeclaration : toSerialize) {
                JCTree.JCLiteral fieldLiteral = data.tm().Literal(TypeTag.CLASS, variableDeclaration.name.toString());
                fieldNameExpressions = fieldNameExpressions.append(fieldLiteral);
            }
        }

        JCTree.JCExpression listExpression;
        if (toSerialize == null || toSerialize.isEmpty()) listExpression = data.tm().Literal(TypeTag.BOT, null);
        else listExpression = data.tm().Apply(List.nil(),
                concatenation("java.util.List.of", data.getNames(), data.tm()), fieldNameExpressions); // java.util.List.of(field1, field2...)

        List<JCTree.JCExpression> arguments = List.of(
                data.tm().Ident(data.getNames()._this),
                concatenation("dev.m4nd3l.utils.ToStringType." + data.getFormatType().name(), data.getNames(), data.tm()),
                data.tm().Literal(TypeTag.BOOLEAN, data.isCompactValue() ? 1 : 0),
                listExpression
        ); // (this, dev.m4nd3l.utils.ToStringType.TYPE, true/false, java.util.List.of(field1, field2...))

        JCTree.JCMethodInvocation methodInvocation = data.tm().Apply(List.nil(),
                concatenation("dev.m4nd3l.classutils.JacksonToStringUtils_ClassUtilsInternalGeneratedUtilClass.serialize", data.getNames(), data.tm()), arguments);
        JCTree.JCStatement returnStatement = data.tm().Return(methodInvocation);

        List<JCTree.JCStatement> statements = List.of(returnStatement);
        JCTree.JCBlock body = data.tm().Block(0, statements);

        return generateMethod(Flags.PUBLIC, "toString",
                data.tm().Ident(data.getNames().fromString("String")), Symtab.instance(data.getContext()).stringType, Map.of("Override", List.nil()), List.nil(), body, data);
    }

    private static JCTree.JCExpression concatenation(String name, Names names, TreeMaker treeMaker) {
        String[] parts = name.split("\\.");
        if (parts.length == 0) return null;
        JCTree.JCExpression expression = treeMaker.Ident(names.fromString(parts[0]));
        for (int i = 1; i < parts.length; i++) expression = treeMaker.Select(expression, names.fromString(parts[i]));
        return expression;
    }

    private static long getModifier(AccessLevel level) {
        return switch (level) {
            case PUBLIC -> Flags.PUBLIC;
            case PROTECTED -> Flags.PROTECTED;
            case PACKAGE -> 0L;
            case PRIVATE -> Flags.PRIVATE;
        };
    }

    private static JCTree.JCMethodDecl generateMethod(long mod, String name, JCTree.JCExpression returnExpression, Type returnType,
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
        classDeclaration.sym.members().enter(methodSymbol);

        return methodDeclaration;
    }
}
