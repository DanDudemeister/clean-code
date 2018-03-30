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


    private boolean isNullAssigned() {
        PsiExpression rightExpression = assignmentExpression.getRExpression();
        return rightExpression != null && PsiType.NULL.equals(rightExpression.getType());
    }


    private String getVariableNameFromAssignment() {
        return ((PsiReferenceExpressionImpl) assignmentExpression.getLExpression()).getReferenceName();
    }
}
