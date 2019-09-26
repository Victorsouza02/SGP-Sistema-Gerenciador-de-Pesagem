
package lerserialbalanca.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class Propriedades {
    private Properties prop;
    private String porta;
    private String equipamento;
    private String fonte;
    
    public Propriedades(){
        try {
            prop = new Properties();
            prop.load(getClass().getResourceAsStream("/lerserialbalanca/properties/config.properties"));
            //prop.load(new FileInputStream(new File("").getAbsolutePath() + "\\config\\config.properties"));
            carregarPropriedades();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void carregarPropriedades(){
        setPorta(prop.getProperty("porta"));
        setEquipamento(prop.getProperty("equipamento"));
        setFonte(prop.getProperty("tamanhofonte"));
    }
    
    public void alterarPropriedades(String porta, String equipamento , String fonte){
        try {
            prop.setProperty("porta", porta);
            prop.setProperty("equipamento", equipamento);
            prop.setProperty("tamanhofonte", fonte);
            prop.store(new FileOutputStream(new File("").getAbsolutePath() + "\\src\\lerserialbalanca\\properties\\config.properties"), null);
            //prop.store(new FileOutputStream(new File("").getAbsolutePath() + "\\config\\config.properties"), null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }
    
    
    
}
