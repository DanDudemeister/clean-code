package cleancode.nullreturn.detectors;

import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReturnStatement;

public class NullReturnDetectorFactory {

    public static NullReturnDetector getNullDetector(DetectorType detectorType, PsiElement psiElement) {
        NullReturnDetector nullReturnDetector;

        switch (detectorType) {
            case ASSIGNMENT:
                nullReturnDetector = new NullAssignmentDetector((PsiAssignmentExpression) psiElement);
                break;

            case RETURN_STATEMENT:
                nullReturnDetector = new NullReturnStatementDetector((PsiReturnStatement) psiElement);
                break;

            default:
                throw new IllegalArgumentException("Unsupported DetectorType!");
        }

        return nullReturnDetector;
    }
}
