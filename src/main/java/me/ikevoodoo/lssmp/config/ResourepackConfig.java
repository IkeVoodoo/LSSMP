package me.ikevoodoo.lssmp.config;

import me.ikevoodoo.smpcore.config.annotations.Config;

@Config("resourcepack.yml")
public class ResourepackConfig {

    public static boolean enabled = true;
    public static String url = "https://www.dropbox.com/s/wkvjcmz296je6v3/HeartPack.zip?dl=1";

    public static String getUrl() {
        return url == null ? "https://www.dropbox.com/s/wkvjcmz296je6v3/HeartPack.zip?dl=1" : url;
    }

}
