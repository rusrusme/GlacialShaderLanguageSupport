package net.rusrusme.bsh.core;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class BshCompletionContributor extends CompletionContributor {
    public BshCompletionContributor() {
        InsertHandler<com.intellij.codeInsight.lookup.LookupElement> parensInsertHandler = (context, item) -> {
            Editor editor = context.getEditor();
            int offset = context.getTailOffset(); // Das Ende des eingefügten Wortes (z.B. nach "if")

            editor.getDocument().insertString(offset, " ()");

            editor.getCaretModel().moveToOffset(offset + 2);
        };

        // Registriere die Vorschläge für jedes PsiElement
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

                // Keywords MIT Klammer-Generierung
                String[] statementsWithParens = {"if", "while", "for", "switch"};
                for (String keyword : statementsWithParens) {
                    result.addElement(LookupElementBuilder.create(keyword).bold() // Macht die Schrift fett im Popup
                            .withInsertHandler(parensInsertHandler));
                }

                // Keywords OHNE Klammer-Generierung (nur als Text-Autocomplete)
                String[] simpleKeywords = {"else", "do", "return", "break", "continue", "discard", "void", "vec2", "vec3", "vec4", "mat3", "mat4", "float", "int"};
                for (String keyword : simpleKeywords) {
                    result.addElement(LookupElementBuilder.create(keyword).bold());
                }
            }
        });
    }
}
