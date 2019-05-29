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
    
    public void setGroupRecycler(Query query, int recyclerViewId) {
        FirestoreRecyclerOptions<DataHolder.GroupHolder> options = new FirestoreRecyclerOptions.Builder<DataHolder.GroupHolder>().setQuery(query, DataHolder.GroupHolder.class).build();

        RecyclerView recyclerView = activity.findViewById(recyclerViewId);
        DataAdaptor.GroupAdaptor inboxAdaptor = new DataAdaptor.GroupAdaptor(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(inboxAdaptor);
        inboxAdaptor.startListening();
    }

    private void pin(String msg) {
        Log.d("ddddd", msg);
    }

}