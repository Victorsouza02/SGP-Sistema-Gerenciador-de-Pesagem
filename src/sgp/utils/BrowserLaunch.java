/*
    *CLASSE : BrowserLaunch
    *FUNCÃO : Metodos para abrir navegador padrão com uma URL
 */
package sgp.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class BrowserLaunch {
    
    //ABRE O NAVEGADOR PADRÃO COM A URL ESPECIFICADA
    public static void openURL(String url) {
        String sourceFilePathStr = url;
        File source = new File(sourceFilePathStr);
        url = source.toURI().toString();
        Desktop desktop = null;
        desktop = Desktop.getDesktop();

        try {
            URI uri = new URI(url);
            desktop.browse(uri);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
    }


}
