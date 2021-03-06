package cleancode.nullreturn.detectors.implementations;

import cleancode.nullreturn.detectors.NullDetector;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;

public class NullReturnStatementDetector extends NullDetector {

    private PsiReturnStatement returnStatement;


    public NullReturnStatementDetector(PsiReturnStatement returnStatement) {
        super(returnStatement);
        this.returnStatement = returnStatement;
    }


    @Override
    public boolean isNullDetected() {
        return returnsNullLiteral();
    }


    private boolean returnsNullLiteral() {
        PsiExpression returnValue = returnStatement.getReturnValue();
        return returnValue != null && PsiType.NULL.equals(returnValue.getType());
    }
}
