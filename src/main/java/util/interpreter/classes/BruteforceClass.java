package util.interpreter.classes;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import util.interpreter.annotations.UsableClass;
import util.interpreter.annotations.UsableConstructor;
import util.interpreter.annotations.UsableMethod;
import util.items.Device;
import util.service.Bruteforce;
import util.service.Service;

import java.util.Map;

@UsableClass(name = "Bruteforce")
public class BruteforceClass extends Bruteforce {

    @UsableConstructor
    public BruteforceClass(String serviceUuid, DeviceClass device) {
        super(serviceUuid, device);
    }

    public BruteforceClass(Service service) {
        super(service);
    }

    // BruteforceCommands

    @Override
    @UsableMethod(name = "attack")
    public void attack(Device targetDevice, Service targetService) throws InvalidServerResponseException {
        super.attack(targetDevice, targetService);
    }

    @Override
    @UsableMethod(name = "isRunning")
    public boolean isRunning() {
        return super.isRunning();
    }

    @Override
    @UsableMethod(name = "stop")
    public boolean stop() {
        return super.stop();
    }


    // Service Commands

    @Override
    @UsableMethod(name = "getDevice")
    public Device getDevice() {
        return super.getDevice();
    }

    @Override
    @UsableMethod(name = "delete")
    public void delete() throws InvalidServerResponseException, UnknownMicroserviceException {
        super.delete();
    }

    @Override
    @UsableMethod(name = "getRunningPort")
    public int getRunningPort() {
        return super.getRunningPort();
    }
}
