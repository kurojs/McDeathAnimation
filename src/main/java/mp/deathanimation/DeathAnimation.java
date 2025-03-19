package mp.deathanimation;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class DeathAnimation extends JavaPlugin implements Listener {

    private boolean animacionActiva;
    private List<String> fotogramas;
    private long velocidadAnimacion; // Animation speed in ticks
    private boolean sonidoActivado;
    private float volumenSonido;
    private float pitchSonido;
    private List<String> comandos; // List of commands to execute
    private long titleWait; // Wait time for the 'title' command


    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§aDeathAnimation Plugin Activado");
        saveDefaultConfig();

        // Load initial configuration
        animacionActiva = getConfig().getBoolean("animacion", true);
        String unicodeInicial = getConfig().getString("unicode_inicial", "\\uE000");
        int numeroDeFotogramas = getConfig().getInt("numero_de_fotogramas", 92);
        velocidadAnimacion = getConfig().getLong("velocidad_animacion", 10); // Animation speed in ticks (20 ticks = 1 second)

        // Load sound configuration
        sonidoActivado = getConfig().getBoolean("sonido.activar", true);
        volumenSonido = (float) getConfig().getDouble("sonido.volumen", 1.0);
        pitchSonido = (float) getConfig().getDouble("sonido.pitch", 1.0);

        // Load commands from config.yml
        comandos = getConfig().getStringList("comandos");

        // Wait configuration for the 'title' command
        titleWait = getConfig().getLong("title_wait", 20); // 20 ticks por defecto

        // Generate the list of frames dynamically
        fotogramas = generarFotogramas(unicodeInicial, numeroDeFotogramas);

        if (fotogramas.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("§cWARNING: No photograms were generated. Verify the configuration.");
        } else {
            Bukkit.getConsoleSender().sendMessage("§aPhotograms generated correctly:" + fotogramas.size());
        }

        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§cDeathAnimation Plugin Desactivado");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!animacionActiva) return;

        Player jugador = event.getEntity();
        mostrarAnimacion(jugador);

        // Play the sound if enabled in the configuration
        if (sonidoActivado) {
            reproducirSonidoParaTodos(jugador);
        }

        // Execute commands from the list
        ejecutarComandos(jugador);
    }

    private void mostrarAnimacion(Player jugador) {
        int numeroDeFotogramas = fotogramas.size();

        // Use an asynchronous thread to handle animation without affecting performance
        Bukkit.getScheduler().runTask(this, () -> {
            for (int i = 0; i < numeroDeFotogramas; i++) {
                final String simbolo = fotogramas.get(i);

                Bukkit.getScheduler().runTaskLater(this, () -> {
                    // Show the animation to all players
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("§f" + simbolo, "", 0, 20, 0);
                    }
                }, i * (int) velocidadAnimacion);
            }
        });
    }

    private void reproducirSonidoParaTodos(Player jugador) {
        // Reproduce sound for all connected players
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.playSound(p.getLocation(), "minecraft:muerte", volumenSonido, pitchSonido);
        }
    }

    private void ejecutarComandos(Player jugador) {
        // Execute the commands of the "Commands" list
        for (String comando : comandos) {
            final String comandoCorrecto = comando.replace("{player}", jugador.getName());

            if (comando.startsWith("title")) {
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), comandoCorrecto);
                }, titleWait);
            } else {
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), comandoCorrecto);
                });
            }
        }
    }

    private List<String> generarFotogramas(String unicodeInicial, int cantidad) {
        List<String> fotogramasGenerados = new ArrayList<>();

        try {
            // Convert the initial unicode to the entire valuevv
            int codigoInicial = Integer.parseInt(unicodeInicial.substring(2), 16);

            // generate sequential unicode characters
            for (int i = 0; i < cantidad; i++) {
                int codigoActual = codigoInicial + i;
                String simboloUnicode = Character.toString((char) codigoActual);
                fotogramasGenerados.add(simboloUnicode);


                Bukkit.getConsoleSender().sendMessage("§aFotograma " + i + ": §2" + simboloUnicode + " §a(Código: §2U+" + Integer.toHexString(codigoActual).toUpperCase() + "§a)");
            }
        } catch (NumberFormatException e) {
            Bukkit.getConsoleSender().sendMessage("§c[DeathAnimation] Error al procesar unicode_inicial: " + e.getMessage());
        }

        return fotogramasGenerados;
    }
}
