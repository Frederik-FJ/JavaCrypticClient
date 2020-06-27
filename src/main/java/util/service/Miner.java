package util.service;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import information.Information;
import items.Device;
import util.Wallet;

import java.util.*;

public class Miner extends Service {
    public Miner(String serviceUuid, Device device) {
        super(serviceUuid, device);
    }

    public Miner(Service service){
        super(service.serviceUuid, service.device);
    }


    int power = -1;
    Wallet wallet = null;

    @Override
    public String getName() {
        return "miner";
    }

    @Override
    public boolean isAttackService() {
        return false;
    }

    public static Miner createMiner(String walletUuid, Device device) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("create");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", device.getUuid());
        data.put("name", "miner");
        data.put("wallet_uuid", walletUuid);
        Map result = Information.webSocketClient.microservice("service", endpoint, data);
        return new Miner(result.get("uuid").toString(), new Device(result.get("device").toString()));
    }

    public void connectWallet(Wallet wallet) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("miner", "wallet");
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", this.serviceUuid);
        data.put("wallet_uuid", wallet.getUuid());
        Information.webSocketClient.microservice("service", endpoint, data);
        this.wallet = wallet;
    }

    public void setPower(int power) {
        List<String> endpoint = Arrays.asList("miner", "power");
        Map<String, Object> data = new HashMap<>();
        data.put("service_uuid", this.serviceUuid);
        data.put("power", power/100.0);
        try {
            Information.webSocketClient.microservice("service", endpoint, data);
            this.power = power;
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
        }
    }

    public void collect() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("miner", "collect");
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", this.serviceUuid);
        Information.webSocketClient.microservice("service", endpoint, data);
    }

    private Map getInfos() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("miner", "get");
        Map<String, String> data = new HashMap<>();
        data.put("service_uuid", this.serviceUuid);
        return Information.webSocketClient.microservice("service", endpoint, data);
    }

    public int getPower(){
        if(power == -1){
            try{
                return Double.valueOf((double)getInfos().get("power")*100).intValue();
            } catch (UnknownMicroserviceException | InvalidServerResponseException ignore){ }
        }
        return power;
    }

    public Wallet getWallet(){
        if(wallet == null){
            try{
                this.wallet = new Wallet(getInfos().get("wallet").toString());
            }catch (UnknownMicroserviceException | InvalidServerResponseException ignore){ }
        }
        return wallet;
    }
}
