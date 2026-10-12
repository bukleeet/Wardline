package model;

public class Ward {
        private int ward_id;
        private String floor;
        private int ward_no;
        private String w_status;

        public Ward(String floor, int wn, String status){
            this.floor = floor;
            ward_no = wn;
            w_status = status;
        }

        public Ward(int id)
        {
            ward_id = id;
        }

        public void setWard_id(int id)
        {
            ward_id = id;
        }

        public void setFloor(String f)
        {
            floor = f;
        }

        public void setWardNo(int no)
        {
            ward_no = no;
        }

        public void setStatus(String s)
        {
            w_status = s;
        }

        public int getWard_id() {
            return ward_id;
        }

        public String getFloor() {
            return floor;
        }

        public int getWardNo()
        {
            return ward_no;
        }

        public String getStatus()
        {
            return w_status;
        }



    }
