package util.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import items.Device;

import java.util.*;

public class Service {

    protected String serviceUuid;
    protected Device device;

    public Service(String serviceUuid, Device device){
        this.device = device;
        this.serviceUuid = serviceUuid;
    }

    protected Map command(List<String> endpoint) throws UnknownMicroserviceException, InvalidServerResponseException {
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", serviceUuid);
        data.put("device_uuid", device.getUuid());
        return Information.webSocketClient.microservice("service", endpoint, data);
    }

    protected Map getInfo(boolean privat) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint;
        if(privat)
            endpoint = Collections.singletonList("private_info");
        else
            endpoint = Collections.singletonList("public_info");
        return command(endpoint);
    }

    public void delete() throws InvalidServerResponseException, UnknownMicroserviceException {
        command(Collections.singletonList("delete"));
    }

    public void toggle() throws InvalidServerResponseException, UnknownMicroserviceException {
        command(Collections.singletonList("toggle"));
    }

    public String getUuid() {
        return serviceUuid;
    }

    public static List<Service> getServiceList(Device device) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<Service> ret = new ArrayList<>();

        List<String> endpoint = Collections.singletonList("list");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", device.getUuid());
        Map result = Information.webSocketClient.microservice("service", endpoint, data);
        for(Map map : (List<Map>)result.get("Services")){
            Service s = new Service(map.get("uuid").toString(), new Device(map.get("device").toString()));
            switch (map.get("name").toString()){
                case "bruteforce":
                    ret.add(new Bruteforce(s));
                    break;
                case "miner":
                    ret.add(new Miner(s));
                    break;
                case "portscan":
                    ret.add(new Portscan(s));
                    break;
                default:
                    ret.add(s);
                    break;
            }
        }
        return ret;
    }
}
