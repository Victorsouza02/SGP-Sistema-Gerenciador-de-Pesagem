package lerserialbalanca.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lerserialbalanca.main.Principal;
import lerserialbalanca.models.Pecas;
import lerserialbalanca.utils.Formatacao;

public class CadastroPMP implements Initializable {
  
    private String peso_liquido;
    private String codEstabilidade;
    private boolean calcularPmp = false;
    private boolean calcularPecas = false;
    private BigDecimal pmp_decimal;
    
    @FXML
    private Label peso_liq;
    @FXML
    private Label estabilidade;
    @FXML
    private Label qtd_pecas;
    @FXML
    private Label label_gran_peso;
    @FXML
    private Label label_gran_pmp;
    @FXML
    private Label pmp;
    @FXML
    private ComboBox grandeza;
    @FXML
    private TextField qtd_amostras;
    @FXML
    private TextField nome;
    @FXML
    private TextField descricao;
    @FXML
    private Button salvar;
    
    Thread displayThread;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formatacao();
        eventos();
        //Inicia Thread de atualização no display
        displayThread = new Thread(this::DisplayThread);
        displayThread.start();
        grandeza.setValue("kg");
        carregarComboBox();
        Principal.ativoThreadPmp = true;
    }

    private void formatacao(){
        Formatacao.onlyNumber(qtd_amostras);
        Formatacao.addTextLimiter(qtd_amostras, 4);
    }
    private void eventos(){
         //AO DIGITAR UM CARACTERE NO CAMPO QUANTIDADE DE AMOSTRAS
        qtd_amostras.setOnKeyReleased((event) -> {
            if (qtd_amostras.getText().length() > 0) { //SE O NUMERO DE CARACTERES FOR MAIOR QUE 0
                calcularPmp = true;
            } else {//SE O NUMERO DE CARACTERES FOR ZERO
                //LIMPA TODOS OS CAMPOS
                calcularPmp = false;
                calcularPecas = false;
                qtd_pecas.setText("0");
                pmp.setText("0");
            }
        });

        salvar.setOnMouseClicked((event) ->{
            Pecas pec = new Pecas(nome.getText(), descricao.getText(), getPmp_decimal().toString(), qtd_amostras.getText(), grandeza.getValue().toString());
            pec.salvarPeca();
            calcularPmp = false;
            calcularPecas = false;
            qtd_pecas.setText("0");
            pmp.setText("0");
            nome.setText("");
            descricao.setText("");
            qtd_amostras.setText("");
        });
    }
    
    private void DisplayThread() {
        //THREAD PARA LEITURA DE PESO LIQUIDO E CALCULO DE PMP
        while (Principal.ativoThreadPmp) {
            Platform.runLater(() -> {
                //PEGA O PESO LIQUIDO/ESTABILIDADE E ADICIONA AO DISPLAY
                peso_liquido = Principal.getPeso_liq();
                peso_liq.setText(peso_liquido);
                codEstabilidade = Principal.getCodEstabilidade();
                Formatacao.estabilizacaoDisplay(estabilidade, codEstabilidade);
                //SE TIVER QUE CALCULAR O PMP
                if(calcularPmp == true){
                    //FAZ O CALCULO E COLOCA NO DISPLAY
                    String pmp_var = calculoPmp(peso_liquido, qtd_amostras.getText());
                    pmp.setText(pmp_var);
                    calcularPmp = false; //DESATIVA CALCULO DE PMP
                    calcularPecas = true; //ATIVA CALCULO DE PEÇAS
                }
                //SE TIVER QUE CALCULAR A QUANTIDADE DE PEÇAS
                if(calcularPecas == true){
                    //FAZ O CALCULO DE PEÇAS E ADICIONA AO DISPLAY
                    String qtd_pecas_var = calculoPecas(peso_liquido, pmp.getText());
                    qtd_pecas.setText(qtd_pecas_var);
                }        
                //Coloca as grandezas selecionadas no Label
                label_gran_peso.setText(grandeza.getValue().toString());
                label_gran_pmp.setText(grandeza.getValue().toString());

            });
            try {
                Thread.sleep(20);
            } catch (InterruptedException iex) {
                iex.printStackTrace();
            }
        }
    }
    
     //CARREGA COMBOBOX COM DADOS DE PORTAS E EQUIPAMENTOS DISPONIVEIS
    public void carregarComboBox(){
        List<String> grandezas = new ArrayList<String>();
        grandezas.add("mg");
        grandezas.add("g");
        grandezas.add("kg");
        grandeza.setItems(FXCollections.observableArrayList(grandezas));
        
    }
    
    private String calculoPmp(String peso_liq, String qtd_amostras){
        if(!qtd_amostras.equals("0")){
            BigDecimal num = new BigDecimal(peso_liq).divide(new BigDecimal(qtd_amostras+".00"), 4, RoundingMode.HALF_EVEN);
            setPmp_decimal(num);
            return num.setScale(3, RoundingMode.HALF_EVEN).toString().replaceAll(",", ".");
        }
        return "0";
    }
    
    private String calculoPecas(String peso_liq, String pmp){
        if(pmp.equals("0")){
            return "0";
        }
        BigDecimal num = new BigDecimal(peso_liq).divide(pmp_decimal,30,RoundingMode.HALF_EVEN);
        num = num.setScale(0,RoundingMode.HALF_EVEN);
        return String.valueOf(num.intValue());
    }
    

    public BigDecimal getPmp_decimal() {
        return pmp_decimal;
    }

    public void setPmp_decimal(BigDecimal pmp_decimal) {
        this.pmp_decimal = pmp_decimal;
    }
    
    
    
}
