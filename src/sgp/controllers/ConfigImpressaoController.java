/*
    * CLASSE : ConfigImpressaoController
    * FUNÇÃO : Controlar os eventos da tela de configurações de impressão e usar os metodos necessários.
*/
package sgp.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sgp.models.Propriedades;
import sgp.utils.Formatacao;


public class ConfigImpressaoController implements Initializable {

    @FXML
    private TextField nomeempresa;
    @FXML
    private TextField enderecoempresa;
    @FXML
    private TextField telempresa;
    @FXML
    private TextField tamfonte;
    @FXML
    private TextField altura;
    @FXML
    private Button salvar;
    
    //INICIALIZA O CONTROLLER
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nomeempresa.setText(Propriedades.getNomeempresa());
        enderecoempresa.setText(Propriedades.getEnderecoempresa());
        telempresa.setText(Propriedades.getTelempresa());
        tamfonte.setText(Propriedades.getFonte());
        altura.setText(Propriedades.getAltura());
        eventos();
    }    
    
    public void eventos(){
        Formatacao.onlyNumber(tamfonte); //SOMENTE NUMEROS NO CAMPO FONTE
        Formatacao.addTextLimiter(tamfonte, 2); //NO MAXIMO 2 DIGITOS NO CAMPO FONTE
        Formatacao.onlyNumber(altura); //SOMENTE NUMEROS NO CAMPO ALTURA 
        Formatacao.addTextLimiter(altura, 2); //NO MAXIMO 2 DIGITOS NO CAMPO ALTURA
        salvar.setOnMouseClicked((event) ->{ //Ao clicar em salvar
            Propriedades prop = new Propriedades();
            //Altera Proprierdades(Tamanho da Fonte, Altura/Espaçamento Cupom,  Nome Empresa, Endereço Empresa e Telefone Empesa)
            prop.alterarPropriedades(tamfonte.getText(), nomeempresa.getText(), enderecoempresa.getText(), telempresa.getText(),altura.getText());
            System.exit(0);
        });
    }
    
}
