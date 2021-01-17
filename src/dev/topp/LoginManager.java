package dev.topp;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

public class LoginManager {

    private static String username = "";
    private static String cryptedPassword;

    public static List<String> loginToAccount(String username, String password, WebClient webClient) {
        List<String> availableWorlds = new ArrayList<>();

        // Fake purpose
        webClient.Get("http://www.die-staemme.de/");

        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("user", username));
        formParams.add(new BasicNameValuePair("password", password));
        formParams.add(new BasicNameValuePair("cookie", "true"));
        formParams.add(new BasicNameValuePair("clear", "true"));
        String response = webClient.Post("http://www.die-staemme.de/index.php?action=login&show_server_selection=1", formParams);

        StringSelection stringSelection = new StringSelection(response);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);

        if (response.contains("error")) {
            /*
            {"error":"Account nicht vorhanden"}

            {"error":"Passwort ung\u00fcltig"}
            */
            response = response.substring(10);
            response = response.replace("\\u00fc", "ü");
            response = response.substring(0, response.indexOf("\""));

            LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "WebServer response: " + response, "loginToAccount(String, String, WebClient)", "LoginManager");
        } else {
            //region Region: response with correct information: (without line break)
                /*
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

            LoggingManager.log(LoggingManager.LoggingLevel.INFO, "LogIn successful.", "loginToAccount(String, String, WebClient)", "LoginManager");

            response = response.substring(response.indexOf("<input name=\\\"password\\\""));
            response = response.substring(response.indexOf("value") + 8);
            cryptedPassword = response.substring(0, response.indexOf("\\"));
            LoginManager.username = username;

            // Get available worlds
            while (response.contains("world_button_active")) {
                response = response.substring(response.indexOf("world_button_active") + 22);
                if(response.substring(0, response.indexOf("<")).contains("Welt")) {
                    availableWorlds.add(response.substring(0, response.indexOf("<")));
                    LoggingManager.log(LoggingManager.LoggingLevel.DEBUG, "AvailableWorld: " + response.substring(0, response.indexOf("<")), "loginToAccount(String, String, WebClient)", "LoginManager");
                }
            }

            if (availableWorlds.isEmpty())
                LoggingManager.log(LoggingManager.LoggingLevel.CRITICAL, "You don't play on any world. Please start a new one manually.", "loginToAccount(String, String, WebClient)", "LoginManager");
        }

        return availableWorlds;
    }
}
