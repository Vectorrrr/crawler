package service.linksHolder;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Class allow hold all that we can find from this page, and page
 * which we can jump from started page
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class LinksHolder {
    private static final String EXCEPTION_EMPTY_LINKS_LIST = "You want link but I don't hve link";
    private ConcurrentLinkedDeque<String> linksForSearch = new ConcurrentLinkedDeque<>();
    private Set<String> additionalLinks = new HashSet<>();

    public String getNextLink() {
        if (linksForSearch.size() == 0) {
            throw new IllegalStateException(EXCEPTION_EMPTY_LINKS_LIST);
        }
        return linksForSearch.pop();
    }

    public void addNewLinks(List<String> links) {
        links.forEach(s -> linksForSearch.add(s));
    }


    public void setAdditionalLinks(List<String> list) {
        list.forEach(s -> additionalLinks.add(s));
    }

    public String[] getAllAdditionalLinks(){
        String[] allList=new String[additionalLinks.size()];
        int it=0;
        for(String s:additionalLinks){
            allList[it++]=s;
        }
        return allList;
    }

    public int countProcessingLink() {
        return this.linksForSearch.size();
    }
}
