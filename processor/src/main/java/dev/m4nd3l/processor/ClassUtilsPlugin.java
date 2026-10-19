package dev.m4nd3l.processor;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

public class ClassUtilsPlugin implements Plugin {
    @Override
    public String getName() {
        return "ClassUtilsPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        Context context = ((JavacTaskImpl) task).getContext();
        TreeMaker treeMaker = TreeMaker.instance(context);
        Names names = Names.instance(context);

        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent event) {
                if (event.getKind() != TaskEvent.Kind.PARSE) return;
                ((JCTree.JCCompilationUnit) event.getCompilationUnit()).accept(new ClassUtilsASTTranslator(treeMaker, names, context));
            }
        });
    }
}
