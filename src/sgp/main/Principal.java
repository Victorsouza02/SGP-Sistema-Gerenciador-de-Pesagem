/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sgp.main;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sgp.models.Autorizacao;
import sgp.models.LerSerial;
import sgp.models.Propriedades;
import sgp.models.Threads;


public class Principal extends Application {
    // Stages
    public static Stage primaryStage;
    public static Stage secondStage;
    public static Stage errorStage;
    
    // **** Variaveis de uso geral
    
    //INFORMAÇÕES DA SERIAL
    private static String peso_bru = "0";
    private static String peso_liq = "0";
    private static String codEstabilidade = "E";
    private static LerSerial serial;
       
    //THREADS
    Thread serialThread;
    Thread securityThread;
    public static boolean ativoThreadPmp = false;
    
    public static void main(String[] args) {
        launch(args);
    }
    

    @Override
    public void start(Stage stage) {
        Autorizacao pd = new Autorizacao();
        new Propriedades();
        if(pd.isAutorizado()){ //SE O USUARIO ESTIVER AUTORIZADO
            //Inicia Stage Principal e as Threads
            this.primaryStage = stage;
            this.errorStage = stage;
            initRootLayout();
            serial = new LerSerial(Propriedades.getPorta(),Propriedades.getEquipamento());
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
            Parent root = FXMLLoader.load(Principal.class.getResource("/sgp/views/telaprincipal.fxml"));
            primaryStage.setTitle("Sistema Gerenciador de Peso - EBM Metrologia");
            primaryStage.getIcons().addAll(new Image(Principal.class.getResourceAsStream("/sgp/imgs/ebmico.jpg")));
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
            Parent root = FXMLLoader.load(Principal.class.getResource("/sgp/views/erro.fxml"));
            errorStage.setTitle("Sistema Gerenciador de Peso - EBM Metrologia");
            errorStage.getIcons().addAll(new Image(Principal.class.getResourceAsStream("/sgp/imgs/ebmico.jpg")));
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
            secondStage.getIcons().add(new Image(Principal.class.getResourceAsStream("/sgp/imgs/ebmico.jpg")));
            secondStage.setResizable(resizable);
            secondStage.setTitle(titlePage);
            secondStage.setScene(scene);
            secondStage.show();

            secondStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent arg0) {
                    if(ativoThreadPmp){
                        ativoThreadPmp = false;
                    }
                    secondStage.close();
                    secondStage = null;
                }
            });
         }
    }
    
    
    
    public static Scene sobreScene(){ //SCENE DO MENU SOBRE
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/sgp/views/sobre.fxml"));
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
            root = FXMLLoader.load(Principal.class.getResource("/sgp/views/relatorio.fxml"));
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
            root = FXMLLoader.load(Principal.class.getResource("/sgp/views/config.fxml"));
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
            root = FXMLLoader.load(Principal.class.getResource("/sgp/views/impressao.fxml"));
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
            root = FXMLLoader.load(Principal.class.getResource("/sgp/views/pesquisaplaca.fxml"));
            scene = new Scene(root, 860, 482);
            
        } catch (IOException ex){
            ex.printStackTrace();
        }
        
        return scene;
    }
    
    public static Scene cadastrarPmpScene(){ //SCENE DO MENU DE PESQUISA DE PLACA
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/sgp/views/cadastropmp.fxml"));
            scene = new Scene(root, 968, 490);
            
        } catch (IOException ex){
            ex.printStackTrace();
        }
        
        return scene;
    }
    
    public static Scene contadorPmpScene(){ //SCENE DO MENU DE PESQUISA DE PLACA
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("/sgp/views/pmpconhecido.fxml"));
            scene = new Scene(root, 968, 490);
            
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

    public static String getCodEstabilidade() {
        return codEstabilidade;
    }

    public static void setCodEstabilidade(String codEstabilidade) {
        Principal.codEstabilidade = codEstabilidade;
    } 

    public static String getPeso_liq() {
        return peso_liq;
    }

    public static void setPeso_liq(String peso_liq) {
        Principal.peso_liq = peso_liq;
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}