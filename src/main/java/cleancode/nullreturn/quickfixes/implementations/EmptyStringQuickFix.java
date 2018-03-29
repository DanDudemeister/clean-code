package cleancode.nullreturn.quickfixes.implementations;

import cleancode.nullreturn.utils.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EmptyStringQuickFix implements LocalQuickFix {

    private static final String EMPTY_STRING_TEXT = "\"\"";

    @Nls
    @NotNull
    @Override
    public String getName() {
        return "Replace null with empty string";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        PsiExpression emptyStringExpression = PsiElementFactory.SERVICE.getInstance(project).createExpressionFromText(EMPTY_STRING_TEXT, null);
        Optional<PsiExpression> assignedOrReturnedExpression = PsiUtils.getAssignedOrReturnedExpressionFromElement(problemDescriptor.getPsiElement());
        assignedOrReturnedExpression.ifPresent(expression -> expression.replace(emptyStringExpression));
    }
}
