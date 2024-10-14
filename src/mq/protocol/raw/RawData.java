package mq.protocol.raw;

import mq.routing.RoutingKey;

public class RawData {
    private Raw raw;
    private String channel;
    private String virtualHost;
    private RoutingKey routingKey;

    public RawData(Raw raw, String channel, String virtualHost, RoutingKey routingKey) {
        this.raw = raw;
        this.channel = channel;
        this.virtualHost = virtualHost;
        this.routingKey = routingKey;
    }

    // Getters and setters
    public Raw getRaw() {
        return raw;
    }

    public void setRaw(Raw raw) {
        this.raw = raw;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public RoutingKey getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(RoutingKey routingKey) {
        this.routingKey = routingKey;
    }

    @Override
    public String toString() {
        return "RawData{" +
                "raw=" + raw +
                ", channel='" + channel + '\'' +
                ", virtualHost='" + virtualHost + '\'' +
                ", routingKey=" + routingKey +
                '}';
    }
}

