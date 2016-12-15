package hemal.t.shah.expensetracker.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO representing single row in UserDetailsEntry table.
 * Created by hemal on 15/12/16.
 */
public class UserDetailsParcelable implements Parcelable {
    String name, email, user_id;

    public UserDetailsParcelable(String name, String email, String user_id) {
        this.name = name;
        this.email = email;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    protected UserDetailsParcelable(Parcel in) {
        name = in.readString();
        email = in.readString();
        user_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(user_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserDetailsParcelable> CREATOR =
            new Creator<UserDetailsParcelable>() {
                @Override
                public UserDetailsParcelable createFromParcel(Parcel in) {
                    return new UserDetailsParcelable(in);
                }

                @Override
                public UserDetailsParcelable[] newArray(int size) {
                    return new UserDetailsParcelable[size];
                }
            };
}
