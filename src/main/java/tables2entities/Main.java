package tables2entities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        T2EManager t2EManager = new T2EManager();
        File repositoryFolder = new File("src/main/java/" + t2EManager.projectName + "/repository");
        if (!repositoryFolder.exists()) {
            repositoryFolder.mkdirs();
        }

        File domainFolder = new File("src/main/java/" + t2EManager.projectName + "/domain");
        if (!domainFolder.exists()) {
            domainFolder.mkdirs();
        }

        for (String table_name: t2EManager.getTableList()) {
            try(FileWriter writer = new FileWriter("src/main/java/" + t2EManager.projectName + "/repository/"+ t2EManager.firstUp(table_name) +"Repository.java", false))
            {
                writer.write(t2EManager.getCodeForRepository(table_name));
                writer.append('\n');
                writer.flush();
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
            try(FileWriter writer = new FileWriter("src/main/java/" + t2EManager.projectName + "/domain/"+ t2EManager.firstUp(table_name) +".java", false))
            {
                writer.write(t2EManager.getCodeForEntity(table_name));
                writer.append('\n');
                writer.flush();
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
        }
    }
}
