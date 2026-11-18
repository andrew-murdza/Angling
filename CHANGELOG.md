# 1.0.1-forge.1 — Minecraft 1.20.1

- Ported Angling 1.0.1 from Fabric 1.19.2 to Forge 1.20.1 and GeckoLib 4.
- Preserved all 13 creatures, fish buckets, aquarium blocks, plants, sounds, models, and animations.
- Added an Angling creative tab and Forge biome modifiers for natural spawns and world features.
- Replaced Cardinal Components with persistent fish state, including legacy NBT reads and bucket transfers.
- Migrated variant registries and tags to Forge, with string IDs for entity appearance synchronization.
- Fixed server-side roe species placement, egg color persistence, starfish/urchin save synchronization, frozen tadpole bucket behavior, and water-adjacent feature checks.
- Fixed collision boxes when hatching roe/slug eggs, growing fry, and releasing a pelican's previous catch.
- Registered the anemone texture in Minecraft 1.20.1's block atlas and aligned carried creatures with the pelican's animated jaw.
- Added isolated runtime GameTests and resource/release archive validation.

See TESTING.md for measured validation and outstanding manual compatibility checks.
