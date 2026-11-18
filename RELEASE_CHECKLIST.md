# Angling Forge 1.20.1 publication checklist

This is an unofficial community port of [EightSidedSquare's Angling](https://github.com/8s2/Angling). Retain the original CC0-1.0 `LICENSE`, authors (EightSidedSquare, Diansu, Sillvia), and upstream link in the distribution and project description. The repository license is included in the jar. Do not imply that the original authors maintain or endorse this port.

## Artifact acceptance

- [x] Build with Java 17 using the checked-in wrapper.
- [x] Pass the runtime GameTests and review logs for errors, failed mixin injections, missing registry/tag entries, broken data packs, and resource warnings (13/13 passed).
- [x] Pass `python tools/validate_release.py --jar <release.jar> --gametest-log <successful-log> --report build/reports/release-validation.json` and save its SHA-256 report (3,118 combined resource/package/runtime checks).
- [x] Launch the remapped jar in a clean client profile and a separate dedicated server.
- [ ] Complete and record the visual, survival, worldgen, save/reload, and two-client checks in `TESTING.md`.
- [x] Confirm the jar contains no development tests, copied dependency classes, Fabric entrypoints, or unresolved development names.
- [x] Keep a copy of the exact tested jar, source snapshot, dependency versions, test logs, and hash.

## CurseForge upload details

- Project title: **Angling — Unofficial Forge Port** (or another clearly distinct title).
- Game: Minecraft Java Edition; game version: **1.20.1**; mod loader: **Forge**; Java: **17**.
- Required dependency: **GeckoLib**, Forge build for Minecraft 1.20.1. The port is built against 4.4.9. Do not select a Fabric or NeoForge GeckoLib file.
- File: the remapped `angling-forge-1.20.1-1.0.1-forge.1.jar` from `build/libs`; attach sources separately if desired.
- License: **CC0-1.0**; link the upstream source and describe this repository's port changes.
- Use an **alpha** or **beta** release label until the client and multiplayer acceptance checks have been completed. A server-only test pass does not establish visual or multiplayer quality.
- Include installation instructions, dependency versions, tested environments, known limitations, and a link to the port's actual issue tracker. Do not direct port-specific reports to upstream unless upstream agrees.
- Publish only after reviewing the final artifact and the completed test record. Creating this checklist does not upload or publish anything.

## Suggested description

> An unofficial Forge 1.20.1 port of Angling by EightSidedSquare, Diansu, and Sillvia, released under the original CC0-1.0 license. Adds fish, sea creatures, aquatic plants, and breeding mechanics. Requires the Forge version of GeckoLib for Minecraft 1.20.1. This port is maintained separately from the original Fabric project.

Replace or extend the description with the measured release status and the actual maintainer/support links before uploading.
