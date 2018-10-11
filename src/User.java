/**
 * Created by paulrenner on 10/29/15.
 */
public class User {
    private String name;
    private String password;
    private SessionCookie cookie;

    public User(String name, String password, SessionCookie cookie) {
        this.name = name;
        this.password = password;
        this.cookie = cookie;
    }

    public String getName() {
        return name;
    }

    public boolean checkPassword(String password) {
        if (password.equals(this.password)) {
            return true;
        }
        return false;
    }

    public SessionCookie getCookie() {
        return cookie;
    }

    public void setCookie(SessionCookie cookie) {
        this.cookie = cookie;
    }


}
