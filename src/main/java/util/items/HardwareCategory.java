package util.items;

import java.util.ArrayList;
import java.util.List;

public class HardwareCategory {

    String name;
    HardwareCategory parentCategory;
    List<HardwareCategory> childCategories = new ArrayList<>();
    List<HardwareElement> elements = new ArrayList<>();

    public HardwareCategory(String name, HardwareCategory parentCategory){
        this.parentCategory = parentCategory;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public HardwareCategory getParentCategory() {
        return parentCategory;
    }

    public void addElement(HardwareElement e){
        elements.add(e);
    }

    public List<HardwareElement> getElements(){
        return elements;
    }

    public void addChildCategory(HardwareCategory e){
        childCategories.add(e);
    }

    public List<HardwareCategory> getChildCategories() {
        return childCategories;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(name + "\n");
        for(HardwareCategory h: childCategories){
            output.append(h.toString());
        }
        for(HardwareElement e: elements){
            output.append("-")
                    .append(e.getName())
                    .append("\n");
        }

        return output.toString()+"\n";
    }
}
