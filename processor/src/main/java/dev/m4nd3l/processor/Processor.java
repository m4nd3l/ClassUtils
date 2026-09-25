package dev.m4nd3l.processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.core.Getter;
import dev.m4nd3l.core.Setter;
import dev.m4nd3l.core.ToString;
import dev.m4nd3l.processor.utility.SerializationUtilityClass;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

import static dev.m4nd3l.processor.utils.ManageAnnotations.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "dev.m4nd3l.core.Getter",
        "dev.m4nd3l.core.Setter",
        "dev.m4nd3l.core.ToString"
})
@SupportedSourceVersion(SourceVersion.RELEASE_24)
public class Processor extends AbstractProcessor {

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

        Set<TypeElement> classesToProcess = new HashSet<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(Getter.class)) {
            TypeElement classElem = getEnclosingClass(element);
            if (classElem != null) classesToProcess.add(classElem);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Setter.class)) {
            TypeElement classElem = getEnclosingClass(element);
            if (classElem != null) classesToProcess.add(classElem);
        }

        Filer filer = processingEnv.getFiler();
        for (Element element : roundEnv.getElementsAnnotatedWith(ToString.class)) {
            SerializationUtilityClass.generateSerializationUtilityClass(processingEnv, filer);
            TypeElement classElem = getEnclosingClass(element);
            if (classElem != null) classesToProcess.add(classElem);
        }

        for (TypeElement classElement : classesToProcess) {
            JCTree astNode = (JCTree) trees.getTree(classElement);
            if (astNode instanceof JCTree.JCClassDecl classDecl) {
                if (classDecl.sym != null) {
                    classDecl.defs = manageGetterAnnotation(classDecl, names, treeMaker);
                    classDecl.defs = manageSetterAnnotation(classDecl, names, treeMaker);
                    classDecl.defs = manageToStringAnnotation(classDecl, names, treeMaker, context);
                }
            }
        }

        return true;
    }

    private TypeElement getEnclosingClass(Element element) {
        while (element != null && element.getKind() != ElementKind.CLASS) element = element.getEnclosingElement();
        return (TypeElement) element;
    }
}