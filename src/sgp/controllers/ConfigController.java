/*
    * CLASSE : ConfigController
    * FUNÇÃO : Controlar os eventos da tela de configurações gerais e usar os metodos necessários.
*/
package sgp.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import sgp.models.LerSerial;
import sgp.models.Propriedades;


public class ConfigController implements Initializable {
 
    @FXML
    private Button salvar;
    @FXML
    private ComboBox porta;
    @FXML
    private ComboBox equipamento;
    @FXML
    private ComboBox cb_somenteestavel;

    
    
    //INICIALIZA O CONTROLLER
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        porta.setValue(Propriedades.getPorta());
        equipamento.setValue(Propriedades.getEquipamento());
        cb_somenteestavel.setValue((Propriedades.isSomente_estavel())? "SIM" : "NÃO");
        carregarComboBox();
        eventos();
    } 
    
    //EVENTOS DOS ELEMENTOS 
    public void eventos(){
        salvar.setOnMouseClicked((event)->{ //Ao clicar em Salvar
            Propriedades prop = new Propriedades();
            //Altera as propriedades (Porta e Equipamento)
            prop.alterarPropriedades(porta.getValue().toString(), equipamento.getValue().toString(), cb_somenteestavel.getValue().toString());
            System.exit(0); //Encerra o sistema
        });
    }
    
    //CARREGA COMBOBOX COM DADOS DE PORTAS E EQUIPAMENTOS DISPONIVEIS
    public void carregarComboBox(){
        porta.setItems(FXCollections.observableArrayList(LerSerial.portas));
        equipamento.setItems(FXCollections.observableArrayList(LerSerial.equipamentos));
        cb_somenteestavel.setItems(FXCollections.observableArrayList("SIM", "NÃO"));
    }
    
}
