package cleancode.nullreturn.quickfixes.implementations.collections;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class EmptySetQuickFix extends EmptyCollectionQuickFix {

    private static final String EMPTY_SET_TEXT = "java.util.Collections.emptySet()";


    @Nls
    @NotNull
    @Override
    public String getName() {
        return "Replace null with an empty set";
    }


    @Override
    public PsiElement getEmptyCollection(Project project) {
        return getEmptySetExpression(project);
    }


    @NotNull
    private PsiExpression getEmptySetExpression(Project project) {
        return PsiElementFactory.SERVICE.getInstance(project).createExpressionFromText(
            EMPTY_SET_TEXT, null);
    }
}
