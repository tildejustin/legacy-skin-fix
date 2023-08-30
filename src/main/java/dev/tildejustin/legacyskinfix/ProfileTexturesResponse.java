package dev.tildejustin.legacyskinfix;

public class ProfileTexturesResponse {
    ProfileTextures textures;

    public static class ProfileTextures {
        public ProfileTexture SKIN;
        public ProfileTexture CAPE;

        public static class ProfileTexture {
            public String url;
        }
    }
}
