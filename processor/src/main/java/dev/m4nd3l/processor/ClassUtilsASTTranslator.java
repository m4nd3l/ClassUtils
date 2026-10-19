package dev.m4nd3l.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.processor.methods.GetterAnnotation;
import dev.m4nd3l.processor.methods.SetterAnnotation;
import dev.m4nd3l.processor.methods.ToStringAnnotation;
import dev.m4nd3l.processor.utility.SerializationUtilityClass;

import java.util.Map;

public class ClassUtilsASTTranslator extends TreeTranslator {
    private TreeMaker treeMaker;
    private Names names;
    private Context context;

    public ClassUtilsASTTranslator(TreeMaker treeMaker, Names names, Context context) {
        this.treeMaker = treeMaker;
        this.names = names;
        this.context = context;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl classDeclaration) {
        super.visitClassDef(classDeclaration);

        if (classDeclaration.sym == null) return;

        classDeclaration.defs = new GetterAnnotation().manage(classDeclaration, names, treeMaker, context);
        classDeclaration.defs = new SetterAnnotation().manage(classDeclaration, names, treeMaker, context);
        classDeclaration.defs = new ToStringAnnotation().manage(classDeclaration, names, treeMaker, context);
    }

    @Override
    public void visitTopLevel(JCTree.JCCompilationUnit tree) {
        super.visitTopLevel(tree);
        for (JCTree.JCClassDecl utilityClass : SerializationUtilityClass.getUninjectedClasses())
            tree.defs = tree.defs.append(utilityClass);
    }
}
