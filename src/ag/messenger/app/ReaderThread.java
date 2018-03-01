package ag.messenger.app;

import ag.messenger.model.Message;
import ag.messenger.model.SharedRepository;
import java.util.List;

/**
 *
 * @author rodrigobento
 */
public class ReaderThread implements Runnable {

    private SharedRepository repository;
    private String nome;
    private int rid = 0;

    public ReaderThread(SharedRepository repository, String nome) {
        this.repository = repository;
        this.nome = nome;
    }

    @Override
    public void run() {
        while (true) {
            List<Message> list = repository.select(rid);
            for (Message message : list) {
                //diferenciar por nome
                if (!message.getFrom().equals(nome)) {
                    System.out.println(String.format("%s: %s",
                            message.getFrom(), message.getText()));
                }
                //
                rid = message.getId();
            }
        }
    }

}
