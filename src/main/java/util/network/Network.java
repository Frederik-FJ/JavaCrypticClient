package util.network;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import com.google.gson.Gson;
import information.Information;
import util.items.Device;

import java.util.*;

public class Network {

    String uuid;

    public Network(String uuid) {
        this.uuid = uuid;
    }


    public static Network createNetwork(Device device, String name, boolean hidden) throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("create");
        Map<String, Object> data = new HashMap<>();
        data.put("device", device.getUuid());
        data.put("name", name);
        data.put("hidden", hidden);
        Map resp;
        try {
            resp = Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
            return null;
        }
        return new Network(resp.get("uuid").toString());
    }

    public static Network[] getOwnNetworks(Device device) throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("owner");
        Map<String, String> data = new HashMap<>();
        data.put("device", device.getUuid());


        Map<String, List<Map<String, String>>> ret;
        try {
            ret = Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
            return new Network[0];
        }
        return getNetworksFromMap(ret.get("networks"));
    }

    public static Network[] getMemberNetworks(Device device) throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("member");
        Map<String, String> data = new HashMap<>();
        data.put("device", device.getUuid());

        System.out.println(new Gson().toJson(data));
        Map<String, List<Map<String, String>>> ret;
        System.out.println(device.getUuid());
        try {
            System.out.println(new Gson().toJson(data));
            ret = Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
            return new Network[0];
        }

        return getNetworksFromMap(ret.get("networks"));
    }

    public static Request[] getDeviceInvitations(Device device) throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("invitations");
        Map<String, String> data = new HashMap<>();
        data.put("device", device.getUuid());

        Map<String, List<Map<String, String>>> res;
        try {
            res = Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
            return new Request[0];
        }
        Request[] ret = new Request[res.get("invitations").size()];
        for (int i = 0; i < res.get("invitations").size(); i++) {
            Map<String, String> inv = res.get("invitations").get(1);
            ret[i] = new Request(inv.get("uuid"), new Device(inv.get("device")), new Network(inv.get("network")));
        }
        return ret;
    }

    public List<Request> getInvitations() throws InvalidServerResponseException {
        return getRequests("invitations", Arrays.asList("invitations", "network"));
    }

    public List<Request> getRequests() throws InvalidServerResponseException {
        return getRequests("requests", Collections.singletonList("requests"));
    }

    public void request(Device sourceDevice) throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("request");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("device", sourceDevice.getUuid());

        try {
            Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public void invite(Device device) throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("invite");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("device", device.getUuid());

        try {
            Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public void leave(Device device) throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("leave");
        Map<String, String> data = new HashMap<>();
            data.put("uuid", uuid);
            data.put("device", device.getUuid());

        try {
            Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public void kick(Device device) throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("kick");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("device", device.getUuid());
        try {
            Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public void delete() throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("delete");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        try {
            Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public Device[] getMembers() throws InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("members");
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);

        Map<String, List<Map<String, String>>> ret;
        try {
            ret = Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
            return new Device[0];
        }

        Device[] res = new Device[ret.get("members").size()];
        for (int i = 0; i < ret.get("members").size(); i++) {
            Map<String, String> member = ret.get("members").get(i);
            res[i] = new Device(member.get("device"));
        }
        return res;
    }

    private static Network[] getNetworksFromMap(List<Map<String, String>> networkList) {
        Network[] res = new Network[networkList.size()];
        for (int i = 0; i < networkList.size(); i++) {
            Map<String, String> network = networkList.get(i);
            res[i] = new Network(network.get("uuid"));
        }
        return res;
    }

    private List<Request> getRequests(String type, List<String> endpoint) throws InvalidServerResponseException {
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);

        Map<?, ?> req;
        try {
            req = Information.webSocketClient.microservice("network", endpoint, data);
        } catch (UnknownMicroserviceException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        List<Request> ret = new ArrayList<>();
        for(Map<?, ?> map : (List<Map<?, ?>>) req.get(type)) {
            ret.add(new Request(map.get("uuid").toString(), new Device(map.get("device").toString())));
        }
        return ret;
    }

}
