package org.thoughtcrime.securesms.preferences.adapter;

import org.thoughtcrime.securesms.registerService.entity.ContactFromApi;

import androidx.annotation.NonNull;

public class EventItem extends ListItem {

    @NonNull
    private ContactFromApi contact;

    public EventItem(@NonNull ContactFromApi contact) {
        this.contact = contact;
    }

    @Override
    public int getType() {
        return TYPE_ITEM;
    }

    @NonNull
    public ContactFromApi getContact() {
        return contact;
    }
}
