# Bamboo Configuration
The Bamboo Configuration module provides a platform-independent
configuration API for Kotlin and Java applications.

It provides a single, consistent API for working with multiple
configuration formats without exposing the underlying configuration
libraries to consumers.

Currently supported formats are:

| Format | Extensions        |
|--------|-------------------|
| YAML   | `.yml`, `.yaml`   |
| JSON   | `.json`           |
| HOCON  | `.conf`, `.hocon` |
| TOML   | `.toml`           |

Bamboo automatically determines the appropriate format from the
configuration file's extension.

## Features
- Platform-independent configuration API
- YAML, JSON, HOCON, and TOML support
- Automatic format detection
- Multiple independent `ConfigManager` instances
- Optional parent directory for relative configuration paths
- In-memory configuration state
- Dirty-state tracking
- Explicit save and reload operations
- Manager-wide save and reload operations
- Reload only configurations whose files have changed
- Protection against overwriting unsaved in-memory changes
- Comment access for formats that support comments
- Backend implementations hidden behind the Bamboo API

## Backends
| Format | Backend                                                                                                                  |
|--------|--------------------------------------------------------------------------------------------------------------------------|
| YAML   | [NightConfig](https://github.com/TheElectronWill/night-config) by [TheElectronWill](https://github.com/TheElectronWill)  |
| JSON   | [NightConfig](https://github.com/TheElectronWill/night-config) by [TheElectronWill](https://github.com/TheElectronWill)  |
| HOCON  | [NightConfig](https://github.com/TheElectronWill/night-config) by [TheElectronWill](https://github.com/TheElectronWill)  |
| TOML   | [NightConfig](https://github.com/TheElectronWill/night-config) by [TheElectronWill](https://github.com/TheElectronWill)  |

A plugin using Bamboo does not need to interact directly
with NightConfig.

NightConfig is licensed under the
GNU Lesser General Public License v3.0.

A copy of the license is included with Bamboo at:
[licenses/night-config/LGPL-3.0.txt](https://github.com/BambooLib/Bamboo/blob/main/licenses/night-config/LGPL-3.0.txt)

See [THIRD-PARTY-NOTICES.md](https://github.com/BambooLib/Bamboo/blob/main/THIRD-PARTY-NOTICES.md) for additional
third-party licensing information.

## Getting Started
Create a `ConfigManager` through the `ConfigApi`.

```kotlin
val manager = configApi.createConfigManager(parentPath)
```
The parent path is optional. When provided, relative configuration
paths are resolved against it.

```kotlin
val manager = configApi.createConfigManager(parentPath)
val config  = manager.load(Path.of("config.yml"))
```
This loads: `<parentPath>/config.yml`
<br>The following works the same way, regardless of the file format:
```kotlin
val yaml    = manager.load(Path.of("config.yml"))
val json    = manager.load(Path.of("messages.json"))
val hocon   = manager.load(Path.of("settings.conf"))
val toml    = manager.load(Path.of("database.toml"))
```
Bamboo detects the format from the file extension automatically.

### Reading Values
Configuration values are accessed using dot-separated paths.

Given:
```yml
database:
  host: localhost
  port: 3306
  enabled: true
```
Values can be read with:
```kotlin
val host    = config.getString("database.host")
val port    = config.getInt("database.port")
val enabled = config.getBoolean("database.enabled")
```
Default values can be provided:
```kotlin
val host    = config.getString("database.host", "localhost")
val port    = config.getInt("database.port", 3306)
val enabled = config.getBoolean("database.enabled", true)
```
Raw values can also be retrieved:
```kotlin
val value = config.get("database.host")
```
Or queried using an `Optional`:
```kotlin
val value = config.getOptional("database.host")
```

### Writing Values
Changes are made in memory and mark the configuration as dirty.
```kotlin
config.set("database.host", "127.0.0.1")
config.set("database.port", 5432)
```
The changes are not written to disk until `save()` is called.
```kotlin
config.save()
```
A configuration exposes its current state through `isDirty`:
```kotlin
if (config.isDirty) {
    config.save()
}
```

### Removing Values
Values can be removed from a configuration:
```kotlin
config.remove("database.oldSetting")
```
The method returns `true` when a value was removed.

### Comments
Formats that support comments expose comment operations through
the same API.
```kotlin
config.setComment(
    "database.host",
    "Address of the database server."
)
```
Existing comments can be retrieved:
```kotlin
val comment = config.getComment("database.host")
```
Whether comments are supported can be determined from the
configuration format:
```kotlin
if (config.format.supportsComments) {
    // Comments are supported
}
```
JSON does not support comments through the Bamboo configuration API.
### Saving
A configuration can be saved explicitly:
```kotlin
config.save()
```
Bamboo tracks whether a configuration has changed and avoids
unnecessarily writing configurations that are already clean.
<br>A `ConfigManager` can save every configuration it currently manages:
```kotlin
manager.saveAll()
```
### Reloading
A configuration can be explicitly reloaded from disk:
```kotlin
config.reload()
```
Reloading discards unsaved in-memory changes.

For applications managing multiple configuration files, the manager
can check all loaded configurations for external changes:
```kotlin
val reloaded = manager.reloadChanged()
```
`reloadChanged()` only reloads configurations whose backing files have
actually changed.

Configurations with no external changes are left untouched.

Configurations containing unsaved in-memory changes are not silently
overwritten by an external file modification.

### Configuration Manager Lifecycle
A `ConfigManager` owns the configurations loaded through it.
```kotlin
val first = manager.load(Path.of("config.yml"))
val second = manager.load(Path.of("config.yml"))

check(first == second)
```
A configuration can be removed from the manager:
```kotlin
manager.unload(Path.of("config.yml"))
```
The manager itself should be closed when it is no longer needed:
```kotlin
manager.close()
```
For Kotlin applications, it can be used with `use`:
```kotlin
configApi.createConfigManager(parentPath).use { manager ->
    val config = manager.load(Path.of("config.yml"))
    
    // Use configuration...
}
```

### Absolute Paths
A manager with a parent path resolves relative paths against that
directory.
```kotlin
val manager = configApi.createConfigManager(dataFolder)
manager.load(Path.of("config.yml"))
```
Absolute paths remain absolute:
```kotlin
manager.load(Path.of("/some/external/config.toml"))
```
This allows applications to use both application-managed configuration
files and explicitly selected external files.

## File Preservation
Bamboo is designed to avoid unnecessary configuration rewrites.

Loading a configuration does not rewrite its file.
<br>Reloading a configuration does not rewrite its file.
<br>Saving a configuration that has not been changed does not
unnecessarily rewrite the file.

When a configuration must be serialized after an actual modification,
the underlying format implementation is responsible for serialization
and preservation of supported configuration information such as
comments and ordering.

Bamboo does not currently guarantee byte-for-byte preservation of every
formatting choice made in the source file.

## Status
The Configuration module is part of the Bamboo ecosystem and is under
active development.

The core configuration API and backend architecture are being
developed first. Configuration schemas, migrations, and advanced
update functionality will build on top of this foundation.

## License
This project uses the MIT license, [click here for more details](https://github.com/BambooLib/Bamboo/blob/main/LICENSE).