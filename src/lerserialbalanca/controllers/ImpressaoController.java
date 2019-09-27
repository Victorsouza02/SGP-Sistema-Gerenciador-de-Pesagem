/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lerserialbalanca.Principal;
import lerserialbalanca.models.Propriedades;
import lerserialbalanca.utils.Format;

/**
 * FXML Controller class
 *
 * @author Desenvolvimento
 */
public class ImpressaoController implements Initializable {

    @FXML
    private TextField nomeempresa;
    @FXML
    private TextField enderecoempresa;
    @FXML
    private TextField telempresa;
    @FXML
    private TextField tamfonte;
    @FXML
    private Button salvar;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nomeempresa.setText(Principal.getNomeempresa());
        enderecoempresa.setText(Principal.getEnderecoempresa());
        telempresa.setText(Principal.getTelempresa());
        tamfonte.setText(Principal.getFonte());
        eventos();
    }    
    
    public void eventos(){
        Format.onlyNumber(tamfonte);
        Format.addTextLimiter(tamfonte, 2);
        salvar.setOnMouseClicked((event) ->{
            Propriedades prop = new Propriedades();
            prop.alterarPropriedades(tamfonte.getText(), nomeempresa.getText(), enderecoempresa.getText(), telempresa.getText());
            System.exit(0);
        });
    }
    
}
