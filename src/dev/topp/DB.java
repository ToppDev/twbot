package dev.topp;

public class DB {
    public static void createTables(DatabaseManager databaseManager) {
        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Creating Database...", "createTables()", "DB");
        long start = System.currentTimeMillis();

        //----------------- ToDo: Remove this
//        boolean clearTables = true;
//        if(clearTables) {
//            worldConfigs.dropTable();
//        }
        //-----------------

        try {
            databaseManager.beginTransaction();
            long start_table = System.currentTimeMillis();

            //worldConfigs.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.DEBUG, "Table ´worldConfigs´ created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");

            databaseManager.setTransactionSuccessful();
        } catch (Exception ex) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, ex.getClass().getSimpleName() + "->" + ex.getMessage(), "createTables()", "DB");
        } finally {
            databaseManager.endTransaction();
        }

        LoggingManager.log(LoggingManager.LoggingLevel.DEBUG, "Database creation successful after " + String.valueOf(System.currentTimeMillis() - start) + " ms", "createTables()", "DB");
    }
}
