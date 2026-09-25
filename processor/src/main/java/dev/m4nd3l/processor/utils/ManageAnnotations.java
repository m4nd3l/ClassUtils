package dev.m4nd3l.processor.utils;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.core.Getter;
import dev.m4nd3l.core.Setter;
import dev.m4nd3l.core.ToString;
import dev.m4nd3l.processor.data.GetterData;
import dev.m4nd3l.processor.data.SetterData;
import dev.m4nd3l.processor.data.ToStringData;
import dev.m4nd3l.utils.AccessLevel;
import dev.m4nd3l.utils.ToStringType;

import javax.annotation.processing.ProcessingEnvironment;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.Set;

import static dev.m4nd3l.processor.utils.CreateMethodsAST.*;

public class ManageAnnotations {
    public static List<JCTree> manageGetterAnnotation(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker) {
        Getter classGetterAnnotation = classDeclaration.sym.getAnnotation(Getter.class);
        boolean isClassLevelGetter = classGetterAnnotation != null;

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

            Getter getterAnnotation = field.sym.getAnnotation(Getter.class);
            Getter.Exclude excludeAnnotation = field.sym.getAnnotation(Getter.Exclude.class);
            Getter.AlternativeNames alternativeNamesAnnotation = field.sym.getAnnotation(Getter.AlternativeNames.class);

            if (getterAnnotation == null && !isClassLevelGetter) continue; // No @Getter in field nor in class
            if (excludeAnnotation != null) continue; // If there is @Getter.Exclude, skip

            String[] otherNames = null;
            AccessLevel level = getterAnnotation != null ? getterAnnotation.accessLevel() : classGetterAnnotation.accessLevel();
            if (alternativeNamesAnnotation != null) otherNames = alternativeNamesAnnotation.value();

            Set<JCTree.JCMethodDecl> generatedGetters = buildGetterMethod(new GetterData(field, otherNames, level, classDeclaration, names, treeMaker));
            for (JCTree.JCMethodDecl getterMethod : generatedGetters) newDefinitions = newDefinitions.append(getterMethod);
        }

        return newDefinitions;
    }

    public static List<JCTree> manageSetterAnnotation(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker) {
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

            Set<JCTree.JCMethodDecl> generatedSetters = buildSetterMethod(new SetterData(field, otherNames, returnItself, level, classDeclaration, names, treeMaker));
            for (JCTree.JCMethodDecl setterMethod : generatedSetters) newDefinitions = newDefinitions.append(setterMethod);
        }

        return newDefinitions;
    }

    public static List<JCTree> manageToStringAnnotation(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker, Context context) {
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

        JCTree.JCMethodDecl toStringMethod = buildToStringMethod(new ToStringData(toSerialize, formatType, compactValue, context, classDeclaration, names, treeMaker));

        return newDefinitions.append(toStringMethod);
    }
}
