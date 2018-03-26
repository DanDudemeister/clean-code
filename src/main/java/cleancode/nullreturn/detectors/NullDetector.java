package cleancode.nullreturn.detectors;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public interface NullDetector {

    boolean possiblyReturnsNull();

    default LocalQuickFix getFix() {
        //TODO only temporal; will be replaced with concrete implementations later
        return new LocalQuickFix() {
            @Nls
            @NotNull
            @Override
            public String getFamilyName() {
                return "family name";
            }

            @Override
            public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {

            }
        };
    };
}
