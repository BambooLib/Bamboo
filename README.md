# Bamboo
Bamboo is a platform-independent library ecosystem for building
Minecraft plugins.

The project is designed around small, focused modules that provide
reusable functionality without being tied to a specific Minecraft server
implementation.

Bamboo separates platform-independent APIs and libraries from
platform-specific implementations, allowing developers to use only the
parts of the ecosystem they need.

## Goals
Bamboo is designed to provide:
- Platform-independent, reusable libraries
- Small, independently consumable modules
- Clean and consistent APIs
- Minimal coupling between systems
- Lightweight and performant implementations
- Kotlin-first development with Java interoperability
- Platform-specific implementations kept separate from the core libraries

The goal is to build a collection of libraries that can be shared across
many Minecraft projects rather than repeatedly implementing the same
systems in individual plugins.

## Modules
Each Bamboo module is its own library and can be used independently.

| Module                                                                       | Description                                                    |
|------------------------------------------------------------------------------|----------------------------------------------------------------|
| [Configuration](https://github.com/BambooLib/Bamboo/tree/main/configuration) | Configuration loading, saving, reloading, and file management. |

More modules will be added as the Bamboo ecosystem grows.

### Using Individual Modules
Applications can depend on only the modules they require.

For example:
```groovy
compileOnly("io.github.deopping.bamboo:configuration:<version>")
```
This keeps dependencies focused and avoids bringing unrelated Bamboo
functionality into a project.

### Using All Bamboo Modules
An `all` artifact is provided for projects that want the complete Bamboo
library ecosystem:
```groovy
compileOnly("io.github.deopping.bamboo:all:<version>")
```
The `all` artifact acts as an umbrella dependency for Bamboo's modules
rather than bundling the libraries into a single shaded JAR.

## Dependency Model
Bamboo modules are designed to hide their implementation dependencies behind
their public APIs whenever possible.

For example, the Configuration module may use an external library internally
while exposing only Bamboo's own configuration interfaces to consumers.

This keeps Bamboo's public API independent of its implementation libraries
and allows implementations to change without unnecessarily affecting
consumers.

## Development
Bamboo is written entirely in Kotlin and targets Java 25.

The project uses a Gradle multi-module build, with each Bamboo library
maintained as an independent Gradle module.

The repository also contains shared Gradle build conventions used across the
modules.

## Project Status
Bamboo is currently under active development.

The project is being built incrementally, with individual modules being
implemented and stabilized before expanding the ecosystem.

APIs may change during development until modules reach a stable release.

## Third-Party Software
Individual modules may use third-party open-source libraries.

Third-party dependencies remain under their respective licenses
and are not relicensed as part of Bamboo.

See [THIRD-PARTY-NOTICES](THIRD-PARTY-NOTICES.md) for third-party attribution
and licensing information.

## License
Bamboo is licensed under the MIT License.
<br>See [LICENSE](LICENSE) for the complete license.