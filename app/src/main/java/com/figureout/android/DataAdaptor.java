package com.figureout.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class DataAdaptor {

    public static class ReportAdaptor extends FirestoreRecyclerAdapter<DataHolder.ReportHolder, ReportAdaptor.ReportViewHolder> {
        private Long avg;

        public void setAvg(Long avg) {
            this.avg = avg;
        }

        public ReportAdaptor(@NonNull FirestoreRecyclerOptions<DataHolder.ReportHolder> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull ReportViewHolder holder, int position, @NonNull DataHolder.ReportHolder reportHolder) {
            holder.name.setText(reportHolder.getName());
            Long invest = reportHolder.getInvested(),
                 due = invest-avg;
            holder.invest.setText("Invested : ₹"+invest.toString());
            if(due>0) {
                holder.due.setText("+₹"+Math.abs(due));
                holder.due.setTextColor(holder.due.getContext().getResources().getColor(R.color.green));
            } else {
                holder.due.setText("- ₹"+Math.abs(due));
                holder.due.setTextColor(holder.due.getContext().getResources().getColor(R.color.red));
            }
        }

        private OnItemClickListener listener;

        @NonNull
        @Override
        public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_member_report, parent, false);
            return new ReportViewHolder(v);
        }

        public static class ReportViewHolder extends RecyclerView.ViewHolder{
            private TextView name, invest, due;

            public ReportViewHolder(final View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.name);
                invest = (TextView) itemView.findViewById(R.id.invest);
                due = (TextView) itemView.findViewById(R.id.due);
            }
        }

        public interface OnItemClickListener {
            void onItemClick(DocumentSnapshot documentSnapshot, int pos);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }

    public static class ChatAdaptor extends FirestoreRecyclerAdapter<DataHolder.ChatHolder, ChatAdaptor.ChatViewHolder> {
        private SessionMang sessionMang;
        private String currentSid;

        public void setCurrentSid(String currentSid) {
            this.currentSid = currentSid;
        }

        public ChatAdaptor(@NonNull FirestoreRecyclerOptions<DataHolder.ChatHolder> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull DataHolder.ChatHolder chatHolder) {
            if(sessionMang==null) {
                sessionMang = SessionMang.getInstance((Activity) holder.itemView.getContext());
            }

            holder.name1.setText(chatHolder.getName());
            holder.name2.setText(chatHolder.getName());
            holder.name3.setText(chatHolder.getName());
            holder.name4.setText(chatHolder.getName());

            if(chatHolder.getAmt()!=null) {
                holder.amt1.setText(chatHolder.getAmt().toString());
                holder.amt2.setText(chatHolder.getAmt().toString());

                holder.note1.setText(chatHolder.getMsg());
                holder.note2.setText(chatHolder.getMsg());
            } else {
                holder.msg1.setText(chatHolder.getMsg());
                holder.msg2.setText(chatHolder.getMsg());
            }


            holder.date1.setText(FireStoreDB.PrettyTime(chatHolder.getDate()));
            holder.date2.setText(FireStoreDB.PrettyTime(chatHolder.getDate()));

            holder.card1.setVisibility(View.GONE);
            holder.card2.setVisibility(View.GONE);
            holder.card3.setVisibility(View.GONE);
            holder.card4.setVisibility(View.GONE);

            if(chatHolder.getType().equals("MSG")) {
                if(isMyMsg(chatHolder.getUserId())) {
                    holder.card2.setVisibility(View.VISIBLE);
                } else {
                    holder.card1.setVisibility(View.VISIBLE);
                }
            } else if(chatHolder.getType().equals("TRANS")) {
                if(isMyMsg(chatHolder.getUserId())) {
                    holder.card4.setVisibility(View.VISIBLE);
                } else {
                    holder.card3.setVisibility(View.VISIBLE);
                }
            }

            if(chatHolder.getSid().getId().equals(currentSid)) {
                holder.card1.setAlpha(1);
                holder.card2.setAlpha(1);
                holder.card3.setAlpha(1);
                holder.card4.setAlpha(1);
            } else {
                holder.card1.setAlpha(0.3f);
                holder.card2.setAlpha(0.3f);
                holder.card3.setAlpha(0.3f);
                holder.card4.setAlpha(0.3f);
            }
            pin(chatHolder.getSid().getId()+" -- "+currentSid);
        }

        private Boolean isMyMsg(DocumentReference userRef) {
            if(sessionMang.getUserId().equals(userRef.getId())) {
                return true;
            }
            return false;
        }

        private OnItemClickListener listener;

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message_view, parent, false);
            return new ChatViewHolder(v);
        }

        public static class ChatViewHolder extends RecyclerView.ViewHolder{
            private TextView name1,name2, name3, name4,
                    msg1, msg2,
                    amt1, amt2,
                    note1, note2,
                    date1, date2;
            private View card1,card2,card3,card4;

            public ChatViewHolder(final View itemView) {
                super(itemView);

                name1 = (TextView) itemView.findViewById(R.id.name1);
                name2 = (TextView) itemView.findViewById(R.id.name2);
                name3 = (TextView) itemView.findViewById(R.id.name3);
                name4 = (TextView) itemView.findViewById(R.id.name4);
                msg1 = (TextView) itemView.findViewById(R.id.msg1);
                msg2 = (TextView) itemView.findViewById(R.id.msg2);
                amt1 = (TextView) itemView.findViewById(R.id.amt1);
                amt2 = (TextView) itemView.findViewById(R.id.amt2);
                note1 = (TextView) itemView.findViewById(R.id.note1);
                note2 = (TextView) itemView.findViewById(R.id.note2);
                date1 = (TextView) itemView.findViewById(R.id.date1);
                date2 = (TextView) itemView.findViewById(R.id.date2);

                card1 = (View) itemView.findViewById(R.id.cardView1);
                card2 = (View) itemView.findViewById(R.id.cardView2);
                card3 = (View) itemView.findViewById(R.id.cardView3);
                card4 = (View) itemView.findViewById(R.id.cardView4);
            }
        }

        public interface OnItemClickListener {
            void onItemClick(DocumentSnapshot documentSnapshot, int pos);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }

    public static class GroupAdaptor extends FirestoreRecyclerAdapter<DataHolder.GroupHolder, GroupAdaptor.GroupHolder> {

        public GroupAdaptor(@NonNull FirestoreRecyclerOptions<DataHolder.GroupHolder> options) {
            super(options);
        }
        private OnItemClickListener listener;

        @Override
        protected void onBindViewHolder(@NonNull final GroupHolder groupCardHolder, int i, @NonNull final DataHolder.GroupHolder groupCard) {
            groupCardHolder.title.setText(groupCard.getTitle());

            groupCardHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snap = getSnapshots().getSnapshot(groupCardHolder.getAdapterPosition());

                    Intent chat = new Intent(groupCardHolder.itemView.getContext(), Chat.class);
                    chat.putExtra("title", groupCard.getTitle());        //to put group title in the chat page
                    chat.putExtra("gid", snap.getId());
                    groupCardHolder.itemView.getContext().startActivity(chat);
                }
            });

        }

        @NonNull
        @Override
        public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_group_view, parent, false);
            return new GroupHolder(v);
        }

        public class GroupHolder extends RecyclerView.ViewHolder {
            private TextView title;

            public GroupHolder(@NonNull final View itemView) {
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

    public static class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ContactViewHolder>{
        private List<DataHolder.ContactHolder> contactVOList;
        private Context mContext;
        private String phone;
        private static MemberChangeListener memberChangeListener;

        public AllContactsAdapter(List<DataHolder.ContactHolder> contactVOList, Context mContext){
            this.contactVOList = contactVOList;
            this.mContext = mContext;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_contact_view, null);
            ContactViewHolder contactViewHolder = new ContactViewHolder(view);
            return contactViewHolder;
        }

        @Override
        public void onBindViewHolder(final ContactViewHolder holder, int position) {
            DataHolder.ContactHolder contactVO = contactVOList.get(position);
            if(contactVO.getContactNumber()!=null && contactVO.getContactNumber().trim().length()>=10) {
                phone = contactVO.getContactNumber().replace(" ", "");
                phone = phone.substring(phone.length() - 10);
                holder.tvContactName.setText(contactVO.getContactName());
                holder.tvPhoneNumber.setText(contactVO.getContactNumber());
                holder.phone = phone;
            }
        }

        public void setMemberChangeListener(MemberChangeListener memberChangeListener) {
            this.memberChangeListener = memberChangeListener;
        }

        public interface MemberChangeListener {
            void onMemberChange(String phone, ImageView imageView);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return contactVOList.size();
        }

        public static class ContactViewHolder extends RecyclerView.ViewHolder{
            ImageView ivContactImage;
            TextView tvContactName;
            TextView tvPhoneNumber;
            String phone;

            public ContactViewHolder(final View itemView) {
                super(itemView);
                ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
                tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
                tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        if (getAdapterPosition()!=RecyclerView.NO_POSITION) {
                            memberChangeListener.onMemberChange(phone, ivContactImage);
                        }
                    }
                });

            }
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