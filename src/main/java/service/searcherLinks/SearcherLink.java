package service.searcherLinks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class create for search link in the text
 * by regular expression
 *
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class SearcherLink {
    private static final String regex = "((?<=href=\")(((http[s]?://)|(/)).*?)(?=(\")|(#)|(&)))";
    private static final Pattern LINKS_PATTERN = Pattern.compile(regex);

    public static List<String> getLinks(String... strings) {
        List<String> answer = new ArrayList<>();

        for (String s : strings) {
            Matcher mat = LINKS_PATTERN.matcher(s);
            while (mat.find()) {
                answer.add(mat.group(1));
            }
        }
        return answer;
    }
}
