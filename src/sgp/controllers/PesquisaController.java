
package sgp.controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import sgp.main.Principal;
import sgp.models.Impressao;
import sgp.models.Motorista;
import sgp.models.Registro;
import sgp.utils.Formatacao;

/**
 * FXML Controller class
 *
 * @author Desenvolvimento
 */
public class PesquisaController implements Initializable {
    @FXML
    private TextField placa;
    @FXML
    private Label nome;
    @FXML
    private Label fornecedor;
    @FXML
    private Label produto;
    @FXML
    private Label registros;
    @FXML
    private Label atividade;
    @FXML
    private Label erro;
    @FXML
    private Pane painel;
    @FXML
    private Button imprimir;

    @FXML
    private TableView<Registro> tabela;
    @FXML
    private TableColumn<Registro, Integer> idcol;
    @FXML
    private TableColumn<Registro, String> placacol;
    @FXML
    private TableColumn<Registro, String> nomecol;
    @FXML
    private TableColumn<Registro, String> forncol;
    @FXML
    private TableColumn<Registro, String> prodcol;
    @FXML
    private TableColumn<Registro, String> decol;
    @FXML
    private TableColumn<Registro, String> hecol;
    @FXML
    private TableColumn<Registro, String> pecol;
    @FXML
    private TableColumn<Registro, String> dscol;
    @FXML
    private TableColumn<Registro, String> hscol;
    @FXML
    private TableColumn<Registro, String> pscol;
    @FXML
    private TableColumn<Registro, String> plcol;
    @FXML
    private ImageView imagem;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image img = new Image(Principal.class.getResourceAsStream("/sgp/imgs/driver.png"));
        imagem.setImage(img);
        formatarCampos();
        eventos();
    }
    
    public void formatarCampos() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                placa.requestFocus();
                imprimir.setDisable(true);
            }
        });
        UnaryOperator<TextFormatter.Change> upperCase = change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        };
        TextFormatter<String> textFormatterPlaca = new TextFormatter<>(upperCase);
        placa.setTextFormatter(textFormatterPlaca);
        Formatacao.addTextLimiter(placa, 7);
    }
    
    public void eventos(){
        painel.setVisible(false);
        placa.setOnKeyReleased((event) -> {
            if (placa.getText().length() == 7) { //SE O NUMERO DE CARACTERES FOR IGUAL 7
                Motorista mot = new Motorista();
                mot = mot.procurarPlaca(placa.getText());
                boolean registrado = mot.getNome() != null;
                if (registrado) { //SE ACHAR MOTORISTA JÁ CADASTRADO COM A PLACA
                    imprimir.setDisable(false);
                    erro.setText("");
                    painel.setVisible(true);
                    nome.setText(mot.getNome());
                    fornecedor.setText(mot.getFornecedor());
                    produto.setText(mot.getProduto());
                    registros.setText(String.valueOf(mot.numRegistros(placa.getText())));
                    atividade.setText(Motorista.ultimaAtividade(placa.getText()));
                    preencherTabela(placa.getText());
                } else { //SE NÃO HOUVER MOTORISTA CADASTRADO COM A PLACA
                    imprimir.setDisable(true);
                    erro.setText("Placa não encontrada");
                    painel.setVisible(false);
                    
                }
            } else { //SE O NUMERO DE CARACTERES FOR MENOR QUE 7
                imprimir.setDisable(true);
                erro.setText("");
                painel.setVisible(false);
                tabela.getItems().clear();
            }
        });
        
        tabela.setRowFactory(tv -> {
            TableRow<Registro> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Registro rowData = row.getItem();
                    Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
                    aviso.initOwner(placa.getScene().getWindow());
                    aviso.setTitle("Impressão");
                    aviso.setHeaderText("Reimprimir registro da placa " + rowData.getPlaca());
                    aviso.setContentText("Deseja fazer a impressão?");
                    ButtonType botaoSim = new ButtonType("Sim");
                    ButtonType botaoNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
                    aviso.getButtonTypes().setAll(botaoSim, botaoNao);
                    Optional<ButtonType> result = aviso.showAndWait();
                    if (result.get() == botaoSim) {
                        Impressao.recriarEtiqueta(rowData.getId());
                    }
                }
            });
            return row;
        });
        
        imprimir.setOnAction((event)->{
            Registro reg = new Registro();
            List<Registro> registros = reg.listaDeRegistros(placa.getText());
            Impressao.fazerRelatorioHtml(registros,placa.getText());
        });
    }
    //PREENCHE A TABELA COM OS DADOS DO BANCO
    public void preencherTabela(String placa) {
        Registro reg = new Registro();
        idcol.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        placacol.setCellValueFactory(
                new PropertyValueFactory<>("placa"));
        nomecol.setCellValueFactory(
                new PropertyValueFactory<>("nome"));
        forncol.setCellValueFactory(
                new PropertyValueFactory<>("fornecedor"));
        prodcol.setCellValueFactory(
                new PropertyValueFactory<>("produto"));
        decol.setCellValueFactory(
                new PropertyValueFactory<>("dt_entrada"));
        hecol.setCellValueFactory(
                new PropertyValueFactory<>("h_entrada"));
        pecol.setCellValueFactory(
                new PropertyValueFactory<>("ps_entrada"));
        dscol.setCellValueFactory(
                new PropertyValueFactory<>("dt_saida"));
        hscol.setCellValueFactory(
                new PropertyValueFactory<>("h_saida"));
        pscol.setCellValueFactory(
                new PropertyValueFactory<>("ps_saida"));
        plcol.setCellValueFactory(
                new PropertyValueFactory<>("ps_liquido"));

        tabela.setItems(reg.listaDeRegistros(placa));
    }
    
}
