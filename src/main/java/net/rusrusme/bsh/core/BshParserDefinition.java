package net.rusrusme.bsh.core;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import net.rusrusme.bsh.lexer.BshLexer;
import net.rusrusme.bsh.parser.BshParser;
import net.rusrusme.bsh.psi.BshTypes;
import org.jetbrains.annotations.NotNull;

public class BshParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(BshLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FlexAdapter(new BshLexer(null));
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new BshParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(BshTypes.COMMENT);
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return BshTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new BshFile(viewProvider);
    }
}
