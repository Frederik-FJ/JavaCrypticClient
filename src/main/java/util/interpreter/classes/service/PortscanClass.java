package util.interpreter.classes.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import util.interpreter.annotations.UsableClass;
import util.interpreter.annotations.UsableConstructor;
import util.interpreter.annotations.UsableMethod;
import util.items.Device;
import util.service.Portscan;
import util.service.Service;

@UsableClass(name = "Portscan")
public class PortscanClass extends Portscan {

    @UsableConstructor
    public PortscanClass(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    public PortscanClass(Service service) {
        super(service);
    }

    // Portscan Methods

    // TODO implement list in the interpreter to continue here

    // Service methods

    @Override
    @UsableMethod(name = "getName")
    public String getName() {
        return super.getName();
    }

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
}
