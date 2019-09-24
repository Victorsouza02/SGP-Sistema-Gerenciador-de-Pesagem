/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    public Conexao() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:./db/dados.db";
        //String url = "jdbc:sqlite:db/dados.db"; 
        conexao = DriverManager.getConnection(url);
        System.out.println("Conexao OK");
    }
    
    public Connection getConexao(){
        return conexao;
    }
}
