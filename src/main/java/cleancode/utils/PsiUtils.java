package cleancode.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

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

        if (element != null) {
            if (element instanceof PsiReturnStatement) {
                expression = Optional.of(((PsiReturnStatement) element).getReturnValue());

            } else if (element instanceof PsiAssignmentExpression) {
                expression = Optional.of(
                    getAssignedExpressionRecursively(((PsiAssignmentExpression) element).getRExpression()));

            } else if (element instanceof PsiDeclarationStatement) {
                expression = getAssignedValueFromDeclaration((PsiDeclarationStatement) element);
            }
        }

        return expression;
    }


    public static PsiExpression getAssignedExpressionRecursively(PsiExpression expression) {
        if (expression instanceof PsiAssignmentExpression) {
            return getAssignedExpressionRecursively(((PsiAssignmentExpression) expression).getRExpression());
        } else {
            return expression;
        }
    }


    public static PsiElement replaceFullyQualifiedNameWithImport(PsiElement psiElementWithFullyQualifiedClassNames, Project project) {
        return JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiElementWithFullyQualifiedClassNames);
    }


    public static Collection<PsiReference> findUsagesOfMethod(PsiMethod method) {
        return ReferencesSearch.search(method).findAll();
    }
}
