import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//todo rewrite commint
/**
 *class contains instance and names of all the fields and their values in the two lists
 * name and number value in the list equals
 * @author Gladush Ivan
 * @since 23.03.16.
 */
public class Bean {
    private Object instance;
    private String name;
    private List<String> primitiveNames = new ArrayList<>();
    private List<String> primitiveValues = new ArrayList<>();
    private List<String> compositeNames = new ArrayList<>();
    private List<String> compositeValues = new ArrayList<>();

    public Bean(String name) {
        this.name = name;
    }

    public List<String> getCompositeValues(){
        return Collections.unmodifiableList(compositeValues);
    }

   public void addPrimitiveName(String name){
       primitiveNames.add(name);
   }
    public void addCompositeName(String name){
        compositeNames.add(name);
    }
    public void addPrimitiveValue(String value){
        primitiveValues.add(value);
    }
    public void addCompositeValue(String value){
        compositeValues.add(value);
    }
    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public String getName() {
        return name;
    }
}
