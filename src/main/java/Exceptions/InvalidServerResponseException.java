package Exceptions;

import com.google.gson.Gson;
import java.util.Map;

public class InvalidServerResponseException extends Exception{

    public InvalidServerResponseException(Map response){
        Gson gson = new Gson();
        new Exception("Invalid Server Response " + gson.toJson(response)).printStackTrace();
    }
}
