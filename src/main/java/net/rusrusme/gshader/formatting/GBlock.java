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

/**
 * Custom Formatting Block for Basis Shader Language (BSH).
 */
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
            if (child.getElementType() != TokenType.WHITE_SPACE) {
                // Pass null for Alignment to prevent column-relative drifting
                blocks.add(new GBlock(child, Wrap.createWrap(WrapType.NONE, false), null, spacingBuilder));
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        IElementType type = myNode.getElementType();

        // Defines indent for new lines created by pressing Enter inside these blocks
        if (type == GTypes.BLOCK_STATEMENT || type == GTypes.STRUCT_DECLARATION || type == GTypes.IF_STATEMENT || type == GTypes.WHILE_STATEMENT || type == GTypes.FOR_STATEMENT || type == GTypes.FUNCTION_DEFINITION) {

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

        // 1. Code-Blöcke innerhalb von Anweisungen ({ ... })
        if (parentType == GTypes.BLOCK_STATEMENT) {
            if (elementType == GTypes.LBRACE || elementType == GTypes.RBRACE) {
                return Indent.getNoneIndent();
            }
            return Indent.getNormalIndent();
        }

        // 2. Struct-Deklarationen & Interface-Blöcke (Uniform/SSBO)
        if (parentType == GTypes.STRUCT_DECLARATION || parentType == GTypes.INTERFACE_BLOCK) {
            if (elementType == GTypes.STRUCT_MEMBER || elementType == GTypes.VARIABLE_DECLARATION) {
                return Indent.getNormalIndent();
            }
            return Indent.getNoneIndent();
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