package org.thoughtcrime.securesms.registerService;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dd.CircularProgressButton;

import org.thoughtcrime.securesms.ApplicationContext;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.registerService.entity.ContactFromApi;

import androidx.annotation.IdRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinLoginFragment extends Fragment {

    public static JoinLoginFragment newInstance() {

        Bundle args = new Bundle();

        JoinLoginFragment fragment = new JoinLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText etUserName;
    private EditText etUserSurname;
    private EditText etUserEmail;
    private CircularProgressButton joinButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_join_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etUserName = view.findViewById(R.id.et_user_name);
        etUserSurname = view.findViewById(R.id.et_user_surname);
        etUserEmail = view.findViewById(R.id.et_user_email);
        joinButton = view.findViewById(R.id.joinButton);
        joinButton.setOnClickListener(v -> checkEmptyLabelsAndRequest());

    }

    private void checkEmptyLabelsAndRequest() {
        if (TextUtils.isEmpty(etUserName.getText())) {
            etUserName.setError(getString(R.string.join_fragment_name_empty_error));
        } else if (TextUtils.isEmpty(etUserSurname.getText())){
            etUserSurname.setError(getString(R.string.join_fragment_surname_empty_error));
        }else if (TextUtils.isEmpty(etUserEmail.getText())){
            etUserEmail.setError(getString(R.string.join_fragment_email_empty_error));
        } else {
            joinRequest();
        }
    }

    private void joinRequest() {
        joinButton.setIndeterminateProgressMode(true);
        joinButton.setProgress(50);
        ContactFromApi contact = new ContactFromApi();
        contact.setName(etUserName.getText().toString());
        contact.setSurname(etUserSurname.getText().toString());
        contact.setEmail(etUserEmail.getText().toString());

        ApplicationContext.getInstance().getService().join(contact).enqueue(new Callback<ContactFromApi>() {
            @Override
            public void onResponse(Call<ContactFromApi> call, Response<ContactFromApi> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        changeFragment(JoinWaitFragment.newInstance(response.body()), R.id.fragmentContainer);
                        ApplicationContext.getInstance().setToken(response.body().getToken());
                    }
                } else {
                    joinButton.setIndeterminateProgressMode(false);
                    joinButton.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ContactFromApi> call, Throwable t) {
                joinButton.setIndeterminateProgressMode(false);
                joinButton.setProgress(0);
            }
        });
    }

    public void changeFragment(Fragment fragment, @IdRes int containerId) {
        getFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .commitNow();
    }
}
