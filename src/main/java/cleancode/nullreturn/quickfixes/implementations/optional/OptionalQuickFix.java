package cleancode.nullreturn.quickfixes.implementations.optional;

import cleancode.utils.PsiUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory.SERVICE;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiMethodReferenceExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.LambdaRefactoringUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class OptionalQuickFix implements LocalQuickFix {

    private static final String QUICK_FIX_NAME = "Replace null with optional";
    private static final String PLACEHOLDER = "{0}";
    private static final String JAVA_UTIL_OPTIONAL_FULL_QUALIFIED_REGEX = "^(java.util.Optional<).*(>)$";
    private static final String JAVA_UTIL_OPTIONAL_SHORTENED_REGEX = "^(Optional<).*(>)$";
    private static final String OF_NULLABLE_TEMPLATE = "java.util.Optional.ofNullable(" + PLACEHOLDER + ")";
    private static final String OPTIONAL_TEMPLATE = "Optional<" + PLACEHOLDER + ">";
    private static final String OR_ELSE_NULL = ".orElse(null)";


    @Nls
    @NotNull
    @Override
    public String getName() {
        return QUICK_FIX_NAME;
    }


    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }


    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
        PsiMethod surroundingMethod = PsiUtils.findSurroundingMethod(problemDescriptor.getPsiElement());

        Collection<PsiReturnStatement> allReturnStatements = findAllReturnStatementsOfMethod(surroundingMethod);

        List<PsiExpression> assignedOrReturnedValues = getAssignedOrReturnedValues(allReturnStatements);
        replaceAllReturnedValuesWithOptionalIfNecessary(project, assignedOrReturnedValues);

        Collection<PsiReference> usagesOfMethod = PsiUtils.findUsagesOfMethod(surroundingMethod);
        List<PsiMethodReferenceExpression> java8MethodReferences = findJava8MethodReferences(usagesOfMethod);
        convertMethodReferencesToLambda(java8MethodReferences);

        usagesOfMethod = PsiUtils.findUsagesOfMethod(surroundingMethod);
        adaptUsagesIfNecessary(project, usagesOfMethod);

        // add "generated" TODOs
        adaptReturnTypeIfNecessary(project, surroundingMethod);
    }


    private Collection<PsiReturnStatement> findAllReturnStatementsOfMethod(PsiMethod method) {
        return PsiTreeUtil.findChildrenOfType(method, PsiReturnStatement.class);
    }


    private List<PsiExpression> getAssignedOrReturnedValues(Collection<PsiReturnStatement> allReturnStatements) {
        return allReturnStatements.stream()
            .map(PsiUtils::getAssignedOrReturnedExpressionFromElement)
            .map(optionalPsiExpression -> optionalPsiExpression.orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }


    private void convertMethodReferencesToLambda(List<PsiMethodReferenceExpression> java8MethodReferences) {
        java8MethodReferences.forEach(psiReference -> LambdaRefactoringUtil.convertMethodReferenceToLambda(psiReference, true, true));
    }


    //TODO Optional.empty(), if null! (instead of Optional.ofNullable(null))
    private void replaceAllReturnedValuesWithOptionalIfNecessary(@NotNull Project project, List<PsiExpression> assignedOrReturnedValues) {
        assignedOrReturnedValues.forEach(returnedValue -> {
            if (returnedValue.getType() != null && !returnedValue.getType().getCanonicalText().matches(JAVA_UTIL_OPTIONAL_FULL_QUALIFIED_REGEX)) {
                String returnedValueAsText = returnedValue.getText();
                returnedValueAsText = OF_NULLABLE_TEMPLATE.replace(PLACEHOLDER, returnedValueAsText);
                PsiExpression newReturnedValue = SERVICE.getInstance(project).createExpressionFromText(returnedValueAsText, null);
                PsiElement newReturnedValueWithImport = PsiUtils.replaceFullyQualifiedNameWithImport(newReturnedValue, project);
                returnedValue.replace(newReturnedValueWithImport);
            }
        });
    }


    private List<PsiMethodReferenceExpression> findJava8MethodReferences(Collection<PsiReference> usagesOfMethod) {
        return usagesOfMethod.stream()
            .filter(usage -> usage instanceof PsiMethodReferenceExpression)
            .map(usage -> (PsiMethodReferenceExpression) usage)
            .collect(Collectors.toList());
    }


    private void adaptUsagesIfNecessary(@NotNull Project project, Collection<PsiReference> usagesOfMethod) {
        usagesOfMethod.forEach(psiReference -> {
            PsiElement parentElementWhichContainsMethodCallChain = getParentElementWhichContainsMethodCallChain(psiReference);
            String currentlyCalledMethodName = getNameOfCurrentlyCalledMethod(psiReference.getElement(), project);

            boolean adaptUsage = isAdaptionOfUsageNecessary(psiReference, currentlyCalledMethodName);

            if (adaptUsage) {
                replaceUsage(project, parentElementWhichContainsMethodCallChain);
            }
        });
    }


    private PsiElement getParentElementWhichContainsMethodCallChain(PsiReference psiReference) {
        PsiElement parent = PsiTreeUtil.findFirstParent(
            psiReference.getElement(),
            psiElement -> psiElement instanceof PsiMethodCallExpression
        );
        PsiElement grandParent = parent.getParent();

        return grandParent instanceof PsiAssignmentExpression ? grandParent : parent;
    }


    private String getNameOfCurrentlyCalledMethod(PsiElement psiElement, Project project) {
        String currentlyCalledMethodName = "";

        PsiElement currentElement = PsiTreeUtil.nextLeaf(psiElement);

        while (!")".equals(currentElement.getText())) {
            currentElement = PsiTreeUtil.nextLeaf(currentElement);
        }

        PsiElement closingBrackets = currentElement;
        PsiElement elementAfterClosingBrackets = PsiTreeUtil.nextLeaf(closingBrackets);

        while (elementAfterClosingBrackets instanceof PsiWhiteSpace) {
            elementAfterClosingBrackets = PsiTreeUtil.nextLeaf(elementAfterClosingBrackets);
        }

        if (".".equals(elementAfterClosingBrackets.getText())) {
            //TODO Doesn't work if there's a new line after the dot (a. \n orElse())
            currentlyCalledMethodName = PsiTreeUtil.nextLeaf(PsiTreeUtil.nextLeaf(elementAfterClosingBrackets)).getText();
        }

        return currentlyCalledMethodName;
    }


    //TODO: this logic doesn't work for all cases:
    // actually it needs to be determined what the place where the method is
    // called expects the method's return type to be
    // which is really not that trivial especially in Java 8 streams
    private boolean isAdaptionOfUsageNecessary(PsiReference psiReference, String currentlyCalledMethodName) {
        boolean adaptUsage;
        boolean onRightSideOfAnAssignment = isOnRightSideOfAnAssignment(psiReference.getElement());

        if (onRightSideOfAnAssignment) {
            adaptUsage = !isAlreadyAssignedAsOptional(psiReference) && !doesMethodNameMatchAnyOfOptionalClassMethodNames(currentlyCalledMethodName);
        } else {
            adaptUsage = !doesMethodNameMatchAnyOfOptionalClassMethodNames(currentlyCalledMethodName);
        }

        return adaptUsage;
    }


    private boolean isOnRightSideOfAnAssignment(PsiElement element) {
        PsiAssignmentExpression assignment = null;
        PsiLocalVariable localVariable = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class, true, PsiCodeBlock.class);

        if (localVariable == null) {
            assignment = PsiTreeUtil.getParentOfType(element, PsiAssignmentExpression.class, true, PsiCodeBlock.class);
        }

        return localVariable != null || assignment != null;
    }


    private boolean isAlreadyAssignedAsOptional(PsiReference psiReference) {
        PsiType type = getTypeOfAssignedVariableFromElementRecursively(((PsiReferenceExpression) psiReference).getParent());
        return type.getCanonicalText().matches(JAVA_UTIL_OPTIONAL_FULL_QUALIFIED_REGEX);
    }


    private PsiType getTypeOfAssignedVariableFromElementRecursively(PsiElement psiElement) {
        if (psiElement instanceof PsiLocalVariable) {
            return ((PsiLocalVariable) psiElement).getType();

        } else if (psiElement instanceof PsiField) {
            return ((PsiField) psiElement).getType();

        } else if (psiElement instanceof PsiAssignmentExpression) {
            return ((PsiAssignmentExpression) psiElement).getLExpression().getType();

        } else {
            return getTypeOfAssignedVariableFromElementRecursively(psiElement.getParent());
        }
    }


    private boolean doesMethodNameMatchAnyOfOptionalClassMethodNames(String methodName) {
        List<String> allPublicMethodNamesOfOptionalClass = getAllPublicMethodNamesOfOptionalClass();
        return allPublicMethodNamesOfOptionalClass.stream()
            .anyMatch(optionalMethodName -> optionalMethodName.equals(methodName));
    }


    private List<String> getAllPublicMethodNamesOfOptionalClass() {
        Method[] allMethods = Optional.class.getDeclaredMethods();
        return Arrays.stream(allMethods)
            .filter(method -> Modifier.isPublic(method.getModifiers()))
            .map(Method::getName)
            .collect(Collectors.toList());
    }


    private void replaceUsage(@NotNull Project project, PsiElement parentElementWhichContainsMethodCallChain) {
        String methodCallAsText = parentElementWhichContainsMethodCallChain.getText();
        methodCallAsText = methodCallAsText + OR_ELSE_NULL;
        PsiExpression newReturnedValue = SERVICE.getInstance(project).createExpressionFromText(methodCallAsText, null);
        PsiElement newMethodCall = PsiUtils.replaceFullyQualifiedNameWithImport(newReturnedValue, project);
        parentElementWhichContainsMethodCallChain.replace(newMethodCall);
    }


    private void adaptReturnTypeIfNecessary(@NotNull Project project, PsiMethod surroundingMethod) {
        String oldTypeAsText = surroundingMethod.getReturnTypeElement().getType().getPresentableText();
        if (!oldTypeAsText.matches(JAVA_UTIL_OPTIONAL_SHORTENED_REGEX)) {
            String newTypeAsText = OPTIONAL_TEMPLATE.replace(PLACEHOLDER, oldTypeAsText);
            PsiType newPsiType = SERVICE.getInstance(project).createTypeByFQClassName(newTypeAsText);
            PsiElement newReturnType = SERVICE.getInstance(project).createTypeElement(newPsiType);
            surroundingMethod.getReturnTypeElement().replace(newReturnType);
        }
    }
}
