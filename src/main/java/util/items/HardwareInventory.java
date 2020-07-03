package util.items;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import com.google.gson.Gson;
import information.Information;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardwareInventory {



    public HardwareInventory() {
    }

    /**
     * @return returns a Map with the Inventory (Map< String elementName, List< int number, Map elementSourceFromResponse > >)
     * @throws UnknownMicroserviceException   server don't know the microservice
     * @throws InvalidServerResponseException the server response can't be read correctly
     */
    public static Map<HardwareElement, Integer> getInventory(){
        //request
        List<String> endpoint = Arrays.asList("inventory", "list");
        Map result;
        try {
            result = Information.webSocketClient.microservice("inventory", endpoint, new HashMap<>());
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
            return null;
        }

        List elements = (List) result.get("elements");
        Map<String, Integer> inventory = new HashMap<>();
        for (Object e : elements) {
            Map element = (Map) e;
            Integer count = 0;
            String elementName = element.get("element_name").toString();
            // adding element to map
            if (!inventory.containsKey(elementName)) inventory.put(elementName, count);
            // counting
            count = inventory.get(elementName) + 1;
            inventory.put(elementName, count);
        }
        Map<HardwareElement, Integer> ret = new HashMap<>();
        for(String s: inventory.keySet()){
            ret.put(HardwareElement.getItemByName(s), inventory.get(s));
        }
        return ret;
    }
}
