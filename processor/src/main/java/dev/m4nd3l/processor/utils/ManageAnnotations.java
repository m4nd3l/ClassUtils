package dev.m4nd3l.processor.utils;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.core.Getter;
import dev.m4nd3l.core.Setter;

import java.util.Set;

import static dev.m4nd3l.processor.utils.CreateMethodsAST.buildGetterMethod;
import static dev.m4nd3l.processor.utils.CreateMethodsAST.buildSetterMethod;

public class ManageAnnotations {
    public static List<JCTree> manageGetterAnnotation(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker) {
        Getter classGetterAnnotation = classDeclaration.sym.getAnnotation(Getter.class);
        boolean isClassLevelGetter = classGetterAnnotation != null;

        List<JCTree> newDefinitions = com.sun.tools.javac.util.List.nil();

        for (JCTree definition : classDeclaration.defs) {
            newDefinitions = newDefinitions.append(definition);

            if (!(definition instanceof JCTree.JCVariableDecl field)) continue;

            if (field.sym == null) continue;

            Getter getterAnnotation = field.sym.getAnnotation(Getter.class);
            Getter.Exclude excludeAnnotation = field.sym.getAnnotation(Getter.Exclude.class);
            Getter.AlternativeNames alternativeNamesAnnotation = field.sym.getAnnotation(Getter.AlternativeNames.class);

            if (getterAnnotation == null && !isClassLevelGetter) continue; // No @Getter in field nor in class
            if (excludeAnnotation != null) continue; // If there is @Getter.Exclude, skip

            String[] otherNames = null;
            if (alternativeNamesAnnotation != null) otherNames = alternativeNamesAnnotation.value();

            Set<JCTree.JCMethodDecl> generatedGetters = buildGetterMethod(field, names, treeMaker, otherNames);
            for (JCTree.JCMethodDecl getterMethod : generatedGetters) newDefinitions = newDefinitions.append(getterMethod);
        }

        return newDefinitions;
    }

    public static List<JCTree> manageSetterAnnotation(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker) {
        Setter classSetterAnnotation = classDeclaration.sym.getAnnotation(Setter.class);
        boolean isClassLevelSetter = classSetterAnnotation != null;

        List<JCTree> newDefinitions = com.sun.tools.javac.util.List.nil();

        for (JCTree definition : classDeclaration.defs) {
            newDefinitions = newDefinitions.append(definition);

            if (!(definition instanceof JCTree.JCVariableDecl field)) continue;

            if (field.sym == null) continue;

            Setter setterAnnotation = field.sym.getAnnotation(Setter.class);
            Setter.Exclude excludeAnnotation = field.sym.getAnnotation(Setter.Exclude.class);
            Setter.AlternativeNames alternativeNamesAnnotation = field.sym.getAnnotation(Setter.AlternativeNames.class);

            if (setterAnnotation == null && !isClassLevelSetter) continue; // No @Setter in field nor in class
            if (excludeAnnotation != null) continue; // If there is @Setter.Exclude, skip

            boolean returnItself = isClassLevelSetter ? classSetterAnnotation.value() : setterAnnotation.value();

            String[] otherNames = null;
            if (alternativeNamesAnnotation != null) otherNames = alternativeNamesAnnotation.value();

            Set<JCTree.JCMethodDecl> generatedSetters = buildSetterMethod(classDeclaration, field, names, treeMaker, otherNames, returnItself);
            for (JCTree.JCMethodDecl setterMethod : generatedSetters) newDefinitions = newDefinitions.append(setterMethod);
        }

        return newDefinitions;
    }
}
