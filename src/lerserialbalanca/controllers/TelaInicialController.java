package lerserialbalanca.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;
import jssc.SerialPortException;
import lerserialbalanca.models.LerSerial;
import lerserialbalanca.models.ManipuladorEtiqueta;
import lerserialbalanca.models.Motorista;
import lerserialbalanca.models.Registro;

/**
 * FXML Controller class
 *
 * @author Desenvolvimento
 */
public class TelaInicialController implements Initializable {

    @FXML
    private Label peso_bru_id;
    @FXML
    private Button confirma;
    @FXML
    private Button cancela;
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
    Thread labelThread;

    boolean isReading = true;
    private String peso;
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
            //Image img = new Image(new FileInputStream("./pe-display.jpg"));
            //imagem.setImage(img);
            serialThread = new Thread(this::ReadSerialThread);
            serialThread.start();
            displayThread = new Thread(this::DisplayThread);
            displayThread.start();
            
            //ManipuladorEtiqueta man = new ManipuladorEtiqueta();
            //man.fazerEtiquetaHtml("C:\\Users\\Desenvolvimento\\Documents\\Java\\etiqueta.txt","C:\\Users\\Desenvolvimento\\Documents\\Java\\PRINT.HTML");

        } catch (SerialPortException ex) {
            JOptionPane.showMessageDialog(null, ex.getPortName()+" - "+ex.getExceptionType(),"Erro", 0);
            System.exit(0);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),"Erro", 0);
            System.exit(0);
        }
    }

    //PEGA DADOS DO ARQUIVO
    public LerSerial getProperties() throws SerialPortException {
        InputStream is;
        Properties prop = new Properties();
        try {
            // le o arquivo
            is = getClass().getResourceAsStream("../properties/config.properties");
            prop.load(is);
            String porta = prop.getProperty("porta");
            String equipamento = prop.getProperty("equipamento");
            LerSerial serial = new LerSerial(porta,equipamento);
            return serial;
            //prop.load(new FileInputStream("./config/config.properties")); PARA JAR
        } catch (FileNotFoundException ex) {
             JOptionPane.showMessageDialog(null,"Arquivo .properties não foi encontrado");
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
        addTextLimiter(text_placa, 7);
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
                            //text_peso_ent.setText(peso_bru_id.getText());
                        }
                        if (mot.getStatus().equals("S")) {
                            mostrarSaida = true;
                            Registro regis = reg.ultimoRegistro(mot.getPlaca());
                            text_peso_ent.setText(regis.getPs_entrada());
                            //text_peso_sai.setText(peso_bru_id.getText());
                        }
                    } else { //SE NÃO HOUVER MOTORISTA CADASTRADO COM A PLACA
                        text_motorista.requestFocus();
                        mostrarEntrada = true;
                        //text_peso_ent.setText(peso_bru_id.getText() + " KG");
                    }

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),"Erro", 0);
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
                        if(tipo.equals("S")){
                            reg.registrarSaida(mot.getPlaca(), text_peso_ent.getText(), text_peso_sai.getText());
                        } else if(tipo.equals("E")){
                            reg.registrarEntrada(mot.getPlaca(), text_peso_ent.getText());
                        }
                        atualizarTabela();
                        limparCampos();
                    } else { // SE O MOTORISTA NÃO ESTÁ REGISTRADO
                        mot.cadastrar();
                        reg.registrarEntrada(mot.getPlaca(), text_peso_ent.getText());
                        atualizarTabela();
                        limparCampos();
                    }

                    
                    
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),"Erro", 0);
                System.exit(0);
            }
        });
        
        cancela.setOnMouseClicked((event) ->{ //AO CLICAR NO BOTÃO CANCELAR
            limparCampos();
            text_placa.requestFocus();
        });
        
    }

    public boolean validacaoCampos() { //VALIDAÇÃO DE CAMPOS
        if(text_placa.getText().equals("") || text_placa.getText().length() < 7){
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

    private void ReadSerialThread() { //THREAD PARA LEITURA DE SERIAL CONTINUA
        while (isReading) {
            try {
                Map<String,String> dados = new HashMap<String, String>();
                dados = serial.selecionarDadosEquipamento();
                String estavel_var = dados.get("estavel");
                String peso_bru_var = dados.get("peso_bru");
                String tara_var = dados.get("tara");
                String peso_liq_var = dados.get("peso_liq");
                Platform.runLater(() -> {
                    peso = peso_bru_var;
                });
                try {
                    Thread.sleep(20);
                } catch (InterruptedException iex) {
                    JOptionPane.showMessageDialog(null,"Conexão Serial interrompida","Erro", 0);
                    System.exit(0);
                }
            } catch (SerialPortException ex) {
                JOptionPane.showMessageDialog(null, ex.getPortName()+" - "+ex.getExceptionType(),"Erro", 0);
                System.exit(0);

            }
        }

    }
    
    private void DisplayThread() { //THREAD PARA LEITURA DE SERIAL CONTINUA
        while (isReading) {
            Platform.runLater(() -> {
                peso_bru_id.setText(peso);
                if(mostrarEntrada){
                    text_peso_ent.setText(peso);
                } else if(mostrarSaida){
                    text_peso_sai.setText(peso);
                }
                
            });
            try {
                Thread.sleep(20);
            } catch (InterruptedException iex) {
                JOptionPane.showMessageDialog(null,"Conexão Serial interrompida","Erro", 0);
                System.exit(0);
            }
        }

    }
    
    //METODO PARA ADICIONAR LIMITE DE TEXTO EM UM CAMPO
    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }
    
    //PREENCHE A TABELA COM OS DADOS DO BANCO
    public void preencherTabela() throws ClassNotFoundException, SQLException {
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
        try {
            tabela.setItems(reg.listaDeRegistros());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),"Erro", 0);
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

}
