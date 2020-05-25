package Exceptions;

public class UnknownMicroserviceException extends Exception{

    public UnknownMicroserviceException(String ms){
        new Error("Unknown Microservice " + ms).printStackTrace();
    }
}
