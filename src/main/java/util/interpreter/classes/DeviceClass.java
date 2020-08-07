package util.interpreter.classes;

import util.file.File;
import util.interpreter.annotations.*;
import util.items.Device;
import util.service.Bruteforce;
import util.service.Miner;
import util.service.Portscan;
import util.service.Service;

@UsableClass(name = "Device")
public class DeviceClass extends Device {

    @UsableConstructor
    public DeviceClass(String uuid) {
        super(uuid);
    }

    public DeviceClass(Device device) {
        super(device.getUuid());
    }

    @UsableFunction(name = "getRandomDevice")
    public static DeviceClass getRandomDevice() {
        return new DeviceClass(Device.getRandomDevice().getUuid());
    }

    @Override
    @UsableMethod(name = "getUuid")
    public String getUuid() {
        return super.getUuid();
    }
    @Override

    @UsableMethod(name = "getName")
    public String getName() {
        return super.getName();
    }

    @Override
    @UsableMethod(name = "isOnline")
    public boolean isOnline() {
        return super.isOnline();
    }

    @Override
    @UsableMethod(name = "boot")
    public void boot() {
        super.boot();
    }

    @Override
    @UsableMethod(name = "shutdown")
    public void shutdown() {
        super.shutdown();
    }

    @Override
    @UsableMethod(name = "changeName")
    public void changeName(String name) {
        super.changeName(name);
    }

    @Override
    @UsableMethod(name = "getMiner")
    public Miner getMinerService() {
        return super.getMinerService();
    }

    @Override
    @UsableMethod(name = "getPortscan")
    public Portscan getPortscanService() {
        return super.getPortscanService();
    }

    @UsableMethod(name = "getBruteforce")
    public Bruteforce getBruteforceService() {
        for (Service s : this.getServices()) {
            if (s instanceof Bruteforce) {
                return new BruteforceClass(s);
            }
        }
        return null;
    }


    @Override
    @UsableMethod(name = "getRootDir")
    public FileClass getRootDirectory() {
        return new FileClass(super.getRootDirectory());
    }

    @Override
    public String toString() {
        return "Device:\n\tName: " + getName() + "\n\tUUID: " + getUuid();
    }
}
