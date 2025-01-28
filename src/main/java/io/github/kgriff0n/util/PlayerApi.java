package io.github.kgriff0n.util;

import io.github.kgriff0n.PlayerSearch;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PlayerApi {

    public static JSONObject getProfileFromName(String username) {
        PlayerSearch.LOGGER.info("Request sent to Mojang API...");
        JSONObject profile;
        try {
            profile = getJSONObject("https://api.mojang.com/users/profiles/minecraft/" + username);
        } catch (Exception e) {
            return null;
        }
        return profile;
    }

    public static JSONObject getProfileFromUUID(String uuid) {
        PlayerSearch.LOGGER.info("Request sent to Mojang API...");
        JSONObject profile;
        try {
            profile = getJSONObject("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
        } catch (Exception e) {
            return null;
        }
        return profile;
    }

    public static JSONArray getNameHistoryLaby(String uuid) {
        PlayerSearch.LOGGER.info("Request sent to Laby API...");
        JSONArray array;
        try {
            array = (JSONArray) new JSONParser().parse(Unirest.get("https://laby.net/api/user/%s/get-names".formatted(uuid)).header("User-Agent", "Mozilla/5.0 (compatible; PlayerSearch; +https://modrinth.com/mod/player-search)").asString().getBody());
        } catch (ParseException | UnirestException e) {
            throw new RuntimeException(e);
        }
        return array;
    }

    public static JSONArray getNameHistoryCrafty(String uuid) {
        PlayerSearch.LOGGER.info("Request sent to Crafty API...");
        JSONArray array;
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(Unirest.get("https://api.crafty.gg/api/v2/players/%s".formatted(uuid)).header("User-Agent", "Mozilla/5.0 (compatible; PlayerSearch; +https://modrinth.com/mod/player-search)").asString().getBody());
            JSONObject dataObject = (JSONObject) jsonObject.get("data");
            array = (JSONArray) dataObject.get("usernames");
        } catch (ParseException | UnirestException e) {
            throw new RuntimeException(e);
        }
        return reverseJSONArray(array);
    }

    private static JSONObject getJSONObject(String url)
    {
        JSONObject obj;

        try
        {
            obj = (JSONObject) new JSONParser().parse(Unirest.get(url).asString().getBody());
            String errorMessage = (String) (obj.get("errorMessage"));
            if (errorMessage != null)
            {
                throw new RuntimeException(errorMessage);
            }
        }
        catch (ParseException | UnirestException e)
        {
            throw new RuntimeException(e);
        }

        return obj;
    }

    private static JSONArray reverseJSONArray(JSONArray array) {
        JSONArray reversedArray = new JSONArray();
        for (int i = array.size() - 1; i >= 0; i--) {
            reversedArray.add(array.get(i));
        }
        return reversedArray;
    }
}
