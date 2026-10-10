package net.rusrusme.bsh.formatting;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import net.rusrusme.bsh.core.BshLanguage;
import org.jetbrains.annotations.NotNull;

public class BshLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

    @Override
    public @NotNull Language getLanguage() {
        return BshLanguage.INSTANCE;
    }

    @Override
    public void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings,
                                  @NotNull CommonCodeStyleSettings.IndentOptions indentOptions) {
        indentOptions.INDENT_SIZE = 4;
        indentOptions.TAB_SIZE = 4;
        indentOptions.CONTINUATION_INDENT_SIZE = 4;
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return """
                #vert
                #pragma once

                void main() {
                
                }
                """;
    }
}