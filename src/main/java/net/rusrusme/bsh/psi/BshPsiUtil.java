package net.rusrusme.bsh.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BshPsiUtil {

    /**
     * Unified record representing any visible symbol (variables, parameters, buffer fields) in scope.
     */
    public record VisibleSymbol(String name, String type, PsiElement element) {}

    public static List<VisibleSymbol> findVisibleSymbols(PsiElement element) {
        List<VisibleSymbol> result = new ArrayList<>();
        if (element == null) return result;

        PsiFile file = element.getContainingFile();
        if (file == null) return result;

        int caretOffset = element.getTextOffset();

        // 1. Lokale und globale Variablen (Null-Checks hinzugefügt)
        Collection<BshVariableDeclaration> declarations = PsiTreeUtil.findChildrenOfType(file, BshVariableDeclaration.class);

        for (BshVariableDeclaration decl : declarations) {
            if (decl.getIdentifier() != null) {
                int declOffset = decl.getTextOffset();
                if (declOffset < caretOffset && !decl.getTextRange().containsOffset(caretOffset)) {
                    String name = decl.getIdentifier().getText();
                    String type = decl.getType() != null ? decl.getType().getText() : "var";
                    result.add(new VisibleSymbol(name, type, decl.getIdentifier()));
                }
            }
        }

        // 2. Funktions-Parameter (Null-Checks hinzugefügt)
        BshFunctionDefinition enclosingFunction = PsiTreeUtil.getParentOfType(element, BshFunctionDefinition.class);
        if (enclosingFunction != null && enclosingFunction.getParameterList() != null) {
            for (BshParameter param : enclosingFunction.getParameterList()) {
                if (param.getIdentifier() != null) {
                    String name = param.getIdentifier().getText();
                    String type = param.getType() != null ? param.getType().getText() : "var";
                    result.add(new VisibleSymbol(name, type, param.getIdentifier()));
                }
            }
        }

        // 3. Uniform Block & SSBO Member (Null-Checks hinzugefügt)
        Collection<BshInterfaceBlock> interfaceBlocks = PsiTreeUtil.findChildrenOfType(file, BshInterfaceBlock.class);

        for (BshInterfaceBlock block : interfaceBlocks) {
            for (BshStructMember member : block.getStructMemberList()) {
                if (member.getIdentifier() != null) {
                    String name = member.getIdentifier().getText();
                    String type = member.getType() != null ? member.getType().getText() : "var";
                    result.add(new VisibleSymbol(name, type, member.getIdentifier()));
                }
            }
        }

        return result;
    }

    public static List<BshVariableDeclaration> findVisibleVariables(PsiElement element) {
        List<BshVariableDeclaration> result = new ArrayList<>();
        if (element == null) return result;
        PsiFile file = element.getContainingFile();
        if (file == null) return result;

        int caretOffset = element.getTextOffset();
        Collection<BshVariableDeclaration> declarations = PsiTreeUtil.findChildrenOfType(file, BshVariableDeclaration.class);

        for (BshVariableDeclaration decl : declarations) {
            if (decl.getIdentifier() != null) {
                int declOffset = decl.getTextOffset();
                if (declOffset < caretOffset && !decl.getTextRange().containsOffset(caretOffset)) {
                    result.add(decl);
                }
            }
        }
        return result;
    }

    public static List<BshFunctionDefinition> findFunctions(PsiElement element) {
        if (element == null || element.getContainingFile() == null) return List.of();
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), BshFunctionDefinition.class));
    }

    public static List<BshStructDeclaration> findStructs(PsiElement element) {
        if (element == null || element.getContainingFile() == null) return List.of();
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(element.getContainingFile(), BshStructDeclaration.class));
    }
}