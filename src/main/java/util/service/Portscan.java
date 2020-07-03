package util.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import util.items.Device;

import java.util.*;

public class Portscan extends Service {
    public Portscan(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    public Portscan(Service service) {
        super(service.serviceUuid, service.device);
    }

    @Override
    public String getName() {
        return "portscan";
    }

    @Override
    public boolean isAttackService() {
        return true;
    }

    public List<Service> run(Device target_device) {
        List<String> endpoint = Collections.singletonList("use");
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", serviceUuid);
        data.put("device_uuid", device.getUuid());
        data.put("target_device", target_device.getUuid());
        Map result;
        try {
            result = Information.webSocketClient.microservice("service", endpoint, data);
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
            return null;
        }
        List<Service> ret = new ArrayList<>();
        for (Map map : (List<Map>) result.get("services")) {
            Service s = new UnknownService(map.get("uuid").toString(), new Device(map.get("device").toString()));
            s = (Service.toServiceType(s, map.get("name").toString()));
            s.setRunningPort(Double.valueOf((double) map.get("running_port")).intValue());
            try {
                s.setRunning((boolean) map.get("running"));
            } catch (NullPointerException ignore) {
            }
            ret.add(s);
        }
        return ret;
    }
}
