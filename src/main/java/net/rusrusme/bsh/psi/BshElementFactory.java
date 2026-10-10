package net.rusrusme.bsh.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import net.rusrusme.bsh.core.BshFile;
import net.rusrusme.bsh.core.BshFileType;

public class BshElementFactory {
    public static BshVariableDeclaration createVariableDeclaration(Project project, String name) {
        String code = "float " + name + ";";
        BshFile file = (BshFile) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.bsh", BshFileType.INSTANCE, code);
        return PsiTreeUtil.findChildOfType(file, BshVariableDeclaration.class);
    }

    public static BshStructDeclaration createStructDeclaration(Project project, String name) {
        String code = "struct " + name + " { int dummy; };";
        BshFile file = (BshFile) PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.bsh", BshFileType.INSTANCE, code);
        return PsiTreeUtil.findChildOfType(file, BshStructDeclaration.class);
    }
}