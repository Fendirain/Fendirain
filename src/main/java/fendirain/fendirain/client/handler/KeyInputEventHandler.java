package fendirain.fendirain.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import fendirain.fendirain.reference.Key;
import fendirain.fendirain.utility.LogHelper;

public class KeyInputEventHandler {

    private static Key getPressedKeyBinding() {

        /*if (KeyBindings.[key].isPressed()) {
            return Key.[key];
        }*/

        return Key.UNKNOWN;
    }

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent keyInputEvent) {
        Key key = getPressedKeyBinding();
        if (key != Key.UNKNOWN) {
            LogHelper.info(key);
        }
    }
}
