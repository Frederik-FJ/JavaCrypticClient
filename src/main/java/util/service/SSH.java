package util.service;

import items.Device;

public class SSH extends Service{
    public SSH(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    public SSH(Service service){
        super(service.serviceUuid, service.device);
    }

    @Override
    public String getName() {
        return "ssh";
    }

    @Override
    public boolean isAttackService() {
        return false;
    }


}
