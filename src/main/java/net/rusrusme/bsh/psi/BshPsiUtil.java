package net.rusrusme.bsh.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BshPsiUtil {

    public static List<BshVariableDeclaration> findVisibleVariables(PsiElement element) {
        List<BshVariableDeclaration> result = new ArrayList<>();
        if (element == null) return result;

        PsiFile file = element.getContainingFile();
        if (file == null) return result;

        int caretOffset = element.getTextOffset();

        Collection<BshVariableDeclaration> declarations =
                PsiTreeUtil.findChildrenOfType(file, BshVariableDeclaration.class);

        for (BshVariableDeclaration decl : declarations) {
            if (decl.getIdentifier() != null) {
                int declOffset = decl.getTextOffset();
                // Variable muss vor dem Cursor stehen und darf den Cursorbereich nicht selbst umspannen
                if (declOffset < caretOffset && !decl.getTextRange().containsOffset(caretOffset)) {
                    result.add(decl);
                }
            }
        }

        return result;
    }
}