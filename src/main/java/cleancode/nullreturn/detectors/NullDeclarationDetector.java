package cleancode.nullreturn.detectors;

import com.intellij.psi.PsiDeclarationStatement;

public class NullDeclarationDetector implements NullReturnDetector {

    private PsiDeclarationStatement statement;


    public NullDeclarationDetector(PsiDeclarationStatement statement) {
        this.statement = statement;
//        statement.getDeclaredElements()[0].getChildren()
    }


    @Override
    public boolean possiblyReturnsNull() {
        return false;
    }
}
