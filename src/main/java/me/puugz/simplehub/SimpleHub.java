package me.puugz.simplehub;

import lombok.Getter;
import me.puugz.simplehub.command.HubCommand;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

/**
 * @author puugz
 * @since May 21, 2023
 */
@Getter
public class SimpleHub extends Plugin {

    @Getter
    private static SimpleHub instance;

    private Configuration config;

    @Override
    public void onEnable() {
        instance = this;

        this.loadConfig();
        this.getProxy().getPluginManager()
                .registerCommand(this, new HubCommand(
                        this.config.getString("command.name"),
                        this.config.getString("command.permission"),
                        this.config.getStringList("command.aliases")
                                .toArray(new String[0])
                ));
    }

    private void loadConfig() {
        // Create plugin config folder if it doesn't exist
        if (!this.getDataFolder().exists()) {
            this.getLogger().info("Created config folder: " + this.getDataFolder().mkdir());
        }

        final File configFile = new File(getDataFolder(), "config.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            try {
                final FileOutputStream outputStream = new FileOutputStream(configFile);
                final InputStream in = this.getResourceAsStream("config.yml");

                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer, 0, 8192)) >= 0) {
                    outputStream.write(buffer, 0, read);
                }

                in.close();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            this.getLogger().severe("Unable to load the config file!");
        }
    }
}
