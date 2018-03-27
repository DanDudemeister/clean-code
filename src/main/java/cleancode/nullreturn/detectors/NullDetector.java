package cleancode.nullreturn.detectors;

import cleancode.nullreturn.quickfixes.QuickFixFactory;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;

public abstract class NullDetector {

    private PsiElement inspectedElement;


    public NullDetector(PsiElement inspectedElement) {
        this.inspectedElement = inspectedElement;
    }


    public abstract boolean possiblyReturnsNull();


    public LocalQuickFix getQuickFix() {
        return QuickFixFactory.getQuickFix(inspectedElement);
    };
}
