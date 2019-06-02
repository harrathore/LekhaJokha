package lekha.stanbuzz.com.lekhajokha;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class DataHolder {

    public static  class ChatHolder{
        private Long amt;
        private Date date;
        private String msg, type, name;
        private DocumentReference sid, userId;

        //public ChatHolder() {
        //}

        public ChatHolder(Long amt, Date date, String msg, String type, String name, DocumentReference sid, DocumentReference userId) {
            this.amt = amt;
            this.date = date;
            this.msg = msg;
            this.type = type;
            this.name = name;
            this.sid = sid;
            this.userId = userId;
        }

        public Long getAmt() {
            return amt;
        }

        public Date getDate() {
            return date;
        }

        public String getMsg() {
            return msg;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public DocumentReference getSid() {
            return sid;
        }

        public DocumentReference getUserId() {
            return userId;
        }
    }

    public static class ContactHolder {
        private String ContactImage;
        private String ContactName;
        private String ContactNumber;

        public String getContactImage() {
            return ContactImage;
        }

        public void setContactImage(String contactImage) {
            this.ContactImage = ContactImage;
        }

        public String getContactName() {
            return ContactName;
        }

        public void setContactName(String contactName) {
            ContactName = contactName;
        }

        public String getContactNumber() {
            return ContactNumber;
        }

        public void setContactNumber(String contactNumber) {
            ContactNumber = contactNumber;
        }
    }

    public static class GroupHolder {
        private String title;

        public GroupHolder() {
        }

        public GroupHolder(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}