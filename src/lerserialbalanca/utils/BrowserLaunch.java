
package lerserialbalanca.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class BrowserLaunch {    
    public static void openURL(String url)
    {
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
