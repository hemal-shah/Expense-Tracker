package hemal.t.shah.expensetracker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hemal on 10/11/16.
 */
public class ExpenseParcelable implements Parcelable {

    String about;
    int amount;

    protected ExpenseParcelable(Parcel in) {
        about = in.readString();
        amount = in.readInt();
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ExpenseParcelable(String about, int amount) {
        this.about = about;
        this.amount = amount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(about);
        dest.writeInt(amount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
}
