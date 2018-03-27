package cleancode.nullreturn.detectors.implementations;

import cleancode.nullreturn.detectors.NullDetector;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;

public class NullReturnStatementDetector extends NullDetector {

    private PsiReturnStatement returnStatement;


    public NullReturnStatementDetector(PsiReturnStatement returnStatement) {
        super(returnStatement);
        this.returnStatement = returnStatement;
    }


    @Override
    public boolean possiblyReturnsNull() {
        return returnsNullLiteral();
    }


    private boolean returnsNullLiteral() {
        PsiType returnValueType = returnStatement.getReturnValue().getType();
        return PsiType.NULL.equals(returnValueType);
    }
}
