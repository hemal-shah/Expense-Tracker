package hemal.t.shah.expensetracker.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents single row in ClusterUsersParcelable table.
 * Created by hemal on 15/12/16.
 */
public class ClusterUsersParcelable implements Parcelable {
    int cluster_id, user_id;

    public ClusterUsersParcelable(int cluster_id, int user_id) {
        this.cluster_id = cluster_id;
        this.user_id = user_id;
    }

    public int getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(int cluster_id) {
        this.cluster_id = cluster_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    protected ClusterUsersParcelable(Parcel in) {
        cluster_id = in.readInt();
        user_id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cluster_id);
        dest.writeInt(user_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClusterUsersParcelable> CREATOR =
            new Creator<ClusterUsersParcelable>() {
                @Override
                public ClusterUsersParcelable createFromParcel(Parcel in) {
                    return new ClusterUsersParcelable(in);
                }

                @Override
                public ClusterUsersParcelable[] newArray(int size) {
                    return new ClusterUsersParcelable[size];
                }
            };
}
