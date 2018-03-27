package cleancode.nullreturn;

import cleancode.nullreturn.detectors.DetectorType;
import cleancode.nullreturn.detectors.NullDetectorFactory;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import cleancode.nullreturn.detectors.NullDetector;
import org.jetbrains.annotations.NotNull;

public class NullReturnInspection extends JavaElementVisitor {

    private ProblemsHolder holder;


    public NullReturnInspection(@NotNull final ProblemsHolder holder) {
        this.holder = holder;
    }


    @Override
    public void visitReturnStatement(PsiReturnStatement statement) {
        String inspectionMessage = "You should not return a null-literal";
        NullDetector nullDetector = NullDetectorFactory.getNullDetector(DetectorType.RETURN_STATEMENT, statement);
        detectNullAndApplyQuickfixIfNecessary(nullDetector, statement, inspectionMessage);
    }


    @Override
    public void visitAssignmentExpression(PsiAssignmentExpression expression) {
        String inspectionMessage = "You assign null to a variable which is returned later";
        NullDetector nullDetector = NullDetectorFactory.getNullDetector(DetectorType.ASSIGNMENT, expression);
        detectNullAndApplyQuickfixIfNecessary(nullDetector, expression, inspectionMessage);
    }


    @Override
    public void visitDeclarationStatement(PsiDeclarationStatement statement) {
        String inspectionMessage = "You initialize a variable with null which is returned later";
        NullDetector nullDetector = NullDetectorFactory.getNullDetector(DetectorType.DECLARATION, statement);
        detectNullAndApplyQuickfixIfNecessary(nullDetector, statement, inspectionMessage);
    }

    private void detectNullAndApplyQuickfixIfNecessary(NullDetector nullDetector, PsiElement psiElement, String message) {
        if (nullDetector.isNullDetected()) {
            LocalQuickFix quickFix = nullDetector.getQuickFix();
            holder.registerProblem(psiElement, message, quickFix);
        }
    }
}
