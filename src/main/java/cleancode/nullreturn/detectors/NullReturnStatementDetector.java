package cleancode.nullreturn.detectors;

import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;

public class NullReturnStatementDetector implements NullReturnDetector {

    private PsiReturnStatement returnStatement;


    public NullReturnStatementDetector(PsiReturnStatement returnStatement) {
        this.returnStatement = returnStatement;
    }


    @Override
    public boolean possiblyReturnsNull() {
        PsiType returnValueType = returnStatement.getReturnValue().getType();
        return PsiType.NULL.equals(returnValueType);
    }
}
