package me.eccentric_nz.plugins.autocolour;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AutoColourCommands implements CommandExecutor {

    private AutoColour plugin;
    AutoColourDatabase service = AutoColourDatabase.getInstance();
    String code;

    public AutoColourCommands(AutoColour plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // If the player typed /autocolour then do the following...
        // check there is the right number of arguments
        if (cmd.getName().equalsIgnoreCase("autocolour")) {
            if (args.length == 0) {
                sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + " Colour List");
                int i = 0;
                for (String c : AutoColourConstants.validColours) {
                    String colour = AutoColourConstants.validColours[i];
                    code = AutoColourConstants.validCodes[i];
                    String message = "¤" + code + colour + "¤r | &" + code;
                    sender.sendMessage(message);
                    i++;
                }
                return true;
            }
            if (args.length > 0 && args.length < 2) {
                sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + " Not enough command arguments!");
                return false;
            }
            if (args.length == 2) {
                String colour = args[1].toLowerCase();
                if (!Arrays.asList(AutoColourConstants.validColours).contains(colour) && !colour.startsWith("&")) {
                    sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + " Not a valid colour or format!");
                    return false;
                }
                if (colour.startsWith("&")) {
                    String codestr = colour.substring(1, 2).toLowerCase();
                    if (colour.length() > 2 || !Arrays.asList(AutoColourConstants.validCodes).contains(codestr)) {
                        sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + " Not a valid colour or format code!");
                        return false;
                    }
                    code = codestr;
                } else {
                    int i = 0;
                    for (String c : AutoColourConstants.validColours) {
                        if (c.equalsIgnoreCase(colour)) {
                            code = AutoColourConstants.validCodes[i];
                            break;
                        }
                        i++;
                    }
                }
                String[] find = new String[3];
                find[0] = args[0].toLowerCase();
                find[1] = args[0].toUpperCase();
                StringBuilder sb = new StringBuilder(find[0]);
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                find[2] = sb.toString();
                try {
                    Connection connection = service.getConnection();
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO autocolour (find, colour) VALUES (?,?)");
                    for (String f : find) {
                        statement.setString(1, f);
                        statement.setString(2, code);
                        statement.executeUpdate();
                    }
                    statement.close();
                } catch (SQLException e) {
                    System.err.println(AutoColourConstants.MY_PLUGIN_NAME + "Couldn't get substitutions: " + e);
                }
                AutoColourHighlighter highlighter = new AutoColourHighlighter(plugin);
                plugin.replace = highlighter.buildSubstitutions();

                sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + " Successfully added word!");
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("acremove")) {
            if (args.length == 0) {
                sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + " You must specify an AutoColour word!");
                return false;
            }
            try {
                Connection connection = service.getConnection();
                Statement statement = connection.createStatement();
                String wordStr = args[0].toLowerCase();
                String queryChkWord = "SELECT find FROM autocolour WHERE find = '" + wordStr + "'";
                ResultSet rsWord = statement.executeQuery(queryChkWord);
                if (!rsWord.isBeforeFirst()) {
                    sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + " Could not find that word!");
                    return false;
                }
                String[] word = new String[3];
                word[0] = wordStr;
                word[1] = args[0].toUpperCase();
                StringBuilder sb = new StringBuilder(wordStr);
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                word[2] = sb.toString();
                for (String w : word) {
                    String queryWord = "DELETE FROM autocolour WHERE find = '" + w + "'";
                    statement.executeUpdate(queryWord);
                }
                rsWord.close();
                statement.close();
            } catch (SQLException e) {
                System.err.println(AutoColourConstants.MY_PLUGIN_NAME + "Couldn't get substitutions: " + e);
            }
            AutoColourHighlighter highlighter = new AutoColourHighlighter(plugin);
            plugin.replace = highlighter.buildSubstitutions();

            sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + "Successfully removed word!");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("aclist")) {
            try {
                Connection connection = service.getConnection();
                Statement statement = connection.createStatement();
                String queryList = "SELECT * FROM autocolour";
                ResultSet rsList = statement.executeQuery(queryList);
                if (!rsList.isBeforeFirst()) {
                    sender.sendMessage(AutoColourConstants.MY_PLUGIN_NAME + "There are word in the list yet!");
                    return false;
                }
                String compare = "";
                List<String> unique = new ArrayList<String>();
                while (rsList.next()) {
                    String word = rsList.getString("find").toLowerCase();
                    if (!word.equals(compare)) {
                        unique.add("¤"+rsList.getString("colour")+word);
                        compare = word;
                    }
                }
                rsList.close();
                statement.close();
                for (String u : unique) {
                    sender.sendMessage(u);
                }
            } catch (SQLException e) {
                System.err.println(AutoColourConstants.MY_PLUGIN_NAME + "Couldn't get substitutions: " + e);
            }
            return true;
        }
        return false;
    }
}
