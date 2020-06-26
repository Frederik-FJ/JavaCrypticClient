package connection;

import Exceptions.*;
import com.google.gson.Gson;
import util.Path;
import information.Information;
import items.Device;
import items.HardwareElement;
import items.HardwareInventory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Client {

    public WebSocketClient clientEndPoint;
    String server;
    volatile boolean started = false;

    public String user = "";
    public String device = "";
    String token = "";

    public Path path = null;

    public Device connectedDevice = null;
    public boolean connected = false;
    public boolean loggedIn = false;

    public Client(String server) {
        this.server = server;
    }

    public WebSocketClient getClient() {
        return clientEndPoint;
    }

    public boolean isStarted(){
        return started;
    }


    public void init() {
        Scanner s = new Scanner(System.in);
        try {
            clientEndPoint = new WebSocketClient(new URI(this.server));
            Information.webSocketClient = clientEndPoint;

            System.out.println(onlinePlayer());

            Thread t = new ConnectionThread(clientEndPoint);
            t.start();
            started = true;

            while (true) {
                System.out.print("[" + this.user);
                if (connected) System.out.print("@" + this.device);
                System.out.print("]");
                if (connected) System.out.print(path.getPwd());
                System.out.print(" $");

                String st = s.nextLine();
                try {
                    System.out.println(processCommand(st));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }


        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxexception exception: " + ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            s.close();
        }
    }


    public Map sendCommand(Map<String, String> cmd) {
        return clientEndPoint.request(cmd, false);
    }


    public String processCommand(String cmd) throws InvalidServerResponseException, UnknownMicroserviceException { Gson gson = new Gson();

        while(cmd.contains("  ")){
            cmd = cmd.replace("  ", " ");
        }

        // Befehl
        boolean status = cmd.equalsIgnoreCase("status") || cmd.replace(" ", "").equalsIgnoreCase("onlineplayer");
        boolean exit = cmd.equalsIgnoreCase("exit") || cmd.equalsIgnoreCase("quit");
        boolean logout = cmd.equalsIgnoreCase("logout");
        boolean login = cmd.equalsIgnoreCase("login");
        boolean empty = cmd.equalsIgnoreCase("");

        boolean deviceCmd = cmd.startsWith("device");
        boolean inventoryCmd = cmd.startsWith("inventory");
        boolean shopCmd = cmd.startsWith("shop");

        boolean pwd = cmd.startsWith("pwd");
        boolean ls = cmd.startsWith("ls") || cmd.startsWith("dir");
        boolean cd = cmd.startsWith("cd");

        String[] params = cmd.split( " ");

        // Commands which do the same in every case
        if(status) return "Online Player: " + onlinePlayer();
        if(empty) return "";

        // Commands if you're logged in (doesn't matter if you're connected to a device)

        if(loggedIn ){
            if(cmd.equalsIgnoreCase("info")){
                Map response = info();
                String res = "Name: \t" + response.get("name").toString();
                res += "\nPlayer UUID: \t" + response.get("uuid").toString();
                return res;
            }
            if(inventoryCmd) return inventoryCmd(cmd);
        }
        if (connected) {  // if you're connected with a device
            if (exit) {
                disconnect();
            }

            if(ls){
                return path.listFiles();
            }

            if(cd){
                if(params.length < 2){
                    return path.changeDirectory("/");
                }
                return path.changeDirectory(params[1]);
            }

            if(pwd){
                path.updatePwd();
                return path.getPwd();
            }

            if(shopCmd) return shopCmd(cmd);

        } else if (loggedIn) {  // if you're logged in in an account but not connected

            if (exit) {
                System.out.println("exit");
                System.exit(1);
            }

            if(logout){
                this.logout();
                System.out.println("logged out");
                System.exit(1);
            }

            if(deviceCmd) return deviceCmd(cmd);

        } else { // if you're not logged in

            if (exit) {
                System.out.println("Exit");
                System.exit(1);
            }

            if(login){
                Scanner s = new Scanner(System.in);
                System.out.print("Username: ");
                String uname = s.nextLine();
                System.out.print("Password: ");
                String pw = s.nextLine();

                try {
                    token = login(uname, pw);
                    return "Logged in as " + user;
                } catch (InvalidLoginException e) {
                    return "\n The Login Credentials were wrong";
                }
            }

        }


        return "Unknown Command: \"" + cmd + "\"";
    }




    // info
    public int onlinePlayer() {
        Map<String, String> cmd = new HashMap<>();
        if(!loggedIn) cmd.put("action", "status");
        if(loggedIn) cmd.put("action", "info");
        int response = 0;
        try {
            Map resp = sendCommand(cmd);
            if(resp.containsKey("error")) throw new InvalidServerResponseException(resp);
            Double d = (double) resp.get("online");
            response = d.intValue();
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.out.println("Es kam eine unerwartete Anwort zurück");
        } catch (InvalidServerResponseException e){
            e.printStackTrace();
        }
        return response;
    }

    public Map info() throws InvalidServerResponseException {
        Map<String, String> cmd = new HashMap<>();
        cmd.put("action", "info");
        Map resp = sendCommand(cmd);
        if(resp.containsKey("error")) throw new InvalidServerResponseException(resp);
        return resp;
    }


    // User
    public String login(String username, String password) throws InvalidLoginException, InvalidServerResponseException {
        Map<String, String> req = new HashMap<>();
        req.put("action", "login");
        req.put("name", username);
        req.put("password", password);
        Map response = clientEndPoint.request(req, false);

        if (response.containsKey("error")) {
            String error = response.get("error").toString();
            if(error.equals("permissions denied")) throw new InvalidLoginException();
            throw new InvalidServerResponseException(response);
        }
        if(!response.containsKey("token")) throw new InvalidServerResponseException(response);
        loggedIn = true;
        this.user = info().get("name").toString();
        return response.get("token").toString();
    }

    public void logout() {
        Map<String, String> cmd = new HashMap<String, String>(){{put("action", "logout");}};
        clientEndPoint.request(cmd, true);
        System.exit(1);

    }


    //device
    public String deviceCmd(String cmd) throws UnknownMicroserviceException, InvalidServerResponseException {
        Gson gson = new Gson();
        String usage = "device list|build";

        String[] command = cmd.split(" ");
        try{
            cmd = command[1];
        }catch (ArrayIndexOutOfBoundsException e){
            return usage;
        }



        String[] params = new String[command.length-2];
        for(int i = 2; i < command.length; i++){
            params[i-2] = command[i];
        }

        // general device commands
        if(cmd.equalsIgnoreCase("list")){
            Map res = getDevices();
            List devices = (List) res.get("devices");
            String ret = "";
            for(Object d: devices){
                Map device = (Map) d;
                ret = "\t" + device.get("name") + " (" + device.get("uuid") + ") \t" + ((boolean) device.get("powered_on")?"on":"off") + "\n";
            }
            return ret;
        }
        if(cmd.equalsIgnoreCase("build")) {
            if (params.length != 8) {
                return "device build <mainboard> <cpu> <gpu> <ram> <disk> <cooler> <powerPack> <case>";
            }
            Map result = buildDeviceCmd(params[0], params[1], params[2], Arrays.asList(params[3]), Arrays.asList(params[4]), Arrays.asList(params[5]), params[6], params[7]);
            return "gson.toJson(result)";
        }

        // device commands for a specific device
        if(params.length < 1) return "specify a uuid as parameter";
        Device device = null;
        if(params[0].length() == 32+4){
            device = new Device(params[0], clientEndPoint);
        }else {
            Map res = getDevices();
            List<Map> devices = (List<Map>) res.get("devices");
            for(Map d: devices){
                if(d.get("name").equals(params[0])){
                    device = new Device((String) d.get("uuid"), clientEndPoint);
                }
            }
        }
        if(device == null){
            return "Unknown Device";
        }


        if(cmd.equalsIgnoreCase("connect")){
            try {
                return connect(device);
            } catch (DeviceNotOnlineException e) {
                return "Device not online";
            }
        }

        if(cmd.equals("info")){
            String uuid = params[0];
            Map info = device.deviceInfo();
            List<Map> hardware = (List<Map>) info.get("hardware");
            StringBuilder ret = new StringBuilder("Name: \t" + info.get("name"));
            ret.append("\nUUID: \t").append(info.get("uuid"));
            ret.append("\nStatus: \t").append((boolean) info.get("powered_on") ? "on" : "off");
            ret.append("\nHardware:");
            for(Map component : hardware){
                ret.append("\n\t").append(component.get("hardware_type")).append(":\t ").append(component.get("hardware_element"));
            }
            return ret + gson.toJson(hardware);
        }

        if(cmd.equals("ping")){

            return device.isOnline() + "";
        }

        if(cmd.equals("boot")){

            return gson.toJson(device.boot());
        }

        if(cmd.equals("shutdown")){

            return gson.toJson(device.shutdown());
        }

        if(cmd.equals("elements")){
            HardwareElement[] elements = device.getElements();
            StringBuilder ret = new StringBuilder();
            for(HardwareElement e: elements){
                ret.append(e.getType()).append(": \t").append(e.getName()).append("\n");
            }
            return ret.toString();
        }

        if(cmd.equals("owner")){
            //return gson.toJson(device.getOwner());
        }

        return usage;
    }

    public String connect(Device device) throws DeviceNotOnlineException {
            if(!device.isOnline()) throw new DeviceNotOnlineException();

        connected = true;
        this.device = device.getName();
        connectedDevice = device;
        path = new Path(connectedDevice);
        return "connected";
    }

    public void disconnect(){
        path = null;
        connected = false;
        device = "";
        connectedDevice = null;
    }

    public Map getDevices() throws InvalidServerResponseException, UnknownMicroserviceException {
        List<String> endpoint = Arrays.asList("device", "all");
        Map result = clientEndPoint.microservice("device", endpoint, new HashMap<>());
        return result;
    }

    public Map buildDeviceCmd(String mainboard, String cpu, String gpu, List<String> ram, List<String> disk, List<String> processCooler, String powerPack, String computerCase) throws InvalidServerResponseException, UnknownMicroserviceException {
        Map<String, List> inventory = new HardwareInventory(clientEndPoint).getInventory();

        for(String s: inventory.keySet()){

            if(s.replace(" ", "").equals(mainboard)) {
                mainboard = s;
                continue;
            }

            if(s.replace(" ", "").equals(cpu)) {
                cpu = s;
                continue;
            }

            if(s.replace(" ", "").equals(gpu)) {
                gpu = s;
                continue;
            }
            if(s.replace(" ", "").equals(powerPack)){
                powerPack = s;
                continue;
            }
            if(s.replace(" ", "").equals(computerCase)){
                computerCase = s;
                continue;
            }
            for(int i = 0; i < ram.size(); i++){
                if(s.replace(" ", "").equals(ram.get(i))){
                    ram.set(i, s);
                }
            }
            for(int i = 0; i < disk.size(); i++){
                if(s.replace(" ", "").equals(disk.get(i))){
                    disk.set(i, s);
                }
            }
            for(int i = 0; i < processCooler.size(); i++){
                if(s.replace(" ", "").equals(processCooler.get(i))){
                    processCooler.set(i, s);
                }
            }

        }

        return buildDevice(mainboard, cpu, gpu, ram, disk, processCooler, powerPack, computerCase);

    }

    public Map buildDevice(String mainboard, String cpu, String gpu, List<String> ram, List<String> disk, List<String> processCooler,String powerPack, String computerCase) throws UnknownMicroserviceException, InvalidServerResponseException {

        Gson gson = new Gson();

        List<String> endpoint = Arrays.asList("device", "create");
        Map<String, Object> components = new HashMap<>();
        components.put("mainboard", mainboard);
        components.put("cpu", Arrays.asList(cpu));
        components.put("gpu", Arrays.asList(gpu));
        components.put("ram", ram);
        components.put("disk", disk);
        components.put("processorCooler", processCooler);
        components.put("powerPack", powerPack);
        components.put("case", computerCase);
        System.out.println(gson.toJson(components));
        Map result = clientEndPoint.microservice("device", endpoint, components);
        System.out.println(gson.toJson(result));

        return null;
    }


    //files
    public String fileCmd(String cmd){


        return "";
    }




    // inventory
    public String inventoryCmd(String cmd) throws InvalidServerResponseException, UnknownMicroserviceException {
        Gson gson = new Gson();

        String command[] = cmd.split(" ");
        cmd = command[1];

        if(cmd.equalsIgnoreCase("list")){
            Map<String, List> inventory = new HardwareInventory(clientEndPoint).getInventory();

            final String[] ret = {""};

            inventory.forEach((k, v) -> ret[0] += v.get(0) + "x " +  k + "\n");

            return ret[0] + inventory.get("ATX"); // inventory.get("ATX"); ist eine Ausgabe zum sehen, wie das Json aufgebaut ist
        }

        return "inventory list|(trade)";
    }


    // shop
    public String shopCmd(String cmd) throws InvalidServerResponseException, UnknownMicroserviceException {
        Gson gson = new Gson();
        String usage = "shop list|(buy)";

        String command[] = cmd.split(" ");
        try{
            cmd = command[1];
        }catch (NullPointerException e){
            return usage;
        }

        String[] params = new String[command.length-2];
        for(int i = 2; i < command.length; i++){
            params[i-2] = command[i];
        }

        if(cmd.equals("list")){
            Map items = getShopItems();
            return gson.toJson(items);
        }

        return usage;
    }


    public Map getShopItems() throws UnknownMicroserviceException, InvalidServerResponseException {
        return clientEndPoint.microservice("inventory", Arrays.asList("shop", "list"), new HashMap<>());
    }


    public class ConnectionThread extends Thread {
        WebSocketClient client;

        public ConnectionThread(WebSocketClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(30000);
                    processCommand("status");
                } catch (InterruptedException | InvalidServerResponseException | UnknownMicroserviceException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
