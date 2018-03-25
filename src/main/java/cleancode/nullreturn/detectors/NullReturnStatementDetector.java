package cleancode.nullreturn.detectors;

import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;

public class NullReturnStatementDetector implements NullReturnDetector {

    private PsiReturnStatement returnStatement;


    public NullReturnStatementDetector(PsiReturnStatement returnStatement) {
        this.returnStatement = returnStatement;
    }


    @Override
    public boolean possiblyReturnsNull() {
        boolean returnsNullLiteral = returnsNullLiteral();
//        if (returnsNullLiteral) {
            return returnsNullLiteral;
//        } else {
//            PsiMethod surroundingMethod = findSurroundingMethod();
//            String returnedVariableName = returnStatement.getReturnValue().getText();
//            List<PsiExpressionStatement> expressions = findAllExpressionsWhereVariableIsUsedInsideMethod(returnedVariableName, surroundingMethod);
//            return isThereAnAssignmentToNull(expressions);
//        }
    }


    private boolean returnsNullLiteral() {
        PsiType returnValueType = returnStatement.getReturnValue().getType();
        return PsiType.NULL.equals(returnValueType);
    }


//    private PsiMethod findSurroundingMethod() {
//        PsiMethod surroundingMethod = PsiTreeUtil.getParentOfType(returnStatement, PsiMethod.class);
//        return surroundingMethod;
//    }

//    private List<PsiExpressionStatement> findAllExpressionsWhereVariableIsUsedInsideMethod(String variableName, PsiMethod method) {
//        Collection<PsiExpressionStatement> childrenOfTypeExpression = PsiTreeUtil.findChildrenOfType(method, PsiExpressionStatement.class);
//        return childrenOfTypeExpression.stream()
//                .filter(childOfTypeExpression -> {
//                    PsiAssignmentExpression assignment = (PsiAssignmentExpression) childOfTypeExpression.getChildren()[0];
//                    PsiExpression lExpression = assignment.getLExpression();
//                    String referenceName = ((PsiReferenceExpressionImpl) lExpression).getReferenceName();
//                    return variableName.equals(referenceName);
//                })
//                .collect(Collectors.toList());
//    }


//    private boolean isThereAnAssignmentToNull(List<PsiExpressionStatement> expressions) {
//        return expressions.stream()
//                .anyMatch(expression -> {
//                    return ((PsiAssignmentExpression) expression.getChildren()[0]).getRExpression().getType().equals(PsiType.NULL);
//                });
//    }
}
