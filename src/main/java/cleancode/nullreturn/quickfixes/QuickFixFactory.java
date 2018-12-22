package cleancode.nullreturn.quickfixes;

import cleancode.nullreturn.quickfixes.implementations.optional.OptionalQuickFix;
import cleancode.utils.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

import java.util.Optional;

public class QuickFixFactory {

    public static Optional<LocalQuickFix> getQuickFix(PsiElement psiElement) {
        Optional<LocalQuickFix> quickFix = Optional.empty();

        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(psiElement);
        PsiType returnType = surroundingMethod.getReturnType();

        if (returnType != null) {
            return Optional.of(new OptionalQuickFix());
        }

        return quickFix;
    }
}
