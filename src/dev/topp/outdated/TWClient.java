package dev.topp.outdated;

import dev.topp.LoggingManager;
import dev.topp.outdated.TWHelper.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class TWClient {

    public static boolean newReport;
    public static boolean newMail;
    public static boolean newForumThread;
    public static boolean newIncomingAttacks;
    public static int incomingAttacksCount;

    public static long TimeDifference;
    public static int userID;

    public static int actVillageID = 0;
    public static ScreenType actScreen = null;

    public static String Get(String url) {
        String tmp = MyHttpClient.Get(url);
        checkForTWEvents(tmp);
        return tmp;
    }

    public static String Post(String url, List<NameValuePair> params) {
        String tmp = MyHttpClient.Post(url, params);
        checkForTWEvents(tmp);
        return tmp;
    }

    /**
     *
     * @param site Site you wanna go to.
     * @param villageID Id of the village you wanna go to.
     * @return Source code of the destination HTML web page.
     */
    public static String Goto (ScreenType site, int villageID) {
        String response = "";

        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Going to " + villageID + "-" + TWHelper.getNameFromType(site) + " coming from " + actVillageID + "-" + TWHelper.getNameFromType(actScreen), "Goto(ScreenType, int)", "TWClient");

        if ((actVillageID != villageID && actScreen != ScreenType.OVERVIEW_VILLAGES) || site == ScreenType.OVERVIEW_VILLAGES) {
            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Navigating: " + TWHelper.getNameFromType(ScreenType.OVERVIEW_VILLAGES), "Goto(ScreenType, int)", "TWClient");
            if(actVillageID == 0) actVillageID = villageID;
            response = Get("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + actVillageID + "&screen=" + TWHelper.getTWUrlFromType(ScreenType.OVERVIEW_VILLAGES));
            actScreen = ScreenType.OVERVIEW_VILLAGES;

            TWHelper.GetSiteInformation(response, actScreen);

            actVillageID = villageID;
            // Wartezeit(new Random().Next(WAIT_MIN, WAIT_MAX));
        }

        if (site != ScreenType.OVERVIEW && actScreen != ScreenType.OVERVIEW && site != ScreenType.OVERVIEW_VILLAGES) {
            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Navigating: " + TWHelper.getNameFromType(ScreenType.OVERVIEW), "Goto(ScreenType, int)", "TWClient");
            response = Get("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + actVillageID + "&screen=" + TWHelper.getTWUrlFromType(ScreenType.OVERVIEW));
            actScreen = ScreenType.OVERVIEW;
            TWHelper.GetSiteInformation(response, actScreen);
            // Wartezeit(new Random().Next(WAIT_MIN, WAIT_MAX));
        }
        if (site != ScreenType.OVERVIEW_VILLAGES) {
            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Navigating: " + TWHelper.getNameFromType(site), "Goto(ScreenType, int)", "TWClient");
            response = Get("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + actVillageID + "&screen=" + TWHelper.getTWUrlFromType(site));
            actScreen = site;
            TWHelper.GetSiteInformation(response, actScreen);
            // Wartezeit(new Random().Next(WAIT_MIN, WAIT_MAX));
        }

        return response;
    }

    /**
     *
     * @param building Type of the building you want to construct.
     * @param villageID Id of the village you want to construct your building.
     */
    public static boolean Build (BuildingType building, int villageID) {
        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Building " + TWHelper.getNameFromType(building) + " in " + villageID, "Build(BuildingType, int)", "TWClient");
        try {
            String response = Goto(ScreenType.MAIN, villageID);
            response = response.substring(response.indexOf("<tr id=\"main_buildrow_" + TWHelper.getTWUrlFromType(building) + "\">"));
            response = response.substring(0, response.indexOf("</tr>"));

            if (!response.contains("class=\"inactive")) {
                response = response.substring(response.indexOf("id=\"main_buildlink_" + TWHelper.getTWUrlFromType(building) + "\""));
                // href="/game.php?village=110405&amp;action=upgrade_building&amp;h=c57b&amp;id=main&amp;type=main&amp;screen=main
                String h = response.substring(response.indexOf("&amp;h=") + 7, response.indexOf("&amp;id="));

                // http://de112.die-staemme.de/game.php?village=135135&ajaxaction=upgrade_building&h=abb8&type=main&screen=main&&client_time=1421851821
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id", TWHelper.getTWUrlFromType(building)));
                nameValuePairs.add(new BasicNameValuePair("force", "1"));
                nameValuePairs.add(new BasicNameValuePair("destroy", "0"));
                nameValuePairs.add(new BasicNameValuePair("source", String.valueOf(villageID)));
                response = Post("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + villageID + "&action=upgrade_building&h=" + h + "&type=main&screen=main", nameValuePairs);

                response = response.substring(response.indexOf("\"date_complete\"") + 16);
                response = response.substring(0, response.indexOf(","));

                LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Construction will finish in " + response + " seconds", "Build(BuildingType, int)", "TWClient");
                DatabaseManager.customQuery("UPDATE " + DB.villages.tableName + " SET " + DB.villages.buildqueue_count + " = " + DB.villages.buildqueue_count + " + 1 WHERE " + DB.villages.id + " = " + villageID + ";");

                List<DatabaseManager.NameValuePair> dbNameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
                long triggerTime = System.currentTimeMillis() + Long.parseLong(response) * 1000;
                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.gid, triggerTime + "" + villageID));
                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.villageID, String.valueOf(villageID)));
                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.triggerTime, String.valueOf(triggerTime)));
                if (building == BuildingType.MAIN)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.main, "1"));
                else if (building == BuildingType.BARRACKS)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.barracks, "1"));
                else if (building == BuildingType.STABLE)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.stable, "1"));
                else if (building == BuildingType.GARAGE)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.garage, "1"));
                else if (building == BuildingType.CHURCH)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.church, "1"));
                else if (building == BuildingType.CHURCH_F)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.church_f, "1"));
                else if (building == BuildingType.SNOB)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.snob, "1"));
                else if (building == BuildingType.SMITH)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.smith, "1"));
                else if (building == BuildingType.PLACE)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.place, "1"));
                else if (building == BuildingType.STATUE)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.statue, "1"));
                else if (building == BuildingType.MARKET)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.market, "1"));
                else if (building == BuildingType.WOOD)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.wood, "1"));
                else if (building == BuildingType.STONE)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.stone, "1"));
                else if (building == BuildingType.IRON)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.iron, "1"));
                else if (building == BuildingType.FARM)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.farm, "1"));
                else if (building == BuildingType.STORAGE)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.storage, "1"));
                else if (building == BuildingType.HIDE)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.hide, "1"));
                else if (building == BuildingType.WALL)
                    dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.buildingEvents.wall, "1"));
                DatabaseManager.insertOrUpdate(DB.buildingEvents.tableName, DB.buildingEvents.gid, dbNameValuePairs);
                return true;
            }
            else {
                response = response.substring(response.indexOf("class=\"inactive"));
                response = response.substring(response.indexOf(">") + 1);
                response = response.substring(0, response.indexOf("<")).trim();
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not build " + TWHelper.getNameFromType(building) + " in " + villageID + ". Reason: " + response, "Build(BuildingType, int)", "TWClient");
                return false;
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Could not build " + TWHelper.getNameFromType(building) + " in " + villageID + ". Reason: " + e.getClass().getSimpleName() + "->" + e.getMessage(), "Build(BuildingType, int)", "TWClient");
            return false;
        }
    }

    /**
     *
     * @param unit Type of the unit you want to recruit.
     * @param amount Amount of units you want to recruit
     * @param villageID Id of the village you want to recruit your units.
     */
    public static boolean Recruit(UnitType unit, int amount, int villageID) {
        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Recruiting " + amount + " " + TWHelper.getNameFromType(unit) + " in " + villageID, "Recruit(UnitType, int, int)", "TWClient");

        try {
            if (unit == UnitType.SPEAR | unit == UnitType.SWORD | unit == UnitType.AXE | unit == UnitType.ARCHER
                    | unit == UnitType.SPY | unit == UnitType.LIGHT | unit == UnitType.MARCHER | unit == UnitType.HEAVY | unit == UnitType.RAM | unit == UnitType.CATAPULT)
            {
                String response = Goto(ScreenType.TRAIN, villageID);
                if (response.contains("<input name=\"" + TWHelper.getTWUrlFromType(unit)))
                {
                    response = response.substring(response.indexOf("<form action") + 14);
                    String h = response.substring(response.indexOf("&h=") + 3, response.indexOf("&mode="));

                    List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                    formparams.add(new BasicNameValuePair("units[" + TWHelper.getTWUrlFromType(unit) + "]", String.valueOf(amount)));
                    // http://de92.die-staemme.de/game.php?village=52727&ajaxaction=train&h=e65b&mode=train&screen=train
                    // units%5Baxe%5D=1&units%5Blight%5D=1
                    response = Post("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + villageID + "&ajaxaction=train&h=" + h + "&mode=train&screen=" + TWHelper.getTWUrlFromType(ScreenType.TRAIN), formparams);

                    if(response.contains("\"error\":\"")) {
                        response = response.substring(response.indexOf("\"error\":\"") + 9);
                        response = response.substring(0, response.indexOf("\""));
                        response = response.replace("\\u00f6", "ö");
                        LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not recruit " + TWHelper.getNameFromType(unit) + " in " + villageID + ". Reason: " + response, "Recruit(UnitType, int, int)", "TWClient");
                        return false;
                    } else {
                        if(unit == UnitType.SPEAR | unit == UnitType.SWORD | unit == UnitType.AXE | unit == UnitType.ARCHER ) {
                            response = response.substring(response.indexOf("trainqueue_wrap_barracks"));
                            response = response.substring(0, response.indexOf("<\\/tbody>"));
                            if(response.contains("<tbody id=\\\"trainqueue_barracks\\\">\\n\\t\\t\\n\\t\\t\\t\\t\\t\\t\\t\\t<tr class")) {
                                response = response.substring(response.indexOf("trainqueue_barracks"));
                                response = response.substring(response.lastIndexOf("<tr class=\\\"sortable_row\\\""));
                                response = response.substring(response.indexOf("<td>") + 4);
                                response = response.substring(response.indexOf("<td>") + 4);
                                response = response.substring(0, response.indexOf("<"));
                            } else {
                                response = response.substring(response.indexOf("<\\/div>"));
                                response = response.substring(response.indexOf("<td class=\\\"lit-item\\\">") + 23);
                                response = response.substring(response.indexOf("<td class=\\\"lit-item\\\">") + 23);
                                response = response.substring(0, response.indexOf("<"));
                            }
                        } else if(unit == UnitType.SPY | unit == UnitType.LIGHT | unit == UnitType.MARCHER | unit == UnitType.HEAVY) {
                            response = response.substring(response.indexOf("trainqueue_wrap_stable"));
                            response = response.substring(response.indexOf("<\\/tbody>"));
                            if(response.contains("<tbody id=\\\"trainqueue_stable\\\">\\n\\t\\t\\n\\t\\t\\t\\t\\t\\t\\t\\t<tr class")) {
                                response = response.substring(response.indexOf("trainqueue_stable"));
                                response = response.substring(response.lastIndexOf("<tr class=\\\"sortable_row\\\""));
                                response = response.substring(response.indexOf("<td>") + 4);
                                response = response.substring(response.indexOf("<td>") + 4);
                                response = response.substring(0, response.indexOf("<"));
                            } else {
                                response = response.substring(response.indexOf("<\\/div>"));
                                response = response.substring(response.indexOf("<td class=\\\"lit-item\\\">") + 23);
                                response = response.substring(response.indexOf("<td class=\\\"lit-item\\\">") + 23);
                                response = response.substring(0, response.indexOf("<"));
                            }
                        } else {
                            response = response.substring(response.indexOf("trainqueue_wrap_garage"));
                            response = response.substring(response.indexOf("<\\/tbody>"));
                            if(response.contains("<tbody id=\\\"trainqueue_garage\\\">\\n\\t\\t\\n\\t\\t\\t\\t\\t\\t\\t\\t<tr class")) {
                                response = response.substring(response.indexOf("trainqueue_garage"));
                                response = response.substring(response.lastIndexOf("<tr class=\\\"sortable_row\\\""));
                                response = response.substring(response.indexOf("<td>") + 4);
                                response = response.substring(response.indexOf("<td>") + 4);
                                response = response.substring(0, response.indexOf("<"));
                            } else {
                                response = response.substring(response.indexOf("<\\/div>"));
                                response = response.substring(response.indexOf("<td class=\\\"lit-item\\\">") + 23);
                                response = response.substring(response.indexOf("<td class=\\\"lit-item\\\">") + 23);
                                response = response.substring(0, response.indexOf("<"));
                            }
                        }

                        long finishTime = TWHelper.convertTWTimeToLong(response);

                        for(int i = 0; i < amount; i++) {
                            List<DatabaseManager.NameValuePair> dbNameValuePairs = new ArrayList<DatabaseManager.NameValuePair>();
                            long triggerTime = finishTime - TWHelper.GetUnitBuildtimeInMillis(unit, villageID) * i;
                            dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.gid, triggerTime + "" + villageID));
                            dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.villageID, String.valueOf(villageID)));
                            dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.triggerTime, String.valueOf(triggerTime)));
                            if (unit == UnitType.SPEAR)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.spear, "1"));
                            else if (unit == UnitType.SWORD)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.sword, "1"));
                            else if (unit == UnitType.AXE)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.axe, "1"));
                            else if (unit == UnitType.ARCHER)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.archer, "1"));
                            else if (unit == UnitType.SPY)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.spy, "1"));
                            else if (unit == UnitType.LIGHT)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.light, "1"));
                            else if (unit == UnitType.MARCHER)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.marcher, "1"));
                            else if (unit == UnitType.HEAVY)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.heavy, "1"));
                            else if (unit == UnitType.RAM)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.ram, "1"));
                            else if (unit == UnitType.CATAPULT)
                                dbNameValuePairs.add(new DatabaseManager.NameValuePair(DB.unitCreationEvents.catapult, "1"));

                            DatabaseManager.insertOrUpdate(DB.unitCreationEvents.tableName, DB.unitCreationEvents.gid, dbNameValuePairs);
                        }
                        return true;
                    }
                }
                else {
                    LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not recruit " + TWHelper.getNameFromType(unit) + " in " + villageID + ". Reason: Unit may not be researched.", "Recruit(UnitType, int, int)", "TWClient");
                    return false;
                }
            }
            else if (unit == UnitType.KNIGHT) {
                //ToDo: Rekrutierung vom Paladin implementieren
                //browser_goto("statue", villageID);
                //Wartezeit(new Random().Next(400, 800));
                //HtmlElementCollection tags = wb.Document.Links;
                //foreach (HtmlElement element in tags)
                //{
                //    if (element.InnerText == "Paladin ernennen")
                //    {
                //        element.InvokeMember("Click");
                //        break;
                //    }
                //}
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not recruit " + TWHelper.getNameFromType(unit) + " in " + villageID + ". Reason: UnitType not implemented yet.", "Recruit(UnitType, int, int)", "TWClient");
                return false;
            }
            else if (unit == UnitType.SNOB) {
                //ToDo: Rekrutierung vom Adelsgeschlecht implementieren
                //browser_goto("snob", villageID);
                //Wartezeit(new Random().Next(400, 800));
                //HtmlElementCollection tags = wb.Document.Links;
                //foreach (HtmlElement element in tags)
                //{
                //    if (element.InnerText == "Einheit erzeugen") //element.InnerText.Contains("Goldmünze prägen")
                //    {
                //        element.InvokeMember("Click");
                //        break;
                //    }
                //}
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not recruit " + TWHelper.getNameFromType(unit) + " in " + villageID + ". Reason: UnitType not implemented yet.", "Recruit(UnitType, int, int)", "TWClient");
                return false;
            }
            else return false;
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Could not recruit " + TWHelper.getNameFromType(unit) + " in " + villageID + ". Reason: " + e.getClass().getSimpleName() + "->" + e.getMessage(), "Recruit(UnitType, int, int)", "TWClient");
            return false;
        }
    }

    public static long SendTroops(int sourceVillageID, int x, int y, int spear, int sword, int axe, int archer, int spy, int light, int marcher, int heavy, int ram, int catapult, int knight, int snob) {
        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Sending Troops from " + sourceVillageID + " to " + x + "|" + y, "SendTroops(int, int, ...)", "TWClient");

        try {
            String response = Goto(ScreenType.PLACE, sourceVillageID);
            response = response.substring(response.indexOf("<form id=\"units_form"));
            response = response.substring(response.indexOf("type=\"hidden\"") + 20);
            String hiddenName = response.substring(0, response.indexOf("\""));
            response = response.substring(response.indexOf("value=\"") + 7);
            String hiddenValue = response.substring(0, response.indexOf("\""));

            String s_spear = String.valueOf(spear); if (spear == 0) s_spear = "";
            String s_sword = String.valueOf(sword); if (sword == 0) s_sword = "";
            String s_axe = String.valueOf(axe); if (axe == 0) s_axe = "";
            String s_archer = String.valueOf(archer); if (archer == 0) s_archer = "";
            String s_spy = String.valueOf(spy); if (spy == 0) s_spy = "";
            String s_light = String.valueOf(light); if (light == 0) s_light = "";
            String s_marcher = String.valueOf(marcher); if (marcher == 0) s_marcher = "";
            String s_heavy = String.valueOf(heavy); if (heavy == 0) s_heavy = "";
            String s_ram = String.valueOf(ram); if (ram == 0) s_ram = "";
            String s_catapult = String.valueOf(catapult); if (catapult == 0) s_catapult = "";
            String s_knight = String.valueOf(knight); if (knight == 0) s_knight = "";
            String s_snob = String.valueOf(snob); if (snob == 0) s_snob = "";

            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair(hiddenName, hiddenValue));
            formparams.add(new BasicNameValuePair("template_id", ""));
            formparams.add(new BasicNameValuePair("spear", s_spear));
            formparams.add(new BasicNameValuePair("sword", s_sword));
            formparams.add(new BasicNameValuePair("axe", s_axe));
            if(DB.worldConfigs.isArcherActive())
                formparams.add(new BasicNameValuePair("archer", s_archer));
            formparams.add(new BasicNameValuePair("spy", s_spy));
            formparams.add(new BasicNameValuePair("light", s_light));
            if(DB.worldConfigs.isArcherActive())
                formparams.add(new BasicNameValuePair("marcher", s_marcher));
            formparams.add(new BasicNameValuePair("heavy", s_heavy));
            formparams.add(new BasicNameValuePair("ram", s_ram));
            formparams.add(new BasicNameValuePair("catapult", s_catapult));
            if(DB.worldConfigs.isKnightActive())
                formparams.add(new BasicNameValuePair("knight", s_knight));
            formparams.add(new BasicNameValuePair("snob", s_snob));
            formparams.add(new BasicNameValuePair("x", String.valueOf(x)));
            formparams.add(new BasicNameValuePair("y", String.valueOf(y)));
            formparams.add(new BasicNameValuePair("target_type", "coord"));
            formparams.add(new BasicNameValuePair("input", x + "|" + y));
            formparams.add(new BasicNameValuePair("attack", "Angreifen"));

            // http://de102.die-staemme.de/game.php?village=110405&try=confirm&screen=place
            // 4266139fe1a750b8e38f6b=1b214d8b426613&template_id=&spear=20&sword=59&axe=&spy=&light=&heavy=&ram=&catapult=&knight=1&snob=&x=794&y=662&target_type=coord&input=794%7C662&attack=Angreifen
            response = Post("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + sourceVillageID + "&try=confirm&screen=" + TWHelper.getTWUrlFromType(ScreenType.PLACE), formparams);
            actScreen = ScreenType.CONFIRM_ATTACK;

            if (response.contains("<div class=\"error_box\">")) {
                response = response.substring(response.indexOf("<div class=\"error_box\">") + 24);
                response = response.substring(0, response.indexOf("</div>")).trim();
                //ToDo: Add reactions to diffrent Errors
                // Ziel nicht vorhanden
                // Keine Einheiten ausgewählt
                // Der Angriffstrupp muss aus mindestens 9 Einwohnern bestehen.
                // Du musst die x und y Koordinaten des Ziels angeben.
                // Das Ziel steht noch unter Angriffsschutz. Du darfst erst am 18.02. um 14:24 Uhr angreifen.
                // Nicht genügend Einheiten vorhanden
                // Das Ziel kann bis zum 11.4. 1:25 nur angreifen und angegriffen werden, wenn das Punkte-Verhältnis zwischen Angreifer und Verteidiger höchstens 20 : 1 ist.
                LoggingManager.log(LoggingManager.LoggingLevel.WARNING, "Could not send. An error occurred. Error: " + response, "SendTroops(int, int, ...)", "TWClient");
                return 0;
            }
            else {
                response = response.substring(response.indexOf("command-confirm-form"));
                response = response.substring(response.indexOf("&amp;h=") + 7);
                String h = response.substring(0, response.indexOf("&amp;"));
                response = response.substring(response.indexOf("name=\"ch\"") + 17);
                String ch = response.substring(0, response.indexOf("\""));
                response = response.substring(response.indexOf("name=\"action_id\"") + 24);
                String action_id = response.substring(0, response.indexOf("\""));

                formparams = new ArrayList<NameValuePair>();
                formparams.add(new BasicNameValuePair("attack", "true"));
                formparams.add(new BasicNameValuePair("ch", ch));
                formparams.add(new BasicNameValuePair("x", String.valueOf(x)));
                formparams.add(new BasicNameValuePair("y", String.valueOf(y)));
                formparams.add(new BasicNameValuePair("action_id", action_id));
                if(spear > 0) formparams.add(new BasicNameValuePair("spear", String.valueOf(spear)));
                if(sword > 0) formparams.add(new BasicNameValuePair("sword", String.valueOf(sword)));
                if(axe > 0) formparams.add(new BasicNameValuePair("axe", String.valueOf(axe)));
                if(DB.worldConfigs.isArcherActive() && archer > 0) formparams.add(new BasicNameValuePair("archer", String.valueOf(archer)));
                if(spy > 0) formparams.add(new BasicNameValuePair("spy", String.valueOf(spy)));
                if(light > 0) formparams.add(new BasicNameValuePair("light", String.valueOf(light)));
                if(DB.worldConfigs.isArcherActive() && marcher > 0) formparams.add(new BasicNameValuePair("marcher", String.valueOf(marcher)));
                if(heavy > 0) formparams.add(new BasicNameValuePair("heavy", String.valueOf(heavy)));
                if(ram > 0) formparams.add(new BasicNameValuePair("ram", String.valueOf(ram)));
                if(catapult > 0) formparams.add(new BasicNameValuePair("catapult", String.valueOf(catapult)));
                if(DB.worldConfigs.isKnightActive() && knight > 0) formparams.add(new BasicNameValuePair("knight", String.valueOf(knight)));
                if(snob > 0) formparams.add(new BasicNameValuePair("snob", String.valueOf(snob)));

                // attack=true&ch=5e1784ca117295ecbe43269bdbaf4dd66ea1ea1c&x=794&y=662&action_id=4919378&spear=20&sword=59&axe=0&spy=0&light=0&heavy=0&ram=0&catapult=0&knight=1&snob=0
                Post("http://" + LoginManager.getWorld() + ".die-staemme.de/game.php?village=" + sourceVillageID + "&action=command&h=" + h + "&screen=" + TWHelper.getTWUrlFromType(ScreenType.PLACE), formparams);
                actScreen = ScreenType.PLACE;

                float unitSpeed = DB.unitInfo.getValue(DB.unitInfo.spy_speed);
                if(spear > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.spear_speed));
                if(sword > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.sword_speed));
                if(axe > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.axe_speed));
                if(DB.worldConfigs.isArcherActive() && archer > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.archer_speed));
                if(light > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.light_speed));
                if(DB.worldConfigs.isArcherActive() && marcher > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.marcher_speed));
                if(heavy > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.heavy_speed));
                if(ram > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.ram_speed));
                if(catapult > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.catapult_speed));
                if(DB.worldConfigs.isKnightActive() && knight > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.knight_speed));
                if(snob > 0) unitSpeed = Math.max(unitSpeed, DB.unitInfo.getValue(DB.unitInfo.snob_speed));

                return TWHelper.calculateTimeForDistance(sourceVillageID, x, y, unitSpeed);
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Could not send. An error occurred. Error: " + e.getClass().getSimpleName() + "->" + e.getMessage(), "SendTroops(int, int, ...)", "TWClient");
            return 0;
        }
    }

    private static void checkForTWEvents(String response) {
        newReport = checkForNewReports(response);

        newMail = checkForNewMails(response);

        newForumThread = checkForNewForumThreads(response);

        int incomingAttacks = getIncomingAttacks(response);
        if (incomingAttacksCount < incomingAttacks)
            newIncomingAttacks = true;
        incomingAttacksCount = incomingAttacks;
    }

    private static boolean checkForNewReports(String response) {
        return response.contains("<span id=\"new_report\" class=\"icon header new_report\" title=\"Neuer Bericht\"></span>");
    }

    private static boolean checkForNewMails(String response) {
        return response.contains("<span id=\"new_mail\" class=\"icon header new_mail\" title=\"Neue Nachricht\"></span>");
    }

    private static boolean checkForNewForumThreads(String response) {
        return response.contains("Neuer Beitrag im Stammesforum");
    }

    private static int getIncomingAttacks(String response) {
        String res = "";
        try {
            if(response.contains("<span id=\"incomings_amount\">")) {
                res = response.substring(response.indexOf("<span id=\"incomings_amount\">") + 28);
                return Integer.valueOf(res.substring(0, res.indexOf("<")));
            }
        } catch (Exception e) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, e.getClass().getSimpleName() + "->" + e.getMessage() + "->" + res, "getIncomingAttacks()", "TWBackgroundManager");
        }
        return 0;
    }
}
