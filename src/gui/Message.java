package gui;

public class Message {
    final String user;
    final String message;
    final String time;

    public Message(String user, String message, String time) {
        this.user = user;
        this.message = message;
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
