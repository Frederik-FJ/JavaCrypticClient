package util.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import util.items.Device;

import java.util.*;

public abstract class Service {

    protected String serviceUuid;
    protected Device device;

    protected int runningPort;

    boolean running;

    public Service(String serviceUuid, Device device) {
        this.device = device;
        this.serviceUuid = serviceUuid;
    }

    public static List<Service> getServiceList(Device device) {
        List<Service> ret = new ArrayList<>();

        List<String> endpoint = Collections.singletonList("list");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", device.getUuid());
        Map result = null;
        try {
            result = Information.webSocketClient.microservice("service", endpoint, data);
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }
        for (Map map : (List<Map>) result.get("services")) {
            Service s = new UnknownService(map.get("uuid").toString(), new Device(map.get("device").toString()));
            ret.add(Service.toServiceType(s, map.get("name").toString()));
        }
        return ret;
    }

    public static Service toServiceType(Service s, String name) {

        switch (name) {
            case "bruteforce":
                return new Bruteforce(s);
            case "miner":
                return new Miner(s);
            case "portscan":
                return new Portscan(s);
            case "ssh":
                return new SSH(s);
            case "telnet":
                return new Telnet(s);
            default:
                return new UnknownService(s.getUuid(), s.getDevice());
        }
    }

    public abstract String getName();

    public abstract boolean isAttackService();

    public int getRunningPort() {
        return runningPort;
    }

    public void setRunningPort(int runningPort) {
        this.runningPort = runningPort;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    protected Map command(List<String> endpoint) throws UnknownMicroserviceException, InvalidServerResponseException {
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", serviceUuid);
        data.put("device_uuid", device.getUuid());
        return Information.webSocketClient.microservice("service", endpoint, data);
    }

    protected Map getInfo(boolean privat) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint;
        if (privat)
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

    public Device getDevice() {
        return device;
    }
}
