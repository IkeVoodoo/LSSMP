package me.ikevoodoo.lssmp.language;

import dev.refinedtech.configlang.ConfigLang;
import dev.refinedtech.configlang.ConfigSection;
import dev.refinedtech.configlang.ConfigStructure;
import dev.refinedtech.configlang.scope.Scope;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.senders.CustomSender;
import me.ikevoodoo.smpcore.senders.SenderBuilder;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import me.ikevoodoo.smpcore.utils.ClassUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Language extends PluginProvider {

    private static final Pattern VAR_PATTERN = Pattern.compile("\\{\\{(.+?)\\}\\}");

    private final ConfigLang lang;

    public Language(SMPPlugin plugin) {
        super(plugin);
        lang = new ConfigLang();
        lang.setErrorLogger(getPlugin().getLogger());
        lang.submit(new ConfigStructure("execute",
                ConfigStructure.keyStructure("command"),
                ConfigStructure.keyStructure("as")) {

            @Override
            protected Object run(ConfigSection section, Scope scope, Object... args) {
                String command = String.valueOf(getVar(section, "command", scope, args));
                String sender = String.valueOf(getVar(section, "as", scope, args));
                boolean logToSender = section.<Boolean>get("log", true);

                CommandSender commandSender = Bukkit.getConsoleSender();

                if (sender.toLowerCase(Locale.ROOT).equalsIgnoreCase("console"))
                    commandSender = Bukkit.getConsoleSender();

                Player player = Bukkit.getPlayer(sender);
                if (player != null)
                    commandSender = player;

                if (logToSender)
                    Bukkit.dispatchCommand(commandSender, command);
                else
                    Bukkit.dispatchCommand(SenderBuilder.createNewSender(CustomSender.as().noLog().sender(commandSender)), command);

                return null;
            }
        });
        lang.submit(new ConfigStructure("condition", true) {
            @Override
            public boolean returnsData() {
                return true;
            }

            @Override
            protected Object run(ConfigSection section, Scope scope, Object... objects) {
                AtomicBoolean res = new AtomicBoolean(true);

                lang.executeChildren(section, scope).forEach(result -> {
                    if (result instanceof Boolean bool)
                        res.set(res.get() && bool);
                });

                return res.get();
            }
        });
        lang.submit(new ConfigStructure("operation",
                ConfigStructure.keyStructure("left"),
                ConfigStructure.keyStructure("operator"),
                ConfigStructure.keyStructure("right")) {
            @Override
            public boolean returnsData() {
                return true;
            }

            @Override
            protected Object run(ConfigSection section, Scope scope, Object... args) {
                Object left = getVar(section, "left", scope, args);
                Object right = getVar(section, "right", scope, args);

                String operator = String.valueOf(getVar(section, "operator", scope, args));

                if (left == null || right == null) return false;

                if (operator.equalsIgnoreCase("==")) {
                    return left.equals(right);
                }

                if (operator.equalsIgnoreCase("!=")) {
                    return !left.equals(right);
                }

                if (operator.equalsIgnoreCase("===")) {
                    return (left.getClass().isAssignableFrom(right.getClass()) || right.getClass().isAssignableFrom(left.getClass()))
                            && left.equals(right);
                }

                if (operator.equalsIgnoreCase("!==")) {
                    return (left.getClass().isAssignableFrom(right.getClass()) || right.getClass().isAssignableFrom(left.getClass()))
                            && !left.equals(right);
                }

                if (ClassUtils.is(left, Number.class) && ClassUtils.is(right, Number.class)) {
                    double leftDouble = ((Number)left).doubleValue();
                    double rightDouble = ((Number)right).doubleValue();

                    int leftInt = ((Number)left).intValue();
                    int rightInt = ((Number)right).intValue();

                    return switch (operator) {
                        case "*" -> leftDouble * rightDouble;
                        case "/" -> leftDouble / rightDouble;
                        case "%" -> leftDouble % rightDouble;
                        case "+" -> leftDouble + rightDouble;
                        case "-" -> leftDouble - rightDouble;
                        case "<<" -> leftInt << rightInt;
                        case ">>" -> leftInt >> rightInt;
                        case ">>>" -> leftInt >>> rightInt;
                        case "<" -> leftDouble < rightDouble;
                        case ">" -> leftDouble > rightDouble;
                        case "<=" -> leftDouble <= rightDouble;
                        case ">=" -> leftDouble >= rightDouble;
                        default -> false;
                    };
                }

                if (ClassUtils.is(left, Boolean.class) && ClassUtils.is(right, Boolean.class)) {
                    return switch (operator) {
                        case "&" -> ((Boolean) left) & ((Boolean) right);
                        case "^" -> ((Boolean) left) ^ ((Boolean) right);
                        case "|" -> ((Boolean) left) | ((Boolean) right);
                        case "&&" -> ((Boolean) left) && ((Boolean) right);
                        case "||" -> ((Boolean) left) || ((Boolean) right);
                        default -> false;
                    };
                }


                if (operator.equalsIgnoreCase("+")) {
                    return String.valueOf(left) + right;
                }

                return false;
            }
        });

        lang.submit(new ConfigStructure("if",
                lang.get("condition").orElseThrow()) {
            @Override
            protected Object run(ConfigSection section, Scope scope, Object... objects) {
                Object res = lang.execute(section.getConfigSection("condition").orElseThrow(), scope, objects);
                if (res instanceof Boolean bool && bool && section.isConfigSection("then")) {
                    lang.executeChildrenRecursive(section.getConfigSection("then").orElseThrow(), scope, objects);
                } else if (section.isConfigSection("else")) {
                    lang.executeChildrenRecursive(section.getConfigSection("else").orElseThrow(), scope, objects);
                }
                return null;
            }
        });
    }

    public void execute(ConfigSection section, Scope scope, Object... args) {
        this.lang.executeChildrenRecursive(section, scope, args);
    }

    private Object getVar(ConfigSection section, String key, Scope scope, Object... args) {
        Object obj = lang.getData(section, key, scope, args);
        if (obj instanceof List<?> list && list.isEmpty())
            obj = section.getObject(key).orElse(null);

        if(obj instanceof String text) {
            Matcher matcher = VAR_PATTERN.matcher(text);
            while (matcher.find()) {
                String var = matcher.group(1);
                Optional<Object> opt = scope.variables().parseVariableRaw(var);
                if (opt.isPresent()) {
                    text = matcher.replaceFirst(String.valueOf(opt.get()));
                    matcher.reset(text);
                }
            }
            return text;
        }
        return obj;
    }

}
