package lerserialbalanca.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
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
import javax.swing.JOptionPane;
import jssc.SerialPortException;
import lerserialbalanca.Principal;
import lerserialbalanca.models.LerSerial;
import lerserialbalanca.models.ManipuladorEtiqueta;
import lerserialbalanca.models.Motorista;
import lerserialbalanca.models.Registro;
import lerserialbalanca.utils.BrowserLaunch;
import lerserialbalanca.utils.Format;

/**
 * FXML Controller class
 *
 * @author Desenvolvimento
 */
public class TelaInicialController implements Initializable {
    @FXML
    private MenuItem menu_relatorio;
    @FXML
    private Label peso_bru_id;
    @FXML
    private Label status;
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
    Thread serialThread;
    Thread displayThread;

    boolean isReading = true;
    private String peso;
    private boolean estavel;
    boolean mostrarEntrada = false;
    boolean mostrarSaida = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            serial = getProperties(); // LE O ARQUIVO .properties E RECEBE AS CONFIGURAÇÕES DO USUARIO
            eventosElementos(); //Eventos dos elementos visuais
            formatarCampos(); //Formatação de campos
            preencherTabela(); //Preenchimento da tabela com dados do banco
            //PARA EXECUTAVEL
            //Image img = new Image(new FileInputStream("./src/pe-display.jpg"));
            //imagem.setImage(img);
            serialThread = new Thread(this::ReadSerialThread);
            serialThread.start();
            displayThread = new Thread(this::DisplayThread);
            displayThread.start();

        } catch (SerialPortException ex) {
            JOptionPane.showMessageDialog(null, ex.getPortName() + " - " + ex.getExceptionType(), "Erro", 0);
            System.exit(0);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", 0);
            System.exit(0);
        } catch (ParseException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //PEGA DADOS DO ARQUIVO
    public LerSerial getProperties() throws SerialPortException, InterruptedException {
        Properties prop = new Properties();
        try {
            // le o arquivo
            prop.load(getClass().getResourceAsStream("../properties/config.properties"));
            //prop.load(new FileInputStream("./config/config.properties"));
            String porta = prop.getProperty("porta");
            String equipamento = prop.getProperty("equipamento");
            LerSerial serial = new LerSerial(porta, equipamento);
            return serial;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Arquivo .properties não foi encontrado");
            System.exit(0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
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
        Format.addTextLimiter(text_placa, 7);
        text_peso_ent.setStyle("-fx-opacity: 1.0;");
        text_peso_sai.setStyle("-fx-opacity: 1.0;");

    }

    //EVENTOS DE ELEMENTOS
    public void eventosElementos() {
        //AO DIGITAR UM CARACTERE NO CAMPO PLACA
        text_placa.setOnKeyReleased((event) -> {
            if (text_placa.getText().length() == 7) { //SE O NUMERO DE CARACTERES FOR IGUAL 7
                try {
                    Motorista mot = new Motorista();
                    mot = mot.procurarPlaca(text_placa.getText());
                    boolean registrado = mot.getNome() != null;
                    if (registrado) { //SE ACHAR MOTORISTA JÁ CADASTRADO COM A PLACA
                        Registro reg = new Registro();
                        text_motorista.setText(mot.getNome());
                        text_fornecedor.setText(mot.getFornecedor());
                        text_produto.setText(mot.getProduto());
                        if (mot.getStatus().equals("E")) {
                            mostrarEntrada = true;
                        }
                        if (mot.getStatus().equals("S")) {
                            mostrarSaida = true;
                            reg = reg.ultimoRegistro(mot.getPlaca());
                            text_peso_ent.setText(reg.getPs_entrada());
                        }
                    } else { //SE NÃO HOUVER MOTORISTA CADASTRADO COM A PLACA
                        text_motorista.requestFocus();
                        mostrarEntrada = true;
                    }

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", 0);
                    System.exit(0);
                }
            } else { //SE O NUMERO DE CARACTERES FOR MENOR QUE 7
                mostrarEntrada = false;
                mostrarSaida = false;
                text_motorista.setText("");
                text_peso_ent.setText("");
                text_peso_sai.setText("");
                text_fornecedor.setText("");
                text_produto.setText("");
            }
        });

        confirma.setOnMouseClicked((event) -> { //AO CLICAR NO BOTÃO CONFIRMA
            try {
                if (validacaoCampos()) { //SE PASSAR PELA VALIDAÇÃO DE CAMPOS
                    confirma.setDisable(true);
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
                        if (tipo.equals("S")) {
                            reg.registrarSaida(mot.getPlaca(), text_peso_ent.getText(), text_peso_sai.getText());
                            fazerEtiqueta("S", mot.getPlaca());
                        } else if (tipo.equals("E")) {
                            reg.registrarEntrada(mot.getPlaca(), text_peso_ent.getText());
                            fazerEtiqueta("E", mot.getPlaca());
                        }
                        atualizarTabela();
                        limparCampos();
                    } else { // SE O MOTORISTA NÃO ESTÁ REGISTRADO
                        mot.cadastrar();
                        reg.registrarEntrada(mot.getPlaca(), text_peso_ent.getText());
                        fazerEtiqueta("E", mot.getPlaca());
                        atualizarTabela();
                        limparCampos();
                    }
                    confirma.setDisable(false);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", 0);
                System.exit(0);
            } catch (ParseException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        cancela.setOnMouseClicked((event) -> { //AO CLICAR NO BOTÃO CANCELAR
            limparCampos();
            text_placa.requestFocus();
        });

        relatorio.setOnMouseClicked((event) -> { //AO CLICAR NO BOTÃO CANCELAR
            Principal.loadScene("views/relatorio.fxml", "Busca de Relatório");
        });

        //AO CLICAR DUAS VEZES EM UMA LINHA NA TABELA
        tabela.setRowFactory(tv -> {
            TableRow<Registro> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Registro rowData = row.getItem();
                    int option = JOptionPane.showConfirmDialog(null, "Deseja imprimir o registro da placa "+rowData.getPlaca()+"?", "Imprimir", JOptionPane.YES_NO_OPTION);
                    if (option == 0) {
                        try {
                            ManipuladorEtiqueta.recriarEtiqueta(rowData.getId());
                            BrowserLaunch.openURL(ManipuladorEtiqueta.getPath_html());
                        } catch (IOException ex) {
                            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ParseException ex) {
                            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            return row;
        });
        
        menu_relatorio.setOnAction((event)->{
            Principal.loadScene("views/relatorio.fxml", "Busca de Relatório");
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

    public void fazerEtiqueta(String tipo, String placa) throws IOException, ClassNotFoundException, SQLException, ParseException, ParseException {
        if (tipo.equals("E")) {
            Alert aviso = new Alert(Alert.AlertType.CONFIRMATION);
            aviso.setTitle("Impressão");
            aviso.setHeaderText("Impressão do Ticket - ENTRADA");
            aviso.setContentText("Deseja fazer a impressão de entrada?");
            ButtonType botaoSim = new ButtonType("Sim");
            ButtonType botaoNao = new ButtonType("Não", ButtonData.CANCEL_CLOSE);
            aviso.getButtonTypes().setAll(botaoSim, botaoNao);
            Optional<ButtonType> result = aviso.showAndWait();
            if (result.get() == botaoSim) {
                ManipuladorEtiqueta.fazerEtiquetaHtml(placa);
                BrowserLaunch.openURL(ManipuladorEtiqueta.getPath_html());
            }
        } else if (tipo.equals("S")) {
            ManipuladorEtiqueta.fazerEtiquetaHtml(placa);
            BrowserLaunch.openURL(ManipuladorEtiqueta.getPath_html());
        }

    }

    //PREENCHE A TABELA COM OS DADOS DO BANCO
    public void preencherTabela() throws ClassNotFoundException, SQLException, ParseException {
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
    public void atualizarTabela() throws ParseException {
        Registro reg = new Registro();
        tabela.getItems().clear();
        try {
            tabela.setItems(reg.listaDeRegistros());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", 0);
            System.exit(0);
        }
    }

    public void limparCampos() { //LIMPA TODOS OS CAMPOS DO FORMULÁRIO
        mostrarEntrada = false;
        mostrarSaida = false;
        text_placa.setText("");
        text_motorista.setText("");
        text_peso_ent.setText("");
        text_peso_sai.setText("");
        text_fornecedor.setText("");
        text_produto.setText("");
    }

    
    private void ReadSerialThread() {
        //THREAD PARA LEITURA DE SERIAL CONTINUA
        while (isReading) {
            try {
                Map<String, String> dados = new HashMap<String, String>();
                dados = serial.selecionarDadosEquipamento();
                boolean estavel_var = (dados.get("estavel").equals("Estável"))? true : false;
                String peso_bru_var = dados.get("peso_bru");
                Platform.runLater(() -> {
                    peso = peso_bru_var;
                    estavel = estavel_var;
                });
                try {
                    Thread.sleep(20);
                } catch (InterruptedException iex) {
                    JOptionPane.showMessageDialog(null, "Conexão Serial interrompida", "Erro", 0);
                    System.exit(0);
                }
            } catch (SerialPortException ex) {
                JOptionPane.showMessageDialog(null, ex.getPortName() + " - " + ex.getExceptionType(), "Erro", 0);
                System.exit(0);

            }
        }
    }

    private void DisplayThread() {
        //THREAD PARA LEITURA DE SERIAL CONTINUA
        while (isReading) {
            Platform.runLater(() -> {
                peso_bru_id.setText(peso);
                if(estavel){
                    status.setText("Estável");
                    status.setStyle("-fx-text-fill: green;");
                } else {
                    status.setText("Oscilando");
                    status.setStyle("-fx-text-fill: red;");
                }
                if (mostrarEntrada) {
                    text_peso_ent.setText(peso);
                } else if (mostrarSaida) {
                    text_peso_sai.setText(peso);
                }
            });
            try {
                Thread.sleep(20);
            } catch (InterruptedException iex) {
                JOptionPane.showMessageDialog(null, "Conexão Serial interrompida", "Erro", 0);
            }
        }
    }

}
