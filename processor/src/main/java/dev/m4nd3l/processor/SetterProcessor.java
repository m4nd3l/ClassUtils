package dev.m4nd3l.processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.core.Getter;
import dev.m4nd3l.core.Setter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static dev.m4nd3l.processor.utils.ManageAnnotations.manageSetterAnnotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.m4nd3l.annotation.Setter")
@SupportedSourceVersion(SourceVersion.RELEASE_24)
public class SetterProcessor extends AbstractProcessor {

    private Names names;
    private Trees trees;
    private TreeMaker treeMaker;
    private Context context;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
        this.context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.names = Names.instance(context);
        this.treeMaker = TreeMaker.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) return false;

        for (Element element : roundEnv.getElementsAnnotatedWith(Setter.class)) {
            JCTree astNode = (JCTree) trees.getTree(element);
            if (astNode != null) {
                astNode.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl classDecl) {
                        super.visitClassDef(classDecl);
                        if (classDecl.sym == null) return;
                        classDecl.defs = manageSetterAnnotation(classDecl, names, treeMaker);
                    }
                });
            }
        }

        return true;
    }
}