package cleancode.nullreturn.detectors.implementations;

import cleancode.nullreturn.detectors.NullDetector;
import cleancode.utils.PsiUtils;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NullDeclarationDetector extends NullDetector {

    private PsiDeclarationStatement statement;


    public NullDeclarationDetector(PsiDeclarationStatement statement) {
        super(statement);
        this.statement = statement;
    }


    @Override
    public boolean isNullDetected() {
        List<String> variableNamesFromDeclaration = getVariableNamesFromDeclaration();
        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(statement);
        Optional<PsiExpression> assignedValue = PsiUtils.getAssignedValueFromDeclaration(statement);

        return assignedValue.isPresent() &&
                isNullAssigned(assignedValue.get()) &&
                isAnyOfDeclaredVariablesReturnedByMethod(variableNamesFromDeclaration, surroundingMethod);

    }


    private List<String> getVariableNamesFromDeclaration() {
        return Arrays.stream(statement.getDeclaredElements())
                .flatMap(declaredElement -> Arrays.stream(declaredElement.getChildren()))
                .filter(psiElement -> psiElement instanceof PsiIdentifier)
                .map(PsiElement::getText)
                .collect(Collectors.toList());
    }


    private boolean isNullAssigned(PsiExpression psiExpression) {
        return PsiType.NULL.equals(psiExpression.getType());
    }


    private boolean isAnyOfDeclaredVariablesReturnedByMethod(List<String> variableNamesFromDeclaration, PsiMethod surroundingMethod) {
        return variableNamesFromDeclaration.stream()
                .anyMatch(variableName -> PsiUtils.isVariableReturnedByMethod(variableName, surroundingMethod));
    }
}
