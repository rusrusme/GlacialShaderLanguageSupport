package net.rusrusme.gshader.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class GPsiUtil {
    private GPsiUtil() { /* Utility Class */ }

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
        Collection<GVariableDeclaration> declarations = PsiTreeUtil.findChildrenOfType(file, GVariableDeclaration.class);
        for (GVariableDeclaration decl : declarations) {
            int declOffset = decl.getTextOffset();
            if (declOffset < caretOffset && !decl.getTextRange().containsOffset(caretOffset)) {
                String name = decl.getIdentifier().getText();
                String type = decl.getType().getText();
                symbols.add(new VisibleSymbol(name, type, decl.getIdentifier()));
            }
        }

        // 2. Funktionsparameter der umschließenden Funktion
        GFunctionDefinition enclosingFunction = PsiTreeUtil.getParentOfType(element, GFunctionDefinition.class);
        if (enclosingFunction != null) {
            for (GParameter param : enclosingFunction.getParameterList()) {
                if (param.getIdentifier() != null) {
                    String name = param.getIdentifier().getText();
                    String type = param.getType().getText();
                    symbols.add(new VisibleSymbol(name, type, param.getIdentifier()));
                }
            }
        }

        // 3. Members in Uniform Blocks & SSBOs
        Collection<GInterfaceBlock> interfaceBlocks = PsiTreeUtil.findChildrenOfType(file, GInterfaceBlock.class);
        for (GInterfaceBlock block : interfaceBlocks) {
            for (GStructMember member : block.getStructMemberList()) {
                String name = member.getIdentifier().getText();
                String type = member.getType().getText();
                symbols.add(new VisibleSymbol(name, type, member.getIdentifier()));
            }
        }

        return symbols;
    }

    // ============================================================================
    // Top-Level Element Queries
    // ============================================================================

    @NotNull
    public static List<GFunctionDefinition> findFunctions(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), GFunctionDefinition.class));
    }

    @NotNull
    public static List<GStructDeclaration> findStructs(@Nullable PsiElement element) {
        if (element == null || element.getContainingFile() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), GStructDeclaration.class));
    }
}