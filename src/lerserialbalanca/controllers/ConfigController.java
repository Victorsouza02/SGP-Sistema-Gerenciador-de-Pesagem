/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lerserialbalanca.Principal;
import lerserialbalanca.models.LerSerial;
import lerserialbalanca.models.Propriedades;
import lerserialbalanca.utils.Format;

/**
 * FXML Controller class
 *
 * @author Desenvolvimento
 */
public class ConfigController implements Initializable {
 
    @FXML
    private Button salvar;
    @FXML
    private ComboBox porta;
    @FXML
    private ComboBox equipamento;
    @FXML
    private TextField fonte;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        porta.setValue(Principal.getPorta());
        equipamento.setValue(Principal.getEquipamento());
        fonte.setText(Principal.getFonte());
        carregarComboBox();
        eventos();
    } 
    
    public void eventos(){
        Format.onlyNumber(fonte);
        Format.addTextLimiter(fonte, 2);
        salvar.setOnMouseClicked((event)->{
            Propriedades prop = new Propriedades();
            prop.alterarPropriedades(porta.getValue().toString(), equipamento.getValue().toString(), fonte.getText());
            System.exit(0);
        });
    }
    
    public void carregarComboBox(){
        porta.setItems(FXCollections.observableArrayList(LerSerial.portas));
        equipamento.setItems(FXCollections.observableArrayList(LerSerial.equipamentos));
    }
    
}
