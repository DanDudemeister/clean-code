package cleancode.nullreturn.detectors.implementations;

import cleancode.nullreturn.detectors.NullDetector;
import cleancode.utils.PsiUtils;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;

public class NullAssignmentDetector extends NullDetector {

    private PsiAssignmentExpression assignmentExpression;


    public NullAssignmentDetector(PsiAssignmentExpression assignmentExpression) {
        super(assignmentExpression);
        this.assignmentExpression = assignmentExpression;
    }


    @Override
    public boolean isNullDetected() {
        String variableName = getVariableNameFromAssignment();
        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(assignmentExpression);

        return isNullAssigned() && PsiUtils.isVariableReturnedByMethod(variableName, surroundingMethod);
    }


    private String getVariableNameFromAssignment() {
        PsiExpression leftExpression = assignmentExpression.getLExpression();

        if (leftExpression instanceof PsiReferenceExpression) {
            return ((PsiReferenceExpression) leftExpression).getReferenceName();
        }

        return "";
    }


    private boolean isNullAssigned() {
        PsiExpression assignedValue = PsiUtils.getAssignedValueRecursively(assignmentExpression.getRExpression());
        return assignedValue != null && PsiType.NULL.equals(assignedValue.getType());
    }
}
