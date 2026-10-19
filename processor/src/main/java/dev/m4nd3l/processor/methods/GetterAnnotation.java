package dev.m4nd3l.processor.methods;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.core.Getter;
import dev.m4nd3l.processor.data.GetterData;
import dev.m4nd3l.utils.AccessLevel;

import java.util.*;

public class GetterAnnotation extends ClassUtilsAnnotationProcessor {
    @Override
    public List<JCTree> manage(JCTree.JCClassDecl classDeclaration, Names names, TreeMaker treeMaker, Context context) {
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

            Set<JCTree.JCMethodDecl> generatedGetters = build(new GetterData(field, otherNames, level, classDeclaration, names, treeMaker));
            for (JCTree.JCMethodDecl getterMethod : generatedGetters) newDefinitions = newDefinitions.append(getterMethod);
        }

        return newDefinitions;
    }

    private static Set<JCTree.JCMethodDecl> build(GetterData data) {
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
}
