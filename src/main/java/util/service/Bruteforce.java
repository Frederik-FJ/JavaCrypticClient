package util.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import util.items.Device;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bruteforce extends Service {
    public Bruteforce(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    public Bruteforce(Service service) {
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

    public boolean inAttack() {
        try {
            status();
            return true;
        } catch (InvalidServerResponseException ignore) {
            return false;
        }
    }

    public void attack(Device targetDevice, Service targetService) throws InvalidServerResponseException {
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

    public double getProgress() throws InvalidServerResponseException {
        double progress =  Double.parseDouble(this.status().get("progress").toString());
        double percent = (progress / 20 - 0.1) * 100;
        return ((int) (percent * 100)) / 100.0;
    }

    public boolean stop() {
        List<String> endpoint = Arrays.asList("bruteforce", "stop");
        try {
            return (boolean) command(endpoint).get("access");
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
