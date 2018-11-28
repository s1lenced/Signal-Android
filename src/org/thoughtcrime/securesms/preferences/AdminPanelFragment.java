package org.thoughtcrime.securesms.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.thoughtcrime.securesms.ApplicationContext;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.components.registration.PulsingFloatingActionButton;
import org.thoughtcrime.securesms.preferences.adapter.UsersAdapter;

public class AdminPanelFragment extends Fragment {

    private RecyclerView rvUsers;
    private UsersAdapter mUsersAdapter;
    private PulsingFloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_panel_pef, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUsers = view.findViewById(R.id.recycler_view_admin_panel);
        fab = view.findViewById(R.id.fab);
        fab.startPulse(3 * 1000);
        mUsersAdapter = new UsersAdapter();
        mUsersAdapter.setContacts(ApplicationContext.getContactsFromApi());
        mUsersAdapter.setListener(contact -> {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            intent.putExtra(UserActivity.USER_EXTRA, contact);
            startActivity(intent);
        });
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(mUsersAdapter);

        fab.setOnClickListener(v -> startActivity(new Intent(getActivity(), UserActivity.class)));
    }

    @Override
    public void onPause() {
        super.onPause();
        fab.stopPulse();
    }
}
