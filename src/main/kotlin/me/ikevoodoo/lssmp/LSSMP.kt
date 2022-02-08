package me.ikevoodoo.lssmp

import me.ikevoodoo.lssmp.TranslationManager.Companion.addTranslation
import me.ikevoodoo.lssmp.commands.*
import me.ikevoodoo.lssmp.commands.completion.*
import me.ikevoodoo.lssmp.listeners.PlayerListener
import me.ikevoodoo.lssmp.storage.AbstractStorage
import me.ikevoodoo.lssmp.storage.SimpleStorage
import me.ikevoodoo.lssmp.storage.TranslationStorage
import me.ikevoodoo.lssmp.utils.EliminationUtils.Companion.tryRevive
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.color
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.parseRecipe
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.plus
import me.ikevoodoo.lssmp.utils.ExtensionUtils.Companion.reset
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.getCustomModelData
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setCustomModelData
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setIdentifier
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setLore
import me.ikevoodoo.lssmp.utils.ItemUtils.Companion.setName
import org.bstats.bukkit.Metrics
import org.bukkit.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.*

class LSSMP: JavaPlugin() {

    companion object {
        lateinit var ELIMINATION_STORAGE: AbstractStorage
        lateinit var TARGET_STORAGE: AbstractStorage
        lateinit var BAN_TIMES: AbstractStorage
        lateinit var PLAYER_HEALTH_STORAGE: AbstractStorage
        lateinit var TO_ELIMINATE: AbstractStorage
        lateinit var INSTANCE: LSSMP
        lateinit var HEART_ITEM: ItemStack
        lateinit var HEART_KEY: NamespacedKey
        lateinit var HEART_CONFIG: FileConfiguration
        val CONNECTED_IPS: MutableMap<String, Int> = mutableMapOf()
        val PREFIX = ChatColor.DARK_AQUA.toString() + "[" + ChatColor.AQUA + "LSSMP" + ChatColor.DARK_AQUA + "] " + ChatColor.RESET
    }

    override fun onEnable() {
        INSTANCE = this
        Bukkit.getPluginManager().registerEvents(PlayerListener(), this)
        ELIMINATION_STORAGE = SimpleStorage(dataFolder.absolutePath + "/data/eliminations.ls")
        TARGET_STORAGE = SimpleStorage(dataFolder.absolutePath + "/data/targets.ls")
        BAN_TIMES = SimpleStorage(dataFolder.absolutePath + "/data/banTimes.ls")
        PLAYER_HEALTH_STORAGE = SimpleStorage(dataFolder.absolutePath + "/data/playerHealth.ls")
        TO_ELIMINATE = SimpleStorage(dataFolder.absolutePath + "/data/toEliminate.ls")

        Metrics(this, 12177)

        getCommand("lsreload")?.setExecutor(ReloadCommand())
        getCommand("lswithdraw")?.setExecutor(WithdrawCommand())
        getCommand("lsreset")?.setExecutor(ResetCommand())
        getCommand("lsrevive")?.setExecutor(ReviveCommand())
        getCommand("lshealth")?.setExecutor(HealthCommand())
        getCommand("lslanguage")?.setExecutor(LanguageCommand())
        getCommand("lsversion")?.setExecutor(VersionCommand())
        getCommand("lschangelog")?.setExecutor(ChangelogCommand())
        getCommand("lseliminate")?.setExecutor(EliminateCommand())

        getCommand("lsreset")?.tabCompleter = ResetCompleter()
        getCommand("lsrevive")?.tabCompleter = ReviveCompleter()
        getCommand("lshealth")?.tabCompleter = HealthCompleter()
        getCommand("lslanguage")?.tabCompleter = LanguageCompleter()
        getCommand("lseliminate")?.tabCompleter = EliminateCompleter()

        val toRemove = mutableListOf<String>()

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
            toRemove.clear()
            BAN_TIMES.getAllKeys().forEach {
                BAN_TIMES[it] = BAN_TIMES[it].toString().toLong() - 5.toLong()
                if (BAN_TIMES[it].toString().toLong() <= 0) {
                    toRemove.add(it)
                }
            }
            BAN_TIMES.save()
            toRemove.forEach {
                val player = Bukkit.getOfflinePlayer(UUID.fromString(it))
                player.tryRevive()
                if(player.isOnline) player.player?.reset()
                else PLAYER_HEALTH_STORAGE[player.uniqueId.toString()] = "RESET"
            }
            if(!config.getBoolean("elimination.bans.shouldBan")
                && config.getBoolean("elimination.spectate.shouldSpectate")
                && !config.getBoolean("elimination.spectate.followKiller")) {
                for(id in ELIMINATION_STORAGE) {
                    val player = Bukkit.getOfflinePlayer(UUID.fromString(id.toString()))
                    if(!player.isOnline) continue

                    player as Player

                    if(player.gameMode != GameMode.SPECTATOR) player.gameMode = GameMode.SPECTATOR

                    if(!TARGET_STORAGE.contains(id.toString())) continue

                    val target = Bukkit.getOfflinePlayer(UUID.fromString(TARGET_STORAGE[id.toString()].toString()))

                    if(!target.isOnline) {
                        player.kickPlayer(config.getString("elimination.spectate.killerNotOnline")?.color())
                        continue
                    }

                    target as Player

                    player.spectatorTarget = target.player
                    player.teleport(target.location)
                }
            }
        }, 0, 5)
/*
        if(!File(dataFolder.absolutePath + "/config.yml").exists()) {
            config.set("elimination.environmentStealsHearts", false)
            config.set("elimination.healthScale", 1.0)
            config.set("elimination.environmentHealthScale", 1.0)
            config.set("elimination.maxHearts", 10)
            config.set("elimination.defaultHearts", 10)
            config.set("elimination.useMaxHealth", false)
            config.set("elimination.scaleHealth", true)
            config.set("elimination.shouldEliminate", true)

            config.set("elimination.spectate.shouldSpectate", true)
            config.set("elimination.spectate.followKiller", true)
            config.set("elimination.spectate.killerNotOnline", "&cYour killer is not online so you are not allowed to spectate!")

            config.set("elimination.bans.shouldBan", true)
            config.set("elimination.bans.usingBanTime", false)
            config.set("elimination.bans.broadcastBan", false)
            config.set("elimination.bans.banMessage", "&cYou have been banned due to loosing all of your hearts, your last killer was &4%player%")
            config.set("elimination.bans.broadcastMessage", "&c%player% has lost all of it's hearts and has been banned.")
            config.set("elimination.bans.banTime", "00:00:00.0000")

            config.set("elimination.allowedWorlds", listOf("world", "world_nether", "world_the_end"))

            config.set("antiAlts.altsAllowed", false)
            config.set("antiAlts.maxAlts", 0)

            config.set("events.revived.maxHealth", "RESET")
            config.set("events.revived.commands", listOf("do something"))
            config.set("events.eliminated.commands", listOf("do something"))
            config.set("events.killed.commands", listOf("do something"))

            config.set("items.heart.name", "&c❤ &fExtra heart.")
            config.set("items.heart.lore", listOf("Gives you an extra heart!"))

            saveConfig()
        }*/

        saveDefaultConfig()

        HEART_KEY = NamespacedKey(this, "lssmp_heart_item")
        HEART_ITEM = ItemStack(Material.RED_DYE)
            .setName(((config.getString("items.heart.name")?.color()
                ?: (ChatColor.RED + "❤ " + ChatColor.WHITE + "Extra heart."))))
            .setIdentifier(HEART_KEY)
            .setCustomModelData(931)
            .setLore(config.getStringList("items.heart.lore").ifEmpty { listOf("Gives you an extra heart!") })

        val heartRecipeFile = File(dataFolder.absolutePath + "/heartRecipe.yml")
        if(!heartRecipeFile.exists()) {
            heartRecipeFile.createNewFile()
            HEART_CONFIG = YamlConfiguration()
            HEART_CONFIG["options.enabled"] = true
            HEART_CONFIG["options.outputAmount"] = 1
            HEART_CONFIG["options.shaped"] = true


            HEART_CONFIG["recipe.slots.1.item"] = "gold block"
            HEART_CONFIG["recipe.slots.2.item"] = "bone meal"
            HEART_CONFIG["recipe.slots.3.item"] = "gold block"

            HEART_CONFIG["recipe.slots.4.item"] = "obsidian"
            HEART_CONFIG["recipe.slots.5.item"] = "fermented spider eye"
            HEART_CONFIG["recipe.slots.6.item"] = "obsidian"

            HEART_CONFIG["recipe.slots.7.item"] = "diamond block"
            HEART_CONFIG["recipe.slots.8.item"] = "diamond block"
            HEART_CONFIG["recipe.slots.9.item"] = "diamond block"

            HEART_CONFIG.save(heartRecipeFile)
        } else HEART_CONFIG = YamlConfiguration.loadConfiguration(heartRecipeFile)

        addHeartRecipe()

        val en = TranslationStorage(dataFolder.absolutePath + "/languages/en_us.lst", "en_us", "English")
        en["environment.displayName"] = "Environment"
        en["reload"] = "Reloaded!"
        en["elimination.ban.message"] = "&cYou have been banned as you have been eliminated!"
        en["commands.withdraw.message"] = "&cYou have withdrawn %amount% hearts!"
        en["antiAlts.kickMessage"] = "&cYou have been kicked as you have been detected as an alt!"
        en["language.not-found"] = "&cThe language %language% does not exist!"
        en["language.set"] = "&aThe forced language has been set to %language%!"
        en["language.cleared"] = "&aThe forced language has been cleared!"
        en["commands.version.message"] = "&6Current version: %version-color%%version% %status%"
        en["commands.version.status.outdated"] = "OUTDATED"
        en["commands.version.status.latest"] = "LATEST"
        en["commands.errors.player-not-found"] = "&cThe player %player% does not exist!"
        en["commands.changelog.message"] = "&6Changelog:"
        en["commands.no-args"] = "&cYou must specify at least player!"
        en["commands.withdraw.no-perms"]  = "&cYou are not permitted to use /lswithdraw!"
        en["commands.version.no-perms"]   = "&cYou are not permitted to use /lsversion!"
        en["commands.revive.no-perms"]    = "&cYou are not permitted to use /lsrevive!"
        en["commands.reset.no-perms"]     = "&cYou are not permitted to use /lsreset!"
        en["commands.reload.no-perms"]    = "&cYou are not permitted to use /lsreload!"
        en["commands.language.no-perms"]  = "&cYou are not permitted to use /lslanguage!"
        en["commands.health.no-perms"]    = "&cYou are not permitted to use /lshealth!"
        en["commands.eliminate.no-perms"] = "&cYou are not permitted to use /lseliminate!"
        en["commands.changelog.no-perms"] = "&cYou are not permitted to use /lschangelog!"

        val it = TranslationStorage(dataFolder.absolutePath + "/languages/it_it.lst", "it_it", "Italian")
        it["environment.displayName"] = "Ambiente"
        it["reload"] = "Ricaricato!"
        it["elimination.ban.message"] = "&cSei stato/a bannato/a perchè sei stato/a eliminato/a!"
        it["commands.withdraw.message"] = "&cHai prelevato %amount% quori!"
        it["antiAlts.kickMessage"] = "&cSei stato/a kickato/a perchè sei stato/a rilevato/a come alt!"
        it["language.not-found"] = "&cLa lingua %language% non esiste!"
        it["language.set"] = "&aLa lingua forzata è stata impostata a %language%!"
        it["language.cleared"] = "&aLa lingua forzata è stata rimossa!"
        it["commands.version.message"] = "&6Versione corrente: %version-color%%version% %status%"
        it["commands.version.status.outdated"] = "VECCHIA"
        it["commands.version.status.latest"] = "NUOVA"
        it["commands.errors.player-not-found"] = "&cIl giocatore %player% non esiste!"
        it["commands.changelog.message"] = "&6Cambiamenti:"
        it["commands.no-args"] = "&cDevi specificare almeno un giocatore!"
        en["commands.withdraw.no-perms"]  = "&cNon sei permesso/a di usare /lswithdraw!"
        en["commands.version.no-perms"]   = "&cNon sei permesso/a di usare /lsversion!"
        en["commands.revive.no-perms"]    = "&cNon sei permesso/a di usare /lsrevive!"
        en["commands.reset.no-perms"]     = "&cNon sei permesso/a di usare /lsreset!"
        en["commands.reload.no-perms"]    = "&cNon sei permesso/a di usare /lsreload!"
        en["commands.language.no-perms"]  = "&cNon sei permesso/a di usare /lslanguage!"
        en["commands.health.no-perms"]    = "&cNon sei permesso/a di usare /lshealth!"
        en["commands.eliminate.no-perms"] = "&cNon sei permesso/a di usare /lseliminate!"
        en["commands.changelog.no-perms"] = "&cNon sei permesso/a di usare /lschangelog!"

        val nl = TranslationStorage(dataFolder.absolutePath + "/languages/nl_nl.lst", "nl_nl", "Dutch")
        nl["environment.displayName"] = "Omgeving"
        nl["reload"] = "Herladen!"
        nl["elimination.ban.message"] = "&cJe bent verbannen omdat je verloren hebt!"
        nl["commands.withdraw.message"] = "&cJe hebt %amount% harten verloren!"
        nl["antiAlts.kickMessage"] = "&cJe bent gekickt omdat je als alt bent gedetecteerd!"
        nl["language.not-found"] = "&cDe taal %language% bestaat niet!"
        nl["language.set"] = "&aDe geforceerde taal is ingesteld op %language%!"
        nl["language.cleared"] = "&aDe geforceerde taal is verwijderd!"
        nl["commands.version.message"] = "&6Huidige versie: %version-color%%version% %status%"
        nl["commands.version.status.outdated"] = "VEROUDERD"
        nl["commands.version.status.latest"] = "NIEUW"
        nl["commands.errors.player-not-found"] = "&cDe speler %player% bestaat niet!"
        nl["commands.changelog.message"] = "&6Wijzigingen:"
        nl["commands.no-args"] = "&cJe moet minimaal één speler opgeven!"

        en.addTranslation()
        it.addTranslation()
        nl.addTranslation()

        checkForUpdates()
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
            checkForUpdates()
        }, 0, (20 * 60 * 5).toLong())
    }

    override fun onDisable() {
        ELIMINATION_STORAGE.save()
        BAN_TIMES.save()
        PLAYER_HEALTH_STORAGE.save()
        TO_ELIMINATE.save()
    }

    fun removeHeartRecipe() {
        server.recipeIterator().let {
            while(it.hasNext()) {
                it.next().let { recipe ->
                    if (recipe != null && recipe.result.getCustomModelData() == HEART_ITEM.getCustomModelData())
                        it.remove()
                }
            }
        }
    }

    fun addHeartRecipe() {
        val recipe = HEART_CONFIG.parseRecipe()
        if(recipe.enabled) {
            if(recipe.shaped) {
                val shaped = ShapedRecipe(HEART_KEY, HEART_ITEM)
                shaped.shape("012", "345", "678")
                for(i in 0 until 9)
                    shaped.setIngredient(i.toString()[0], recipe.slots[i])
                Bukkit.addRecipe(shaped)
            } else {
                val shapeless = ShapelessRecipe(HEART_KEY, HEART_ITEM)
                for(mat in recipe.slots)
                    shapeless.addIngredient(mat)
                Bukkit.addRecipe(shapeless)
            }
        }
    }

    fun reloadHeartRecipe() {
        removeHeartRecipe()
        addHeartRecipe()
    }

    fun checkForUpdates() {
        var inputStream: InputStream = try {
            getInputStream("http://188.34.178.99:8080/LSSMP/version")
        } catch (ignored: IOException) {
            return
        } ?: return
        try {
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                val line = br.readLine()
                if (!line.equals(description.version, ignoreCase = true)) {
                    Bukkit.broadcast(
                        PREFIX + ChatColor.GOLD + "There is a new update available! Current version: "
                                + ChatColor.RED + description.version
                                + ChatColor.GOLD + ", Updated Version: "
                                + ChatColor.GREEN + line,
                        "lssmp.update.checker"
                    )
                    UpdateData.UPDATE_AVAILABLE = true
                    UpdateData.VERSION = line
                } else UpdateData.UPDATE_AVAILABLE = false
            }
        } catch (ignored: Exception) { }
        inputStream = try {
            getInputStream("http://188.34.178.99:8080/LSSMP/changelog")
        } catch (ignored: IOException) {
            return
        } ?: return
        try {
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                val sb = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null)
                    sb.append(line)
                if (sb.toString().isNotEmpty()) {
                    UpdateData.CHANGELOG = sb.toString()
                    UpdateData.CHANGELOG_AVAILABLE = true
                } else UpdateData.CHANGELOG_AVAILABLE = false
            }
        } catch (ignored: Exception) { }
    }

    @Throws(IOException::class)
    private fun getInputStream(website: String): InputStream? {
        val url = URL(website)
        val huc = url.openConnection() as HttpURLConnection
        HttpURLConnection.setFollowRedirects(false)
        huc.connectTimeout = 5 * 1000
        huc.requestMethod = "GET"
        huc.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36"
        )
        huc.connect()
        return try {
            huc.inputStream
        } catch (exception: SocketTimeoutException) {
            null
        }
    }

    fun saveStorage() {
        ELIMINATION_STORAGE.save()
        TARGET_STORAGE.save()
        BAN_TIMES.save()
        PLAYER_HEALTH_STORAGE.save()
        TO_ELIMINATE.save()
    }


}