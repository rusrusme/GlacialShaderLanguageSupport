package net.rusrusme.bsh.psi;

import com.intellij.psi.tree.IElementType;
import net.rusrusme.bsh.core.BshLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BshTokenType extends IElementType {
    public BshTokenType(@NonNls @NotNull String debugName) {
        super(debugName, BshLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
