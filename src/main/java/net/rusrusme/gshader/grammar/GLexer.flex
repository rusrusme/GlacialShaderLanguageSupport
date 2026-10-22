package net.rusrusme.gshader.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import net.rusrusme.gshader.psi.GTypes;

%%

%public
%class GLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE=[\ \t\r\n]+
LINE_COMMENT="//"[^\r\n]*
BLOCK_COMMENT="/"\*([^*]|\*+[^/*])*\*+"/"
IDENTIFIER=[a-zA-Z_][a-zA-Z0-9_]*
NUMBER=(0[xX][0-9a-fA-F]+[uU]?)|(([0-9]+\.[0-9]*|\.[0-9]+)([eE][+-]?[0-9]+)?[fF]?)|([0-9]+([eE][+-]?[0-9]+)?[fFuU]?)
STRING_LITERAL=\"[^\"]*\"

%%

<YYINITIAL> {
  {WHITE_SPACE}         { return TokenType.WHITE_SPACE; }
  {LINE_COMMENT}        { return GTypes.COMMENT; }
  {BLOCK_COMMENT}       { return GTypes.COMMENT; }
  {STRING_LITERAL}      { return GTypes.STRING_LITERAL; }

  /* Directives */
  "#vert"               { return GTypes.DIRECTIVE_VERT; }
  "#frag"               { return GTypes.DIRECTIVE_FRAG; }
  "#geom"               { return GTypes.DIRECTIVE_GEOM; }
  "#comp"               { return GTypes.DIRECTIVE_COMP; }
  "#include"            { return GTypes.DIRECTIVE_INCLUDE; }
  "#pragma"             { return GTypes.DIRECTIVE_PRAGMA; }
  "once"                { return GTypes.KEYWORD_ONCE; }
  "#version"            { return GTypes.DIRECTIVE_VERSION; }
  "#define"             { return GTypes.DIRECTIVE_DEFINE; }
  "#ifdef"              { return GTypes.DIRECTIVE_IFDEF; }
  "#ifndef"             { return GTypes.DIRECTIVE_IFNDEF; }
  "#if"                 { return GTypes.DIRECTIVE_IF; }
  "#elif"               { return GTypes.DIRECTIVE_ELIF; }
  "#else"               { return GTypes.DIRECTIVE_ELSE; }
  "#endif"              { return GTypes.DIRECTIVE_ENDIF; }
  "#common"             { return GTypes.DIRECTIVE_COMMON; }
  "defined"             { return GTypes.KEYWORD_DEFINED; }

  /* Keywords */
  "break"               { return GTypes.KEYWORD_BREAK; }
  "buffer"              { return GTypes.KEYWORD_BUFFER; }
  "case"                { return GTypes.KEYWORD_CASE; }
  "const"               { return GTypes.KEYWORD_CONST; }
  "continue"            { return GTypes.KEYWORD_CONTINUE; }
  "default"             { return GTypes.KEYWORD_DEFAULT; }
  "discard"             { return GTypes.KEYWORD_DISCARD; }
  "do"                  { return GTypes.KEYWORD_DO; }
  "else"                { return GTypes.KEYWORD_ELSE; }
  "for"                 { return GTypes.KEYWORD_FOR; }
  "if"                  { return GTypes.KEYWORD_IF; }
  "in"                  { return GTypes.KEYWORD_IN; }
  "layout"              { return GTypes.KEYWORD_LAYOUT; }
  "out"                 { return GTypes.KEYWORD_OUT; }
  "return"              { return GTypes.KEYWORD_RETURN; }
  "struct"              { return GTypes.KEYWORD_STRUCT; }
  "switch"              { return GTypes.KEYWORD_SWITCH; }
  "uniform"             { return GTypes.KEYWORD_UNIFORM; }
  "while"               { return GTypes.KEYWORD_WHILE; }

  /* Types */
  "bool"                { return GTypes.TYPE_BOOL; }
  "bvec2"               { return GTypes.TYPE_BVEC2; }
  "bvec3"               { return GTypes.TYPE_BVEC3; }
  "bvec4"               { return GTypes.TYPE_BVEC4; }
  "dmat2"               { return GTypes.TYPE_DMAT2; }
  "dmat3"               { return GTypes.TYPE_DMAT3; }
  "dmat4"               { return GTypes.TYPE_DMAT4; }
  "double"              { return GTypes.TYPE_DOUBLE; }
  "dvec2"               { return GTypes.TYPE_DVEC2; }
  "dvec3"               { return GTypes.TYPE_DVEC3; }
  "dvec4"               { return GTypes.TYPE_DVEC4; }
  "float"               { return GTypes.TYPE_FLOAT; }
  "image2D"             { return GTypes.TYPE_IMAGE2D; }
  "int"                 { return GTypes.TYPE_INT; }
  "isampler2D"          { return GTypes.TYPE_ISAMPLER2D; }
  "ivec2"               { return GTypes.TYPE_IVEC2; }
  "ivec3"               { return GTypes.TYPE_IVEC3; }
  "ivec4"               { return GTypes.TYPE_IVEC4; }
  "mat2"                { return GTypes.TYPE_MAT2; }
  "mat3"                { return GTypes.TYPE_MAT3; }
  "mat4"                { return GTypes.TYPE_MAT4; }
  "sampler1D"           { return GTypes.TYPE_SAMPLER1D; }
  "sampler2D"           { return GTypes.TYPE_SAMPLER2D; }
  "sampler2DArray"      { return GTypes.TYPE_SAMPLER2DARRAY; }
  "sampler2DShadow"     { return GTypes.TYPE_SAMPLER2DSHADOW; }
  "sampler3D"           { return GTypes.TYPE_SAMPLER3D; }
  "samplerCube"         { return GTypes.TYPE_SAMPLERCUBE; }
  "uint"                { return GTypes.TYPE_UINT; }
  "usampler2D"          { return GTypes.TYPE_USAMPLER2D; }
  "uvec2"               { return GTypes.TYPE_UVEC2; }
  "uvec3"               { return GTypes.TYPE_UVEC3; }
  "uvec4"               { return GTypes.TYPE_UVEC4; }
  "vec2"                { return GTypes.TYPE_VEC2; }
  "vec3"                { return GTypes.TYPE_VEC3; }
  "vec4"                { return GTypes.TYPE_VEC4; }
  "void"                { return GTypes.TYPE_VOID; }

  "true"                { return GTypes.BOOL_TRUE; }
  "false"               { return GTypes.BOOL_FALSE; }

  /* Compound Operators (must be defined before single-char tokens) */
  "=="                  { return GTypes.EQ; }
  "!="                  { return GTypes.NEQ; }
  "<="                  { return GTypes.LE; }
  ">="                  { return GTypes.GE; }
  "&&"                  { return GTypes.AND; }
  "||"                  { return GTypes.OR; }
  "<<"                  { return GTypes.SHL; }
  ">>"                  { return GTypes.SHR; }
  "++"                  { return GTypes.INC; }
  "--"                  { return GTypes.DEC; }
  "+="                  { return GTypes.ADD_ASSIGN; }
  "-="                  { return GTypes.SUB_ASSIGN; }
  "*="                  { return GTypes.MUL_ASSIGN; }
  "/="                  { return GTypes.DIV_ASSIGN; }
  "%="                  { return GTypes.MOD_ASSIGN; }
  "<<="                 { return GTypes.SHL_ASSIGN; }
  ">>="                 { return GTypes.SHR_ASSIGN; }
  "&="                  { return GTypes.AND_ASSIGN; }
  "|="                  { return GTypes.OR_ASSIGN; }
  "^="                  { return GTypes.XOR_ASSIGN; }

  /* Single Character Operators & Punctuation */
  "="                   { return GTypes.ASSIGN; }
  "<"                   { return GTypes.LT; }
  ">"                   { return GTypes.GT; }
  "!"                   { return GTypes.NOT; }
  "&"                   { return GTypes.BIT_AND; }
  "|"                   { return GTypes.BIT_OR; }
  "^"                   { return GTypes.BIT_XOR; }
  "~"                   { return GTypes.BIT_NOT; }
  "+"                   { return GTypes.PLUS; }
  "-"                   { return GTypes.MINUS; }
  "*"                   { return GTypes.MUL; }
  "/"                   { return GTypes.DIV; }
  "%"                   { return GTypes.MOD; }
  ";"                   { return GTypes.SEMICOLON; }
  ","                   { return GTypes.COMMA; }
  "."                   { return GTypes.DOT; }
  ":"                   { return GTypes.COLON; }
  "?"                   { return GTypes.QUESTION; }
  "{"                   { return GTypes.LBRACE; }
  "}"                   { return GTypes.RBRACE; }
  "("                   { return GTypes.LPAREN; }
  ")"                   { return GTypes.RPAREN; }
  "["                   { return GTypes.LBRACK; }
  "]"                   { return GTypes.RBRACK; }

  /* Storage / Memory Qualifiers */
  "readonly"            { return GTypes.KEYWORD_READONLY; }
  "writeonly"           { return GTypes.KEYWORD_WRITEONLY; }
  "coherent"            { return GTypes.KEYWORD_COHERENT; }
  "volatile"            { return GTypes.KEYWORD_VOLATILE; }
  "restrict"            { return GTypes.KEYWORD_RESTRICT; }

  {NUMBER}              { return GTypes.NUMBER; }
  {IDENTIFIER}          { return GTypes.IDENTIFIER; }
}

[^] { return TokenType.BAD_CHARACTER; }