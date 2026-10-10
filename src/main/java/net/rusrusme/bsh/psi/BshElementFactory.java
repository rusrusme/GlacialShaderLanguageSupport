package net.rusrusme.bsh.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import net.rusrusme.bsh.core.BshFile;
import net.rusrusme.bsh.core.BshFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BshElementFactory {
    private BshElementFactory() { /* Utility Class */ }

    @Nullable
    public static BshVariableDeclaration createVariableDeclaration(@NotNull Project project, @NotNull String name) {
        String code = "float " + name + ";";
        BshFile file = createFile(project, code);
        return PsiTreeUtil.findChildOfType(file, BshVariableDeclaration.class);
    }

    @Nullable
    public static BshStructDeclaration createStructDeclaration(@NotNull Project project, @NotNull String name) {
        String code = "struct " + name + " { int dummy; };";
        BshFile file = createFile(project, code);
        return PsiTreeUtil.findChildOfType(file, BshStructDeclaration.class);
    }

    @NotNull
    private static BshFile createFile(@NotNull Project project, @NotNull String text) {
        return (BshFile) PsiFileFactory.getInstance(project).createFileFromText("dummy.bsh", BshFileType.INSTANCE, text);
    }
}