package Model;

/**
 * Created by kmsoft on 2/9/18.
 */

public class SqLiteDatabaseModel {

    String id;
    String FacebookTime;
    String InstagramTime;
    String SnapChatTime;
    String curentDate;

    public SqLiteDatabaseModel() {

    }
    public SqLiteDatabaseModel(String id, String facebookTime) {
        this.id = id;
        this.FacebookTime = facebookTime;
    }

    public SqLiteDatabaseModel(String id, String facebookTime, String snapChatTime) {
        this.id = id;
        this.FacebookTime = facebookTime;
        this.SnapChatTime = snapChatTime;
    }

    public SqLiteDatabaseModel(String id, String facebookTime, String snapChatTime, String instagramTime) {
        this.id = id;
        this.FacebookTime = facebookTime;
        this.SnapChatTime = snapChatTime;
        this.InstagramTime = instagramTime;
    }


    public SqLiteDatabaseModel(String id, String facebookTime, String snapChatTime, String instagramTime, String cuurentDate) {
        this.id = id;
        this.FacebookTime = facebookTime;
        this.SnapChatTime = snapChatTime;
        this.InstagramTime = instagramTime;
        this.curentDate = cuurentDate;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacebookTime() {
        return FacebookTime;
    }

    public void setFacebookTime(String facebookTime) {
        FacebookTime = facebookTime;
    }

    public String getInstagramTime() {
        return InstagramTime;
    }

    public void setInstagramTime(String instagramTime) {
        InstagramTime = instagramTime;
    }

    public String getSnapChatTime() {
        return SnapChatTime;
    }

    public void setSnapChatTime(String snapChatTime) {
        SnapChatTime = snapChatTime;
    }

    public String getCurentDate() {
        return curentDate;
    }

    public void setCurentDate(String curentDate) {
        this.curentDate = curentDate;
    }

}
