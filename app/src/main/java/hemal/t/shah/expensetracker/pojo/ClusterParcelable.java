package hemal.t.shah.expensetracker.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO representing single row in the Cluster Table.
 * Created by hemal on 15/12/16.
 */
public class ClusterParcelable implements Parcelable {
    String title, timestamp;
    int is_shared;
    double sum;

    public ClusterParcelable(String title, String timestamp, double sum) {
        this.title = title;
        this.timestamp = timestamp;
        this.sum = sum;
    }

    public ClusterParcelable(String title, String timestamp, int is_shared, double sum) {
        this.title = title;
        this.timestamp = timestamp;
        this.is_shared = is_shared;
        this.sum = sum;
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

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    protected ClusterParcelable(Parcel in) {
        title = in.readString();
        timestamp = in.readString();
        is_shared = in.readInt();
        sum = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(timestamp);
        dest.writeInt(is_shared);
        dest.writeDouble(sum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
}
