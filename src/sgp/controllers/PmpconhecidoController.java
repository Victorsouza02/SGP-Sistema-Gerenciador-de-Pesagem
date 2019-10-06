/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sgp.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
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
import javafx.scene.layout.Pane;
import javax.swing.JOptionPane;
import sgp.main.Principal;
import sgp.models.Pecas;
import sgp.utils.Formatacao;

/**
 * FXML Controller class
 *
 * @author Desenvolvimento
 */
public class PmpconhecidoController implements Initializable {

    @FXML
    private Label peso_liq;
    @FXML
    private Label estabilidade;
    @FXML
    private Label grandeza;
    @FXML
    private Label qtd_pecas;
    @FXML
    private Label lb_nome_peca;
    @FXML
    private Label lb_desc;
    @FXML
    private Label lb_pmp;

    @FXML
    private ComboBox cb_grandeza;

    @FXML
    private TextField tf_pmp;
    @FXML
    private TextField tf_cod_peca;

    @FXML
    private Button btn_ok;
    @FXML
    private Button btn_buscar;
    
    @FXML
    private Pane pane_peca;

    private boolean calcularPecas = false;
    private String pmp;
    Thread displayThread;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Inicia Thread de atualização no display
        displayThread = new Thread(this::DisplayThread);
        displayThread.start();
        Principal.ativoThreadPmp = true;
        eventos();
        carregarComboBox();
        Formatacao.onlyNumber(tf_cod_peca);
        cb_grandeza.setValue("kg");
        pane_peca.setVisible(false);
        
    }

    private void eventos() {
        btn_ok.setOnMouseClicked((event) -> {
            String pmp_valor = "0";
            if(tf_pmp.getText().contains(",")){
                pmp_valor = tf_pmp.getText().replaceAll(",", ".");
            }else {
                pmp_valor = tf_pmp.getText();
            }
            pmp = pmp_valor;
            calcularPecas = true;
        });

        btn_buscar.setOnMouseClicked((event) -> {
            Pecas pec = new Pecas();
            pec = pec.procurarPeca(Integer.parseInt(tf_cod_peca.getText()));
            if (pec != null){
                pane_peca.setVisible(true);
                lb_nome_peca.setText(pec.getNome());
                lb_desc.setText(pec.getDescricao());
                lb_pmp.setText(pec.getPmp()+pec.getGrandeza());
                cb_grandeza.setValue(pec.getGrandeza());
                pmp = pec.getPmp();
                calcularPecas = true;
            }else {
                pane_peca.setVisible(false);
                JOptionPane.showMessageDialog(null, "Peça não encontrada");
            }
        });
    }

    private void DisplayThread() {
        //THREAD PARA LEITURA DE PESO LIQUIDO E CALCULO DE PMP
        while (Principal.ativoThreadPmp) {
            Platform.runLater(() -> {
                //PEGA O PESO LIQUIDO/ESTABILIDADE E ADICIONA AO DISPLAY
                peso_liq.setText(Principal.getPeso_liq());
                Formatacao.estabilizacaoDisplay(estabilidade, Principal.getCodEstabilidade());
                //SE TIVER QUE CALCULAR A QUANTIDADE DE PEÇAS
                if (calcularPecas == true) {
                    //FAZ O CALCULO DE PEÇAS E ADICIONA AO DISPLAY
                    String qtd_pecas_var = calculoPecas(Principal.getPeso_liq(), pmp);
                    qtd_pecas.setText(qtd_pecas_var);
                }

                //Coloca as grandezas selecionadas no Label
                grandeza.setText(cb_grandeza.getValue().toString());
            });
            try {
                Thread.sleep(20);
            } catch (InterruptedException iex) {
                iex.printStackTrace();
            }
        }
    }

    private String calculoPecas(String peso_liq, String pmp) {
        try{
            if (pmp.equals("0")) {
                return "0";
            }
            
            BigDecimal num = new BigDecimal(peso_liq).divide(new BigDecimal(pmp), 30, RoundingMode.HALF_EVEN);
            System.out.println(num);
            num = num.setScale(0, RoundingMode.HALF_EVEN);
            return String.valueOf(num.intValue());
        } catch(Exception e){
            calcularPecas = false;
            JOptionPane.showMessageDialog(null, "Valor inválido");
            tf_pmp.setText("");
        }
        return "0";
    }

    public void carregarComboBox() {
        List<String> grandezas = new ArrayList<String>();
        grandezas.add("mg");
        grandezas.add("g");
        grandezas.add("kg");
        cb_grandeza.setItems(FXCollections.observableArrayList(grandezas));

    }

}
