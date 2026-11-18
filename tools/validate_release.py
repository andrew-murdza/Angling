#!/usr/bin/env python3
"""Validate Angling's resources and optional remapped publication jar (Python 3.11+).

This is a static release gate. It does not replace Forge startup or gameplay tests.
Only Angling-owned resource references are resolved here; Minecraft/Forge references
are resolved by the real runtime GameTests.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import pathlib
import re
import struct
import sys
import tomllib
import zipfile

ROOT = pathlib.Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src/main/resources"
JAVA = ROOT / "src/main/java/com/eightsidedsquare/angling"
EXPECTED_ENTITIES = {
    "fry", "sunfish", "pelican", "nautilus", "sea_slug", "crab", "dongfish",
    "catfish", "seahorse", "bubble_eye", "anomalocaris", "anglerfish", "mahi_mahi",
}
EXPECTED_FEATURES = {"oyster_reef", "clams", "wormy_block", "patch_sargassum", "patch_duckweed", "patch_papyrus"}


class Validation:
    def __init__(self):
        self.errors: list[str] = []
        self.checks = 0

    def check(self, condition, message):
        self.checks += 1
        if not condition:
            self.errors.append(message)

    def exists(self, relative, context):
        self.check((RESOURCES / relative).is_file(), f"{context}: missing {relative}")


def unique_object(pairs):
    result = {}
    for key, value in pairs:
        if key in result:
            raise ValueError(f"Duplicate JSON key: {key}")
        result[key] = value
    return result


def walk(value):
    yield value
    if isinstance(value, dict):
        for child in value.values():
            yield from walk(child)
    elif isinstance(value, list):
        for child in value:
            yield from walk(child)


def own_path(identifier):
    return identifier.removeprefix("angling:") if isinstance(identifier, str) and identifier.startswith("angling:") else None


def class_references(data):
    """Read actual JVM class constants, excluding harmless Mixin target strings."""
    count = struct.unpack_from(">H", data, 8)[0]
    offset, index, strings, classes = 10, 1, {}, []
    fixed = {3: 4, 4: 4, 5: 8, 6: 8, 8: 2, 9: 4, 10: 4, 11: 4,
             12: 4, 15: 3, 16: 2, 17: 4, 18: 4, 19: 2, 20: 2}
    while index < count:
        tag = data[offset]
        offset += 1
        if tag == 1:
            length = struct.unpack_from(">H", data, offset)[0]
            offset += 2
            strings[index] = data[offset:offset + length].decode("utf-8", errors="replace")
            offset += length
        elif tag == 7:
            classes.append(struct.unpack_from(">H", data, offset)[0])
            offset += 2
        else:
            offset += fixed[tag]
            if tag in (5, 6):
                index += 1
        index += 1
    return {strings[index].lstrip("[").removeprefix("L").removesuffix(";") for index in classes}


def registered_ids():
    blocks_source = (JAVA / "core/AnglingBlocks.java").read_text()
    items_source = (JAVA / "core/AnglingItems.java").read_text()
    blocks = set(re.findall(r'= create\("([a-z0-9_]+)"', blocks_source))
    items = set(re.findall(r'= create\("([a-z0-9_]+)"', items_source))
    items |= {s + "_bucket" for s in re.findall(r'= createBucket\("([a-z0-9_]+)"', items_source)}
    items |= {s + "_spawn_egg" for s in re.findall(r'= createSpawnEgg\("([a-z0-9_]+)"', items_source)}
    items |= {re.search(r'create\("([a-z0-9_]+)"', line)[1]
              for line in blocks_source.splitlines() if '= create("' in line and line.rstrip().endswith(', true);')}
    entities = set(re.findall(r'EntityType\.Builder.*?\.build\("angling:([a-z0-9_]+)"\)', (JAVA / "core/AnglingEntities.java").read_text()))
    registries = {"blocks": blocks, "items": items, "entity_types": entities}
    for folder, cls in {"crab_variant": "CrabVariant", "sunfish_variant": "SunfishVariant",
                        "sea_slug_color": "SeaSlugColor", "sea_slug_pattern": "SeaSlugPattern"}.items():
        registries[folder] = set(re.findall(r'= create\("([a-z0-9_]+)"', (JAVA / f"common/entity/util/{cls}.java").read_text()))
    return registries


def validate_resources(v):
    documents = {}
    for file in sorted(RESOURCES.rglob("*.json")):
        relative = file.relative_to(RESOURCES).as_posix()
        try:
            documents[relative] = json.loads(file.read_text(encoding="utf-8-sig"), object_pairs_hook=unique_object)
            v.check(True, relative)
        except (ValueError, UnicodeError) as error:
            v.check(False, f"{relative}: {error}")
    registries = registered_ids()
    block_atlas = documents.get("assets/minecraft/atlases/blocks.json", {})
    atlas_textures = {
        source.get("resource") for source in block_atlas.get("sources", [])
        if source.get("type") in ("minecraft:single", "single")
        and source.get("sprite", source.get("resource")) == source.get("resource")
    }
    v.check(registries["entity_types"] == EXPECTED_ENTITIES, "Entity inventory differs from the 13 upstream entities")
    for relative, data in documents.items():
        if relative.startswith(("assets/", "data/")):
            v.check(relative == relative.lower(), f"Uppercase resource path: {relative}")
        for node in walk(data):
            if not isinstance(node, dict):
                continue
            model = own_path(node.get("model"))
            if model:
                v.exists(f"assets/angling/models/{model}.json", relative)
            for field in ("item",):
                item = own_path(node.get(field))
                if item:
                    v.check(item in registries["items"], f"{relative}: unknown item angling:{item}")
            if node.get("type") in ("minecraft:item", "item"):
                item = own_path(node.get("name"))
                if item:
                    v.check(item in registries["items"], f"{relative}: unknown loot item {item}")
            block = own_path(node.get("Name"))
            if block:
                v.check(block in registries["blocks"], f"{relative}: unknown block state {block}")
        if relative.startswith("assets/angling/models/"):
            parent = own_path(data.get("parent"))
            if parent:
                v.exists(f"assets/angling/models/{parent}.json", relative)
            for texture in data.get("textures", {}).values():
                texture = own_path(texture)
                if texture:
                    v.exists(f"assets/angling/textures/{texture}.png", relative)
                    if texture.startswith("entity/"):
                        v.check(f"angling:{texture}" in atlas_textures,
                                f"{relative}: entity texture angling:{texture} needs an explicit single source in assets/minecraft/atlases/blocks.json")
        if relative.startswith("data/") and "/tags/" in relative:
            namespace, tag_path = relative[5:].split("/tags/", 1)
            kind = tag_path.rsplit("/", 1)[0]
            registry_kind = kind.removeprefix("angling/")
            for entry in data.get("values", []):
                required = not isinstance(entry, dict) or entry.get("required", True)
                entry = entry.get("id") if isinstance(entry, dict) else entry
                if entry.startswith("#angling:") and required:
                    v.exists(f"data/angling/tags/{kind}/{entry[9:]}.json", relative)
                elif entry.startswith("angling:") and registry_kind in registries and required:
                    v.check(entry[8:] in registries[registry_kind], f"{relative}: unknown {kind} entry {entry}")
                v.check(not (required and entry.startswith("#c:")), f"{relative}: required Fabric convention tag {entry}")
        if relative.startswith("data/angling/worldgen/placed_feature/"):
            feature = own_path(data.get("feature"))
            if feature:
                v.exists(f"data/angling/worldgen/configured_feature/{feature}.json", relative)
            v.check(any(p.get("type") == "minecraft:biome" for p in data.get("placement", [])), f"{relative}: missing biome filter")
        if relative.startswith("data/angling/advancements/"):
            for criterion in data.get("criteria", {}).values():
                if criterion.get("trigger") in ("minecraft:item_used_on_block", "minecraft:placed_block", "minecraft:allay_drop_item_on_block"):
                    v.check(isinstance(criterion.get("conditions", {}).get("location"), list), f"{relative}: 1.20.1 location criterion must be an array of loot predicates")
        if relative.startswith("data/angling/forge/biome_modifier/"):
            tag = data.get("biomes", "")
            if isinstance(tag, str) and tag.startswith("#angling:"):
                v.exists(f"data/angling/tags/worldgen/biome/{tag[9:]}.json", relative)
            if data.get("type") == "forge:add_features":
                for feature in [data["features"]] if isinstance(data["features"], str) else data["features"]:
                    if own_path(feature):
                        v.exists(f"data/angling/worldgen/placed_feature/{feature[8:]}.json", relative)
            if data.get("type") == "forge:add_spawns":
                for spawn in [data["spawners"]] if isinstance(data["spawners"], dict) else data["spawners"]:
                    v.check(own_path(spawn["type"]) in EXPECTED_ENTITIES, f"{relative}: invalid spawn type")
                    v.check(0 < spawn["minCount"] <= spawn["maxCount"] and spawn["weight"] > 0, f"{relative}: invalid spawn weight/count")
    for name, definition in documents.get("assets/angling/sounds.json", {}).items():
        for sound in definition["sounds"]:
            identifier = sound if isinstance(sound, str) else sound["name"]
            if own_path(identifier):
                v.exists(f"assets/angling/sounds/{identifier[8:]}.ogg", name)
    animations = {name for path, data in documents.items() if "/animations/" in path for name in data.get("animations", {})}
    for source in JAVA.rglob("*.java"):
        text = source.read_text(encoding="utf-8-sig")
        for animation in re.findall(r'"(animation\.[a-z0-9_.]+)"', text):
            v.check(animation in animations, f"{source.name}: missing animation {animation}")
        for resource in re.findall(r'"((?:geo|textures|animations)/[^"+]+\.(?:png|json))"', text):
            v.exists(f"assets/angling/{resource}", source.name)
        v.check(not re.search(r'^import (?:net\.fabricmc|dev\.onyxstudios\.cca|software\.bernie\.geckolib3)\.', text, re.M), f"{source.name}: legacy loader/library import")
    lang = documents.get("assets/angling/lang/en_us.json", {})
    for name in registries["items"]:
        v.exists(f"assets/angling/models/item/{name}.json", name)
        v.check(f"item.angling.{name}" in lang or f"block.angling.{name}" in lang, f"Missing item translation: {name}")
    for name in registries["blocks"]:
        v.exists(f"assets/angling/blockstates/{name}.json", name)
    for name in EXPECTED_FEATURES:
        for kind in ("configured_feature", "placed_feature"):
            v.exists(f"data/angling/worldgen/{kind}/{name}.json", name)
        v.exists(f"data/angling/forge/biome_modifier/add_{name}.json", name)
    for kind in ("sunfish_variant", "crab_variant", "sea_slug_color", "sea_slug_pattern"):
        v.check(any((RESOURCES / f"data/angling/tags/angling/{kind}").glob("*.json")), f"Custom registry tags must use Forge's namespaced folder: {kind}")
    return {"json_files": len(documents), "items": len(registries["items"]), "blocks": len(registries["blocks"]), "entities": len(registries["entity_types"]), "animations": len(animations)}


def validate_jar(v, jar):
    v.check(not any(word in jar.stem for word in ("-dev", "-sources", "-javadoc")), "Select the remapped release jar, not a development/sources jar")
    with zipfile.ZipFile(jar) as archive:
        names = set(archive.namelist())
        for required in ("META-INF/mods.toml", "META-INF/MANIFEST.MF", "META-INF/accesstransformer.cfg", "pack.mcmeta", "LICENSE", "angling.mixins.json"):
            v.check(required in names, f"Jar is missing {required}")
        for name in names:
            v.check(not (name == "fabric.mod.json" or name.endswith((".java", ".accesswidener")) or "/gametest/" in name.lower() or name.startswith(("net/fabricmc/", "dev/onyxstudios/", "software/bernie/", "local/anglingqa/"))), f"Unwanted publication entry: {name}")
            if name.endswith(".class"):
                data = archive.read(name)
                v.check(struct.unpack(">H", data[6:8])[0] <= 61, f"{name}: requires newer than Java 17")
                for residue in (b"net/fabricmc/", b"dev/onyxstudios/cca/", b"software/bernie/geckolib3/"):
                    v.check(residue not in data, f"{name}: unresolved dependency {residue.decode()}")
                yarn_prefixes = ("net/minecraft/entity/", "net/minecraft/block/", "net/minecraft/registry/",
                                 "net/minecraft/nbt/Nbt", "net/minecraft/item/", "net/minecraft/util/Identifier")
                unresolved = sorted(ref for ref in class_references(data) if ref.startswith(yarn_prefixes))
                v.check(not unresolved, f"{name}: unremapped development class references: {unresolved}")
        for source in RESOURCES.rglob("*"):
            if source.is_file() and source.suffix != ".accesswidener":
                relative = source.relative_to(RESOURCES).as_posix()
                v.check(relative in names, f"Resource missing from jar: {relative}")
                if relative in names and relative != "META-INF/mods.toml":
                    expected = source.read_bytes()
                    actual = archive.read(relative)
                    if source.suffix == ".json":
                        expected, actual = json.loads(expected), json.loads(actual)
                    v.check(actual == expected, f"Jar contains stale or modified resource: {relative}")
        if "META-INF/mods.toml" in names:
            raw = archive.read("META-INF/mods.toml").decode()
            v.check("${" not in raw, "Unexpanded version placeholder in mods.toml")
            metadata = tomllib.loads(raw)
            declared_version = re.search(r"(?m)^mod_version=(.+)$", (ROOT / "gradle.properties").read_text()).group(1).strip()
            v.check(any(mod.get("modId") == "angling" and mod.get("version") == declared_version for mod in metadata.get("mods", [])),
                    "Jar version must match gradle.properties")
            v.check(metadata.get("license") == "CC0-1.0", "License metadata must retain CC0-1.0")
            v.check(metadata.get("modLoader") == "javafml", "Incorrect Forge mod loader metadata")
            deps = {d["modId"]: d for d in metadata.get("dependencies", {}).get("angling", [])}
            for name in ("forge", "minecraft", "geckolib"):
                v.check(name in deps and deps[name].get("mandatory") and deps[name].get("side") == "BOTH", f"Missing mandatory BOTH dependency {name}")
        if "angling.mixins.json" in names:
            mixins = json.loads(archive.read("angling.mixins.json"))
            refmap_name = mixins.get("refmap")
            v.check(refmap_name in names, f"Missing mixin refmap: {refmap_name}")
            if refmap_name in names:
                refmap = json.loads(archive.read(refmap_name))
                v.check(bool(refmap.get("mappings")), "Empty mixin refmap")
        if "META-INF/MANIFEST.MF" in names:
            v.check(b"MixinConfigs: angling.mixins.json" in archive.read("META-INF/MANIFEST.MF"), "Mixin config missing from jar manifest")
    return {"jar": str(jar), "bytes": jar.stat().st_size, "sha256": hashlib.sha256(jar.read_bytes()).hexdigest()}


def validate_gametest_log(v, path):
    text = path.read_text(encoding="utf-8-sig", errors="replace")
    expected = (ROOT / "src/gametest/java/com/eightsidedsquare/angling/gametest/AnglingGameTests.java").read_text().count("@GameTest(")
    summaries = re.findall(r"(\d+) GAME TESTS COMPLETE", text)
    completed = int(summaries[-1]) if summaries else 0
    v.check(completed == expected, f"GameTest log must report completion of {expected} tests, got {completed}")
    v.check("required tests passed" in text, "GameTest success summary is missing")
    v.check(not re.search(r"\d+ required tests failed| failed!", text), "GameTest log contains failed tests")
    v.check("Game test server shutting down" in text, "GameTest server did not shut down normally")
    for failure in ("Parsing error loading custom advancement", "Couldn't load advancement", "Couldn't load tag",
                    "Failed to load registries", "Mixin apply failed", "InvalidInjectionException", "Exception in server tick loop"):
        v.check(failure not in text, f"GameTest log contains runtime failure: {failure}")
    return {"gametest_log": str(path), "gametests_completed": completed, "gametests_expected": expected,
            "gametest_log_sha256": hashlib.sha256(path.read_bytes()).hexdigest()}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jar", type=pathlib.Path, help="Remapped release jar to audit")
    parser.add_argument("--gametest-log", type=pathlib.Path, help="Verify the real runtime completion summary and resource-loading errors")
    parser.add_argument("--report", type=pathlib.Path, help="Write the measured JSON report")
    args = parser.parse_args()
    validation = Validation()
    report = validate_resources(validation)
    if args.jar:
        report.update(validate_jar(validation, args.jar))
    if args.gametest_log:
        report.update(validate_gametest_log(validation, args.gametest_log))
    report.update(checks=validation.checks, passed=not validation.errors, errors=validation.errors)
    if args.report:
        args.report.parent.mkdir(parents=True, exist_ok=True)
        args.report.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(report, indent=2))
    return 0 if report["passed"] else 1


if __name__ == "__main__":
    sys.exit(main())
