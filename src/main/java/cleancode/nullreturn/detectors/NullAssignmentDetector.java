package cleancode.nullreturn.detectors;

import com.intellij.psi.PsiAssignmentExpression;

public class NullAssignmentDetector implements NullReturnDetector {

    private PsiAssignmentExpression assignmentExpression;


    public NullAssignmentDetector(PsiAssignmentExpression assignmentExpression) {
        this.assignmentExpression = assignmentExpression;
    }


    @Override
    public boolean possiblyReturnsNull() {
        return false;
    }
}
