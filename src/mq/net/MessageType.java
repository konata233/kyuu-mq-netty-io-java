package mq.net;

public enum MessageType {
    PUSH((byte) 0),
    FETCH((byte) 1),
    NOP((byte) 0xf);

    private final byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
