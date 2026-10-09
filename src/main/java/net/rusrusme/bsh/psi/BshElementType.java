package net.rusrusme.bsh.psi;

import com.intellij.psi.tree.IElementType;
import net.rusrusme.bsh.core.BshLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BshElementType extends IElementType {
    public BshElementType(@NonNls @NotNull String debugName) {
        super(debugName, BshLanguage.INSTANCE);
    }
}
