package net.rusrusme.bsh.core;

import com.intellij.lang.Language;

public class BshLanguage extends Language {
    public static final BshLanguage INSTANCE = new BshLanguage();

    protected BshLanguage() {
        super("BasisShader");
    }
}
