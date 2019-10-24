
package sgp.config;


public class VariaveisGlobais {
    
    private static boolean erroSerialDetectado = false;
    private static boolean erroFormatDetectado = false;
    
    private static boolean modoManual = false;
    
    private static String pesoManual = "0";

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

    public static boolean isModoManual() {
        return modoManual;
    }

    public static void setModoManual(boolean isModoManual) {
        VariaveisGlobais.modoManual = isModoManual;
    }

    public static String getPesoManual() {
        return pesoManual;
    }

    public static void setPesoManual(String pesoManual) {
        VariaveisGlobais.pesoManual = pesoManual;
    }
    
    
    
    
    
    
    
    
    
    
}
