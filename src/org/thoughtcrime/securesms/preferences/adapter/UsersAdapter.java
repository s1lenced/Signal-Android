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
import java.util.Collections;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> items = Collections.emptyList();
    private UserClickListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case ListItem.TYPE_HEADER:
                return new StatusViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_status, viewGroup, false));
            case ListItem.TYPE_ITEM:
                return new UserViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, viewGroup, false));
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = getItemViewType(i);
        switch (viewType) {
            case ListItem.TYPE_HEADER: {
                HeaderItem header = (HeaderItem) items.get(i);
                StatusViewHolder holder = (StatusViewHolder) viewHolder;
                holder.bind(header.getStatus());
                break;
            }
            case ListItem.TYPE_ITEM: {
                EventItem event = (EventItem) items.get(i);
                UserViewHolder holder = (UserViewHolder) viewHolder;
                holder.bind(event.getContact());
                break;
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        else return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    public List<ListItem> getItems() {
        return items;
    }

    public void setItems(List<ListItem> items) {
        this.items = items;
    }

    public void setListener(UserClickListener listener) {
        this.listener = listener;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvSurName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvSurName = itemView.findViewById(R.id.tv_user_surname);
        }

        public void bind(ContactFromApi contact) {
            tvName.setText(contact.getName());
            tvSurName.setText(contact.getSurname());
            itemView.setOnClickListener(v -> listener.onUserClick(contact));
        }
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStatus;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(String status) {
            tvStatus.setText(status.toUpperCase());
        }
    }

    public interface UserClickListener {
        void onUserClick(ContactFromApi contact);
    }
}
