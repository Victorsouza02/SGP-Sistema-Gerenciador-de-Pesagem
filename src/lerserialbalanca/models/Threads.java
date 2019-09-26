/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.models;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import jssc.SerialPort;
import lerserialbalanca.Principal;
import lerserialbalanca.controllers.TelaInicialController;

/**
 *
 * @author Desenvolvimento
 */
public class Threads {
    
    public void ReadSerialThread(LerSerial serial) {
        //THREAD PARA LEITURA DE SERIAL CONTINUA
        while (serial.isOk()) {
            Map<String, String> dados = new HashMap<String, String>();
            Map<String, String> result = new HashMap<String, String>();
            dados = serial.selecionarDadosEquipamento();
            boolean estavel_var = (dados.get("estavel").equals("Estável")) ? true : false;
            String peso_bru_var = dados.get("peso_bru");
            Platform.runLater(() -> {
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
    
    public void SecurityThread() {
        int cnt = 0;
        while (true) {
            Autorizacao aut = new Autorizacao();
            aut.pegarSeriais();
            aut.verificarSerial();
            if (aut.isAutorizado() == false && cnt == 0) {
                Platform.runLater(() -> {
                    Principal.primaryStage.close();
                    Principal.initErrorLayout();     
                });
                cnt++;
            } else if(aut.isAutorizado() == true && cnt != 0){
                JOptionPane.showMessageDialog(null, "Pen drive detectado! Reinicie o programa.");
                System.exit(0);
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
