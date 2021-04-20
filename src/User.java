import encryption.Secret;

public class User {
    private String name;
    private int cid;
    private Secret secret;

    public User(String name, int cid) {
        this.name = name;
        this.cid = cid;
        secret = new Secret();
    }
}
