package hemal.t.shah.expensetracker.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO representing single row in the Cluster Table.
 * Created by hemal on 15/12/16.
 */
public class ClusterParcelable implements Parcelable {
    public static final Creator<ClusterParcelable> CREATOR = new Creator<ClusterParcelable>() {
        @Override public ClusterParcelable createFromParcel(Parcel in) {
            return new ClusterParcelable(in);
        }

        @Override public ClusterParcelable[] newArray(int size) {
            return new ClusterParcelable[size];
        }
    };
    String title, timestamp;
    int is_shared;
    int id;
    String firebaseKey;

    public ClusterParcelable(String title, String timestamp, int is_shared, String firebaseKey) {
        this.title = title;
        this.timestamp = timestamp;
        this.is_shared = is_shared;
        this.firebaseKey = firebaseKey;
    }

    public ClusterParcelable(String title, String timestamp, int is_shared, int id) {
        this.title = title;
        this.timestamp = timestamp;
        this.is_shared = is_shared;
        this.id = id;
    }
    // TODO: 26/12/16 changed to cluster id from online.

    protected ClusterParcelable(Parcel in) {
        title = in.readString();
        timestamp = in.readString();
        is_shared = in.readInt();
        id = in.readInt();
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getIs_shared() {
        return is_shared;
    }

    public void setIs_shared(int is_shared) {
        this.is_shared = is_shared;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(timestamp);
        dest.writeInt(is_shared);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
