/*
 * CLASSE : Impressao
 * Função : Gerenciar tudo relacionado a impressão de cupom/relatórios.
*/
package lerserialbalanca.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import lerserialbalanca.main.Principal;
import lerserialbalanca.persistence.AcoesSQL;
import lerserialbalanca.utils.BrowserLaunch;


public class Impressao {

    private static final String PATH_TXT = new File("").getAbsolutePath() + "\\print\\etiqueta.txt";
    private static final String PATH_HTML = new File("").getAbsolutePath() + "\\print\\PRINT.HTML";
    private static final String PATH_HTML_REPORT = new File("").getAbsolutePath() + "\\print\\report.html";
    private static final String FONTE = Principal.getFonte();
    private static final String[] substituir = new String[]{"$NOMEEMPRESA","$ENDERECOEMPRESA","$TELEMPRESA","$ID", "$PRODUTO", "$FORNECEDOR", "$MOTORISTA", "$PLACA", "$DT_ENTRADA",
                        "$HH_ENTRADA", "$PS_ENTRADA", "$DT_SAIDA", "$HH_SAIDA", "$PS_SAIDA", "$PS_LIQ"};

    public static void fazerEtiquetaHtml(String placa) { //FAZER O ETIQUETA/CUPOM EM HTML PARA IMPRESSÃO
        try {
            AcoesSQL acao = new AcoesSQL();
            //PEGA O ULTIMO REGISTRO DA PLACA NO BANCO DE DADOS
            Registro reg = acao.getUltimoRegistro(placa);
            //ARQUIVO DE LEITURA
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(PATH_TXT), StandardCharsets.ISO_8859_1));
            String linha = "";
            //ARQUIVO DE ESCRITA
            OutputStreamWriter buffWrite = new OutputStreamWriter(new FileOutputStream(PATH_HTML), StandardCharsets.UTF_8);

            buffWrite.write("");
            buffWrite.append("<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body><pre style='font-size:"+FONTE+"px'><center>");
            while (true) {
                if (linha != null) {
                    for (String str : substituir) {
                        //VERIFICA SE A LINHA TEM A VARIAVEL DE SUBSTITUIÇÃO E COLOCA UM VALOR NO LOCAL
                        if (linha.contains(str)) {
                            linha = colocarValores(linha, str, reg);
                        }
                    }
                    buffWrite.append(linha + "<br>");
                } else {
                    break;
                }
                linha = buffRead.readLine();
            }
            buffWrite.append("</pre><script>print()</script></body></html>");
            buffRead.close();
            buffWrite.close();
            BrowserLaunch.openURL(PATH_HTML);
        } catch (IOException iEx) {
            System.out.println(iEx.getMessage());
        }
    }

    //LISTA OS RELATÓRIOS DO BANCO DE DADOS DE UM PERIODO EM UM ARQUIVO HTML
    public static void fazerRelatorioHtml(List<Registro> registros, LocalDate inicio, LocalDate fim) {
        try {
            String data_ini = inicio.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            String data_fim = fim.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            String linha = "";

            //ARQUIVO PARA ESCRITA
            OutputStreamWriter buffWrite = new OutputStreamWriter(new FileOutputStream(PATH_HTML_REPORT), StandardCharsets.UTF_8);

            buffWrite.write("");
            buffWrite.append("<!DOCTYPE html><html>\n"
                    + "<head>"
                    + "<style type=\"text/css\">"
                    + "@media print {br.pb {page-break-after:always}}"
                    + "body {font-family:verdana; font-size: 9px}"
                    + "th {font-family:verdana; font-size: 10px; text-align: left}"
                    + "td {font-family:verdana; font-size: 9px}"
                    + "</style>"
                    + "</head>"
                    + "<body> <table>"
                    + "<tr><td style=\"font-size: 12px\">RELATÓRIO DE PESAGENS NO PERÍODO DE " + data_ini + " ATÉ " + data_fim + "</td></tr>"
                    + "</table>"
                    + "<br><table width=\"100%\">"
                    + "<tr><th>ID</th><th>PLACA</th><th>MOTORISTA</th><th>DT ENTRADA</th><th>H ENTRADA</th><th>PESO ENTRADA</th><th>DT SAIDA</th><th>H SAIDA</th><th>PESO SAIDA</th><th>PESO LIQ</th><th>FORNECEDOR</th><th>PRODUTO</th></tr>");
            for (Registro reg : registros) {
                //A CADA REGISTRO ENCONTRADO
                buffWrite.append("<tr><td>" + reg.getId() + "</td><td>" + reg.getPlaca() + "</td><td>" + reg.getNome() + "</td><td>" + reg.getDt_entrada() + "</td><td>" + reg.getH_entrada() + "</td><td>" + reg.getPs_entrada()
                        + " kg</td><td>" + ((reg.getDt_saida() == "") ? "---" : reg.getDt_saida()) + "</td><td>" + ((reg.getH_saida() == null) ? "---" : reg.getH_saida()) + "</td><td>" + ((reg.getPs_saida() == null) ? "---" : reg.getPs_saida()) + " kg</td><td>" + ((reg.getPs_liquido() == null) ? "---" : reg.getPs_liquido()) + " kg</td><td>"
                        + reg.getFornecedor() + "</td><td>" + reg.getProduto() + "</td></tr>");
            }
            buffWrite.append("</table></body></html>");
            buffWrite.close();
            BrowserLaunch.openURL(PATH_HTML_REPORT);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    //LISTA OS RELATÓRIOS DO BANCO DE DADOS DE ACORDO COM A PLACA EM UM ARQUIVO HTML
    public static void fazerRelatorioHtml(List<Registro> registros , String placa) {
        try {
            String linha = "";

            //ARQUIVO PARA ESCRITA
            OutputStreamWriter buffWrite = new OutputStreamWriter(new FileOutputStream(PATH_HTML_REPORT), StandardCharsets.UTF_8);

            buffWrite.write("");
            buffWrite.append("<!DOCTYPE html><html>\n"
                    + "<head>"
                    + "<style type=\"text/css\">"
                    + "@media print {br.pb {page-break-after:always}}"
                    + "body {font-family:verdana; font-size: 9px}"
                    + "th {font-family:verdana; font-size: 10px; text-align: left}"
                    + "td {font-family:verdana; font-size: 9px}"
                    + "</style>"
                    + "</head>"
                    + "<body> <table>"
                    + "<tr><td style=\"font-size: 12px\">RELATÓRIO DE PESAGENS DA PLACA " + placa + "</td></tr>"
                    + "</table>"
                    + "<br><table width=\"100%\">"
                    + "<tr><th>ID</th><th>PLACA</th><th>MOTORISTA</th><th>DT ENTRADA</th><th>H ENTRADA</th><th>PESO ENTRADA</th><th>DT SAIDA</th><th>H SAIDA</th><th>PESO SAIDA</th><th>PESO LIQ</th><th>FORNECEDOR</th><th>PRODUTO</th></tr>");
            for (Registro reg : registros) {
                buffWrite.append("<tr><td>" + reg.getId() + "</td><td>" + reg.getPlaca() + "</td><td>" + reg.getNome() + "</td><td>" + reg.getDt_entrada() + "</td><td>" + reg.getH_entrada() + "</td><td>" + reg.getPs_entrada()
                        + " kg</td><td>" + ((reg.getDt_saida() == "") ? "---" : reg.getDt_saida()) + "</td><td>" + ((reg.getH_saida() == null) ? "---" : reg.getH_saida()) + "</td><td>" + ((reg.getPs_saida() == null) ? "---" : reg.getPs_saida()) + " kg</td><td>" + ((reg.getPs_liquido() == null) ? "---" : reg.getPs_liquido()) + " kg</td><td>"
                        + reg.getFornecedor() + "</td><td>" + reg.getProduto() + "</td></tr>");
            }
            buffWrite.append("</table></body></html>");
            buffWrite.close();
            BrowserLaunch.openURL(PATH_HTML_REPORT);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    //RECRIA A IMPRESSÃO DA ETIQUETA/CUPOM
    public static void recriarEtiqueta(int id){
        AcoesSQL acao = new AcoesSQL();
        Registro reg = acao.pegarRegistro(id);
        
        try {
            //ARQUIVO DE LEITURA
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(PATH_TXT), StandardCharsets.ISO_8859_1));
            String linha = "";
            //ARQUIVO DE ESCRITA
            OutputStreamWriter buffWrite = new OutputStreamWriter(new FileOutputStream(PATH_HTML), StandardCharsets.UTF_8);

            buffWrite.write("");
            buffWrite.append("<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body><pre style='font-size:"+FONTE+"px'><center>");
            while (true) {
                if (linha != null) {
                    for (String str : substituir) {
                        if (linha.contains(str)) {
                            linha = colocarValores(linha, str, reg);
                        }
                    }
                    buffWrite.append(linha + "<br>");
                } else {
                    break;
                }
                linha = buffRead.readLine();
            }
            buffWrite.append("</pre><script>print()</script></body></html>");
            buffRead.close();
            buffWrite.close();
            BrowserLaunch.openURL(PATH_HTML);
        } catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    //TROCA OS VALORES DOS CAMPOS SUBSTITUIVEIS PELOS DADOS DO REGISTRO
    public static String colocarValores(String linha, String var, Registro reg) {
        SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatView = new SimpleDateFormat("dd/MM/yyyy");
        Date data = new Date();       
        try {
            switch (var) {
                case "$NOMEEMPRESA":
                    linha = linha.replace(var, Principal.getNomeempresa());
                    break;
                    
                case "$ENDERECOEMPRESA":
                    linha = linha.replace(var, Principal.getEnderecoempresa());
                    break;
                    
                case "$TELEMPRESA":
                    linha = linha.replace(var, Principal.getTelempresa());
                    break;
                case "$ID":
                    linha = linha.replace(var, Integer.toString(reg.getId()));
                    break;
                case "$PRODUTO":
                    linha = linha.replace(var, reg.getProduto());
                    break;
                case "$FORNECEDOR":
                    linha = linha.replace(var, reg.getFornecedor());
                    break;
                case "$MOTORISTA":
                    linha = linha.replace(var, reg.getNome());
                    break;
                case "$PLACA":
                    linha = linha.replace(var, reg.getPlaca());
                    break;
                case "$DT_ENTRADA":
                    data = dateFormatSql.parse(reg.getDt_entrada());
                    String data_entrada = dateFormatView.format(data);
                    linha = linha.replace(var, data_entrada);
                    break;
                case "$HH_ENTRADA":
                    linha = linha.replace(var, reg.getH_entrada());
                    break;
                case "$PS_ENTRADA":
                    linha = linha.replace(var, reg.getPs_entrada() + "kg");
                    break;
                case "$DT_SAIDA":
                    if (reg.getDt_saida() != null) {
                        data = dateFormatSql.parse(reg.getDt_entrada());
                        String data_saida = dateFormatView.format(data);
                        linha = linha.replace(var, data_saida);
                    } else {
                        linha = linha.replace(var, "---");
                    }
                    break;
                case "$HH_SAIDA":
                    linha = linha.replace(var, (reg.getH_saida() == null) ? "---" : reg.getH_saida());
                    break;
                case "$PS_SAIDA":
                    linha = linha.replace(var, (reg.getPs_saida() == null) ? "---" : reg.getPs_saida() + "kg");
                    break;
                case "$PS_LIQ":
                    linha = linha.replace(var, (reg.getPs_liquido() == null) ? "---" : reg.getPs_liquido() + "kg");
                    break;
            }
        } catch (ParseException ex){
            System.out.println(ex.getMessage());
        }
        return linha;
    }

    //GETTERS
    
    public static String getPath_txt() {
        return PATH_TXT;
    }

    public static String getPath_html() {
        return PATH_HTML;
    }

    public static String getPath_html_report() {
        return PATH_HTML_REPORT;
    }


}
