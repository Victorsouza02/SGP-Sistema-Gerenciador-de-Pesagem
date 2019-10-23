/*
    * CLASSE : TelaInicialController
    * FUNÇÃO : Controlar os eventos da Tela Inicial e usar os metodos necessários.
*/

package sgp.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import sgp.config.ConfiguracaoGlobal;
import sgp.config.VariaveisGlobais;
import sgp.main.Principal;
import sgp.models.Impressao;
import sgp.models.LerSerial;
import sgp.models.Motorista;
import sgp.models.Registro;
import sgp.utils.Formatacao;

public class TelaInicialController implements Initializable {

    @FXML
    private MenuItem menu_relatorio;
    @FXML
    private MenuItem menu_sobre;
    @FXML
    private MenuItem menu_config;
    @FXML
    private MenuItem menu_impressao;
    @FXML
    private MenuItem menu_pesquisa;

    @FXML
    private Pane painel_erro;
    @FXML
    private Label msg_erro;
    @FXML
    private Label peso_bru_id;
    @FXML
    private Label status;
    @FXML
    private Label entradaSaida;
    @FXML
    private Button confirma;
    @FXML
    private Button cancela;
    @FXML
    private Button relatorio;
    @FXML
    private ImageView imagem;
    @FXML
    private TextField text_placa;
    @FXML
    private TextField text_motorista;
    @FXML
    private TextField text_peso_ent;
    @FXML
    private TextField text_peso_sai;
    @FXML
    private TextField text_fornecedor;
    @FXML
    private TextField text_produto;

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

    public LerSerial serial;
    public byte[] buffer;
    Thread displayThread;
    Thread verificarErrosThread;   
    boolean isReading = true;
    private String peso;
    private String codEstabilidade;
    boolean mostrarEntrada = false;
    boolean mostrarSaida = false;

    
    //INICIALIZA O CONTROLLER
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        eventosElementos(); //Eventos dos elementos visuais
        formatarCampos(); //Formatação de campos
        preencherTabela(); //Preenchimento da tabela com dados do banco
        atribuirValores();
        //Inicia Thread de atualização no display
        displayThread = new Thread(this::DisplayThread);
        displayThread.start();
        //Inicia Thread de verificação de erros
        verificarErrosThread = new Thread(this::verificarErrosThread);
        verificarErrosThread.start();
        
    }
    
    //ATRIBUIÇÃO DE VALORES
    private void atribuirValores(){
        painel_erro.setVisible(false);
        //Carrega imagem do display
        Image img = new Image(Principal.class.getResourceAsStream("/sgp/imgs/"+ConfiguracaoGlobal.getDISPLAY_PRINCIPAL_IMG()));
        imagem.setImage(img);
    }

    //FORMATAÇÃO DOS CAMPOS
    public void formatarCampos() {
        UnaryOperator<Change> upperCase = change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        };
        TextFormatter<String> textFormatterPlaca = new TextFormatter<>(upperCase);
        text_placa.setTextFormatter(textFormatterPlaca);
        TextFormatter<String> textFormatterMot = new TextFormatter<>(upperCase);
        text_motorista.setTextFormatter(textFormatterMot);
        TextFormatter<String> textFormatterForn = new TextFormatter<>(upperCase);
        text_fornecedor.setTextFormatter(textFormatterForn);
        TextFormatter<String> textFormatterProd = new TextFormatter<>(upperCase);
        text_produto.setTextFormatter(textFormatterProd);
        Formatacao.addTextLimiter(text_placa, 7);
        text_peso_ent.setStyle("-fx-opacity: 1.0;");
        text_peso_sai.setStyle("-fx-opacity: 1.0;");

    }

    //EVENTOS DE ELEMENTOS
    public void eventosElementos() {
        //AO DIGITAR UM CARACTERE NO CAMPO PLACA
        text_placa.setOnKeyReleased((event) -> {
            if (text_placa.getText().length() == 7) { //SE O NUMERO DE CARACTERES FOR IGUAL 7
                Motorista mot = new Motorista();
                mot = mot.procurarPlaca(text_placa.getText());
                boolean registrado = mot.getNome() != null;
                if (registrado) { //SE ACHAR MOTORISTA JÁ CADASTRADO COM A PLACA
                    Registro reg = new Registro();
                    //PREENCHE CAMPOS
                    text_motorista.setText(mot.getNome());
                    text_fornecedor.setText(mot.getFornecedor());
                    text_produto.setText(mot.getProduto());
                    if (mot.getStatus().equals("E")) { //SE FOR ENTRADA
                        entradaSaida.setText("ENTRADA");
                        mostrarEntrada = true; //MOSTRA O PESO NO CAMPO DE ENTRADA
                    }
                    if (mot.getStatus().equals("S")) { //SE FOR SAÍDA
                        entradaSaida.setText("SAÍDA");
                        mostrarSaida = true; //MOSTRA O PESO NO CAMPO DE SAÍDA
                        reg = reg.ultimoRegistro(mot.getPlaca()); //PEGA O ULTIMO REGISTRO DA PLACA
                        text_peso_ent.setText(reg.getPs_entrada()); //MOSTRA ULTIMO PESO DE ENTRDA NO CAMPO DE ENTRADA
                    }
                } else { //SE NÃO HOUVER MOTORISTA CADASTRADO COM A PLACA
                    entradaSaida.setText("ENTRADA");
                    text_motorista.requestFocus();
                    mostrarEntrada = true;
                }
            } else { //SE O NUMERO DE CARACTERES FOR MENOR QUE 7
                //LIMPA TODOS OS CAMPOS
                entradaSaida.setText("");
                mostrarEntrada = false;
                mostrarSaida = false;
                text_motorista.setText("");
                text_peso_ent.setText("");
                text_peso_sai.setText("");
                text_fornecedor.setText("");
                text_produto.setText("");
            }
        });

        confirma.setOnMouseClicked((event) -> { //AO CLICAR NO BOTAO CONFIRMA
            if (validacaoCampos()) { //SE PASSAR PELA VALIDAÇÃO DE CAMPOS
                confirma.setDisable(true); //DESATIVA BOTAO CONFIRMAR
                Motorista mot = new Motorista();
                Registro reg = new Registro();
                mot = mot.procurarPlaca(text_placa.getText());
                boolean registrado = mot.getNome() != null;
                String tipo = (registrado == true) ? mot.getStatus() : "S";

                mot = new Motorista();
                mot.setNome(text_motorista.getText());
                mot.setPlaca(text_placa.getText());
                mot.setFornecedor(text_fornecedor.getText());
                mot.setProduto(text_produto.getText());
                mot.setStatus(tipo);

                if (registrado) { //SE O MOTORISTA JÁ ESTIVER REGISTRADO
                    mot.editar();
                    if (tipo.equals("S")) { //SE FOR SAIDA
                        reg.registrarSaida(mot.getPlaca(), text_peso_ent.getText(), text_peso_sai.getText());
                        fazerEtiqueta("S", mot.getPlaca());
                    } else if (tipo.equals("E")) { //SE FOR ENTRADA
                        reg.registrarEntrada(mot.getPlaca(), text_peso_ent.getText());
                        fazerEtiqueta("E", mot.getPlaca());
                    }
                    atualizarTabela(); //ATUALIZA TABELA
                    limparCampos(); // LIMPA TODOS OS CAMPOS
                } else {// SE O MOTORISTA NÃO ESTÁ REGISTRADO
                    mot.cadastrar();
                    reg.registrarEntrada(mot.getPlaca(), text_peso_ent.getText());
                    fazerEtiqueta("E", mot.getPlaca());
                    atualizarTabela();
                    limparCampos();
                }
                confirma.setDisable(false);
            }
        });

        cancela.setOnMouseClicked((event) -> { //AO CLICAR NO BOTÃO CANCELAR
            limparCampos();
            text_placa.requestFocus();
        });

        relatorio.setOnMouseClicked((event) -> { //AO CLICAR NO BOTÃO RELATÓRIO
            Principal.loadScene(Principal.relatorioScene(), "Busca de Relatório",false);
        });
        

        //EVENTO NA TABELA
        tabela.setRowFactory(tv -> {
            TableRow<Registro> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                //AO CLICAR DUAS VEZES EM UMA LINHA NA TABELA
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Registro rowData = row.getItem();
                    //PERGUNTA SE QUER REIMPRESSÃO DA ETIQUETA
                    Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
                    aviso.initOwner(confirma.getScene().getWindow());
                    aviso.setTitle("Impressão");
                    aviso.setHeaderText("Reimprimir registro da placa " + rowData.getPlaca());
                    aviso.setContentText("Deseja fazer a impressão?");
                    ButtonType botaoSim = new ButtonType("Sim");
                    ButtonType botaoNao = new ButtonType("Não", ButtonData.CANCEL_CLOSE);
                    aviso.getButtonTypes().setAll(botaoSim, botaoNao);
                    Optional<ButtonType> result = aviso.showAndWait();
                    if (result.get() == botaoSim) { //Se a opção for SIM
                        Impressao.recriarEtiqueta(rowData.getId()); //Reimprime a etiqueta 
                    }
                }
            });
            return row;
        });
        
        //AO CLICAR NO MENU DE RELATÓRIO
        menu_relatorio.setOnAction((event) -> {
            //Carrega modal de relatório
            Principal.loadScene(Principal.relatorioScene(), "Busca de Relatório",false);
        });
        
        //AO CLICAR NO MENU SOBRE
        menu_sobre.setOnAction((event) -> {
            //Carrega modal de sobre o programa
            Principal.loadScene(Principal.sobreScene(), "Sobre o programa",false);
        });
        
        //AO CLICAR NO MENU CONFIGURAÇÕES GERAIS
        menu_config.setOnAction((event) -> {
            //Carrega modal de configurações gerais
            Principal.loadScene(Principal.configScene(), "Configurações Gerais",false);
        });
        
        //AO CLICAR NO MENU CONFIGURAÇÕES DE IMPRESSÃO
        menu_impressao.setOnAction((event) ->{
            //Carrega modal de configurações de impressão
            Principal.loadScene(Principal.impressaoScene(), "Configurações de Impressão",false);
        });
        
        //AO CLICAR NO MENU DE PESQUISAR PLACA
        menu_pesquisa.setOnAction((event) -> {
            //Carrega modal de pesquisa de placas
            Principal.loadScene(Principal.pesquisaScene(), "Pesquisar Placa",false);
        });

    }

    public boolean validacaoCampos() { //VALIDAÇÃO DE CAMPOS
        if (text_placa.getText().equals("") || text_placa.getText().length() < 7) {
            JOptionPane.showMessageDialog(null, "Campo PLACA vazio ou imcompleto");
            text_placa.requestFocus();
            return false;
        }
        if (text_motorista.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Preencha o campo MOTORISTA");
            text_motorista.requestFocus();
            return false;
        }
        if (text_fornecedor.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Preencha o campo FORNECEDOR");
            text_fornecedor.requestFocus();
            return false;
        }
        if (text_produto.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Preencha o campo PRODUTO");
            text_produto.requestFocus();
            return false;
        }
        return true;
    }

    //FAZ ETIQUETA/CUPOM DO MOTORISTA
    public void fazerEtiqueta(String tipo, String placa) {
        if (tipo.equals("E")) { //SE FOR ENTRADA
            //PERGUNTA SE QUER IMPRIMIR A ENTRADA
            Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
            aviso.initOwner(confirma.getScene().getWindow());
            aviso.setTitle("Impressão");
            aviso.setHeaderText("Impressão do Ticket - ENTRADA");
            aviso.setContentText("Deseja fazer a impressão de entrada?");
            ButtonType botaoSim = new ButtonType("Sim");
            ButtonType botaoNao = new ButtonType("Não", ButtonData.CANCEL_CLOSE);
            aviso.getButtonTypes().setAll(botaoSim, botaoNao);
            Optional<ButtonType> result = aviso.showAndWait();
            if (result.get() == botaoSim) {//SE APERTAR EM SIM
                Impressao.fazerEtiquetaHtml(placa); //FAZ IMPRESSÃO DA ETIQUETA
            }
        } else if (tipo.equals("S")) { //SE FOR SAIDA 
            Impressao.fazerEtiquetaHtml(placa); //FAZ IMPRESSAO DA ETIQUETA
        }
    }

    //PREENCHE A TABELA COM OS DADOS DO BANCO
    public void preencherTabela() {
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

        tabela.setItems(reg.listaDeRegistros());
    }

    //ATUALIZA A TABELA COM NOVOS DADOS
    public void atualizarTabela() {
        Registro reg = new Registro();
        tabela.getItems().clear();
        tabela.setItems(reg.listaDeRegistros());
    }

    public void limparCampos() { //LIMPA TODOS OS CAMPOS DO FORMULÁRIO
        mostrarEntrada = false;
        mostrarSaida = false;
        entradaSaida.setText("");
        text_placa.setText("");
        text_motorista.setText("");
        text_peso_ent.setText("");
        text_peso_sai.setText("");
        text_fornecedor.setText("");
        text_produto.setText("");
    }

    private void DisplayThread() {
        //THREAD PARA LEITURA DE SERIAL CONTINUA
        while (isReading) {
            Platform.runLater(() -> {
                peso = Principal.getPeso_bru();
                codEstabilidade = Principal.getCodEstabilidade();
                peso_bru_id.setText(peso);
                Formatacao.estabilizacaoDisplay(status, codEstabilidade);
                if (mostrarEntrada) {
                    text_peso_ent.setText(peso);
                } else if (mostrarSaida) {
                    text_peso_sai.setText(peso);
                }
            });
            try {
                Thread.sleep(20);
            } catch (InterruptedException iex) {
                System.out.println(iex.getMessage());
            }
        }
    }
    
    private void verificarErrosThread() {
        boolean exibiuPainel = false;
        while (true) {
            if(VariaveisGlobais.isErroSerialDetectado() || VariaveisGlobais.isErroFormatDetectado()){
                if(!exibiuPainel){
                    painel_erro.setVisible(true);
                    FadeTransition ft = new FadeTransition(Duration.millis(2000), painel_erro);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.play();
                    exibiuPainel = true;
                }
                Platform.runLater(() -> {
                    confirma.setDisable(true);
                    cancela.setDisable(true);
                    text_placa.setDisable(true);
                    msg_erro.setText(VariaveisGlobais.getMensagem());
                });
            } else {
                painel_erro.setVisible(false);
                confirma.setDisable(false);
                cancela.setDisable(false);
                text_placa.setDisable(false);
                exibiuPainel = false;
            }
            try {
                if(exibiuPainel){
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(20);
                }
            } catch (InterruptedException iex) {
                iex.printStackTrace();
            }
        }
    }


}
