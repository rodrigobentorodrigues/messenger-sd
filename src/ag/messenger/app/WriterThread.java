package ag.messenger.app;

import ag.messenger.model.Message;
import ag.messenger.model.SharedRepository;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author rodrigobento
 */
public class WriterThread implements Runnable {
    
    private Scanner scanner;
    private String nome;
    private SharedRepository repository;
    private int rid;
    
    public WriterThread(SharedRepository repo, String nome){
        this.nome = nome;
        this.repository = repo;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
//            System.out.print("Sua mensagem: ");
            //
            String msg = scanner.nextLine();
            
//            rid = repository.getLasID();
//            rid++;
            //
            Message m = new Message();
            m.setId(rid);
            m.setFrom(nome);
            m.setText(msg);
            m.setDate(new Date());
            //
            repository.store(m);
        }
    }

}
