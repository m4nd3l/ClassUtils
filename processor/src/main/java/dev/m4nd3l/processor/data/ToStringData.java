package dev.m4nd3l.processor.data;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import dev.m4nd3l.utils.ToStringType;

import java.util.ArrayList;

public class ToStringData extends Data {
    private ArrayList<JCTree.JCVariableDecl> toSerialize;
    private ToStringType formatType;
    private boolean compactValue;
    private Context context;

    public ToStringData(ArrayList<JCTree.JCVariableDecl> toSerialize, ToStringType formatType, boolean compactValue, Context context,
                        JCTree.JCClassDecl _class, Names names, TreeMaker treeMaker) {
        super(_class, names, treeMaker);
        this.toSerialize = toSerialize;
        this.formatType = formatType;
        this.compactValue = compactValue;
        this.context = context;
    }

    public ArrayList<JCTree.JCVariableDecl> getToSerialize() { return toSerialize; }
    public ToStringType getFormatType() { return formatType; }
    public boolean isCompactValue() { return compactValue; }
    public Context getContext() { return context; }
}
