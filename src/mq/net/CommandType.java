package mq.net;

public enum CommandType {
    NEW_QUEUE((byte) 0),
    NEW_EXCHANGE((byte) 1),
    NEW_BINDING((byte) 2),
    DROP_QUEUE((byte) 3),
    DROP_EXCHANGE((byte) 4),
    DROP_BINDING((byte) 5),
    NOP((byte) 0xf);

    private final byte value;

    CommandType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
