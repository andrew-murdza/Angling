# Optional graphical test companion

This small Forge client mod captures Minecraft's rendered framebuffer, opens the Angling creative tab, reloads resources, and records client-side entity/block-entity/inventory data. It is test instrumentation, not a dependency of Angling, and is excluded from the release JAR.

Build from the repository root with Java 17:

```powershell
.\gradlew.bat -I qa/client/client-qa.init.gradle remapClientQaJar
```

Put `build/client-qa/angling-client-qa-1.0.0.jar` in an isolated test client's `mods` folder alongside the release Angling and Forge GeckoLib JARs. Do not install it in a normal player profile or upload it as part of Angling. It does not need to be installed on the dedicated server.

After joining a local test world/server, write a request into `qa-request.txt` in that client's game directory. Each request must differ from the previous one; use a distinct output name.

```text
capture aquarium
inventory angling-items
close back-to-world
reload resource-reload
snapshot before-change
```

The file contains one command at a time. Screenshots appear in `screenshots/angling-qa-<name>.png`; `snapshot` writes `qa-state-<name>.json`. Use server console commands to place specimens and position the test players. For synchronization tests, capture both clients, change specimen data on the server, then capture both again. The helper never sends server commands or changes the world.

After a resource reload, wait for its completion in the client log and request a separate screenshot so that the loading-screen fade has finished. Inspect screenshots directly; a screenshot being saved alone is not a visual pass. Remove the test companion when testing ends.
