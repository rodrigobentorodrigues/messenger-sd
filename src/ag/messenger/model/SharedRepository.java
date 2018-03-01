package ag.messenger.model;

import java.util.List;

/**
 *
 * @author rodrigobento
 */
public interface SharedRepository {

    void store(Message m);

    List<Message> select(int id);

//    public int getLasID();
}
