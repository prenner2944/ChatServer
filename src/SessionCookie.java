/**
 * Created by paulrenner on 10/29/15.
 */
public class SessionCookie {
    private long id;
    private long timeOfActivity;
    public static int timeoutLength = 300;

    public SessionCookie(long i) {
        id = i;
        timeOfActivity = System.currentTimeMillis();
    }

    public boolean hasTimeout() {
        long elapsedTime = System.currentTimeMillis() - timeOfActivity;
        if (elapsedTime > timeoutLength * 1000)
            return true;
        return false;
    }

    public void updateTimeOfActivty() {
        timeOfActivity = System.currentTimeMillis();
    }
    public long getID() {
        return id;
    }
}
