package net.rusrusme.bsh.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import net.rusrusme.bsh.psi.*;
import org.jetbrains.annotations.NotNull;

public class BshAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element.getNode().getElementType() != BshTypes.IDENTIFIER) {
            return;
        }

        PsiElement parent = element.getParent();

        // Deklarationselemente überspringen
        if (parent instanceof BshVariableDeclaration varDecl && varDecl.getIdentifier() == element) return;
        if (parent instanceof BshFunctionDefinition funcDef && funcDef.getIdentifier() == element) return;
        if (parent instanceof BshStructDeclaration structDecl && structDecl.getIdentifier() == element) return;
        if (parent instanceof BshParameter param && param.getIdentifier() == element) return;
        if (parent instanceof BshStructMember member && member.getIdentifier() == element) return;
        if (parent instanceof BshMemberAccess) return;

        PsiReference reference = element.getReference();
        if (reference != null && reference.resolve() == null) {
            String name = element.getText();

            if (isBuiltinFunction(name)) return;

            holder.newAnnotation(HighlightSeverity.ERROR, "Cannot resolve symbol '" + name + "'")
                    .range(element)
                    .create();
        }
    }

    private boolean isBuiltinFunction(String name) {
        return switch (name) {
            case "radians", "degrees", "sin", "cos", "tan", "asin", "acos", "atan", "pow", "exp", "log", "exp2", "log2",
                 "sqrt", "inversesqrt", "abs", "sign", "floor", "trunc", "round", "ceil", "fract", "mod", "min", "max",
                 "clamp", "mix", "step", "smoothstep", "length", "distance", "dot", "cross", "normalize", "faceforward",
                 "reflect", "refract", "matrixCompMult", "transpose", "determinant", "inverse", "texture", "textureLod",
                 "texelFetch", "textureSize", "main" -> true;
            default -> false;
        };
    }
}