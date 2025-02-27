## DeathAnimation Plugin

**DeathAnimation** is an advanced plugin for **Minecraft** that adds custom animations and sound effects triggered when a player dies. This plugin utilizes a resource pack to display the animation and play a sound when a player dies in the game. Additionally, the plugin allows executing custom commands when a player dies.

![MCDeath Demo](https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExNmtmemdjNjQ3NjlyZmIxbzluYmpoaW1kMzl2ZnU1YW5qcW5mbjQ2eiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/u6OwFS94l8yJCtFVFy/giphy.gif)

  ### Main Features:

  - **Custom Animations:** Displays a text animation to all players when another player dies. The frames for the animation are dynamically generated from Unicode codes.
  - **Custom Sounds:** Plays a sound for all players connected when a player dies, with adjustable volume and pitch settings.
  - **Custom Commands:** Executes a list of commands when a player dies. The commands can include the `title` command to show a message to the player with a configurable delay.
  
  ### Configuration:

  The plugin's parameters can be adjusted through the `config.yml` file. Some configurable parameters include:

  - **Animation:** Enable or disable the animation.
  - **Animation Speed:** Adjust the speed of the animation frames in ticks.
  - **Sound:** Enable or disable the sound and adjust its volume and pitch.
  - **Commands:** Specify a list of commands to execute upon player death, including support for the `title` command.

  ### How to Install:

  1. Place the `.jar` file into your Minecraft server's `plugins` folder.
  2. Configure the plugin according to your preferences in the `config.yml` file.
  3. Restart or reload the server to activate the plugin.

  ### Example Configuration (`config.yml`):

  ```yaml
  animacion: true
  unicode_inicial: "\uE000"
  numero_de_fotogramas: 92
  velocidad_animacion: 10
  sonido:
    activar: true
    volumen: 1.0
    pitch: 1.0
  comandos:
    - "title {player} title Death Text"
  title_wait: 20
