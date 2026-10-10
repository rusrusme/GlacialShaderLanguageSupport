package net.rusrusme.bsh.core;

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
import com.intellij.util.ProcessingContext;
import net.rusrusme.bsh.psi.BshPsiUtil;
import net.rusrusme.bsh.psi.BshVariableDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BshCompletionContributor extends CompletionContributor {
    public BshCompletionContributor() {
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

            // Blendet direkt das Popup mit (binding =, location =, etc.) auf
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
                    addTypeAwareMembers(result, varType);
                    return;
                }

                // 2. Layout-Parameter Schlüsselwörter NUR vorschlagen, wenn wir VOR dem '=' stehen
                if (isInsideLayoutKeyContext(text, offset)) {
                    addLayoutParamCompletions(result);
                    return;
                }

                // Wenn wir hinter '=' in layout(...) stehen, normale Keyword-Vorschläge unterdrücken
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
                        "#define", "#ifdef", "#ifndef", "#endif"
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

                // 3. Layout Vorschlag mit Klammer-Generierung
                LookupElementBuilder layoutElement = LookupElementBuilder.create("layout")
                        .bold()
                        .withTailText(" ()", true)
                        .withInsertHandler(layoutInsertHandler);
                result.addElement(PrioritizedLookupElement.withPriority(layoutElement, 120.0));

                // Control Flow Keywords mit ()
                String[] statementsWithParens = {"if", "while", "for", "switch"};
                for (String keyword : statementsWithParens) {
                    LookupElementBuilder element = LookupElementBuilder.create(keyword)
                            .bold()
                            .withTailText(" ()", true)
                            .withInsertHandler(parensInsertHandler);
                    result.addElement(PrioritizedLookupElement.withPriority(element, 100.0));
                }

                // Standard Keywords
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

                List<BshVariableDeclaration> visibleVars = BshPsiUtil.findVisibleVariables(position);

                for (BshVariableDeclaration var : visibleVars) {
                    if (var.getIdentifier() != null) {
                        String varName = var.getIdentifier().getText();
                        String varType = var.getType() != null ? var.getType().getText() : "var";

                        LookupElementBuilder element = LookupElementBuilder.create(varName)
                                .withTypeText(varType, true)
                                .withIcon(com.intellij.icons.AllIcons.Nodes.Variable)
                                .bold();

                        result.addElement(PrioritizedLookupElement.withPriority(element, 150.0));
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
                if (varName.isEmpty()) return "vec4"; // Default Fallback
                List<BshVariableDeclaration> visibleVars = BshPsiUtil.findVisibleVariables(position);
                for (BshVariableDeclaration var : visibleVars) {
                    if (var.getIdentifier() != null && varName.equals(var.getIdentifier().getText())) {
                        return var.getType() != null ? var.getType().getText() : "vec4";
                    }
                }
                return "vec4";
            }

            private void addTypeAwareMembers(CompletionResultSet result, String varType) {
                int dimension = 4;
                String baseScalar = "float";

                if (varType.endsWith("2")) {
                    dimension = 2;
                }
                else if (varType.endsWith("3")) {
                    dimension = 3;
                }
                else if (varType.endsWith("4")) dimension = 4;

                if (varType.startsWith("i")) {
                    baseScalar = "int";
                }
                else if (varType.startsWith("u")) {
                    baseScalar = "uint";
                }
                else if (varType.startsWith("b")) baseScalar = "bool";

                String retVec2 = varType.startsWith("v") || varType.startsWith("m") ? "vec2" : baseScalar + "2";
                String retVec3 = varType.startsWith("v") || varType.startsWith("m") ? "vec3" : baseScalar + "3";
                String retVec4 = varType.startsWith("v") || varType.startsWith("m") ? "vec4" : baseScalar + "4";

                // 1. Position / Spatial: x, y, z, w
                char[] spatial = {'x', 'y', 'z', 'w'};
                for (int i = 0; i < dimension; i++) {
                    result.addElement(LookupElementBuilder.create(String.valueOf(spatial[i]))
                            .withTypeText(baseScalar)
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                }

                // 2. Color: r, g, b, a
                char[] color = {'r', 'g', 'b', 'a'};
                for (int i = 0; i < dimension; i++) {
                    result.addElement(LookupElementBuilder.create(String.valueOf(color[i]))
                            .withTypeText(baseScalar)
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                }

                // 3. Texture: s, t, p, q
                char[] tex = {'s', 't', 'p', 'q'};
                for (int i = 0; i < dimension; i++) {
                    result.addElement(LookupElementBuilder.create(String.valueOf(tex[i]))
                            .withTypeText(baseScalar)
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                }

                // 4. 2D Swizzles (xy, yz, xz ...)
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

                // 5. 3D Swizzles (xyz, xzy, yxz ...)
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

                // 6. 4D Swizzles
                if (dimension == 4) {
                    String[] common4 = {"xyzw", "rgba", "stpq"};
                    for (String s : common4) {
                        result.addElement(LookupElementBuilder.create(s)
                                .withTypeText(retVec4)
                                .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                    }
                }

                // 7. Methoden ganz unten
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
        });
    }
}