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
    private String title, created_by_user, firebase_cluster_id;
    private int is_shared, offline_id;
    private long timeStamp;

    public ClusterParcelable(String title, String created_by_user, String firebase_cluster_id,
            int is_shared, long timeStamp) {
        this.title = title;
        this.created_by_user = created_by_user;
        this.firebase_cluster_id = firebase_cluster_id;
        this.is_shared = is_shared;
        this.timeStamp = timeStamp;
    }

    public ClusterParcelable(String title, String firebase_cluster_id, int is_shared, long timeStamp, int id) {
        this.title = title;
        this.offline_id = id;
        this.firebase_cluster_id = firebase_cluster_id;
        this.is_shared = is_shared;
        this.timeStamp = timeStamp;
    }

    public ClusterParcelable(String title, String created_by_user, int is_shared, int offline_id,
            long timeStamp) {
        this.title = title;
        this.created_by_user = created_by_user;
        this.is_shared = is_shared;
        this.offline_id = offline_id;
        this.timeStamp = timeStamp;
    }

    protected ClusterParcelable(Parcel in) {
        title = in.readString();
        created_by_user = in.readString();
        firebase_cluster_id = in.readString();
        is_shared = in.readInt();
        offline_id = in.readInt();
        timeStamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(created_by_user);
        dest.writeString(firebase_cluster_id);
        dest.writeInt(is_shared);
        dest.writeInt(offline_id);
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

    public String getCreated_by_user() {
        return created_by_user;
    }

    public void setCreated_by_user(String created_by_user) {
        this.created_by_user = created_by_user;
    }

    public String getFirebase_cluster_id() {
        return firebase_cluster_id;
    }

    public void setFirebase_cluster_id(String firebase_cluster_id) {
        this.firebase_cluster_id = firebase_cluster_id;
    }

    public int getIs_shared() {
        return is_shared;
    }

    public void setIs_shared(int is_shared) {
        this.is_shared = is_shared;
    }

    public int getOffline_id() {
        return offline_id;
    }

    public void setOffline_id(int offline_id) {
        this.offline_id = offline_id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
