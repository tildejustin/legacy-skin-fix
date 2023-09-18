package dev.tildejustin.legacyskinfix;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
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
import java.util.Optional;

public class LegacySkinFix implements ClientModInitializer {
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static String skinUrl;
    public static String capeUrl;

    @Override
    public void onInitializeClient() {
        final String texturesURL = "https://sessionserver.mojang.com/session/minecraft/profile/"
                + client.getSession().getUuid().replace("-", "");

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

    protected static HttpURLConnection createUrlConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }
}
