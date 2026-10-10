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
        List<BshPsiUtil.VisibleSymbol> symbols = BshPsiUtil.findVisibleSymbols(getElement());
        for (BshPsiUtil.VisibleSymbol sym : symbols) {
            if (key.equals(sym.name())) {
                return new ResolveResult[]{new PsiElementResolveResult(sym.element())};
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