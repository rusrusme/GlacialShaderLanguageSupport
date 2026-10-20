package net.rusrusme.gshader.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import net.rusrusme.gshader.core.GIcons;
import org.jetbrains.annotations.NotNull;

public class GCreateFileAction extends CreateFileFromTemplateAction {

    public GCreateFileAction() {
        super("Glacial Shader File", "Creates a new Glacial Shader file", GIcons.FILE);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("New Glacial Shader File")
                .addKind("Glacial Shader File", GIcons.FILE, "Glacial Shader File");
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return "Create Glacial Shader File " + newName;
    }
}