/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Desenvolvimento
 */
public class BrowserLaunch {    
    public static void openURL(String url)
    {
        url = url.replace("\\", "/").replaceFirst("/", "");
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
    
    public static void openPrint(String file){
        String url = new File("").getAbsolutePath()+"\\src\\"+file+"";
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
        
    }
}
