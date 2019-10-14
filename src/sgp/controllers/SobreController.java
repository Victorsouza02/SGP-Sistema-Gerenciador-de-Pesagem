/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sgp.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sgp.config.ConfiguracaoGlobal;
import sgp.main.Principal;

/**
 * FXML Controller class
 *
 * @author Desenvolvimento
 */
public class SobreController implements Initializable {
    @FXML
    private ImageView imagem;
    @FXML
    private Label lb_nomeempresa;
    @FXML
    private Label lb_versao;
    @FXML
    private Label lb_telefone;
    @FXML
    private Label lb_site;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image img = new Image(Principal.class.getResourceAsStream("/sgp/imgs/"+ConfiguracaoGlobal.getLOGO_EMPRESA()));
        imagem.setImage(img);
        lb_nomeempresa.setText(ConfiguracaoGlobal.getNOME_EMPRESA());
        lb_versao.setText("Versão : "+ConfiguracaoGlobal.getVERSAO());
        lb_telefone.setText("Contato : "+ConfiguracaoGlobal.getTELEFONE());
        lb_site.setText(ConfiguracaoGlobal.getSITE());
    }    
    
}
