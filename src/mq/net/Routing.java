package mq.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Routing {
    public enum RoutingType {
        ROUTE,
        ANY,
        STOP
    }

    private RoutingType type;
    private String route;

    private Routing(RoutingType type, String route) {
        this.type = type;
        this.route = route;
    }

    private Routing(RoutingType type) {
        this.type = type;
        this.route = null;
    }

    public static Routing Route(String route) {
        return new Routing(RoutingType.ROUTE, route);
    }

    public static Routing Any() {
        return new Routing(RoutingType.ANY);
    }

    public static Routing Stop() {
        return new Routing(RoutingType.STOP);
    }

    public byte[] serialize() {
        byte[] serialized = new byte[32];

        switch (this.type) {
            case ROUTE:
                ByteBuffer buffer = ByteBuffer.allocate(32);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                byte[] routeBytes = route.getBytes(StandardCharsets.UTF_8);
                buffer.put(routeBytes, 0, Math.min(routeBytes.length, 32));
                byte[] dst = new byte[32];
                buffer.get(dst);
                System.arraycopy(dst, 0, serialized, 0, Math.min(routeBytes.length, 32));
                break;
            case ANY:
                serialized[0] = '*';
                break;
            case STOP:
                serialized[0] = '!';
                break;
        }

        return serialized;
    }

    public RoutingType getType() {
        return type;
    }

    public String getRoute() {
        return route;
    }

    @Override
    public String toString() {
        switch (this.type) {
            case ROUTE:
                return "Route(" + route + ")";
            case ANY:
                return "Any";
            case STOP:
                return "Stop";
            default:
                return super.toString();
        }
    }
}

