package me.puugz.simplehub.command;

import me.puugz.simplehub.SimpleHub;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

/**
 * @author puugz
 * @since May 21, 2023
 */
public class HubCommand extends Command {

    private final String hubServerName;
    private final String message;

    public HubCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);

        final Configuration config = SimpleHub.getInstance().getConfig();

        this.hubServerName = config.getString("command.hub-server-name");
        this.message = config.getString("command.message")
                .replace("<hub_name>", this.hubServerName);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            final TextComponent text = new TextComponent("Only players may execute this command.");
            text.setColor(ChatColor.RED);
            sender.sendMessage(text);
            return;
        }

        final ServerInfo targetServer = ProxyServer.getInstance().getServerInfo(this.hubServerName);
        final ProxiedPlayer player = (ProxiedPlayer) sender;

        if (targetServer == null) {
            final TextComponent text = new TextComponent("The server you tried to connect to does not exist.");
            text.setColor(ChatColor.RED);
            player.sendMessage(text);
            return;
        }

        if (player.getServer().getInfo().equals(targetServer)) {
            final TextComponent text = new TextComponent("You are already connected to the hub!");
            text.setColor(ChatColor.RED);
            player.sendMessage(text);
            return;
        }

        final TextComponent text = new TextComponent(TextComponent.fromLegacyText(
                ChatColor.translateAlternateColorCodes('&', this.message)
        ));

        player.sendMessage(text);
        player.connect(targetServer);
    }
}
