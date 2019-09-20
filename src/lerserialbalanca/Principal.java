/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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




/**
 *
 * @author Desenvolvimento
 */
public class Principal extends Application {

    /**
     * @param args the command line arguments
     */
    //private String config = getProperties();
    
    public static void main(String[] args) {
        launch(args);
        
    }
   
    private static Stage primaryStage;
    private static Stage secondStage;

    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
        this.primaryStage = primaryStage;
        initRootLayout();
    }
    
    /**
     * Inicializa o root layout (layout base).
     */
    public void initRootLayout() throws URISyntaxException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("views/principal.fxml"));
            primaryStage.setTitle("Sistema Gerenciador de Peso - EBM Metrologia");
            primaryStage.getIcons().addAll(new Image("file:/C:/Users/Desenvolvimento/Documents/Java/src/ebmico.jpg"));
            //primaryStage.getIcons().addAll(new Image(new FileInputStream("./src/ebmico.jpg")));
            primaryStage.setScene(new Scene(root));
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
    
    public static void loadScene(String nameFile, String titlePage) {
        Parent root;
        try {
         if(secondStage == null){   
            root = FXMLLoader.load(Principal.class.getResource(nameFile));
            Scene scene = new Scene(root, 600, 400);
            secondStage = new Stage();
            secondStage.initModality(Modality.WINDOW_MODAL);
            secondStage.initOwner(primaryStage);
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
        } catch (IOException e) {
         e.printStackTrace();
        }
    }
    
    /**
     * Retorna o palco principal
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    
}