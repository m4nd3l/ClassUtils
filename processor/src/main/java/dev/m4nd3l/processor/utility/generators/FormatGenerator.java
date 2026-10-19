package dev.m4nd3l.processor.utility.generators;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

public interface FormatGenerator {
    JCTree.JCClassDecl generate(Context context);
}