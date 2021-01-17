package dev.topp.outdated;

import dev.topp.LoggingManager;
import dev.topp.outdated.TWHelper.*;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StartUpManager {

    private enum FileType {
        CONFIG,
        VILLAGE,
        PLAYER,
        UNIT_INFO,
        BUILDING_INFO
    }

    public static void doStartUp() {
        downloadTWFiles();
        setDatabaseUpdateDateToCurrent();

        String response = TWClient.Goto(ScreenType.OVERVIEW_VILLAGES, 0);

        System.out.println("Getting local time difference...");
        TWHelper.GetTimeDifference(response);

        System.out.println("Reading UserID...");
        TWHelper.GetUserID(response);

        System.out.println("Getting village information...");
        TWHelper.GetSiteInformation(response, ScreenType.OVERVIEW_VILLAGES);

        String[] selectString = {DB.villages.id, DB.villages.name};
        List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.player, String.valueOf(TWClient.userID)));

        ResultSet resultSet = DatabaseManager.select(DB.villages.tableName, selectString, nameValuePairs);
        try {
            if(resultSet.first()) {
                int villageId = resultSet.getInt(DB.villages.id);
                String villageName = resultSet.getString(DB.villages.name);
                do {
                    System.out.println("Reading " + villageName + "...");
                    response = TWClient.Goto(ScreenType.OVERVIEW, resultSet.getInt(DB.villages.id));
                    TWHelper.GetReturningTroops(response, villageId);
                    TWClient.Goto(ScreenType.MAIN, villageId);
                    TWClient.Goto(ScreenType.TRAIN, villageId);
                    if(DB.villages.getValue(DB.villages.statue_level, villageId).equals("1"))
                        TWClient.Goto(ScreenType.STATUE, villageId);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getClass().getSimpleName() + "->" + e.getMessage(), "doStartUp()", "StartUpManager");
        }

        System.out.println("Calculating farm distances...");
        calculateDistances();
    }

    private static void setDatabaseUpdateDateToCurrent() {
        List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.lastDatabaseUpdateDate, String.valueOf(System.currentTimeMillis())));
        DB.worldConfigs.updateValue(nameValuePairs);
    }

    private static void downloadTWFiles() {
        if (DB.worldConfigs.isEmpty()) {
            DB.worldConfigs.insertDefault();
            downloadFile("http://" + LoginManager.getWorld() + ".die-staemme.de/interface.php?func=get_config", "config.txt", FileType.CONFIG);
        }

        if (System.currentTimeMillis() - Long.parseLong(DB.worldConfigs.getValue(DB.worldConfigs.lastDatabaseUpdateDate)) >= Long.parseLong(DB.worldConfigs.getValue(DB.worldConfigs.databaseUpdateInterval))) {
            downloadFile("http://" + LoginManager.getWorld() + ".die-staemme.de/map/village.txt", "village.txt", FileType.VILLAGE);

            downloadFile("http://" + LoginManager.getWorld() + ".die-staemme.de/map/player.txt", "player.txt", FileType.PLAYER);
        }

        if (DB.unitInfo.isEmpty()) {
            DB.unitInfo.insertDefault();
            downloadFile("http://" + LoginManager.getWorld() + ".die-staemme.de/interface.php?func=get_unit_info", "unitInfo.txt", FileType.UNIT_INFO);
        }

        if (DB.buildingInfo.isEmpty()) {
            DB. buildingInfo.insertDefault();
            downloadFile("http://" + LoginManager.getWorld() + ".die-staemme.de/interface.php?func=get_building_info", "buildingInfo.txt", FileType.BUILDING_INFO);
        }
    }

    private static void deleteTWFile(String filename) {
        try {
            File file = new File(filename);

            if (file.delete()) {
                LoggingManager.log(LoggingManager.LoggingLevel.INFO, filename + " is deleted.", "deleteTWFiles(String)", "StartUpManager");
            } else {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, filename + " delete operation failed.", "deleteTWFiles(String)", "StartUpManager");
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, filename + " delete operation failed: " + e.getClass().getSimpleName() + "->" + e.getMessage(), "deleteTWFiles(String)", "StartUpManager");
        }
    }

    private static void downloadFile(String url, String filename, FileType fileType) {
        System.out.print("Downloading File");
        switch (fileType) {
            case CONFIG:
                System.out.println(" [" + "worldConfig" + "] ...");
                break;
            case VILLAGE:
                System.out.println(" [" + "village" + "] ...");
                break;
            case PLAYER:
                System.out.println(" [" + "player" + "] ...");
                break;
            case UNIT_INFO:
                System.out.println(" [" + "unitInfo" + "] ...");
                break;
            case BUILDING_INFO:
                System.out.println(" [" + "buildingInfo" + "] ...");
                break;
            default:
                System.out.println(" [" + url + "] ...");
                break;
        }
        MyHttpClient.downloadFile(url, filename);

        System.out.println("\tDownload finished. Resolving file now.");

        FileInputStream fStream = null;
        try {
            fStream = new FileInputStream(filename);
            DataInputStream in = null;
            try {
                in = new DataInputStream(fStream);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(in));

                    String strLine;

                    int lines = countLinesInFile(filename);

                    int actLine = 0;
                    int percentage = 0;

                    DatabaseManager.beginTransaction();

                    try {
                        while ((strLine = br.readLine()) != null) {
                            if(!strLine.isEmpty()) {
                                switch (fileType) {
                                    case CONFIG:
                                        resolveConfigLine(strLine);
                                        break;
                                    case VILLAGE:
                                        resolveVillageLine(strLine);
                                        break;
                                    case PLAYER:
                                        resolvePlayerLine(strLine);
                                        break;
                                    case UNIT_INFO:
                                        resolveUnitInfoLine(strLine);
                                        break;
                                    case BUILDING_INFO:
                                        resolveBuildingInfoLine(strLine);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            actLine++;
                            if(percentage < actLine * 100/lines) {
                                percentage = actLine * 100/lines;
                                updateProgress(percentage);
                            }
                        }
                    } catch (IOException e) {
                        LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "downloadFile(String, String, FileType)", "StartUpManager");
                    }
                    System.out.println();

                    DatabaseManager.setTransactionSuccessful();
                } finally {
                    DatabaseManager.endTransaction();
                    try {
                        if (br != null) {
                            br.close();
                            br = null;
                        }
                    } catch (IOException e) {
                        LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not close BufferedReader: " + e.getMessage(), "downloadFile(String, String, FileType)", "StartUpManager");
                    }
                }
            } finally {
                try {
                    if (in != null) {
                        in.close();
                        in = null;
                    }
                } catch (IOException e) {
                    LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not close DataInputStream: " + e.getMessage(), "downloadFile(String, String, FileType)", "StartUpManager");
                }
            }
        } catch (FileNotFoundException e) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not access file: " + e.getMessage(), "downloadFile(String, String, FileType)", "StartUpManager");
        } finally {
            try {
                if (fStream != null) {
                    fStream.close();
                    fStream = null;
                }
            } catch (IOException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not close FileInputStream: " + e.getMessage(), "downloadFile(String, String, FileType)", "StartUpManager");
            }
        }
        System.gc();
        deleteTWFile(filename);
    }

    private static void updateProgress(double progressPercentage) {
        final int width = 50; // progress bar width in chars

        if (progressPercentage > 1)
            progressPercentage /= 100;
        System.out.print("\r\t[");
        int i = 0;
        for (; i <= (int)(progressPercentage*width); i++) {
            System.out.print("=");
        }
        for (; i < width; i++) {
            System.out.print(" ");
        }
        System.out.print("]");
    }

    private static int countLinesInFile(String filename) {
        InputStream is = null;
        int count = 0;
        boolean empty = true;
        try {
            is = new BufferedInputStream(new FileInputStream(filename));
            byte[] c = new byte[1024];
            int readChars;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
        }catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getMessage(), "countLinesInFile(String)", "StartUpManager");
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not close InputStream: " + e.getMessage(), "countLinesInFile(String)", "StartUpManager");
            }
        }
        return (count == 0 && !empty) ? 1 : count;
    }

    private static boolean ConfigFileNight;
    private static void resolveConfigLine(String strLine) {
        List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
        if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<unit_speed>[0-9.]*</unit_speed>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.unit_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<moral>\\d*</moral>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.moral, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<destroy>\\d*</destroy>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.build_destroy, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<trade_cancel_time>\\d*</trade_cancel_time>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.trade_cancel_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<millis_arrival>\\d*</millis_arrival>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.command_millis_arrival, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<command_cancel_time>\\d*</command_cancel_time>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.command_cancel_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<days>\\d*</days>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.newbie_days, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<base_config>\\d*</base_config>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.game_base_config, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<new_buildtime_formula>\\d*</new_buildtime_formula>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.new_buildtime_formula, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<knight>\\d*</knight>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.knight, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<archer>\\d*</archer>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<tech>\\d*</tech>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.tech, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<spy>\\d*</spy>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.spy, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<farm_limit>\\d*</farm_limit>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.farm_limit, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<church>\\d*</church>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.church, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<fake_limit>\\d*</fake_limit>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.fake_limit, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<base_production>\\d*</base_production>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.base_production, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<gold>\\d*</gold>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_gold, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<cheap_rebuild>\\d*</cheap_rebuild>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_cheap_rebuild, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<simple>\\d*</simple>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_simple, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<rise>\\d*</rise>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_rise, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<max_dist>\\d*</max_dist>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_max_dist, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<factor>\\d*</factor>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<coin_wood>\\d*</coin_wood>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_coin_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<coin_stone>\\d*</coin_stone>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_coin_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<coin_iron>\\d*</coin_iron>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.snob_coin_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<bonus_villages>\\d*</bonus_villages>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.bonus_villages, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<sleep>\\s*$")) {
            ConfigFileNight = false;
        }  else if(strLine.matches("^\\s*<night>\\s*$")) {
            ConfigFileNight = true;
        } else if(strLine.matches("^\\s*<active>\\d*</active>\\s*$") && ConfigFileNight) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.night_active, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<start_hour>\\d*</start_hour>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.night_start_hour, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<end_hour>\\d*</end_hour>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.night_end_hour, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<def_factor>\\d*</def_factor>\\s*$")) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.worldConfigs.night_def_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.worldConfigs.tableName, nameValuePairs, null);
        }
    }

    private static void resolveVillageLine(String strLine) {
        // $id, $name, $x, $y, $player, $points, $rank
        String[] tmp = strLine.split(",");
        tmp[1] = tmp[1].replace("+", " ")
                .replace("%C2%AB", "«")
                .replace("%C2%BB", "»")
                .replace("%C2%B2", "²")
                .replace("%C2%B3", "³")
                .replace("%C3%B6", "ö")
                .replace("%C3%BC", "ü")
                .replace("%C3%9F", "ß")
                .replace("%C3%84", "Ä")
                .replace("%C3%96", "Ö")
                .replace("%C3%9C", "Ü")
                .replace("%C3%A4", "ä")
                .replace("%21", "!")
                .replace("%23", "#")
                .replace("%24", "$")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2A", "*")
                .replace("%2B", "+")
                .replace("%2C", ",")
                .replace("%2F", "/")
                .replace("%3A", ":")
                .replace("%3B", ";")
                .replace("%3D", "=")
                .replace("%3F", "?")
                .replace("%40", "@")
                .replace("%5B", "[")
                .replace("%5D", "]")
                .replace("%7B", "{")
                .replace("%7C", "|")
                .replace("%7D", "}")
                .replace("%7E", "~")
                .replace("%25", "%");

        DB.villages.insertOrUpdateValue(tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5]);
    }

    private static void resolvePlayerLine(String strLine) {
        // $id, $name, $ally, $villages, $points, $rank
        String[] tmp = strLine.split(",");
        tmp[1] = tmp[1].replace("+", " ")
                .replace("%C2%AB", "«")
                .replace("%C2%BB", "»")
                .replace("%C2%B2", "²")
                .replace("%C2%B3", "³")
                .replace("%C3%B6", "ö")
                .replace("%C3%BC", "ü")
                .replace("%C3%9F", "ß")
                .replace("%C3%84", "Ä")
                .replace("%C3%96", "Ö")
                .replace("%C3%9C", "Ü")
                .replace("%C3%A4", "ä")
                .replace("%21", "!")
                .replace("%23", "#")
                .replace("%24", "$")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%2A", "*")
                .replace("%2B", "+")
                .replace("%2C", ",")
                .replace("%2F", "/")
                .replace("%3A", ":")
                .replace("%3B", ";")
                .replace("%3D", "=")
                .replace("%3F", "?")
                .replace("%40", "@")
                .replace("%5B", "[")
                .replace("%5D", "]")
                .replace("%7B", "{")
                .replace("%7C", "|")
                .replace("%7D", "}")
                .replace("%7E", "~")
                .replace("%25", "%");


        String oldPoints;
        try {
            //LoggingManager.stopDebugging();
            oldPoints = DB.players.getValue(DB.players.points, Integer.parseInt(tmp[0]));
        } finally {
            //LoggingManager.startDebugging();
        }


        List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.players.name, tmp[1]));
        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.players.ally, tmp[2]));
        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.players.villageCount, tmp[3]));
        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.players.points, tmp[4]));
        if (oldPoints != null)
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.players.old_points, oldPoints));
        DB.players.updateValue(Integer.parseInt(tmp[0]), nameValuePairs);

        if (oldPoints != null) {
            if (tmp[4].equals(oldPoints)
                    && Long.parseLong(DB.worldConfigs.getValue(DB.worldConfigs.lastDatabaseUpdateDate))
                    + Long.parseLong(DB.worldConfigs.getValue(DB.worldConfigs.declarePlayerAsFarmInterval)) > System.currentTimeMillis()) {
                nameValuePairs.clear();
                nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.farmVillage, "1"));
                DB.villages.updateValue(Integer.parseInt(tmp[0]), nameValuePairs);
            }
        }
    }

    private static int unit;
    private static void resolveUnitInfoLine(String strLine) {
        List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
        if(strLine.matches("^\\s*<spear>\\s*$")) {
            unit = 0;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spear_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<sword>\\s*$")) {
            unit = 1;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.sword_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<axe>\\s*$")) {
            unit = 2;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.axe_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<archer>\\s*$")) {
            unit = 3;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.archer_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<spy>\\s*$")) {
            unit = 4;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.spy_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<light>\\s*$")) {
            unit = 5;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.light_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<marcher>\\s*$")) {
            unit = 6;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.marcher_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<heavy>\\s*$")) {
            unit = 7;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.heavy_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<ram>\\s*$")) {
            unit = 8;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.ram_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<catapult>\\s*$")) {
            unit = 9;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.catapult_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<knight>\\s*$")) {
            unit = 10;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.knight_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<snob>\\s*$")) {
            unit = 11;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.snob_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<militia>\\s*$")) {
            unit = 12;
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<speed>[0-9.]*</speed>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_speed, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<attack>[0-9.]*</attack>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_attack, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense>[0-9.]*</defense>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_defense, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_cavalry>[0-9.]*</defense_cavalry>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_defense_cavalry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<defense_archer>[0-9.]*</defense_archer>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_defense_archer, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<carry>[0-9.]*</carry>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_carry, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && unit == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitInfo.militia_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.unitInfo.tableName, nameValuePairs, null);
        }
    }

    private static int building;
    private static void resolveBuildingInfoLine(String strLine) {
        List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
        if(strLine.matches("^\\s*<main>\\s*$")) {
            building = 0;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 0) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.main_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<barracks>\\s*$")) {
            building = 1;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 1) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.barracks_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stable>\\s*$")) {
            building = 2;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 2) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stable_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<garage>\\s*$")) {
            building = 3;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 3) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.garage_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<church>\\s*$")) {
            building = 4;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 4) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<church_f>\\s*$")) {
            building = 5;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 5) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.church_f_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<snob>\\s*$")) {
            building = 6;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 6) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.snob_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<smith>\\s*$")) {
            building = 7;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 7) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.smith_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<place>\\s*$")) {
            building = 8;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 8) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.place_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<statue>\\s*$")) {
            building = 9;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 9) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.statue_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<market>\\s*$")) {
            building = 10;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 10) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.market_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>\\s*$")) {
            building = 11;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 11) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wood_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>\\s*$")) {
            building = 12;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 12) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.stone_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>\\s*$")) {
            building = 13;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 13) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.iron_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<farm>\\s*$")) {
            building = 14;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 14) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.farm_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<storage>\\s*$")) {
            building = 15;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 15) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.storage_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<hide>\\s*$")) {
            building = 16;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 16) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.hide_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wall>\\s*$")) {
            building = 17;
        } else if(strLine.matches("^\\s*<max_level>[0-9.]*</max_level>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_max_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<min_level>[0-9.]*</min_level>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_min_level, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood>[0-9.]*</wood>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_wood, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone>[0-9.]*</stone>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_stone, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron>[0-9.]*</iron>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_iron, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop>[0-9.]*</pop>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_pop, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<wood_factor>[0-9.]*</wood_factor>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_wood_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<stone_factor>[0-9.]*</stone_factor>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_stone_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<iron_factor>[0-9.]*</iron_factor>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_iron_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<pop_factor>[0-9.]*</pop_factor>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_pop_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time>[0-9.]*</build_time>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_build_time, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        } else if(strLine.matches("^\\s*<build_time_factor>[0-9.]*</build_time_factor>\\s*$") && building == 17) {
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingInfo.wall_build_time_factor, strLine.substring(strLine.indexOf(">") + 1, strLine.lastIndexOf("<"))));
            DatabaseManager.update(DB.buildingInfo.tableName, nameValuePairs, null);
        }
    }

    private static void calculateDistances() {
        ResultSet my = null;
        try {
            String [] selectString = {DB.villages.x, DB.villages.y, DB.villages.id, DB.villages.name};
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(String.valueOf(DB.villages.player), String.valueOf(TWClient.userID)));
            my = DatabaseManager.select(DB.villages.tableName, selectString, nameValuePairs);
            if(my.first()) {
                do {
                    //LoggingManager.stopDebugging();
                    DatabaseManager.customQuery("ALTER TABLE " + DB.villages.tableName + " ADD village_" + my.getString(DB.villages.id) + " REAL DEFAULT -1;");
                    //LoggingManager.startDebugging();

                    DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET " + "village_" + my.getString(DB.villages.id)
                             + " = POWER(POWER(" + DB.villages.x + " - " + my.getInt(DB.villages.x) + ", 2) + POWER(" + DB.villages.y + " - " + my.getInt(DB.villages.y) + ", 2) , 0.5)");
                } while (my.next());
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getClass().getSimpleName() + "->" + e.getMessage(), "calculateDistances()", "StartUpManager");
        } finally {
            try {
                if (my != null)
                    my.close();
            } catch (SQLException e) {
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not close ResultSet.", "calculateDistances()", "StartUpManager");
            }
        }
    }
}