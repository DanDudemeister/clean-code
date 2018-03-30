package cleancode.nullreturn.quickfixes;

import cleancode.nullreturn.quickfixes.implementations.EmptyListQuickFix;
import cleancode.nullreturn.quickfixes.implementations.EmptyStringQuickFix;
import cleancode.utils.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

import java.util.Optional;

public class QuickFixFactory {

    private static final String JAVA_LANG_STRING = "java.lang.String";
    private static final String JAVA_UTIL_LIST_REGEX = "^(java.util.List<).*(>)$";


    public static Optional<LocalQuickFix> getQuickFix(PsiElement psiElement) {
        Optional<LocalQuickFix> quickFix = Optional.empty();

        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(psiElement);
        PsiType returnType = surroundingMethod.getReturnType();

        if (returnType != null) {
            String returnTypeAsText = returnType.getCanonicalText(false);

            if (returnTypeAsText.equals(JAVA_LANG_STRING)) {
                return Optional.of(new EmptyStringQuickFix());

            } else if (returnTypeAsText.matches(JAVA_UTIL_LIST_REGEX)) {
                return Optional.of(new EmptyListQuickFix());
            }
        }

        return quickFix;
    }
}
