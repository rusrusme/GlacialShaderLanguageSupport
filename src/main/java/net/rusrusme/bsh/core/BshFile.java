package net.rusrusme.bsh.core;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class BshFile extends PsiFileBase {
    protected BshFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, BshLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return BshFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Basis Shader File";
    }
}
