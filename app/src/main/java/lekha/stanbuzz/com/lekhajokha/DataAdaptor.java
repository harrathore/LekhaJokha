package lekha.stanbuzz.com.lekhajokha;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class DataAdaptor {

    public static class GroupAdaptor extends FirestoreRecyclerAdapter<DataHolder.GroupHolder, GroupAdaptor.GroupHolderHolder> {

        public GroupAdaptor(@NonNull FirestoreRecyclerOptions<DataHolder.GroupHolder> options) {
            super(options);
        }
        private OnItemClickListener listener;

        @Override
        protected void onBindViewHolder(@NonNull final GroupHolderHolder groupCardHolder, int i, @NonNull DataHolder.GroupHolder groupCard) {
            groupCardHolder.title.setText(groupCard.getTitle());
        }

        @NonNull
        @Override
        public GroupHolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_group_view, parent, false);
            return new GroupHolderHolder(v);
        }

        public class GroupHolderHolder extends RecyclerView.ViewHolder {
            private TextView title;

            public GroupHolderHolder(@NonNull final View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
            }
        }

        public interface OnItemClickListener {
            void onItemClick(DocumentSnapshot documentSnapshot, int pos);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }

    private static void pin(String m) {
        try {
            Log.d("ADPX", m);
        } catch (NullPointerException ex) {
            Log.d("ADPX", "Null pointer found");
        }
    }
}