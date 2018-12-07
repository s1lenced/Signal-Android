package org.thoughtcrime.securesms.registerService;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.thoughtcrime.securesms.ApplicationContext;
import org.thoughtcrime.securesms.ConversationListActivity;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.crypto.IdentityKeyUtil;
import org.thoughtcrime.securesms.crypto.PreKeyUtil;
import org.thoughtcrime.securesms.crypto.SessionUtil;
import org.thoughtcrime.securesms.crypto.UnidentifiedAccessUtil;
import org.thoughtcrime.securesms.database.Address;
import org.thoughtcrime.securesms.database.DatabaseFactory;
import org.thoughtcrime.securesms.database.IdentityDatabase;
import org.thoughtcrime.securesms.jobs.DirectoryRefreshJob;
import org.thoughtcrime.securesms.jobs.GcmRefreshJob;
import org.thoughtcrime.securesms.jobs.RotateCertificateJob;
import org.thoughtcrime.securesms.logging.Log;
import org.thoughtcrime.securesms.push.AccountManagerFactory;
import org.thoughtcrime.securesms.registerService.entity.ContactFromApi;
import org.thoughtcrime.securesms.service.DirectoryRefreshListener;
import org.thoughtcrime.securesms.service.RotateSignedPreKeyListener;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.thoughtcrime.securesms.util.Util;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.internal.push.LockedException;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinWaitFragment extends Fragment {
    public static final String TAG = JoinWaitFragment.class.getName();
    public static final String JOIN_WAIT_CONTACT = "join_wait_contact";

    private SignalServiceAccountManager accountManager;
    private RegistrationState registrationState;

    private CircularProgressButton refreshButton;

    private ContactFromApi contact;

    public static JoinWaitFragment newInstance(ContactFromApi contact) {

        Bundle args = new Bundle();
        args.putSerializable(JOIN_WAIT_CONTACT, contact);
        JoinWaitFragment fragment = new JoinWaitFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contact = (ContactFromApi) getArguments().getSerializable(JOIN_WAIT_CONTACT);
        return inflater.inflate(R.layout.fragment_join_wait, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshButton = view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> checkJoin());
    }

    private void checkJoin() {
        refreshButton.setIndeterminateProgressMode(true);
        refreshButton.setProgress(50);

        ApplicationContext.getInstance().getService().singleUser(contact.getNumber()).enqueue(new Callback<ContactFromApi>() {
            @Override
            public void onResponse(Call<ContactFromApi> call, Response<ContactFromApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getRegStatus().equals("accepted")) {
                        handleRequestVerification(response.body().getNumber(), true);
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.join_wait_fragment_please_wait_message), Toast.LENGTH_LONG).show();
                        refreshButton.setIndeterminateProgressMode(false);
                        refreshButton.setProgress(0);
                    }
                } else {
                    refreshButton.setIndeterminateProgressMode(false);
                    refreshButton.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ContactFromApi> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                refreshButton.setIndeterminateProgressMode(false);
                refreshButton.setProgress(0);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void handleRequestVerification(@NonNull String e164number, boolean gcmSupported) {

        new AsyncTask<Void, Void, Pair<String, Optional<String>>>() {
            @Override
            protected @Nullable
            Pair<String, Optional<String>> doInBackground(Void... voids) {
                try {
                    String password = Util.getSecret(18);

                    Optional<String> gcmToken;

                    if (gcmSupported) {
                        gcmToken = Optional.of(GoogleCloudMessaging.getInstance(getContext()).register(GcmRefreshJob.REGISTRATION_ID));
                    } else {
                        gcmToken = Optional.absent();
                    }

                    accountManager = AccountManagerFactory.createManager(getContext(), e164number, password);
                    accountManager.requestSmsVerificationCode();

                    return new Pair<>(password, gcmToken);
                } catch (Throwable e) {
                    Log.w(TAG, "Error during account registration", e);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    registrationState = new RegistrationState(RegistrationState.State.VERIFYING, registrationState);
                    refreshButton.setIndeterminateProgressMode(false);
                    refreshButton.setProgress(0);
                    return null;
                }
            }

            protected void onPostExecute(@Nullable Pair<String, Optional<String>> result) {
                if (result == null) {
                    Toast.makeText(getContext(), R.string.RegistrationActivity_unable_to_connect_to_service, Toast.LENGTH_LONG).show();
                    refreshButton.setIndeterminateProgressMode(false);
                    refreshButton.setProgress(0);
                    return;
                }

                registrationState = new RegistrationState(RegistrationState.State.VERIFYING, e164number, result.first, result.second);
                verifySignalPin("101202");
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressLint("StaticFieldLeak")
    private void verifySignalPin(String code) {
        new AsyncTask<Void, Void, Pair<Integer, Long>>() {
            @Override
            protected Pair<Integer, Long> doInBackground(Void... voids) {
                try {
                    verifyAccount(code, null);
                    return new Pair<>(1, -1L);
                } catch (LockedException e) {
                    Log.w(TAG, e);
                    return new Pair<>(2, e.getTimeRemaining());
                } catch (IOException e) {
                    Log.w(TAG, e);
                    return new Pair<>(3, -1L);
                }
            }

            @Override
            protected void onPostExecute(Pair<Integer, Long> result) {
                if (result.first == 1) {
                    handleSuccessfulRegistration();
                } else if (result.first == 2) {
                    registrationState = new RegistrationState(RegistrationState.State.PIN, registrationState);
                    refreshButton.setIndeterminateProgressMode(false);
                    refreshButton.setProgress(0);
                } else {
                    registrationState = new RegistrationState(RegistrationState.State.VERIFYING, registrationState);
                    refreshButton.setIndeterminateProgressMode(false);
                    refreshButton.setProgress(0);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void handleSuccessfulRegistration() {
        ApplicationContext.getInstance(getActivity()).getJobManager().add(new DirectoryRefreshJob(getActivity(), false));
        ApplicationContext.getInstance(getActivity()).getJobManager().add(new RotateCertificateJob(getActivity()));

        DirectoryRefreshListener.schedule(getActivity());
        RotateSignedPreKeyListener.schedule(getActivity());

        Intent nextIntent = getActivity().getIntent().getParcelableExtra("next_intent");

        if (nextIntent == null) {
            nextIntent = new Intent(getActivity(), ConversationListActivity.class);
        }

        startActivity(nextIntent);
        getActivity().finish();
    }

    private void verifyAccount(@NonNull String code, @Nullable String pin) throws IOException {
        int registrationId = KeyHelper.generateRegistrationId(false);
        byte[] unidentifiedAccessKey = UnidentifiedAccessUtil.getSelfUnidentifiedAccessKey(getContext());
        boolean universalUnidentifiedAccess = TextSecurePreferences.isUniversalUnidentifiedAccess(getContext());

        TextSecurePreferences.setLocalRegistrationId(getContext(), registrationId);
        SessionUtil.archiveAllSessions(getContext());

        String signalingKey = Util.getSecret(52);

        accountManager.verifyAccountWithCode(code, signalingKey, registrationId, !registrationState.gcmToken.isPresent(), pin,
                unidentifiedAccessKey, universalUnidentifiedAccess);

        IdentityKeyPair identityKey = IdentityKeyUtil.getIdentityKeyPair(getContext());
        List<PreKeyRecord> records = PreKeyUtil.generatePreKeys(getContext());
        SignedPreKeyRecord signedPreKey = PreKeyUtil.generateSignedPreKey(getContext(), identityKey, true);

        accountManager.setPreKeys(identityKey.getPublicKey(), signedPreKey, records);

        if (registrationState.gcmToken.isPresent()) {
            accountManager.setGcmId(registrationState.gcmToken);
        }

        TextSecurePreferences.setGcmRegistrationId(getContext(), registrationState.gcmToken.orNull());
        TextSecurePreferences.setGcmDisabled(getContext(), !registrationState.gcmToken.isPresent());
        TextSecurePreferences.setWebsocketRegistered(getContext(), true);

        DatabaseFactory.getIdentityDatabase(getContext())
                .saveIdentity(Address.fromSerialized(registrationState.e164number),
                        identityKey.getPublicKey(), IdentityDatabase.VerifiedStatus.VERIFIED,
                        true, System.currentTimeMillis(), true);

        TextSecurePreferences.setVerifying(getContext(), false);
        TextSecurePreferences.setPushRegistered(getContext(), true);
        TextSecurePreferences.setLocalNumber(getContext(), registrationState.e164number);
        TextSecurePreferences.setPushServerPassword(getContext(), registrationState.password);
        TextSecurePreferences.setSignalingKey(getContext(), signalingKey);
        TextSecurePreferences.setSignedPreKeyRegistered(getContext(), true);
        TextSecurePreferences.setPromptedPushRegistration(getContext(), true);
        TextSecurePreferences.setUnauthorizedReceived(getContext(), false);
    }

    private static class RegistrationState {
        private enum State {
            INITIAL, VERIFYING, CHECKING, PIN
        }

        private final JoinWaitFragment.RegistrationState.State state;
        private final String e164number;
        private final String password;
        private final Optional<String> gcmToken;

        RegistrationState(JoinWaitFragment.RegistrationState.State state, String e164number, String password, Optional<String> gcmToken) {
            this.state = state;
            this.e164number = e164number;
            this.password = password;
            this.gcmToken = gcmToken;
        }

        RegistrationState(JoinWaitFragment.RegistrationState.State state, JoinWaitFragment.RegistrationState previous) {
            this.state = state;
            this.e164number = previous.e164number;
            this.password = previous.password;
            this.gcmToken = previous.gcmToken;
        }
    }
}
