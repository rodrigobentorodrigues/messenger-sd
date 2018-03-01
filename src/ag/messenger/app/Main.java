package ag.messenger.app;

import java.util.Scanner;

import ag.messenger.infra.SharedRepositoryImpl;
import ag.messenger.infra.SharedRepositoryTemp;
import ag.messenger.model.SharedRepository;

/**
 *
 * @author rodrigobento
 */
public class Main {

    public static void main(String[] args) {
        //
        SharedRepository repository = new SharedRepositoryImpl();
//        SharedRepository repository = new SharedRepositoryTemp();
        //
        Scanner scanner = new Scanner(System.in);
        //
        System.out.print("Por favor, informe seu nome: ");
        String name = scanner.nextLine();
        //
        System.out.println();
        //
        System.out.println("Chat");
        System.out.println("-----------------------------");
        //
        Thread read = new Thread(new ReaderThread(repository, name));
        Thread write = new Thread(new WriterThread(repository, name));
        read.start();
        write.start();
    }
}
