package mq.net;

public enum RoutingType {
    DIRECT((byte) 0),
    TOPIC((byte) 1),
    FANOUT((byte) 2),
    NOP((byte) 0xf);

    private final byte value;

    RoutingType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
