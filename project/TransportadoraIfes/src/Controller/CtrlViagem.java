/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.ItemRemessa;
import Model.Remessa;
import Model.Viagem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
import persistencia.ConnectionBd;
import persistencia.ItemRemessaPersistencia;
import persistencia.RemessaPersistencia;
import persistencia.ViagemPersistencia;
import transportadoraifes.Util;

/**
 *
 * @author Weliton de Oliveira
 */
public class CtrlViagem {
    
    public ArrayList insertRecord(Viagem viagem, DefaultListModel listRemessa) {
        ArrayList list = new ArrayList();
        try {
            java.sql.Connection con = ConnectionBd.getConnection();
            
            //int codArmazem = obj.getCodArmazem();
            //int codViagem = obj.getCodViagem();
            //int numViagem = obj.getNumViagem();
            double qtdPesoViagem = 0;
            double qtdVolumeViagem = 0;
            
            con.setAutoCommit(false); //transaction block start
            
            // Insere a viagem
            ViagemPersistencia viagemPersistencia = new ViagemPersistencia();
            int codViagem = viagemPersistencia.insertRecord(viagem, con);

            /* INSERCAO DAS REMESSAS */
            for(int i = 0; i < listRemessa.getSize(); i++){
                Remessa remessa = (Remessa) listRemessa.get(i);
                remessa.setCodViagem(codViagem);
                
                // Insere a remessa
                RemessaPersistencia remessaPersistencia = new RemessaPersistencia();
                int codRemessa = remessaPersistencia.insertRecord(remessa, con);
                
                for(int r = 0; r < remessa.getArrayItemRemessa().getSize(); r++){
                    // Realiza a busca do produto no banco para obter os dados
                    
                    
                    ItemRemessa itemRemessa = (ItemRemessa) remessa.getArrayItemRemessa().get(r);
                    itemRemessa.setCodRemessa(codRemessa);
                    
                    double qtdPesoProduto = itemRemessa.getProduto().getQtdPesoProduto();
                    double qtdVolumeProduto = itemRemessa.getProduto().getQtdVolumeProduto();
                    
                    // Realiza somatorio para a viagem
                    qtdPesoViagem += qtdPesoProduto * itemRemessa.getQtdProduto();
                    qtdVolumeViagem += qtdVolumeProduto * itemRemessa.getQtdProduto();

                    // Insere o item de remessa
                    ItemRemessaPersistencia itemRemessaPersistencia = new ItemRemessaPersistencia();
                    itemRemessaPersistencia.insertRecord(itemRemessa, con);
                }
            }
            
            viagem.setQtdPesoViagem(qtdPesoViagem);
            viagem.setQtdVolumeViagem(qtdVolumeViagem);
            viagem.setCodViagem(codViagem);
            
            // Altera o peso e volume da viagem
            viagemPersistencia.updateRecord(viagem, con);
            
            con.commit(); //transaction block end
            con.close();
            
        } catch(SQLException e){
            Util.showCatch(e.getMessage());
        } catch(Exception e) {
            Util.showCatch(e.getMessage());
        }
        return list;
    }
    
    public void setDataTable(DefaultTableModel tableModel) throws SQLException, Exception{
        
        ViagemPersistencia viagemPersistencia = new ViagemPersistencia();
        java.sql.Connection con = ConnectionBd.getConnection();
        ResultSet rs;
        
        try {
            rs  = viagemPersistencia.selectAllRecords(con);
            while(rs.next()){
                tableModel.addRow( new Object[] { 
                    rs.getInt("codViagem"),
                    rs.getString("numViagem"),
                    rs.getInt("codArmazem"),
                    rs.getInt("codCaminhao"),
                    rs.getDouble("qtdPesoViagem"),
                    rs.getDouble("qtdVolumeViagem")
                });
            }    
        } catch(SQLException e){
            throw new SQLException("Error SQLException (CtrlViagem): " + e.getMessage());
        } catch(Exception e){
            throw new Exception("Error Exception (CtrlViagem): " + e.getMessage());
        }
    }
}