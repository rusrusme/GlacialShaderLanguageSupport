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
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import net.rusrusme.bsh.psi.BshLayoutQualifier;
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

                // 1. Swizzle/Member-Completion nach einem Punkt (.)
                if (offset > 0 && text.charAt(offset - 1) == '.') {
                    addVectorAndMatrixMembers(result);
                    return;
                }

                // 2. Parameter-Completion INNERHALB von layout(...)
                if (isInsideLayout(position, text, offset)) {
                    addLayoutParamCompletions(result);
                    return;
                }

                int start = offset;
                while (start > 0) {
                    char c = text.charAt(start - 1);
                    if (Character.isJavaIdentifierPart(c) || c == '#') {
                        start--;
                        if (c == '#') break;
                    } else {
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
                        "else", "do", "return", "break", "continue", "discard", "uniform", "in", "out",
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

            private boolean isInsideLayout(PsiElement position, CharSequence text, int offset) {
                if (PsiTreeUtil.getParentOfType(position, BshLayoutQualifier.class) != null) {
                    return true;
                }
                int lineStart = Math.max(0, offset - 100);
                String beforeCaret = text.subSequence(lineStart, offset).toString();
                int lastLayout = beforeCaret.lastIndexOf("layout");
                if (lastLayout != -1) {
                    String afterLayout = beforeCaret.substring(lastLayout);
                    return afterLayout.contains("(") && !afterLayout.contains(")");
                }
                return false;
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

            private void addVectorAndMatrixMembers(CompletionResultSet result) {
                result.addElement(LookupElementBuilder.create("length()")
                        .withTypeText("int")
                        .bold());

                String[] swizzles = {
                        "x", "y", "z", "w",
                        "r", "g", "b", "a",
                        "s", "t", "p", "q",
                        "xy", "xyz", "xyzw",
                        "rgb", "rgba",
                        "st", "stp"
                };
                for (String s : swizzles) {
                    result.addElement(LookupElementBuilder.create(s)
                            .withTypeText("float")
                            .withIcon(com.intellij.icons.AllIcons.Nodes.Field));
                }
            }
        });
    }
}