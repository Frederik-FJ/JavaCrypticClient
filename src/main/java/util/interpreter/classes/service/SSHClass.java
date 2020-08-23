package util.interpreter.classes.service;

import Exceptions.interpreter.WrongServiceException;
import util.interpreter.Interpreter;
import util.interpreter.annotations.UsableClass;
import util.interpreter.annotations.UsableConstructor;
import util.interpreter.annotations.UsableMethod;
import util.items.Device;
import util.service.SSH;
import util.service.Service;

@UsableClass(name = "SSH")
public class SSHClass extends SSH {

    Interpreter interpreter;

    @UsableConstructor
    public SSHClass(Interpreter interpreter, String serviceUuid, Device device) throws WrongServiceException {
        super(serviceUuid, device);
        this.interpreter = interpreter;

        if (!this.getInfo(false).get("name").toString().equalsIgnoreCase("ssh")) {
            throw new WrongServiceException("Not a SSH Service");
        }
    }

    public SSHClass(Service service) {
        super(service);
    }

    // Service Commands

    @Override
    @UsableMethod(name = "getPort")
    public int getRunningPort() {
        return super.getRunningPort();
    }

    @Override
    @UsableMethod(name = "getUuid")
    public String getUuid() {
        return super.getUuid();
    }

    @Override
    @UsableMethod(name = "getDevice")
    public Device getDevice() {
        return super.getDevice();
    }

}
