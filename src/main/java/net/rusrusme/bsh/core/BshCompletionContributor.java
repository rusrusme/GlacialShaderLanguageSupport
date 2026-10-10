package net.rusrusme.bsh.core;

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
        InsertHandler<LookupElement> parensInsertHandler = (context, item) -> {
            Editor editor = context.getEditor();
            Project project = context.getProject();

            // Create a Live Template session for " ($EXPR$) $END$"
            TemplateManager templateManager = TemplateManager.getInstance(project);
            Template template = templateManager.createTemplate("", "", " ($EXPR$)$END$");
            template.addVariable("EXPR", new ConstantNode(""), true);

            // Start the template session
            templateManager.startTemplate(editor, template);
        };

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {

                int offset = parameters.getOffset();
                Document document = parameters.getEditor().getDocument();
                CharSequence text = document.getCharsSequence();

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

                String[] statementsWithParens = {"if", "while", "for", "switch"};
                for (String keyword : statementsWithParens) {
                    LookupElementBuilder element = LookupElementBuilder.create(keyword)
                            .bold()
                            .withTailText(" ()", true)
                            .withInsertHandler(parensInsertHandler);
                    result.addElement(PrioritizedLookupElement.withPriority(element, 100.0));
                }

                String[] simpleKeywords = {
                        "else", "do", "return", "break", "continue", "discard", "layout", "uniform", "in", "out",
                        "void", "float", "int", "uint", "bool", "double", "once",
                        "vec2", "vec3", "vec4", "ivec2", "ivec3", "ivec4", "uvec2", "uvec3", "uvec4", "bvec2", "bvec3", "bvec4",
                        "mat2", "mat3", "mat4", "sampler2D", "samplerCube", "image2D"
                };
                for (String keyword : simpleKeywords) {
                    result.addElement(LookupElementBuilder.create(keyword).bold());
                }

                PsiElement position = parameters.getPosition();
                List<BshVariableDeclaration> visibleVars = BshPsiUtil.findVisibleVariables(position);

                for (BshVariableDeclaration var : visibleVars) {
                    if (var.getIdentifier() != null) {
                        String varName = var.getIdentifier().getText();
                        String varType = var.getType() != null ? var.getType().getText() : "var";

                        LookupElementBuilder element = LookupElementBuilder.create(varName)
                                .withTypeText(varType, true) // Displays type on right side (e.g., "vec3")
                                .withIcon(com.intellij.icons.AllIcons.Nodes.Variable)
                                .bold();

                        result.addElement(PrioritizedLookupElement.withPriority(element, 150.0));
                    }
                }
            }
        });
    }
}