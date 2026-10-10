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

        // 1. Local and Global Variable Declarations
        Collection<BshVariableDeclaration> declarations =
                PsiTreeUtil.findChildrenOfType(file, BshVariableDeclaration.class);

        for (BshVariableDeclaration decl : declarations) {
            int declOffset = decl.getTextOffset();
            if (declOffset < caretOffset && !decl.getTextRange().containsOffset(caretOffset)) {
                String name = decl.getIdentifier().getText();
                String type = decl.getType().getText();
                result.add(new VisibleSymbol(name, type, decl.getIdentifier()));
            }
        }

        // 2. Function Parameters (enclosingFunction.getParameterList() directly returns List<BshParameter>)
        BshFunctionDefinition enclosingFunction = PsiTreeUtil.getParentOfType(element, BshFunctionDefinition.class);
        if (enclosingFunction != null) {
            for (BshParameter param : enclosingFunction.getParameterList()) {
                String name = param.getIdentifier().getText();
                String type = param.getType().getText();
                result.add(new VisibleSymbol(name, type, param.getIdentifier()));
            }
        }

        // 3. Uniform Block & Storage Buffer (SSBO) Members
        Collection<BshInterfaceBlock> interfaceBlocks =
                PsiTreeUtil.findChildrenOfType(file, BshInterfaceBlock.class);

        for (BshInterfaceBlock block : interfaceBlocks) {
            for (BshStructMember member : block.getStructMemberList()) {
                String name = member.getIdentifier().getText();
                String type = member.getType().getText();
                result.add(new VisibleSymbol(name, type, member.getIdentifier()));
            }
        }

        return result;
    }

    // Helper for backward compatibility
    public static List<BshVariableDeclaration> findVisibleVariables(PsiElement element) {
        List<BshVariableDeclaration> result = new ArrayList<>();
        if (element == null) return result;
        PsiFile file = element.getContainingFile();
        if (file == null) return result;

        int caretOffset = element.getTextOffset();
        Collection<BshVariableDeclaration> declarations =
                PsiTreeUtil.findChildrenOfType(file, BshVariableDeclaration.class);

        for (BshVariableDeclaration decl : declarations) {
            int declOffset = decl.getTextOffset();
            if (declOffset < caretOffset && !decl.getTextRange().containsOffset(caretOffset)) {
                result.add(decl);
            }
        }
        return result;
    }
}