/*
    * CLASSE : ErroController
    * FUNÇÃO : Controlar os eventos da tela de erro e usar os metodos necessários.
*/
package lerserialbalanca.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lerserialbalanca.main.Principal;


public class ErroController implements Initializable {
    @FXML
    private ImageView imagem;
    
    //INICIALIZA O CONTROLLER
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image img = new Image(Principal.class.getResourceAsStream("/imgs/pendrive.png"));
        imagem.setImage(img);
    }    
    
}
