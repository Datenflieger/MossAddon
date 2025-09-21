package xyz.datenflieger.modules;

import com.dwarslooper.cactus.client.feature.module.Category;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class BrandNameChanger extends Module {
    public static BrandNameChanger INSTANCE;

    public final Setting<String> brandName;

    public BrandNameChanger(Category category) {
        super("brand_name_changer", category, new Module.Options().set(Module.Flag.RUN_IN_MENU, true).set(Module.Flag.HUD_LISTED, false));
        INSTANCE = this;
        this.brandName = this.mainGroup.add(new StringSetting("brand", "moss"));
    }

    public String getCustomBrandOrNull() {
        if (this.active()) {
            String value = this.brandName.get();
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
