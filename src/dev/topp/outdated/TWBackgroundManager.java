package dev.topp.outdated;

import dev.topp.LoggingManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TWBackgroundManager {

    public static long doWork() {
        EventTrigger();

        if(TWClient.newReport)
            ReadNewReports();

        if(TWClient.newMail)
            ReadNewMails();

        if(TWClient.newForumThread)
            ReadNewForumThreads();

        if(TWClient.newIncomingAttacks)
            ReactOnIncomingAttacks();

        updateResourcesForVillage();

        TaskWorker();

        FarmManager();

        return calculateWorkerSleepDuration();
    }
    
    private static void EventTrigger() {
        BuildingEventTrigger();

        UnitEventTrigger();

        CommandEventTrigger();
    }
    
    private static void BuildingEventTrigger() {
        ResultSet resultSet = null;
        try {
            resultSet = DatabaseManager.customSelect("SELECT "
                           + DB.buildingEvents.villageID + ", " + DB.buildingEvents.gid
                    + ", " + DB.buildingEvents.main + ", " + DB.buildingEvents.barracks
                    + ", " + DB.buildingEvents.stable + ", " + DB.buildingEvents.garage
                    + ", " + DB.buildingEvents.church + ", " + DB.buildingEvents.church_f
                    + ", " + DB.buildingEvents.snob + ", " + DB.buildingEvents.smith
                    + ", " + DB.buildingEvents.place + ", " + DB.buildingEvents.statue
                    + ", " + DB.buildingEvents.market + ", " + DB.buildingEvents.wood
                    + ", " + DB.buildingEvents.stone + ", " + DB.buildingEvents.iron
                    + ", " + DB.buildingEvents.farm + ", " + DB.buildingEvents.storage
                    + ", " + DB.buildingEvents.hide + ", " + DB.buildingEvents.wall
                    + " FROM " + DB.buildingEvents.tableName
                    + " WHERE " + DB.buildingEvents.triggerTime + " <= " + System.currentTimeMillis() + ";");
            if(resultSet.first()) {
                do {
                    DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET "
                            + DB.villages.main_level + " = " + DB.villages.main_level + " + " + resultSet.getString(DB.buildingEvents.main)
                            + ", " + DB.villages.barracks_level + " = " + DB.villages.barracks_level + " + " + resultSet.getString(DB.buildingEvents.barracks)
                            + ", " + DB.villages.stable_level + " = " + DB.villages.stable_level + " + " + resultSet.getString(DB.buildingEvents.stable)
                            + ", " + DB.villages.garage_level + " = " + DB.villages.garage_level + " + " + resultSet.getString(DB.buildingEvents.garage)
                            + ", " + DB.villages.church_level + " = " + DB.villages.church_level + " + " + resultSet.getString(DB.buildingEvents.church)
                            + ", " + DB.villages.church_f_level + " = " + DB.villages.church_f_level + " + " + resultSet.getString(DB.buildingEvents.church_f)
                            + ", " + DB.villages.snob_level + " = " + DB.villages.snob_level + " + " + resultSet.getString(DB.buildingEvents.snob)
                            + ", " + DB.villages.smith_level + " = " + DB.villages.smith_level + " + " + resultSet.getString(DB.buildingEvents.smith)
                            + ", " + DB.villages.place_level + " = " + DB.villages.place_level + " + " + resultSet.getString(DB.buildingEvents.place)
                            + ", " + DB.villages.statue_level + " = " + DB.villages.statue_level + " + " + resultSet.getString(DB.buildingEvents.statue)
                            + ", " + DB.villages.market_level + " = " + DB.villages.market_level + " + " + resultSet.getString(DB.buildingEvents.market)
                            + ", " + DB.villages.wood_level + " = " + DB.villages.wood_level + " + " + resultSet.getString(DB.buildingEvents.wood)
                            + ", " + DB.villages.stone_level + " = " + DB.villages.stone_level + " + " + resultSet.getString(DB.buildingEvents.stone)
                            + ", " + DB.villages.iron_level + " = " + DB.villages.iron_level + " + " + resultSet.getString(DB.buildingEvents.iron)
                            + ", " + DB.villages.farm_level + " = " + DB.villages.farm_level + " + " + resultSet.getString(DB.buildingEvents.farm)
                            + ", " + DB.villages.storage_level + " = " + DB.villages.storage_level + " + " + resultSet.getString(DB.buildingEvents.storage)
                            + ", " + DB.villages.hide_level + " = " + DB.villages.hide_level + " + " + resultSet.getString(DB.buildingEvents.hide)
                            + ", " + DB.villages.wall_level + " = " + DB.villages.wall_level + " + " + resultSet.getString(DB.buildingEvents.wall)
                            + " WHERE " + DB.villages.id + " = " + resultSet.getString(DB.buildingEvents.villageID) + ";");

                    DatabaseManager.customQuery("DELETE FROM " + DB.buildingEvents.tableName + " WHERE "
                            + DB.buildingEvents.gid + " = " + resultSet.getString(DB.buildingEvents.gid) + ";");
                } while (resultSet.next());
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "BuildingEventTrigger()", "TWBackgroundManager");
        } finally {
            try {
                if(resultSet != null)
                    resultSet.close();
            } catch (Exception e) {
                LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Could not close ResultSet: " + e.getClass().getSimpleName() + "->" + e.getMessage(), "BuildingEventTrigger()", "TWBackgroundManager");
            }
        }
    }

    private static void UnitEventTrigger() {
        ResultSet resultSet = null;
        try {
            resultSet = DatabaseManager.customSelect("SELECT " + DB.unitCreationEvents.villageID + ", " + DB.unitCreationEvents.gid
                    + ", " + DB.unitCreationEvents.spear + ", " + DB.unitCreationEvents.sword
                    + ", " + DB.unitCreationEvents.axe + ", " + DB.unitCreationEvents.archer
                    + ", " + DB.unitCreationEvents.spy + ", " + DB.unitCreationEvents.light
                    + ", " + DB.unitCreationEvents.marcher + ", " + DB.unitCreationEvents.heavy
                    + ", " + DB.unitCreationEvents.ram + ", " + DB.unitCreationEvents.catapult
                    + ", " + DB.unitCreationEvents.snob + ", " + DB.unitCreationEvents.knight
                    + " FROM " + DB.unitCreationEvents.tableName
                    + " WHERE " + DB.unitCreationEvents.triggerTime + " <= " + System.currentTimeMillis() + ";");
            if(resultSet.first()) {
                do {
                    DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET "
                            + DB.villages.spear_all + " = " + DB.villages.spear_all + " + " + resultSet.getString(DB.unitCreationEvents.spear)
                            + ", " + DB.villages.sword_all + " = " + DB.villages.sword_all + " + " + resultSet.getString(DB.unitCreationEvents.sword)
                            + ", " + DB.villages.axe_all + " = " + DB.villages.axe_all + " + " + resultSet.getString(DB.unitCreationEvents.axe)
                            + ", " + DB.villages.archer_all + " = " + DB.villages.archer_all + " + " + resultSet.getString(DB.unitCreationEvents.archer)
                            + ", " + DB.villages.spy_all + " = " + DB.villages.spy_all + " + " + resultSet.getString(DB.unitCreationEvents.spy)
                            + ", " + DB.villages.light_all + " = " + DB.villages.light_all + " + " + resultSet.getString(DB.unitCreationEvents.light)
                            + ", " + DB.villages.marcher_all + " = " + DB.villages.marcher_all + " + " + resultSet.getString(DB.unitCreationEvents.marcher)
                            + ", " + DB.villages.heavy_all + " = " + DB.villages.heavy_all + " + " + resultSet.getString(DB.unitCreationEvents.heavy)
                            + ", " + DB.villages.ram_all + " = " + DB.villages.ram_all + " + " + resultSet.getString(DB.unitCreationEvents.ram)
                            + ", " + DB.villages.catapult_all + " = " + DB.villages.catapult_all + " + " + resultSet.getString(DB.unitCreationEvents.catapult)
                            + ", " + DB.villages.snob_all + " = " + DB.villages.snob_all + " + " + resultSet.getString(DB.unitCreationEvents.snob)
                            + ", " + DB.villages.knight_all + " = " + DB.villages.knight_all + " + " + resultSet.getString(DB.unitCreationEvents.knight)
                            + ", " + DB.villages.spear_village + " = " + DB.villages.spear_village + " + " + resultSet.getString(DB.unitCreationEvents.spear)
                            + ", " + DB.villages.sword_village + " = " + DB.villages.sword_village + " + " + resultSet.getString(DB.unitCreationEvents.sword)
                            + ", " + DB.villages.axe_village + " = " + DB.villages.axe_village + " + " + resultSet.getString(DB.unitCreationEvents.axe)
                            + ", " + DB.villages.archer_village + " = " + DB.villages.archer_village + " + " + resultSet.getString(DB.unitCreationEvents.archer)
                            + ", " + DB.villages.spy_village + " = " + DB.villages.spy_village + " + " + resultSet.getString(DB.unitCreationEvents.spy)
                            + ", " + DB.villages.light_village + " = " + DB.villages.light_village + " + " + resultSet.getString(DB.unitCreationEvents.light)
                            + ", " + DB.villages.marcher_village + " = " + DB.villages.marcher_village + " + " + resultSet.getString(DB.unitCreationEvents.marcher)
                            + ", " + DB.villages.heavy_village + " = " + DB.villages.heavy_village + " + " + resultSet.getString(DB.unitCreationEvents.heavy)
                            + ", " + DB.villages.ram_village + " = " + DB.villages.ram_village + " + " + resultSet.getString(DB.unitCreationEvents.ram)
                            + ", " + DB.villages.catapult_village + " = " + DB.villages.catapult_village + " + " + resultSet.getString(DB.unitCreationEvents.catapult)
                            + ", " + DB.villages.snob_village + " = " + DB.villages.snob_village + " + " + resultSet.getString(DB.unitCreationEvents.snob)
                            + ", " + DB.villages.knight_village + " = " + DB.villages.knight_village + " + " + resultSet.getString(DB.unitCreationEvents.knight)
                            + " WHERE " + DB.villages.id + " = " + resultSet.getString(DB.unitCreationEvents.villageID) + "';");

                    DatabaseManager.customQuery("DELETE FROM " + DB.unitCreationEvents.tableName + " WHERE "
                            + DB.unitCreationEvents.gid + " = " + resultSet.getString(DB.unitCreationEvents.gid) + ";");
                } while (resultSet.next());
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "UnitEventTrigger()", "TWBackgroundManager");
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (Exception e) {
                LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Could not close ResultSet: " + e.getClass().getSimpleName() + "->" + e.getMessage(), "UnitEventTrigger()", "TWBackgroundManager");
            }
        }
    }

    private static void CommandEventTrigger() {
        ResultSet resultSet = null;
        try {
             resultSet = DatabaseManager.customSelect("SELECT " + DB.commandReturnEvents.villageID + ", " + DB.commandReturnEvents.gid
                    + ", " + DB.commandReturnEvents.wood + ", " + DB.commandReturnEvents.stone
                    + ", " + DB.commandReturnEvents.iron
                    + ", " + DB.commandReturnEvents.spear + ", " + DB.commandReturnEvents.sword
                    + ", " + DB.commandReturnEvents.axe + ", " + DB.commandReturnEvents.archer
                    + ", " + DB.commandReturnEvents.spy + ", " + DB.commandReturnEvents.light
                    + ", " + DB.commandReturnEvents.marcher + ", " + DB.commandReturnEvents.heavy
                    + ", " + DB.commandReturnEvents.ram + ", " + DB.commandReturnEvents.catapult
                    + ", " + DB.commandReturnEvents.snob + ", " + DB.commandReturnEvents.knight
                    + " FROM " + DB.commandReturnEvents.tableName
                    + " WHERE " + DB.commandReturnEvents.triggerTime + " <= " + System.currentTimeMillis() + ";");
            if(resultSet.first()) {
                do {
                    DatabaseManager.customQuery(DB.villages.res_wood + " = " + DB.villages.res_wood + " + " + resultSet.getString(DB.commandReturnEvents.wood)
                            + ", " + DB.villages.res_stone + " = " + DB.villages.res_stone + " + " + resultSet.getString(DB.commandReturnEvents.stone)
                            + ", " + DB.villages.res_iron + " = " + DB.villages.res_iron + " + " + resultSet.getString(DB.commandReturnEvents.iron)
                            + ", " + DB.villages.spear_village + " = " + DB.villages.spear_village + " + " + resultSet.getString(DB.unitCreationEvents.spear)
                            + ", " + DB.villages.sword_village + " = " + DB.villages.sword_village + " + " + resultSet.getString(DB.unitCreationEvents.sword)
                            + ", " + DB.villages.axe_village + " = " + DB.villages.axe_village + " + " + resultSet.getString(DB.unitCreationEvents.axe)
                            + ", " + DB.villages.archer_village + " = " + DB.villages.archer_village + " + " + resultSet.getString(DB.unitCreationEvents.archer)
                            + ", " + DB.villages.spy_village + " = " + DB.villages.spy_village + " + " + resultSet.getString(DB.unitCreationEvents.spy)
                            + ", " + DB.villages.light_village + " = " + DB.villages.light_village + " + " + resultSet.getString(DB.unitCreationEvents.light)
                            + ", " + DB.villages.marcher_village + " = " + DB.villages.marcher_village + " + " + resultSet.getString(DB.unitCreationEvents.marcher)
                            + ", " + DB.villages.heavy_village + " = " + DB.villages.heavy_village + " + " + resultSet.getString(DB.unitCreationEvents.heavy)
                            + ", " + DB.villages.ram_village + " = " + DB.villages.ram_village + " + " + resultSet.getString(DB.unitCreationEvents.ram)
                            + ", " + DB.villages.catapult_village + " = " + DB.villages.catapult_village + " + " + resultSet.getString(DB.unitCreationEvents.catapult)
                            + ", " + DB.villages.snob_village + " = " + DB.villages.snob_village + " + " + resultSet.getString(DB.unitCreationEvents.snob)
                            + ", " + DB.villages.knight_village + " = " + DB.villages.knight_village + " + " + resultSet.getString(DB.unitCreationEvents.knight)
                            + " WHERE " + DB.villages.id + " = " + resultSet.getString(DB.commandReturnEvents.villageID)  + ";");

                    DatabaseManager.customQuery("DELETE FROM " + DB.commandReturnEvents.tableName + " WHERE "
                            + DB.commandReturnEvents.gid + " = " + resultSet.getString(DB.commandReturnEvents.gid) + ";");
                } while (resultSet.next());
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "UnitEventTrigger()", "TWBackgroundManager");
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (Exception e) {
                LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Could not close ResultSet: " + e.getClass().getSimpleName() + "->" + e.getMessage(), "UnitEventTrigger()", "TWBackgroundManager");
            }
        }
    }

    private static void ReadNewReports() {
        try {
            boolean sitesLeft;

            do {
                sitesLeft = false;
                String report_attack = TWClient.Get("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + TWClient.actVillageID + "&mode=attack&screen=report");
                TWClient.actScreen = TWHelper.ScreenType.REPORT_ATTACK;

                report_attack = report_attack.substring(report_attack.indexOf("<form action=\""));
                report_attack = report_attack.substring(0, report_attack.indexOf("</form>"));
                report_attack = report_attack.substring(report_attack.indexOf("<tr>") + 4);

                while (report_attack.contains("greift")) {
                    sitesLeft = true;
                    report_attack = report_attack.substring(report_attack.indexOf("<td>") + 4);

                    if (report_attack.substring(0, report_attack.indexOf("</tr>")).contains("greift")) {
                        report_attack = report_attack.substring(report_attack.indexOf("name=\"id_") + 9);
                        String reportID = report_attack.substring(0, report_attack.indexOf("\""));

                        //http://de86.die-staemme.de/game.php?village=101774&mode=attack&view=36189950&screen=report
                        String report = TWClient.Get("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + TWClient.actVillageID + "&mode=attack&view=" + reportID + "&screen=report");
                        TWClient.actScreen = TWHelper.ScreenType.REPORT;

                        if (report.contains("Kampfzeit")) {

                            report = report.substring(report.indexOf("Kampfzeit"));
                            report = report.substring(report.indexOf("<td>") + 11);
                            long farmTime = TWHelper.convertTWTimeToLong(report.substring(0, report.indexOf("<")));


                            report = report.substring(report.indexOf("Herkunft:"));
                            report = report.substring(report.indexOf(";id=") + 4);
                            int myVillageID = Integer.parseInt(report.substring(0, report.indexOf("&")));
                            String vill_name = report.substring(report.indexOf(">") + 1, report.indexOf("<"));
                            vill_name = vill_name.substring(vill_name.lastIndexOf("(") + 1, vill_name.lastIndexOf(")"));
                            int my_x = Integer.parseInt(vill_name.substring(0, vill_name.indexOf("|")));
                            int my_y = Integer.parseInt(vill_name.substring(vill_name.indexOf("|") + 1));

                            int spear, sword, axe, archer = 0, spy, light, marcher = 0, heavy, ram, catapult, knight = 0, snob;
                            report = report.substring(report.indexOf("Anzahl:"));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            spear = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            sword = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            axe = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            if (DB.worldConfigs.isArcherActive()) {
                                archer = Integer.parseInt(report.substring(0, report.indexOf("<")));
                                report = report.substring(report.indexOf("unit-item"));
                                report = report.substring(report.indexOf(">") + 1);
                            }
                            spy = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            light = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            if (DB.worldConfigs.isArcherActive()) {
                                marcher = Integer.parseInt(report.substring(0, report.indexOf("<")));
                                report = report.substring(report.indexOf("unit-item"));
                                report = report.substring(report.indexOf(">") + 1);
                            }
                            heavy = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            ram = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            catapult = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            if (DB.worldConfigs.isKnightActive()) {
                                knight = Integer.parseInt(report.substring(0, report.indexOf("<")));
                                report = report.substring(report.indexOf("unit-item"));
                                report = report.substring(report.indexOf(">") + 1);
                            }
                            snob = Integer.parseInt(report.substring(0, report.indexOf("<")));

                            int spear_lost, sword_lost, axe_lost, archer_lost = 0, spy_lost, light_lost, marcher_lost = 0, heavy_lost, ram_lost, catapult_lost, knight_lost = 0, snob_lost;
                            report = report.substring(report.indexOf("Verluste:"));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            spear_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            sword_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            axe_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            if (DB.worldConfigs.isArcherActive()) {
                                archer_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                                report = report.substring(report.indexOf("unit-item"));
                                report = report.substring(report.indexOf(">") + 1);
                            }
                            spy_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            light_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            if (DB.worldConfigs.isArcherActive()) {
                                marcher_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                                report = report.substring(report.indexOf("unit-item"));
                                report = report.substring(report.indexOf(">") + 1);
                            }
                            heavy_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            ram_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            catapult_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                            report = report.substring(report.indexOf("unit-item"));
                            report = report.substring(report.indexOf(">") + 1);
                            if (DB.worldConfigs.isKnightActive()) {
                                knight_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));
                                report = report.substring(report.indexOf("unit-item"));
                                report = report.substring(report.indexOf(">") + 1);
                            }
                            snob_lost = Integer.parseInt(report.substring(0, report.indexOf("<")));

                            report = report.substring(report.indexOf("Ziel:"));
                            report = report.substring(report.indexOf(";id=") + 4);
                            int farmVillageID = Integer.parseInt(report.substring(0, report.indexOf("&")));

                            vill_name = report.substring(report.indexOf(">") + 1, report.indexOf("<"));
                            vill_name = vill_name.substring(vill_name.lastIndexOf("(") + 1, vill_name.lastIndexOf(")"));
                            int target_x = Integer.parseInt(vill_name.substring(0, vill_name.indexOf("|")));
                            int target_y = Integer.parseInt(vill_name.substring(vill_name.indexOf("|") + 1));

                            if (report.contains("Keiner deiner Kämpfer ist lebend zurückgekehrt. Es konnten keine Informationen über die Truppenstärke des Gegners erlangt werden")
                                    | (spear == spear_lost && sword == sword_lost && axe == axe_lost && archer == archer_lost
                                                    && light == light_lost && marcher == marcher_lost && heavy == heavy_lost)) {
                                List<DatabaseManager.NameValuePair> updateValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
                                updateValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.village_farm_flag_type, "1"));
                                updateValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.village_farm_flag_value, String.valueOf(System.currentTimeMillis() + 60 * 60 * 24)));
                                List<DatabaseManager.NameValuePair> whereValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
                                whereValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.id, String.valueOf(farmVillageID)));
                                DatabaseManager.update(DB.villages.tableName, updateValuePairs, whereValuePairs);
                            } else {
                                int fvill_wood = 0, fvill_stone = 0, fvill_iron = 0;
                                byte fvill_main_level = 0, fvill_barracks_level = 0, fvill_stable_level = 0, fvill_garage_level = 0, fvill_church_level = 0, fvill_church_f_level = 0, fvill_snob_level = 0, fvill_smith_level = 0, fvill_place_level = 0, fvill_statue_level = 0, fvill_market_level = 0, fvill_wood_level = 1, fvill_stone_level = 1, fvill_iron_level = 1, fvill_farm_level = 0, fvill_storage_level = 3, fvill_hide_level = 0, fvill_wall_level = 0;

                                report = report.replace("<span class=\"grey\">.</span>", "");

                                if (report.contains("Spionage")) {
                                    report = report.substring(report.indexOf("Spionage"));

                                    String spionage = report.substring(0, report.indexOf("</table>"));
                                    if (spionage.contains("icon header wood")) {
                                        spionage = spionage.substring(spionage.indexOf("</span>") + 7);
                                        fvill_wood = Integer.parseInt(spionage.substring(0, spionage.indexOf("<") - 1).replace(".", ""));
                                    }
                                    if (spionage.contains("icon header stone")) {
                                        spionage = spionage.substring(spionage.indexOf("</span>") + 7);
                                        fvill_stone = Integer.parseInt(spionage.substring(0, spionage.indexOf("<") - 1).replace(".", ""));
                                    }
                                    if (spionage.contains("icon header iron")) {
                                        spionage = spionage.substring(spionage.indexOf("</span>") + 7);
                                        fvill_iron = Integer.parseInt(spionage.substring(0, spionage.indexOf("<") - 1).replace(".", ""));
                                    }

                                    if (spionage.contains("Hauptgebäude")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_main_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Kaserne")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_barracks_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Stall")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_stable_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Werkstatt")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_garage_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Erste Kirche")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_church_f_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Kirche")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_church_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Adelshof")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_snob_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Schmiede")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_smith_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Versammlungsplatz")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_place_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Statue")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_statue_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Marktplatz")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_market_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Holzfäller")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_wood_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    } else fvill_wood_level = 0;
                                    if (spionage.contains("Lehmgrube")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_stone_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    } else fvill_stone_level = 0;
                                    if (spionage.contains("Eisenmine")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_iron_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    } else fvill_iron_level = 0;
                                    if (spionage.contains("Bauernhof")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_farm_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Speicher")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_storage_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Versteck")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_hide_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                    if (spionage.contains("Wall")) {
                                        spionage = spionage.substring(spionage.indexOf("(") + 7);
                                        fvill_wall_level = Byte.parseByte(spionage.substring(0, spionage.indexOf(")")));
                                    }
                                }

                                int wood = 0, stone = 0, iron = 0, all_get, all_max;
                                if (report.contains("Beute:")) {
                                    report = report.substring(report.indexOf("Beute:"));

                                    if (report.contains("icon header wood")) {
                                        report = report.substring(report.indexOf("icon header wood"));
                                        report = report.substring(report.indexOf("</span>") + 7);
                                        String wood_string = report.substring(0, report.indexOf("<"));
                                        wood = Integer.parseInt(wood_string);
                                    }

                                    if (report.contains("icon header stone")) {
                                        report = report.substring(report.indexOf("icon header stone"));
                                        report = report.substring(report.indexOf("</span>") + 7);
                                        String stone_string = report.substring(0, report.indexOf("<"));
                                        stone = Integer.parseInt(stone_string);
                                    }

                                    if (report.contains("icon header iron")) {
                                        report = report.substring(report.indexOf("icon header iron"));
                                        report = report.substring(report.indexOf("</span>") + 7);
                                        String iron_string = report.substring(0, report.indexOf("<"));
                                        iron = Integer.parseInt(iron_string);
                                    }

                                    report = report.substring(report.indexOf("<td>") + 4);
                                    all_get = Integer.parseInt(report.substring(0, report.indexOf("/")));

                                    report = report.substring(report.indexOf("/") + 1);
                                    String all_max_string = report.substring(0, report.indexOf("</td>"));
                                    all_max = Integer.parseInt(all_max_string);

                                    if (all_get >= all_max && fvill_wood == 0 && fvill_stone == 0 && fvill_iron == 0) {
                                        fvill_wood = 2000;
                                        fvill_stone = 2000;
                                        fvill_iron = 2000;
                                    }
                                }

                                double velocity = TWHelper.GetUnitConfigInfo(TWHelper.UnitType.SPY, "speed");
                                if (snob != 0) velocity = TWHelper.GetUnitConfigInfo(TWHelper.UnitType.SNOB, "speed");
                                else if (ram != 0 | catapult != 0) velocity = TWHelper.GetUnitConfigInfo(TWHelper.UnitType.RAM, "speed");
                                else if (sword != 0) velocity = TWHelper.GetUnitConfigInfo(TWHelper.UnitType.SWORD, "speed");
                                else if (spear != 0 | axe != 0 | archer != 0)
                                    velocity = TWHelper.GetUnitConfigInfo(TWHelper.UnitType.SPEAR, "speed");
                                else if (heavy != 0) velocity = TWHelper.GetUnitConfigInfo(TWHelper.UnitType.HEAVY, "speed");
                                else if (light != 0 | marcher != 0 | knight != 0)
                                    velocity = TWHelper.GetUnitConfigInfo(TWHelper.UnitType.LIGHT, "speed");
                                double travelTime = Math.sqrt(Math.pow((my_x - target_x), 2) + Math.pow((my_y - target_y), 2)) * velocity * Float.parseFloat(DB.worldConfigs.getValue(DB.worldConfigs.speed)) * Float.parseFloat(DB.worldConfigs.getValue(DB.worldConfigs.unit_speed));
                                long returnTime = farmTime + (long)(travelTime * 60 * 1000) + 5; // 5 seconds buffer

                                DB.commandReturnEvents.addValue(myVillageID, returnTime, wood, stone, iron,
                                        spear - spear_lost, sword - sword_lost, axe - axe_lost, archer - archer_lost,
                                        spy - spy_lost, light - light_lost, marcher - marcher_lost, heavy - heavy_lost,
                                        ram - ram_lost, catapult - catapult_lost, snob - snob_lost, knight - knight_lost);

                                DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET "
                                        + DB.villages.res_wood + " = " + fvill_wood
                                        + ", " + DB.villages.res_stone + " = " + fvill_stone
                                        + ", " + DB.villages.res_iron + " = " + fvill_iron
                                        + ", " + DB.villages.main_level + " = " + fvill_main_level
                                        + ", " + DB.villages.barracks_level + " = " + fvill_barracks_level
                                        + ", " + DB.villages.stable_level + " = " + fvill_stable_level
                                        + ", " + DB.villages.garage_level + " = " + fvill_garage_level
                                        + ", " + DB.villages.church_f_level + " = " + fvill_church_f_level
                                        + ", " + DB.villages.church_level + " = " + fvill_church_level
                                        + ", " + DB.villages.snob_level + " = " + fvill_snob_level
                                        + ", " + DB.villages.smith_level + " = " + fvill_smith_level
                                        + ", " + DB.villages.statue_level + " = " + fvill_statue_level
                                        + ", " + DB.villages.place_level + " = " + fvill_place_level
                                        + ", " + DB.villages.market_level + " = " + fvill_market_level
                                        + ", " + DB.villages.wood_level + " = " + fvill_wood_level
                                        + ", " + DB.villages.stone_level + " = " + fvill_stone_level
                                        + ", " + DB.villages.iron_level + " = " + fvill_iron_level
                                        + ", " + DB.villages.farm_level + " = " + fvill_farm_level
                                        + ", " + DB.villages.storage_level + " = " + fvill_storage_level
                                        + ", " + DB.villages.hide_level + " = " + fvill_hide_level
                                        + ", " + DB.villages.wall_level + " = " + fvill_wall_level
                                        + " WHERE " + DB.villages.id + " = " + farmVillageID + ";");

                            }

                            DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET "
                                    + DB.villages.farmTime + " = 0"
                                    + " WHERE " + DB.villages.id + " = " + farmVillageID + ";");

                            DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET "
                                    + DB.villages.spear_all + " = " + DB.villages.spear_all + " - " + spear_lost
                                    + ", " + DB.villages.sword_all + " = " + DB.villages.sword_all + " - " + sword_lost
                                    + ", " + DB.villages.axe_all + " = " + DB.villages.axe_all + " - " + axe_lost
                                    + ", " + DB.villages.archer_all + " = " + DB.villages.archer_all + " - " + archer_lost
                                    + ", " + DB.villages.spy_all + " = " + DB.villages.spy_all + " - " + spy_lost
                                    + ", " + DB.villages.light_all + " = " + DB.villages.light_all + " - " + light_lost
                                    + ", " + DB.villages.marcher_all + " = " + DB.villages.marcher_all + " - " + marcher_lost
                                    + ", " + DB.villages.heavy_all + " = " + DB.villages.heavy_all + " - " + heavy_lost
                                    + ", " + DB.villages.ram_all + " = " + DB.villages.ram_all + " - " + ram_lost
                                    + ", " + DB.villages.catapult_all + " = " + DB.villages.catapult_all + " - " + catapult_lost
                                    + ", " + DB.villages.snob_all + " = " + DB.villages.snob_all + " - " + snob_lost
                                    + ", " + DB.villages.knight_all + " = " + DB.villages.knight_all + " - " + knight_lost
                                    + " WHERE " + DB.villages.id + " = " + myVillageID + ";");


                            report = report.substring(report.indexOf("action=del_one"));
                            report = report.substring(report.indexOf("h=") + 2);
                            String h = report.substring(0, report.indexOf("&"));

                            //http://de86.die-staemme.de/game.php?village=101774&mode=attack&action=del_one&h=109d&id=36189950&screen=report
                            TWClient.Get("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + TWClient.actVillageID + "&mode=attack&action=del_one&h=" + h + "&id=" + reportID + "&screen=report");
                        }
                    }
                    report_attack = report_attack.substring(report_attack.indexOf("<tr>") + 4);
                }
            } while (sitesLeft);
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "ReadNewReports()", "TWBackgroundManager");
        }
    }

    private static void ReadNewMails() {
        try {
            // ToDo: Implement ReadNewMails
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "ReadNewMails()", "TWBackgroundManager");
        }
    }

    private static void ReadNewForumThreads() {
        try {
            // ToDo: Implement ReadNewForumThreads
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "ReadNewForumThreads()", "TWBackgroundManager");
        }
    }

    private static void ReactOnIncomingAttacks() {
        try {
            // ToDo: Implement ReactOnIncomingAttacks
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "ReactOnIncomingAttacks()", "TWBackgroundManager");
        }
    }

    public static void updateResourcesForVillage() {
        //r_wood += c_speed * 25.807 * Math.Exp(0.1511 * double.Parse(reader[3].ToString())) * hour * bonus_wood;
        float c_speed = Float.parseFloat(DB.worldConfigs.getValue(DB.worldConfigs.speed));
        String time_hour = "((" + System.currentTimeMillis() + " - " + DB.villages.res_updateDate + ")/(1000*60*60))";
        String delta_res_wood = "(" + c_speed * 25.807 + " * EXP(0.1511 * " + DB.villages.wood_level + ") * " + time_hour + " * "
                                        + DB.villages.bonus_wood + " * " + DB.villages.bonus_resources + " * " + DB.villages.flag_resources + ")";
        String delta_res_stone = "(" + c_speed * 25.807 + " * EXP(0.1511 * " + DB.villages.stone_level + ") * " + time_hour + " * "
                                        + DB.villages.bonus_stone + " * " + DB.villages.bonus_resources + " * " + DB.villages.flag_resources + ")";
        String delta_res_iron = "(" + c_speed * 25.807 + " * EXP(0.1511 * " + DB.villages.iron_level + ") * " + time_hour + " * "
                                        + DB.villages.bonus_iron + " * " + DB.villages.bonus_resources + " * " + DB.villages.flag_resources + ")";
        DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET " + DB.villages.res_wood + " = " + DB.villages.res_wood + " + " + delta_res_wood + ", "
                                                                                + DB.villages.res_stone + " = " + DB.villages.res_stone + " + " + delta_res_stone + ", "
                                                                                + DB.villages.res_wood + " = " + DB.villages.res_wood + " + " + delta_res_iron + ", "
                                                                                + DB.villages.res_updateDate + " = " + System.currentTimeMillis() + ";");
    }

    private static long calculateWorkerSleepDuration() {
        if (TWClient.newReport || TWClient.newMail || TWClient.newForumThread || TWClient.newIncomingAttacks)
            return 0;

        long sleepTime = 0;

        ResultSet resultSet = null;
        try {
            resultSet = DatabaseManager.customSelect("SELECT " + DB.buildingEvents.triggerTime + " FROM " + DB.buildingEvents.tableName
                    + " ORDER BY " + DB.buildingEvents.villageID + " ASC, " + DB.buildingEvents.triggerTime + "DESC;");
            if (resultSet.first())
                sleepTime = resultSet.getLong(DB.buildingEvents.triggerTime);
            resultSet = DatabaseManager.customSelect("SELECT " + DB.unitCreationEvents.triggerTime + " FROM " + DB.unitCreationEvents.tableName
                    + " ORDER BY " + DB.unitCreationEvents.triggerTime + "ASC;");
            if (resultSet.first() && resultSet.getLong(DB.unitCreationEvents.triggerTime) < sleepTime)
                sleepTime = resultSet.getLong(DB.unitCreationEvents.triggerTime);
            resultSet = DatabaseManager.customSelect("SELECT " + DB.commandReturnEvents.triggerTime + " FROM " + DB.commandReturnEvents.tableName
                    + " ORDER BY " + DB.commandReturnEvents.triggerTime + "ASC;");
            if (resultSet.first() && resultSet.getLong(DB.commandReturnEvents.triggerTime) < sleepTime)
                sleepTime = resultSet.getLong(DB.commandReturnEvents.triggerTime);
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "calculateWorkerSleepDuration()", "BackgroundWorker");
            return -1;
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not close resultSet: " + e.getClass().getSimpleName() + "->" + e.getMessage(), "calculateWorkerSleepDuration()", "BackgroundWorker");
            }
        }

        long sleepDuration = sleepTime - System.currentTimeMillis();
        if(sleepDuration < 0)
            sleepDuration = 600000; // if no time set, sleep 10 min

        return sleepDuration;
    }

    private static void TaskWorker() {
        try {
            String[] villageSelectString = {DB.villages.id, DB.villages.buildqueue_count};
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.player, String.valueOf(TWClient.userID)));
            ResultSet villages = DatabaseManager.select(DB.villages.tableName, villageSelectString, nameValuePairs);
            if (villages.first()) {
                do {
                    String[] taskSelectString = {DB.tasks.type, DB.tasks.value};
                    nameValuePairs.clear();
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.tasks.villageID, villages.getString(DB.villages.id)));
                    ResultSet tasks = DatabaseManager.select(DB.tasks.tableName, taskSelectString, nameValuePairs);
                    if(tasks.first()) {
                        do {
                            if (tasks.getInt(DB.tasks.type) < 20) { // Building
                                if (villages.getInt(DB.villages.buildqueue_count) >= 2)
                                    break;
                                TWHelper.BuildingType buildingType = TWHelper.getBuildingTypeFromNumber(tasks.getInt(DB.tasks.type));
                                String buildingLevelDbName = "";
                                switch (buildingType) {
                                    case MAIN:
                                        buildingLevelDbName = DB.villages.main_level;
                                        break;
                                    case BARRACKS:
                                        buildingLevelDbName = DB.villages.barracks_level;
                                        break;
                                    case STABLE:
                                        buildingLevelDbName = DB.villages.stable_level;
                                        break;
                                    case GARAGE:
                                        buildingLevelDbName = DB.villages.garage_level;
                                        break;
                                    case CHURCH:
                                        buildingLevelDbName = DB.villages.church_level;
                                        break;
                                    case CHURCH_F:
                                        buildingLevelDbName = DB.villages.church_f_level;
                                        break;
                                    case SNOB:
                                        buildingLevelDbName = DB.villages.snob_level;
                                        break;
                                    case SMITH:
                                        buildingLevelDbName = DB.villages.smith_level;
                                        break;
                                    case PLACE:
                                        buildingLevelDbName = DB.villages.place_level;
                                        break;
                                    case STATUE:
                                        buildingLevelDbName = DB.villages.statue_level;
                                        break;
                                    case MARKET:
                                        buildingLevelDbName = DB.villages.market_level;
                                        break;
                                    case WOOD:
                                        buildingLevelDbName = DB.villages.wood_level;
                                        break;
                                    case STONE:
                                        buildingLevelDbName = DB.villages.stone_level;
                                        break;
                                    case IRON:
                                        buildingLevelDbName = DB.villages.iron_level;
                                        break;
                                    case FARM:
                                        buildingLevelDbName = DB.villages.farm_level;
                                        break;
                                    case STORAGE:
                                        buildingLevelDbName = DB.villages.storage_level;
                                        break;
                                    case HIDE:
                                        buildingLevelDbName = DB.villages.hide_level;
                                        break;
                                    case WALL:
                                        buildingLevelDbName = DB.villages.wall_level;
                                        break;
                                }

                                String[] villageSelectStringDetailed = {DB.villages.res_wood, DB.villages.res_stone, DB.villages.res_iron, DB.villages.res_pop, DB.villages.farm_level, buildingLevelDbName};
                                nameValuePairs.clear();
                                nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.id, villages.getString(DB.villages.id)));
                                ResultSet villagesDetailed = DatabaseManager.select(DB.villages.tableName, villageSelectStringDetailed, nameValuePairs);
                                if (villagesDetailed.first()) {
                                    int buildingLevel = villagesDetailed.getInt(buildingLevelDbName);

                                    // Check resources
                                    if (TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "wood") <= villagesDetailed.getInt(DB.villages.res_wood)
                                            && TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "stone") <= villagesDetailed.getInt(DB.villages.res_stone)
                                            && TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "iron") <= villagesDetailed.getInt(DB.villages.res_iron)
                                            && TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "pop") <=
                                            (TWHelper.GetBuildingValue(TWHelper.BuildingType.FARM, villagesDetailed.getInt(DB.villages.farm_level), "effect") - villagesDetailed.getInt(DB.villages.res_pop))) {

                                        // Goto Main, this will update village resources
                                        TWClient.Goto(TWHelper.ScreenType.MAIN, villages.getInt(DB.villages.id));

                                        // Check resources again
                                        if (TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "wood") <= villagesDetailed.getInt(DB.villages.res_wood)
                                                && TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "stone") <= villagesDetailed.getInt(DB.villages.res_stone)
                                                && TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "iron") <= villagesDetailed.getInt(DB.villages.res_iron)
                                                && TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "pop") <=
                                                (TWHelper.GetBuildingValue(TWHelper.BuildingType.FARM, villagesDetailed.getInt(DB.villages.farm_level), "effect") - villagesDetailed.getInt(DB.villages.res_pop))) {
                                            // Build
                                            if (TWClient.Build(buildingType, villages.getInt(DB.villages.id))) {
                                                // Delete task
                                                

                                                // Update resources of village
                                                List<DatabaseManager.NameValuePair> resourceUpdatePair = new ArrayList<DatabaseManager.NameValuePair>();
                                                resourceUpdatePair.add(new DatabaseManager.NameValuePair(DB.villages.res_wood, String.valueOf(villagesDetailed.getInt(DB.villages.res_wood) - TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "wood"))));
                                                resourceUpdatePair.add(new DatabaseManager.NameValuePair(DB.villages.res_stone, String.valueOf(villagesDetailed.getInt(DB.villages.res_stone) - TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "stone"))));
                                                resourceUpdatePair.add(new DatabaseManager.NameValuePair(DB.villages.res_iron, String.valueOf(villagesDetailed.getInt(DB.villages.res_iron) - TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "iron"))));
                                                resourceUpdatePair.add(new DatabaseManager.NameValuePair(DB.villages.res_pop, String.valueOf(villagesDetailed.getInt(DB.villages.res_pop) + TWHelper.GetBuildingValue(buildingType, buildingLevel + 1, "pop"))));
                                                DB.villages.updateValue(villages.getInt(DB.villages.id), resourceUpdatePair);
                                            }
                                        } else
                                            break;
                                    } else
                                        break;
                                }
                            } else { // Unit
                                TWHelper.BuildingType buildingType = TWHelper.getBuildingTypeFromNumber(tasks.getInt(DB.tasks.type));
                            }


                        } while (tasks.next());
                    }

                } while (villages.next());
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "TaskWorker()", "TWBackgroundManager");
        }
    }

    private static void FarmManager() {
        try {
            // ToDo: Implement FarmManager
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "FarmManager()", "TWBackgroundManager");
        }
    }
}
