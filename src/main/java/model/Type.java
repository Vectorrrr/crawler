package model;

/**
 * @author Gladush Ivan
 * @since 23.03.16.
 */
public enum Type {
    INTEGER("Integer") {
        @Override
        public Object getExample(String s) {
            return Integer.valueOf(s);
        }
    },
    STRING("String") {
        @Override
        public Object getExample(String s) {
            return s;
        }
    };
    private String sing;
    Type(String sing){
        this.sing=sing;
    }
    public abstract Object getExample(String s);

    public String getSing() {
        return sing;
    }
}
