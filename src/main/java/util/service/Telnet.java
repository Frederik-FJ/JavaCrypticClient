package util.service;

import items.Device;

public class Telnet extends Service{
    public Telnet(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    public Telnet(Service service){
        super(service.serviceUuid, service.device);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isAttackService() {
        return false;
    }
}
