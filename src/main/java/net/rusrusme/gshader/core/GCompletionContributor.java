package net.rusrusme.gshader.core;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.ConstantNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import net.rusrusme.gshader.psi.GPsiUtil;
import net.rusrusme.gshader.psi.GStructDeclaration;
import net.rusrusme.gshader.psi.GStructMember;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GCompletionContributor extends CompletionContributor {
    public GCompletionContributor() {
        // Standard Live-Template Handler für if (), while () etc.
        InsertHandler<LookupElement> parensInsertHandler = (context, item) -> {
            Editor editor = context.getEditor();
            Project project = context.getProject();

            TemplateManager templateManager = TemplateManager.getInstance(project);
            Template template = templateManager.createTemplate("", "", " ($EXPR$)$END$");
            template.addVariable("EXPR", new ConstantNode(""), true);

            templateManager.startTemplate(editor, template);
        };

        // Spezieller Handler für layout(...)
        InsertHandler<LookupElement> layoutInsertHandler = (context, item) -> {
            Editor editor = context.getEditor();
            Project project = context.getProject();

            TemplateManager templateManager = TemplateManager.getInstance(project);
            Template template = templateManager.createTemplate("", "", "($PARAM$)$END$");
            template.addVariable("PARAM", new ConstantNode(""), true);

            templateManager.startTemplate(editor, template);
            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
        };

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {

                int offset = parameters.getOffset();
                Document document = parameters.getEditor().getDocument();
                CharSequence text = document.getCharsSequence();
                PsiElement position = parameters.getPosition();

                // 1. Typsichere Swizzle/Member-Completion nach Punkt (.)
                if (offset > 0 && text.charAt(offset - 1) == '.') {
                    String varName = getIdentifierBeforeDot(text, offset - 1);
                    String varType = resolveVariableType(position, varName);

                    if (isBuiltinVectorType(varType)) {
                        addTypeAwareMembers(result, varType);
                    }
                    else if (!varType.isEmpty()) {
                        addCustomStructMembers(result, position, varType);
                    }
                    return;
                }

                // 2. Layout-Parameter Schlüsselwörter VOR dem '='
                if (isInsideLayoutKeyContext(text, offset)) {
                    addLayoutParamCompletions(result);
                    return;
                }

                if (isInsideLayoutValueContext(text, offset)) {
                    return;
                }

                int start = offset;
                while (start > 0) {
                    char c = text.charAt(start - 1);
                    if (Character.isJavaIdentifierPart(c) || c == '#') {
                        start--;
                        if (c == '#') break;
                    }
                    else {
                        break;
                    }
                }

                String prefix = text.subSequence(start, offset).toString();

                String[] directives = {
                        "#frag", "#vert", "#geom", "#comp",
                        "#pragma once", "#include", "#version",
                        "#define", "#ifdef", "#ifndef", "#endif",
                        "#common"
                };

                if (prefix.startsWith("#")) {
                    CompletionResultSet directiveResult = result.withPrefixMatcher(prefix);
                    for (String directive : directives) {
                        LookupElementBuilder element = LookupElementBuilder.create(directive)
                                .bold()
                                .withPresentableText(directive);
                        directiveResult.addElement(PrioritizedLookupElement.withPriority(element, 200.0));
                    }
                    return;
                }

                for (String directive : directives) {
                    LookupElementBuilder element = LookupElementBuilder.create(directive)
                            .bold()
                            .withLookupString(directive.substring(1))
                            .withPresentableText(directive);
                    result.addElement(PrioritizedLookupElement.withPriority(element, 50.0));
                }

                LookupElementBuilder layoutElement = LookupElementBuilder.create("layout")
                        .bold()
                        .withTailText(" ()", true)
                        .withInsertHandler(layoutInsertHandler);
                result.addElement(PrioritizedLookupElement.withPriority(layoutElement, 120.0));

                String[] statementsWithParens = {"if", "while", "for", "switch"};
                for (String keyword : statementsWithParens) {
                    LookupElementBuilder element = LookupElementBuilder.create(keyword)
                            .bold()
                            .withTailText(" ()", true)
                            .withInsertHandler(parensInsertHandler);
                    result.addElement(PrioritizedLookupElement.withPriority(element, 100.0));
                }

                String[] simpleKeywords = {
                        "else", "do", "return", "break", "continue", "discard", "uniform", "in", "out", "buffer",
                        "void", "float", "int", "uint", "bool", "double", "once",
                        "vec2", "vec3", "vec4", "ivec2", "ivec3", "ivec4", "uvec2", "uvec3", "uvec4", "bvec2", "bvec3", "bvec4",
                        "mat2", "mat3", "mat4", "sampler2D", "samplerCube", "image2D",
                        // GLSL Built-in Functions
                        "radians", "degrees", "sin", "cos", "tan", "asin", "acos", "atan",
                        "pow", "exp", "log", "exp2", "log2", "sqrt", "inversesqrt",
                        "abs", "sign", "floor", "trunc", "round", "ceil", "fract", "mod", "min", "max", "clamp", "mix", "step", "smoothstep",
                        "length", "distance", "dot", "cross", "normalize", "faceforward", "reflect", "refract",
                        "matrixCompMult", "transpose", "determinant", "inverse",
                        "texture", "textureLod", "texelFetch", "textureSize"
                };
                for (String keyword : simpleKeywords) {
                    result.addElement(LookupElementBuilder.create(keyword).bold());
                }

                // 3. Dynamisch deklarierte Variablen, Parameter & Buffer-Member
                List<GPsiUtil.VisibleSymbol> visibleSymbols = GPsiUtil.findVisibleSymbols(position);
                for (GPsiUtil.VisibleSymbol sym : visibleSymbols) {
                    LookupElementBuilder element = LookupElementBuilder.create(sym.name())
                            .withTypeText(sym.type(), true)
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Variable)
                            .bold();

                    result.addElement(PrioritizedLookupElement.withPriority(element, 150.0));
                }

                // 4. Custom Struct-Namen als Typ-Vorschläge anbieten (z.B. PointLight)
                if (position.getContainingFile() != null) {
                    Collection<GStructDeclaration> structs =
                            PsiTreeUtil.findChildrenOfType(position.getContainingFile(), GStructDeclaration.class);

                    for (GStructDeclaration structDecl : structs) {
                        if (structDecl.getIdentifier() != null) {
                            String structName = structDecl.getIdentifier().getText();
                            LookupElementBuilder element = LookupElementBuilder.create(structName)
                                    .withTypeText("struct", true)
                                    .withIcon(com.intellij.icons.AllIcons.Nodes.Class)
                                    .bold();

                            result.addElement(PrioritizedLookupElement.withPriority(element, 160.0));
                        }
                    }
                }
            }

            private String getIdentifierBeforeDot(CharSequence text, int dotOffset) {
                int start = dotOffset;
                while (start > 0 && Character.isJavaIdentifierPart(text.charAt(start - 1))) {
                    start--;
                }
                return text.subSequence(start, dotOffset).toString().trim();
            }

            private String resolveVariableType(PsiElement position, String varName) {
                if (varName.isEmpty()) return "";
                List<GPsiUtil.VisibleSymbol> visibleSymbols = GPsiUtil.findVisibleSymbols(position);
                for (GPsiUtil.VisibleSymbol sym : visibleSymbols) {
                    if (varName.equals(sym.name())) {
                        return sym.type();
                    }
                }
                return "";
            }

            private void addTypeAwareMembers(CompletionResultSet result, String varType) {
                if (!varType.endsWith("2") && !varType.endsWith("3") && !varType.endsWith("4")) {
                    return;
                }

                int dimension = Character.getNumericValue(varType.charAt(varType.length() - 1));

                String baseScalar = "float";
                String vecPrefix = "vec";

                if (varType.startsWith("i")) {
                    baseScalar = "int";
                    vecPrefix = "ivec";
                }
                else if (varType.startsWith("u")) {
                    baseScalar = "uint";
                    vecPrefix = "uvec";
                }
                else if (varType.startsWith("b")) {
                    baseScalar = "bool";
                    vecPrefix = "bvec";
                }
                else if (varType.startsWith("d")) {
                    baseScalar = "double";
                    vecPrefix = "dvec";
                }

                String retVec2 = vecPrefix + "2";
                String retVec3 = vecPrefix + "3";
                String retVec4 = vecPrefix + "4";

                char[] spatial = {'x', 'y', 'z', 'w'};
                for (int i = 0; i < dimension; i++) {
                    result.addElement(LookupElementBuilder.create(String.valueOf(spatial[i]))
                            .withTypeText(baseScalar)
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                }

                char[] color = {'r', 'g', 'b', 'a'};
                for (int i = 0; i < dimension; i++) {
                    result.addElement(LookupElementBuilder.create(String.valueOf(color[i]))
                            .withTypeText(baseScalar)
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                }

                char[] tex = {'s', 't', 'p', 'q'};
                for (int i = 0; i < dimension; i++) {
                    result.addElement(LookupElementBuilder.create(String.valueOf(tex[i]))
                            .withTypeText(baseScalar)
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                }

                if (dimension >= 2) {
                    String[] common2 = {"xy", "yz", "xz", "yx", "rg", "gb", "rb", "st"};
                    for (String s : common2) {
                        if (isValidSwizzleForDim(s, dimension)) {
                            result.addElement(LookupElementBuilder.create(s)
                                    .withTypeText(retVec2)
                                    .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                        }
                    }
                }

                if (dimension >= 3) {
                    String[] common3 = {"xyz", "xzy", "yxz", "zyx", "rgb", "bgr", "stp"};
                    for (String s : common3) {
                        if (isValidSwizzleForDim(s, dimension)) {
                            result.addElement(LookupElementBuilder.create(s)
                                    .withTypeText(retVec3)
                                    .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                        }
                    }
                }

                if (dimension == 4) {
                    String[] common4 = {"xyzw", "rgba", "stpq"};
                    for (String s : common4) {
                        result.addElement(LookupElementBuilder.create(s)
                                .withTypeText(retVec4)
                                .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                    }
                }

                result.addElement(LookupElementBuilder.create("length()")
                        .withTypeText("int")
                        .bold());
            }

            private boolean isValidSwizzleForDim(String swizzle, int dim) {
                String validSpatial = "xyzw".substring(0, dim);
                String validColor = "rgba".substring(0, dim);
                String validTex = "stpq".substring(0, dim);

                boolean matchSpatial = true, matchColor = true, matchTex = true;
                for (char c : swizzle.toCharArray()) {
                    if (validSpatial.indexOf(c) == -1) matchSpatial = false;
                    if (validColor.indexOf(c) == -1) matchColor = false;
                    if (validTex.indexOf(c) == -1) matchTex = false;
                }
                return matchSpatial || matchColor || matchTex;
            }

            private boolean isInsideLayoutKeyContext(CharSequence text, int offset) {
                int lineStart = Math.max(0, offset - 100);
                String beforeCaret = text.subSequence(lineStart, offset).toString();
                int lastLayout = beforeCaret.lastIndexOf("layout");
                if (lastLayout == -1) return false;

                String afterLayout = beforeCaret.substring(lastLayout);
                if (!afterLayout.contains("(") || afterLayout.contains(")")) return false;

                int openParen = afterLayout.indexOf('(');
                String insideParens = afterLayout.substring(openParen + 1);

                int lastComma = insideParens.lastIndexOf(',');
                String currentSegment = (lastComma != -1) ? insideParens.substring(lastComma + 1) : insideParens;

                return !currentSegment.contains("=");
            }

            private boolean isInsideLayoutValueContext(CharSequence text, int offset) {
                int lineStart = Math.max(0, offset - 100);
                String beforeCaret = text.subSequence(lineStart, offset).toString();
                int lastLayout = beforeCaret.lastIndexOf("layout");
                if (lastLayout == -1) return false;

                String afterLayout = beforeCaret.substring(lastLayout);
                if (!afterLayout.contains("(") || afterLayout.contains(")")) return false;

                int openParen = afterLayout.indexOf('(');
                String insideParens = afterLayout.substring(openParen + 1);

                int lastComma = insideParens.lastIndexOf(',');
                String currentSegment = (lastComma != -1) ? insideParens.substring(lastComma + 1) : insideParens;

                return currentSegment.contains("=");
            }

            private void addLayoutParamCompletions(CompletionResultSet result) {
                String[] params = {
                        "binding = ", "location = ",
                        "local_size_x = ", "local_size_y = ", "local_size_z = ",
                        "std140", "std430", "push_constant"
                };
                for (String param : params) {
                    LookupElementBuilder element = LookupElementBuilder.create(param)
                            .bold()
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Property);
                    result.addElement(PrioritizedLookupElement.withPriority(element, 300.0));
                }
            }

            private boolean isBuiltinVectorType(String typeName) {
                return typeName.endsWith("2") || typeName.endsWith("3") || typeName.endsWith("4");
            }

            private void addCustomStructMembers(CompletionResultSet result, PsiElement position, String structTypeName) {
                if (position == null || position.getContainingFile() == null) return;

                Collection<GStructDeclaration> structs =
                        PsiTreeUtil.findChildrenOfType(position.getContainingFile(), GStructDeclaration.class);

                for (GStructDeclaration structDecl : structs) {
                    if (structDecl.getIdentifier() != null && structTypeName.equals(structDecl.getIdentifier().getText())) {
                        for (GStructMember member : structDecl.getStructMemberList()) {
                            if (member.getIdentifier() != null) {
                                String fieldName = member.getIdentifier().getText();
                                String fieldType = member.getType() != null ? member.getType().getText() : "var";

                                LookupElementBuilder element = LookupElementBuilder.create(fieldName)
                                        .withTypeText(fieldType, true)
                                        .withIcon(com.intellij.icons.AllIcons.Nodes.Field)
                                        .bold();

                                result.addElement(PrioritizedLookupElement.withPriority(element, 200.0));
                            }
                        }
                        break;
                    }
                }
            }
        });
    }
}