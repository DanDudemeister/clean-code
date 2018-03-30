package cleancode.nullreturn.detectors;

import cleancode.nullreturn.quickfixes.QuickFixFactory;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;

import java.util.Optional;

public abstract class NullDetector {

    private PsiElement inspectedElement;


    public NullDetector(PsiElement inspectedElement) {
        this.inspectedElement = inspectedElement;
    }


    public abstract boolean isNullDetected();


    public Optional<LocalQuickFix> getQuickFix() {
        return QuickFixFactory.getQuickFix(inspectedElement);
    };
}
