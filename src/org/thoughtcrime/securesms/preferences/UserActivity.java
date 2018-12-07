package org.thoughtcrime.securesms.preferences;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import org.thoughtcrime.securesms.ApplicationContext;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.registerService.entity.ContactFromApi;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    public static final String USER_EXTRA = "add_user";

    private EditText etUserName;
    private EditText etUserSurname;
    private EditText etUserEmail;
    private Spinner spinnerUserRole;
    private TextView tvIdentityCode;
    private String role;
    private CircularProgressButton addUserButton;
    private CircularProgressButton finishButton;

    private ContactFromApi contact;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        contact = (ContactFromApi) getIntent().getSerializableExtra(USER_EXTRA);

        etUserName = findViewById(R.id.et_user_name);
        etUserSurname = findViewById(R.id.et_user_surname);
        etUserEmail = findViewById(R.id.et_user_email);
        tvIdentityCode = findViewById(R.id.tv_identity_code);
        spinnerUserRole = findViewById(R.id.spinner_user);
        addUserButton = findViewById(R.id.userButton);
        finishButton = findViewById(R.id.userButtonFinish);

        disableEditingContentIfUpdateCase();

        addUserButton.setOnClickListener(v -> {
            if (contact == null) {
                addUser();
            } else {
                updateUser();
            }
        });
        finishButton.setOnClickListener(v -> finishUserAdding());

        initSpinner();
    }

    private void updateUser() {
        addUserButton.setIndeterminateProgressMode(true);
        addUserButton.setProgress(50);

        ContactFromApi updateContact = new ContactFromApi();
        updateContact.setRole(spinnerUserRole.getSelectedItem().toString().toLowerCase());

        ApplicationContext.getInstance().getService().updateUser(updateContact, contact.getNumber()).enqueue(new Callback<ContactFromApi>() {
            @Override
            public void onResponse(Call<ContactFromApi> call, Response<ContactFromApi> response) {
                if (response.body() != null) {
                    tvIdentityCode.setVisibility(View.VISIBLE);
                    tvIdentityCode.setText(response.body().getVerificationCode());
                    loadContactsForRefresh();
                    Toast.makeText(UserActivity.this, getResources().getString(R.string.user_activity_user_update_successfully), Toast.LENGTH_SHORT).show();

                    addUserButton.setClickable(false);
                    addUserButton.setVisibility(View.GONE);
                    finishButton.setVisibility(View.VISIBLE);
                } else {
                    addUserButton.setIndeterminateProgressMode(false);
                    addUserButton.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ContactFromApi> call, Throwable t) {
                addUserButton.setIndeterminateProgressMode(false);
                addUserButton.setProgress(0);
                Toast.makeText(UserActivity.this, getResources().getString(R.string.user_activity_user_update_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableEditingContentIfUpdateCase() {
        if (contact != null) {
            /*String[] nameAndSurname = contact.getName().split(" ");*/
            etUserName.setText(contact.getName());
            etUserSurname.setText(contact.getSurname());
            etUserEmail.setText(contact.getEmail());

            disableEditText(etUserName);
            disableEditText(etUserSurname);
            disableEditText(etUserEmail);
            addUserButton.setText(getResources().getString(R.string.user_activity_add_button_update));
        } else {
            addUserButton.setText(getResources().getString(R.string.user_activity_add_user_to_server));
        }
    }

    private void initSpinner() {
        List<String> roles = Arrays.asList(getResources().getStringArray(R.array.role_keys));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_list_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerUserRole.setAdapter(adapter);
        spinnerUserRole.setSelection(getSelectionItem(roles));
        role = spinnerUserRole.getSelectedItem().toString();
    }

    private int getSelectionItem(List<String> rolesArray) {
        if (contact != null) {
            for (int i = 0; i < rolesArray.size(); i++) {
                if (contact.getRole().toLowerCase().equals(rolesArray.get(i).toLowerCase())) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void addUser() {
        addUserButton.setIndeterminateProgressMode(true);
        addUserButton.setProgress(50);
        ContactFromApi contact = new ContactFromApi();
        if (TextUtils.isEmpty(etUserName.getText())) {
            etUserName.setError("Please, write your Name");
            addUserButton.setIndeterminateProgressMode(false);
            addUserButton.setProgress(0);
        } else if (TextUtils.isEmpty(etUserSurname.getText())) {
            etUserSurname.setError("Please, write your Surname");
            addUserButton.setIndeterminateProgressMode(false);
            addUserButton.setProgress(0);
        } else if (TextUtils.isEmpty(etUserEmail.getText())) {
            etUserEmail.setError("Please, write your eMail");
            addUserButton.setIndeterminateProgressMode(false);
            addUserButton.setProgress(0);
        } else {
            contact.setName(etUserName.getText().toString());
            contact.setSurname(etUserSurname.getText().toString());
            contact.setEmail(etUserEmail.getText().toString());

            ApplicationContext.getInstance().getService().addUser(contact).enqueue(new Callback<ContactFromApi>() {
                @Override
                public void onResponse(Call<ContactFromApi> call, Response<ContactFromApi> response) {
                    if (response.body() != null) {
                        tvIdentityCode.setVisibility(View.VISIBLE);
                        tvIdentityCode.setText(response.body().getNumber());
                        loadContactsForRefresh();
                        Toast.makeText(UserActivity.this, getResources().getString(R.string.user_activity_user_added_successfully), Toast.LENGTH_SHORT).show();

                        addUserButton.setClickable(false);
                        finishButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ContactFromApi> call, Throwable t) {
                    addUserButton.setIndeterminateProgressMode(false);
                    addUserButton.setProgress(0);
                    Toast.makeText(UserActivity.this, getResources().getString(R.string.user_activity_user_added_fail), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadContactsForRefresh() {
        ApplicationContext.getInstance().getService().loadAllContacts().enqueue(new Callback<List<ContactFromApi>>() {
            @Override
            public void onResponse(Call<List<ContactFromApi>> call, Response<List<ContactFromApi>> response) {
                addUserButton.setIndeterminateProgressMode(false);
                addUserButton.setProgress(0);
                if (response.body() != null) {
                    ApplicationContext.setContactsFromApi(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ContactFromApi>> call, Throwable t) {
                addUserButton.setIndeterminateProgressMode(false);
                addUserButton.setProgress(0);
                Toast.makeText(UserActivity.this, getResources().getString(R.string.user_activity_users_update_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void finishUserAdding() {
        setResult(RESULT_OK);
        UserActivity.this.finish();
    }
}
