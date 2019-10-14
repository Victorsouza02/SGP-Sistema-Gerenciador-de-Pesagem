/*
 * CLASSE : Autorizacao
 * Função : Pegar as seriais das unidades e verificar autorização do usuário.
*/
package sgp.models;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import sgp.config.ConfiguracaoGlobal;

public class Autorizacao {
    //SERIAL DO USUARIO AUTORIZADO
    private final String SERIALUSUARIO = Propriedades.getAutorizacao();
    private boolean autorizado = (ConfiguracaoGlobal.isPROTECAO()) ? false : true;
    private List<String> seriais;
    
    public Autorizacao (){
        pegarSeriais();
        verificarSerial();
    }
    
    public void pegarSeriais(){ //PEGA AS SERIAIS DE TODAS UNIDADES DO COMPUTADOR E ADICIONA EM UMA LISTA
        try {
            StringBuilder sb = new StringBuilder();
            seriais = new ArrayList<String>();
            for(FileStore store : FileSystems.getDefault().getFileStores()){
                MessageDigest md = MessageDigest.getInstance("MD5"); //CONVERTE PARA MD5
                //DEVOLVE PARA UMA STRING JA CRIPTOGRAFADA
                String serialCrip = new String(hexCodes(md.digest(store.getAttribute("volume:vsn").toString().getBytes("UTF-8"))));
                seriais.add(serialCrip);
            }
        } catch(IOException ex){
            ex.printStackTrace();
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }
    
    public void verificarSerial(){ //VERIFICA SE ALGUMA SERIAL DA LISTA BATE COM A SERIAL DO USUARIO
        for(String serial : seriais){
            if(serial.equals(SERIALUSUARIO)){ //SE FOR EQUIVALENTE A SERIAL DO USUARIO
                autorizado = true; //LIBERA ACESSO
            }
        }
    }
    
    //CONVERTE PARA STRING O MD5
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
