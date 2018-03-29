package cleancode;

import cleancode.nullreturn.NullReturnInspectionTool;
import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public class InspectionToolsProvider implements InspectionToolProvider {
    @NotNull
    public Class[] getInspectionClasses() {
        return new Class[]{NullReturnInspectionTool.class};
    }
}