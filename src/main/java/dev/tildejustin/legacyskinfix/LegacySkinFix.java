package dev.tildejustin.legacyskinfix;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.spongepowered.include.com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class LegacySkinFix implements ClientModInitializer {
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static Map<Type, String> skins = new HashMap<>();
    public final String UsernameToUUID = "https://api.mojang.com/users/profiles/minecraft/";

    @Override
    public void onInitializeClient() {
        String uuid;
        try {
            uuid = performGetRequest(UsernameToUUID + client.getSession().getUsername());
        } catch (IOException e) {
            System.out.println("could not resolve player UUID");
            return;
        }
        uuid = new Gson().fromJson(new StringReader(uuid), MinecraftProfileUsernameResponse.class).id;

        final String texturesURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;

        String result;
        try {
            result = performGetRequest(texturesURL);
        } catch (IOException e) {
            System.out.println("Could not get profile texture(s)");
            return;
        }
        MinecraftProfilePropertiesResponse.MinecraftProfileProperties properties = new Gson().fromJson(new StringReader(result), MinecraftProfilePropertiesResponse.class).properties[0];
        ProfileTexturesResponse.ProfileTextures profileTextures = new Gson().fromJson(
                new StringReader(new String(Base64.getDecoder().decode(properties.value))),
                ProfileTexturesResponse.class
        ).textures;
        skins.put(Type.SKIN, profileTextures.SKIN.url);
        skins.put(Type.CAPE, profileTextures.CAPE.url);
    }

    protected HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    public String performGetRequest(String url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = createUrlConnection(new URL(url));


        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            return IOUtils.toString(inputStream, Charsets.UTF_8);
        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                return IOUtils.toString(inputStream, Charsets.UTF_8);
            } else {
                throw e;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public enum Type {
        SKIN,
        CAPE
    }
}
