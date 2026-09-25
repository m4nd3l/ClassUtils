package dev.m4nd3l.processor.data;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;

public class Data {
    private JCTree.JCClassDecl _class;
    private Names names;
    private TreeMaker treeMaker;

    public Data(JCTree.JCClassDecl _class, Names names, TreeMaker treeMaker) {
        this._class = _class;
        this.names = names;
        this.treeMaker = treeMaker;
    }

    public JCTree.JCClassDecl getClassDeclaration() { return _class; }
    public Names getNames() { return names; }
    public TreeMaker tm() { return treeMaker; }
}
