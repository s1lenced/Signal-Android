package org.thoughtcrime.securesms.preferences.adapter;

public abstract class ListItem {
    static final int TYPE_HEADER = 0;
    static final int TYPE_ITEM = 1;

    abstract public int getType();
}
