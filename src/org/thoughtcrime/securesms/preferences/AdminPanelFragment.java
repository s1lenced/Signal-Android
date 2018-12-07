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
import org.thoughtcrime.securesms.ApplicationPreferencesActivity;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.components.registration.PulsingFloatingActionButton;
import org.thoughtcrime.securesms.preferences.adapter.EventItem;
import org.thoughtcrime.securesms.preferences.adapter.HeaderItem;
import org.thoughtcrime.securesms.preferences.adapter.ListItem;
import org.thoughtcrime.securesms.preferences.adapter.UsersAdapter;
import org.thoughtcrime.securesms.registerService.entity.ContactFromApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static android.app.Activity.RESULT_OK;

public class AdminPanelFragment extends Fragment {

    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_UPDATE = 0;


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

        initAdapter();

        fab.setOnClickListener(v -> startActivityForResult(new Intent(getActivity(), UserActivity.class), REQUEST_CODE_ADD));
    }

    @Override
    public void onResume() {
        super.onResume();

        ((ApplicationPreferencesActivity) getActivity()).getSupportActionBar().setTitle(R.string.preferences__admin_panel);
    }

    private void initAdapter() {
        mUsersAdapter = new UsersAdapter();
        mUsersAdapter.setItems(getAdapterItems());
        mUsersAdapter.setListener(contact -> {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            intent.putExtra(UserActivity.USER_EXTRA, contact);
            startActivityForResult(intent, REQUEST_CODE_UPDATE);
        });
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(mUsersAdapter);
    }

    private List<ListItem> getAdapterItems() {
        List<ContactFromApi> contacts = ApplicationContext.getContactsFromApi();
        List<ContactFromApi> filteredContacts = new ArrayList<>();
        for (ContactFromApi contact : contacts) {
            if (!contact.getRegStatus().toLowerCase().equals("permitted".toLowerCase()) && !contact.getRegStatus().toLowerCase().equals("pending".toLowerCase())) {
                filteredContacts.add(contact);
            }
        }
        @NonNull
        List<ListItem> adapterItems = new ArrayList<>();

        Map<String, List<ContactFromApi>> groupContacts = toMap(filteredContacts);
        for (String status: groupContacts.keySet()) {
            HeaderItem header = new HeaderItem(status);
            adapterItems.add(header);
            for (ContactFromApi contact: Objects.requireNonNull(groupContacts.get(status))) {
                EventItem item = new EventItem(contact);
                adapterItems.add(item);
            }
        }
        return adapterItems;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD:
                    mUsersAdapter.setItems(getAdapterItems());
                    mUsersAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_UPDATE:
                    mUsersAdapter.setItems(getAdapterItems());
                    mUsersAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fab.stopPulse();
    }

    @NonNull
    private Map<String, List<ContactFromApi>> toMap(@NonNull List<ContactFromApi> contacts) {
        Map<String, List<ContactFromApi>> map = new TreeMap<>();
        for (ContactFromApi event : contacts) {
            List<ContactFromApi> value = map.get(event.getRegStatus());
            if (value == null) {
                value = new ArrayList<>();
                map.put(event.getRegStatus(), value);
            }
            value.add(event);
        }
        return map;
    }
}
