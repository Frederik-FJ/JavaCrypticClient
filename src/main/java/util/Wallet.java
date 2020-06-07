package util;

import Exceptions.InvalidServerResponseException;
import Exceptions.InvalidWalletException;
import Exceptions.UnknownMicroserviceException;
import com.google.gson.Gson;
import information.Information;

import java.util.*;

public class Wallet {

    Gson gson = new Gson();

    String uuid;
    String pw;

    public Wallet(String uuid){
        this.uuid = uuid;
    }

    public Wallet(String uuid, String pw){
        this.uuid = uuid;
        this.pw = pw;
    }

    public void setPassword(String pw) {
        this.pw = pw;
    }

    public double getMorphcoins() throws UnknownMicroserviceException, InvalidWalletException {

        if(pw == null){
            throw new InvalidWalletException();
        }

        if(uuid == null || uuid.length() != 32 || pw.length() != 10){
            return -1;
        }

        Map<String, String> data = new HashMap<>();
        data.put("source_uuid", uuid);
        data.put("key", pw);

        Map result;

        try{
            result = Information.webSocketClient.microservice("currency", Collections.singletonList("get"), data);
        }catch (InvalidServerResponseException e){
            return -1;
        }

        //System.out.println(gson.toJson(result));
        try{
            return (double) result.get("amount");
        } catch (ClassCastException e){
            return -1;
        }
    }

    public Map listConnectedMiner() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("miner", "list");
        Map<String, String> data = new HashMap<>();
        data.put("wallet_uuid", this.uuid);
        return Information.webSocketClient.microservice("service", endpoint, data);
    }


    public void stopMiners() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("miner", "stop");
        Map<String, String> data = new HashMap<>();
        data.put("wallet_uuid", this.uuid);
        Information.webSocketClient.microservice("service", endpoint, data);
    }

    public User getOwner() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("owner");
        Map<String, String> data = new HashMap<>();
        data.put("source_uuid", this.uuid);
        Map result = Information.webSocketClient.microservice("currency", endpoint, data);
        return new User(result.get("owner").toString());
    }

    public void delete() throws InvalidWalletException, UnknownMicroserviceException, InvalidServerResponseException {
        if(this.pw == null){
            throw new InvalidWalletException();
        }
        List<String> endpoint = Collections.singletonList("delete");
        Map<String, String> data = new HashMap<>();
        data.put("source_uuid", this.uuid);
        data.put("key", this.pw);
        Map result = Information.webSocketClient.microservice("currency", endpoint, data);
    }

    public void reset() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("reset");
        Map<String, String> data = new HashMap<>();
        data.put("source_uuid", this.uuid);
        Map result = Information.webSocketClient.microservice("currency", endpoint, data);
    }

    public void send(int amount, Wallet destination, String cause) throws InvalidWalletException, UnknownMicroserviceException, InvalidServerResponseException {
        if(pw == null){
            throw new InvalidWalletException();
        }
        List<String> endpoint = Collections.singletonList("send");
        Map<String, Object> data = new HashMap<>();
        data.put("source_uuid", this.uuid);
        data.put("key", this.pw);
        data.put("destination_uuid", destination.getUuid());
        data.put("send_amount", amount);
        data.put("usage", cause);
        Information.webSocketClient.microservice("currency", endpoint, data);
    }

    public static List<Wallet> getWallets() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<Wallet> ret = new ArrayList<>();
        List<String> endpoint = Collections.singletonList("list");
        Map result = Information.webSocketClient.microservice("currency", endpoint, new HashMap<>());
        for(String uuid : (List<String>)result.get("wallets")){
            ret.add(new Wallet(uuid));
        }
        return ret;
    }


    public String getUuid() {
        return uuid;
    }
}
