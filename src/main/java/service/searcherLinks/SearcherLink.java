package service.searcherLinks;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class create for search link in the text
 * by regular expression
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class SearcherLink {

    public static List<String> getLinks(String... strings) {
        List<String> answer = new ArrayList<>();
        Pattern pattern = Pattern.compile("((?<=href=\")(http:)*/*(\\w+(/|\\.))*(\\w*|%)*\\w*)(?=\")");

        for (String s : strings) {
            Matcher mat = pattern.matcher(s);
            while (mat.find()) {
                answer.add(mat.group(1));
            }
        }
        return answer;
    }
}
