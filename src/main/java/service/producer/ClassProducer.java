package service.producer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import model.Bean;
import service.property.loader.PropertyLoader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Class is created to read all the properties
 * of the files that are stored classpath. On
 * the basis of the information received, the
 * class should be able to create any object.
 * After loading all the beans, the class checks
 * for the presence of a cyclic sequence
 * @author Gladush Ivan
 * @since 24.03.16.
 */
public class ClassProducer {
    private static final String EXCEPTION_BEANS_DOWNLOAD = "I can't download beans";
    private static final String EXCEPTION_CYCLE_IN_BEAN_FILE = "You have cycle in you beans file";
    private static final String EXCEPTION_BEAN_ATTRIBUTE = "Your bean doesn't correct!";
    private static final String EXCEPTION_CREATE_BEAN = "I can't create bean";
    private static final String EXCEPTION_BEAN_NOT_CONTAINS="I don't contain bean";
    /**
     * A list which stores all beans
     * */
    private static List<Bean> beans = new ArrayList<>();

    /**
     * list shows whether we have used the beans to
     * it or not. It is necessary that we have avoided
     * the dependency graph and beans when crawling ran
     * across the top of a used, this means that the bean
     * Drafting is not correct, as between the beans have
     * a circular dependency
     */
    private static List<String> used = new ArrayList<>();

    /**
     * when the class is loaded into memory, it
     * loads into memory all the beans, then each
     * user, upon request, a new class
     * */
    static {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            SaxHandler handler = new SaxHandler();
            parser.parse(PropertyLoader.getProperty("file.configuration.name"), handler);
            checkCycleInBeans();
        } catch (Exception e) {
            throw new IllegalStateException(EXCEPTION_BEANS_DOWNLOAD + e.getMessage());
        }
    }

    /**
     * Method check cycle in beans if
     * that method find cycle it throw new Illegal Argument Exception
     * */
    private static void checkCycleInBeans() {
        //bfs for bean
        Deque<Bean> usedBean = new ArrayDeque<>();
        for (Bean b : beans) {
            //todo I don't know how correct explained why we need do it
            used.clear();
            String nameBean = b.getName();
            if (!used.contains(nameBean)) {
                used.add(nameBean);
                usedBean.add(b);
                while (!usedBean.isEmpty()) {
                    checkParameters(usedBean);
                }
            }
        }
        //cleared resource
        used.clear();
    }

    /**
     * method checks all fields of beans
     * if the field is empty, he misses it,
     * if the field bean, then it checks
     * whether this bean is used even earlier,
     * if so then the file is not made correctly
     */
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


    private static Bean getBeanByName(String name) {
        for (Bean b : beans) {
            if (b.getName().equals(name)) {
                return b;
            }
        }
        throw new IllegalArgumentException(EXCEPTION_BEAN_NOT_CONTAINS);
    }

    public Object getInstance(String instanceName) {
        try {
            Bean bean = getBeanByName(instanceName);
            Object inst = initInstance(bean);
            initField(inst, bean);
            return inst;
        } catch (Exception e) {
            throw new IllegalArgumentException(EXCEPTION_CREATE_BEAN + e.getMessage());
        }
    }

    private void initField(Object inst, Bean bean) throws IllegalAccessException {
        for (Field f : inst.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (bean.isPrimaryField(f)) {
                f.set(inst, bean.getPrimaryValue(f));
            } else if (bean.isCompositeType(f)) {
                f.set(inst, getInstance(bean.getCompositeValue(f.getName())));
            }
        }
    }

    /**
     * Method return instance by Bean
     * */
    private Object initInstance(Bean b)
            throws Exception {
        return Class.forName(b.getClassPath()).newInstance();
    }


    /**
     * Class designed for parsing an XML file. During
     * the entrance of each new tag called a startElement
     * method which allows us to consider all of the attributes
     * of this tag
     *
     * */
    private static final class SaxHandler extends DefaultHandler {
        private static final String EXCEPTION_BEAN_IN_BEAN="Your bean file incorrect because bean located in bean";
        private Bean bean;
        private boolean readProperty = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            if (readProperty) {
                String value = attrs.getValue("value");
                if (value == null) {
                    bean.addCompositeName(attrs.getValue("name"));
                    bean.addCompositeValue(attrs.getValue("ref"));

                } else {
                    bean.addPrimitiveName(attrs.getValue("name"));
                    bean.addPrimitiveValue(value);
                }
            }
            if ("bean".equals(qName)) {
                //todo we do not need to check if the file is checked beforehand
                if (readProperty) {
                    throw new IllegalArgumentException(EXCEPTION_BEAN_IN_BEAN);
                }
                bean = initBean(attrs);
                readProperty = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if ("bean".equals(qName)) {
                readProperty = false;
                beans.add(bean);
            }
        }

        //todo we do not need to check if the file is checked beforehand
        private Bean initBean(Attributes attrs) {
            String id = attrs.getValue("id");
            String className = attrs.getValue("class");
            if (id == null || className == null) {
                throw new IllegalStateException(EXCEPTION_BEAN_ATTRIBUTE);
            }
            return new Bean(id, className);
        }
    }
}
