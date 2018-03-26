package cleancode.nullreturn.detectors.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Collection;

public class PsiUtils {
    
    public static boolean isVariableReturnedByMethod(String variableName, PsiMethod surroundingMethod) {
        if (!PsiType.VOID.equals(surroundingMethod.getReturnType())) {
            Collection<PsiReturnStatement> returnStatements = PsiTreeUtil.findChildrenOfType(surroundingMethod, PsiReturnStatement.class);
            return returnStatements.stream()
                    .anyMatch(returnStatement ->
                            variableName.equals(returnStatement.getReturnValue().getText())
                    );
        }

        return false;
    }


    public static PsiMethod findSurroundingMethod(PsiElement psiElement) {
        return PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
    }
}
