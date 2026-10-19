package dev.m4nd3l.processor.methods;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;
import dev.m4nd3l.core.Setter;
import dev.m4nd3l.processor.data.SetterData;
import dev.m4nd3l.utils.AccessLevel;

import java.util.*;

public class SetterAnnotation extends ClassUtilsAnnotationProcessor {
    @Override
    public List<JCTree> manage(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker, Context context) {
        Setter classSetterAnnotation = classDeclaration.sym.getAnnotation(Setter.class);
        boolean isClassLevelSetter = classSetterAnnotation != null;

        List<JCTree> newDefinitions = List.nil();

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

            Setter setterAnnotation = field.sym.getAnnotation(Setter.class);
            Setter.Exclude excludeAnnotation = field.sym.getAnnotation(Setter.Exclude.class);
            Setter.AlternativeNames alternativeNamesAnnotation = field.sym.getAnnotation(Setter.AlternativeNames.class);

            if (setterAnnotation == null && !isClassLevelSetter) continue; // No @Setter in field nor in class
            if (excludeAnnotation != null) continue; // If there is @Setter.Exclude, skip

            boolean returnItself = isClassLevelSetter ? classSetterAnnotation.value() : setterAnnotation.value();

            String[] otherNames = null;
            AccessLevel level = setterAnnotation != null ? setterAnnotation.accessLevel() : classSetterAnnotation.accessLevel();
            if (alternativeNamesAnnotation != null) otherNames = alternativeNamesAnnotation.value();

            Set<JCTree.JCMethodDecl> generatedSetters = build(new SetterData(field, otherNames, returnItself, level, classDeclaration, names, treeMaker));
            for (JCTree.JCMethodDecl setterMethod : generatedSetters) newDefinitions = newDefinitions.append(setterMethod);
        }

        return newDefinitions;
    }

    private static Set<JCTree.JCMethodDecl> build(SetterData data) {
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

            JCTree.JCBlock body = data.tm().Block(0, statements.toList()); // Block statement containing the return code ({ this.field = field; }
                                                                                 // or { this.field = field; return this; })

            Type returnType = data.returnItself() ? data.getClassDeclaration().sym.type : new Type.JCVoidType();

            setters.add(generateMethod(getModifier(data.getLevel()), methodName,
                    data.returnItself() ? data.tm().Ident(data.getClassDeclaration().name) : data.tm().TypeIdent(TypeTag.VOID),
                    returnType, new HashMap<>(), List.of(parameterVariableDeclaration), body, data));
        }

        return setters;
    }
}
