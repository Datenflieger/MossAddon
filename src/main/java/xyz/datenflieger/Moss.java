package xyz.datenflieger;

import com.dwarslooper.cactus.client.addon.v2.ICactusAddon;
import com.dwarslooper.cactus.client.addon.v2.RegistryBus;
import com.dwarslooper.cactus.client.feature.module.Category;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.datenflieger.modules.BrandNameChanger;
import xyz.datenflieger.modules.ArrowTrails;

public class Moss implements ICactusAddon {
	public static final String MOD_ID = "moss";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Category MOSS_ADDON_CATEGORY = new Category("mossaddon", Items.MOSS_BLOCK.getDefaultStack());

	@Override
	public void onInitialize(RegistryBus bus) {
		LOGGER.info("Initializing Moss addon for Cactus Mod");
		// Register our custom category first
		bus.register(Category.class, (list, ctx) -> list.add(MOSS_ADDON_CATEGORY));
		// Register our module inside the custom category
		bus.register(com.dwarslooper.cactus.client.feature.module.Module.class, ctx -> new BrandNameChanger(MOSS_ADDON_CATEGORY));
		bus.register(com.dwarslooper.cactus.client.feature.module.Module.class, ctx -> new ArrowTrails(MOSS_ADDON_CATEGORY));
	}
}