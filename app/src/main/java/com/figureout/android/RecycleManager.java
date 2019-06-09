package com.figureout.android;

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

    public void setChatRecycler(Query query, int recyclerViewId, String currSid){
        FirestoreRecyclerOptions<DataHolder.ChatHolder> options = new FirestoreRecyclerOptions.Builder<DataHolder.ChatHolder>().setQuery(query, DataHolder.ChatHolder.class).build();
        RecyclerView recyclerView = activity.findViewById(recyclerViewId);
        DataAdaptor.ChatAdaptor chatAdaptor = new DataAdaptor.ChatAdaptor(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        chatAdaptor.setCurrentSid(currSid);
        recyclerView.setAdapter(chatAdaptor);
        chatAdaptor.startListening();
    }

    public void setReportRecycler(Query query, int recyclerViewId, Long avg){
        FirestoreRecyclerOptions<DataHolder.ReportHolder> options = new FirestoreRecyclerOptions.Builder<DataHolder.ReportHolder>().setQuery(query, DataHolder.ReportHolder.class).build();
        RecyclerView recyclerView = activity.findViewById(recyclerViewId);
        DataAdaptor.ReportAdaptor reportAdaptor = new DataAdaptor.ReportAdaptor(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        reportAdaptor.setAvg(avg);
        recyclerView.setAdapter(reportAdaptor);
        reportAdaptor.startListening();
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