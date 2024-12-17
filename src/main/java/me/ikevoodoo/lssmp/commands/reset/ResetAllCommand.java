package me.ikevoodoo.lssmp.commands.reset;

import me.ikevoodoo.helix.api.commands.CommandExecutionResult;
import me.ikevoodoo.helix.api.commands.HelixCommandParameters;
import me.ikevoodoo.helix.api.commands.arguments.ArgumentList;
import me.ikevoodoo.helix.api.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ResetAllCommand extends ResetCommandBase {

    private final Configuration generalConfiguration;

    public ResetAllCommand(Configuration generalConfiguration) {
        this.generalConfiguration = generalConfiguration;
    }

    @Override
    protected HelixCommandParameters makeParameters() {
        return HelixCommandParameters.create("all");
    }

    @Override
    public CommandExecutionResult handleGenericSender(@NotNull CommandSender sender, @NotNull ArgumentList args) {
        for (var online : Bukkit.getOnlinePlayers()) {
            super.reset(online, this.generalConfiguration);
        }

        sender.sendMessage("§aReset §3" + Bukkit.getOnlinePlayers().size() + "§a players!");

        return CommandExecutionResult.HANDLED;
    }
}
