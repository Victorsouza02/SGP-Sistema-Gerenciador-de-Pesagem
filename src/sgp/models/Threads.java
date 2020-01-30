/*
 * CLASSE : Threads
 * Função : Organizar as funções das Threads de leitura serial e segurança.
 */
package sgp.models;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javax.swing.JOptionPane;
import sgp.main.Principal;

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
            String codEstabilidade = dados.get("estavel");
            String peso_bru_var = dados.get("peso_bru");
            String peso_liq_var = dados.get("peso_liq");
            if (Propriedades.isSomente_estavel()) { //SE A CONFIGURAÇÃO DE MOSTRAR SOMENTE ESTÁVEL ESTIVER ATIVA
                if (codEstabilidade.equals("E")) { //SE FOR ESTÁVEL PASSE O VALOR PARA AS VARIAVEIS
                    Platform.runLater(() -> {
                        Principal.setPeso_liq(peso_liq_var);
                        Principal.setPeso_bru(peso_bru_var);
                        Principal.setCodEstabilidade(codEstabilidade);
                    });
                }
            } else { //SE A CONFIGURAÇÃO DE MOSTRAR SOMENTE ESTÁVEL ESTIVER DESATIVADA
                //SEMPRE PASSAR OS VALORES LIDOS PARA AS VARIAVEIS
                Platform.runLater(() -> { 
                    Principal.setPeso_liq(peso_liq_var);
                    Principal.setPeso_bru(peso_bru_var);
                    Principal.setCodEstabilidade(codEstabilidade);
                });
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException iex) {
                iex.printStackTrace();
            }
        }
    }

}
