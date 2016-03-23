/**
 * @author Gladush Ivan
 * @since 23.03.16.
 */
public class A {
    private B b;
    public void setB(B b){
        this.b=b;
    }
    @Override
    public String toString(){
        return b.toString()+"SFAFS";
    }
}
