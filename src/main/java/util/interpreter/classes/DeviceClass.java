package util.interpreter.classes;

import Exceptions.InvalidServerResponseException;
import Exceptions.interpreter.PermissionDeniedException;
import information.Information;
import util.interpreter.annotations.*;
import util.interpreter.classes.service.BruteforceClass;
import util.interpreter.classes.service.SSHClass;
import util.items.Device;
import util.service.*;

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
    public void changeName(String name) throws PermissionDeniedException {
        try {
            super.changeName(name);
        } catch (InvalidServerResponseException e) {
            e.printStackTrace();
            if (e.getMessage().contains("\"error\":\"permission_denied\"")) {
                throw new PermissionDeniedException("No Permission to change Name for this Device");
            }
        }
    }

    //FIXME return MinerClass and PortscanClass

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

    @UsableMethod(name = "getSSH")
    public SSH getSSHService() {
        for (Service s : this.getServices()) {
            if (s instanceof SSH) {
                return new SSHClass(s);
            }
        }
        return null;
    }

    @UsableMethod(name = "portscanSSH")
    public SSH getSSHWithPortscan(Device target) {
        for (Service s : getPortscanService().run(target)) {
            if (s instanceof SSH) {
                return new SSHClass(s);
            }
        }
        return null;
    }

    @UsableMethod(name = "connect")
    public void connect() {
        Information.Desktop.startTerminal(this);
    }


    @Override
    @UsableMethod(name = "getRootDir")
    public FileClass getRootDirectory() {
        return new FileClass(super.getRootDirectory());
    }

    @Override
    public String toString() {
        return "Device:" +
                "\n\tName: " + getName() +
                "\n\tUUID: " + getUuid() +
                "\n\tReference: Device@" + Integer.toHexString(this.hashCode());
    }
}
