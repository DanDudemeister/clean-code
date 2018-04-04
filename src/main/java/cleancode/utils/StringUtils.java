package cleancode.utils;

public class StringUtils {

    public static final String PLACEHOLDER = "{0}";

    public static String insertStringIntoTemplate(String insert, String template) {
        return template.replace(PLACEHOLDER, insert);
    }
}
