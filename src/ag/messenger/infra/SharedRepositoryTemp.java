package ag.messenger.infra;

import ag.messenger.model.Message;
import ag.messenger.model.SharedRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rodrigobento
 */
public class SharedRepositoryTemp implements SharedRepository {

    private final Path arquivo;
    private final Translate translate;
    private final static String PATH = "rep/data.txt";
    
    public SharedRepositoryTemp(){
        this.arquivo = Paths.get(PATH);
        this.translate = new TranslateImpl();
    }
    
    
    @Override
    public void store(Message m) {
        try {
            Thread.sleep(3000);
            if (arquivo.toFile().exists()) {
                //
                Path temp = Paths.get("rep/data-lock.txt");
                Files.move(arquivo, temp);
                
                // Buscando o ultimo ID
                BufferedReader bf = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(temp.toFile())));
                String linha = "";
                List<String> linhas = new ArrayList<>();
                while ((linha = bf.readLine()) != null) {
                    linhas.add(linha);
                }
                int rid = 0;
                for (String aux : linhas) {
                    Message msg = translate.fromJSON(aux);
                    rid = msg.getId();
                }
                rid++;
                m.setId(rid);
                
                //
                FileOutputStream out = new FileOutputStream(temp.toFile(), true);
                out.write(translate.toJSON(m).getBytes());
                out.write("\n".getBytes());
                out.close();
                //
                Files.move(temp, arquivo);
            } else {
                Thread.sleep(2000);
                store(m);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Message> select(int id) {
        try {
            if (arquivo.toFile().exists()) {
                FileInputStream input = new FileInputStream(arquivo.toFile());
                //
                List<String> lines = new ArrayList<>();
                StringBuffer sb = new StringBuffer();
                while (true) {
                    byte[] b = new byte[1];
                    //
                    int r = input.read(b);
                    if (r == -1) {
                        break;
                    }
                    if (r == 0) {
                        continue;
                    }
                    //
                    String charact = new String(b);
                    if (charact.equals("\n")) {
                        lines.add(sb.toString());
                        sb = new StringBuffer();
                    } else {
                        sb.append(charact);
                    }
                }
                input.close();
                //
                List<Message> list = new ArrayList<>();
                for (String l : lines) {
                    Message m = translate.fromJSON(l);
                    if (m.getId() > id) {
                        list.add(m);
                    }
                }
                //
                return list;
            } else {
                Thread.sleep(2000);
                select(id);
            }
        } catch (IOException e) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            select(id);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }
}
