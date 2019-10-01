/*
 * CLASSE : Conexao
 * FUNÇÃO : Conectar com o banco de dados SQLITE
 */
package lerserialbalanca.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Desenvolvimento
 */
public class Conexao {
    private static Connection conexao;
    
    public Conexao() {
        try{
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:./db/dados.db";
            //String url = "jdbc:sqlite:db/dados.db"; 
            conexao = DriverManager.getConnection(url);
           //System.out.println("Conexao Banco OK");
        } catch (SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public Connection getConexao(){
        return conexao;
    }
}
