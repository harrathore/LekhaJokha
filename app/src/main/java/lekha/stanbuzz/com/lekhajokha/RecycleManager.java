package lekha.stanbuzz.com.lekhajokha;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class RecycleManager {
    private Activity activity;

    public RecycleManager(Activity a) {
        activity = a;
    }

    public void setChatRecycler(Query query, int recyclerViewId){
        FirestoreRecyclerOptions<DataHolder.ChatHolder> options = new FirestoreRecyclerOptions.Builder<DataHolder.ChatHolder>().setQuery(query, DataHolder.ChatHolder.class).build();
        RecyclerView recyclerView = activity.findViewById(recyclerViewId);
        DataAdaptor.ChatAdaptor chatAdaptor = new DataAdaptor.ChatAdaptor(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(chatAdaptor);
        chatAdaptor.startListening();
    }

    public void setGroupRecycler(Query query, int recyclerViewId){
        FirestoreRecyclerOptions<DataHolder.GroupHolder> options = new FirestoreRecyclerOptions.Builder<DataHolder.GroupHolder>().setQuery(query, DataHolder.GroupHolder.class).build();
        RecyclerView recyclerView = activity.findViewById(recyclerViewId);
        DataAdaptor.GroupAdaptor groupAdaptor = new DataAdaptor.GroupAdaptor(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(groupAdaptor);
        groupAdaptor.startListening();
    }

    private void pin(String msg) {
        Log.d("ddddd", msg);
    }

}