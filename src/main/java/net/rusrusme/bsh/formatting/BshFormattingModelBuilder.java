package net.rusrusme.bsh.formatting;

import com.intellij.formatting.*;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import net.rusrusme.bsh.core.BshLanguage;
import net.rusrusme.bsh.psi.BshTypes;
import org.jetbrains.annotations.NotNull;

public class BshFormattingModelBuilder implements FormattingModelBuilder {
    private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, BshLanguage.INSTANCE)
                .after(BshTypes.KEYWORD_IF).spaces(1)
                .after(BshTypes.KEYWORD_WHILE).spaces(1)
                .after(BshTypes.KEYWORD_FOR).spaces(1)
                .before(BshTypes.LBRACE).spaces(1);
    }

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        BshBlock block = new BshBlock(
                formattingContext.getNode(),
                Wrap.createWrap(WrapType.NONE, false),
                Alignment.createAlignment(),
                createSpaceBuilder(settings)
        );
        return FormattingModelProvider.createFormattingModelForPsiFile(
                formattingContext.getContainingFile(), block, settings);
    }
}
