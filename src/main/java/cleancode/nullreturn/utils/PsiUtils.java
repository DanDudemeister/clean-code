package cleancode.nullreturn.utils;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.*;

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


    public static Optional<PsiExpression> getAssignedValueFromDeclaration(PsiDeclarationStatement statement) {
        PsiElement[] declaredElements = statement.getDeclaredElements();
        int indexOfLastDeclaredElement = declaredElements.length - 1;
        PsiElement lastDeclaredElement = declaredElements[indexOfLastDeclaredElement];

        return Arrays.stream(lastDeclaredElement.getChildren())
                .filter(psiElement -> psiElement instanceof PsiLiteralExpression)
                .map(psiElement -> (PsiExpression) psiElement)
                .findFirst();
    }


    public static Optional<PsiExpression> getAssignedOrReturnedExpressionFromElement(PsiElement element) {
        Optional<PsiExpression> expression = Optional.empty();

        if (element instanceof PsiReturnStatement) {
            expression = Optional.of(((PsiReturnStatement) element).getReturnValue());

        } else if (element instanceof PsiAssignmentExpression) {
            expression = Optional.of(((PsiAssignmentExpression) element).getRExpression());

        } else if (element instanceof PsiDeclarationStatement) {
            expression = PsiUtils.getAssignedValueFromDeclaration((PsiDeclarationStatement) element);
        }

        return expression;
    }
}
