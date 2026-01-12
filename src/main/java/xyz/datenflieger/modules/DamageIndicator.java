package xyz.datenflieger.modules;

import com.dwarslooper.cactus.client.feature.module.Category;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class DamageIndicator extends Module {
	public static DamageIndicator INSTANCE;

	public enum DisplayType {
		Hearts,
		Amount,
		Percent
	}

	private final SettingGroup sgGeneral;
	public final Setting<Boolean> includeAbsorption;
	public final Setting<Boolean> includePlayers;
	public final Setting<Boolean> includeOtherEntities;
	public final Setting<DisplayType> displayType;

	public DamageIndicator(Category category) {
		super("damage_indicator", category, new Module.Options().set(Flag.HUD_LISTED, false));
		INSTANCE = this;

		this.sgGeneral = this.settings.buildGroup("general");
		this.includeAbsorption = this.sgGeneral.add(new BooleanSetting("includeAbsorption", true));
		this.includePlayers = this.sgGeneral.add(new BooleanSetting("includePlayers", true));
		this.includeOtherEntities = this.sgGeneral.add(new BooleanSetting("includeOtherEntities", true));
		this.displayType = this.sgGeneral.add(new EnumSetting<>("displayType", DisplayType.Hearts));
	}
}
