package dev.m4nd3l.processor.data;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.utils.AccessLevel;

public class SetterData extends Data {
    private JCTree.JCVariableDecl field;
    private String[] otherNames;
    private boolean returnItself;
    private AccessLevel level;

    public SetterData(JCTree.JCVariableDecl field, String[] otherNames, boolean returnItself, AccessLevel level,
                      JCTree.JCClassDecl _class, Names names, TreeMaker treeMaker) {
        super(_class, names, treeMaker);
        this.field = field;
        this.otherNames = otherNames;
        this.returnItself = returnItself;
        this.level = level;
    }

    public JCTree.JCVariableDecl getField() { return field; }
    public String[] getOtherNames() { return otherNames; }
    public boolean returnItself() { return returnItself; }
    public AccessLevel getLevel() { return level; }
}
