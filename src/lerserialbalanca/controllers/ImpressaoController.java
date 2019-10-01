/*
    * CLASSE : ImpressaoController
    * FUNÇÃO : Controlar os eventos da tela de configurações de impressão e usar os metodos necessários.
*/
package lerserialbalanca.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lerserialbalanca.main.Principal;
import lerserialbalanca.models.Propriedades;
import lerserialbalanca.utils.Format;


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
    
    //INICIALIZA O CONTROLLER
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nomeempresa.setText(Principal.getNomeempresa());
        enderecoempresa.setText(Principal.getEnderecoempresa());
        telempresa.setText(Principal.getTelempresa());
        tamfonte.setText(Principal.getFonte());
        eventos();
    }    
    
    public void eventos(){
        Format.onlyNumber(tamfonte); //SOMENTE NUMEROS NO CAMPO FONTE
        Format.addTextLimiter(tamfonte, 2); //NO MAXIMO 2 DIGITOS NO CAMPO FONTE
        salvar.setOnMouseClicked((event) ->{ //Ao clicar em salvar
            Propriedades prop = new Propriedades();
            //Altera Proprierdades(Tamanho da Fonte, Nome Empresa, Endereço Empresa e Telefone Empesa)
            prop.alterarPropriedades(tamfonte.getText(), nomeempresa.getText(), enderecoempresa.getText(), telempresa.getText());
            System.exit(0);
        });
    }
    
}
