/*
 * CLASSE : Threads
 * Função : Organizar as funções das Threads de leitura serial e segurança.
*/
package lerserialbalanca.models;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javax.swing.JOptionPane;
import lerserialbalanca.main.Principal;

/**
 *
 * @author Desenvolvimento
 */
public class Threads {
    
    //Metodo para ler serial e colocar o valor na classe principal para uso futuro.  
    public void ReadSerialThread(LerSerial serial) {
        Map<String, String> dados = new HashMap<String, String>();
        //THREAD PARA LEITURA DE SERIAL CONTINUA
        while (true) {
            dados = serial.selecionarDadosEquipamento();
            boolean estavel_var = (dados.get("estavel").equals("Estável")) ? true : false;
            String peso_bru_var = dados.get("peso_bru");
            String peso_liq_var = dados.get("peso_liq");
            Platform.runLater(() -> {
                Principal.setPeso_liq(peso_liq_var);
                Principal.setPeso_bru(peso_bru_var);
                Principal.setEstavel(estavel_var);
            });
            try {
                Thread.sleep(20);
            } catch (InterruptedException iex) {
                JOptionPane.showMessageDialog(null, "Conexão Serial interrompida", "Erro", 0);
                System.exit(0);
            }
        }
    }
    
    //Metodo para verificar se o usuario tem autorizacao de rodar o programa
    public void SecurityThread() {
        boolean jaParou = false;
        boolean status = true;
        while (true) {
            Autorizacao aut = new Autorizacao();
            aut.pegarSeriais();
            aut.verificarSerial();
            //Se o usuário não for autorizado muda para o Stage de Erro
            if (aut.isAutorizado() == false && jaParou == false) {
                jaParou = true;
                status = false;
                Platform.runLater(() -> {
                    Principal.closePrimaryStage();
                    Principal.initErrorLayout();     
                });
              //Se o usuario agora estiver autorizado volta para o Stage Principal
            } else if(aut.isAutorizado() == true && status == false){
                jaParou = false;
                status = true;
                Platform.runLater(() -> {
                    Principal.closeErrorStage();
                    Principal.initRootLayout();
                });
            }
            try {
                Thread.sleep(1000); //Execute a cada 1 segundo
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

}
