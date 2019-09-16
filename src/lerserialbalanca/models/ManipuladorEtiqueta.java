/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lerserialbalanca.models;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Desenvolvimento
 */
public class ManipuladorEtiqueta {

    public static void fazerEtiquetaHtml(String path_txt, String path_html) throws IOException {
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(new FileInputStream(path_txt), StandardCharsets.ISO_8859_1));
        // buffRead = new BufferedReader(new FileReader(path_txt));
        String linha = "";
        OutputStreamWriter buffWrite = new OutputStreamWriter(new FileOutputStream(path_html), StandardCharsets.UTF_8);
        //BufferedWriter buffWrite = new BufferedWriter(new FileWriter(path_html,false));
        buffWrite.write("");
        buffWrite.append("<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body><pre style='font-size:20'>");
        while (true) {
            if (linha != null) {
                String[] variaveis = new String[]{"$ID", "$PRODUTO", "$FORNECEDOR", "$MOTORISTA", "$PLACA", "$DT_ENTRADA",
                    "$HH_ENTRADA", "$PS_ENTRADA", "$DT_SAIDA", "$HH_SAIDA", "$PS_SAIDA", "$PS_LIQ"};
                for (String str : variaveis) {
                    if (linha.contains(str)) {
                        linha = colocarValores(linha, str);
                    }
                }
                buffWrite.append(linha + "<br>");
                System.out.println(linha);
            } else {
                break;
            }
            linha = buffRead.readLine();
        }
        buffWrite.append("</pre><script>print()</script></body></html>");
        buffRead.close();
        buffWrite.close();
    }

    public static String colocarValores(String linha, String var) {
        switch (var) {
            case "$ID":
                linha = linha.replace(var, "ID_TESTE");
                break;
            case "$PRODUTO":
                linha = linha.replace(var, "PROD_TESTE");
                break;
            case "$FORNECEDOR":
                linha = linha.replace(var, "FORN_TESTE");
                break;
            case "$MOTORISTA":
                linha = linha.replace(var, "MOT_TESTE");
                break;
            case "$PLACA":
                linha = linha.replace(var, "PL_TESTE");
                break;
            case "$DT_ENTRADA":
                linha = linha.replace(var, "DT_EN_TESTE");
                break;
            case "$HH_ENTRADA":
                linha = linha.replace(var, "HH_EN_TESTE");
                break;
            case "$PS_ENTRADA":
                linha = linha.replace(var, "PS_EN_TESTE");
                break;
            case "$DT_SAIDA":
                linha = linha.replace(var, "DT_SAI_TESTE");
                break;
            case "$HH_SAIDA":
                linha = linha.replace(var, "HH_SAI_TESTE");
                break;
            case "$PS_SAIDA":
                linha = linha.replace(var, "PS_SAI_TESTE");
                break;
            case "$PS_LIQ":
                linha = linha.replace(var, "PS_LIQ_TESTE");
                break;
        }
        return linha;
    }
}
