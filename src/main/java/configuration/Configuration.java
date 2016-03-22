package configuration;

import service.producer.URL.Producer;
import service.property.loader.SystemSettingsLoader;
import service.save.Storage;

import javax.xml.bind.annotation.*;

/**
 * class contains information about the program settings,
 * and allows you to receive classes for storing and retrieving data
 * @author Gladush Ivan
 * @since 21.03.16.
 */
@XmlRootElement(name= "configuration")
@XmlType(name = "configuration", propOrder = {
        "storage",
        "urlGetter",
})
public class Configuration {
    private static final String EXCEPTION_CLASS_CREATE_EXAMPLE = "I can't create class";
    private static final String STORAGE_DIRECTORY= SystemSettingsLoader.getStorageDirectory();
    private static final String PRODUCER_DIRECTORY=SystemSettingsLoader.getProducerDirectory();
    private String storage;
    private String urlGetter;

    /**
     * method returns an object to storage
     * */
    public Storage getNewStorage() {
        try {
            Class c = Class.forName(STORAGE_DIRECTORY + storage);
            return (Storage) c.newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException(EXCEPTION_CLASS_CREATE_EXAMPLE + " " + e.getMessage());
        }
    }

    public String getStorage() {
        return storage;
    }

    @XmlElement(required = true)
    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getUrlGetter() {
        return urlGetter;
    }

    @XmlElement(required = true)
    public void setUrlGetter(String urlGetter) {
        this.urlGetter = urlGetter;
    }

    @Override
    public String toString() {
        return storage + "  " + urlGetter;
    }

    /**
     *  method returns an object through which you
     *  can get the URL of a particular website
     * */
    public Producer getProvider() {
        try {
            return (Producer) Class.forName(PRODUCER_DIRECTORY + urlGetter).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException(EXCEPTION_CLASS_CREATE_EXAMPLE + " " + e.getMessage());
        }
    }
}
