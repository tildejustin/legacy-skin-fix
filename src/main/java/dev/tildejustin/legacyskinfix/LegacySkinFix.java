package dev.tildejustin.legacyskinfix;

import net.minecraft.client.Minecraft;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.spongepowered.include.com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class LegacySkinFix {
    public static Minecraft client = Minecraft.getMinecraft();
    public static Map<Type, String> skins = new HashMap<>();

    public static void initialize() {
        final String texturesURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + client.session.field_1048.split(":")[client.session.field_1048.split(":").length - 1].replace("-", "");

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

    protected static HttpURLConnection createUrlConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    public static String performGetRequest(String url) throws IOException {
        HttpURLConnection connection = createUrlConnection(new URL(url));


        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
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
