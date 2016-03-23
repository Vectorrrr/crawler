import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * @author Gladush Ivan
 * @since 23.03.16.
 */
public class ProducerInstance  {
    private static final String EXCEPTION_BEANS_DOWNLOAD="I can't download beans";
    private static final String EXCEPTION_CYCLE_IN_BEAN_FILE ="You have cycle in you beans file";
    private static List<Bean> beans=new ArrayList<>();
    /**
     * list shows whether we have used the beans to
     * it or not. It is necessary that we have avoided
     * the dependency graph and beans when crawling ran
     * across the top of a used, this means that the bean
     * Drafting is not correct, as between the beans have
     * a circular dependency
     * */
    private static List<String> used=new ArrayList<>();
    private static Object instance;
    private Map<String,Object> map=new HashMap<>();
    /**
     * when the class is loaded into memory, it
     * loads into memory all the beans, then each
     * user, upon request, a new class
     * */
    //todo make file name property
    static{
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            SaxHandler handler = new SaxHandler();
            parser.parse("settings.xml", handler);
            checkCorrectsBeans();
        } catch (Exception e) {
            throw new IllegalStateException(EXCEPTION_BEANS_DOWNLOAD+ e.getMessage());
        }
    }

    private static void checkCorrectsBeans() {
        //bfs for bean
        for (Bean b : beans) {
            //todo I don't know how correct explained why we need do it
            Deque<Bean> usedBean = new ArrayDeque<>();
            String nameBean = b.getName();
            if (!used.contains(nameBean)) {
                used.add(nameBean);
                usedBean.add(b);
                while (!usedBean.isEmpty()) {
                    checkParameters(usedBean);
                }
            }
        }
    }
    /**
     * method checks all fields of beans
     * if the field is empty, he misses it,
     * if the field bean, then it checks
     * whether this bean is used even earlier,
     * if so then the file is not made correctly
     * */
    //todo that good separate logic
    private static void checkParameters(Deque<Bean> usedBean) {
        for (String name : usedBean.pop().getCompositeValues()) {
            if (used.contains(name)) {
                throw new IllegalArgumentException(EXCEPTION_CYCLE_IN_BEAN_FILE);
            } else {
                    usedBean.add(getBeanByName(name));
                    used.add(name);

            }
        }
    }


    private static Bean getBeanByName(String name){
        for(Bean b:beans){
            if(b.getName().equals(name)){
                return b;
            }
        }
        throw new IllegalArgumentException("I don't contain bean");
    }

    public Object getInstance(String instanseName) throws Exception {

        return null ;
    }


    /**
     * When bean are thrown in bean throw new Exception
     * */
    private static final class SaxHandler extends DefaultHandler {
        private static int id=0;
        private Bean bean;
        private boolean readProperty=false;
        // we enter to element 'qName':
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            if ("bean".equals(qName)) {
                bean = new Bean(attrs.getValue("id"));
                if (readProperty) {
                    throw new IllegalArgumentException("Your bean file incorrect because bean located in bean");
                }
                readProperty = true;
                try {
                    instance = Class.forName(attrs.getValue("class")).newInstance();
                    bean.setInstance(instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if (readProperty) {
                String value = attrs.getValue("value");
                if (value == null) {

                    bean.addCompositeName( attrs.getValue("name"));
                    bean.addCompositeValue( attrs.getValue("ref"));

                } else {
                    bean.addPrimitiveName(attrs.getValue("name"));
                    bean.addCompositeName(value);
                }
            }
        }
        @Override
        public void endElement(String uri, String localName, String qName){
            if("bean".equals(qName)){
                readProperty=false;
                beans.add(bean);
            }
        }

    }
}
