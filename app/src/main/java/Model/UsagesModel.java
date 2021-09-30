package Model;

public class UsagesModel {

    String Id;
    String deviceid;
    String starttime;
    String endtime;
    long totalsec;
    String currentdate;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public long getTotalsec() {
        return totalsec;
    }

    public void setTotalsec(long totalsec) {
        this.totalsec = totalsec;
    }

    public String getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(String currentdate) {
        this.currentdate = currentdate;
    }
}
