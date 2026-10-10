package net.rusrusme.bsh.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import net.rusrusme.bsh.psi.BshElementFactory;
import net.rusrusme.bsh.psi.BshStructDeclaration;
import net.rusrusme.bsh.psi.BshTypes;
import net.rusrusme.bsh.psi.BshVariableDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BshNamedElementImpl extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {

    public BshNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return findChildByType(BshTypes.IDENTIFIER);
    }

    @Override
    public String getName() {
        PsiElement id = getNameIdentifier();
        return id != null ? id.getText() : null;
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        PsiElement id = getNameIdentifier();
        if (id != null) {
            PsiElement newId = null;
            if (this instanceof BshVariableDeclaration) {
                var dummy = BshElementFactory.createVariableDeclaration(getProject(), newName);
                if (dummy != null) newId = dummy.getIdentifier();
            }
            else if (this instanceof BshStructDeclaration) {
                var dummy = BshElementFactory.createStructDeclaration(getProject(), newName);
                if (dummy != null) newId = dummy.getIdentifier();
            }

            if (newId != null) {
                id.replace(newId);
            }
        }
        return this;
    }
}