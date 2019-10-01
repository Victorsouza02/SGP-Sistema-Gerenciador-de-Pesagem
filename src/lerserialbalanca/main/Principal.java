/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.main;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;
import jssc.SerialPortException;
import lerserialbalanca.models.Autorizacao;
import lerserialbalanca.models.LerSerial;
import lerserialbalanca.models.Propriedades;
import lerserialbalanca.models.Threads;


public class Principal extends Application {
    // Stages
    public static Stage primaryStage;
    public static Stage secondStage;
    public static Stage errorStage;
    
    // **** Variaveis de uso geral
    
    //INFORMAÇÕES DA SERIAL
    private static String peso_bru = "0";
    private static String peso_liq = "0";
    private static boolean estavel = true;
    private static LerSerial serial;
    
    //PROPRIEDADES
    private static String porta;
    private static String equipamento;
    private static String fonte;
    private static String nomeempresa;
    private static String enderecoempresa;
    private static String telempresa;
    
    //THREADS
    Thread serialThread;
    Thread securityThread;
    
    public static void main(String[] args) {
        launch(args);
    }
    

    @Override
    public void start(Stage stage) {
        Autorizacao pd = new Autorizacao();
        carregarPropriedades();
        if(pd.isAutorizado()){ //SE O USUARIO ESTIVER AUTORIZADO
            //Inicia Stage Principal e as Threads
            this.primaryStage = stage;
            this.errorStage = stage;
            initRootLayout();
            serial = new LerSerial(porta,equipamento);
            securityThread = new Thread(protecaoPendrive);
            securityThread.start();
            serialThread = new Thread(lerSerial);
            serialThread.start();

        } else { //SE NÃO ESTIVER AUTORIZADO
            //Inicia Stage de Erro
            this.errorStage = stage;
            initErrorLayout();
        }
    }
    
    
    public static void initRootLayout() { //INICIA TELA PRINCIPAL
        try {
            Parent root = FXMLLoader.load(Principal.class.getResource("/lerserialbalanca/views/telaprincipal.fxml"));
            primaryStage.setTitle("Sistema Gerenciador de Peso - EBM Metrologia");
            primaryStage.getIcons().addAll(new Image(Principal.class.getResourceAsStream("/lerserialbalanca/imgs/ebmico.jpg")));
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(true);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent arg0) {
                    System.exit(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    
    public static void initErrorLayout(){ //INICIA TELA DE ERRO
        try {
            Parent root = FXMLLoader.load(Principal.class.getResource("/lerserialbalanca/views/erro.fxml"));
            errorStage.setTitle("Sistema Gerenciador de Peso - EBM Metrologia");
            errorStage.getIcons().addAll(new Image(Principal.class.getResourceAsStream("/lerserialbalanca/imgs/ebmico.jpg")));
            errorStage.setScene(new Scene(root));
            errorStage.setResizable(true);
            errorStage.show();
            errorStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent arg0) {
                    System.exit(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void closePrimaryStage(){ //FECHA TELA PRINCIPAL
        primaryStage.close();
    }
    
    public static void closeErrorStage(){ //FECHA TELA DE ERRO
        errorStage.close();
    }
    
    //CARREGA SCENE NO STAGE SECUNDÁRIO COMO MODAL
    public static void loadScene(Scene scene, String titlePage, boolean resizable) {
         if(secondStage == null){   
            secondStage = new Stage();
            secondStage.initModality(Modality.WINDOW_MODAL);
            secondStage.initOwner(primaryStage);
            secondStage.getIcons().add(new Image(Principal.class.getResourceAsStream("/lerserialbalanca/imgs/ebmico.jpg")));
            secondStage.setResizable(resizable);
            secondStage.setTitle(titlePage);
            secondStage.setScene(scene);
            secondStage.show();
            secondStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent arg0) {
                    secondStage.close();
                    secondStage = null;
                }
            });
         }
    }
    
    //CARREGA AS PROPRIEDADES DO USUÁRIO
    public void carregarPropriedades(){
        Propriedades prop = new Propriedades();
        porta = prop.getPorta();
        equipamento = prop.getEquipamento();
        fonte = prop.getFonte();
        nomeempresa = prop.getNomeempresa();
        enderecoempresa = prop.getEnderecoempresa();
        telempresa = prop.getTelempresa();
    }
    
    
    public static Scene sobreScene(){ //SCENE DO MENU SOBRE
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/lerserialbalanca/views/sobre.fxml"));
            scene = new Scene(root, 400, 230);
            
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        
        return scene;
    }
    
    public static Scene relatorioScene(){ //SCENE DO MENU RELATÓRIO
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/lerserialbalanca/views/relatorio.fxml"));
            scene = new Scene(root, 400, 230);
            
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        
        return scene;
    }

    public static Scene configScene(){ //SCENE DO MENU CONFIGURAÇÕES GERAIS
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/lerserialbalanca/views/config.fxml"));
            scene = new Scene(root, 329, 374);
            
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        
        return scene;
    }
    
    public static Scene impressaoScene(){ //SCENE DO MENU DE CONFIGURAÇÕES DE IMPRESSAO
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/lerserialbalanca/views/impressao.fxml"));
            scene = new Scene(root, 432, 489);
            
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        
        return scene;
    }
    
    public static Scene pesquisaScene(){ //SCENE DO MENU DE PESQUISA DE PLACA
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/lerserialbalanca/views/pesquisaplaca.fxml"));
            scene = new Scene(root, 860, 482);
            
        } catch (IOException ex){
            ex.printStackTrace();
        }
        
        return scene;
    }
    
    public static Scene pmpScene(){ //SCENE DO MENU DE PESQUISA DE PLACA
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/lerserialbalanca/views/pmp.fxml"));
            scene = new Scene(root, 910, 530);
            
        } catch (IOException ex){
            ex.printStackTrace();
        }
        
        return scene;
    }
    
    
    
    private static Runnable lerSerial = new Runnable() { //INICIA THREAD LEITURA SERIAL
        public void run() {
            Threads th = new Threads();
            th.ReadSerialThread(serial);
        }
    };
    
    private static Runnable protecaoPendrive = new Runnable() { //INICIA THREAD SEGURANÇA
        public void run() {
            Threads th = new Threads();
            th.SecurityThread();
        }
    };
    
    
    /**
     * Retorna o palco principal
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static String getPeso_bru() {
        return peso_bru;
    }

    public static void setPeso_bru(String peso_bru) {
        Principal.peso_bru = peso_bru;
    }

    public static boolean isEstavel() {
        return estavel;
    }

    public static void setEstavel(boolean estavel) {
        Principal.estavel = estavel;
    }

    public static String getEquipamento() {
        return equipamento;
    }

    public static void setEquipamento(String equipamento) {
        Principal.equipamento = equipamento;
    }

    public static String getFonte() {
        return fonte;
    }

    public static void setFonte(String fonte) {
        Principal.fonte = fonte;
    }

    public static String getPorta() {
        return porta;
    }

    public static void setPorta(String porta) {
        Principal.porta = porta;
    }

    public static String getNomeempresa() {
        return nomeempresa;
    }

    public static void setNomeempresa(String nomeempresa) {
        Principal.nomeempresa = nomeempresa;
    }

    public static String getEnderecoempresa() {
        return enderecoempresa;
    }

    public static void setEnderecoempresa(String enderecoempresa) {
        Principal.enderecoempresa = enderecoempresa;
    }

    public static String getTelempresa() {
        return telempresa;
    }

    public static void setTelempresa(String telempresa) {
        Principal.telempresa = telempresa;
    }

    public static String getPeso_liq() {
        return peso_liq;
    }

    public static void setPeso_liq(String peso_liq) {
        Principal.peso_liq = peso_liq;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}