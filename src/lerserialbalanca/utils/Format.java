/*
    *CLASSE : Format
    *FUNCÃO : Metodos de formatação de campos
 */
package lerserialbalanca.utils;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 *
 * @author Desenvolvimento
 */
public class Format {
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

}
