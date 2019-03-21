package com.enes.burdayim;

public class Functions {

    static class sendUsername {
        private String username;
        public sendUsername() { }
        public sendUsername(String username) { this.username = username; }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
    }

    static class sendQrCode {
        private String qrCode;
        public sendQrCode() { }
        public sendQrCode(String qrCode) {
            this.qrCode = qrCode;
        }
        public String getQrCode() {
            return qrCode;
        }
        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }
    }
    static class fillTable{
        private boolean start;
        public fillTable(){}

        public fillTable(boolean start) {
            this.start = start;
        }

        public boolean isStart() {
            return start;
        }

        public void setStart(boolean start) {
            this.start = start;
        }
    }

    static class sendList{
        private boolean send;
        private String LessonName;
        private String LessonTime;

        public sendList() {
        }

        public sendList(boolean send, String lessonName, String lessonTime) {
            this.send = send;
            LessonName = lessonName;
            LessonTime = lessonTime;
        }

        public boolean isSend() {
            return send;
        }

        public void setSend(boolean send) {
            this.send = send;
        }

        public String getLessonName() {
            return LessonName;
        }

        public void setLessonName(String lessonName) {
            LessonName = lessonName;
        }

        public String getLessonTime() {
            return LessonTime;
        }

        public void setLessonTime(String lessonTime) {
            LessonTime = lessonTime;
        }
    }

    static class clearCache{
        private boolean clear;

        public clearCache() {
        }

        public clearCache(boolean clear) {
            this.clear = clear;
        }

        public boolean isClear() {
            return clear;
        }

        public void setClear(boolean clear) {
            this.clear = clear;
        }
    }
}
