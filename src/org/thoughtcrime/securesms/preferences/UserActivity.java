package org.thoughtcrime.securesms.preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.dd.CircularProgressButton;

import org.thoughtcrime.securesms.R;

import java.util.Arrays;

import androidx.annotation.Nullable;

public class UserActivity extends AppCompatActivity {

    public static final String USER_EXTRA = "add_user";

    private EditText etUserName;
    private EditText etUserSurname;
    private EditText etUserEmail;
    private Spinner spinnerUserRole;
    private String role;
    private CircularProgressButton addUserButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        etUserName = findViewById(R.id.et_user_name);
        etUserSurname = findViewById(R.id.et_user_surname);
        etUserEmail = findViewById(R.id.et_user_email);
        spinnerUserRole = findViewById(R.id.spinner_user);
        addUserButton = findViewById(R.id.userButton);
        initSpinner();
    }

    private void initSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_list_item, Arrays.asList(getResources().getStringArray(R.array.role_keys)));
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerUserRole.setAdapter(adapter);
        spinnerUserRole.setSelection(0);
        role = spinnerUserRole.getSelectedItem().toString();
    }

    private void addUser() {

    }
}
