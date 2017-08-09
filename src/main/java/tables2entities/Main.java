package tables2entities;

public class Main {
    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        for (String s : dbManager.getTableList()) {
            for (String c : dbManager.getColumnMap(s)) {
                System.out.print(c + " : " + dbManager.getColumnType(s, c));
                System.out.println();
            }

        }
    }
}
