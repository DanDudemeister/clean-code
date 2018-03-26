package cleancode.nullreturn.detectors.implementations;

import cleancode.nullreturn.detectors.NullDetector;
import cleancode.nullreturn.detectors.utils.PsiUtils;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;

public class NullAssignmentDetector implements NullDetector {

    private PsiAssignmentExpression assignmentExpression;


    public NullAssignmentDetector(PsiAssignmentExpression assignmentExpression) {
        this.assignmentExpression = assignmentExpression;
    }


    @Override
    public boolean possiblyReturnsNull() {
        String variableName = getVariableNameFromAssignment();
        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(assignmentExpression);

        boolean isNullAssigned = PsiType.NULL.equals(assignmentExpression.getRExpression().getType());

        return isNullAssigned && PsiUtils.isVariableReturnedByMethod(variableName, surroundingMethod);
    }

    private String getVariableNameFromAssignment() {
        return ((PsiReferenceExpressionImpl) assignmentExpression.getLExpression()).getReferenceName();
    }
}
