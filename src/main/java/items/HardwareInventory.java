package items;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import connection.WebSocketClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardwareInventory {

    WebSocketClient client;

    public HardwareInventory(WebSocketClient client){
        this.client = client;
    }

    /**
     *
     * @return returns a Map with the Inventory (Map< String elementName, List< int number, Map elementSourceFromResponse > >)
     * @throws UnknownMicroserviceException server don't know the microservice
     * @throws InvalidServerResponseException the server response can't be read correctly
     */
    public Map<String, List> getInventory() throws UnknownMicroserviceException, InvalidServerResponseException {
        //request
        List<String> endpoint = Arrays.asList("inventory", "list");
        Map result = client.microservice("inventory", endpoint, new HashMap<>());

        List elements = (List) result.get("elements");
        Map<String, List> inventory = new HashMap<>();
        for (Object e : elements) {
            Map element = (Map) e;
            List<Object> count = Arrays.asList(0, element);
            String elementName = element.get("element_name").toString();
            if (!inventory.containsKey(elementName)) inventory.put(elementName, count);
            count.set(0, (Integer) inventory.get(elementName).get(0) + 1);
            inventory.put(elementName, count);
        }
        return inventory;
    }
}
