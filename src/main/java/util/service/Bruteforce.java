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

    @Override
    public String getName() {
        return "bruteforce";
    }

    @Override
    public boolean isAttackService() {
        return true;
    }

    public boolean inAttack(){
        try{
            status();
            return true;
        }catch (InvalidServerResponseException ignore){
            return false;
        }
    }

    public void attack(Device targetDevice, Service targetService) throws InvalidServerResponseException{
        List<String> endpoint = Arrays.asList("bruteforce", "attack");
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", this.serviceUuid);
        data.put("device_uuid", this.device.getUuid());
        data.put("target_device", targetDevice.getUuid());
        data.put("target_service", targetService.getUuid());
        try {
            Map result = Information.webSocketClient.microservice("service", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public Map status() throws InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("bruteforce", "status");
        try {
            return this.command(endpoint);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map stop(){
        List<String> endpoint = Arrays.asList("bruteforce", "stop");
        try {
            return command(endpoint);
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
