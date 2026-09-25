package dev.m4nd3l.processor.data;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.utils.AccessLevel;

public class GetterData extends Data {
    private JCTree.JCVariableDecl field;
    private String[] otherNames;
    private AccessLevel level;

    public GetterData(JCTree.JCVariableDecl field, String[] otherNames, AccessLevel level,
                      JCTree.JCClassDecl _class, Names names, TreeMaker treeMaker) {
        super(_class, names, treeMaker);
        this.field = field;
        this.otherNames = otherNames;
        this.level = level;
    }

    public JCTree.JCVariableDecl getField() { return field; }
    public String[] getOtherNames() { return otherNames; }
    public AccessLevel getLevel() { return level; }
}
