package dev.topp.outdated;

import dev.topp.LoggingManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TWHelper {

    public enum BuildingType {
        MAIN,
        BARRACKS,
        STABLE,
        GARAGE,
        CHURCH,
        CHURCH_F,
        SNOB,
        SMITH,
        PLACE,
        STATUE,
        MARKET,
        WOOD,
        STONE,
        IRON,
        FARM,
        STORAGE,
        HIDE,
        WALL
    }

    public enum UnitType {
        SPEAR,
        SWORD,
        AXE,
        ARCHER,
        SPY,
        LIGHT,
        MARCHER,
        HEAVY,
        RAM,
        CATAPULT,
        SNOB,
        KNIGHT
    }

    public enum ScreenType {
        OVERVIEW_VILLAGES,
        OVERVIEW,
        TRAIN,
        INFO_VILLAGE,
        INFO_COMMAND,
        CONFIRM_ATTACK,
        REPORT_ATTACK,
        REPORT,
        MAIN,
        BARRACKS,
        STABLE,
        GARAGE,
        CHURCH,
        CHURCH_F,
        SNOB,
        SMITH,
        PLACE,
        STATUE,
        MARKET,
        WOOD,
        STONE,
        IRON,
        FARM,
        STORAGE,
        HIDE,
        WALL
    }

    public static String getNameFromType(BuildingType buildingType) {
        if (buildingType != null) {
            switch (buildingType) {
                case MAIN:
                    return "Main";
                case BARRACKS:
                    return "Barracks";
                case STABLE:
                    return "Stable";
                case GARAGE:
                    return "Garage";
                case CHURCH:
                    return "Church";
                case CHURCH_F:
                    return "First Church";
                case SNOB:
                    return "Snob";
                case SMITH:
                    return "Smith";
                case PLACE:
                    return "Place";
                case STATUE:
                    return "Statue";
                case MARKET:
                    return "Market";
                case WOOD:
                    return "Wood";
                case STONE:
                    return "Stone";
                case IRON:
                    return "Iron";
                case FARM:
                    return "Farm";
                case STORAGE:
                    return "Storage";
                case HIDE:
                    return "Hide";
                case WALL:
                    return "Wall";
                default:
                    LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown Type: " + buildingType.name(), "getNameFromType(BuildingType)", "TWHelper");
                    return "";
            }
        } else
            return "";
    }

    public static String getTWUrlFromType(BuildingType buildingType) {
        if(buildingType != null) {
            switch (buildingType) {
                case MAIN:
                    return "main";
                case BARRACKS:
                    return "barracks";
                case STABLE:
                    return "stable";
                case GARAGE:
                    return "garage";
                case CHURCH:
                    return "church";
                case CHURCH_F:
                    return "church_f";
                case SNOB:
                    return "snob";
                case SMITH:
                    return "smith";
                case PLACE:
                    return "place";
                case STATUE:
                    return "statue";
                case MARKET:
                    return "market";
                case WOOD:
                    return "wood";
                case STONE:
                    return "stone";
                case IRON:
                    return "iron";
                case FARM:
                    return "farm";
                case STORAGE:
                    return "storage";
                case HIDE:
                    return "hide";
                case WALL:
                    return "wall";
                default:
                    LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown Type: " + buildingType.name(), "getTWUrlFromType(BuildingType)", "TWHelper");
                    return "";
            }
        } else
            return "";
    }

    public static String getNameFromType(UnitType unitType) {
        if(unitType != null) {
            switch (unitType) {
                case SPEAR:
                    return "Spear";
                case SWORD:
                    return "Sword";
                case AXE:
                    return "Axe";
                case ARCHER:
                    return "Archer";
                case SPY:
                    return "Spy";
                case LIGHT:
                    return "Light";
                case MARCHER:
                    return "Marcher";
                case HEAVY:
                    return "Heavy";
                case RAM:
                    return "Ram";
                case CATAPULT:
                    return "Catapult";
                case SNOB:
                    return "Snob";
                case KNIGHT:
                    return "Knight";
                default:
                    LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown Type: " + unitType.name(), "getNameFromType(UnitType)", "TWHelper");
                    return "";
            }
        } else
            return "";
    }

    public static String getTWUrlFromType(UnitType unitType) {
        if(unitType != null) {
            switch (unitType) {
                case SPEAR:
                    return "spear";
                case SWORD:
                    return "sword";
                case AXE:
                    return "axe";
                case ARCHER:
                    return "archer";
                case SPY:
                    return "spy";
                case LIGHT:
                    return "light";
                case MARCHER:
                    return "marcher";
                case HEAVY:
                    return "heavy";
                case RAM:
                    return "ram";
                case CATAPULT:
                    return "catapult";
                case SNOB:
                    return "snob";
                case KNIGHT:
                    return "knight";
                default:
                    LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown Type: " + unitType.name(), "getTWUrlFromType(UnitType)", "TWHelper");
                    return "";
            }
        } else
            return "";
    }

    public static String getNameFromType(ScreenType screenType) {
        if(screenType != null) {
            switch (screenType) {
                case OVERVIEW_VILLAGES:
                    return "Overview Villages";
                case OVERVIEW:
                    return "Overview";
                case CONFIRM_ATTACK:
                    return "Confirm Attack";
                case TRAIN:
                    return "Training";
                case INFO_VILLAGE:
                    return "Village Info";
                case INFO_COMMAND:
                    return "Command Info";
                case REPORT_ATTACK:
                    return "Attack report";
                case REPORT:
                    return "Report";
                case MAIN:
                    return "Main";
                case BARRACKS:
                    return "Barracks";
                case STABLE:
                    return "Stable";
                case GARAGE:
                    return "Garage";
                case CHURCH:
                    return "Church";
                case CHURCH_F:
                    return "First Church";
                case SNOB:
                    return "Snob";
                case SMITH:
                    return "Smith";
                case PLACE:
                    return "Place";
                case STATUE:
                    return "Statue";
                case MARKET:
                    return "Market";
                case WOOD:
                    return "Wood";
                case STONE:
                    return "Stone";
                case IRON:
                    return "Iron";
                case FARM:
                    return "Farm";
                case STORAGE:
                    return "Storage";
                case HIDE:
                    return "Hide";
                case WALL:
                    return "Wall";
                default:
                    LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown Type: " + screenType.name(), "getNameFromType(ScreenType)", "TWHelper");
                    return "";
            }
        } else
            return "";
    }

    public static String getTWUrlFromType(ScreenType screenType) {
        if(screenType != null) {
            switch (screenType) {
                case OVERVIEW_VILLAGES:
                    return "overview_villages";
                case OVERVIEW:
                    return "overview";
                case CONFIRM_ATTACK:
                    return "confirm_attack";
                case TRAIN:
                    return "train";
                case INFO_VILLAGE:
                    return "info_village";
                case INFO_COMMAND:
                    return "info_command";
                case REPORT_ATTACK:
                    return "report_attack";
                case REPORT:
                    return "report";
                case MAIN:
                    return "main";
                case BARRACKS:
                    return "barracks";
                case STABLE:
                    return "stable";
                case GARAGE:
                    return "garage";
                case CHURCH:
                    return "church";
                case CHURCH_F:
                    return "church_f";
                case SNOB:
                    return "snob";
                case SMITH:
                    return "smith";
                case PLACE:
                    return "place";
                case STATUE:
                    return "statue";
                case MARKET:
                    return "market";
                case WOOD:
                    return "wood";
                case STONE:
                    return "stone";
                case IRON:
                    return "iron";
                case FARM:
                    return "farm";
                case STORAGE:
                    return "storage";
                case HIDE:
                    return "hide";
                case WALL:
                    return "wall";
                default:
                    LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown Type: " + screenType.name(), "getTWNameFromScreenType(ScreenType)", "TWHelper");
                    return "";
            }
        } else
            return "";
    }

    public static BuildingType getBuildingTypeFromNumber(int number) {
        switch (number) {
            case 1:
                return BuildingType.MAIN;
            case 2:
                return BuildingType.BARRACKS;
            case 3:
                return BuildingType.STABLE;
            case 4:
                return BuildingType.GARAGE;
            case 5:
                return BuildingType.CHURCH;
            case 6:
                return BuildingType.CHURCH_F;
            case 7:
                return BuildingType.SNOB;
            case 8:
                return BuildingType.SMITH;
            case 9:
                return BuildingType.PLACE;
            case 10:
                return BuildingType.STATUE;
            case 11:
                return BuildingType.MARKET;
            case 12:
                return BuildingType.WOOD;
            case 13:
                return BuildingType.STONE;
            case 14:
                return BuildingType.IRON;
            case 15:
                return BuildingType.FARM;
            case 16:
                return BuildingType.STORAGE;
            case 17:
                return BuildingType.HIDE;
            case 18:
                return BuildingType.WALL;
            default:
                LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown Number: " + number, "getBuildingTypeFromNumber(int)", "TWHelper");
        }
        return null;
    }

    /**
     * Converts a Tribal Wars time into a long time.
     *
     * @param time Tribal Wars time. Allowed formats:
     *             <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;heute um 23:18:56 Uhr
     *             <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;morgen um 01:37:30 Uhr
     *             <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;am 12.12. um 01:37:30 Uhr
     * @return Time in long format.
     */
    public static long convertTWTimeToLong(String time) {
        try {
            String time_tmp = time;
            Calendar calendar = GregorianCalendar.getInstance();

            int year, month, day, hour, minute, second;

            if(time_tmp.contains("heute")) { // heute um 23:18:56 Uhr
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                if (!time_tmp.endsWith("Uhr")) time_tmp += " Uhr";
            } else if(time_tmp.contains("morgen")) { // morgen um 01:37:30 Uhr
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                if (!time_tmp.endsWith("Uhr")) time_tmp += " Uhr";
            } else if(time_tmp.contains("am")) { // am 12.12. um 01:37:30 Uhr
                time_tmp = time_tmp.substring(time_tmp.indexOf("am") + 3);
                day = Integer.parseInt(time_tmp.substring(0, time_tmp.indexOf(".")));
                time_tmp = time_tmp.substring(time_tmp.indexOf(".") + 1);
                //month starts with 0 in calendar
                month = Integer.parseInt(time_tmp.substring(0, time_tmp.indexOf("."))) - 1;
                year = calendar.get(Calendar.YEAR);
                if(calendar.get(Calendar.MONTH) < month)
                    year++;
                if (!time_tmp.endsWith("Uhr")) time_tmp += " Uhr";
            } else { // 30.03.14 15:11:13
                day = Integer.parseInt(time_tmp.substring(0, time_tmp.indexOf(".")));
                time_tmp = time_tmp.substring(time_tmp.indexOf(".") + 1);
                //month starts with 0 in calendar
                month = Integer.parseInt(time_tmp.substring(0, time_tmp.indexOf("."))) - 1;
                time_tmp = time_tmp.substring(time_tmp.indexOf(".") + 1);
                year = 2000 + Integer.parseInt(time_tmp.substring(0, time_tmp.indexOf(" ")));
                time_tmp = time_tmp.substring(time_tmp.indexOf(" ") + 1);
                time_tmp = "um " + time_tmp + " Uhr";
            }
            time_tmp = time_tmp.substring(time_tmp.indexOf("um") + 3, time_tmp.indexOf("Uhr") - 1);

            hour = Integer.parseInt(time_tmp.substring(0, time_tmp.indexOf(":")));
            time_tmp = time_tmp.substring(time_tmp.indexOf(":") + 1);
            minute = Integer.parseInt(time_tmp.substring(0, time_tmp.indexOf(":")));
            time_tmp = time_tmp.substring(time_tmp.indexOf(":") + 1);
            second = Integer.parseInt(time_tmp);

            calendar.set(year, Calendar.JANUARY, day, hour, minute, second);
            calendar.set(Calendar.MONTH, month);

            return calendar.getTimeInMillis();
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage() + "->" + time, "convertTWTimeToLong(String)", "TWHelper");
            return 0;
        }
    }

    public static long calculateTimeForDistance(int sourceVillageId, int destX, int destY, float unitSpeed) {
        try {
            String[] selectString = {DB.villages.x, DB.villages.y};
            List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
            nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.id, String.valueOf(sourceVillageId)));
            ResultSet resultSet = DatabaseManager.select(DB.villages.tableName, selectString, nameValuePairs);
            if (resultSet.first()) {
                double distance = Math.sqrt(Math.pow(resultSet.getInt(DB.villages.x) - destX, 2)
                                            + Math.pow(resultSet.getInt(DB.villages.y) - destY, 2));
                return (long)(distance * unitSpeed * 60 * Float.parseFloat(DB.worldConfigs.getValue(DB.worldConfigs.speed)) * Float.parseFloat(DB.worldConfigs.getValue(DB.worldConfigs.unit_speed)) * 1000);
            }
        } catch (SQLException e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "calculateTimeForDistance(int, int, int, float)", "TWHelper");
        }

        LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Source village is not in db.", "calculateTimeForDistance(int, int, int, float)", "TWHelper");

        return -1;
    }

    public static int GetBuildingBuildtimeInMillis(BuildingType building, int newLevel, int villageID) {
        double baseValue = GetBuildingConfigInfo(building, "build_time");
        double baseValueFactor = GetBuildingConfigInfo(building, "build_time_factor");
        double factor = 1;

        if (DB.worldConfigs.isNewBuildTimeFormulaActive())
        {
            if (newLevel == 1) factor = 9.0225263157894736842105263157895;
            else if (newLevel == 2) factor = 10.827052631578947368421052631579;
            else if (newLevel == 3) factor = 3.0401231527093596059113300492611;
            else if (newLevel == 4) factor = 1.9854423592493297587131367292225;
            else if (newLevel == 5) factor = 1.6041245487364620938628158844765;
            else if (newLevel == 6) factor = 1.4115486432825943084050297816016;
            else if (newLevel == 7) factor = 1.2965653495440729483282674772036;
            else if (newLevel == 8) factor = 1.2202224870878029400079459674215;
            else if (newLevel == 9) factor = 1.1659474849731097753875355900032;
            else if (newLevel == 10) factor = 1.1253613231552162849872773536896;
            else if (newLevel == 11) factor = 1.0938169826875515251442704039571;
            else if (newLevel == 12) factor = 1.0687430776976002685014264138278;
            else if (newLevel == 13) factor = 1.048336076817558299039780521262;
            else if (newLevel == 14) factor = 1.0313596491228070175438596491228;
            else if (newLevel == 15) factor = 1.0169118462391424875254111994086;
            else if (newLevel == 16) factor = 1.0046420692278432864206922784329;
            else if (newLevel == 17) factor = 0.99405469828126960230836783339606;
            else if (newLevel == 18) factor = 0.98475842783905546061830045052043;
            else if (newLevel == 19) factor = 0.97659091881713527624427611588993;
            else if (newLevel == 20) factor = 0.96931082085589890623340766698524;
            else if (newLevel == 21) factor = 0.96281277468502783474948725461471;
            else if (newLevel == 22) factor = 0.95699453949763378230797233345468;
            else if (newLevel == 23) factor = 0.95172063555913113435237329042639;
            else if (newLevel == 24) factor = 0.94692775906346929926958609878931;
            else if (newLevel == 25) factor = 0.94255543413608509814228210200158;
            else if (newLevel == 26) factor = 0.93855890083906291250100435027146;
            else if (newLevel == 27) factor = 0.93487647322236725516163762302657;
            else if (newLevel == 28) factor = 0.93148472790272690594666434612007;
            else if (newLevel == 29) factor = 0.92834148040104597837084926610688;
            else if (newLevel == 30) factor = 0.92543006380412297985448948514603;
        }

        return (int)(baseValue * Math.pow(baseValueFactor, (newLevel - 1)) * Math.pow(0.952381, Integer.parseInt(DB.villages.getValue(DB.villages.main_level, villageID))) / factor * 1000);
    }

    public static int GetUnitBuildtimeInMillis(UnitType unit, int villageID) {
        double baseValue = GetUnitConfigInfo(unit, "build_time");
        BuildingType prodBuilding = BuildingType.BARRACKS;
        int prodLevel = 1;

        if(unit == UnitType.SPEAR || unit == UnitType.SWORD || unit == UnitType.AXE || unit == UnitType.ARCHER) {
            prodBuilding = BuildingType.BARRACKS;
            prodLevel = Integer.parseInt(DB.villages.getValue(DB.villages.barracks_level, villageID));
        } else if(unit == UnitType.SPY|| unit == UnitType.LIGHT || unit == UnitType.MARCHER || unit == UnitType.HEAVY) {
            prodBuilding = BuildingType.STABLE;
            prodLevel = Integer.parseInt(DB.villages.getValue(DB.villages.stable_level, villageID));
        } else if(unit == UnitType.RAM || unit == UnitType.CATAPULT) {
            prodBuilding = BuildingType.GARAGE;
            prodLevel = Integer.parseInt(DB.villages.getValue(DB.villages.garage_level, villageID));
        }

        return (int)(baseValue * Float.parseFloat(DB.worldConfigs.getValue(DB.worldConfigs.speed)) * GetBuildingValue(prodBuilding, prodLevel, "effect") * 1000);
    }

    private static float GetBuildingConfigInfo(BuildingType building, String property) {
        return DB.buildingInfo.getValue(getTWUrlFromType(building) + "_" + property);
    }

    public static float GetUnitConfigInfo(UnitType unit, String property) {
        return DB.unitInfo.getValue(getTWUrlFromType(unit) + "_" + property);
    }

    /**
     *
     * @param building
     * @param level
     * @param value wood, stone, iron, pop, effect
     * @return
     */
    public static double GetBuildingValue(BuildingType building, int level, String value) {
        if (value == "wood" || value == "stone" || value == "iron") {
            return Math.round(GetBuildingConfigInfo(building, value) * Math.pow(GetBuildingConfigInfo(building, value + "_factor"), level - 1));
        } else if (value == "pop") {
            if (level == 1)
                return Math.round(GetBuildingConfigInfo(building, value) * Math.pow(GetBuildingConfigInfo(building, value + "_factor"), level - 1));
            else
                return Math.round(GetBuildingConfigInfo(building, value) * Math.pow(GetBuildingConfigInfo(building, value + "_factor"), level - 1)) - Math.round(GetBuildingConfigInfo(building, value) * Math.pow(GetBuildingConfigInfo(building, value + "_factor"), level - 2));
        }
        else if (building == BuildingType.MAIN && value == "effect")
            return Math.round(Math.pow(0.952381, level));
        else if ((building == BuildingType.BARRACKS || building == BuildingType.GARAGE || building == BuildingType.STABLE) && value == "effect")
        {
            if (level == 0) return 1;
            else if (level == 1) return 0.63;
            else if (level == 2) return 0.59;
            else if (level == 3) return 0.56;
            else if (level == 4) return 0.53;
            else if (level == 5) return 0.50;
            else if (level == 6) return 0.47;
            else if (level == 7) return 0.44;
            else if (level == 8) return 0.42;
            else if (level == 9) return 0.39;
            else if (level == 10) return 0.37;
            else if (level == 11) return 0.35;
            else if (level == 12) return 0.33;
            else if (level == 13) return 0.31;
            else if (level == 14) return 0.29;
            else if (level == 15) return 0.28;
            else if (level == 16) return 0.26;
            else if (level == 17) return 0.25;
            else if (level == 18) return 0.23;
            else if (level == 19) return 0.22;
            else if (level == 20) return 0.21;
            else if (level == 21) return 0.20;
            else if (level == 22) return 0.19;
            else if (level == 23) return 0.17;
            else if (level == 24) return 0.16;
            else if (level == 25) return 0.16;
        }
        else if ((building == BuildingType.IRON || building == BuildingType.STONE || building == BuildingType.WOOD) && value == "effect")
            return Math.round(Float.parseFloat(DB.worldConfigs.getValue(DB.worldConfigs.speed)) * 30 * Math.pow(1.163118, level - 1));
        else if (building == BuildingType.FARM && value == "effect")
        {
            if (level == 1) return 240;
            else if (level == 2) return 281;
            else if (level == 3) return 329;
            else if (level == 4) return 386;
            else if (level == 5) return 452;
            else if (level == 6) return 530;
            else if (level == 7) return 622;
            else if (level == 8) return 729;
            else if (level == 9) return 854;
            else if (level == 10) return 1002;
            else if (level == 11) return 1174;
            else if (level == 12) return 1376;
            else if (level == 13) return 1613;
            else if (level == 14) return 1891;
            else if (level == 15) return 2216;
            else if (level == 16) return 2598;
            else if (level == 17) return 3045;
            else if (level == 18) return 3569;
            else if (level == 19) return 4183;
            else if (level == 20) return 4904;
            else if (level == 21) return 5748;
            else if (level == 22) return 6737;
            else if (level == 23) return 7896;
            else if (level == 24) return 9255;
            else if (level == 25) return 10848;
            else if (level == 26) return 12715;
            else if (level == 27) return 14904;
            else if (level == 28) return 17469;
            else if (level == 29) return 20476;
            else if (level == 30) return 24000;
        }
        else if (building == BuildingType.STORAGE && value == "effect")
        {
            if (level == 1) return 1000;
            else if (level == 2) return 1229;
            else if (level == 3) return 1512;
            else if (level == 4) return 1859;
            else if (level == 5) return 2285;
            else if (level == 6) return 2810;
            else if (level == 7) return 3454;
            else if (level == 8) return 4247;
            else if (level == 9) return 5222;
            else if (level == 10) return 6420;
            else if (level == 11) return 7893;
            else if (level == 12) return 9705;
            else if (level == 13) return 11932;
            else if (level == 14) return 14670;
            else if (level == 15) return 18037;
            else if (level == 16) return 22177;
            else if (level == 17) return 27266;
            else if (level == 18) return 33523;
            else if (level == 19) return 41217;
            else if (level == 20) return 50675;
            else if (level == 21) return 62305;
            else if (level == 22) return 76604;
            else if (level == 23) return 94184;
            else if (level == 24) return 115798;
            else if (level == 25) return 142372;
            else if (level == 26) return 175047;
            else if (level == 27) return 215219;
            else if (level == 28) return 264611;
            else if (level == 29) return 325337;
            else if (level == 30) return 400000;
        }
        else if (building == BuildingType.HIDE && value == "effect")
            return Math.round(150 * Math.pow(1.3335, level - 1));
        else if (building == BuildingType.WALL && value == "effect")
            return Math.round(Math.pow(1.03699, level) * 100);
        return 0;
    }

    public static boolean GetTimeDifference(String response) {
        try
        {
            response = response.substring(response.indexOf("id=\"serverTime\">"));
            response = response.substring(response.indexOf(">") + 1);
            int hour = Integer.parseInt(response.substring(0, response.indexOf(":")));
            response = response.substring(response.indexOf(":") + 1);
            int minute = Integer.parseInt(response.substring(0, response.indexOf(":")));
            response = response.substring(response.indexOf(":") + 1);
            int second = Integer.parseInt(response.substring(0, response.indexOf("<")));

            response = response.substring(response.indexOf("id=\"serverDate\">"));
            response = response.substring(response.indexOf(">") + 1);
            int day = Integer.parseInt(response.substring(0, response.indexOf("/")));
            response = response.substring(response.indexOf("/") + 1);
            int month = Integer.parseInt(response.substring(0, response.indexOf("/")));
            response = response.substring(response.indexOf("/") + 1);
            int year = Integer.parseInt(response.substring(0, response.indexOf("<")));

            Calendar calendar = GregorianCalendar.getInstance();
            calendar.set(year, Calendar.JANUARY, day, hour, minute, second);
            calendar.set(Calendar.MONTH, month - 1);

            TWClient.TimeDifference = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

            return true;
        }
        catch (Exception e)
        {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetTimeDifference(String)", "TWHelper");
            return false;
        }
    }

    public static boolean GetUserID(String response) {
        try
        {
            response = response.substring(response.indexOf("//<![CDATA["));
            response = response.substring(response.indexOf("{\"player\":"));
            response = response.substring(response.indexOf(",\"id\":\""));
            TWClient.userID = Integer.parseInt(response.substring(response.indexOf(":\"") + 2, response.indexOf("\",\"")));

            return true;
        }
        catch (Exception e)
        {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetUserID(String)", "TWHelper");
            return false;
        }
    }

    public static void GetSiteInformation(String response, ScreenType actScreen) {
        int actVillageID = GetGeneralInformation(response);

        switch (actScreen) {
            case OVERVIEW_VILLAGES:
                GetVillageList(response);
                break;
            case OVERVIEW:
                break;
            case TRAIN:
                GetRecruitQueue(response, actVillageID);
                GetUnits(response, actVillageID);
                break;
            case MAIN:
                GetBuildQueue(response, actVillageID);
                break;
            case BARRACKS:
                break;
            case STABLE:
                break;
            case GARAGE:
                break;
            case CHURCH:
                break;
            case CHURCH_F:
                break;
            case SNOB:
                break;
            case SMITH:
                break;
            case PLACE:
                break;
            case STATUE:
                GetKnight(response, actVillageID);
                break;
            case MARKET:
                break;
            case WOOD:
                break;
            case STONE:
                break;
            case IRON:
                break;
            case FARM:
                break;
            case STORAGE:
                break;
            case HIDE:
                break;
            case WALL:
                break;
            default:
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Unknown Type: " + actScreen.name(), "GetSiteInformation(String, ScreenType, int)", "TWHelper");
                break;
        }
    }

    private static int GetGeneralInformation(String response) {
        try {
            response = response.substring(response.indexOf("\"village\"") + 10);
            int actVillageID;
            if (response.indexOf("\":\"") < response.indexOf(",")) {
                response = response.substring(response.indexOf("\"id\"") + 6);
                actVillageID = Integer.parseInt(response.substring(0, response.indexOf("\"")));
            } else {
                response = response.substring(response.indexOf("\"id\"") + 5);
                actVillageID = Integer.parseInt(response.substring(0, response.indexOf(",")));
            }

            response = response.substring(response.indexOf("\"name\"") + 8);
            String actVillageName = response.substring(0, response.indexOf("\""));

            response = response.substring(response.indexOf("\"wood_float\"") + 13);
            float r_wood = Float.parseFloat(response.substring(0, response.indexOf(",")));
            response = response.substring(response.indexOf(":") + 1);
            float r_stone = Float.parseFloat(response.substring(0, response.indexOf(",")));
            response = response.substring(response.indexOf(":") + 1);
            float r_iron = Float.parseFloat(response.substring(0, response.indexOf(",")));
            response = response.substring(response.indexOf("\"pop\"") + 7);
            int pop = Integer.parseInt(response.substring(0, response.indexOf("\"")));

            response = response.substring(response.indexOf("\"bonus_id\"") + 11);
            int bonus = 0;
            if (!response.substring(0, response.indexOf(",")).equals("null"))
                bonus = Integer.parseInt(response.substring(0, response.indexOf(",")));
            float bonus_wood = 1, bonus_stone = 1, bonus_iron = 1, bonus_farm = 1, bonus_barracks = 1, bonus_stable = 1, bonus_garage = 1, bonus_resources = 1, bonus_storage = 1;
            switch (bonus) {
                case 0:
                    break;
                case 1:
                    bonus_wood = 2;
                    break;
                case 2:
                    bonus_stone = 2;
                    break;
                case 3:
                    bonus_iron = 2;
                    break;
                case 4:
                    bonus_farm = 1.1f;
                    break;
                case 5:
                    bonus_barracks = 1.33f;
                    break;
                case 6:
                    bonus_stable = 1.33f;
                    break;
                case 7:
                    bonus_garage = 1.5f;
                    break;
                case 8:
                    bonus_resources = 1.3f;
                    break;
                case 9:
                    bonus_storage = 1.5f;
                    break;
            }
            response = response.substring(response.indexOf("\"wood\"") + 7);
            float wood_bonus_factor = Float.parseFloat(response.substring(0, response.indexOf(",")));
            response = response.substring(response.indexOf(":") + 1);
            float stone_bonus_factor = Float.parseFloat(response.substring(0, response.indexOf(",")));
            response = response.substring(response.indexOf(":") + 1);
            float iron_bonus_factor = Float.parseFloat(response.substring(0, response.indexOf("}")));

            response = response.substring(response.indexOf("\"buildings\"") + 13);
            response = response.substring(response.indexOf("\"main\"") + 8);
            byte b_main = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"farm\"") + 8);
            byte b_farm = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"storage\"") + 11);
            byte b_storage = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"place\"") + 9);
            byte b_place = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"barracks\"") + 12);
            byte b_barracks = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            byte b_church = 0, b_church_f = 0;
            if(DB.worldConfigs.isChurchActive()) {
                response = response.substring(response.indexOf("\"church\"") + 10);
                b_church = Byte.parseByte(response.substring(0, response.indexOf("\"")));
                response = response.substring(response.indexOf("\"church_f\"") + 12);
                b_church_f = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            }
            response = response.substring(response.indexOf("\"smith\"") + 9);
            byte b_smith = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"wood\"") + 8);
            byte b_wood = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"stone\"") + 9);
            byte b_stone = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"iron\"") + 8);
            byte b_iron = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"market\"") + 10);
            byte b_market = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"stable\"") + 10);
            byte b_stable = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"wall\"") + 8);
            byte b_wall = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"garage\"") + 10);
            byte b_garage = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"hide\"") + 8);
            byte b_hide = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            response = response.substring(response.indexOf("\"snob\"") + 8);
            byte b_snob = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            byte b_statue = 0;
            if(DB.worldConfigs.isKnightActive()) {
                response = response.substring(response.indexOf("\"statue\"") + 10);
                b_statue = Byte.parseByte(response.substring(0, response.indexOf("\"")));
            }

            long ressupdate = System.currentTimeMillis();

            DatabaseManager.customQuery("UPDATE " + DB.villages.tableName
                    + " SET " + DB.villages.name + " = '" + actVillageName + "' "
                    + ", " + DB.villages.res_wood + " = '" + r_wood + "' "
                    + ", " + DB.villages.res_stone + " = '" + r_stone + "' "
                    + ", " + DB.villages.res_iron + " = '" + r_iron + "' "
                    + ", " + DB.villages.res_pop + " = '" + pop + "' "
                    + ", " + DB.villages.bonus_wood + " = '" + bonus_wood + "' "
                    + ", " + DB.villages.bonus_stone + " = '" + bonus_stone + "' "
                    + ", " + DB.villages.bonus_iron + " = '" + bonus_iron + "' "
                    + ", " + DB.villages.bonus_farm + " = '" + bonus_farm + "' "
                    + ", " + DB.villages.bonus_barracks + " = '" + bonus_barracks + "' "
                    + ", " + DB.villages.bonus_stable + " = '" + bonus_stable + "' "
                    + ", " + DB.villages.bonus_garage + " = '" + bonus_garage + "' "
                    + ", " + DB.villages.bonus_resources + " = '" + bonus_resources + "' "
                    + ", " + DB.villages.bonus_storage + " = '" + bonus_storage + "' "
                    + ", " + DB.villages.res_wood_bonus_factor + " = '" + wood_bonus_factor + "' "
                    + ", " + DB.villages.res_stone_bonus_factor + " = '" + stone_bonus_factor + "' "
                    + ", " + DB.villages.res_iron_bonus_factor + " = '" + iron_bonus_factor + "' "
                    + ", " + DB.villages.main_level + " = '" + b_main + "' "
                    + ", " + DB.villages.farm_level + " = '" + b_farm + "' "
                    + ", " + DB.villages.storage_level + " = '" + b_storage + "' "
                    + ", " + DB.villages.place_level + " = '" + b_place + "' "
                    + ", " + DB.villages.barracks_level + " = '" + b_barracks + "' "
                    + ", " + DB.villages.church_level + " = '" + b_church + "' "
                    + ", " + DB.villages.church_f_level + " = '" + b_church_f + "' "
                    + ", " + DB.villages.smith_level + " = '" + b_smith + "' "
                    + ", " + DB.villages.wood_level + " = '" + b_wood + "' "
                    + ", " + DB.villages.stone_level + " = '" + b_stone + "' "
                    + ", " + DB.villages.iron_level + " = '" + b_iron + "' "
                    + ", " + DB.villages.market_level + " = '" + b_market + "' "
                    + ", " + DB.villages.stable_level + " = '" + b_stable + "' "
                    + ", " + DB.villages.wall_level + " = '" + b_wall + "' "
                    + ", " + DB.villages.garage_level + " = '" + b_garage + "' "
                    + ", " + DB.villages.hide_level + " = '" + b_hide + "' "
                    + ", " + DB.villages.snob_level + " = '" + b_snob + "' "
                    + ", " + DB.villages.statue_level + " = '" + b_statue + "' "
                    + ", " + DB.villages.res_updateDate + " = '" + ressupdate + "' "
                    + " WHERE " + DB.villages.id + " = " + actVillageID + ";");
            return actVillageID;
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetGeneralInformation(String)", "TWHelper");
            return 0;
        }
    }

    private static boolean GetVillageList(String response) {
        try {
            response = response.substring(response.indexOf("production_table"));
            while (response.contains("<span class=\"quickedit-vn\" data-id=\"")) {
                response = response.substring(response.indexOf("<span class=\"quickedit-vn"));
                response = response.substring(response.indexOf("href=\"/game.php?village=") + 24);
                String id = response.substring(0, response.indexOf("&amp;"));
                response = response.substring(response.indexOf("<span class=\"quickedit-label\" data-text=\"") + 41);
                String name = response.substring(0, response.indexOf("\">"));
                //response = response.substring(response.indexOf("\">") + 2);
                //response = response.substring(response.substring(0, response.indexOf("</span>")).lastIndexOf("("));
                //int x = Integer.parseInt(response.substring(0, response.indexOf("|")));
                //int y = Integer.parseInt(response.substring(response.indexOf("|") + 1, response.indexOf(")")));
                response = response.substring(response.indexOf("</span>") + 7);
                response = response.substring(response.indexOf("</span>") + 7);
                response = response.substring(response.indexOf("</span>") + 7);
                response = response.substring(response.indexOf("<td>") + 4);
                String points = response.substring(0, response.indexOf("</td>")).replace("<span class=\"grey\">.</span>", "");
                response = response.substring(response.indexOf("res wood") + 10);
                String res_wood = response.substring(0, response.indexOf("</span> ")).replace("<span class=\"grey\">.</span>", "");
                response = response.substring(response.indexOf("res stone") + 11);
                String res_stone = response.substring(0, response.indexOf("</span> ")).replace("<span class=\"grey\">.</span>", "");
                response = response.substring(response.indexOf("res iron") + 10);
                String res_iron = response.substring(0, response.indexOf("</span> ")).replace("<span class=\"grey\">.</span>", "");
                response = response.substring(response.indexOf("<td>") + 4);
                response = response.substring(response.indexOf("<td>") + 4);
                String res_pop = response.substring(0, response.indexOf("/"));

                if(TWClient.userID != 0) {
                    List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.name, name));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.player, String.valueOf(TWClient.userID)));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.points, points));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.res_wood, res_wood));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.res_stone, res_stone));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.res_iron, res_iron));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.villages.res_pop, res_pop));
                    DB.villages.updateValue(Integer.parseInt(id), nameValuePairs);
                }
            }
            return true;
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetVillageList(String)", "TWHelper");
            return false;
        }
    }

    /**
     * Reads all homecoming commands
     *
     * @param response HTML code of 'overview' page
     * @return true if nothing went wrong
     */
    public static boolean GetReturningTroops(String response, int actVillageID) {
        try {
            if(response.contains("<div id=\"show_outgoing_units\" class=\"vis moveable widget\">")) {
                while (response.contains("Rückkehr von ")) {
                    response = response.substring(response.substring(0, response.indexOf("Rückkehr von ")).lastIndexOf("/game.php"));
                    response = response.substring(response.indexOf("id=") + 3);
                    int id = Integer.parseInt(response.substring(0, response.indexOf("&amp;")));

                    LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Navigating: info_command-" + id, "GetReturningTroops(String, int)", "TWHelper");
                    String code = TWClient.Get("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + actVillageID + "&id=" + id + "&type=own&screen=info_command");
                    TWClient.actScreen = ScreenType.INFO_COMMAND;

                    code = code.substring(code.indexOf("Ankunft:") + 17);
                    long triggerTime = convertTWTimeToLong(code.substring(0, code.indexOf("<")));

                    code = code.replace("<span class=\"grey\">.</span>", "");
                    int spear = 0, sword = 0, axe = 0, archer = 0, spy = 0, light = 0, marcher = 0, heavy = 0, ram = 0, catapult = 0, knight = 0, snob = 0;
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    spear = Integer.parseInt(code.substring(0, code.indexOf("<")));
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    sword = Integer.parseInt(code.substring(0, code.indexOf("<")));
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    axe = Integer.parseInt(code.substring(0, code.indexOf("<")));
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    if (DB.worldConfigs.isArcherActive())
                    {
                        archer = Integer.parseInt(code.substring(0, code.indexOf("<")));
                        code = code.substring(code.indexOf("unit-item"));
                        code = code.substring(code.indexOf(">") + 1);
                    }
                    spy = Integer.parseInt(code.substring(0, code.indexOf("<")));
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    light = Integer.parseInt(code.substring(0, code.indexOf("<")));
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    if (DB.worldConfigs.isArcherActive())
                    {
                        marcher = Integer.parseInt(code.substring(0, code.indexOf("<")));
                        code = code.substring(code.indexOf("unit-item"));
                        code = code.substring(code.indexOf(">") + 1);
                    }
                    heavy = Integer.parseInt(code.substring(0, code.indexOf("<")));
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    ram = Integer.parseInt(code.substring(0, code.indexOf("<")));
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    catapult = Integer.parseInt(code.substring(0, code.indexOf("<")));
                    code = code.substring(code.indexOf("unit-item"));
                    code = code.substring(code.indexOf(">") + 1);
                    if (DB.worldConfigs.isKnightActive())
                    {
                        knight = Integer.parseInt(code.substring(0, code.indexOf("<")));
                        code = code.substring(code.indexOf("unit-item"));
                        code = code.substring(code.indexOf(">") + 1);
                    }
                    snob = Integer.parseInt(code.substring(0, code.indexOf("<")));

                    int wood = 0, stone = 0, iron = 0;

                    if (code.contains("<td>Beute:"))
                    {
                        if(code.contains("wood")) {
                            code = code.substring(code.indexOf("wood"));
                            code = code.substring(code.indexOf("</span>") + 7);
                            wood = Integer.parseInt(code.substring(0, code.indexOf("<")));
                        }
                        if(code.contains("stone")) {
                            code = code.substring(code.indexOf("stone"));
                            code = code.substring(code.indexOf("</span>") + 7);
                            stone = Integer.parseInt(code.substring(0, code.indexOf("<")));
                        }
                        if(code.contains("iron")) {
                            code = code.substring(code.indexOf("iron"));
                            code = code.substring(code.indexOf("</span>") + 7);
                            iron = Integer.parseInt(code.substring(0, code.indexOf("<")));
                        }
                    }

                    DB.commandReturnEvents.addValue(actVillageID, triggerTime, wood, stone, iron,
                            spear, sword, axe, archer, spy, light, marcher, heavy, ram, catapult, snob, knight);

                    response = response.substring(response.indexOf("Rückkehr von ") + 10);
                }
            }
            return true;
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetReturningTroops(String, int)", "TWHelper");
            return false;
        }
    }

    private static boolean GetBuildQueue(String response, int actVillageID) {
        try {
            if(response.contains("buildqueue_wrap")) {
                response = response.substring(response.indexOf("buildqueue_wrap"));
                while (response.contains("<td class=\"lit-item\">")) {
                    response = response.substring(response.indexOf("<td class=\"lit-item\">") + 21);
                    response = response.substring(response.indexOf("/>") + 2);
                    String building = response.substring(0, response.indexOf("<br />")).trim();
                    response = response.substring(response.indexOf("Stufe ") + 6);
                    int level = Integer.parseInt(response.substring(0, response.indexOf("</td>")).trim());
                    response = response.substring(response.indexOf("<td class=\"lit-item\">") + 21);
                    response = response.substring(response.indexOf("<td class=\"lit-item\">") + 21);
                    long triggerTime = convertTWTimeToLong(response.substring(0, response.indexOf("</td>")));

                    String buildingDBName = "";
                    if(building.equals("Hauptgebäude")) buildingDBName = DB.buildingEvents.main;
                    else if(building.equals("Kaserne")) buildingDBName = DB.buildingEvents.barracks;
                    else if(building.equals("Stall")) buildingDBName = DB.buildingEvents.stable;
                    else if(building.equals("Werkstatt")) buildingDBName = DB.buildingEvents.garage;
                    else if(building.equals("Kirche")) buildingDBName = DB.buildingEvents.church;
                    else if(building.equals("Erste Kirche")) buildingDBName = DB.buildingEvents.church_f;
                    else if(building.equals("Adelshof")) buildingDBName = DB.buildingEvents.snob;
                    else if(building.equals("Schmiede")) buildingDBName = DB.buildingEvents.smith;
                    else if(building.equals("Versammlungsplatz")) buildingDBName = DB.buildingEvents.place;
                    else if(building.equals("Statue")) buildingDBName = DB.buildingEvents.statue;
                    else if(building.equals("Marktplatz")) buildingDBName = DB.buildingEvents.market;
                    else if(building.equals("Holzfäller")) buildingDBName = DB.buildingEvents.wood;
                    else if(building.equals("Lehmgrube")) buildingDBName = DB.buildingEvents.stone;
                    else if(building.equals("Eisenmine")) buildingDBName = DB.buildingEvents.iron;
                    else if(building.equals("Bauernhof")) buildingDBName = DB.buildingEvents.farm;
                    else if(building.equals("Speicher")) buildingDBName = DB.buildingEvents.storage;
                    else if(building.equals("Versteck")) buildingDBName = DB.buildingEvents.hide;
                    else if(building.equals("Wall")) buildingDBName = DB.buildingEvents.wall;
                    else {
                        LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown building: " + building, "GetBuildQueue(String, int)", "TWHelper");
                        return false;
                    }

                    List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.gid, String.valueOf(triggerTime) + String.valueOf(actVillageID)));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.triggerTime, String.valueOf(triggerTime)));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.villageID, String.valueOf(actVillageID)));
                    nameValuePairs.add(new DatabaseManager.NameValuePair(buildingDBName, "1"));
                    DatabaseManager.insertOrUpdate(DB.buildingEvents.tableName, DB.buildingEvents.gid, nameValuePairs);

                    response = response.substring(response.indexOf("<td class=\"lit-item\">") + 21);
                }
            }
            return true;
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetBuildQueue(String, int)", "TWHelper");
            return false;
        }
    }

    private static boolean GetUnits(String response, int actVillageID) {
        try {
            response = response.substring(response.indexOf("contentContainer"));
            response = response.substring(response.indexOf("train_form"));
            response = response.substring(0, response.indexOf("</form>"));

            int barracks_level = Integer.parseInt(DB.villages.getValue(DB.villages.barracks_level, actVillageID));
            int smith_level = Integer.parseInt(DB.villages.getValue(DB.villages.smith_level, actVillageID));
            int stable_level = Integer.parseInt(DB.villages.getValue(DB.villages.stable_level, actVillageID));
            int garage_level = Integer.parseInt(DB.villages.getValue(DB.villages.garage_level, actVillageID));

            int spear_village = 0, spear_all = 0;
            if (barracks_level >= 1 && response.indexOf("Speerträger") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                spear_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                spear_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int sword_village = 0, sword_all = 0;
            if (barracks_level >= 1 && smith_level >= 1 && response.indexOf("Schwertkämpfer") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                sword_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                sword_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int axe_village = 0, axe_all = 0;
            if (barracks_level >= 1 && smith_level >= 2 && response.indexOf("Axtkämpfer") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                axe_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                axe_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int archer_village = 0, archer_all = 0;
            if (barracks_level >= 5 && smith_level >= 5 && DB.worldConfigs.isArcherActive() && response.indexOf("Bogenschütze") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                archer_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                archer_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int spy_village = 0, spy_all = 0;
            if (stable_level >= 1 && response.indexOf("Späher") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                spy_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                spy_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int light_village = 0, light_all = 0;
            if (stable_level >= 3 && response.indexOf("Leichte Kavallerie") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                light_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                light_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int marcher_village = 0, marcher_all = 0;
            if (stable_level >= 5 && DB.worldConfigs.isArcherActive() && response.indexOf("Berittener Bogenschütze") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                marcher_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                marcher_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int heavy_village = 0, heavy_all = 0;
            if (stable_level >= 10 && smith_level >= 15 && response.indexOf("Schwere Kavallerie") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                heavy_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                heavy_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int ram_village = 0, ram_all = 0;
            if (garage_level >= 1 && response.indexOf("Rammbock") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                ram_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                ram_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }
            int catapult_village = 0, catapult_all = 0;
            if (garage_level >= 2 && smith_level >= 12 && response.indexOf("Katapult") < response.indexOf("<td style=\"text-align: center\">"))
            {
                response = response.substring(response.indexOf("<td style=\"text-align: center\">") + 31);
                catapult_village = Integer.parseInt(response.substring(0, response.indexOf("/")));
                response = response.substring(response.indexOf("/") + 1);
                catapult_all = Integer.parseInt(response.substring(0, response.indexOf("<")));
            }

            DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET "
                    + DB.villages.spear_village + " = " + spear_village
                    + ", " + DB.villages.spear_all + " = " + spear_all
                    + ", " + DB.villages.sword_village + " = " + sword_village
                    + ", " + DB.villages.sword_all + " = " + sword_all
                    + ", " + DB.villages.axe_village + " = " + axe_village
                    + ", " + DB.villages.axe_all + " = " + axe_all
                    + ", " + DB.villages.archer_village + " = " + archer_village
                    + ", " + DB.villages.archer_all + " = " + archer_all
                    + ", " + DB.villages.spy_village + " = " + spy_village
                    + ", " + DB.villages.spy_all + " = " + spy_all
                    + ", " + DB.villages.light_village + " = " + light_village
                    + ", " + DB.villages.light_all + " = " + light_all
                    + ", " + DB.villages.marcher_village + " = " + marcher_village
                    + ", " + DB.villages.marcher_all + " = " + marcher_all
                    + ", " + DB.villages.heavy_village + " = " + heavy_village
                    + ", " + DB.villages.heavy_all + " = " + heavy_all
                    + ", " + DB.villages.ram_village + " = " + ram_village
                    + ", " + DB.villages.ram_all + " = " + ram_all
                    + ", " + DB.villages.catapult_village + " = " + catapult_village
                    + ", " + DB.villages.catapult_all + " = " + catapult_all
                    + " WHERE " + DB.villages.id + " = " + actVillageID + ";");
            return true;
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetUnits(String, int)", "TWHelper");
            return false;
        }
    }

    private static boolean GetRecruitQueue(String response, int actVillageID) {
        try {
            if(response.contains("current_prod_wrapper")) {
                response = response.substring(response.indexOf("current_prod_wrapper"));
                while (response.contains("<tr class=\"lit\">") || response.contains("<tr class=\"sortable_row\"")) {
                    int amount;
                    String unit;
                    long triggerTime;
                    if (response.contains("<tr class=\"lit\">")
                            && (!response.contains("<tr class=\"sortable_row\"")
                            || response.indexOf("<tr class=\"lit\">") < response.indexOf("<tr class=\"sortable_row\""))) {
                        response = response.substring(response.indexOf("<tr class=\"lit\">"));
                        response = response.substring(response.indexOf("</div>") + 6);
                        response = response.trim();
                        amount = Integer.parseInt(response.substring(0, response.indexOf(" ")));
                        response = response.substring(response.indexOf(" ") + 1);
                        unit = response.substring(0, response.indexOf("</td>")).trim();
                        response = response.substring(response.indexOf("<td class=\"lit-item\">") + 21);
                        response = response.substring(response.indexOf("<td class=\"lit-item\">") + 21);
                        triggerTime = convertTWTimeToLong(response.substring(0, response.indexOf("</td>")));
                    } else {
                        response = response.substring(response.indexOf("<tr class=\"sortable_row\""));
                        response = response.substring(response.indexOf("</div>") + 6);
                        response = response.trim();
                        amount = Integer.parseInt(response.substring(0, response.indexOf(" ")));
                        response = response.substring(response.indexOf(" ") + 1);
                        unit = response.substring(0, response.indexOf("</td>")).trim();
                        response = response.substring(response.indexOf("<td>") + 4);
                        response = response.substring(response.indexOf("<td>") + 4);
                        triggerTime = convertTWTimeToLong(response.substring(0, response.indexOf("</td>")));
                    }

                    String unitDBName;
                    UnitType unitType;
                    if(unit.equals("Speerträger")) { unitDBName = DB.unitCreationEvents.spear; unitType = UnitType.SPEAR; }
                    else if(unit.equals("Schwertkämpfer")) { unitDBName = DB.unitCreationEvents.sword; unitType = UnitType.SWORD; }
                    else if(unit.equals("Axtkämpfer")) { unitDBName = DB.unitCreationEvents.axe; unitType = UnitType.AXE; }
                    else if(unit.equals("Bogenschütze")) { unitDBName = DB.unitCreationEvents.archer; unitType = UnitType.ARCHER; }
                    else if(unit.equals("Bogenschützen")) { unitDBName = DB.unitCreationEvents.archer; unitType = UnitType.ARCHER; }
                    else if(unit.equals("Späher")) { unitDBName = DB.unitCreationEvents.spy; unitType = UnitType.SPY; }
                    else if(unit.equals("Leichte Kavallerie")) { unitDBName = DB.unitCreationEvents.light; unitType = UnitType.LIGHT; }
                    else if(unit.equals("Berittener Bogenschütze")) { unitDBName = DB.unitCreationEvents.marcher; unitType = UnitType.MARCHER; }
                    else if(unit.equals("Berittene Bogenschützen")) { unitDBName = DB.unitCreationEvents.marcher; unitType = UnitType.MARCHER; }
                    else if(unit.equals("Schwere Kavallerie")) { unitDBName = DB.unitCreationEvents.heavy; unitType = UnitType.HEAVY; }
                    else if(unit.equals("Rammbock")) { unitDBName = DB.unitCreationEvents.ram; unitType = UnitType.RAM; }
                    else if(unit.equals("Rammböcke")) { unitDBName = DB.unitCreationEvents.ram; unitType = UnitType.RAM; }
                    else if(unit.equals("Katapult")) { unitDBName = DB.unitCreationEvents.catapult; unitType = UnitType.CATAPULT; }
                    else if(unit.equals("Katapulte")) { unitDBName = DB.unitCreationEvents.catapult; unitType = UnitType.CATAPULT; }
                    else {
                        LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unknown unit: " + unit, "GetRecruitQueue(String, int)", "TWHelper");
                        return false;
                    }

                    for(int i = amount - 1; i >= 0; i--) {
                        List<DatabaseManager.NameValuePair> nameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
                        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.gid, String.valueOf(triggerTime - i * GetUnitBuildtimeInMillis(unitType, actVillageID)) + String.valueOf(actVillageID)));
                        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.triggerTime, String.valueOf(triggerTime - i * GetUnitBuildtimeInMillis(unitType, actVillageID))));
                        nameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.villageID, String.valueOf(actVillageID)));
                        nameValuePairs.add(new DatabaseManager.NameValuePair(unitDBName, "1"));
                        DatabaseManager.insertOrUpdate(DB.unitCreationEvents.tableName, DB.unitCreationEvents.gid, nameValuePairs);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetRecruitQueue(String, int)", "TWHelper");
            return false;
        }
    }

    private static boolean GetKnight(String response, int actVillageID) {
        try {
            byte knight_village = 0, knight_all = 0;
            if (response.contains("hierher umstationieren."))
            {
                knight_all = 0;
                knight_village = 0;
            }
            else if (response.contains("Paladin greift") | response.contains("Paladin unterstützt") | response.contains("Paladin kehrt zurück."))
            {
                knight_village = 0;
                knight_all = 1;
            }
            else if (response.contains("befindet sich in diesem Dorf."))
            {
                knight_village = 1;
                knight_all = 1;
            }

            DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET "
                    + DB.villages.knight_village + " = " + knight_village
                    + ", " + DB.villages.knight_all + " = " + knight_all
                    + " WHERE " + DB.villages.id + " = " + actVillageID + ";");

            return true;
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage(), "GetKnight(String, int)", "TWHelper");
            return false;
        }
    }
}