package dev.m4nd3l.processor.methods;

import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;
import dev.m4nd3l.core.ToString;
import dev.m4nd3l.processor.data.ToStringData;
import dev.m4nd3l.processor.utility.SerializationUtilityClass;
import dev.m4nd3l.utils.ToStringType;

import java.util.*;

public class ToStringAnnotation extends ClassUtilsAnnotationProcessor {
    @Override
    public List<JCTree> manage(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker, Context context) {
        ToString classToStringAnnotation = classDeclaration.sym.getAnnotation(ToString.class);
        if (classToStringAnnotation == null) return classDeclaration.defs;

        ToStringType formatType = classToStringAnnotation.value();
        boolean compactValue = classToStringAnnotation.compact();

        List<JCTree> newDefinitions = List.nil();
        ArrayList<JCTree.JCVariableDecl> toSerialize = new ArrayList<>();

        for (JCTree definition : classDeclaration.defs) {
            newDefinitions = newDefinitions.append(definition);

            if (!(definition instanceof JCTree.JCVariableDecl field)) continue;

            if (field.sym == null) {
                for (Symbol s : classDeclaration.sym.members().getSymbols()) {
                    if (s instanceof Symbol.VarSymbol && s.name.equals(field.name)) {
                        field.sym = (Symbol.VarSymbol) s;
                        break;
                    }
                }
            }

            ToString.Exclude excludeAnnotation = field.sym.getAnnotation(ToString.Exclude.class);

            if (excludeAnnotation == null) toSerialize.add(field);
        }

        JCTree.JCMethodDecl toStringMethod = build(new ToStringData(toSerialize, formatType, compactValue, context, classDeclaration, names, treeMaker));

        return newDefinitions.append(toStringMethod);
    }

    private static JCTree.JCMethodDecl build(ToStringData data) {
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

        SerializationUtilityClass.getOrCreateUtilityClass(data.getContext(), data.getFormatType());

        JCTree.JCMethodInvocation methodInvocation = data.tm().Apply(List.nil(),
                concatenation(SerializationUtilityClass.getName(data.getFormatType()), data.getNames(), data.tm()), arguments);
        JCTree.JCStatement returnStatement = data.tm().Return(methodInvocation);

        List<JCTree.JCStatement> statements = List.of(returnStatement);
        JCTree.JCBlock body = data.tm().Block(0, statements);

        return generateMethod(Flags.PUBLIC, "toString",
                data.tm().Ident(data.getNames().fromString("String")), Symtab.instance(data.getContext()).stringType, Map.of("Override", List.nil()), List.nil(), body, data);
    }
}
