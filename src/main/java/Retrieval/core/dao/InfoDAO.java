package Retrieval.core.dao;
import java.sql.*;

import Retrieval.core.utils.DocStruct;
import java.util.ArrayList;

public class InfoDAO {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String databaseName= "test_db";
    static final String DB_URL = "jdbc:mysql://localhost:3306/"+databaseName+"?serverTimezone=UTC&characterEncoding=utf-8";

    private Connection conn;
    private Statement stt;
    private ResultSet set;

    public InfoDAO(String usr, String pw) throws ClassNotFoundException {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, usr, pw);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<DocStruct> getPoems() {
        String sql = "select * from poems;";
        ArrayList<DocStruct> ret= new ArrayList<>();
        try {
            stt = conn.createStatement();
            set = stt.executeQuery(sql);

            while(set.next()){
                DocStruct doc=new DocStruct();
                doc.id=set.getInt("id");
                doc.head=set.getString("head");
                doc.author=set.getString("author");
                doc.content=set.getString("content");
                ret.add(doc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            return ret;
        }
    }

    public DocStruct getPoemById(int id){
        String sql = "select * from poems where id="+id+";";
        DocStruct ret= new DocStruct();
        try {
            stt = conn.createStatement();
            set = stt.executeQuery(sql);

            while(set.next()){
                ret.id=set.getInt("id");
                ret.head=set.getString("head");
                ret.author=set.getString("author");
                ret.content=set.getString("content");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            return ret;
        }
    }

    public void finalize(){
        try{
//            set.close();
            conn.close();
        }catch(Exception e2){
            e2.printStackTrace();
        }
    }
}
