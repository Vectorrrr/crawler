package service.producer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import model.Bean;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

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
    private static final String EXCEPTION_BEAN_ATTRIBUTE = "Your bean doesn't correct!";
    private static final String EXCEPTION_CREATE_BEAN = "I can't create bean";
    private static final String EXCEPTION_BEAN_NOT_CONTAINS = "I don't contain bean";
    private Map<String, Object> initObject = new HashMap<>();

    /**
     * A list which stores all beans
     */
    private List<Bean> beans = new ArrayList<>();


    /**
     * when the class is loaded into memory, it
     * loads into memory all the beans, then each
     * user, upon request, a new class
     */
    private ClassProducer(File f)
            throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        SaxHandler handler = new SaxHandler();
        parser.parse(f, handler);

    }


    public static ClassProducer initClassProducer(File file) {
        try {
            return new ClassProducer(file);
        } catch (Exception e) {
            throw new IllegalStateException(EXCEPTION_BEANS_DOWNLOAD + e.getMessage());
        }
    }

    public synchronized Object getInstance(String instanceName) {
        try {
            Bean bean = getBeanByName(instanceName);
            Object inst = initInstance(bean);
            initObject.put(instanceName, inst);
            initField(inst, bean);
            return inst;
        } catch (Exception e) {
            throw new IllegalArgumentException(EXCEPTION_CREATE_BEAN + e.getMessage());
        }
    }

    private Bean getBeanByName(String name) {
        for (Bean b : beans) {
            if (b.getName().equals(name)) {
                return b;
            }
        }
        throw new IllegalArgumentException(EXCEPTION_BEAN_NOT_CONTAINS);
    }


    private void initField(Object inst, Bean bean) throws IllegalAccessException {
        for (Field f : inst.getClass().getDeclaredFields()) {
            String name = f.getName();
            f.setAccessible(true);
            if (bean.isPrimaryField(f)) {
                f.set(inst, bean.getPrimaryValue(f));
            } else if (bean.isCompositeType(f)) {
                Object o = initObject.get(name);
                if (o != null) {
                    initObject.remove(name);
                    f.set(inst, o);
                } else {
                    f.set(inst, getInstance(bean.getCompositeValue(name)));
                }
            }
            //todo why I can't write f.set(inst, initObject.getOrDefault(name,bean.getCompositeValue(name)))
        }
    }

    /**
     * Method return instance by Bean
     */
    private Object initInstance(Bean b)
            throws Exception {
        return Class.forName(b.getClassPath()).newInstance();
    }


    /**
     * Class designed for parsing an XML file. During
     * the entrance of each new tag called a startElement
     * method which allows us to consider all of the attributes
     * of this tag
     */
    private final class SaxHandler extends DefaultHandler {
        private static final String EXCEPTION_BEAN_IN_BEAN = "Your bean file incorrect because bean located in bean";
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
