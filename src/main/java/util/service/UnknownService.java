package util.service;

import util.items.Device;

public class UnknownService extends Service {
    public UnknownService(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    @Override
    public String getName() {
        return "Unknown Service";
    }

    @Override
    public boolean isAttackService() {
        return false;
    }
}
