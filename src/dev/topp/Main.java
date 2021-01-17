package dev.topp;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Main {

    /**
     * Main function of program.
     * @param args 0 -> twUsername<br>
     *             1 -> twPassword<br>
     *             2 -> twWorld<br>
     *             3 -> dbHost<br>
     *             4 -> dbPort<br>
     *             5 -> dbName<br>
     *             6 -> dbUsername<br>
     *             7 -> dbPassword<br>
     */
    public static void main(String[] args) {
        if (args.length != 8) {
            System.out.println("Usage: [TWUsername] [TWPassword] [TWWorld] [SQL-Host] [SQL-Port] [SQL-DB-Name] [SQL-Username] [SQL-Password]");
        } else {
            System.out.println("Program started.");
            try {
                LoggingManager.setLoggingLevel(LoggingManager.LoggingLevel.DEBUG);
                String twUser = args[0];
                String twPass = args[1];
                String twWorld = args[2];
                String dbHost = args[3];
                String dbPort = args[4];
                String dbName = args[5];
                String dbUser = args[6];
                String dbPass = args[7];

                try (WebClient webClient = new WebClient();
                     DatabaseManager databaseManager = new DatabaseManager(dbHost, dbPort, dbName, dbUser, dbPass)) {
                    //DB.createTables(databaseManager);

                    LoginManager.loginToAccount(twUser, twPass, webClient);

                    //StringSelection stringSelection = new StringSelection(response);
                    //Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    //clpbrd.setContents(stringSelection, null);
                }
            } catch (Exception ex) {
                LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "main()", "Main");
            }
            System.out.println("Program finished.");
        }
    }

    public static boolean closeAll() {
        try {
            // ToDo: Clean up system resources
            return true;
        } catch (Exception ex) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "closeAll()", "Main");
            return false;
        }
    }

    /*private static String readDbUserFromConsole() {
        String returnValue = "";
        Console console = System.console();
        if (console == null) {
            LoggingManager.log(LoggingLevel.CRITICAL, "Unable to fetch console.", "readDbUserFromConsole()", "DatabaseManager");
        } else
            while (returnValue.equals("")) {
                System.out.print("Please enter a valid username for your database: ");
                returnValue = console.readLine();
            }
        return returnValue;
    }*/
}
