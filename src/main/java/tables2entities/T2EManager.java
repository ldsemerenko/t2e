package tables2entities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class T2EManager {
    private final String url = "jdbc:postgresql://localhost:5432/guestengine";
    private final String user = "guestengine";
    private final String pass = "guestengine";

    public final String schema = "guestengine";
    public final String projectName = "guestengine";

    private Connection connection;

    private Connection getConnection() {
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


    public String getCodeForRepository(String table_name){
        String result = "";
        result += "package " + projectName + ".repository;\n\n";
        result += "import org.springframework.data.jpa.repository.JpaRepository;\n";
        result += "import " + projectName + ".domain."+ firstUp(table_name) + ";\n\n";
        result += "public interface "+ firstUp(table_name) + "Repository extends JpaRepository<" + firstUp(table_name) + ", Integer>{";
        return result + "}";
    }


    public String getCodeForEntity(String table_name){
        String result = "";

        result += "import javax.persistence.*;\n\n@Entity\n";
        result += "@Table(schema = \""+ schema + "\", name = \""+ table_name + "\")\n";
        result += "public class " + firstUp(table_name) + " {\n";
        result += getColumns(table_name);
        if(result.contains("private Timestamp")){result = "import java.sql.Timestamp;\n" + result;}
        if(result.contains("private BigDecima")){result = "import java.math.BigDecimal;\n" + result;}
        if(result.contains("private Date")){result = "import java.util.Date;\n" + result;}
        result = "package " + projectName + ".domain;\n\n" + result;
        return result + "}";
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

    private String getColumns(String table_name){
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
            result += "\t" + "@Id\n" + "\t@GeneratedValue(strategy = GenerationType.IDENTITY)\n";
        }
        result += "\t" + "@Column(name = \"" + name + "\", columnDefinition = \"" + type + "\")\n";
        result += "\tprivate " + toJavaType(type) + " " + toCamelCase(name) + ";\n";
        return result;
    }

    private String toCamelCase(String name) {
        if(!name.contains("_")) return firstUp(name);
        return firstUp(name.split("_")[0]) + toCamelCase(name.substring(name.indexOf("_") + 1));
    }

    public String firstUp(String text){
        return text.substring(0,1).toUpperCase() + text.substring(1,text.length());
    }

    private String toJavaType(String type) {
        String sqlType = type.split(" ")[0];
        if(sqlType.startsWith("int")) return "Integer";
        if(sqlType.startsWith("smallint")) return "Integer";
        if(sqlType.startsWith("numeric")) return "BigDecimal";
        if(sqlType.startsWith("varc")) return "String";
        if(sqlType.startsWith("char")) return "String";
        if(sqlType.startsWith("bigint")) return "Long";
        if(sqlType.startsWith("decimal")) return "Double";
        if(sqlType.startsWith("text")) return "String";
        if(sqlType.startsWith("text")) return "String";
        if(sqlType.startsWith("date")) return "Date";
        if(sqlType.startsWith("timestamp")) return "Timestamp";

        System.out.println("unknown type >-> " + type);
        return "";
        //for other types http://www.service-architecture.com/articles/database/mapping_sql_and_java_data_types.html
    }

}