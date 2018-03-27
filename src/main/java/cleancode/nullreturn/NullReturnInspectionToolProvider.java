package cleancode.nullreturn;

import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public class NullReturnInspectionToolProvider implements InspectionToolProvider {
    @NotNull
    public Class[] getInspectionClasses() {
        return new Class[]{NullReturnInspectionTool.class};
    }
}