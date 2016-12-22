package ninja.genuine.tooltips.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindingConfig extends KeyBinding {

	public KeyBindingConfig() {
		super("World-Tooltips", Keyboard.KEY_SUBTRACT, "World-Tooltips Config");
	}

	@Override
	public String getKeyCategory() {
		return "World-Tooltips";
	}

	@Override
	public int getKeyCodeDefault() {
		return Keyboard.KEY_SUBTRACT;
	}

	@Override
	public String getKeyDescription() {
		return "Configure World-Tooltips";
	}
}
