package cleancode.nullreturn.quickfixes;

import cleancode.nullreturn.quickfixes.implementations.EmptyStringQuickFix;
import cleancode.nullreturn.utils.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

public class QuickFixFactory {

    private static final String JAVA_LANG_STRING = "java.lang.String";


    public static LocalQuickFix getQuickFix(PsiElement psiElement) {
        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(psiElement);
        PsiType returnType = surroundingMethod.getReturnType();

        if (returnType.equalsToText(JAVA_LANG_STRING)) {
            return new EmptyStringQuickFix();
        }

        //TODO: remove when all quickfixes have been implemented
        return new EmptyStringQuickFix();
    }
}
