package net.rusrusme.gshader.highlight;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import net.rusrusme.gshader.lexer.GLexer;
import net.rusrusme.gshader.psi.GTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class GSyntaxHighlighter extends SyntaxHighlighterBase {

    // Text Attributes Keys

    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("GSHADER_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey PREPROCESSOR =
            createTextAttributesKey("GSHADER_PREPROCESSOR", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey TYPE =
            createTextAttributesKey("GSHADER_TYPE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("GSHADER_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("GSHADER_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey OPERATION_SIGN =
            createTextAttributesKey("GSHADER_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("GSHADER_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    // Token Sets

    private static final TokenSet DIRECTIVES = TokenSet.create(
            GTypes.DIRECTIVE_COMP, GTypes.DIRECTIVE_DEFINE, GTypes.DIRECTIVE_ENDIF,
            GTypes.DIRECTIVE_FRAG, GTypes.DIRECTIVE_GEOM, GTypes.DIRECTIVE_IFDEF,
            GTypes.DIRECTIVE_IFNDEF, GTypes.DIRECTIVE_IF, GTypes.DIRECTIVE_ELIF,
            GTypes.DIRECTIVE_ELSE, GTypes.DIRECTIVE_INCLUDE, GTypes.DIRECTIVE_PRAGMA,
            GTypes.KEYWORD_ONCE, GTypes.KEYWORD_DEFINED, GTypes.DIRECTIVE_VERSION,
            GTypes.DIRECTIVE_VERT, GTypes.DIRECTIVE_COMMON
    );

    private static final TokenSet KEYWORDS = TokenSet.create(
            GTypes.KEYWORD_BREAK, GTypes.KEYWORD_BUFFER, GTypes.KEYWORD_CASE,
            GTypes.KEYWORD_CONST, GTypes.KEYWORD_CONTINUE, GTypes.KEYWORD_DEFAULT,
            GTypes.KEYWORD_DISCARD, GTypes.KEYWORD_DO, GTypes.KEYWORD_ELSE,
            GTypes.KEYWORD_FOR, GTypes.KEYWORD_IF, GTypes.KEYWORD_IN,
            GTypes.KEYWORD_LAYOUT, GTypes.KEYWORD_OUT, GTypes.KEYWORD_RETURN,
            GTypes.KEYWORD_STRUCT, GTypes.KEYWORD_SWITCH, GTypes.KEYWORD_UNIFORM,
            GTypes.KEYWORD_WHILE, GTypes.BOOL_TRUE, GTypes.BOOL_FALSE,
            GTypes.KEYWORD_READONLY, GTypes.KEYWORD_WRITEONLY, GTypes.KEYWORD_COHERENT,
            GTypes.KEYWORD_VOLATILE, GTypes.KEYWORD_RESTRICT
    );

    private static final TokenSet TYPES = TokenSet.create(
            GTypes.TYPE_BOOL, GTypes.TYPE_BVEC2, GTypes.TYPE_BVEC3, GTypes.TYPE_BVEC4,
            GTypes.TYPE_DMAT2, GTypes.TYPE_DMAT3, GTypes.TYPE_DMAT4, GTypes.TYPE_DOUBLE,
            GTypes.TYPE_DVEC2, GTypes.TYPE_DVEC3, GTypes.TYPE_DVEC4, GTypes.TYPE_FLOAT,
            GTypes.TYPE_IMAGE2D, GTypes.TYPE_INT, GTypes.TYPE_ISAMPLER2D,
            GTypes.TYPE_IVEC2, GTypes.TYPE_IVEC3, GTypes.TYPE_IVEC4,
            GTypes.TYPE_MAT2, GTypes.TYPE_MAT3, GTypes.TYPE_MAT4,
            GTypes.TYPE_SAMPLER1D, GTypes.TYPE_SAMPLER2D, GTypes.TYPE_SAMPLER2DARRAY,
            GTypes.TYPE_SAMPLER2DSHADOW, GTypes.TYPE_SAMPLER3D, GTypes.TYPE_SAMPLERCUBE,
            GTypes.TYPE_UINT, GTypes.TYPE_USAMPLER2D,
            GTypes.TYPE_UVEC2, GTypes.TYPE_UVEC3, GTypes.TYPE_UVEC4,
            GTypes.TYPE_VEC2, GTypes.TYPE_VEC3, GTypes.TYPE_VEC4, GTypes.TYPE_VOID
    );

    private static final TokenSet OPERATORS = TokenSet.create(
            GTypes.EQ, GTypes.NEQ, GTypes.LE, GTypes.GE, GTypes.LT, GTypes.GT,
            GTypes.PLUS, GTypes.MINUS, GTypes.MUL, GTypes.DIV, GTypes.ASSIGN,
            GTypes.ADD_ASSIGN, GTypes.SUB_ASSIGN, GTypes.MUL_ASSIGN, GTypes.DIV_ASSIGN,
            GTypes.MOD_ASSIGN, GTypes.SHL_ASSIGN, GTypes.SHR_ASSIGN,
            GTypes.AND_ASSIGN, GTypes.OR_ASSIGN, GTypes.XOR_ASSIGN,
            GTypes.INC, GTypes.DEC, GTypes.NOT, GTypes.BIT_NOT,
            GTypes.BIT_AND, GTypes.BIT_OR, GTypes.BIT_XOR, GTypes.SHL, GTypes.SHR
    );

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new FlexAdapter(new GLexer(null));
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (DIRECTIVES.contains(tokenType)) {
            return pack(PREPROCESSOR);
        }
        if (KEYWORDS.contains(tokenType)) {
            return pack(KEYWORD);
        }
        if (TYPES.contains(tokenType)) {
            return pack(TYPE);
        }
        if (OPERATORS.contains(tokenType)) {
            return pack(OPERATION_SIGN);
        }
        if (tokenType.equals(GTypes.NUMBER)) {
            return pack(NUMBER);
        }
        if (tokenType.equals(GTypes.STRING_LITERAL)) {
            return pack(STRING);
        }
        if (tokenType.equals(GTypes.COMMENT)) {
            return pack(COMMENT);
        }
        return TextAttributesKey.EMPTY_ARRAY;
    }
}