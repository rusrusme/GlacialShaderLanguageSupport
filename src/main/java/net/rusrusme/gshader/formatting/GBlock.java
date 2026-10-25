package net.rusrusme.gshader.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import net.rusrusme.gshader.psi.GTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GBlock extends AbstractBlock {

    private final SpacingBuilder spacingBuilder;

    protected GBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, SpacingBuilder spacingBuilder) {
        super(node, wrap, alignment);
        this.spacingBuilder = spacingBuilder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();

        while (child != null) {
            // Skip whitespace and 0-length error nodes so FormatProcessor never aborts
            if (child.getElementType() != TokenType.WHITE_SPACE && child.getTextLength() > 0) {
                blocks.add(new GBlock(child, Wrap.createWrap(WrapType.NONE, false), null, spacingBuilder));
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        IElementType type = myNode.getElementType();

        if (type == GTypes.BLOCK_STATEMENT || type == GTypes.STRUCT_DECLARATION || type == GTypes.INTERFACE_BLOCK
                || type == GTypes.IF_STATEMENT || type == GTypes.WHILE_STATEMENT || type == GTypes.FOR_STATEMENT
                || type == GTypes.FUNCTION_DEFINITION) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }

        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public @Nullable Indent getIndent() {
        ASTNode parentNode = myNode.getTreeParent();
        if (parentNode == null) {
            return Indent.getNoneIndent();
        }

        IElementType elementType = myNode.getElementType();
        IElementType parentType = parentNode.getElementType();

        if (parentType == GTypes.BLOCK_STATEMENT) {
            if (elementType == GTypes.LBRACE || elementType == GTypes.RBRACE) {
                return Indent.getNoneIndent();
            }
            return Indent.getNormalIndent();
        }

        if (parentType == GTypes.STRUCT_DECLARATION || parentType == GTypes.INTERFACE_BLOCK) {
            if (isInsideBraces(parentNode, myNode)) {
                return Indent.getNormalIndent();
            }
            return Indent.getNoneIndent();
        }

        return Indent.getNoneIndent();
    }

    /**
     * Returns true if childNode appears strictly after LBRACE and before RBRACE within parentNode.
     */
    private boolean isInsideBraces(@NotNull ASTNode parentNode, @NotNull ASTNode childNode) {
        IElementType type = childNode.getElementType();
        if (type == GTypes.LBRACE || type == GTypes.RBRACE) {
            return false;
        }
        ASTNode lBrace = parentNode.findChildByType(GTypes.LBRACE);
        if (lBrace == null || childNode.getStartOffset() <= lBrace.getStartOffset()) {
            return false;
        }
        ASTNode rBrace = parentNode.findChildByType(GTypes.RBRACE);
        return rBrace == null || childNode.getStartOffset() < rBrace.getStartOffset();
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