package xyz.datenflieger.modules;

import com.dwarslooper.cactus.client.feature.module.Category;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import java.awt.Color;

public class ArrowTrails extends Module {
    public static ArrowTrails INSTANCE;

    private final SettingGroup sgVisual;
    public final Setting<Boolean> rainbow;
    public final Setting<Integer> rgbSpeed;
    public final Setting<Integer> particleDensity;
    public final Setting<Integer> particleSize10;
    public final Setting<ColorSetting.ColorValue> color;
    public final Setting<Boolean> ownOnly;
    public final Setting<Integer> offsetSpread1000;
    public final Setting<Integer> minSpeed100;

    public ArrowTrails(Category category) {
        super("arrow_trails", category, new Module.Options().set(Flag.HUD_LISTED, false));
        INSTANCE = this;
        this.sgVisual = this.settings.buildGroup("visual");
        this.rainbow = this.sgVisual.add(new BooleanSetting("rainbow", true));
        this.rgbSpeed = this.sgVisual.add(new IntegerSetting("rgbSpeed", 100).setMin(1).setMax(2000)).visibleIf(() -> (Boolean) this.rainbow.get());
        this.particleDensity = this.sgVisual.add(new IntegerSetting("particleDensity", 3).setMin(1).setMax(20));
        this.particleSize10 = this.sgVisual.add(new IntegerSetting("particleSize10", 10).setMin(1).setMax(50));
        this.color = this.sgVisual.add(new ColorSetting("color", new ColorSetting.ColorValue(new Color(0, 255, 127), false))).visibleIf(() -> !(Boolean) this.rainbow.get());
        this.ownOnly = this.sgVisual.add(new BooleanSetting("ownOnly", true));
        this.offsetSpread1000 = this.sgVisual.add(new IntegerSetting("offsetSpread1000", 100).setMin(0).setMax(500));
        this.minSpeed100 = this.sgVisual.add(new IntegerSetting("minSpeed100", 0).setMin(0).setMax(500));
    }
}
