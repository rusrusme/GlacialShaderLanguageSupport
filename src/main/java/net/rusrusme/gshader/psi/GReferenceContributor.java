package net.rusrusme.gshader.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class GReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(GTypes.IDENTIFIER),
                new PsiReferenceProvider() {
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        if (isDeclarationIdentifier(element)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        TextRange range = new TextRange(0, element.getTextLength());
                        return new PsiReference[]{ new GVariableReference(element, range) };
                    }
                }
        );
    }

    private static boolean isDeclarationIdentifier(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();

        return (parent instanceof GVariableDeclaration varDecl && varDecl.getIdentifier() == element)
                || (parent instanceof GFunctionDefinition funcDef && funcDef.getIdentifier() == element)
                || (parent instanceof GStructDeclaration structDecl && structDecl.getIdentifier() == element)
                || (parent instanceof GParameter param && param.getIdentifier() == element);
    }
}