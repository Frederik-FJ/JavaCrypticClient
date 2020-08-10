package util.interpreter.classes.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import util.interpreter.annotations.UsableClass;
import util.interpreter.annotations.UsableConstructor;
import util.interpreter.annotations.UsableMethod;
import util.interpreter.classes.DeviceClass;
import util.items.Device;
import util.service.Bruteforce;
import util.service.Service;


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

    @UsableMethod(name = "attack")
    public boolean attack(Device targetDevice, Service targetService, Double time) throws InvalidServerResponseException {
        super.attack(targetDevice, targetService);
        int timeInS = time.intValue();
        try {
            Thread.sleep(timeInS * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return super.stop();
    }


    @Override
    @UsableMethod(name = "getProgress")
    public double getProgress() throws InvalidServerResponseException {
        return super.getProgress();
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
        return new DeviceClass(super.getDevice());
    }

    @Override
    @UsableMethod(name = "delete")
    public void delete() throws InvalidServerResponseException, UnknownMicroserviceException {
        super.delete();
    }

    @Override
    @UsableMethod(name = "getName")
    public String getName() {
        return super.getName();
    }

    @Override
    @UsableMethod(name = "getRunningPort")
    public int getRunningPort() {
        return super.getRunningPort();
    }
}
