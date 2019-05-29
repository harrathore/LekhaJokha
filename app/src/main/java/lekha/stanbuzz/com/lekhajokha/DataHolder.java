package lekha.stanbuzz.com.lekhajokha;

public class DataHolder {
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