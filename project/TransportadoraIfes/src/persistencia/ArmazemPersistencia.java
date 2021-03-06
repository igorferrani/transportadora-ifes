/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import Model.Armazem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Igor Ferrani
 */
public class ArmazemPersistencia {
    
    public int insertRecord(Armazem armazem, java.sql.Connection con) throws Exception{
        int codArmazem = 0;
        try {
            ConnectionBd connectionBd = new ConnectionBd();
            con = connectionBd.getConnection();
            
            String sql = "INSERT INTO armazem VALUES(0, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, armazem.getNomArmazem());
            stmt.setDouble(2, armazem.getCodEndereco());
            stmt.executeUpdate();
            
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next())
                codArmazem = generatedKeys.getInt(1);
            
        } catch (SQLException e){
            throw new Exception("Error SQLException ("+this.getClass().getName()+"): " + e.getMessage());
        } catch (Exception e){
            throw new Exception("Error Exception ("+this.getClass().getName()+"): " + e.getMessage());
        }
        return codArmazem;
    }
    
    public ResultSet selectAllRecords(java.sql.Connection con) throws Exception{
        try {            
            String sql = "SELECT * FROM armazem";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e){
            throw new Exception("Error SQLException ("+this.getClass().getName()+"): " + e.getMessage());
        } catch (Exception e){
            throw new Exception("Error Exception ("+this.getClass().getName()+"): " + e.getMessage());
        }
    }
}
