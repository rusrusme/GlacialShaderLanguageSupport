package net.rusrusme.bsh.action;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import net.rusrusme.bsh.core.BshIcons;
import org.jetbrains.annotations.NotNull;

public class BshCreateFileAction extends CreateFileFromTemplateAction {

    public BshCreateFileAction() {
        super("Basis Shader File", "Creates a new Basis Shader file", BshIcons.FILE);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("New Basis Shader File")
                .addKind("Basis Shader File", BshIcons.FILE, "Basis Shader File");
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return "Create Basis Shader File " + newName;
    }
}