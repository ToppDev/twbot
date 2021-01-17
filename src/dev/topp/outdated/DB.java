package dev.topp.outdated;

import dev.topp.LoggingManager;
import dev.topp.outdated.TWHelper.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DB {

    public static void createTables() {
        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Creating Database...", "createTables()", "DB");
        long start = System.currentTimeMillis();

        //----------------- ToDo: Remove this
        boolean clearTables = false;
        if(clearTables) {
            worldConfigs.dropTable();
            unitInfo.dropTable();
            buildingInfo.dropTable();
            villages.dropTable();
            players.dropTable();
            buildingEvents.dropTable();
            unitCreationEvents.dropTable();
            commandReturnEvents.dropTable();
        }
        //-----------------


        try {
            DatabaseManager.beginTransaction();
            long start_table = System.currentTimeMillis();

            worldConfigs.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Table worldConfigs created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");
            start_table = System.currentTimeMillis();

            unitInfo.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Table unitInfo created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");
            start_table = System.currentTimeMillis();

            buildingInfo.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Table buildingInfo created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");
            start_table = System.currentTimeMillis();

            villages.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Table villages created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");
            start_table = System.currentTimeMillis();

            players.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Table players created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");
            start_table = System.currentTimeMillis();

            buildingEvents.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Table buildingEvents created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");
            start_table = System.currentTimeMillis();

            unitCreationEvents.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Table unitCreationEvents created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");
            start_table = System.currentTimeMillis();

            commandReturnEvents.createTable();

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Table commandReturnEvents created after " + String.valueOf(System.currentTimeMillis() - start_table) + " ms", "createTables()", "DB");

            DatabaseManager.setTransactionSuccessful();
        } catch (Exception ex) {
            LoggingManager.log(LoggingManager.LoggingLevel.INFO, ex.getMessage(), "createTables()", "DB");
        } finally {
            DatabaseManager.endTransaction();
        }

        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Database creation successful after " + String.valueOf(System.currentTimeMillis() - start) + " ms", "createTables()", "DB");
    }

    public static class worldConfigs {
        //region Constants
        public static final String tableName = "tbl_worldConfigs";
        public static final String gid = "gid";
        public static final String speed = "speed";
        public static final String unit_speed = "unit_speed";
        /**  0 = inactive<br>1 = active<br>2 = time-based */
        public static final String moral = "moral";
        /**  0 = inactive<br>1 = active */
        public static final String build_destroy = "build_destroy";
        /**  Time in seconds for canceling a trade. */
        public static final String trade_cancel_time = "trade_cancel_time";
        /**  0 = inactive<br>1 = active */
        public static final String command_millis_arrival = "commands_millis_arrival";
        /**  Time in seconds for canceling a command. */
        public static final String command_cancel_time = "command_cancel_time";
        /**  Days you can not be attacked. */
        public static final String newbie_days = "newbie_days";
        public static final String game_base_config = "game_base_config";
        /**  0 = old build-times<br>1 = new_buildtimes */
        public static final String new_buildtime_formula = "new_buildtime_formula";
        /** 0 = deactivated<br>1 = activated<br>2 = activated, can find items */
        public static final String knight = "knight";
        /** 0 = deactivated<br>1 = activated */
        public static final String archer = "archer";
        public static final String tech = "tech";
        public static final String spy = "spy";
        public static final String farm_limit = "farm_limit";
        /** 0 = deactivated<br>1 = activated */
        public static final String church = "church";
        public static final String fake_limit = "fake_limit";
        public static final String base_production = "base_production";
        public static final String snob_gold = "snob_gold";
        public static final String snob_cheap_rebuild = "snob_cheap_rebuild";
        public static final String snob_simple = "snob_simple";
        public static final String snob_rise = "snob_rise";
        public static final String snob_max_dist = "snob_max_dist";
        public static final String snob_factor = "snob_factor";
        public static final String snob_coin_wood = "snob_coin_wood";
        public static final String snob_coin_stone = "snob_coin_stone";
        public static final String snob_coin_iron = "snob_coin_iron";
        /** 0 = deactivated */
        public static final String bonus_villages = "bonus_villages";
        public static final String night_active = "night_active";
        public static final String night_start_hour = "night_start_hour";
        public static final String night_end_hour = "night_end_hour";
        public static final String night_def_factor = "night_def_factor";
        public static final String lastDatabaseUpdateDate = "lastDatabaseUpdateDate";
        public static final String declarePlayerAsFarmInterval = "lastDatabaseUpdateInterval";
        public static final String databaseUpdateInterval = "databaseUpdateInterval";
        //endregion

        public static void dropTable(){
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable(){
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "INTEGER PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(unit_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(moral, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(build_destroy, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(trade_cancel_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(command_millis_arrival, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(command_cancel_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(newbie_days, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(game_base_config, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(new_buildtime_formula, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(tech, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_limit, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(fake_limit, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(base_production, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_gold, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_cheap_rebuild, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_simple, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_rise, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_max_dist, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_factor, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_coin_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_coin_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_coin_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_villages, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(night_active, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(night_start_hour, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(night_end_hour, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(night_def_factor, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(lastDatabaseUpdateDate, "BIGINT DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(declarePlayerAsFarmInterval, "INTEGER DEFAULT 432000000")); // 5 days
            nameValuePairs.add(new DatabaseManager.NameValuePair(databaseUpdateInterval, "INTEGER DEFAULT 432000000")); // 5 days
            DatabaseManager.createTable(tableName, nameValuePairs);
        }

        public static void insertDefault() {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "0"));
            DatabaseManager.insert(tableName, nameValuePairs);
        }

        public static String getValue(String value) {
            String[] selectString = {value};
            ResultSet resultSet = DatabaseManager.select(tableName, selectString, null);
            try {
                if(resultSet.first())
                    return resultSet.getString(value);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "getValue(String = " + value + ")", "DB->worldConfigs");
            }
            return null;
        }

        public static boolean isNewBuildTimeFormulaActive() {
            String[] selectStrings = {new_buildtime_formula};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                if(resultSet.first())
                    return resultSet.getBoolean(new_buildtime_formula);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "isNewBuildTimeFormulaActive()", "DB->worldConfigs");
            }
            return true;
        }

        public static boolean isArcherActive() {
            String[] selectStrings = {archer};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                if(resultSet.first())
                    return resultSet.getBoolean(archer);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "isArcherActive()", "DB->worldConfigs");
            }
            return true;
        }

        public static boolean isKnightActive() {
            String[] selectStrings = {knight};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                if(resultSet.first())
                    return resultSet.getBoolean(knight);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "isKnightActive()", "DB->worldConfigs");
            }
            return true;
        }

        public static boolean isChurchActive() {
            String[] selectStrings = {church};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                if(resultSet.first())
                    return resultSet.getBoolean(church);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "isChurchActive()", "DB->worldConfigs");
            }
            return true;
        }

        public static boolean isBonusVillagesActive() {
            String[] selectStrings = {bonus_villages};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                if(resultSet.first())
                    return resultSet.getBoolean(bonus_villages);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "isBonusVillagesActive()", "DB->worldConfigs");
            }
            return true;
        }

        public static void updateValue(List<DatabaseManager.NameValuePair> updateValuePairs) {
            DatabaseManager.update(tableName, updateValuePairs, null);
        }

        public static boolean isEmpty() {
            String[] selectStrings = {gid};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                return !resultSet.first();
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "isEmpty()", "DB->worldConfigs");
            }
            return false;
        }
    }
    
    public static class unitInfo {
        //region Constants
        public static final String tableName = "tbl_unitInfo";
        public static final String gid = "gid";
        public static final String spear_wood = "spear_wood";
        public static final String spear_stone = "spear_stone";
        public static final String spear_iron = "spear_iron";
        public static final String spear_pop = "spear_pop";
        public static final String spear_speed = "spear_speed";
        public static final String spear_attack = "spear_attack";
        public static final String spear_defense = "spear_defense";
        public static final String spear_defense_cavalry = "spear_defense_cavalry";
        public static final String spear_defense_archer = "spear_defense_archer";
        public static final String spear_carry = "spear_carry";
        public static final String spear_build_time = "spear_build_time";

        public static final String sword_wood = "sword_wood";
        public static final String sword_stone = "sword_stone";
        public static final String sword_iron = "sword_iron";
        public static final String sword_pop = "sword_pop";
        public static final String sword_speed = "sword_speed";
        public static final String sword_attack = "sword_attack";
        public static final String sword_defense = "sword_defense";
        public static final String sword_defense_cavalry = "sword_defense_cavalry";
        public static final String sword_defense_archer = "sword_defense_archer";
        public static final String sword_carry = "sword_carry";
        public static final String sword_build_time = "sword_build_time";

        public static final String axe_wood = "axe_wood";
        public static final String axe_stone = "axe_stone";
        public static final String axe_iron = "axe_iron";
        public static final String axe_pop = "axe_pop";
        public static final String axe_speed = "axe_speed";
        public static final String axe_attack = "axe_attack";
        public static final String axe_defense = "axe_defense";
        public static final String axe_defense_cavalry = "axe_defense_cavalry";
        public static final String axe_defense_archer = "axe_defense_archer";
        public static final String axe_carry = "axe_carry";
        public static final String axe_build_time = "axe_build_time";

        public static final String archer_wood = "archer_wood";
        public static final String archer_stone = "archer_stone";
        public static final String archer_iron = "archer_iron";
        public static final String archer_pop = "archer_pop";
        public static final String archer_speed = "archer_speed";
        public static final String archer_attack = "archer_attack";
        public static final String archer_defense = "archer_defense";
        public static final String archer_defense_cavalry = "archer_defense_cavalry";
        public static final String archer_defense_archer = "archer_defense_archer";
        public static final String archer_carry = "archer_carry";
        public static final String archer_build_time = "archer_build_time";

        public static final String spy_wood = "spy_wood";
        public static final String spy_stone = "spy_stone";
        public static final String spy_iron = "spy_iron";
        public static final String spy_pop = "spy_pop";
        public static final String spy_speed = "spy_speed";
        public static final String spy_attack = "spy_attack";
        public static final String spy_defense = "spy_defense";
        public static final String spy_defense_cavalry = "spy_defense_cavalry";
        public static final String spy_defense_archer = "spy_defense_archer";
        public static final String spy_carry = "spy_carry";
        public static final String spy_build_time = "spy_build_time";

        public static final String light_wood = "light_wood";
        public static final String light_stone = "light_stone";
        public static final String light_iron = "light_iron";
        public static final String light_pop = "light_pop";
        public static final String light_speed = "light_speed";
        public static final String light_attack = "light_attack";
        public static final String light_defense = "light_defense";
        public static final String light_defense_cavalry = "light_defense_cavalry";
        public static final String light_defense_archer = "light_defense_archer";
        public static final String light_carry = "light_carry";
        public static final String light_build_time = "light_build_time";

        public static final String marcher_wood = "marcher_wood";
        public static final String marcher_stone = "marcher_stone";
        public static final String marcher_iron = "marcher_iron";
        public static final String marcher_pop = "marcher_pop";
        public static final String marcher_speed = "marcher_speed";
        public static final String marcher_attack = "marcher_attack";
        public static final String marcher_defense = "marcher_defense";
        public static final String marcher_defense_cavalry = "marcher_defense_cavalry";
        public static final String marcher_defense_archer = "marcher_defense_archer";
        public static final String marcher_carry = "marcher_carry";
        public static final String marcher_build_time = "marcher_build_time";

        public static final String heavy_wood = "heavy_wood";
        public static final String heavy_stone = "heavy_stone";
        public static final String heavy_iron = "heavy_iron";
        public static final String heavy_pop = "heavy_pop";
        public static final String heavy_speed = "heavy_speed";
        public static final String heavy_attack = "heavy_attack";
        public static final String heavy_defense = "heavy_defense";
        public static final String heavy_defense_cavalry = "heavy_defense_cavalry";
        public static final String heavy_defense_archer = "heavy_defense_archer";
        public static final String heavy_carry = "heavy_carry";
        public static final String heavy_build_time = "heavy_build_time";

        public static final String ram_wood = "ram_wood";
        public static final String ram_stone = "ram_stone";
        public static final String ram_iron = "ram_iron";
        public static final String ram_pop = "ram_pop";
        public static final String ram_speed = "ram_speed";
        public static final String ram_attack = "ram_attack";
        public static final String ram_defense = "ram_defense";
        public static final String ram_defense_cavalry = "ram_defense_cavalry";
        public static final String ram_defense_archer = "ram_defense_archer";
        public static final String ram_carry = "ram_carry";
        public static final String ram_build_time = "ram_build_time";

        public static final String catapult_wood = "catapult_wood";
        public static final String catapult_stone = "catapult_stone";
        public static final String catapult_iron = "catapult_iron";
        public static final String catapult_pop = "catapult_pop";
        public static final String catapult_speed = "catapult_speed";
        public static final String catapult_attack = "catapult_attack";
        public static final String catapult_defense = "catapult_defense";
        public static final String catapult_defense_cavalry = "catapult_defense_cavalry";
        public static final String catapult_defense_archer = "catapult_defense_archer";
        public static final String catapult_carry = "catapult_carry";
        public static final String catapult_build_time = "catapult_build_time";

        public static final String knight_wood = "knight_wood";
        public static final String knight_stone = "knight_stone";
        public static final String knight_iron = "knight_iron";
        public static final String knight_pop = "knight_pop";
        public static final String knight_speed = "knight_speed";
        public static final String knight_attack = "knight_attack";
        public static final String knight_defense = "knight_defense";
        public static final String knight_defense_cavalry = "knight_defense_cavalry";
        public static final String knight_defense_archer = "knight_defense_archer";
        public static final String knight_carry = "knight_carry";
        public static final String knight_build_time = "knight_build_time";

        public static final String snob_wood = "snob_wood";
        public static final String snob_stone = "snob_stone";
        public static final String snob_iron = "snob_iron";
        public static final String snob_pop = "snob_pop";
        public static final String snob_speed = "snob_speed";
        public static final String snob_attack = "snob_attack";
        public static final String snob_defense = "snob_defense";
        public static final String snob_defense_cavalry = "snob_defense_cavalry";
        public static final String snob_defense_archer = "snob_defense_archer";
        public static final String snob_carry = "snob_carry";
        public static final String snob_build_time = "snob_build_time";

        public static final String militia_wood = "militia_wood";
        public static final String militia_stone = "militia_stone";
        public static final String militia_iron = "militia_iron";
        public static final String militia_pop = "militia_pop";
        public static final String militia_speed = "militia_speed";
        public static final String militia_attack = "militia_attack";
        public static final String militia_defense = "militia_defense";
        public static final String militia_defense_cavalry = "militia_defense_cavalry";
        public static final String militia_defense_archer = "militia_defense_archer";
        public static final String militia_carry = "militia_carry";
        public static final String militia_build_time = "militia_build_time";
        //endregion

        public static void dropTable(){
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable(){
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "INTEGER PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_speed, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_attack, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_defense, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_defense_cavalry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_defense_archer, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_carry, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_build_time, "INTEGER"));
            DatabaseManager.createTable(tableName, nameValuePairs);
        }

        public static void insertDefault() {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "0"));
            DatabaseManager.insert(tableName, nameValuePairs);
        }

        public static Float getValue(String name) {
            String[] selectStrings = {name};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                if(resultSet.first())
                    return resultSet.getFloat(name);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "getValue(String = " + name + ")", "DB->unitInfo");
            }
            return null;
        }

        public static boolean isEmpty() {
            String[] selectStrings = {gid};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                return !resultSet.first();
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "isEmpty()", "DB->unitInfo");
            }
            return false;
        }
    }
    
    public static class buildingInfo {
        //region Constants
        public static final String tableName = "tbl_buildingInfo";
        public static final String gid = "gid";
        public static final String main_max_level = "main_max_level";
        public static final String main_min_level = "main_min_level";
        public static final String main_wood = "main_wood";
        public static final String main_stone = "main_stone";
        public static final String main_iron = "main_iron";
        public static final String main_pop = "main_pop";
        public static final String main_wood_factor = "main_wood_factor";
        public static final String main_stone_factor = "main_stone_factor";
        public static final String main_iron_factor = "main_iron_factor";
        public static final String main_pop_factor = "main_pop_factor";
        public static final String main_build_time = "main_build_time";
        public static final String main_build_time_factor = "main_build_time_factor";
        public static final String barracks_max_level = "barracks_max_level";
        public static final String barracks_min_level = "barracks_min_level";
        public static final String barracks_wood = "barracks_wood";
        public static final String barracks_stone = "barracks_stone";
        public static final String barracks_iron = "barracks_iron";
        public static final String barracks_pop = "barracks_pop";
        public static final String barracks_wood_factor = "barracks_wood_factor";
        public static final String barracks_stone_factor = "barracks_stone_factor";
        public static final String barracks_iron_factor = "barracks_iron_factor";
        public static final String barracks_pop_factor = "barracks_pop_factor";
        public static final String barracks_build_time = "barracks_build_time";
        public static final String barracks_build_time_factor = "barracks_build_time_factor";
        public static final String stable_max_level = "stable_max_level";
        public static final String stable_min_level = "stable_min_level";
        public static final String stable_wood = "stable_wood";
        public static final String stable_stone = "stable_stone";
        public static final String stable_iron = "stable_iron";
        public static final String stable_pop = "stable_pop";
        public static final String stable_wood_factor = "stable_wood_factor";
        public static final String stable_stone_factor = "stable_stone_factor";
        public static final String stable_iron_factor = "stable_iron_factor";
        public static final String stable_pop_factor = "stable_pop_factor";
        public static final String stable_build_time = "stable_build_time";
        public static final String stable_build_time_factor = "stable_build_time_factor";
        public static final String garage_max_level = "garage_max_level";
        public static final String garage_min_level = "garage_min_level";
        public static final String garage_wood = "garage_wood";
        public static final String garage_stone = "garage_stone";
        public static final String garage_iron = "garage_iron";
        public static final String garage_pop = "garage_pop";
        public static final String garage_wood_factor = "garage_wood_factor";
        public static final String garage_stone_factor = "garage_stone_factor";
        public static final String garage_iron_factor = "garage_iron_factor";
        public static final String garage_pop_factor = "garage_pop_factor";
        public static final String garage_build_time = "garage_build_time";
        public static final String garage_build_time_factor = "garage_build_time_factor";
        public static final String church_max_level = "church_max_level";
        public static final String church_min_level = "church_min_level";
        public static final String church_wood = "church_wood";
        public static final String church_stone = "church_stone";
        public static final String church_iron = "church_iron";
        public static final String church_pop = "church_pop";
        public static final String church_wood_factor = "church_wood_factor";
        public static final String church_stone_factor = "church_stone_factor";
        public static final String church_iron_factor = "church_iron_factor";
        public static final String church_pop_factor = "church_pop_factor";
        public static final String church_build_time = "church_build_time";
        public static final String church_build_time_factor = "church_build_time_factor";
        public static final String church_f_max_level = "church_f_max_level";
        public static final String church_f_min_level = "church_f_min_level";
        public static final String church_f_wood = "church_f_wood";
        public static final String church_f_stone = "church_f_stone";
        public static final String church_f_iron = "church_f_iron";
        public static final String church_f_pop = "church_f_pop";
        public static final String church_f_wood_factor = "church_f_wood_factor";
        public static final String church_f_stone_factor = "church_f_stone_factor";
        public static final String church_f_iron_factor = "church_f_iron_factor";
        public static final String church_f_pop_factor = "church_f_pop_factor";
        public static final String church_f_build_time = "church_f_build_time";
        public static final String church_f_build_time_factor = "church_f_build_time_factor";
        public static final String snob_max_level = "snob_max_level";
        public static final String snob_min_level = "snob_min_level";
        public static final String snob_wood = "snob_wood";
        public static final String snob_stone = "snob_stone";
        public static final String snob_iron = "snob_iron";
        public static final String snob_pop = "snob_pop";
        public static final String snob_wood_factor = "snob_wood_factor";
        public static final String snob_stone_factor = "snob_stone_factor";
        public static final String snob_iron_factor = "snob_iron_factor";
        public static final String snob_pop_factor = "snob_pop_factor";
        public static final String snob_build_time = "snob_build_time";
        public static final String snob_build_time_factor = "snob_build_time_factor";
        public static final String smith_max_level = "smith_max_level";
        public static final String smith_min_level = "smith_min_level";
        public static final String smith_wood = "smith_wood";
        public static final String smith_stone = "smith_stone";
        public static final String smith_iron = "smith_iron";
        public static final String smith_pop = "smith_pop";
        public static final String smith_wood_factor = "smith_wood_factor";
        public static final String smith_stone_factor = "smith_stone_factor";
        public static final String smith_iron_factor = "smith_iron_factor";
        public static final String smith_pop_factor = "smith_pop_factor";
        public static final String smith_build_time = "smith_build_time";
        public static final String smith_build_time_factor = "smith_build_time_factor";
        public static final String place_max_level = "place_max_level";
        public static final String place_min_level = "place_min_level";
        public static final String place_wood = "place_wood";
        public static final String place_stone = "place_stone";
        public static final String place_iron = "place_iron";
        public static final String place_pop = "place_pop";
        public static final String place_wood_factor = "place_wood_factor";
        public static final String place_stone_factor = "place_stone_factor";
        public static final String place_iron_factor = "place_iron_factor";
        public static final String place_pop_factor = "place_pop_factor";
        public static final String place_build_time = "place_build_time";
        public static final String place_build_time_factor = "place_build_time_factor";
        public static final String statue_max_level = "statue_max_level";
        public static final String statue_min_level = "statue_min_level";
        public static final String statue_wood = "statue_wood";
        public static final String statue_stone = "statue_stone";
        public static final String statue_iron = "statue_iron";
        public static final String statue_pop = "statue_pop";
        public static final String statue_wood_factor = "statue_wood_factor";
        public static final String statue_stone_factor = "statue_stone_factor";
        public static final String statue_iron_factor = "statue_iron_factor";
        public static final String statue_pop_factor = "statue_pop_factor";
        public static final String statue_build_time = "statue_build_time";
        public static final String statue_build_time_factor = "statue_build_time_factor";
        public static final String market_max_level = "market_max_level";
        public static final String market_min_level = "market_min_level";
        public static final String market_wood = "market_wood";
        public static final String market_stone = "market_stone";
        public static final String market_iron = "market_iron";
        public static final String market_pop = "market_pop";
        public static final String market_wood_factor = "market_wood_factor";
        public static final String market_stone_factor = "market_stone_factor";
        public static final String market_iron_factor = "market_iron_factor";
        public static final String market_pop_factor = "market_pop_factor";
        public static final String market_build_time = "market_build_time";
        public static final String market_build_time_factor = "market_build_time_factor";
        public static final String wood_max_level = "wood_max_level";
        public static final String wood_min_level = "wood_min_level";
        public static final String wood_wood = "wood_wood";
        public static final String wood_stone = "wood_stone";
        public static final String wood_iron = "wood_iron";
        public static final String wood_pop = "wood_pop";
        public static final String wood_wood_factor = "wood_wood_factor";
        public static final String wood_stone_factor = "wood_stone_factor";
        public static final String wood_iron_factor = "wood_iron_factor";
        public static final String wood_pop_factor = "wood_pop_factor";
        public static final String wood_build_time = "wood_build_time";
        public static final String wood_build_time_factor = "wood_build_time_factor";
        public static final String stone_max_level = "stone_max_level";
        public static final String stone_min_level = "stone_min_level";
        public static final String stone_wood = "stone_wood";
        public static final String stone_stone = "stone_stone";
        public static final String stone_iron = "stone_iron";
        public static final String stone_pop = "stone_pop";
        public static final String stone_wood_factor = "stone_wood_factor";
        public static final String stone_stone_factor = "stone_stone_factor";
        public static final String stone_iron_factor = "stone_iron_factor";
        public static final String stone_pop_factor = "stone_pop_factor";
        public static final String stone_build_time = "stone_build_time";
        public static final String stone_build_time_factor = "stone_build_time_factor";
        public static final String iron_max_level = "iron_max_level";
        public static final String iron_min_level = "iron_min_level";
        public static final String iron_wood = "iron_wood";
        public static final String iron_stone = "iron_stone";
        public static final String iron_iron = "iron_iron";
        public static final String iron_pop = "iron_pop";
        public static final String iron_wood_factor = "iron_wood_factor";
        public static final String iron_stone_factor = "iron_stone_factor";
        public static final String iron_iron_factor = "iron_iron_factor";
        public static final String iron_pop_factor = "iron_pop_factor";
        public static final String iron_build_time = "iron_build_time";
        public static final String iron_build_time_factor = "iron_build_time_factor";
        public static final String farm_max_level = "farm_max_level";
        public static final String farm_min_level = "farm_min_level";
        public static final String farm_wood = "farm_wood";
        public static final String farm_stone = "farm_stone";
        public static final String farm_iron = "farm_iron";
        public static final String farm_pop = "farm_pop";
        public static final String farm_wood_factor = "farm_wood_factor";
        public static final String farm_stone_factor = "farm_stone_factor";
        public static final String farm_iron_factor = "farm_iron_factor";
        public static final String farm_pop_factor = "farm_pop_factor";
        public static final String farm_build_time = "farm_build_time";
        public static final String farm_build_time_factor = "farm_build_time_factor";
        public static final String storage_max_level = "storage_max_level";
        public static final String storage_min_level = "storage_min_level";
        public static final String storage_wood = "storage_wood";
        public static final String storage_stone = "storage_stone";
        public static final String storage_iron = "storage_iron";
        public static final String storage_pop = "storage_pop";
        public static final String storage_wood_factor = "storage_wood_factor";
        public static final String storage_stone_factor = "storage_stone_factor";
        public static final String storage_iron_factor = "storage_iron_factor";
        public static final String storage_pop_factor = "storage_pop_factor";
        public static final String storage_build_time = "storage_build_time";
        public static final String storage_build_time_factor = "storage_build_time_factor";
        public static final String hide_max_level = "hide_max_level";
        public static final String hide_min_level = "hide_min_level";
        public static final String hide_wood = "hide_wood";
        public static final String hide_stone = "hide_stone";
        public static final String hide_iron = "hide_iron";
        public static final String hide_pop = "hide_pop";
        public static final String hide_wood_factor = "hide_wood_factor";
        public static final String hide_stone_factor = "hide_stone_factor";
        public static final String hide_iron_factor = "hide_iron_factor";
        public static final String hide_pop_factor = "hide_pop_factor";
        public static final String hide_build_time = "hide_build_time";
        public static final String hide_build_time_factor = "hide_build_time_factor";
        public static final String wall_max_level = "wall_max_level";
        public static final String wall_min_level = "wall_min_level";
        public static final String wall_wood = "wall_wood";
        public static final String wall_stone = "wall_stone";
        public static final String wall_iron = "wall_iron";
        public static final String wall_pop = "wall_pop";
        public static final String wall_wood_factor = "wall_wood_factor";
        public static final String wall_stone_factor = "wall_stone_factor";
        public static final String wall_iron_factor = "wall_iron_factor";
        public static final String wall_pop_factor = "wall_pop_factor";
        public static final String wall_build_time = "wall_build_time";
        public static final String wall_build_time_factor = "wall_build_time_factor";
        //endregion

        public static void dropTable(){
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable(){
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "INTEGER PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_build_time_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_max_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_min_level, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_wood, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_stone, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_iron, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_wood_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_stone_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_iron_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_pop_factor, "REAL"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_build_time, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_build_time_factor, "REAL"));
            DatabaseManager.createTable(tableName, nameValuePairs);
        }

        public static void insertDefault() {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "0"));
            DatabaseManager.insert(tableName, nameValuePairs);
        }

        public static Float getValue(String name) {
            String[] selectStrings = {name};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                if(resultSet.first())
                    return resultSet.getFloat(name);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "getValue(String = " + name + ")", "DB->buildingInfo");
            }
            return null;
        }

        public static boolean isEmpty() {
            String[] selectStrings = {gid};
            ResultSet resultSet = DatabaseManager.select(tableName, selectStrings, null);
            try {
                return !resultSet.first();
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "isEmpty()", "DB->buildingInfo");
            }
            return false;
        }
    }

    public static class villages {
        //region Constants
        public static final String tableName = "tbl_villages";
        public static final String gid = "gid";
        public static final String id = "id";
        public static final String x = "x";
        public static final String y = "y";
        public static final String name = "name";
        public static final String player = "player";
        public static final String points = "points";
        public static final String bonus_wood = "bonus_wood";
        public static final String bonus_stone = "bonus_stone";
        public static final String bonus_iron = "bonus_iron";
        public static final String bonus_resources = "bonus_resources";
        public static final String bonus_storage = "bonus_storage";
        public static final String bonus_farm = "bonus_farm";
        public static final String bonus_barracks = "bonus_barracks";
        public static final String bonus_stable = "bonus_stable";
        public static final String bonus_garage = "bonus_garage";
        public static final String flag_resources = "flag_resources";
        public static final String flag_recruitment_speed = "flag_recruitment_speed";
        public static final String flag_farm = "flag_farm";

        public static final String farmVillage = "farmVillage";
        public static final String farmTime = "farmTime";

        public static final String res_wood = "res_wood";
        public static final String res_stone = "res_stone";
        public static final String res_iron = "res_iron";
        public static final String res_pop = "res_pop";
        public static final String res_wood_bonus_factor = "res_wood_bonus_factor";
        public static final String res_stone_bonus_factor = "res_stone_bonus_factor";
        public static final String res_iron_bonus_factor = "res_iron_bonus_factor";
        public static final String res_updateDate = "res_updateDate";
        public static final String buildqueue_count = "buildqueue_count";

        public static final String main_level = "main_level";
        public static final String barracks_level = "barracks_level";
        public static final String stable_level = "stable_level";
        public static final String garage_level = "garage_level";
        public static final String church_level = "church_level";
        public static final String church_f_level = "church_f_level";
        public static final String snob_level = "snob_level";
        public static final String smith_level = "smith_level";
        public static final String place_level = "place_level";
        public static final String statue_level = "statue_level";
        public static final String market_level = "market_level";
        public static final String wood_level = "wood_level";
        public static final String stone_level = "stone_level";
        public static final String iron_level = "iron_level";
        public static final String farm_level = "farm_level";
        public static final String storage_level = "storage_level";
        public static final String hide_level = "hide_level";
        public static final String wall_level = "wall_level";

        public static final String spear_village = "spear_village";
        public static final String spear_all = "spear_all";
        public static final String sword_village = "sword_village";
        public static final String sword_all = "sword_all";
        public static final String axe_village = "axe_village";
        public static final String axe_all = "axe_all";
        public static final String archer_village = "archer_village";
        public static final String archer_all = "archer_all";
        public static final String spy_village = "spy_village";
        public static final String spy_all = "spy_all";
        public static final String light_village = "light_village";
        public static final String light_all = "light_all";
        public static final String marcher_village = "marcher_village";
        public static final String marcher_all = "marcher_all";
        public static final String heavy_village = "heavy_village";
        public static final String heavy_all = "heavy_all";
        public static final String ram_village = "ram_village";
        public static final String ram_all = "ram_all";
        public static final String catapult_village = "catapult_village";
        public static final String catapult_all = "catapult_all";
        public static final String snob_village = "snob_village";
        public static final String snob_all = "snob_all";
        public static final String knight_village = "knight_village";
        public static final String knight_all = "knight_all";
        public static final String militia_village = "militia_village";
        /** 1 = Don't farm till time is past.
         *
         */
        public static final String village_farm_flag_type = "village_farm_flag_type";
        public static final String village_farm_flag_value = "village_farm_flag_value";
        //endregion

        public static void dropTable(){
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable(){
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "INTEGER PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(id, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(x, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(y, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(name, "TEXT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(player, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(points, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_wood, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_stone, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_iron, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_resources, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_storage, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_farm, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_barracks, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_stable, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(bonus_garage, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(flag_resources, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(flag_recruitment_speed, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(flag_farm, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farmVillage, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farmTime, "BIGINT DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(res_wood, "REAL DEFAULT 100"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(res_stone, "REAL DEFAULT 100"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(res_iron, "REAL DEFAULT 100"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(res_pop, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(res_wood_bonus_factor, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(res_stone_bonus_factor, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(res_iron_bonus_factor, "REAL DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(res_updateDate, "BIGINT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(buildqueue_count, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main_level, "INTEGER DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place_level, "INTEGER DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm_level, "INTEGER DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage_level, "INTEGER DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide_level, "INTEGER DEFAULT 1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall_level, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight_all, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(militia_village, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(village_farm_flag_type, "INTEGER DEFAULT -1"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(village_farm_flag_value, "BIGINT DEFAULT -1"));
            DatabaseManager.createTable(tableName, nameValuePairs);
        }

        public static void insertOrUpdateValue(String id, String name, String x, String y, String player, String points) {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.id, id));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.name, name));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.x, x));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.y, y));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.player, player));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.points, points));
            DatabaseManager.insertOrUpdate(tableName, villages.gid, nameValuePairs);
        }

        public static void addValue(String id, String name, String x, String y, String player, String points) {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.id, id));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.name, name));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.x, x));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.y, y));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.player, player));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villages.points, points));
            DatabaseManager.insert(tableName, nameValuePairs);
        }

        public static String getValue(String value, int villageID) {
            List<DatabaseManager.NameValuePair> whereValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            whereValuePairs.add(new DatabaseManager.NameValuePair(villages.id, String.valueOf(villageID)));
            String[] selectString = {value};
            ResultSet resultSet = DatabaseManager.select(tableName, selectString, whereValuePairs);
            try {
                if(resultSet.first())
                    return resultSet.getString(value);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "getValue(String = " + value + ")", "DB->villages");
            }
            return null;
        }

        public static void updateValue(int villageID, List<DatabaseManager.NameValuePair> updateValuePairs) {
            List<DatabaseManager.NameValuePair> whereValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            whereValuePairs.add(new DatabaseManager.NameValuePair(villages.id, String.valueOf(villageID)));
            DatabaseManager.update(tableName, updateValuePairs, whereValuePairs);
        }
    }

    public static class players {
        //region Constants
        public static final String tableName = "tbl_players";
        public static final String gid = "gid";
        public static final String id = "id";
        public static final String name = "name";
        public static final String ally = "ally";
        public static final String villageCount = "villageCount";
        public static final String points = "points";
        public static final String old_points = "old_points";
        //endregion

        public static void dropTable(){
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable(){
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "INTEGER PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(id, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(name, "TEXT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ally, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villageCount, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(points, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(old_points, "INTEGER"));
            DatabaseManager.createTable(tableName, nameValuePairs);
        }

        public static void addValue(String id, String name, String ally, String villageCount, String points) {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(players.id, id));
            nameValuePairs.add(new DatabaseManager.NameValuePair(players.name, name));
            nameValuePairs.add(new DatabaseManager.NameValuePair(players.ally, ally));
            nameValuePairs.add(new DatabaseManager.NameValuePair(players.villageCount, villageCount));
            nameValuePairs.add(new DatabaseManager.NameValuePair(players.points, points));
            DatabaseManager.insert(tableName, nameValuePairs);
        }

        public static String getValue(String value, int villageID) {
            List<DatabaseManager.NameValuePair> whereValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            whereValuePairs.add(new DatabaseManager.NameValuePair(players.id, String.valueOf(villageID)));
            String[] selectString = {value};
            ResultSet resultSet = DatabaseManager.select(tableName, selectString, whereValuePairs);
            try {
                if(resultSet.first())
                    return resultSet.getString(value);
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "getValue(String = " + value + ")", "DB->players");
            }
            return null;
        }

        public static void updateValue(int villageID, List<DatabaseManager.NameValuePair> updateValuePairs) {
            List<DatabaseManager.NameValuePair> whereValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            whereValuePairs.add(new DatabaseManager.NameValuePair(players.id, String.valueOf(villageID)));
            DatabaseManager.update(tableName, updateValuePairs, whereValuePairs);
        }
    }

    public static class buildingEvents {
        //region Constants
        public static final String tableName = "tbl_buildingEvents";
        /**
         * triggerTime + villageID
         */
        public static final String gid = "gid";
        public static final String villageID = "villageID";
        public static final String triggerTime = "triggerTime";
        public static final String main = "main";
        public static final String barracks = "barracks";
        public static final String stable = "stable";
        public static final String garage = "garage";
        public static final String church = "church";
        public static final String church_f = "church_f";
        public static final String snob = "snob";
        public static final String smith = "smith";
        public static final String place = "place";
        public static final String statue = "statue";
        public static final String market = "market";
        public static final String wood = "wood";
        public static final String stone = "stone";
        public static final String iron = "iron";
        public static final String farm = "farm";
        public static final String storage = "storage";
        public static final String hide = "hide";
        public static final String wall = "wall";
        //endregion
        
        public static void dropTable(){
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable(){
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "BIGINT PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villageID, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(triggerTime, "BIGINT DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(main, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(barracks, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stable, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(garage, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(church_f, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(smith, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(place, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(statue, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(market, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(farm, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(storage, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(hide, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wall, "INTEGER DEFAULT 0"));
            DatabaseManager.createTable(tableName, nameValuePairs);
        }
    }

    public static class unitCreationEvents {
        //region Constants
        public static final String tableName = "tbl_unitCreationEvents";
        /**
         * triggerTime + villageID
         */
        public static final String gid = "gid";
        public static final String villageID = "villageID";
        public static final String triggerTime = "triggerTime";
        public static final String spear = "spear";
        public static final String sword = "sword";
        public static final String axe = "axe";
        public static final String archer = "archer";
        public static final String spy = "spy";
        public static final String light = "light";
        public static final String marcher = "marcher";
        public static final String heavy = "heavy";
        public static final String ram = "ram";
        public static final String catapult = "catapult";
        public static final String snob = "snob";
        public static final String knight = "knight";
        //endregion

        public static void dropTable(){
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable(){
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "BIGINT PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villageID, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(triggerTime, "BIGINT DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight, "INTEGER DEFAULT 0"));
            DatabaseManager.createTable(tableName, nameValuePairs);
        }
    }

    public static class commandReturnEvents {
        //region Constants
        public static final String tableName = "tbl_commandReturnEvents";
        /**
         * triggerTime + villageID
         */
        public static final String gid = "gid";
        public static final String villageID = "villageID";
        public static final String triggerTime = "triggerTime";
        public static final String wood = "wood";
        public static final String stone = "stone";
        public static final String iron = "iron";
        public static final String spear = "spear";
        public static final String sword = "sword";
        public static final String axe = "axe";
        public static final String archer = "archer";
        public static final String spy = "spy";
        public static final String light = "light";
        public static final String marcher = "marcher";
        public static final String heavy = "heavy";
        public static final String ram = "ram";
        public static final String catapult = "catapult";
        public static final String snob = "snob";
        public static final String knight = "knight";
        //endregion

        public static void dropTable(){
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable(){
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "BIGINT PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villageID, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(triggerTime, "BIGINT DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(wood, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(stone, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(iron, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spear, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(sword, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(axe, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(archer, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(spy, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(light, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(marcher, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(heavy, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(ram, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(catapult, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(snob, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(knight, "INTEGER DEFAULT 0"));
            DatabaseManager.createTable(tableName, nameValuePairs);
        }

        public static void addValue(int villageID, long triggerTime, int wood, int stone, int iron,
                                    int spear, int sword, int axe, int archer,
                                    int spy, int light, int marcher, int heavy,
                                    int ram, int catapult, int snob, int knight) {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.villageID, String.valueOf(villageID)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.triggerTime, String.valueOf(triggerTime)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.wood, String.valueOf(wood)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.stone, String.valueOf(stone)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.iron, String.valueOf(iron)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.spear, String.valueOf(spear)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.sword, String.valueOf(sword)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.axe, String.valueOf(axe)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.archer, String.valueOf(archer)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.spy, String.valueOf(spy)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.light, String.valueOf(light)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.marcher, String.valueOf(marcher)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.heavy, String.valueOf(heavy)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.ram, String.valueOf(ram)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.catapult, String.valueOf(catapult)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.snob, String.valueOf(snob)));
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.knight, String.valueOf(knight)));
            DatabaseManager.insert(tableName, nameValuePairs);
        }
    }

    public static class tasks {
        //region Constants
        public static final String tableName = "tbl_tasks";
        public static final String gid = "gid";
        public static final String villageID = "villageID";
        /**
         * 1=Main, 2=Barracks, 3=Stable, 4=Garage,
         * 5=Church, 6=Church_f, 7=Snob, 8=Smith,
         * 9=Place, 10=Statue, 11=Market,
         * 12=Wood, 13=Stone, 14=Iron,
         * 15=Farm, 16=Storage, 17=Hide, 18=Wall<br>
         * 21=Spear, 22=Sword, 23=Axe, 24=Archer,
         * 25=Spy, 26=Light, 27=Marcher, 28=Heavy
         * 29=Ram, 30=Catapult, 31=Snob, 32= Knight
         */
        public static final String type = "type";
        public static final String value = "value";
        //endregion

        public static void dropTable() {
            DatabaseManager.dropTable(tableName);
        }

        public static void createTable() {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(gid, "INTEGER PRIMARY KEY AUTO_INCREMENT"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(villageID, "INTEGER"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(type, "INTEGER DEFAULT 0"));
            nameValuePairs.add(new DatabaseManager.NameValuePair(value, "INTEGER DEFAULT 1"));
            DatabaseManager.createTable(tableName, nameValuePairs);
        }

        public static void addValue(int villageID, BuildingType buildingType) {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.villageID, String.valueOf(villageID)));
            switch (buildingType) {
                case MAIN:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "1"));
                    break;
                case BARRACKS:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "2"));
                    break;
                case STABLE:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "3"));
                    break;
                case GARAGE:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "4"));
                    break;
                case CHURCH:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "5"));
                    break;
                case CHURCH_F:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "6"));
                    break;
                case SNOB:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "7"));
                    break;
                case SMITH:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "8"));
                    break;
                case PLACE:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "9"));
                    break;
                case STATUE:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "10"));
                    break;
                case MARKET:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "11"));
                    break;
                case WOOD:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "12"));
                    break;
                case STONE:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "13"));
                    break;
                case IRON:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "14"));
                    break;
                case FARM:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "15"));
                    break;
                case STORAGE:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "16"));
                    break;
                case HIDE:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "17"));
                    break;
                case WALL:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "18"));
                    break;
                default:
                    LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown building type: " + buildingType.name(), "addValue(int, BuildingType)", "DB->tasks");
                    break;
            }
            DatabaseManager.insert(tableName, nameValuePairs);
        }

        public static void addValue(int villageID, UnitType unitType) {
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(commandReturnEvents.villageID, String.valueOf(villageID)));
            switch (unitType) {
                case SPEAR:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "21"));
                    break;
                case SWORD:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "22"));
                    break;
                case AXE:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "23"));
                    break;
                case ARCHER:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "24"));
                    break;
                case SPY:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "25"));
                    break;
                case LIGHT:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "26"));
                    break;
                case MARCHER:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "27"));
                    break;
                case HEAVY:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "28"));
                    break;
                case RAM:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "29"));
                    break;
                case CATAPULT:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "30"));
                    break;
                case SNOB:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "31"));
                    break;
                case KNIGHT:
                    nameValuePairs.add(new DatabaseManager.NameValuePair(tasks.type, "32"));
                    break;
                default:
                    LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown unit type: " + unitType.name(), "addValue(int, UnitType)", "DB->tasks");
                    break;
            }
            DatabaseManager.insert(tableName, nameValuePairs);
        }
    }
}