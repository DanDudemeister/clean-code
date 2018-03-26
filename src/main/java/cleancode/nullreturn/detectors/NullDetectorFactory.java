package cleancode.nullreturn.detectors;

import cleancode.nullreturn.detectors.implementations.NullAssignmentDetector;
import cleancode.nullreturn.detectors.implementations.NullDeclarationDetector;
import cleancode.nullreturn.detectors.implementations.NullReturnStatementDetector;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReturnStatement;

public class NullDetectorFactory {

    public static NullDetector getNullDetector(DetectorType detectorType, PsiElement psiElement) {
        NullDetector nullDetector;

        switch (detectorType) {
            case ASSIGNMENT:
                nullDetector = new NullAssignmentDetector((PsiAssignmentExpression) psiElement);
                break;

            case DECLARATION:
                nullDetector = new NullDeclarationDetector((PsiDeclarationStatement) psiElement);
                break;

            case RETURN_STATEMENT:
                nullDetector = new NullReturnStatementDetector((PsiReturnStatement) psiElement);
                break;

            default:
                throw new IllegalArgumentException("Unsupported DetectorType!");
        }

        return nullDetector;
    }
}
