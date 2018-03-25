package cleancode.nullreturn.detectors;

import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;

public class NullAssignmentDetector extends VariableReturner implements NullReturnDetector {

    private PsiAssignmentExpression assignmentExpression;


    public NullAssignmentDetector(PsiAssignmentExpression assignmentExpression) {
        super(
            ((PsiReferenceExpressionImpl) assignmentExpression.getLExpression()).getReferenceName(),
            PsiTreeUtil.getParentOfType(assignmentExpression, PsiMethod.class)
        );
        this.assignmentExpression = assignmentExpression;
    }


    @Override
    public boolean possiblyReturnsNull() {
        boolean isNullAssigned = PsiType.NULL.equals(assignmentExpression.getRExpression().getType());
        return isNullAssigned && isVariableReturned();
    }
}
