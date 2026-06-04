# HeadHunter 🎯

A lightweight, highly optimized, and robust custom head-hunting plugin for Minecraft servers (Paper/Purpur 1.21+). Perfect for server events, Easter egg hunts, or seasonal activities.

## ✨ Features

* **Custom Texture Support:** Use any Minecraft texture URL directly from the configuration file.
* **Progress Tracking:** Real-time progress bar displayed seamlessly in the player's Action Bar.
* **Flexible Rewards:** Support for custom item rewards (with CustomModelData, names, lore, and enchantments) or console command execution.
* **Interactive Setup:** Quick registration and removal of target heads via simple in-game commands looking directly at the blocks.
* **Flat-file Storage:** Individual player progress is saved efficiently in separate YAML files to prevent data corruption.
* **Built on KynosLib:** Utilizes a custom utility library for streamlined performance and clean message handling.

## 🛠️ Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/headhunter info` | Displays plugin and developer information. | `headhunter.player.info` |
| `/headhunter givehead [amount]` | Obtains the special event heads to place. | `headhunter.admin.givehead` |
| `/headhunter sethead` | Registers the head you are looking at into the system. | `headhunter.admin.sethead` |
| `/headhunter removehead` | Unregisters and removes the head you are looking at. | `headhunter.admin.removehead` |
| `/headhunter reloadconfig` | Reloads the main `config.yml` file. | `headhunter.admin.reloadconfig` |
| `/headhunter reloadheads` | Reloads the `heads.yml` storage file. | `headhunter.admin.reloadheads` |

## 🚀 Installation

1. Drop `HeadHunter.jar` into your server's `plugins` folder.
2. Ensure you have `KynosLib.jar` installed as well. *(Note: KynosLib is required and can be downloaded from its own repository [HERE](inserisci_qui_il_link_della_repository_di_kynoslib)).*
3. Restart the server to generate the default configuration files.
4. Configure your custom texture URL and rewards in `config.yml`.
5. Enjoy your hunt!
