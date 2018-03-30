package cleancode.utils;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.*;

public class PsiUtils {
    
    public static boolean isVariableReturnedByMethod(String variableName, PsiMethod surroundingMethod) {
        if (!PsiType.VOID.equals(surroundingMethod.getReturnType())) {
            Collection<PsiReturnStatement> returnStatements = PsiTreeUtil.findChildrenOfType(surroundingMethod, PsiReturnStatement.class);
            return returnStatements.stream()
                    .anyMatch(returnStatement -> {
                        PsiExpression returnValue = returnStatement.getReturnValue();
                        return returnValue != null && variableName.equals(returnValue.getText());
                    });
        }

        return false;
    }


    public static PsiMethod findSurroundingMethod(PsiElement psiElement) {
        return PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
    }


    public static Optional<PsiExpression> getAssignedValueFromDeclaration(PsiDeclarationStatement statement) {
        Optional<PsiExpression> assignedValue = Optional.empty();
        PsiElement[] declaredElements = statement.getDeclaredElements();

        if (declaredElements.length > 0) {
            int indexOfLastDeclaredElement = declaredElements.length - 1;
            PsiElement lastDeclaredElement = declaredElements[indexOfLastDeclaredElement];

            assignedValue = Arrays.stream(lastDeclaredElement.getChildren())
                    .filter(psiElement -> psiElement instanceof PsiLiteralExpression)
                    .map(psiElement -> (PsiExpression) psiElement)
                    .findFirst();
        }

        return assignedValue;
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
