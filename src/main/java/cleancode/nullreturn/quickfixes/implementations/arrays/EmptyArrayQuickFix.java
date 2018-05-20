package cleancode.nullreturn.quickfixes.implementations.arrays;

import cleancode.utils.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import java.util.Optional;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class EmptyArrayQuickFix implements LocalQuickFix {

    @Nls
    @NotNull
    @Override
    public String getName() {
        return "Replace null with an empty array";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }


    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        PsiElement psiElement = problemDescriptor.getPsiElement();
        Optional<String> optionalReturnType = getReturnTypeFromPsiElement(psiElement);

        optionalReturnType.ifPresent(returnType -> {
            replaceNullWithEmptyArray(project, psiElement, returnType);
        });
    }


    @NotNull
    private Optional<String> getReturnTypeFromPsiElement(PsiElement psiElement) {
        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(psiElement);

        if (surroundingMethod.getReturnType() != null) {
            return Optional.of(surroundingMethod.getReturnType().getPresentableText());
        }

        return Optional.empty();
    }


    @NotNull
    private String createArrayExpressionAsTextFromReturnType(String returnType) {
        String delimiter = "0]";
        String[] stringParts = returnType.split("]");
        return "new  " + String.join(delimiter, stringParts) + delimiter;
    }


    private void replaceNullWithEmptyArray(@NotNull Project project, PsiElement psiElement,
        String returnType) {
        String newArrayExpressionAsText = createArrayExpressionAsTextFromReturnType(returnType);

        PsiExpression emptyArrayExpression = PsiElementFactory.SERVICE.getInstance(project)
            .createExpressionFromText(newArrayExpressionAsText, null);
        Optional<PsiExpression> assignedOrReturnedExpression = PsiUtils.getAssignedOrReturnedExpressionFromElement(psiElement);

        assignedOrReturnedExpression.ifPresent(expression -> expression.replace(emptyArrayExpression));
    }
}
