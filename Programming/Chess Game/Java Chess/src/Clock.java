
public class Clock {

    private int hour;
    private int min;
    private int sec;

    public Clock(int hh, int mm, int ss) {
        hour = hh;
        min = mm;
        sec = ss;
    }

    public boolean outOfTime() {
        return (hour == 0 && min == 0 && sec == 0);
    }

    public void decr() {
        if (min == 0 && sec == 0) {
            sec = 59;
            min = 59;
            hour--;
        } else if (sec == 0) {
            sec = 59;
            min--;
        } else {
            sec--;
        }
    }

    public String getTime() {
        String fHrs = String.format("%02d", hour);
        String fMins = String.format("%02d", min);
        String fSecs = String.format("%02d", sec);
        String fTime = fHrs + ":" + fMins + ":" + fSecs;
        return fTime;
    }
}
