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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lerserialbalanca.Principal;

/**
 * FXML Controller class
 *
 * @author Desenvolvimento
 */
public class ErroController implements Initializable {
    @FXML
    private ImageView imagem;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image img = new Image(Principal.class.getResourceAsStream("/imgs/pendrive.png"));
        imagem.setImage(img);
    }    
    
}
