package display_users;

public class UserDataModel implements Comparable<UserDataModel> {
    public String name;
    public String status;
    public String image;
    public String date;
    private String id;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserDataModel() {
    }

    public UserDataModel(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int compareTo(UserDataModel o) {
        return new Double(this.getDate()).compareTo(new Double(o.getDate()));
    }
}
