package items;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import com.google.gson.Gson;
import information.Information;

import java.util.*;

public class Wallet {

    Gson gson = new Gson();

    String uuid;
    String pw;

    public Wallet(String uuid, String pw){
        this.uuid = uuid;
        this.pw = pw;
    }

    public Wallet(){
        this.uuid = Information.walletUuid;
        this.pw = Information.walletPw;
    }

    public double getMorphcoins() throws UnknownMicroserviceException {

        if(uuid == null || pw == null || uuid.length() < 8 || pw.length() < 6){
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

}
