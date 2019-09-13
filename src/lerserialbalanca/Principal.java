/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lerserialbalanca.models.ManipuladorEtiqueta;


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
   
    private Stage primaryStage;

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
            Parent root = FXMLLoader.load(getClass().getResource("views/TelaInicial.fxml"));
            primaryStage.setTitle("EBM Metrologia");
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
    
    /**
     * Retorna o palco principal.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    
}