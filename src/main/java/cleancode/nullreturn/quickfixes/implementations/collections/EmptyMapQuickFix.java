package cleancode.nullreturn.quickfixes.implementations.collections;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class EmptyMapQuickFix extends EmptyCollectionQuickFix {

    private static final String EMPTY_MAP_TEXT = "java.util.Collections.emptyMap()";


    @Nls
    @NotNull
    @Override
    public String getName() {
        return "Replace null with an empty map";
    }


    @Override
    public PsiElement getEmptyCollection(Project project) {
        return getEmptyMapExpression(project);
    }


    @NotNull
    private PsiExpression getEmptyMapExpression(Project project) {
        return PsiElementFactory.SERVICE.getInstance(project)
            .createExpressionFromText(EMPTY_MAP_TEXT, null);
    }
}
