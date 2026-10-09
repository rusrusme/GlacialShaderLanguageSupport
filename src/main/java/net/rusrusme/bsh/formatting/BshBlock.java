package net.rusrusme.bsh.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import net.rusrusme.bsh.psi.BshTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BshBlock extends AbstractBlock {
    private final SpacingBuilder spacingBuilder;

    protected BshBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment);
        this.spacingBuilder = spacingBuilder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();
        while (child != null) {
            if (child.getElementType() != TokenType.WHITE_SPACE) {
                blocks.add(new BshBlock(child, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), spacingBuilder));
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        IElementType type = myNode.getElementType();

        if (type == BshTypes.BLOCK_STATEMENT ||
                type == BshTypes.IF_STATEMENT ||
                type == BshTypes.WHILE_STATEMENT ||
                type == BshTypes.FOR_STATEMENT ||
                type == BshTypes.FUNCTION_DEFINITION) {

            return new ChildAttributes(Indent.getNormalIndent(), null);
        }

        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public @Nullable Indent getIndent() {
        IElementType elementType = myNode.getElementType();
        IElementType parentType = myNode.getTreeParent() == null ? null : myNode.getTreeParent().getElementType();

        if (parentType == null) {
            return Indent.getNoneIndent();
        }

        if (parentType == BshTypes.BLOCK_STATEMENT) {
            if (elementType == BshTypes.LBRACE || elementType == BshTypes.RBRACE) {
                return Indent.getNoneIndent();
            }
            return Indent.getNormalIndent();
        }

        return Indent.getNoneIndent();
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
