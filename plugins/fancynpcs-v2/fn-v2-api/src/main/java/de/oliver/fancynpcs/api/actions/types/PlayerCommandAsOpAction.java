package de.oliver.fancynpcs.api.actions.types;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.chatcolorhandler.common.parser.Parsers;
import org.lushplugins.chatcolorhandler.paper.PaperColor;

/**
 * PlayerCommandAsOpAction is a npc action that allows a player to execute a command as an operator when triggered by an NPC interaction.
 */
@Deprecated
public class PlayerCommandAsOpAction extends NpcAction {

    public PlayerCommandAsOpAction() {
        super("player_command_as_op", true);
    }

    /**
     * Executes a player command as an operator when triggered by an NPC interaction.
     */
    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String command = PaperColor.handler().translateRaw(value, player, Parsers::placeholder);

        if (command.toLowerCase().startsWith("server")) {
            String[] args = value.split(" ");
            if (args.length < 2) {
                return;
            }
            String server = args[1];

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(FancyNpcsPlugin.get().getPlugin(), "BungeeCord", out.toByteArray());
            return;
        }

        FancyNpcsPlugin.get().getScheduler().runTask(
                player.getLocation(),
                () -> {
                    boolean wasOp = player.isOp();

                    player.setOp(true);
                    try {
                        player.chat("/" + command);
                    } catch (Exception e) {
                        FancyNpcsPlugin.get().getFancyLogger().warn("Failed to execute command: " + command);
                    } finally {
                        player.setOp(wasOp);
                        FancyNpcsPlugin.get().getFancyLogger().debug("Reset OP status of player " + player.getName() + " to " + wasOp);
                    }
                }
        );
    }

}
