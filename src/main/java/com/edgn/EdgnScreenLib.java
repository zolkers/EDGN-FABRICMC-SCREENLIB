package com.edgn;

import com.edgn.example.ExampleScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * to hide the example screen, the user has to set it by himself to true or false
 * Usually you shouldn't put this kind of code in the main class,
 * but since we won't ever use main for anything else, it does not really matter
 * the event also doesn't get registered if shouldSHowExampleScreen is false
 * @author EDGN
 */
public class EdgnScreenLib implements ModInitializer {
	public static final String MOD_ID = "edgnscreenlib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean shouldShowExampleScreen = true;
	private static final String CATEGORY = "EDGN'S SCREEN LIB";

	private static final KeyBinding openExampleScreenKey = new KeyBinding(
			"Example Screen",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_1,
			CATEGORY
	);

	@Override
	public void onInitialize() {
		if(shouldShowExampleScreen) {
			KeyBindingHelper.registerKeyBinding(openExampleScreenKey);

			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (openExampleScreenKey.wasPressed()) {
					client.setScreen(new ExampleScreen(null));
				}
			});

		}
	}
}