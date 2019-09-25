/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca;
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
import javax.swing.JOptionPane;
import lerserialbalanca.models.Autorizacao;


public class Principal extends Application {

    public static void main(String[] args) {
        launch(args);
        
    }
   
    private static Stage primaryStage;
    private static Stage secondStage;

    @Override
    public void start(Stage primaryStage) {
        Autorizacao pd = new Autorizacao();
        if(pd.isAutorizado()){
            this.primaryStage = primaryStage;
            initRootLayout();
        } else {
            JOptionPane.showMessageDialog(null, "Você não está autorizado a executar este programa, verifique seu Pen Drive.", "Não autorizado", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    /**
     * Inicializa o root layout (layout base).
     */
    public void initRootLayout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("views/principal.fxml"));
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
    
    public static void loadScene(String nameFile, String titlePage) {
        Parent root;
        try {
         if(secondStage == null){   
            root = FXMLLoader.load(Principal.class.getResource(nameFile));
            Scene scene = new Scene(root, 400, 230);
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