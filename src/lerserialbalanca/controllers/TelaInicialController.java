package lerserialbalanca.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
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
import jssc.SerialPort;
import jssc.SerialPortException;
import lerserialbalanca.models.ManipuladorEtiqueta;
import lerserialbalanca.models.Motorista;

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
    private TableView<Motorista> tabela;
    @FXML
    private TableColumn<Motorista, String> placacol;
    @FXML
    private TableColumn<Motorista, String> nomecol;
    @FXML
    private TableColumn<Motorista, String> forncol;
    @FXML
    private TableColumn<Motorista, String> prodcol;
    @FXML
    private TableColumn<Motorista, String> statuscol;

    public SerialPort serialPort = new SerialPort("COM4");
    public byte[] buffer;
    Thread dateThread;
    boolean isReading = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conSerial();
            eventsElements();
            formatFields();
            testes();
            addTextLimiter(text_placa, 7);
            //PARA EXECUTAVEL
            //Image img = new Image(new FileInputStream("./pe-display.jpg"));
            //imagem.setImage(img);
            dateThread = new Thread(this::ReadSerialThread);
            dateThread.start();
            //ManipuladorEtiqueta man = new ManipuladorEtiqueta();
            //man.fazerEtiquetaHtml("C:\\Users\\Desenvolvimento\\Documents\\Java\\etiqueta.txt","C:\\Users\\Desenvolvimento\\Documents\\Java\\PRINT.HTML");

        } catch (SerialPortException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //COMUNICAÇÃO SERIAL
    public void conSerial() throws SerialPortException {
        serialPort.openPort();
        serialPort.setParams(9600, 8, 1, 0, false, false);
    }

    //RETORNA DADOS DA SERIAL
    public String dataSerial() throws SerialPortException {
        buffer = serialPort.readBytes(27);
        return new String(buffer);
    }

    //PEGA DADOS DO ARQUIVO
    public String getProperties() {
        InputStream is;
        Properties prop = new Properties();

        try {
            // le o arquivo
            is = getClass().getResourceAsStream("properties/config.properties");
            prop.load(is);
            //prop.load(new FileInputStream("./config/config.properties")); PARA JAR

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String port = prop.getProperty("prop.port");
        return port;

    }

    //FORMATAÇÃO DOS CAMPOS
    public void formatFields() {
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
    }

    //EVENTOS DE ELEMENTOS
    public void eventsElements() {
        text_placa.setOnKeyReleased((event) -> {
            if (text_placa.getText().length() == 7) {
                try {
                    Motorista mot = new Motorista();
                    if (mot.procurarPlaca(text_placa.getText())) {
                        text_motorista.setText(mot.getNome());
                        text_fornecedor.setText(mot.getFornecedor());
                        text_produto.setText(mot.getProduto());
                        if (mot.getStatus().equals("E")) {
                            text_peso_ent.setText(peso_bru_id.getText() + " KG");
                        }
                        if (mot.getStatus().equals("S")) {
                            text_peso_sai.setText(peso_bru_id.getText() + " KG");
                        }
                    } else {
                        text_motorista.requestFocus();
                        text_peso_ent.setText(peso_bru_id.getText() + " KG");
                    }

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                text_motorista.setText("");
                text_peso_ent.setText("");
                text_peso_sai.setText("");
                text_fornecedor.setText("");
                text_produto.setText("");
            }
        });

        confirma.setOnMouseClicked((event) -> {
            try {
                if (validacaoCampos()) {
                    Motorista mot = new Motorista();
                    boolean registrado = mot.procurarPlaca(text_placa.getText());
                    String tipo = mot.getStatus();
                    mot = new Motorista();
                    mot.setNome(text_motorista.getText());
                    mot.setPlaca(text_placa.getText());
                    mot.setFornecedor(text_fornecedor.getText());
                    mot.setProduto(text_produto.getText());
                    mot.setStatus(tipo);
                    if (registrado) {
                        mot.editar();
                        refreshTable();
                        limparCampos();
                    } else {
                        mot.cadastrar();
                        refreshTable();
                        limparCampos();
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        cancela.setOnMouseClicked((event) ->{
            limparCampos();
            text_placa.requestFocus();
        });
        
    }

    public boolean validacaoCampos() {
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

    public String removeZeros(String linha) {
        String linha_retorno;
        if (linha.equals("0000000")) {
            return "0";
        }
        if (linha.substring(0, 1).equals("-")) {
            linha_retorno = "-" + linha.substring(1, 7).replaceFirst("0*", "");
        } else {
            linha_retorno = linha.replaceFirst("0*", "");
        }

        return linha_retorno;
    }

    private void ReadSerialThread() {
        while (isReading) {
            try {
                String dados = dataSerial();
                String estavel_var = (dados.substring(0, 1).equals("0")) ? "Estável" : "Oscilando";
                String peso_bru_var = dados.substring(2, 9);
                String tara_var = dados.substring(10, 17);
                String peso_liq_var = dados.substring(18, 25);
                Platform.runLater(() -> {
                    peso_bru_id.setText(removeZeros(peso_bru_var));
                });
                try {
                    Thread.sleep(20);
                } catch (InterruptedException iex) {

                }
            } catch (SerialPortException ex) {
                Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);

            }
        }

    }

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

    public void testes() throws ClassNotFoundException, SQLException {
        Motorista mot = new Motorista();
        placacol.setCellValueFactory(
                new PropertyValueFactory<>("placa"));
        nomecol.setCellValueFactory(
                new PropertyValueFactory<>("nome"));
        forncol.setCellValueFactory(
                new PropertyValueFactory<>("fornecedor"));
        prodcol.setCellValueFactory(
                new PropertyValueFactory<>("produto"));
        statuscol.setCellValueFactory(
                new PropertyValueFactory<>("status"));
        tabela.setItems(mot.listaDeClientes());
    }

    public void refreshTable() {
        Motorista mot = new Motorista();
        tabela.getItems().clear();
        try {
            tabela.setItems(mot.listaDeClientes());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TelaInicialController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limparCampos() {
        text_placa.setText("");
        text_motorista.setText("");
        text_peso_ent.setText("");
        text_peso_sai.setText("");
        text_fornecedor.setText("");
        text_produto.setText("");
    }

}
