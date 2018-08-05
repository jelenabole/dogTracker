package hr.tvz.trackmydog.dogModel;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Extras implements Serializable {

    private boolean empty;
    private boolean emptyParcel;
    private int mFlags;
    private boolean parcelled;
    private int size;

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isEmptyParcel() {
        return emptyParcel;
    }

    public void setEmptyParcel(boolean emptyParcel) {
        this.emptyParcel = emptyParcel;
    }

    public int getmFlags() {
        return mFlags;
    }

    public void setmFlags(int mFlags) {
        this.mFlags = mFlags;
    }

    public boolean isParcelled() {
        return parcelled;
    }

    public void setParcelled(boolean parcelled) {
        this.parcelled = parcelled;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        String str = "";

        str += "\n " + "empty: " + empty;
        str += " - " + "emptyParcel: " + emptyParcel;
        str += " - " + "mFlags: " + mFlags;
        str += " - " + "parcelled: " + parcelled;
        str += " - " + "size: " + size;

        return str;
    }
}
