package dev.topp.outdated;

import dev.topp.LoggingManager;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class LoginManager {

    private static String username = "";
    private static String password = "";
    private static String world = "";
    private static String cryptedPassword;

    public static String getWorld() {
        return world;
    }

    public static boolean firstLogin(String username, String password, String world) {
        if (username.equals(""))
            LoginManager.username = readUsernameFromConsole();
        else LoginManager.username = username;
        if (password.equals(""))
            LoginManager.password = readPasswordFromConsole();
        else LoginManager.password = password;

        // Go to main page, just for fake purpose
        MyHttpClient.Get("http://www.die-staemme.de/index.php");


        if(world.equals("") || !isTWWorldString(world)) {
            // If world is not valid, get available worlds
            System.out.println("Available worlds for your account:");
            List<String> availableWorlds = getAvailableWorldsAndSetCryptedPassword();
            for (String availableWorld : availableWorlds)
                System.out.println("\t-" + availableWorld);

            // Let user choose, which world he wants and parse it
            do {
                world = readWorldFromConsole(availableWorlds.get(0));
            } while (!availableWorlds.contains(world));
            LoginManager.world = convertWorldToTWWorld(world);
        } else {
            // If world is valid, just parse it
            LoginManager.world = world;
            // Get the crypted password and ignore the worlds returned
            getAvailableWorldsAndSetCryptedPassword();
        }

        return loginToWorld();
    }

    public static boolean isLoogedIn(String response) {
        return response.contains("<h2>Session abgelaufen</h2>");
    }

    public static boolean loginToWorld() {
        String response;
        do {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            formParams.add(new BasicNameValuePair("user", username));
            formParams.add(new BasicNameValuePair("password", cryptedPassword));
            response = MyHttpClient.Post("http://www.die-staemme.de/index.php?action=login&server_" + world, formParams);
        } while (worldLoginResponseHasErrors(response));

        return true;
    }

    private static List<String> getAvailableWorldsAndSetCryptedPassword() {
        String response;
        do {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            formParams.add(new BasicNameValuePair("user", username));
            formParams.add(new BasicNameValuePair("password", password));
            formParams.add(new BasicNameValuePair("cookie", "true"));
            formParams.add(new BasicNameValuePair("clear", "true"));
            response = MyHttpClient.Post("http://www.die-staemme.de/index.php?action=login&show_server_selection=1", formParams);
            //region Region: response
        /* response with correct information: (without line break)
        {"res":"<form action=\"index.php?action=login\" method=\"post\" class=\"server-form\" id=\"server_select_list\">\n\n
        \t<input name=\"user\" type=\"hidden\" value=\"...Zack...\" \/>\t<input name=\"password\" type=\"hidden\" value=\"3941b807ed046edb0c019de26cde8bf8ad528635\" \/>\n
        \t<div id=\"active_server\">\n
        \t\t\t\t\t<p class=\"pseudo-heading\">Auf welcher Welt willst du dich einloggen?<\/p>\n
        \t\t\t\t\t\t\t<div class=\"clearfix\">\n
        \t\t\t\t\t\t<a href=\"#\" onclick=\"return Index.submit_login('server_de102');\">\n
        \t\t\t\t<span class=\"world_button_active\">Welt 102<\/span>\n
        \t\t\t<\/a>\n\t\t\t\t\t\t<\/div>\n\t\t\n\t\t\t\t\t<div class=\"clearfix\">\n
        \t\t\t<a href=\"#\" onclick=\"return Index.submit_login('server_de104');\">\n
        \t\t\t\t<span class=\"world_button_inactive\">Welt 104<\/span>\n\t\t\t<\/a>\n\t\t\t<\/div>\n
        \t\t\n\t\t\t\t\t<p class=\"pseudo-heading\" id=\"show_all_server\">\n
        \t\t\t\t<a href=\"#\" onclick=\"$('#show_all_server').hide();$('#inactive_server_list').show();return false\">Zeige alle Welten<\/a>\n
        \t\t\t<\/p>\n\t\t    <\/div>\n\n    \t<div id=\"inactive_server_list\" class=\"clearfix\" style=\"display:none;\">\n
        \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t
        \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t
        \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t
        \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de91')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 91<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de93')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 93<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de95')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 95<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de96')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 96<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de97')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 97<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de98')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 98<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de99')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 99<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de100')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 100<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de101')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 101<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t
        <a href=\"#\" onclick=\"return Index.submit_login('server_de103')\">\n\t\t\t\t<span class=\"world_button_inactive\">Welt 103<\/span>\n\t\t\t<\/a>\n\t\t\t\t\t\t\t\t\t\t\t\t\t
        \t\t\t\t\t\t\t\t\t\t\t\t\t<\/div>\n\t\n\t\n<\/form>\n\n<script type=\"text\/javascript\">\n    GAPageTracking.track({ page_identifier : \"login_world_selection\"});\n<\/script>\n"}
        */
            //endregion
        } while (availableWorldsResponseHasErrors(response));

        // Get cryptedPassword from response
        response = response.substring(response.indexOf("<input name=\\\"password\\\""));
        response = response.substring(response.indexOf("value") + 8);
        cryptedPassword = response.substring(0, response.indexOf("\\"));

        // Get available worlds
        List<String> worlds = new ArrayList<String>();
        while (response.contains("world_button_active")) {
            response = response.substring(response.indexOf("world_button_active") + 22);
            if(response.substring(0, response.indexOf("<")).contains("Welt"))
                worlds.add(response.substring(0, response.indexOf("<")));
        }

        if (worlds.isEmpty()) {
            System.out.println("You don't play on any world. Please start a new one manually.");
            System.exit(0);
        }

        return worlds;
    }

    private static boolean availableWorldsResponseHasErrors(String response) {
        if (response.contains("error")) {
            /*
            {"error":"Account nicht vorhanden"}

            {"error":"Passwort ung\u00fcltig"}
            */
            response = response.substring(10);
            response = response.replace("\\u00fc", "ü");

            if (response.substring(0, response.indexOf("\"")).equals("Account nicht vorhanden")) {
                System.out.println("Your username does not exist.");
                username = readUsernameFromConsole();
            } else if (response.substring(0, response.indexOf("\"")).equals("Passwort ungültig")) {
                System.out.println("Your password is not valid.");
                password = readPasswordFromConsole();
            }
            return true;
        } else
            return false;
    }

    private static boolean worldLoginResponseHasErrors(String response) {
        if (response.contains("<div class=\"error\">")) {
            //<div class="error">Account nicht vorhanden</div>
            response = response.substring(response.indexOf("<div class=\"error\">") + 19);

            if (response.substring(0, response.indexOf("<")).equals("Account nicht vorhanden")) {
                System.out.println("Your username does not exist.");
                username = readUsernameFromConsole();
            } else {
                LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unhandled error while logging in: " + response.substring(0, response.indexOf("<")), "worldLoginResponseHasErrors(String)", "TWLoginManager");
            }
            return true;
        } else if (response.contains("Passwort ungültig")) {
            System.out.println("Your password is not valid.");
            password = readPasswordFromConsole();

            // Updates crpyted Password
            getAvailableWorldsAndSetCryptedPassword();

            return true;
        } else
            return false;
    }

    private static String convertWorldToTWWorld(String languageWorld) {
        languageWorld = "de" + languageWorld.substring(languageWorld.indexOf(" ") + 1);
        return languageWorld;
    }

    private static boolean isTWWorldString(String TWWorldString) {
        return TWWorldString.matches("^de\\d+$");
    }

    private static String readUsernameFromConsole() {
        String returnValue = "";
        Console console = System.console();
        if (console == null) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unable to fetch console.", "readUsernameFromConsole()", "TWLoginManager");
        } else
            while (returnValue.equals("")) {
                System.out.print("Please enter a valid username: ");
                returnValue = console.readLine();
            }
        return returnValue;
    }

    private static String readPasswordFromConsole() {
        String returnValue = "";
        Console console = System.console();
        if (console == null) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unable to fetch console.", "readPasswordFromConsole()", "TWLoginManager");
        } else
            while (returnValue.equals("")) {
                System.out.print("Please enter your password: ");
                returnValue = console.readLine();
            }
        return returnValue;
    }

    private static String readWorldFromConsole(String exampleWorld) {
        String returnValue = "";
        Console console = System.console();
        if (console == null) {
            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "Unable to fetch console.", "readWorldFromConsole()", "TWLoginManager");
        } else
            while (returnValue.equals("")) {
                if (exampleWorld.equals(""))
                    System.out.print("Please enter your world (e.g. Welt 107): ");
                else
                    System.out.print("Please enter your world (e.g. " + exampleWorld + "): ");
                returnValue = console.readLine();
            }
        return returnValue;
    }
}
