package cleancode.nullreturn.quickfixes.implementations.collections;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class EmptyListQuickFix extends EmptyCollectionQuickFix {

    private static final String EMPTY_LIST_TEXT = "java.util.Collections.emptyList()";


    @Nls
    @NotNull
    @Override
    public String getName() {
        return "Replace null with an empty list";
    }


    @Override
    public PsiElement getEmptyCollection(Project project) {
        return getEmptyListExpression(project);
    }


    @NotNull
    private PsiExpression getEmptyListExpression(Project project) {
        return PsiElementFactory.SERVICE.getInstance(project)
            .createExpressionFromText(EMPTY_LIST_TEXT, null);
    }
}
