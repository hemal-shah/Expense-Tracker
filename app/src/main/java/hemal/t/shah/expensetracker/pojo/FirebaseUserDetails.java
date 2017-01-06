package hemal.t.shah.expensetracker.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * There is no need to create a table for this class,
 * All details will always be obtained from Firebase.
 * Created by hemal on 6/1/17.
 */

public class FirebaseUserDetails implements Parcelable {
    public static final Creator<FirebaseUserDetails> CREATOR = new Creator<FirebaseUserDetails>() {
        @Override
        public FirebaseUserDetails createFromParcel(Parcel in) {
            return new FirebaseUserDetails(in);
        }

        @Override
        public FirebaseUserDetails[] newArray(int size) {
            return new FirebaseUserDetails[size];
        }
    };
    private String user_name, user_email, user_uid, user_photo_url;

    public FirebaseUserDetails(String user_name, String user_email, String user_uid, String user_photo_url) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_uid = user_uid;
        this.user_photo_url = user_photo_url;
    }

    protected FirebaseUserDetails(Parcel in) {
        user_name = in.readString();
        user_email = in.readString();
        user_uid = in.readString();
        user_photo_url = in.readString();
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getUser_photo_url() {
        return user_photo_url;
    }

    public void setUser_photo_url(String user_photo_url) {
        this.user_photo_url = user_photo_url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);
        dest.writeString(user_email);
        dest.writeString(user_uid);
        dest.writeString(user_photo_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
