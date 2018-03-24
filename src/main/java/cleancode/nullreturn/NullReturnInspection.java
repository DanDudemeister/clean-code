package cleancode.nullreturn;

import cleancode.nullreturn.detectors.DetectorType;
import cleancode.nullreturn.detectors.NullReturnDetectorFactory;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReturnStatement;
import cleancode.nullreturn.detectors.NullReturnDetector;
import org.jetbrains.annotations.NotNull;

public class NullReturnInspection extends JavaElementVisitor {

    private ProblemsHolder holder;


    public NullReturnInspection(@NotNull final ProblemsHolder holder) {
        this.holder = holder;
    }


    @Override
    public void visitReturnStatement(PsiReturnStatement statement) {
        NullReturnDetector nullReturnDetector = NullReturnDetectorFactory.getNullDetector(DetectorType.RETURN_STATEMENT, statement);
        detectNullAndApplyQuickfixIfNecessary(nullReturnDetector, statement);
    }


    @Override
    public void visitAssignmentExpression(PsiAssignmentExpression expression) {
        NullReturnDetector nullReturnDetector = NullReturnDetectorFactory.getNullDetector(DetectorType.ASSIGNMENT, expression);
        detectNullAndApplyQuickfixIfNecessary(nullReturnDetector, expression);
    }


    private void detectNullAndApplyQuickfixIfNecessary(NullReturnDetector nullReturnDetector, PsiElement psiElement) {
        if (nullReturnDetector.possiblyReturnsNull()) {
            LocalQuickFix quickFix = nullReturnDetector.getFix();
            holder.registerProblem(psiElement, "Don't return null!", quickFix);
        }
    }
}
