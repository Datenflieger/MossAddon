![Banner](https://i.imgur.com/lEsZNHy.jpeg)

A lightweight Fabric addon for [Cactus Mod](https://cactusmod.xyz/) on Minecraft 1.21.11. It adds a new category and extra utility/visual modules.

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

- Minecraft: 1.21.11
- Fabric Loader
- Fabric API (matching Minecraft 1.21.11)
- Cactus Mod

## Installation

1. Install Fabric Loader (if you don't have it yet).
2. Install Fabric API (matching Minecraft 1.21.11).
3. Install Cactus Mod.
4. Download the __Moss Addon__ JAR from the repository __Releases__.
5. Put all JARs into your `mods/` folder.
6. Start Minecraft.

## In‑game usage

1. Open the Cactus Mod modules UI.
2. Switch to the __Moss Addon__ category.
3. Enable what you want

## License

This project is licensed under the terms of the license file included in this repository (`LICENSE`).
