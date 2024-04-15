package spiderweb.bridges;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class BridgeFactory {

    /**
     * Builds a bridge with the specified parameters.
     *
     * @param distance      The distance of the bridge.
     * @param initialStrand The initial strand connected by the bridge.
     * @param finalStrand   The final strand connected by the bridge.
     * @param initialPoint  The initial point of the bridge.
     * @param finalPoint    The final point of the bridge.
     * @param color         The color of the bridge.
     * @param type          The type of the bridge.
     * @return The bridge with the specified parameters.
     */
    public static Bridge buildBridge(int distance, int initialStrand, int finalStrand, Point initialPoint, Point finalPoint, String color, Bridge.Types type) {

        Bridge bridge;

        HashMap<Bridge.Types, Class<? extends Bridge>> bridgeTypes = new HashMap<>();

        bridgeTypes.put(Bridge.Types.NORMAL, NormalBridge.class);
        bridgeTypes.put(Bridge.Types.FIXED, FixedBridge.class);
        bridgeTypes.put(Bridge.Types.TRANSFORMER, TransformerBridge.class);
        bridgeTypes.put(Bridge.Types.WEAK, WeakBridge.class);
        bridgeTypes.put(Bridge.Types.MOBILE, MobileBridge.class);
        bridgeTypes.put(Bridge.Types.FUNNY, FunnyBridge.class);

        try {
            bridge = bridgeTypes.get(type).getDeclaredConstructor(int.class, int.class, int.class, Point.class, Point.class, String.class).newInstance(distance, initialStrand, finalStrand, initialPoint, finalPoint, color);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return bridge;
    }
}
