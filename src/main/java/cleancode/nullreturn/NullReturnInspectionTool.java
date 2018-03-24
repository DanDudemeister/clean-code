package cleancode.nullreturn;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class NullReturnInspectionTool extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public String getDisplayName() {
        return "Never return null!";
    }


    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }


    @NotNull
    @Override
    public String getShortName() {
        return "NullReturn";
    }


    @Override
    public boolean isEnabledByDefault() {
        return true;
    }


    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new NullReturnInspection(holder);
    }
}
