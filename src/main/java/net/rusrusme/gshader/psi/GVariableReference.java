package net.rusrusme.gshader.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GVariableReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String key;

    public GVariableReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText();
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        // 1. Variablen, Parameter & Buffer-Felder
        List<GPsiUtil.VisibleSymbol> symbols = GPsiUtil.findVisibleSymbols(getElement());
        for (GPsiUtil.VisibleSymbol sym : symbols) {
            if (key.equals(sym.name())) {
                return new ResolveResult[]{new PsiElementResolveResult(sym.element())};
            }
        }

        // 2. Funktionen
        List<GFunctionDefinition> functions = GPsiUtil.findFunctions(getElement());
        for (GFunctionDefinition func : functions) {
            if (func.getIdentifier() != null && key.equals(func.getIdentifier().getText())) {
                return new ResolveResult[]{new PsiElementResolveResult(func.getIdentifier())};
            }
        }

        // 3. Custom Structs
        List<GStructDeclaration> structs = GPsiUtil.findStructs(getElement());
        for (GStructDeclaration struct : structs) {
            if (struct.getIdentifier() != null && key.equals(struct.getIdentifier().getText())) {
                return new ResolveResult[]{new PsiElementResolveResult(struct.getIdentifier())};
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