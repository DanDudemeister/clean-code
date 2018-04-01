package cleancode.nullreturn.detectors.implementations;

import cleancode.nullreturn.detectors.NullDetector;
import cleancode.utils.PsiUtils;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;

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

        //TODO linke Seite Arrayzugriff?
        if (leftExpression instanceof PsiReferenceExpressionImpl) {
            return ((PsiReferenceExpressionImpl) leftExpression).getReferenceName();
        }

        return "";
    }


    private boolean isNullAssigned() {
        PsiExpression assignedValue = PsiUtils.getAssignedValueRecursively(assignmentExpression.getRExpression());
        return assignedValue != null && PsiType.NULL.equals(assignedValue.getType());
    }
}
