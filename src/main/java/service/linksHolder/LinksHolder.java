package service.linksHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Class allow hold all that we can find from this page, and page
 * which we can jump from started page
 *
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class LinksHolder {
    private static final String EXCEPTION_EMPTY_LINKS_LIST = "You want link but I don't hve link";
    private ConcurrentLinkedDeque<String> linksForSearch = new ConcurrentLinkedDeque<>();
    private Set<String> metaLinks = new HashSet<>();

    public String nextLink() {
        if (linksForSearch.size() == 0) {
            throw new IllegalStateException(EXCEPTION_EMPTY_LINKS_LIST);
        }
        return linksForSearch.pop();
    }

    public void addLinkForSearch(List<String> links) {
        links.forEach(s -> linksForSearch.add(s));
    }

    public void addMetaLinks(List<String> list) {
        list.forEach(s -> metaLinks.add(s));
    }

    public String[] getMetaLinks() {
        String[] allList = new String[metaLinks.size()];
        int it = 0;
        for (String s : metaLinks) {
            allList[it++] = s;
        }
        return allList;
    }

    public boolean hasNext() {
        return !this.linksForSearch.isEmpty();
    }
}
