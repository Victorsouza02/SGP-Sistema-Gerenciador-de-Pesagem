/*
    *CLASSE : Formatacao
    *FUNCÃO : Metodos de formatação de campos
 */
package lerserialbalanca.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author Desenvolvimento
 */
public class Formatacao {
    //METODO PARA ADICIONAR LIMITE DE TEXTO EM UM CAMPO

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }

    public static void onlyNumber(final TextField tf) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (!newValue.matches("\\d*")) {
                    tf.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    public static void estabilizacaoDisplay(Label labelEstabilizacao, String codEstabilidade) {
        switch (codEstabilidade) {
            case "E":
                labelEstabilizacao.setText("Estável");
                labelEstabilizacao.setStyle("-fx-text-fill: green;");
                break;
            case "O":
                labelEstabilizacao.setText("Oscilando");
                labelEstabilizacao.setStyle("-fx-text-fill: red;");
                break;
            case "SOB":
                labelEstabilizacao.setText("Sobrecarga");
                labelEstabilizacao.setStyle("-fx-text-fill: yellow;");
                break;
            case "SAT":
                break;

        }
    }

    public static Map<String, String> formatarDados3101C(String dado) {
        Map<String, String> dados = new HashMap<String, String>();

        //String dado = dataSerial();
        String peso_bru = "";
        String peso_liq = "";
        String tara = "";
        String estabilidade = "";

        boolean sobrecarga = dado.contains("S<BRE");
        boolean temVirgula = dado.contains(",");
        boolean comTara = false;
        int casasDecimais = 0;

        //ALOCAÇÃO DE VALORES
        if (temVirgula) { //SE TIVER VIRGULA NOS DADOS
            peso_bru = dado.substring(3, 10).replaceAll(",", "."); // PARTE ( 00,000) 
            peso_liq = dado.substring(3, 10).replaceAll(",", "."); // PARTE ( 00,000) 
            tara = dado.substring(13, 20).replaceAll(",", "."); // PARTE ( 00,000) 
            casasDecimais = (6 - tara.indexOf(".")); // CONTA QUANTAS CASAS DECIMAIS
        } else if (!temVirgula && !sobrecarga) { //SE NAO TIVER VIRGULA OU SOBRECARGA
            peso_bru = dado.substring(3, 9); // PARTE ( 00000) 
            peso_liq = dado.substring(3, 9); // PARTE ( 00000) 
            tara = dado.substring(12, 18); // PARTE ( 00000) 
        }

        //
        if (!sobrecarga) { //SE NÃO ESTIVER COM SOBRECARGA
            List<String> pesos = new ArrayList<String>();
            pesos.add(peso_bru);
            pesos.add(peso_liq);
            pesos.add(tara);
            int cnt = 0;
            for (String peso : pesos) {
                if (peso.equals(" 00000")) {
                    peso = "0";
                } else {
                    if (peso.contains("-")) {
                        if (peso.contains(".")) {
                            int pos = peso.indexOf(".");
                            peso = "-" + peso.replace(peso.substring(0, (pos - 1)), peso.substring(1, (pos - 1)).replaceFirst("0*", ""));
                        } else {
                            peso = "-" + peso.substring(1, 6).replaceFirst("0*", ""); //OK
                        }
                    } else {
                        if (peso.contains(".")) {
                            int pos = peso.indexOf(".");
                            peso = (pos <= 2) ? peso.replaceFirst(peso.substring(1, (pos - 1)), peso.substring(1, (pos - 1)).replaceFirst("0*", "")) : peso.replace(peso.substring(1, (pos - 1)), peso.substring(1, (pos - 1)).replaceFirst("0*", ""));
                        } else {
                            try {
                                peso = peso.substring(1, 6).replaceFirst("0*", ""); //OK
                            } catch (Exception ex) {
                                peso = peso.substring(1, 5).replaceFirst("0*", ""); //OK
                            }
                        }
                    }
                }
                if (peso.contains(" ")) {
                    peso = peso.replace(" ", "");
                };
                pesos.set(cnt, peso);
                switch (cnt) {
                    case 0:
                        peso_bru = peso;
                        break;
                    case 1:
                        peso_liq = peso;
                        break;
                    case 2:
                        if (Double.parseDouble(peso) != 0.0) {
                            comTara = true;
                        }
                        tara = peso;
                        break;
                }
                cnt++;
            }

            if (comTara) {
                if (temVirgula) {
                    Float pb = Float.parseFloat(peso_liq) + Float.parseFloat(tara);
                    dados.put("peso_bru", formatoDecimal(casasDecimais, pb));
                } else {
                    int pb = Integer.parseInt(peso_liq) + Integer.parseInt(tara);
                    dados.put("peso_bru", String.valueOf(pb));
                }
            } else {
                dados.put("peso_bru", peso_bru);
            }
            dados.put("estavel", !dado.contains("*") ? "E" : "O");
            dados.put("peso_liq", peso_liq);
            dados.put("tara", tara);
        } else { //SE ESTIVER COM SOBRECARGA
            dados.put("estavel", "SOB");
            dados.put("peso_bru", "0");
            dados.put("peso_liq", "0");
            dados.put("tara", "0");
        }

        System.out.println("Peso Bruto: " + dados.get("peso_bru") + "/  Peso Liquido : " + dados.get("peso_liq") + "/  Tara : " + dados.get("tara"));

        return dados;
    }

    public static Map<String, String> formatarDadosWT1000N(String dado) {
        Map<String, String> dados = new HashMap<String, String>();
        boolean sobrecarga = dado.contains("OL");

        if (!sobrecarga) {
            String peso_bru = dado.substring(2, 9);
            String peso_liq = dado.substring(18, 25);

            List<String> pesos = new ArrayList<String>();
            pesos.add(peso_bru);
            pesos.add(peso_liq);
            int cnt = 0;
            for (String peso : pesos) {
                if (peso.equals("000000 ") || peso.equals("0000000")) {
                    peso = "0";
                } else {
                    if (peso.contains("-")) {
                        if (peso.contains(".")) {
                            int pos = peso.indexOf(".");
                            peso = "-" + peso.replace(peso.substring(0, (pos - 1)), peso.substring(1, (pos - 1)).replaceFirst("0*", ""));
                        } else {
                            peso = "-" + peso.substring(1, 7).replaceFirst("0*", "");
                        }
                    } else {
                        if (peso.contains(".")) {
                            int pos = peso.indexOf(".");
                            peso = (pos <= 3) ? peso.replaceFirst(peso.substring(0, (pos - 1)), peso.substring(0, (pos - 1)).replaceFirst("0*", "")) : peso.replace(peso.substring(0, (pos - 1)), peso.substring(0, (pos - 1)).replaceFirst("0*", ""));
                        } else {
                            peso = peso.replaceFirst("0*", "");
                        }
                    }
                }
                pesos.set(cnt, peso);
                switch (cnt) {
                    case 0:
                        peso_bru = peso;
                        break;
                    case 1:
                        peso_liq = peso;
                        break;
                }
                cnt++;
            }
            dados.put("estavel", dado.substring(0, 1).equals("0") ? "E" : "O");
            dados.put("peso_bru", peso_bru);
            dados.put("tara", dado.substring(10, 17));
            dados.put("peso_liq", peso_liq);
        } else {
            dados.put("estavel", "SOB");
            dados.put("peso_bru", "0");
            dados.put("tara", "0");
            dados.put("peso_liq", "0");
        }

        return dados;
    }

    public static int qtdCasasDecimais(String num) {
        return ((num.length() - 1) - num.indexOf("."));
    }

    public static String formatoDecimalDouble(int casasdecimais, Double valor) {
        String formato = "";
        switch (casasdecimais) {
            case 1:
                formato = "0.0";
                break;
            case 2:
                formato = "0.00";
                break;
            case 3:
                formato = "0.000";
                break;
            case 4:
                formato = "0.0000";
                break;
            case 5:
                formato = "0.00000";
                break;
            default:
                formato = "0.0";
                break;
        }

        DecimalFormat df = new DecimalFormat(formato);
        return df.format(valor).replaceAll(",", ".");
    }

    public static String formatoDecimal(int casasdecimais, Float valor) {
        String formato = "";
        switch (casasdecimais) {
            case 1:
                formato = "0.0";
                break;
            case 2:
                formato = "0.00";
                break;
            case 3:
                formato = "0.000";
                break;
            case 4:
                formato = "0.0000";
                break;
            case 5:
                formato = "0.00000";
                break;
            default:
                formato = "0.0";
                break;
        }

        DecimalFormat df = new DecimalFormat(formato);
        return df.format(valor).replaceAll(",", ".");
    }

    private String verificarEstabilidade(String statusEstabilidade) {
        String codigoEstabilidade = "";
        switch (statusEstabilidade) {
            case "Estável":
                codigoEstabilidade = "E";
                break;
            case "Oscilando":
                codigoEstabilidade = "O";
                break;
            case "Sobrecarga":
                codigoEstabilidade = "SOB";
                break;
            case "Saturado":
                codigoEstabilidade = "SAT";
                break;
        }
        return codigoEstabilidade;
    }

}
