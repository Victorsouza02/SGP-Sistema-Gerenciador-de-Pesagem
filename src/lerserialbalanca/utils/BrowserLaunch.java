/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import lerserialbalanca.Principal;

/**
 *
 * @author Desenvolvimento
 */
public class BrowserLaunch {    
    public static void openURL(String url)
    {
        Desktop desktop = null;  
        desktop = Desktop.getDesktop();  
        URI uri = null;  
        try {  
                   uri = new URI(url);  
                   desktop.browse(uri);  
        }  
        catch(IOException ioe) {  
                   ioe.printStackTrace();  
        }  
        catch(URISyntaxException use) {  
                   use.printStackTrace();  
        }  
    }
    
    public static void open(){
        String url = new File("").getAbsolutePath()+"\\PRINT.HTML";
        url = url.replace("\\", "/");
        Desktop desktop = null;  
        desktop = Desktop.getDesktop();  
        URI uri = null;  
        try {  
                   uri = new URI(url);  
                   desktop.browse(uri);  
        }  
        catch(IOException ioe) {  
                   ioe.printStackTrace();  
        }  
        catch(URISyntaxException use) {  
                   use.printStackTrace();  
        }  
      //Principal.class.getResourceAsStream("../../../PRINT.HTML").toString());  
        
    }
}
