
package sgp.config;


public class VariaveisGlobais {
    
    private static boolean erroSerialDetectado = false;
    private static boolean erroFormatDetectado = false;

    private static String mensagem = "";


    public static String getMensagem() {
        return mensagem;
    }

    public static void setMensagem(String mensagem) {
        VariaveisGlobais.mensagem = mensagem;
    }

    public static boolean isErroSerialDetectado() {
        return erroSerialDetectado;
    }

    public static void setErroSerialDetectado(boolean erroSerialDetectado) {
        VariaveisGlobais.erroSerialDetectado = erroSerialDetectado;
    }

    public static boolean isErroFormatDetectado() {
        return erroFormatDetectado;
    }

    public static void setErroFormatDetectado(boolean erroFormatDetectado) {
        VariaveisGlobais.erroFormatDetectado = erroFormatDetectado;
    }
    
    
    
    
    
    
}
