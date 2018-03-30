package cleancode.nullreturn.quickfixes.implementations.collections;

import cleancode.utils.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class EmptyCollectionQuickFix implements LocalQuickFix {

    @Nls
    @NotNull
    @Override
    public abstract String getName();


    public abstract PsiElement getEmptyCollection(Project project);


    @Nls
    @NotNull
    @Override
    public final String getFamilyName() {
        return getName();
    }


    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        Optional<PsiExpression> assignedOrReturnedExpression = PsiUtils.getAssignedOrReturnedExpressionFromElement(problemDescriptor.getPsiElement());
        PsiElement emptyCollection = getEmptyCollection(project);

        assignedOrReturnedExpression.ifPresent(expression -> {
            PsiElement emptyCollectionWithImport = PsiUtils.replaceFullyQualifiedNameWithImport(emptyCollection, project);
            expression.replace(emptyCollectionWithImport);
        });
    }
}
