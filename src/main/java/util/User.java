package util;

public class User {

    String uuid;

    public User(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUserName() {
        return "Anonym";
    }
}
