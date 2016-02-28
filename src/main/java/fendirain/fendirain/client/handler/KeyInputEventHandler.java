package fendirain.fendirain.client.handler;

import fendirain.fendirain.reference.Key;
import fendirain.fendirain.utility.helper.LogHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@SuppressWarnings("UnusedParameters")
public class KeyInputEventHandler {

    private static Key getPressedKeyBinding() {
        //if (KeyBindings.[key].isPressed()) return Key.[key];
        return Key.UNKNOWN;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent keyInputEvent) {
        Key key = getPressedKeyBinding();
        if (key != Key.UNKNOWN) LogHelper.info(key);
    }
}
