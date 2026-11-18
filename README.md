# Angling — Forge 1.20.1 port

An unofficial Forge port of [Angling](https://github.com/8s2/Angling), originally created by **EightSidedSquare, Diansu, and Sillvia**. Discover new fish, breed them with worms, raise fry, trade with pelicans, and decorate aquariums with living sea creatures and plants.

## Installation

- Minecraft **1.20.1**, Java **17**, and Forge **47.4.0 or later in the 47.x series**.
- Install **GeckoLib for Forge 1.20.1**, version **4.4.9 or later in the 4.x series** (4.4.9 is the tested baseline).
- Put the release `angling-forge-1.20.1-1.0.1-forge.1.jar` and GeckoLib in `mods/` on both client and server.

Fabric API, Fabric Loader, Cardinal Components, and Architectury API are not runtime dependencies. This port retains the `angling` mod ID; do not install it alongside another Angling version. Back up existing worlds before changing Minecraft versions or mod loaders; cross-loader world migration is not certified by this port.

## Building and testing

Use a JDK 17 installation and the included Gradle wrapper. Windows uses `gradlew.bat`; Linux/macOS use `./gradlew`.

```text
gradlew.bat build
gradlew.bat runGameTestServer
python tools/validate_release.py --jar build/libs/angling-forge-1.20.1-1.0.1-forge.1.jar --gametest-log run/gametest/logs/latest.log --report build/reports/release-validation.json
gradlew.bat runClient
```

The distributable is the JAR in `build/libs` without the `-sources` or `-dev` suffix. Runtime test classes and structures live in a separate `gametest` source set and are excluded from the release JAR. Development runs use isolated folders under `run/`. A server run requires accepting Minecraft's EULA in its run directory.

The build uses Architectury Loom solely as a build tool, with Yarn names and Forge remapping. Gameplay registration, events, custom registries, and biome modifiers use Forge APIs. [TESTING.md](TESTING.md) describes automated coverage and manual checks; [RELEASE_CHECKLIST.md](RELEASE_CHECKLIST.md) covers CurseForge preparation.

## Data packs

The original `angling` item/entity/block IDs and NBT variant IDs are retained. Fish breeding eligibility, pelican contents, and variant selection remain tag-driven. Forge custom registry tags are under `data/<namespace>/tags/angling/<registry>/`; ordinary biome tags stay under `tags/worldgen/biome/`. World generation uses configured/placed feature JSON plus Forge biome modifiers.

## Credits and license

Based on upstream commit `e0c99ab904cc3d0e5ece2d04a39347b5c1e346ea` (Fabric 1.19.2). Original assets, models, sounds, and credit are preserved. The upstream [Modrinth project](https://modrinth.com/mod/angling) and repository declare **CC0-1.0**; the full dedication is retained in [LICENSE](LICENSE) and the mod JAR. This port is not an official release by the original creators.

Publishing this port does not include publishing Minecraft, Forge, or GeckoLib; those are separate dependencies. The upstream source URL identifies the original project, not an issue tracker maintained for this port.
