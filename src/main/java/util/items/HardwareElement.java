package util.items;

public class HardwareElement {

    HardwareCategory parentCategory;
    String name;
    int price;

    public HardwareElement(HardwareCategory parentCategory, String name, int price) {
        this.parentCategory = parentCategory;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public HardwareCategory getParentCategory() {
        return parentCategory;
    }

    public static HardwareElement getItemByName(String name){
        for(HardwareCategory h: Shop.hardwareCategories){
            for (HardwareElement e : h.getElements()){
                if(e.getName().equals(name)){
                    return e;
                }
            }
        }
        return null;
    }
}
