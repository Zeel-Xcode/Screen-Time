package Model;

import java.io.Serializable;

public class UsageModel implements Serializable {

    private int hrs;
    private int mint;
    private int sec;

    public int getHrs(){
        return hrs;
    }

    public void setHrs(int hrs) {
        this.hrs = hrs;
    }

    public int getMint() {
        return mint;
    }

    public void setMint(int mint) {
        this.mint = mint;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

}
