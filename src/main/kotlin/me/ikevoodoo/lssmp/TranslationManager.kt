package me.ikevoodoo.lssmp

import me.ikevoodoo.lssmp.storage.SimpleStorage
import me.ikevoodoo.lssmp.storage.TranslationStorage
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import me.ikevoodoo.lssmp.utils.ReflectionUtils.Companion.getLanguage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TranslationManager private constructor() {

    private val translations = HashMap<String, TranslationStorage>()

    companion object {
        private val INSTANCE = TranslationManager()
        private var forcedLanguage = ""
        private var TRANSLATION_SETTINGS = SimpleStorage(LSSMP.INSTANCE.dataFolder.absolutePath + "/data/translation_settings.ls")

        init {
            if(TRANSLATION_SETTINGS.contains("forced_language"))
                forcedLanguage = TRANSLATION_SETTINGS["forced_language"] as String
        }

        private val TO_CODE = mutableMapOf<String, String>()

        private fun Player.getTranslations(): TranslationStorage? {
            return INSTANCE.translations[forcedLanguage.ifBlank { getLanguage().lowercase() }] ?: INSTANCE.translations["en_us"]
        }

        fun Player.getTranslation(key: String): String? {
            return getTranslations()?.get(key)?.color()
        }

        fun CommandSender.getTranslation(key: String): String? {
            return if (this is Player) getTranslation(key)
            else INSTANCE.translations["en_us"]?.get(key)?.color()
        }

        fun getTranslation(key: String): String? {
            return INSTANCE.translations[forcedLanguage.ifBlank { "en_us" }]?.get(key)?.color()
        }

        fun TranslationStorage.addTranslation() {
            this.save()
            INSTANCE.translations[language] = this
            TO_CODE[name.lowercase()] = language
        }

        fun setForcedLanguage(language: String?) {
            forcedLanguage = language ?: ""
            if(language != null)
                TRANSLATION_SETTINGS["forced_language"] = language
            else
                TRANSLATION_SETTINGS.delete("forced_language")
            TRANSLATION_SETTINGS.save()
        }

        fun getLanguages(): List<String> {
            return INSTANCE.translations.keys.toMutableList()
        }

        fun getLanguageNames(): MutableList<String> {
            val languages = mutableListOf<String>()
            INSTANCE.translations.forEach {
                languages.add(it.value.name)
            }
            return languages
        }

        fun String.convertLanguage(): String {
            return TO_CODE[this.lowercase()] ?: this
        }

    }

}