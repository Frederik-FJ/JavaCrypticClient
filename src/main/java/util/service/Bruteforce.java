package util.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import items.Device;

import java.util.*;

public class Bruteforce extends Service{
    public Bruteforce(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    public Bruteforce(Service service){
        super(service.serviceUuid, service.device);
    }

    public void attack(Device targetDevice, Service targetService) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("bruteforce", "attack");
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", this.serviceUuid);
        data.put("device_uuid", this.device.getUuid());
        data.put("target_device", targetDevice.getUuid());
        data.put("target_service", targetService.getUuid());
        Map result = Information.webSocketClient.microservice("service", endpoint, data);
    }

    public Map status() throws InvalidServerResponseException, UnknownMicroserviceException {
        List<String> endpoint = Arrays.asList("bruteforce", "status");
        return this.command(endpoint);
    }

    public void stop() throws InvalidServerResponseException, UnknownMicroserviceException {
        List<String> endpoint = Arrays.asList("bruteforce", "stop");
        command(endpoint);
    }
}
