package net.rusrusme.gshader.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import net.rusrusme.gshader.core.GFile;
import net.rusrusme.gshader.core.GFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GElementFactory {
    private GElementFactory() { /* Utility Class */ }

    @Nullable
    public static GVariableDeclaration createVariableDeclaration(@NotNull Project project, @NotNull String name) {
        String code = "float " + name + ";";
        GFile file = createFile(project, code);
        return PsiTreeUtil.findChildOfType(file, GVariableDeclaration.class);
    }

    @Nullable
    public static GStructDeclaration createStructDeclaration(@NotNull Project project, @NotNull String name) {
        String code = "struct " + name + " { int dummy; };";
        GFile file = createFile(project, code);
        return PsiTreeUtil.findChildOfType(file, GStructDeclaration.class);
    }

    @NotNull
    private static GFile createFile(@NotNull Project project, @NotNull String text) {
        return (GFile) PsiFileFactory.getInstance(project).createFileFromText("dummy.bsh", GFileType.INSTANCE, text);
    }
}