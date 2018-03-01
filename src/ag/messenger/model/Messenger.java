package ag.messenger.model;

import java.util.List;

/**
 *
 * @author rodrigobento
 */
public interface Messenger {

    void send(Message m);

    List<Message> receive(int id);
}
