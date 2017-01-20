package hemal.t.shah.expensetracker.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Represents one column in the ExpenseEntry table.
 * Created by hemal on 10/11/16.
 */
public class ExpenseParcelable implements Parcelable {

    public static final Creator<ExpenseParcelable> CREATOR = new Creator<ExpenseParcelable>() {
        @Override
        public ExpenseParcelable createFromParcel(Parcel in) {
            return new ExpenseParcelable(in);
        }

        @Override
        public ExpenseParcelable[] newArray(int size) {
            return new ExpenseParcelable[size];
        }
    };
    private String about, firebase_cluster_ref_key, firebase_expense_key,
            description, user_uid;
    private FirebaseUserDetails userDetails; //No need to store this in offline table
    private double amount;
    private long timeStamp;

    /**
     * Use this cluster for shared expense details
     */
    public ExpenseParcelable(String about,
            String firebase_cluster_ref_key,
            @Nullable String userUID,
            FirebaseUserDetails userDetails,
            double amount,
            String firebase_expense_key,
            String description,
            long timeStamp) {
        this.about = about;
        this.firebase_cluster_ref_key = firebase_cluster_ref_key;
        this.userDetails = userDetails;
        this.amount = amount;
        this.description = description;
        this.timeStamp = timeStamp;
        this.user_uid = userUID;
        this.firebase_expense_key = firebase_expense_key;
    }

    /**
     * Use this constructor for personal cluster details.
     */
    public ExpenseParcelable(String about,
            String firebase_cluster_ref_key,
            @Nullable String userUID,
            double amount,
            String firebase_expense_key,
            String description) {
        this.about = about;
        this.firebase_cluster_ref_key = firebase_cluster_ref_key;
        this.user_uid = userUID;
        this.amount = amount;
        this.firebase_expense_key = firebase_expense_key;
        this.description = description;
    }

    public ExpenseParcelable(String about,
            String firebase_cluster_ref_key,
            @Nullable String userUID,
            double amount,
            String firebase_expense_key,
            long timeStamp,
            String description) {
        this.about = about;
        this.firebase_cluster_ref_key = firebase_cluster_ref_key;
        this.amount = amount;
        this.description = description;
        this.firebase_expense_key = firebase_expense_key;
        this.timeStamp = timeStamp;
        this.user_uid = userUID;
    }

    protected ExpenseParcelable(Parcel in) {
        about = in.readString();
        firebase_cluster_ref_key = in.readString();
        firebase_expense_key = in.readString();
        description = in.readString();
        user_uid = in.readString();
        userDetails = in.readParcelable(FirebaseUserDetails.class.getClassLoader());
        amount = in.readDouble();
        timeStamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(about);
        dest.writeString(firebase_cluster_ref_key);
        dest.writeString(firebase_expense_key);
        dest.writeString(description);
        dest.writeString(user_uid);
        dest.writeParcelable(userDetails, flags);
        dest.writeDouble(amount);
        dest.writeLong(timeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAbout() {
        return about;
    }

    public String getFirebase_cluster_ref_key() {
        return firebase_cluster_ref_key;
    }

    public String getFirebase_expense_key() {
        return firebase_expense_key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FirebaseUserDetails getUserDetails() {
        return userDetails;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
