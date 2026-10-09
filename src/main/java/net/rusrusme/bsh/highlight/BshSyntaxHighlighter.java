package net.rusrusme.bsh.highlight;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import net.rusrusme.bsh.lexer.BshLexer;
import net.rusrusme.bsh.psi.BshTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class BshSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("BSH_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey PREPROCESSOR =
            createTextAttributesKey("BSH_PREPROCESSOR", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey TYPE =
            createTextAttributesKey("BSH_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("BSH_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey OPERATION_SIGN =
            createTextAttributesKey("BSH_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("BSH_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new FlexAdapter(new BshLexer(null));
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        String typeStr = tokenType.toString();
        if (typeStr.startsWith("DIRECTIVE_")) {
            return new TextAttributesKey[]{PREPROCESSOR};
        } else if (typeStr.startsWith("KEYWORD_")) {
            return new TextAttributesKey[]{KEYWORD};
        } else if (typeStr.startsWith("TYPE_")) {
            return new TextAttributesKey[]{TYPE};
        } else if (tokenType.equals(BshTypes.NUMBER)) {
            return new TextAttributesKey[]{NUMBER};
        } else if (tokenType.equals(BshTypes.COMMENT)) {
            return new TextAttributesKey[]{COMMENT};
        } else if (tokenType.equals(BshTypes.EQ) || tokenType.equals(BshTypes.NEQ) ||
                tokenType.equals(BshTypes.LE) || tokenType.equals(BshTypes.GE) ||
                tokenType.equals(BshTypes.LT) || tokenType.equals(BshTypes.GT) ||
                tokenType.equals(BshTypes.PLUS) || tokenType.equals(BshTypes.MINUS) ||
                tokenType.equals(BshTypes.MUL) || tokenType.equals(BshTypes.DIV) ||
                tokenType.equals(BshTypes.ASSIGN)) {
            return new TextAttributesKey[]{OPERATION_SIGN};
        }
        return new TextAttributesKey[0];
    }
}
