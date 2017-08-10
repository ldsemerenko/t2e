package tables2entities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class t2eManager {
    private final String url = "jdbc:postgresql://localhost:5432/guestengine";
    private final String user = "guestengine";
    private final String pass = "guestengine";
    public final String schema = "guestengine";
    private Connection connection;

    public Connection getConnection() {
        if(connection == null) {
            System.out.println(url);
            try {
                connection = DriverManager.getConnection(url , user, pass);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("open connection error " + url + " @ " + user + " @ " + pass);
            }        }
        return connection;
    }


    public List<String> getTableList(){
        List<String> tableList = new ArrayList<String>();
        Statement statement;
        try {
            statement = getConnection().createStatement();
            statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='" + schema +"' AND table_type='BASE TABLE';");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()){

                tableList.add(resultSet.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableList;
    }

    public String getColumns(String table_name){
        String result = "";
        Statement statement;
        try {
            statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            statement.executeQuery("SELECT * FROM information_schema.columns WHERE table_schema = '" + schema +"' AND table_name = '" + table_name + "' ORDER BY ordinal_position;");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()){
                int position = resultSet.getInt("ordinal_position");
                String name = resultSet.getString("column_name");
                String type = resultSet.getString("data_type");
                result += "\n";
                result += convertToField(position, name, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String convertToField(int position, String name, String type) {
        String result = "";
        if (position == 1) {
            result += "\t" + "@Id";
        }
        result += "\t" + "@Column(name = \"" + name + "\", columnDefinition = \"" + type + "\")";
        result += "\tprivate " + toJavaType(type) + " " + toCamelCase(name);
        return result;
    }

    private String toCamelCase(String name) {
        if(!name.contains("_")) return name.substring(0,1).toUpperCase() + name.substring(1,name.length());
        String result = name.split("_")[0].substring(0,1).toUpperCase() + name.substring(1,name.length());
        return result + toCamelCase(name.substring(name.indexOf("_")));
    }

    private String toJavaType(String type) {
        String sqlType = type.split(" ")[0];
        if(sqlType.startsWith("int")) return "Integer";
        if(sqlType.startsWith("varc")) return "String";
        if(sqlType.startsWith("char")) return "String";
        if(sqlType.startsWith("bigint")) return "Long";
        if(sqlType.startsWith("decimal")) return "Double";
        if(sqlType.startsWith("text")) return "String";
        System.exit(-5);
        return "";
    }

}