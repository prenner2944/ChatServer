/**
 * Created by paulrenner on 10/29/15.
 */
public class CircularBuffer {
    private String[] chatlog;
    private int messageNum;
    private int size;
    private int current;
    private boolean filled;

    public CircularBuffer(int s) {
        chatlog = new String[s];
        messageNum = 0;
        size = s;
        current = 0;
        filled = false;
    }

    public void put(String message) {
        String messageNumString = "";
        if (messageNum < 10) messageNumString += "000";
        else if (messageNum < 100) messageNumString += "00";
        else if (messageNum < 1000) messageNumString += "0";
        messageNumString += messageNum + ")";

        chatlog[current] = messageNum + ") " + message;
        current++;
        messageNum++;
        if (current >= size) {
            current = 0;
            filled = true;
        }
        if (messageNum > 9999)
            messageNum = 0;
    }
    public String[] getNewest(int numMessages) {
        if (numMessages < 0) return null;
        if (numMessages == 0) return new String[0];

        String[] result;
        if (!filled) {
            if (numMessages >= current) {
                numMessages = current;
                result = new String[current];
                for(int i = current - numMessages; i < current; i++)
                    result[i] = chatlog[i];
            }
            else {
                result = new String[current - numMessages];
                for(int i = current - numMessages; i < current; i++)
                    result[i - (current - numMessages)] = chatlog[i];
            }
        }
        else {

            result = new String[size];
            int j = 0;
            for(int i = current; j < size; i++) {
                result[j] = chatlog[i];
                j++;
            }
        }
        return result;
    }
}