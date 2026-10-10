package net.rusrusme.bsh.psi;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class BshReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(BshTypes.IDENTIFIER),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        // Avoid adding references to the declaration identifier itself
                        if (element.getParent() instanceof BshVariableDeclaration varDecl &&
                                varDecl.getIdentifier() == element) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        return new PsiReference[]{new BshVariableReference(element, element.getTextRangeInParent())};
                    }
                }
        );
    }
}