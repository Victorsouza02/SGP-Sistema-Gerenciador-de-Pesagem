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
import lerserialbalanca.Principal;
import lerserialbalanca.persistence.AcoesSQL;
import lerserialbalanca.utils.BrowserLaunch;

/**
 *
 * @author Desenvolvimento
 */
public class ManipuladorEtiqueta {

    private static String path_txt = new File("").getAbsolutePath() + "\\print\\etiqueta.txt";
    private static String path_html = new File("").getAbsolutePath() + "\\print\\PRINT.HTML";
    private static String path_html_report = new File("").getAbsolutePath() + "\\print\\report.html";
    private static String fonte = Principal.getFonte();

    public static void fazerEtiquetaHtml(String placa) {
        try {
            AcoesSQL acao = new AcoesSQL();
            Registro reg = acao.getUltimoRegistro(placa);
            //ARQUIVO DE LEITURA
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(path_txt), StandardCharsets.ISO_8859_1));
            String linha = "";
            //ARQUIVO DE ESCRITA
            OutputStreamWriter buffWrite = new OutputStreamWriter(new FileOutputStream(path_html), StandardCharsets.UTF_8);

            buffWrite.write("");
            buffWrite.append("<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body><pre style='font-size:"+fonte+"px'><center>");
            while (true) {
                if (linha != null) {
                    String[] variaveis = new String[]{"$ID", "$PRODUTO", "$FORNECEDOR", "$MOTORISTA", "$PLACA", "$DT_ENTRADA",
                        "$HH_ENTRADA", "$PS_ENTRADA", "$DT_SAIDA", "$HH_SAIDA", "$PS_SAIDA", "$PS_LIQ"};
                    for (String str : variaveis) {
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
            BrowserLaunch.openURL(path_html);
        } catch (IOException iEx) {
            System.out.println(iEx.getMessage());
        }
    }

    public static void fazerRelatorioHtml(List<Registro> registros, LocalDate inicio, LocalDate fim) {
        try {
            String data_ini = inicio.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            String data_fim = fim.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            String linha = "";

            //ARQUIVO PARA ESCRITA
            OutputStreamWriter buffWrite = new OutputStreamWriter(new FileOutputStream(path_html_report), StandardCharsets.UTF_8);

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
                buffWrite.append("<tr><td>" + reg.getId() + "</td><td>" + reg.getPlaca() + "</td><td>" + reg.getNome() + "</td><td>" + reg.getDt_entrada() + "</td><td>" + reg.getH_entrada() + "</td><td>" + reg.getPs_entrada()
                        + " kg</td><td>" + ((reg.getDt_saida() == "") ? "---" : reg.getDt_saida()) + "</td><td>" + ((reg.getH_saida() == null) ? "---" : reg.getH_saida()) + "</td><td>" + ((reg.getPs_saida() == null) ? "---" : reg.getPs_saida()) + " kg</td><td>" + ((reg.getPs_liquido() == null) ? "---" : reg.getPs_liquido()) + " kg</td><td>"
                        + reg.getFornecedor() + "</td><td>" + reg.getProduto() + "</td></tr>");
            }
            buffWrite.append("</table></body></html>");
            buffWrite.close();
            BrowserLaunch.openURL(path_html_report);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public static void recriarEtiqueta(int id){
        AcoesSQL acao = new AcoesSQL();
        Registro reg = acao.pegarRegistro(id);
        
        try {
            //ARQUIVO DE LEITURA
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(path_txt), StandardCharsets.ISO_8859_1));
            String linha = "";
            //ARQUIVO DE ESCRITA
            OutputStreamWriter buffWrite = new OutputStreamWriter(new FileOutputStream(path_html), StandardCharsets.UTF_8);

            buffWrite.write("");
            buffWrite.append("<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body><pre style='font-size:"+fonte+"px'><center>");
            while (true) {
                if (linha != null) {
                    String[] variaveis = new String[]{"$ID", "$PRODUTO", "$FORNECEDOR", "$MOTORISTA", "$PLACA", "$DT_ENTRADA",
                        "$HH_ENTRADA", "$PS_ENTRADA", "$DT_SAIDA", "$HH_SAIDA", "$PS_SAIDA", "$PS_LIQ"};
                    for (String str : variaveis) {
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
            BrowserLaunch.openURL(path_html);
        } catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public static String colocarValores(String linha, String var, Registro reg) {
        SimpleDateFormat dateFormatSql = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatView = new SimpleDateFormat("dd/MM/yyyy");
        Date data = new Date();
        try {
            switch (var) {
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

    public static String getPath_txt() {
        return path_txt;
    }

    public static void setPath_txt(String path_txt) {
        ManipuladorEtiqueta.path_txt = path_txt;
    }

    public static String getPath_html() {
        return path_html;
    }

    public static void setPath_html(String path_html) {
        ManipuladorEtiqueta.path_html = path_html;
    }

    public static String getPath_html_report() {
        return path_html_report;
    }

    public static void setPath_html_report(String path_html_report) {
        ManipuladorEtiqueta.path_html_report = path_html_report;
    }

}
