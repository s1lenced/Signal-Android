package org.thoughtcrime.securesms.registerService;

import android.content.Context;

import org.thoughtcrime.securesms.util.TextSecurePreferences;

public class AuthUtils {

    private Context mContext;

    public AuthUtils(Context context) {
        this.mContext = context;
    }

    public String getToken() {
        return TextSecurePreferences.getRegistrationToken(mContext);
    }

    public void setToken(String token) {
        TextSecurePreferences.setRegistrationToken(mContext, token);
    }
}
