package me.ikevoodoo.lssmp.config;


import me.ikevoodoo.smpcore.config2.annotations.Config;
import me.ikevoodoo.smpcore.config2.annotations.data.Getter;

@Config(value = "resourcepack", hidden = true)
public interface ResourepackConfig {

   @Getter(target = "enabled")
   default boolean isEnabled() {
       return true;
   }
   @Getter
   default String url() {
       return "https://raw.githubusercontent.com/IkeVoodoo/LSSMP/v3/HeartPack.zip";
   }

}
