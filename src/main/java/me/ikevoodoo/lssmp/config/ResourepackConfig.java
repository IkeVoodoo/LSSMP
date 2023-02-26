package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config.annotations.Config;

@Config("resourcepack.yml")
public class ResourepackConfig {

    public static boolean enabled = true;
    public static String url = "https://raw.githubusercontent.com/IkeVoodoo/LSSMP/v3/HeartPack.zip";

    public static String getUrl() {
        return url == null ? "https://raw.githubusercontent.com/IkeVoodoo/LSSMP/v3/HeartPack.zip" : url;
    }

}
