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
    private long velocidadAnimacion; // Animation speed in ticks (English) / アニメーションの速度（ティック単位）(日本語) / Velocidad de la animación en ticks (Español)
    private boolean sonidoActivado;
    private float volumenSonido;
    private float pitchSonido;
    private List<String> comandos; // List of commands to execute (English) / 実行するコマンドのリスト (日本語) / Lista de comandos a ejecutar (Español)
    private long titleWait; // Wait time for the 'title' command (English) / 'title'コマンドの待機時間 (日本語) / Tiempo de espera para el comando 'title' (Español)


    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§aDeathAnimation Plugin Activado");
        saveDefaultConfig();

        // Load initial configuration (English) / 初期設定を読み込む (日本語) / Cargar configuración inicial (Español)
        animacionActiva = getConfig().getBoolean("animacion", true);
        String unicodeInicial = getConfig().getString("unicode_inicial", "\\uE000");
        int numeroDeFotogramas = getConfig().getInt("numero_de_fotogramas", 92);
        velocidadAnimacion = getConfig().getLong("velocidad_animacion", 10); // Animation speed in ticks (20 ticks = 1 second)

        // Load sound configuration (English) / 音の設定を読み込む (日本語) / Cargar configuración del sonido (Español)
        sonidoActivado = getConfig().getBoolean("sonido.activar", true);
        volumenSonido = (float) getConfig().getDouble("sonido.volumen", 1.0);
        pitchSonido = (float) getConfig().getDouble("sonido.pitch", 1.0);

        // Load commands from config.yml (English) / config.ymlからコマンドを読み込む (日本語) / Cargar los comandos desde el config.yml (Español)
        comandos = getConfig().getStringList("comandos");

        // Wait configuration for the 'title' command (English) / 'title'コマンドの待機設定 (日本語) / Configuración de espera para el comando 'title' (Español)
        titleWait = getConfig().getLong("title_wait", 20); // 20 ticks por defecto

        // Generate the list of frames dynamically (English) / フレームのリストを動的に生成する (日本語) / Generar la lista de fotogramas dinámicamente (Español)
        fotogramas = generarFotogramas(unicodeInicial, numeroDeFotogramas);

        if (fotogramas.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage("§cAdvertencia: No se generaron fotogramas. Verifica la configuración.");
        } else {
            Bukkit.getConsoleSender().sendMessage("§aFotogramas generados correctamente: " + fotogramas.size());
        }

        // Register events (English) / イベントを登録する (日本語) / Registrar eventos (Español)
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

        // Play the sound if enabled in the configuration (English) / 設定で有効になっている場合、音を再生する (日本語) / Reproducir el sonido si está activado en la configuración (Español)
        if (sonidoActivado) {
            reproducirSonidoParaTodos(jugador);
        }

        // Execute commands from the list (English) / リストからコマンドを実行する (日本語) / Ejecutar los comandos desde la lista (Español)
        ejecutarComandos(jugador);
    }

    private void mostrarAnimacion(Player jugador) {
        int numeroDeFotogramas = fotogramas.size();

        // Use an asynchronous thread for each player to send animations (English) / 各プレイヤーにアニメーションを送信するために非同期スレッドを使用する (日本語) / Usar un hilo asincrónico por cada jugador para enviar las animaciones (Español)
        Bukkit.getScheduler().runTask(this, () -> {
            for (int i = 0; i < numeroDeFotogramas; i++) {
                final String simbolo = fotogramas.get(i);

                // We use runTaskLater to display each frame with the appropriate delay (English) / 適切な遅延で各フレームを表示するためにrunTaskLaterを使用する (日本語) / Usamos runTaskLater para mostrar cada fotograma con el retraso adecuado (Español)
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    // Display the frame as a title on the specific player's client (English) / 特定のプレイヤーのクライアントにタイトルとしてフレームを表示する (日本語) / Mostrar el fotograma como título en el cliente del jugador específico (Español)
                    jugador.sendTitle("§f" + simbolo, "", 0, 20, 0);
                }, i * (int) velocidadAnimacion);
            }
        });
    }

    private void reproducirSonidoParaTodos(Player jugador) {
        // Reproducir el sonido para todos los jugadores conectados
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.playSound(p.getLocation(), "minecraft:muerte", volumenSonido, pitchSonido);  // Reproduce el sonido personalizado
        }
    }

    private void ejecutarComandos(Player jugador) {
        // Ejecutar los comandos de la lista "comandos"
        for (String comando : comandos) {
            // Asegurarse de que el comando tiene un formato válido para ejecutar
            final String comandoCorrecto = comando.replace("{player}", jugador.getName());

            // Si el comando es del tipo 'title', lo ejecutamos con el delay configurado
            if (comando.startsWith("title")) {
                // Ejecutar el comando 'title' después de un delay configurado
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), comandoCorrecto);
                }, titleWait); // El tiempo de espera se toma de la configuración
            } else {
                // Ejecutar otros comandos inmediatamente
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), comandoCorrecto);
                });
            }
        }
    }

    private List<String> generarFotogramas(String unicodeInicial, int cantidad) {
        List<String> fotogramasGenerados = new ArrayList<>();

        try {
            // Convertir el Unicode inicial al valor entero
            int codigoInicial = Integer.parseInt(unicodeInicial.substring(2), 16);

            // Generar caracteres Unicode secuenciales
            for (int i = 0; i < cantidad; i++) {
                int codigoActual = codigoInicial + i;
                String simboloUnicode = Character.toString((char) codigoActual); // Convertir a carácter
                fotogramasGenerados.add(simboloUnicode); // Agregar a la lista

                // Imprimir el fotograma y su código Unicode en la consola
                Bukkit.getConsoleSender().sendMessage("§aFotograma " + i + ": §2" + simboloUnicode + " §a(Código: §2U+" + Integer.toHexString(codigoActual).toUpperCase() + "§a)");
            }
        } catch (NumberFormatException e) {
            Bukkit.getConsoleSender().sendMessage("§c[DeathAnimation] Error al procesar unicode_inicial: " + e.getMessage());
        }

        return fotogramasGenerados;
    }
}
