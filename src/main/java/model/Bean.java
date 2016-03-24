package model;

import java.lang.reflect.Field;
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
    private static final String EXCEPTION_CREATE_PRIMITIVE_TYPE = "I don't support this type";
    private List<String> primitiveNames = new ArrayList<>();
    private List<String> primitiveValues = new ArrayList<>();
    private List<String> compositeNames = new ArrayList<>();
    private List<String> compositeValues = new ArrayList<>();
    private String name;
    private String classPath;

    public Bean(String name, String classPath) {
        this.name = name;
        this.classPath = classPath;
    }

    public List<String> getCompositeValues() {
        return Collections.unmodifiableList(compositeValues);
    }

    public void addPrimitiveName(String name) { primitiveNames.add(name); }

    public void addCompositeName(String name) {
        compositeNames.add(name);
    }

    public void addCompositeValue(String value) {
        compositeValues.add(value);
    }

    public String getClassPath() {
        return classPath;
    }

    public String getName() {
        return name;
    }

    public boolean isPrimaryField(Field f) {
        return primitiveNames.contains(f.getName());
    }

    public Object getPrimaryValue(Field f) {
        String value = primitiveValues.get(primitiveNames.indexOf(f.getName()));
        String type = f.getType().getSimpleName();
        for (Type t : Type.values()) {
            if (t.getSing().equals(type)) {
                return t.getExample(value);
            }
        }
        throw new IllegalArgumentException(EXCEPTION_CREATE_PRIMITIVE_TYPE);
    }

    public boolean isCompositeType(Field f) {
        return compositeNames.contains(f.getName());
    }

    public void addPrimitiveValue(String value) {
        primitiveValues.add(value);
    }

    public String getCompositeValue(String name) {
        return compositeValues.get(compositeNames.indexOf(name));
    }
}
