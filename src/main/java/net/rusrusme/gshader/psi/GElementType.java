package net.rusrusme.gshader.psi;

import com.intellij.psi.tree.IElementType;
import net.rusrusme.gshader.core.GLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GElementType extends IElementType {
    public GElementType(@NonNls @NotNull String debugName) {
        super(debugName, GLanguage.INSTANCE);
    }
}
