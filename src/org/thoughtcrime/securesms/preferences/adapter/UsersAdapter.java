package org.thoughtcrime.securesms.preferences.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.registerService.entity.ContactFromApi;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<ContactFromApi> contacts = new ArrayList<>();
    private UserClickListener listener;

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        userViewHolder.bind(contacts.get(i));
    }

    @Override
    public int getItemCount() {
        if (contacts == null) return 0;
        else return contacts.size();
    }

    public List<ContactFromApi> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactFromApi> contacts) {
        this.contacts = contacts;
    }

    public void setListener(UserClickListener listener) {
        this.listener = listener;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvStatus = itemView.findViewById(R.id.tv_user_status);
        }

        public void bind(ContactFromApi contact) {
            tvName.setText(contact.getName());
            tvStatus.setText(contact.getStatus());
            itemView.setOnClickListener(v -> listener.onUserClick(contact));
        }
    }

    public interface UserClickListener{
        void onUserClick(ContactFromApi contact);
    }
}
