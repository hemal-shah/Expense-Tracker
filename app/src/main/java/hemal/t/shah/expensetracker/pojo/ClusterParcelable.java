package hemal.t.shah.expensetracker.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class ClusterParcelable implements Parcelable {

    public static final Creator<ClusterParcelable> CREATOR = new Creator<ClusterParcelable>() {
        @Override
        public ClusterParcelable createFromParcel(Parcel in) {
            return new ClusterParcelable(in);
        }

        @Override
        public ClusterParcelable[] newArray(int size) {
            return new ClusterParcelable[size];
        }
    };
    private String title, created_by_user, firebase_cluster_id, email, url;
    private int is_shared;
    private long timeStamp;


    public ClusterParcelable(String title, String created_by_user, String firebase_cluster_id,
            int is_shared, long timeStamp) {
        this.title = title;
        this.created_by_user = created_by_user;
        this.firebase_cluster_id = firebase_cluster_id;
        this.is_shared = is_shared;
        this.timeStamp = timeStamp;
    }

    public ClusterParcelable(String title, String created_by_user, String firebase_cluster_id,
            String email, String url, int is_shared, long timeStamp) {
        this.title = title;
        this.created_by_user = created_by_user;
        this.firebase_cluster_id = firebase_cluster_id;
        this.email = email;
        this.url = url;
        this.is_shared = is_shared;
        this.timeStamp = timeStamp;
    }

    protected ClusterParcelable(Parcel in) {
        title = in.readString();
        created_by_user = in.readString();
        firebase_cluster_id = in.readString();
        email = in.readString();
        url = in.readString();
        is_shared = in.readInt();
        timeStamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(created_by_user);
        dest.writeString(firebase_cluster_id);
        dest.writeString(email);
        dest.writeString(url);
        dest.writeInt(is_shared);
        dest.writeLong(timeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirebase_cluster_id() {
        return firebase_cluster_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIs_shared() {
        return is_shared;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
