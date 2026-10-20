package net.rusrusme.gshader.core;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GFileType extends LanguageFileType {
    public static final GFileType INSTANCE = new GFileType();

    protected GFileType() {
        super(GLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "Glacial Shader File";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "Glacial Shader Language File";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "gshader";
    }

    @Override
    public @Nullable Icon getIcon() {
        return GIcons.FILE;
    }
}
