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

/**
 * Custom Formatting Block for Basis Shader Language (BSH).
 */
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
                // Pass null for Alignment to prevent column-relative drifting
                blocks.add(new BshBlock(child, Wrap.createWrap(WrapType.NONE, false), null, spacingBuilder));
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        IElementType type = myNode.getElementType();

        // Defines indent for new lines created by pressing Enter inside these blocks
        if (type == BshTypes.BLOCK_STATEMENT || type == BshTypes.STRUCT_DECLARATION || type == BshTypes.IF_STATEMENT || type == BshTypes.WHILE_STATEMENT || type == BshTypes.FOR_STATEMENT || type == BshTypes.FUNCTION_DEFINITION) {

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
        if (parentType == BshTypes.BLOCK_STATEMENT) {
            if (elementType == BshTypes.LBRACE || elementType == BshTypes.RBRACE) {
                return Indent.getNoneIndent();
            }
            return Indent.getNormalIndent();
        }

        // 2. Struct-Deklarationen & Interface-Blöcke (Uniform/SSBO)
        if (parentType == BshTypes.STRUCT_DECLARATION || parentType == BshTypes.INTERFACE_BLOCK) {
            if (elementType == BshTypes.STRUCT_MEMBER || elementType == BshTypes.VARIABLE_DECLARATION) {
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