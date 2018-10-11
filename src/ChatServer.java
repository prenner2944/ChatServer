import java.util.*;

/**
 * <b> CS 180 - Project 4 - Chat Server Skeleton </b>
 * <p>
 *
 * This is the skeleton code for the ChatServer Class. This is a private chat
 * server for you and your friends to communicate.
 *
 * @author (Your Name) <(YourEmail@purdue.edu)>
 *
 * @lab (Your Lab Section)
 *
 * @version (Today's Date)
 *
 */
public class ChatServer {
    private Random r;
    private User[] users;
    private CircularBuffer chatlog;
    private int maxMessages;
    private int currentUsers;

    public ChatServer(User[] users, int maxMessages) {
        this.r = new Random();
        this.users = new User[100];
        this.users[0] = new User("root", "cs180", null);
        for (int i = 0; i < users.length; i++)
            this.users[i + 1] = users[i];
        this.chatlog = new CircularBuffer(maxMessages);
        this.maxMessages = maxMessages;
        this.currentUsers = 1 + users.length;
    }

    /**
     * This method begins server execution.
     */
    public void run() {
        boolean verbose = true;
        System.out.printf("The VERBOSE option is off.\n\n");
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            // this allows students to manually place "\r\n" at end of command
            // in prompt
            command = replaceEscapeChars(command);

            if (command.startsWith("kill"))
                break;

            if (command.startsWith("verbose")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = MessageFactory.makeErrorMessage(MessageFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getMessage()));
            }

            // change the formatting of the server response so it prints well on
            // the terminal (for testing purposes only)
            if (response.startsWith("SUCCESS\t"))
                response = response.replace("\t", "\n");

            // print the server response
            if (verbose)
                System.out.printf("response:\n");
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }

    /**
     * Replaces "poorly formatted" escape characters with their proper values.
     * For some terminals, when escaped characters are entered, the terminal
     * includes the "\" as a character instead of entering the escape character.
     * This function replaces the incorrectly inputed characters with their
     * proper escaped characters.
     *
     * @param str
     *            - the string to be edited
     * @return the properly escaped string
     */
    private static String replaceEscapeChars(String str) {
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");

        return str;
    }

    /**
     * Determines which client command the request is using and calls the
     * function associated with that command.
     *
     * @param request
     *            - the full line of the client request (CRLF included)
     * @return the server response
     */
    public String parseRequest(String request) {
        String[][] validCommand = {
                {"ADD-USER", "cookieID", "username", "password"},
                {"USER-LOGIN", "username", "password"},
                {"POST-MESSAGE", "cookieID", "message"},
                {"GET-MESSAGES", "cookieID", "numMessages"}};
        int command = -1;
        if (request.indexOf("\r\n") != -1) request = request.substring(0, request.indexOf("\r\n"));
        String[] args = request.split("\t");

        boolean isCommandFormatted = false;

        for (int i = 0; i < validCommand.length; i++) {
            if (args[0].equals(validCommand[i][0])) {
                command = i;
                if (args.length == validCommand[i].length) isCommandFormatted = true;
            }
        }
        if(command == -1)  return MessageFactory.makeErrorMessage(11);
        if (!isCommandFormatted) return MessageFactory.makeErrorMessage(10);

        switch (command) {
            case -1: return MessageFactory.makeErrorMessage(11);
            case 0: return addUser(args);
            case 1: return userLogin(args);
            case 2: return postMessage(args, getUserByCookie(args[1]).getName());
            case 3: return getMessages(args);
            default: return MessageFactory.makeErrorMessage(11);
        }
    }
    public User getUserByCookie(String cookieID) {
        for (int i = 0; i < currentUsers; i++) {
            if (users[i] == null) break;
            if (users[i].getCookie() != null && Long.toString(users[i].getCookie().getID()).equals(cookieID)) return users[i];
        }
        return null;
    }
    public String addUser(String[] args) {
        String username = args[2];
        String password = args[3];

        if (username.isEmpty() || username.length() > 20) return MessageFactory.makeErrorMessage(24);
        if (password.length() < 4 || password.length() > 40) return MessageFactory.makeErrorMessage(24);
        for (int i = 0; i < username.length(); i++) {
            if (!Character.isLetterOrDigit(username.charAt(i)))
                return MessageFactory.makeErrorMessage(24);
        }
        for (int i = 0; i < password.length(); i++) {
            if (!Character.isLetterOrDigit(password.charAt(i)))
                return MessageFactory.makeErrorMessage(24);
        }

        for (int i = 0; i < users.length; i++) {
            if (users[i] == null) break;
            if (users[i].getName().equals(username)) return MessageFactory.makeErrorMessage(22);
        }

        users[currentUsers] = new User(username, password, null);
        currentUsers++;
        return "SUCCESS\r\n";

    }
    public String userLogin(String[] args) {
        long id = r.nextInt(1000);

        for (int i = 0; i < currentUsers; i++) {
            if (users[i].getName().equals(args[1])) {
                if (users[i].getCookie() == null) {
                    if (users[i].checkPassword(args[2])) {
                        for (int j = 0; j < users.length; j++) {
                            if (users[j] == null) break;
                            if (users[j].getCookie() == null) break;
                            if (id == users[j].getCookie().getID()) id = r.nextInt(1000);
                        }
                        users[i].setCookie(new SessionCookie(id));
                        return "SUCCESS\t" + id + "\r\n";
                    }
                    else return MessageFactory.makeErrorMessage(21);
                }
                else return MessageFactory.makeErrorMessage(25);
            }
        }
        return MessageFactory.makeErrorMessage(20);

    }
    public String postMessage(String[] args, String name) {
        if (args[2].trim().length() < 1) return MessageFactory.makeErrorMessage(24);
        String message = name + ": " + args[2].trim();
        chatlog.put(message);
        return "SUCCESS\r\n";
    }
    public String getMessages(String[] args) {
        if (Integer.parseInt(args[2]) < 1) return MessageFactory.makeErrorMessage(24);
        return "SUCCESS\t" + String.join("\t", chatlog.getNewest(Integer.parseInt(args[2]))) + "\r\n";
    }
}
