package ag.messenger.infra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ag.messenger.model.Message;
import ag.messenger.model.SharedRepository;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author rodrigobento
 */
public class SharedRepositoryImpl implements SharedRepository {

    private final static String PATH = "rep/data.txt";
    private final Translate translate;
    private final Path arq;

    public SharedRepositoryImpl() {
        translate = new TranslateImpl();
        arq = Paths.get(PATH);
    }
    
    @Override
    public void store(Message m) {
        try {
            FileOutputStream fos = new FileOutputStream(arq.toFile(), true);
//            FileChannel canal = fos.getChannel();
            FileChannel canal = FileChannel.open(arq, StandardOpenOption.WRITE, 
                    StandardOpenOption.READ);
            // Bloqueio de escrita (exclusivo)
            FileLock lock = canal.lock(0, Long.MAX_VALUE, false);
            
            // Buscando o ultimo ID
            ByteBuffer bufferRead = ByteBuffer.allocate(1);
            int numBytes = canal.read(bufferRead);
            List<String> lines = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            while (numBytes != -1) {
                bufferRead.flip();
                while (bufferRead.hasRemaining()) {
                    char c = (char) bufferRead.get();
                    String aux = String.valueOf(c);
                    if (aux.equals("\n")) {
                        lines.add(sb.toString());
                        sb = new StringBuffer();
                    } else {
                        sb.append(aux);
                    }
                }
                bufferRead.clear();
                numBytes = canal.read(bufferRead);
            }
            int wid = 0;
            for (String linha : lines) {
                wid = translate.fromJSON(linha).getId();
            }
            wid++;
            
            m.setId(wid);

            // Cria os buffers que serão adicionados ao arquivo
            ByteBuffer buffer = ByteBuffer.wrap(translate.toJSON(m).getBytes());
            ByteBuffer buffer2 = ByteBuffer.wrap("\n".getBytes());
            
            // Escreve os dados contidos no buffer ao canal
            canal.write(buffer);
            canal.write(buffer2);

            // Libera o lock e fecha o canal, por fim o stream do arquivo
            lock.release();
            canal.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> select(int id) {
        FileInputStream fis = null;
        try {
            // Stream do arquivo para criar o canal
            fis = new FileInputStream(arq.toFile());
            FileChannel canal = fis.getChannel();
            // Cria o lock a partir do canal, leitura (compartilhado)
            FileLock lock = canal.lock(0, 0, true);
            // Seta o buffer para o tamanho de um byte, iteracao é feita nele
            ByteBuffer buffer = ByteBuffer.allocate(1);
            // Lê os bytes do canal a partir do buffer
            int numBytes = canal.read(buffer);
            
            List<String> lines = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            
            while (numBytes != -1) {
                buffer.flip();
                // Verifica se ainda possui algum byte no arquivo
                while (buffer.hasRemaining()) {
                    // Lê o byte e adiciona a lista de Strings
                    char c = (char) buffer.get();
                    String aux = String.valueOf(c);
                    if (aux.equals("\n")) {
                        lines.add(sb.toString());
                        sb = new StringBuffer();
                    } else {
                        sb.append(aux);
                    }
                }
                // Limpa o buffer
                buffer.clear();
                numBytes = canal.read(buffer);
            }
            // Adiciona as string a lista de mensagens
            List<Message> mensagens = new ArrayList<>();
            for (String linha : lines) {
                Message msg = translate.fromJSON(linha);
                if (msg.getId() > id) {
                    mensagens.add(msg);
                }
            }
            // Libera o lock e fecha o canal e a stream
            lock.release();
            canal.close();
            fis.close();
            return mensagens;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

}
