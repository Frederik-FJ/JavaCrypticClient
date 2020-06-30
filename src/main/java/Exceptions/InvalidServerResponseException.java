package Exceptions;

import com.google.gson.Gson;

import java.util.Map;

public class InvalidServerResponseException extends Exception {

    static Gson gson = new Gson();

    Map response;

    public InvalidServerResponseException(Map response) {
        super("Invalid Server Response " + gson.toJson(response));
        this.response = response;
    }

    public InvalidServerResponseException(Map request, Map response) {
        super("Invalid Server Response \n Request:\n\t" + gson.toJson(request) + "\nAnswer\n\t" + gson.toJson(response));
        this.response = response;
    }

    public Map getResponse(){
        return response;
    }
}
