package net.rusrusme.bsh.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BshVariableReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String key;

    public BshVariableReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText();
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        List<BshVariableDeclaration> vars = BshPsiUtil.findVisibleVariables(getElement());
        for (BshVariableDeclaration var : vars) {
            if (var.getIdentifier() != null && key.equals(var.getIdentifier().getText())) {
                return new ResolveResult[]{new PsiElementResolveResult(var.getIdentifier())};
            }
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    @Override
    public @Nullable PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }
}