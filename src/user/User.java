package user;

public class User {
    String name;
    String id;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setId(String newId) {
        id = newId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
