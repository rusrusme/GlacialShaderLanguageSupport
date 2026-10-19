package net.rusrusme.bsh.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import net.rusrusme.bsh.psi.BshTypes;

%%

%public
%class BshLexer
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
  {LINE_COMMENT}        { return BshTypes.COMMENT; }
  {BLOCK_COMMENT}       { return BshTypes.COMMENT; }
  {STRING_LITERAL}      { return BshTypes.STRING_LITERAL; }

  /* Directives */
  "#vert"               { return BshTypes.DIRECTIVE_VERT; }
  "#frag"               { return BshTypes.DIRECTIVE_FRAG; }
  "#geom"               { return BshTypes.DIRECTIVE_GEOM; }
  "#comp"               { return BshTypes.DIRECTIVE_COMP; }
  "#include"            { return BshTypes.DIRECTIVE_INCLUDE; }
  "#pragma"             { return BshTypes.DIRECTIVE_PRAGMA; }
  "once"                { return BshTypes.KEYWORD_ONCE; }
  "#version"            { return BshTypes.DIRECTIVE_VERSION; }
  "#define"             { return BshTypes.DIRECTIVE_DEFINE; }
  "#ifdef"              { return BshTypes.DIRECTIVE_IFDEF; }
  "#ifndef"             { return BshTypes.DIRECTIVE_IFNDEF; }
  "#if"                 { return BshTypes.DIRECTIVE_IF; }
  "#elif"               { return BshTypes.DIRECTIVE_ELIF; }
  "#else"               { return BshTypes.DIRECTIVE_ELSE; }
  "#endif"              { return BshTypes.DIRECTIVE_ENDIF; }
  "defined"             { return BshTypes.KEYWORD_DEFINED; }

  /* Keywords */
  "break"               { return BshTypes.KEYWORD_BREAK; }
  "buffer"              { return BshTypes.KEYWORD_BUFFER; }
  "case"                { return BshTypes.KEYWORD_CASE; }
  "const"               { return BshTypes.KEYWORD_CONST; }
  "continue"            { return BshTypes.KEYWORD_CONTINUE; }
  "default"             { return BshTypes.KEYWORD_DEFAULT; }
  "discard"             { return BshTypes.KEYWORD_DISCARD; }
  "do"                  { return BshTypes.KEYWORD_DO; }
  "else"                { return BshTypes.KEYWORD_ELSE; }
  "for"                 { return BshTypes.KEYWORD_FOR; }
  "if"                  { return BshTypes.KEYWORD_IF; }
  "in"                  { return BshTypes.KEYWORD_IN; }
  "layout"              { return BshTypes.KEYWORD_LAYOUT; }
  "out"                 { return BshTypes.KEYWORD_OUT; }
  "return"              { return BshTypes.KEYWORD_RETURN; }
  "struct"              { return BshTypes.KEYWORD_STRUCT; }
  "switch"              { return BshTypes.KEYWORD_SWITCH; }
  "uniform"             { return BshTypes.KEYWORD_UNIFORM; }
  "while"               { return BshTypes.KEYWORD_WHILE; }

  /* Types */
  "bool"                { return BshTypes.TYPE_BOOL; }
  "bvec2"               { return BshTypes.TYPE_BVEC2; }
  "bvec3"               { return BshTypes.TYPE_BVEC3; }
  "bvec4"               { return BshTypes.TYPE_BVEC4; }
  "dmat2"               { return BshTypes.TYPE_DMAT2; }
  "dmat3"               { return BshTypes.TYPE_DMAT3; }
  "dmat4"               { return BshTypes.TYPE_DMAT4; }
  "double"              { return BshTypes.TYPE_DOUBLE; }
  "dvec2"               { return BshTypes.TYPE_DVEC2; }
  "dvec3"               { return BshTypes.TYPE_DVEC3; }
  "dvec4"               { return BshTypes.TYPE_DVEC4; }
  "float"               { return BshTypes.TYPE_FLOAT; }
  "image2D"             { return BshTypes.TYPE_IMAGE2D; }
  "int"                 { return BshTypes.TYPE_INT; }
  "isampler2D"          { return BshTypes.TYPE_ISAMPLER2D; }
  "ivec2"               { return BshTypes.TYPE_IVEC2; }
  "ivec3"               { return BshTypes.TYPE_IVEC3; }
  "ivec4"               { return BshTypes.TYPE_IVEC4; }
  "mat2"                { return BshTypes.TYPE_MAT2; }
  "mat3"                { return BshTypes.TYPE_MAT3; }
  "mat4"                { return BshTypes.TYPE_MAT4; }
  "sampler1D"           { return BshTypes.TYPE_SAMPLER1D; }
  "sampler2D"           { return BshTypes.TYPE_SAMPLER2D; }
  "sampler2DArray"      { return BshTypes.TYPE_SAMPLER2DARRAY; }
  "sampler2DShadow"     { return BshTypes.TYPE_SAMPLER2DSHADOW; }
  "sampler3D"           { return BshTypes.TYPE_SAMPLER3D; }
  "samplerCube"         { return BshTypes.TYPE_SAMPLERCUBE; }
  "uint"                { return BshTypes.TYPE_UINT; }
  "usampler2D"          { return BshTypes.TYPE_USAMPLER2D; }
  "uvec2"               { return BshTypes.TYPE_UVEC2; }
  "uvec3"               { return BshTypes.TYPE_UVEC3; }
  "uvec4"               { return BshTypes.TYPE_UVEC4; }
  "vec2"                { return BshTypes.TYPE_VEC2; }
  "vec3"                { return BshTypes.TYPE_VEC3; }
  "vec4"                { return BshTypes.TYPE_VEC4; }
  "void"                { return BshTypes.TYPE_VOID; }

  "true"                { return BshTypes.BOOL_TRUE; }
  "false"               { return BshTypes.BOOL_FALSE; }

  /* Compound Operators (must be defined before single-char tokens) */
  "=="                  { return BshTypes.EQ; }
  "!="                  { return BshTypes.NEQ; }
  "<="                  { return BshTypes.LE; }
  ">="                  { return BshTypes.GE; }
  "&&"                  { return BshTypes.AND; }
  "||"                  { return BshTypes.OR; }
  "<<"                  { return BshTypes.SHL; }
  ">>"                  { return BshTypes.SHR; }
  "++"                  { return BshTypes.INC; }
  "--"                  { return BshTypes.DEC; }
  "+="                  { return BshTypes.ADD_ASSIGN; }
  "-="                  { return BshTypes.SUB_ASSIGN; }
  "*="                  { return BshTypes.MUL_ASSIGN; }
  "/="                  { return BshTypes.DIV_ASSIGN; }
  "%="                  { return BshTypes.MOD_ASSIGN; }
  "<<="                 { return BshTypes.SHL_ASSIGN; }
  ">>="                 { return BshTypes.SHR_ASSIGN; }
  "&="                  { return BshTypes.AND_ASSIGN; }
  "|="                  { return BshTypes.OR_ASSIGN; }
  "^="                  { return BshTypes.XOR_ASSIGN; }

  /* Single Character Operators & Punctuation */
  "="                   { return BshTypes.ASSIGN; }
  "<"                   { return BshTypes.LT; }
  ">"                   { return BshTypes.GT; }
  "!"                   { return BshTypes.NOT; }
  "&"                   { return BshTypes.BIT_AND; }
  "|"                   { return BshTypes.BIT_OR; }
  "^"                   { return BshTypes.BIT_XOR; }
  "~"                   { return BshTypes.BIT_NOT; }
  "+"                   { return BshTypes.PLUS; }
  "-"                   { return BshTypes.MINUS; }
  "*"                   { return BshTypes.MUL; }
  "/"                   { return BshTypes.DIV; }
  "%"                   { return BshTypes.MOD; }
  ";"                   { return BshTypes.SEMICOLON; }
  ","                   { return BshTypes.COMMA; }
  "."                   { return BshTypes.DOT; }
  ":"                   { return BshTypes.COLON; }
  "?"                   { return BshTypes.QUESTION; }
  "{"                   { return BshTypes.LBRACE; }
  "}"                   { return BshTypes.RBRACE; }
  "("                   { return BshTypes.LPAREN; }
  ")"                   { return BshTypes.RPAREN; }
  "["                   { return BshTypes.LBRACK; }
  "]"                   { return BshTypes.RBRACK; }

  /* Storage / Memory Qualifiers */
  "readonly"            { return BshTypes.KEYWORD_READONLY; }
  "writeonly"           { return BshTypes.KEYWORD_WRITEONLY; }
  "coherent"            { return BshTypes.KEYWORD_COHERENT; }
  "volatile"            { return BshTypes.KEYWORD_VOLATILE; }
  "restrict"            { return BshTypes.KEYWORD_RESTRICT; }

  {NUMBER}              { return BshTypes.NUMBER; }
  {IDENTIFIER}          { return BshTypes.IDENTIFIER; }
}

[^] { return TokenType.BAD_CHARACTER; }