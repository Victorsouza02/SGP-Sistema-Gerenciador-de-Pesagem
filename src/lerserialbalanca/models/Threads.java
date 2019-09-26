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
import javax.swing.JOptionPane;
import lerserialbalanca.Principal;
import lerserialbalanca.controllers.TelaInicialController;

/**
 *
 * @author Desenvolvimento
 */
public class Threads {
    
      
    public void ReadSerialThread(LerSerial serial) {
        Map<String, String> dados = new HashMap<String, String>();
        //THREAD PARA LEITURA DE SERIAL CONTINUA
        while (true) {
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
        boolean jaParou = false;
        boolean status = true;
        while (true) {
            Autorizacao aut = new Autorizacao();
            aut.pegarSeriais();
            aut.verificarSerial();
            if (aut.isAutorizado() == false && jaParou == false) {
                jaParou = true;
                status = false;
                Platform.runLater(() -> {
                    Principal.closePrimaryStage();
                    Principal.initErrorLayout();     
                });
            } else if(aut.isAutorizado() == true && status == false){
                jaParou = false;
                status = true;
                Platform.runLater(() -> {
                    Principal.closeErrorStage();
                    Principal.initRootLayout();
                });
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
