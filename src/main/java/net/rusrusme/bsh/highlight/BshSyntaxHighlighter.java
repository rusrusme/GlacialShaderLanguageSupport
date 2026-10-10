package net.rusrusme.bsh.highlight;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
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
            createTextAttributesKey("BSH_TYPE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("BSH_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey OPERATION_SIGN =
            createTextAttributesKey("BSH_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("BSH_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    private static final TokenSet DIRECTIVES = TokenSet.create(
            BshTypes.DIRECTIVE_COMP, BshTypes.DIRECTIVE_DEFINE, BshTypes.DIRECTIVE_ENDIF,
            BshTypes.DIRECTIVE_FRAG, BshTypes.DIRECTIVE_GEOM, BshTypes.DIRECTIVE_IFDEF,
            BshTypes.DIRECTIVE_IFNDEF, BshTypes.DIRECTIVE_INCLUDE, BshTypes.DIRECTIVE_PRAGMA,
            BshTypes.KEYWORD_ONCE, BshTypes.DIRECTIVE_VERSION, BshTypes.DIRECTIVE_VERT
    );

    private static final TokenSet KEYWORDS = TokenSet.create(
            BshTypes.KEYWORD_BREAK, BshTypes.KEYWORD_CASE, BshTypes.KEYWORD_CONST,
            BshTypes.KEYWORD_CONTINUE, BshTypes.KEYWORD_DEFAULT, BshTypes.KEYWORD_DISCARD,
            BshTypes.KEYWORD_DO, BshTypes.KEYWORD_ELSE, BshTypes.KEYWORD_FOR,
            BshTypes.KEYWORD_IF, BshTypes.KEYWORD_IN, BshTypes.KEYWORD_LAYOUT,
            BshTypes.KEYWORD_OUT, BshTypes.KEYWORD_RETURN, BshTypes.KEYWORD_STRUCT,
            BshTypes.KEYWORD_SWITCH, BshTypes.KEYWORD_UNIFORM, BshTypes.KEYWORD_WHILE,
            BshTypes.BOOL_TRUE, BshTypes.BOOL_FALSE
    );

    private static final TokenSet TYPES = TokenSet.create(
            BshTypes.TYPE_BOOL, BshTypes.TYPE_BVEC2, BshTypes.TYPE_BVEC3, BshTypes.TYPE_BVEC4,
            BshTypes.TYPE_DMAT2, BshTypes.TYPE_DMAT3, BshTypes.TYPE_DMAT4, BshTypes.TYPE_DOUBLE,
            BshTypes.TYPE_DVEC2, BshTypes.TYPE_DVEC3, BshTypes.TYPE_DVEC4, BshTypes.TYPE_FLOAT,
            BshTypes.TYPE_IMAGE2D, BshTypes.TYPE_INT, BshTypes.TYPE_ISAMPLER2D,
            BshTypes.TYPE_IVEC2, BshTypes.TYPE_IVEC3, BshTypes.TYPE_IVEC4,
            BshTypes.TYPE_MAT2, BshTypes.TYPE_MAT3, BshTypes.TYPE_MAT4,
            BshTypes.TYPE_SAMPLER1D, BshTypes.TYPE_SAMPLER2D, BshTypes.TYPE_SAMPLER2DARRAY,
            BshTypes.TYPE_SAMPLER2DSHADOW, BshTypes.TYPE_SAMPLER3D, BshTypes.TYPE_SAMPLERCUBE,
            BshTypes.TYPE_UINT, BshTypes.TYPE_USAMPLER2D,
            BshTypes.TYPE_UVEC2, BshTypes.TYPE_UVEC3, BshTypes.TYPE_UVEC4,
            BshTypes.TYPE_VEC2, BshTypes.TYPE_VEC3, BshTypes.TYPE_VEC4, BshTypes.TYPE_VOID
    );

    private static final TokenSet OPERATORS = TokenSet.create(
            BshTypes.EQ, BshTypes.NEQ, BshTypes.LE, BshTypes.GE, BshTypes.LT, BshTypes.GT,
            BshTypes.PLUS, BshTypes.MINUS, BshTypes.MUL, BshTypes.DIV, BshTypes.ASSIGN,
            BshTypes.ADD_ASSIGN, BshTypes.SUB_ASSIGN, BshTypes.MUL_ASSIGN, BshTypes.DIV_ASSIGN
    );

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new FlexAdapter(new BshLexer(null));
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (DIRECTIVES.contains(tokenType)) {
            return new TextAttributesKey[]{PREPROCESSOR};
        }
        else if (KEYWORDS.contains(tokenType)) {
            return new TextAttributesKey[]{KEYWORD};
        }
        else if (TYPES.contains(tokenType)) {
            return new TextAttributesKey[]{TYPE};
        }
        else if (OPERATORS.contains(tokenType)) {
            return new TextAttributesKey[]{OPERATION_SIGN};
        }
        else if (tokenType.equals(BshTypes.NUMBER)) {
            return new TextAttributesKey[]{NUMBER};
        }
        else if (tokenType.equals(BshTypes.COMMENT)) {
            return new TextAttributesKey[]{COMMENT};
        }
        return new TextAttributesKey[0];
    }
}