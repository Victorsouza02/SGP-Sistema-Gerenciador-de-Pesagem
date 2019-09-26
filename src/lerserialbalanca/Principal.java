/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;
import lerserialbalanca.models.Autorizacao;
import lerserialbalanca.models.LerSerial;


public class Principal extends Application {

    public static void main(String[] args) {
        launch(args);
        
    }
   
    public static Stage primaryStage;
    public static Stage secondStage;
    public static Stage errorStage;
    private static String peso_bru;
    private static boolean estavel;
    private LerSerial serial;

    @Override
    public void start(Stage stage) {
        Autorizacao pd = new Autorizacao();
        if(pd.isAutorizado()){
            this.primaryStage = stage;
            this.errorStage = stage;
            initRootLayout();
        } else {
            this.errorStage = stage;
            initErrorLayout();
        }
    }
    
    /**
     * Inicializa o root layout (layout base).
     */
    public static void initRootLayout() {
        try {
            Parent root = FXMLLoader.load(Principal.class.getResource("views/principal.fxml"));
            primaryStage.setTitle("Sistema Gerenciador de Peso - EBM Metrologia");
            primaryStage.getIcons().addAll(new Image(Principal.class.getResourceAsStream("/imgs/ebmico.jpg")));
            primaryStage.setScene(new Scene(root));
            Thread.sleep(6000);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent arg0) {
                    System.exit(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    public static void initErrorLayout(){
        try {
            Parent root = FXMLLoader.load(Principal.class.getResource("views/erro.fxml"));
            errorStage.setTitle("Sistema Gerenciador de Peso - EBM Metrologia");
            errorStage.getIcons().addAll(new Image(Principal.class.getResourceAsStream("/imgs/ebmico.jpg")));
            errorStage.setScene(new Scene(root));
            errorStage.setResizable(false);
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
    
    public static void closePrimaryStage(){
        primaryStage.close();
    }
    
    public static void closeErrorStage(){
        errorStage.close();
    }
    
    public static void loadScene(Scene scene, String titlePage) {
         if(secondStage == null){   
            secondStage = new Stage();
            secondStage.initModality(Modality.WINDOW_MODAL);
            secondStage.initOwner(primaryStage);
            secondStage.getIcons().add(new Image(Principal.class.getResourceAsStream("/imgs/ebmico.jpg")));
            secondStage.setResizable(false);
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
    
    //PEGA DADOS DO ARQUIVO
    public LerSerial getProperties() {
        Properties prop = new Properties();
        try {
            // le o arquivo
            prop.load(getClass().getResourceAsStream("/lerserialbalanca/properties/config.properties"));
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
    
    
    
    public static Scene sobreScene(){
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("views/sobre.fxml"));
            scene = new Scene(root, 400, 230);
            
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        
        return scene;
    }
    
    public static Scene relatorioScene(){
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("views/relatorio.fxml"));
            scene = new Scene(root, 400, 230);
            
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        
        return scene;
    }

    public static Scene configScene(){
        Parent root;
        Scene scene = null;
        try {
            root = FXMLLoader.load(Principal.class.getResource("views/config.fxml"));
            scene = new Scene(root, 400, 230);
            
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        
        return scene;
    }
    
    
}