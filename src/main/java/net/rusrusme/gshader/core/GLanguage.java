package net.rusrusme.gshader.core;

import com.intellij.lang.Language;

public class GLanguage extends Language {
    public static final GLanguage INSTANCE = new GLanguage();

    protected GLanguage() {
        super("GlacialShader");
    }
}
