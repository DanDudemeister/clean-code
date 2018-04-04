package cleancode.nullreturn.quickfixes.implementations.optional;

import cleancode.utils.PsiUtils;
import cleancode.utils.StringUtils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory.SERVICE;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiMethodReferenceExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.LambdaRefactoringUtil;
import com.siyeh.ig.psiutils.ExpectedTypeUtils;
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
    private static final String JAVA_UTIL_OPTIONAL_FULL_QUALIFIED_REGEX = "^(java.util.Optional<).*(>)$";
    private static final String JAVA_UTIL_OPTIONAL_SHORTENED_REGEX = "^(Optional<).*(>)$";
    private static final String OF_NULLABLE_TEMPLATE = "java.util.Optional.ofNullable(" + StringUtils.PLACEHOLDER + ")";
    private static final String OPTIONAL_TEMPLATE = "Optional<" + StringUtils.PLACEHOLDER + ">";
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
        //TODO only expand method-reference if an adaption is necessary
        convertMethodReferencesToLambda(java8MethodReferences);

        usagesOfMethod = PsiUtils.findUsagesOfMethod(surroundingMethod);
        adaptUsagesIfNecessary(project, usagesOfMethod);

        //TODO: add "generated" TODOs
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
                returnedValueAsText = StringUtils.insertStringIntoTemplate(returnedValueAsText, OF_NULLABLE_TEMPLATE);
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
            String currentlyCalledMethodName = getNameOfCurrentlyCalledMethod(psiReference.getElement());
            boolean adaptUsage = isAdaptionOfUsageNecessary(psiReference, currentlyCalledMethodName, project);

            if (adaptUsage) {
                PsiElement parentElementWhichContainsMethodCallChain = getParentElementWhichContainsMethodCallChain(psiReference);
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


    private String getNameOfCurrentlyCalledMethod(PsiElement psiElement) {
        String currentlyCalledMethodName = "";
        PsiElement enclosingMethodCallExpression = psiElement.getParent().getParent().getParent();

        if (enclosingMethodCallExpression instanceof PsiMethodCallExpression) {
            currentlyCalledMethodName = extractMethodNameFromMethodCallExpression((PsiMethodCallExpression) enclosingMethodCallExpression);
        }

        return currentlyCalledMethodName;
    }


    private String extractMethodNameFromMethodCallExpression(PsiMethodCallExpression enclosingMethodCallExpression) {
        String methodName = "";

        PsiElement[] children = enclosingMethodCallExpression.getMethodExpression().getChildren();

        Optional<PsiElement> first = Arrays.stream(children)
            .filter(child -> child instanceof PsiIdentifier)
            .findFirst();

        if (first.isPresent()) {
            methodName = first.get().getText();
        }

        return methodName;
    }


    private boolean isAdaptionOfUsageNecessary(PsiReference psiReference, String currentlyCalledMethodName, Project project) {
        PsiElement methodCall = ((PsiReferenceExpressionImpl) psiReference).getParent();
        PsiType expectedType = ExpectedTypeUtils.findExpectedType((PsiExpression) methodCall, true);

        return isExpectedTypeNotOptionalAndDoesMethodNameNotMatchAnyOfOptionalClassMethodNames(expectedType, currentlyCalledMethodName);
    }


    private boolean isExpectedTypeNotOptionalAndDoesMethodNameNotMatchAnyOfOptionalClassMethodNames(PsiType expectedType, String currentlyCalledMethodName) {
        boolean isExpectedTypeAlreadyOptional = false;

        if (expectedType != null) {
            isExpectedTypeAlreadyOptional = expectedType.getCanonicalText().matches(JAVA_UTIL_OPTIONAL_FULL_QUALIFIED_REGEX);
        }

        return !isExpectedTypeAlreadyOptional && !doesMethodNameMatchAnyOfOptionalClassMethodNames(currentlyCalledMethodName);
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
            String newTypeAsText = StringUtils.insertStringIntoTemplate(oldTypeAsText, OPTIONAL_TEMPLATE);
            PsiType newPsiType = SERVICE.getInstance(project).createTypeByFQClassName(newTypeAsText);
            PsiElement newReturnType = SERVICE.getInstance(project).createTypeElement(newPsiType);
            surroundingMethod.getReturnTypeElement().replace(newReturnType);
        }
    }
}
