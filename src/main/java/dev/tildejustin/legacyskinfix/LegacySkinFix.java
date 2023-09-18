package dev.tildejustin.legacyskinfix;

import net.minecraft.client.Minecraft;
import org.spongepowered.include.com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

public class LegacySkinFix {
    public static Minecraft client = Minecraft.getMinecraft();
    public static String skinUrl;
    public static String capeUrl;

    public static void onInitializeClient() {
        final String[] token = client.session.field_1048.split(":");
        final String texturesURL = "https://sessionserver.mojang.com/session/minecraft/profile/"
                + token[token.length - 1].replace("-", "");

        String result;
        try {
            result = performGetRequest(texturesURL);
        } catch (IOException e) {
            System.out.println("Could not get profile texture(s)");
            return;
        }
        MinecraftProfilePropertiesResponse.MinecraftProfileProperties properties =
                new Gson().fromJson(new StringReader(result), MinecraftProfilePropertiesResponse.class).properties[0];
        ProfileTexturesResponse.ProfileTextures profileTextures = new Gson().fromJson(
                new StringReader(new String(Base64.getDecoder().decode(properties.value))),
                ProfileTexturesResponse.class
        ).textures;

        skinUrl = profileTextures.SKIN.url;
        capeUrl = profileTextures.CAPE.url;
    }

    public static Optional<String> getSkin() {
        return Optional.ofNullable(skinUrl);
    }

    public static Optional<String> getCape() {
        return Optional.ofNullable(capeUrl);
    }

    public static String performGetRequest(String url) throws IOException {
        HttpURLConnection connection = createUrlConnection(new URL(url));
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
            if (inputStream != null) {
                return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
            } else {
                throw e;
            }
        } finally {
            if (inputStream != null) inputStream.close();
        }
    }

    protected static HttpURLConnection createUrlConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }
}
