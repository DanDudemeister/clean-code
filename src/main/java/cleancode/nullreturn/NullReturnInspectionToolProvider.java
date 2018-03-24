package cleancode.nullreturn;

import com.intellij.codeInspection.InspectionToolProvider;

public class NullReturnInspectionToolProvider implements InspectionToolProvider {
    public Class[] getInspectionClasses() {
        return new Class[]{NullReturnInspectionTool.class};
    }
}