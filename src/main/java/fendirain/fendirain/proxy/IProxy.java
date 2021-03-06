package fendirain.fendirain.proxy;

public interface IProxy {
    void registerRenderPreInit();

    void registerRenderInit();

    @SuppressWarnings("EmptyMethod")
    void registerKeyBindings();

    void registerEvents();
}
