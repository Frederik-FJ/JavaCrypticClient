package util.items;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;

import java.util.*;

public class Shop {

    public static Map getShopItems(){
        try {
            return Information.webSocketClient.microservice("inventory", Arrays.asList("shop", "list"), new HashMap<>());
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HardwareCategory rootCategory;
    public static List<HardwareCategory> hardwareCategories = new ArrayList<>();
    public static List<HardwareElement> hardwareElements = new ArrayList<>();

    public static HardwareCategory reloadCategories() throws InvalidServerResponseException {
        Map categoryMap = Shop.getShopItems();
        try {
            HardwareCategory rootCategory = new HardwareCategory("Root", null);
            for (String s : (Set<String>) categoryMap.keySet()) {
                for (HardwareCategory h: getCategories(categoryMap, rootCategory)) {
                    rootCategory.addChildCategory(h);
                }
            }
            return rootCategory;
        } catch (ClassCastException e) {
            throw new InvalidServerResponseException(categoryMap);
        }
    }

    private static List<HardwareCategory> getCategories(Map categoryMap, HardwareCategory parentCategory) throws ClassCastException{
        List<HardwareCategory> hardwareCategories = new ArrayList<>();
        categoryMap = (Map) categoryMap.get("categories");
        for (String s : (Set<String>) categoryMap.keySet()) {
            HardwareCategory hardwareCategory = new HardwareCategory(s, parentCategory);
            for (HardwareCategory childCategory : getCategories((Map) categoryMap.get(s), parentCategory)) {
                hardwareCategory.addChildCategory(childCategory);
            }
            hardwareCategories.add(hardwareCategory);

            for (HardwareElement item: getItems((Map) categoryMap.get(s), hardwareCategory)) {
                hardwareCategory.addElement(item);
            }
        }
        Shop.hardwareCategories.addAll(hardwareCategories);
        return hardwareCategories;
    }

    private static List<HardwareElement> getItems(Map m, HardwareCategory parentCategory) throws ClassCastException {
        List<HardwareElement> hardwareElements = new ArrayList<>();
        Map itemMap = (Map) m.get("items");
        for (String s: (Set<String>) itemMap.keySet()) {
            hardwareElements.add(new HardwareElement(parentCategory,
                    s,
                    (int) ((double) ((Map) itemMap.get(s)).get("price"))));
        }
        Shop.hardwareElements.addAll(hardwareElements);
        return hardwareElements;
    }
}
