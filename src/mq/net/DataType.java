package mq.net;

public enum DataType {
    MESSAGE((byte) 0),
    COMMAND((byte) 1);

    private final byte value;

    DataType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
