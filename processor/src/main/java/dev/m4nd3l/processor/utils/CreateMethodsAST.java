package dev.m4nd3l.processor.utils;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.*;

public class CreateMethodsAST {
    public static Set<JCTree.JCMethodDecl> buildGetterMethod(JCTree.JCVariableDecl field, Names names, TreeMaker treeMaker, String[] otherNames) {
        int length = otherNames != null ? otherNames.length + 1 : 1;

        Set<String> allNamesSet = new HashSet<>(length);
        allNamesSet.add(field.getName().toString());

        if (otherNames != null) Collections.addAll(allNamesSet, otherNames);

        Set<JCTree.JCMethodDecl> getters = new HashSet<>(allNamesSet.size());
        List<String> allNames = new ArrayList<>(allNamesSet);

        for (int i = 0; i < allNamesSet.size(); i++) {
            String alternativeName = allNames.get(i);
            String methodName_str = "get" + Character.toUpperCase(alternativeName.charAt(0)) + alternativeName.substring(1);
            Name methodName = names.fromString(methodName_str);

            JCTree.JCFieldAccess selectField = treeMaker.Select(treeMaker.Ident(names._this), field.name); // Access of the field through "this" (this.field)
            JCTree.JCReturn returnStatement = treeMaker.Return(selectField); // Return statement of the field access expression (return this.field)

            JCTree.JCBlock body = treeMaker.Block(0, com.sun.tools.javac.util.List.of(returnStatement)); // Block statement containing the return code ({ return this.field; })

            getters.add(treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PUBLIC),
                    methodName,
                    field.vartype,
                    com.sun.tools.javac.util.List.nil(),
                    com.sun.tools.javac.util.List.nil(),
                    com.sun.tools.javac.util.List.nil(),
                    body,
                    null
            ));
        }

        return getters;
    }

    public static Set<JCTree.JCMethodDecl> buildSetterMethod(JCTree.JCClassDecl _class, JCTree.JCVariableDecl field, Names names, TreeMaker treeMaker, String[] otherNames, boolean returnItself) {
        int length = otherNames != null ? otherNames.length + 1 : 1;

        Set<String> allNamesSet = new HashSet<>(length);
        allNamesSet.add(field.getName().toString());

        if (otherNames != null) Collections.addAll(allNamesSet, otherNames);

        Set<JCTree.JCMethodDecl> setters = new HashSet<>(allNamesSet.size());
        List<String> allNames = new ArrayList<>(allNamesSet);

        for (int i = 0; i < allNamesSet.size(); i++) {
            String alternativeName = allNames.get(i);
            String methodName_str = "set" + Character.toUpperCase(alternativeName.charAt(0)) + alternativeName.substring(1);
            Name methodName = names.fromString(methodName_str);

            Name parameterName = names.fromString(alternativeName);
            JCTree.JCVariableDecl parameterVariableDeclaration = treeMaker.VarDef(
                    treeMaker.Modifiers(Flags.PARAMETER),
                    parameterName,
                    field.vartype,
                    null
            );

            JCTree.JCFieldAccess selectField = treeMaker.Select(treeMaker.Ident(names._this), field.name); // Access of the field through "this" (this.field)
            JCTree.JCIdent parameterIdentifier = treeMaker.Ident(parameterName); // Access of the parameter (parameter)
            JCTree.JCAssign assignmentStatement = treeMaker.Assign(selectField, parameterIdentifier);

            com.sun.tools.javac.util.ListBuffer<JCTree.JCStatement> statements = new com.sun.tools.javac.util.ListBuffer<>();
            statements.append(treeMaker.Exec(assignmentStatement));

            if (returnItself) {
                JCTree.JCIdent thisIdentifier = treeMaker.Ident(names._this);
                JCTree.JCReturn returnStatement = treeMaker.Return(thisIdentifier); // Return statement of the class (return this)

                statements.append(returnStatement);
            }

            JCTree.JCBlock body = treeMaker.Block(0, statements.toList()); // Block statement containing the return code ({ this.field = field; } or { this.field = field; return this; })

            setters.add(treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PUBLIC),
                    methodName,
                    returnItself ? treeMaker.Ident(_class.name) : treeMaker.TypeIdent(TypeTag.VOID),
                    com.sun.tools.javac.util.List.nil(),
                    com.sun.tools.javac.util.List.of(parameterVariableDeclaration),
                    com.sun.tools.javac.util.List.nil(),
                    body,
                    null
            ));
        }

        return setters;
    }
}
