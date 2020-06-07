package util.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import items.Device;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Portscan extends Service{
    public Portscan(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    public Portscan(Service service){
        super(service.serviceUuid, service.device);
    }

    public void run(String target_device) throws InvalidServerResponseException, UnknownMicroserviceException {
        List<String> endpoint = Collections.singletonList("use");
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", serviceUuid);
        data.put("device_uuid", device.getUuid());
        data.put("target_device", target_device);
        Information.webSocketClient.microservice("service", endpoint, data);
    }
}
