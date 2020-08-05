package util.network;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import util.items.Device;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    String uuid;
    Device sourceDevice = null;
    Network sourceNetwork = null;

    public Request(String uuid) {
        this.uuid = uuid;
    }

    public Request(String uuid, Device sourceDevice) {
        this.uuid = uuid;
        this.sourceDevice = sourceDevice;
    }

    public Request(String uuid, Network sourceNetwork) {
        this.uuid = uuid;
        this.sourceNetwork = sourceNetwork;
    }

    public Request(String uuid, Device sourceDevice, Network sourceNetwork) {
        this.uuid = uuid;
        this.sourceNetwork = sourceNetwork;
        this.sourceDevice = sourceDevice;
    }



    public void accept() throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("accept");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        try {
            Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public void deny() throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("deny");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        try {
            Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public Device getSourceDevice() {
        return sourceDevice;
    }

    public void setSourceNetwork(Network sourceNetwork) {
        this.sourceNetwork = sourceNetwork;
    }

    public void setSourceDevice(Device sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    public String getUuid() {
        return uuid;
    }

    public Network getSourceNetwork() {
        return sourceNetwork;
    }
}
