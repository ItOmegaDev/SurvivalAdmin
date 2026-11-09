# SurvivalAdmin

**Professional administration plugin for Minecraft Survival servers with role-based access control.**

Made by **Omega** ([@ItOmega](https://t.me/ItOmega))

---

## ✨ Features

- **Role-based access control** — `user`, `staff`, `owner`
- **UUID-based role storage** — roles stored by player UUID, immune to name changes
- **Command blocking** — regular users can only use allowed commands
- **Namespace/alias bypass prevention** — blocks `/bukkit:plugins`, `/minecraft:gamemode`, etc.
- **Tab completion filtering** — blocked commands hidden from suggestions
- **Custom messages** — join, quit, death, kill messages
- **Role management** — `/staff`, `/unstaff`, `/owner`, `/unowner` commands
- **Granular permissions** — `survivaladmin.kick`, `survivaladmin.ban`, etc.
- **Atomic file writes** — crash-safe role storage
- **YAML config** — easy to edit `config.yml`

---

## 📦 Installation

1. Download `SurvivalAdmin-1.1.0.jar` from [Releases](https://github.com/ItOmegaDev/SurvivalAdmin/releases)
2. Put in `plugins/`
3. Start server — `plugins/SurvivalAdmin/` is created with `config.yml`
4. Edit `config.yml` to set owners and staff
5. Restart server

---

## ⚙️ Configuration

### config.yml

```yaml
# Plugin info
version: "1.1.0"
author: "Omega"
contact: "@ItOmega"
description: "Professional administration plugin for Survival servers"

# Roles
roles:
  default: "user"          # Default role for new players
  staff: []                # List of staff player names (auto-migrated to UUID on first join)
  owner:
    - "DevOmega"           # List of owner player names (auto-migrated to UUID on first join)

# Granular permissions per role
permissions:
  user: []
  staff:
    - survivaladmin.kick
    - survivaladmin.ban
    - survivaladmin.tempban
    - survivaladmin.unban
    - survivaladmin.mute
    - survivaladmin.unmute
    - survivaladmin.tp
    - survivaladmin.heal
    - survivaladmin.feed
  owner:
    - survivaladmin.*      # All permissions

# Commands allowed for regular users (all others are blocked)
user-commands:
  - "sit"
  - "msg"
  - "tell"
  - "w"
  - "me"
  - "list"
  - "help"

# Custom messages (use {player}, {role}, {killer} placeholders)
messages:
  no-permission: "§cУ вас немає доступу до цієї команди"
  command-blocked: "§cЦя команда недоступна на цьому сервері"
  role-given: "§aГравцю {player} видано роль {role}"
  role-removed: "§cУ гравця {player} забрано роль {role}"
  unknown-role: "§cНевідома роль: {role}"
  player-not-found: "§cГравця {player} не знайдено"
  join: "§a{player} приєднався до гри"
  quit: "§c{player} покинув гру"
  death: "§c{player} помер"
  kill: "§c{player} був убитий гравцем {killer}"
```

### roles.json (auto-generated)

Roles are stored by **UUID** in `plugins/SurvivalAdmin/roles.json`:
```json
{
  "c1c3bec8-a19f-3fc5-afef-b5c7e1c3ab21": {"role": "owner", "name": "DevOmega"},
  "550e8400-e29b-41d4-a716-446655440000": {"role": "staff", "name": "Player1"}
}
```

Legacy name-based roles (from older versions) are automatically migrated to UUID on player join.

---

## 🔧 Commands

### For all players
| Command | Description |
|---|---|
| `/sit` | Sit down |
| `/msg <player> <message>` | Send private message |
| `/help` | Show available commands |

### For staff (requires staff or owner role)
| Command | Description |
|---|---|
| `/admin` | Show admin panel |
| `/kick <player> [reason]` | Kick a player |
| `/ban <player> [reason]` | Ban a player |
| `/tempban <player> <duration> [reason]` | Temporarily ban |
| `/unban <player>` | Unban a player |
| `/mute <player> [reason]` | Mute a player |

### For owners only
| Command | Description |
|---|---|
| `/staff <player>` | Give staff role |
| `/unstaff <player>` | Remove staff role |
| `/owner <player>` | Give owner role |
| `/unowner <player>` | Remove owner role |

---

## 🔒 Permissions

| Permission | Description | Default |
|---|---|---|
| `survivaladmin.owner` | Owner permissions | op |
| `survivaladmin.staff` | Staff permissions | false |
| `survivaladmin.user` | User permissions | true |
| `survivaladmin.kick` | Kick players | false |
| `survivaladmin.ban` | Ban players | false |
| `survivaladmin.mute` | Mute players | false |

---

## 🛠️ Build

```bash
./build.sh
# -> target/SurvivalAdmin-1.1.0.jar
```

Requires Paper/Purpur API jar (auto-detected from `../../servers/`).

---

## 📄 License

This project is provided as-is for the OmegaSMP community.

**Contact:** [@ItOmega](https://t.me/ItOmega)
