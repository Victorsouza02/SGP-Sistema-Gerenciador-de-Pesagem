
package lerserialbalanca.models;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class PenDrive {
    private final String SERIALUSUARIO = "E82DE5A00873691740D1D06570B7BF4B";
    private boolean autorizado = false;
    private List<String> seriais;
    
    public PenDrive () throws IOException, NoSuchAlgorithmException{
        pegarSeriais();
        verificarSerial();
    }
    
    public void pegarSeriais() throws IOException, NoSuchAlgorithmException{
        StringBuilder sb = new StringBuilder();
        seriais = new ArrayList<String>();
        for(FileStore store : FileSystems.getDefault().getFileStores()){
            MessageDigest md = MessageDigest.getInstance("MD5");
            String serialCrip = new String(hexCodes(md.digest(store.getAttribute("volume:vsn").toString().getBytes("UTF-8"))));
            sb.append(String.format("%-20s vsn:%s\n", store, serialCrip));
            //LISTA OS SERIAIS NO OUTPUT
            System.out.println(sb);
            seriais.add(serialCrip);
        }
    }
    
    public void verificarSerial(){
        for(String serial : seriais){
            if(serial.equals(SERIALUSUARIO)){
                autorizado = true;
            }
        }
    }
    
    private static char[] hexCodes(byte[] text) {
        char[] hexOutput = new char[text.length * 2];
        String hexString;
  
        for (int i = 0; i < text.length; i++) {
            hexString = "00" + Integer.toHexString(text[i]);
            hexString.toUpperCase().getChars(hexString.length() - 2,
                                    hexString.length(), hexOutput, i * 2);
        }
        return hexOutput;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

}
