# DynSpawn

Paper-плагин для случайного спавна игроков в заданных регионах.

Игрок при первом спавне получает случайный регион и каждый раз появляется в случайной точке внутри его радиуса. Участники группы делят один регион.

**Paper 1.21+ · Java 21 · Kotlin**

## Как это работает

1. Админ создаёт регионы (`/spawn region create`).
2. При спавне игроку назначается регион (случайный или общий для группы).
3. Каждый спавн — новая случайная точка в радиусе региона.

Приоритет: **группа → личная привязка → новый случайный регион**.

## Команды

```
/spawn region create <name> [radius]   — создать регион
/spawn region set <name>               — сменить центр
/spawn region radius <name> <radius>   — задать радиус
/spawn region remove <name>            — удалить регион
/spawn region list                     — список регионов

/group create <name>                     — создать группу (общий регион)
/group join <name>                     — вступить в группу
/group leave                           — выйти
/group remove <player>                 — исключить игрока

/warp set|remove|list|teleport <name>  — варпы

/dynspawn help|reload|version
```

## Конфиг

```yaml
plugin:
  language: en

spawn:
  auto-spawn: true
  default-radius: 1000

warp:
  enabled: true
  spawn_around: true

groups:
  enabled: true
```

Данные хранятся в `plugins/DynSpawn/`: `regions.yml`, `players.yml`, `groups.yml`, `warps.yml`.

## API

```kotlin
val api = Bukkit.getServicesManager().load(DynSpawnAPI::class.java)
api?.teleportToSpawn(player)
```

## Сборка

```bash
mvn clean package
```

JAR: `target/DynSpawn-1.0-SNAPSHOT.jar`
