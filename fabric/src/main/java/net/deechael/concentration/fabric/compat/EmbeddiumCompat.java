package net.deechael.concentration.fabric.compat;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import net.deechael.concentration.Concentration;
import net.deechael.concentration.FullscreenMode;
import net.deechael.concentration.fabric.config.ConcentrationConfigFabric;
import net.minecraft.network.chat.Component;
import org.embeddedt.embeddium.api.OptionGroupConstructionEvent;
import org.embeddedt.embeddium.client.gui.options.StandardOptions;

import java.util.List;

/**
 * Make Embedddium fullscreen option follow Concentration function
 *
 * @author DeeChael
 */
public class EmbeddiumCompat {

    public static void init() {
        OptionGroupConstructionEvent.BUS.addListener(event -> {
            if (event.getId() != null && event.getId().toString().equals(StandardOptions.Group.WINDOW.toString())) {
                List<Option<?>> options = event.getOptions();
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i).getId().toString().equals(StandardOptions.Option.FULLSCREEN.toString())) {
                        options.add(i, OptionImpl.createBuilder(FullscreenMode.class, SodiumGameOptionPages.getVanillaOpts())
                                // .setId(new ResourceLocation(ConcentrationConstants.MOD_ID, "fullscreen_mode"))
                                .setName(Component.translatable("concentration.option.fullscreen_mode"))
                                .setTooltip(Component.translatable("concentration.option.fullscreen_mode.tooltip"))
                                .setControl((opt) -> new CyclingControl<>(opt, FullscreenMode.class, new Component[]{
                                        Component.translatable("concentration.option.fullscreen_mode.borderless"),
                                        Component.translatable("concentration.option.fullscreen_mode.native")
                                }))
                                .setBinding((vanillaOpts, value) -> {
                                            ConcentrationConfigFabric.getInstance().setFullscreenMode(value);
                                            ConcentrationConfigFabric.getInstance().save();
                                            if (vanillaOpts.fullscreen().get()) {
                                                // If fullscreen turns on, re-toggle to changing the fullscreen mode instantly
                                                Concentration.toggleFullScreenMode(vanillaOpts, true);
                                            }
                                        },
                                        (vanillaOpts) -> ConcentrationConfigFabric.getInstance().getFullscreenMode()
                                )
                                .build());
                        options.set(
                                i + 1,
                                OptionImpl.createBuilder(Boolean.TYPE, SodiumGameOptionPages.getVanillaOpts())
                                        .setId(StandardOptions.Option.FULLSCREEN)
                                        .setName(Component.translatable("options.fullscreen"))
                                        .setTooltip(Component.translatable("sodium.options.fullscreen.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding(Concentration::toggleFullScreenMode, (opts) -> opts.fullscreen().get()).build()
                        );
                        break;
                    }
                }
            }
        });
    }

}
