package mq.routing;

public class RoutingKey {
    public String[] key = new String[4];
    public RoutingType type = RoutingType.Direct;

    public RoutingKey(String[] key, RoutingType type) {
        this.key = key;
        this.type = type;
    }
}
