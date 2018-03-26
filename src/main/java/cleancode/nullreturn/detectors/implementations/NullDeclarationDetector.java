package cleancode.nullreturn.detectors.implementations;

import cleancode.nullreturn.detectors.NullDetector;
import cleancode.nullreturn.detectors.utils.PsiUtils;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiIdentifierImpl;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;

import java.util.Arrays;
import java.util.Optional;

public class NullDeclarationDetector implements NullDetector {

    private PsiDeclarationStatement statement;


    public NullDeclarationDetector(PsiDeclarationStatement statement) {
        this.statement = statement;
    }


    @Override
    public boolean possiblyReturnsNull() {
        String variableNameFromDeclaration = getVariableNameFromDeclaration();
        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(statement);
        Optional<PsiLiteralExpressionImpl> assignedLiteral = getAssignedLiteral();

        return assignedLiteral.isPresent() &&
                isNullAssigned(assignedLiteral.get()) &&
                PsiUtils.isVariableReturnedByMethod(variableNameFromDeclaration, surroundingMethod);

    }


    private String getVariableNameFromDeclaration() {
        PsiIdentifierImpl psiIdentifier = (PsiIdentifierImpl) Arrays.stream(statement.getDeclaredElements()[0].getChildren())
                .filter(psiElement -> psiElement instanceof PsiIdentifierImpl)
                .findFirst()
                .get();

        return psiIdentifier.getText();
    }


    private Optional<PsiLiteralExpressionImpl> getAssignedLiteral() {
        return Arrays.stream(statement.getDeclaredElements()[0].getChildren())
                .filter(psiElement -> psiElement instanceof PsiLiteralExpressionImpl)
                .map(psiElement -> (PsiLiteralExpressionImpl) psiElement)
                .findFirst();
    }


    private boolean isNullAssigned(PsiLiteralExpressionImpl psiLiteralExpression) {
        return PsiType.NULL.equals(psiLiteralExpression.getType());
    }
}
