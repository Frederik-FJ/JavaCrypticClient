package items;

public class HardwareElement {

    String type;
    String name;

    public HardwareElement(String type, String name){
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
