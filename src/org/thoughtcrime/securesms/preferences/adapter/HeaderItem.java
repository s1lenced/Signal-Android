package org.thoughtcrime.securesms.preferences.adapter;

import androidx.annotation.NonNull;

public class HeaderItem extends ListItem {

    @NonNull
    private String status;

    public HeaderItem(@NonNull String status) {
        this.status = status;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}
