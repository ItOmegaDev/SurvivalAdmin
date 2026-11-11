# SurvivalAdmin

**Professional administration plugin for Minecraft Survival servers with role-based access control.**

One jar, Paper/Purpur/Spigot compatible. Made by **Omega** ([@ItOmega](https://t.me/ItOmega)) · [GitHub](https://github.com/ItOmegaDev/SurvivalAdmin) · [Releases](https://github.com/ItOmegaDev/SurvivalAdmin/releases)

---

## ✨ Features

- **Role-based access control** — `user`, `staff`, `owner` with permission inheritance
- **UUID-based role storage** — roles stored by player UUID, immune to name changes and spoofing
- **Command blocking** — regular users can only use whitelisted commands; all others are blocked
- **Namespace/alias bypass prevention** — blocks `/bukkit:plugins`, `/minecraft:gamemode`, etc.
- **Tab completion filtering** — blocked commands hidden from suggestions
- **Custom messages** — join, quit, death, kill messages with placeholders
- **15+ admin commands** — heal, feed, tp, gamemode, fly, god, time, weather, invsee, kick, ban, tempban, mute, vanish, sit
- **Inventory inspection** — `/inv <player>` to view/edit player inventory
- **Ban with duration** — `/getoff <player> <time> [reason]` supports `1h`, `1d`, `30m`, `permanent`
- **Vanish mode** — `/invis` / `/vanish` to become invisible in TAB and the world
- **Granular permissions** — `survivaladmin.kick`, `survivaladmin.ban`, `survivaladmin.mute`, etc.
- **Atomic file writes** — crash-safe role storage
- **Audit log** — every admin action is logged with timestamp and actor
- **YAML config** — easy to edit `config.yml`

---

## 📦 Installation

1. Download `SurvivalAdmin-2.0.0.jar` from [Releases](https://github.com/ItOmegaDev/SurvivalAdmin/releases)
2. Put in `plugins/`
3. Start server — `plugins/SurvivalAdmin/` is created with `config.yml`
4. Edit `config.yml` to set owners and staff
5. Restart server

---

## ⚙️ Configuration

### config.yml

```yaml
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
    - survivaladmin.gamemode
    - survivaladmin.fly
    - survivaladmin.god
    - survivaladmin.time
    - survivaladmin.weather
    - survivaladmin.invsee
    - survivaladmin.vanish
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
| `/unmute <player>` | Unmute a player |
| `/heal [player]` | Heal player |
| `/feed [player]` | Feed player |
| `/tp <player>` | Teleport to player |
| `/gm <0-3> [player]` | Change gamemode |
| `/fly [player]` | Toggle flight |
| `/god [player]` | Toggle god mode |
| `/time <day/night/number>` | Change time |
| `/weather <clear/rain/thunder>` | Change weather |
| `/inv <player>` | View/edit player inventory |
| `/getoff <player> <time> [reason]` | Ban with time and reason |
| `/invis` / `/vanish` | Toggle invisibility |

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
| `survivaladmin.tempban` | Temporarily ban players | false |
| `survivaladmin.unban` | Unban players | false |
| `survivaladmin.mute` | Mute players | false |
| `survivaladmin.unmute` | Unmute players | false |
| `survivaladmin.tp` | Teleport to players | false |
| `survivaladmin.heal` | Heal players | false |
| `survivaladmin.feed` | Feed players | false |
| `survivaladmin.gamemode` | Change gamemode | false |
| `survivaladmin.fly` | Toggle flight | false |
| `survivaladmin.god` | Toggle god mode | false |
| `survivaladmin.time` | Change time | false |
| `survivaladmin.weather` | Change weather | false |
| `survivaladmin.invsee` | View player inventory | false |
| `survivaladmin.vanish` | Toggle invisibility | false |

---

## 🛠️ Build

```bash
./build.sh
# -> target/SurvivalAdmin-2.0.0.jar
```

Requires Paper/Purpur API jar (auto-detected from `../../servers/`).

---

## 📄 License

MIT

**Contact:** [@ItOmega](https://t.me/ItOmega)
