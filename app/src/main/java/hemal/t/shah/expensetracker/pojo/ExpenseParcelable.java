package hemal.t.shah.expensetracker.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents one column in the ExpenseEntry table.
 * Created by hemal on 10/11/16.
 */
public class ExpenseParcelable implements Parcelable {

  public static final Creator<ExpenseParcelable> CREATOR = new Creator<ExpenseParcelable>() {
    @Override public ExpenseParcelable createFromParcel(Parcel in) {
      return new ExpenseParcelable(in);
    }

    @Override public ExpenseParcelable[] newArray(int size) {
      return new ExpenseParcelable[size];
    }
  };
  String about, timestamp;
  double amount;
  int cluster_id, user_id;

  public ExpenseParcelable(String about, String timestamp, double amount, int cluster_id,
      int user_id) {
    this.about = about;
    this.timestamp = timestamp;
    this.amount = amount;
    this.cluster_id = cluster_id;
    this.user_id = user_id;
  }

  protected ExpenseParcelable(Parcel in) {
    about = in.readString();
    timestamp = in.readString();
    amount = in.readDouble();
    cluster_id = in.readInt();
    user_id = in.readInt();
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
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

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(about);
    dest.writeString(timestamp);
    dest.writeDouble(amount);
    dest.writeInt(cluster_id);
    dest.writeInt(user_id);
  }

  @Override public int describeContents() {
    return 0;
  }
}
