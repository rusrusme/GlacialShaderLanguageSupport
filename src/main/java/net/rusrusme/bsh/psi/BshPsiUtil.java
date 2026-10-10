package net.rusrusme.bsh.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class BshPsiUtil {
    private BshPsiUtil() { /* Utility Class */ }

    // ============================================================================
    // Value Records
    // ============================================================================

    public record VisibleSymbol(@NotNull String name, @NotNull String type, @NotNull PsiElement element) {}

    // ============================================================================
    // Scope & Symbol Resolution
    // ============================================================================

    @NotNull
    public static List<VisibleSymbol> findVisibleSymbols(@Nullable PsiElement element) {
        if (element == null) return Collections.emptyList();

        PsiFile file = element.getContainingFile();
        if (file == null) return Collections.emptyList();

        List<VisibleSymbol> symbols = new ArrayList<>();
        int caretOffset = element.getTextOffset();

        // 1. Lokale und globale Variablen vor der aktuellen Position
        Collection<BshVariableDeclaration> declarations = PsiTreeUtil.findChildrenOfType(file, BshVariableDeclaration.class);
        for (BshVariableDeclaration decl : declarations) {
            if (decl.getIdentifier() != null) {
                int declOffset = decl.getTextOffset();
                if (declOffset < caretOffset && !decl.getTextRange().containsOffset(caretOffset)) {
                    String name = decl.getIdentifier().getText();
                    String type = decl.getType() != null ? decl.getType().getText() : "var";
                    symbols.add(new VisibleSymbol(name, type, decl.getIdentifier()));
                }
            }
        }

        // 2. Funktionsparameter der umschließenden Funktion
        BshFunctionDefinition enclosingFunction = PsiTreeUtil.getParentOfType(element, BshFunctionDefinition.class);
        if (enclosingFunction != null && enclosingFunction.getParameterList() != null) {
            for (BshParameter param : enclosingFunction.getParameterList()) {
                if (param.getIdentifier() != null) {
                    String name = param.getIdentifier().getText();
                    String type = param.getType() != null ? param.getType().getText() : "var";
                    symbols.add(new VisibleSymbol(name, type, param.getIdentifier()));
                }
            }
        }

        // 3. Members in Uniform Blocks & SSBOs
        Collection<BshInterfaceBlock> interfaceBlocks = PsiTreeUtil.findChildrenOfType(file, BshInterfaceBlock.class);
        for (BshInterfaceBlock block : interfaceBlocks) {
            for (BshStructMember member : block.getStructMemberList()) {
                if (member.getIdentifier() != null) {
                    String name = member.getIdentifier().getText();
                    String type = member.getType() != null ? member.getType().getText() : "var";
                    symbols.add(new VisibleSymbol(name, type, member.getIdentifier()));
                }
            }
        }

        return symbols;
    }

    // ============================================================================
    // Top-Level Element Queries
    // ============================================================================

    @NotNull
    public static List<BshFunctionDefinition> findFunctions(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), BshFunctionDefinition.class));
    }

    @NotNull
    public static List<BshStructDeclaration> findStructs(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), BshStructDeclaration.class));
    }
}