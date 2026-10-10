package net.rusrusme.bsh.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class BshReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(BshTypes.IDENTIFIER), new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiElement parent = element.getParent();

                // Keine Selbst-Referenzen auf den eigenen Deklarations-Identifiern
                if (parent instanceof BshVariableDeclaration varDecl && varDecl.getIdentifier() == element) {
                    return PsiReference.EMPTY_ARRAY;
                }
                if (parent instanceof BshFunctionDefinition funcDef && funcDef.getIdentifier() == element) {
                    return PsiReference.EMPTY_ARRAY;
                }
                if (parent instanceof BshStructDeclaration structDecl && structDecl.getIdentifier() == element) {
                    return PsiReference.EMPTY_ARRAY;
                }
                if (parent instanceof BshParameter param && param.getIdentifier() == element) {
                    return PsiReference.EMPTY_ARRAY;
                }

                TextRange range = new TextRange(0, element.getTextLength());
                return new PsiReference[]{new BshVariableReference(element, range)};
            }
        });
    }
}