package org.thoughtcrime.securesms.registerService;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.thoughtcrime.securesms.R;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

public class JoinActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        changeFragment(JoinLoginFragment.newInstance(), R.id.fragmentContainer);
    }

    public void changeFragment(Fragment fragment, @IdRes int containerId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .commitNow();
    }
}
