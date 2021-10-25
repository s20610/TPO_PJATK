/**
 *
 *  @author Kotnowski Borys S20610
 *
 */

package zad1;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<ChatClient> {
    public ChatClientTask(Callable<ChatClient> call) {
        super(call);
    }
    public static ChatClientTask create(ChatClient chatClient, List<String> msgs, int waitingValue) {
        return new ChatClientTask(() -> {
            try {
                chatClient.login();
                if (waitingValue != 0) {
                    Thread.sleep(waitingValue);
                }
                for (String message : msgs) {
                    chatClient.send(message);
                    if (waitingValue != 0) {
                        Thread.sleep(waitingValue);
                    }
                }
                chatClient.logout();
                if (waitingValue != 0) {
                    Thread.sleep(waitingValue);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return chatClient;
        });
    }

    public ChatClient getClient() {
        try {
            return this.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
