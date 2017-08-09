package tables2entities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private final String url = "jdbc:postgresql://localhost:5432/guestengine";
    private final String user = "guestengine";
    private final String pass = "guestengine";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
        }
    }

    Connection connection;

    public Connection getConnection() {
        if(connection == null) {
            open(url, user, pass);
        }
        return connection;
    }

    private void open(String url,String user, String pass) {
        System.out.println(url);
        try {
            connection = DriverManager.getConnection(url , user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("open connection error " + url + " @ " + user + " @ " + pass);
        }
    }

    public List<String> getTableList(){
        List<String> tableList = new ArrayList<String>();
        Statement statement;
        try {
            statement = getConnection().createStatement();
            statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='guestengine' AND table_type='BASE TABLE';");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()){

                tableList.add(resultSet.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableList;
    }

    public List<String> getColumnMap(String table_name){
        List<String> collumnList = new ArrayList<>();
        Statement statement;
        try {
            statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            statement.executeQuery("SELECT * FROM information_schema.columns WHERE table_schema = 'guestengine' AND table_name = '" + table_name + "';");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()){

                collumnList.add(resultSet.getString("column_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return collumnList;
    }

    public String getColumnType(String table_name, String colum_name){

        Statement statement;
        try {
            statement = getConnection().createStatement();
            statement.executeQuery("SELECT * FROM information_schema.columns WHERE table_schema = 'guestengine' AND table_name = '" + table_name + "' AND column_name = '" + colum_name + "';");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()){
                return resultSet.getString("data_type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
//   SELECT table_name FROM information_schema.tables WHERE table_schema='guestengine' AND table_type='BASE TABLE';

//   SELECT * FROM information_schema.columns WHERE table_schema = 'guestengine' AND table_name   = 'account_contacts';

//SELECT * FROM information_schema.columns WHERE table_schema = 'guestengine' AND table_name = 'account_contacts' and column_name = 'account_contact_id';
//SELECT * FROM information_schema.columns WHERE table_schema = 'guestengine' AND table_name = '" + table_name + "' and column_name = '" + colum_name + "';