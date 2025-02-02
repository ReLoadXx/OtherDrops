package dev.reloadx.utils;

import org.bukkit.plugin.Plugin;
import java.util.logging.Logger;

public class StartupManager {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PINK = "\u001B[38;5;213m";
    private static final String ANSI_BRIGHT_BLUE = "\u001B[38;5;81m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final String ASCII_ART = """
             _ ___    __ _  __ _  _  __
            / \\ | |_||_ |_)/  / \\|_)|_\s
            \\_/ | | ||__| \\\\__\\_/| \\|__
            """;

    public static void onStartup(Logger logger, Plugin plugin) {
        String author = plugin.getDescription().getAuthors().isEmpty() ? "Desconocido" : plugin.getDescription().getAuthors().get(0);
        String version = plugin.getDescription().getVersion();

        String header = String.format("%s__________%s%s%s__________", ANSI_CYAN, ANSI_PINK, author, ANSI_CYAN);
        String footer = String.format("%s_______%sVersion:%s%s_______", ANSI_CYAN, ANSI_PINK, version, ANSI_CYAN);

        logger.info("\n" + header + "\n" + ANSI_BRIGHT_BLUE + ASCII_ART + ANSI_RESET + "\n" + footer + ANSI_WHITE);

    }
}

