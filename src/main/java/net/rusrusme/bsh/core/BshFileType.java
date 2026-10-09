package net.rusrusme.bsh.core;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BshFileType extends LanguageFileType {
    public static final BshFileType INSTANCE = new BshFileType();

    protected BshFileType() {
        super(BshLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "Basis Shader File";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "Basis shader language file";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "bsh";
    }

    @Override
    public @Nullable Icon getIcon() {
        return BshIcons.FILE;
    }
}
