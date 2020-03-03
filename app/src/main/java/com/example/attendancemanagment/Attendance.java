package com.example.attendancemanagment;

public class Attendance {

    private String Id;
    private String attendance;

    private  Attendance(){}

    private Attendance(String Id,String attendance){
        this.Id = Id;
        this.attendance = attendance;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
