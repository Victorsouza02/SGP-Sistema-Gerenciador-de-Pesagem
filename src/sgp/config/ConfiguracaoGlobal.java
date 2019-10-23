/*
CLASSE: CONFIGURACÃO GLOBAL
FUNÇÃO: CONFIGURAR VARIAVEIS DE IMG E CAMINHO DE ARQUIVOS
 */
package sgp.config;

/**
*
 * @author Desenvolvimento
 */
public class ConfiguracaoGlobal {
    //**************** CONFIGURAÇÃO VISUAL **********************
    
    //********************* TELA PRINCIPAL
    //ICONE DO APLICATIVO
    private static final String ICONE_IMG= "empresaicone.png";
    //IMAGEM DO DISPLAY
    private static final String DISPLAY_PRINCIPAL_IMG = "Display.png";
    //TITULO DA TELA INICIAL
    private static final String TITULO_INICIAL = "Sistema Gerenciador de Pesagem";
    
    //******************* TELA SOBRE
    private static final String LOGO_EMPRESA = "empresaicone.png";
    private static final String NOME_EMPRESA = "VictorSouza02";
    private static final String VERSAO = "1.00";
    private static final String TELEFONE = "(21) 0000-0000";
    private static final String SITE = "site.com.br";
    
    //***********************************************************
    //Ativa a proteção de pen drive
    private static final boolean PROTECAO = false;


    public static String getTITULO_INICIAL() {
        return TITULO_INICIAL;
    }

    public static String getNOME_EMPRESA() {
        return NOME_EMPRESA;
    }

    public static String getVERSAO() {
        return VERSAO;
    }

    public static String getTELEFONE() {
        return TELEFONE;
    }

    public static String getSITE() {
        return SITE;
    }

    public static String getICONE_IMG() {
        return ICONE_IMG;
    }

    public static String getDISPLAY_PRINCIPAL_IMG() {
        return DISPLAY_PRINCIPAL_IMG;
    }

    public static String getLOGO_EMPRESA() {
        return LOGO_EMPRESA;
    }

    public static boolean isPROTECAO() {
        return PROTECAO;
    }
    
    
    
    

    
    
    
    
    
    
    
    
}
