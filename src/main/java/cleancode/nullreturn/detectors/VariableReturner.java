package cleancode.nullreturn.detectors;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Collection;

public abstract class VariableReturner {
    
    private String variableName;
    private PsiMethod surroundingMethod;

    public VariableReturner(String variableName, PsiMethod surroundingMethod) {
        this.variableName = variableName;
        this.surroundingMethod = surroundingMethod;
    }

    protected boolean isVariableReturned() {
        if (!PsiType.VOID.equals(surroundingMethod.getReturnType())) {
            Collection<PsiReturnStatement> returnStatements = PsiTreeUtil.findChildrenOfType(surroundingMethod, PsiReturnStatement.class);
            return returnStatements.stream()
                    .anyMatch(returnStatement ->
                            variableName.equals(returnStatement.getReturnValue().getText())
                    );
        }

        return false;
    }
}
