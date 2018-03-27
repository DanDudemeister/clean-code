package cleancode.nullreturn.detectors.implementations;

import cleancode.nullreturn.detectors.NullDetector;
import cleancode.nullreturn.detectors.utils.PsiUtils;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiIdentifierImpl;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NullDeclarationDetector implements NullDetector {

    private PsiDeclarationStatement statement;


    public NullDeclarationDetector(PsiDeclarationStatement statement) {
        this.statement = statement;
    }


    @Override
    public boolean possiblyReturnsNull() {
        List<String> variableNamesFromDeclaration = getVariableNamesFromDeclaration();
        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(statement);
        Optional<PsiLiteralExpressionImpl> assignedValue = getAssignedValue();

        return assignedValue.isPresent() &&
                isNullAssigned(assignedValue.get()) &&
                isAnyOfDeclaredVariablesReturnedByMethod(variableNamesFromDeclaration, surroundingMethod);

    }


    private List<String> getVariableNamesFromDeclaration() {
        return Arrays.stream(statement.getDeclaredElements())
                .flatMap(declaredElement -> Arrays.stream(declaredElement.getChildren()))
                .filter(psiElement -> psiElement instanceof PsiIdentifierImpl)
                .map(PsiElement::getText)
                .collect(Collectors.toList());
    }


    private Optional<PsiLiteralExpressionImpl> getAssignedValue() {
        PsiElement[] declaredElements = statement.getDeclaredElements();
        int indexOfLastDeclaredElement = declaredElements.length - 1;
        PsiElement lastDeclaredElement = declaredElements[indexOfLastDeclaredElement];

        return Arrays.stream(lastDeclaredElement.getChildren())
                .filter(psiElement -> psiElement instanceof PsiLiteralExpressionImpl)
                .map(psiElement -> (PsiLiteralExpressionImpl) psiElement)
                .findFirst();
    }


    private boolean isNullAssigned(PsiLiteralExpressionImpl psiLiteralExpression) {
        return PsiType.NULL.equals(psiLiteralExpression.getType());
    }


    private boolean isAnyOfDeclaredVariablesReturnedByMethod(List<String> variableNamesFromDeclaration, PsiMethod surroundingMethod) {
        return variableNamesFromDeclaration.stream()
                .anyMatch(variableName -> PsiUtils.isVariableReturnedByMethod(variableName, surroundingMethod));
    }
}
