![Banner](https://i.imgur.com/lEsZNHy.jpeg)

A lightweight Fabric addon for [Cactus Mod](https://cactusmod.xyz/) on Minecraft 1.21.10. It adds a new category and extra utility/visual modules.

## Features

- __Brand Name Changer__
  - Change the client brand reported by the game (e.g. `lunarclient`, `fabric`, `forge`, `vanilla`, custom).
  - Toggle the module and type your brand in the setting `Brand`.
  - Implemented via a Mixin to `ClientBrandRetriever#getClientModName`.

- __Arrow Trails__
  - Colorful particle trails following flying arrows.
  - Settings: Rainbow mode, RGB speed, fixed color, particles per tick, particle size, only own arrows, offset spread, minimum speed.

- __Damage Indicator__
  - Nice Damage Indicator
- more comming soon

## Download

- Download the built JAR from the repository __Releases__.
- Alternatively, you can build the mod yourself (see the section "For Advanced/Technical").

## Prerequisites

- Minecraft: 1.21.10
- Fabric Loader
- Fabric API (matching Minecraft 1.21.10)
- Cactus Mod

## Installation

1. Install Fabric Loader (if you don't have it yet).
2. Install Fabric API (matching Minecraft 1.21.10).
3. Install Cactus Mod.
4. Download the __Moss Addon__ JAR from the repository __Releases__.
5. Put all JARs into your `mods/` folder.
6. Start Minecraft.

## In‑game usage

1. Open the Cactus Mod modules UI.
2. Switch to the __Moss Addon__ category.
3. Enable what you want:
   - __Brand Name Changer__: enable and set your desired value in the "Brand" field.
   - __Arrow Trails__: enable and adjust the settings to your liking.

---

## For advanced users / technical

- Built with Fabric and Java 21.
- Cactus addon entrypoint: `xyz.datenflieger.Moss` (implements `ICactusAddon`).
- Mixins: `BrandClientBrandRetrieverMixin` (client brand), `ArrowEntityTickMixin` (arrow particles).
- Build locally:
  ```bash
  ./gradlew build
  ```
  The built JAR will be under `build/libs/`.

## License

This project is licensed under the terms of the license file included in this repository (`LICENSE`).
