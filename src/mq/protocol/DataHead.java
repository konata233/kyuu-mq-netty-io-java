package mq.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataHead implements ISerializable<DataHead> {
    private byte[] virtualHost = new byte[32];
    private byte[] channel = new byte[32];
    private byte[] version = new byte[4];
    private byte[] routingMod = new byte[4];
    private byte[] command = new byte[24];
    private byte[] route0 = new byte[32];
    private byte[] route1 = new byte[32];
    private byte[] route2 = new byte[32];
    private byte[] route3 = new byte[32];
    private int sliceCount;
    private int sliceSize;
    private int count;
    private short errCode;
    private short ack;
    private final byte[] reserved = new byte[16];

    public DataHead() {
        this.sliceCount = 1;
        this.sliceSize = 1024;
        this.count = 1;
        this.errCode = 0;
        this.ack = 0;
    }

    public DataHead(
            byte[] virtualHost,
            byte[] channel,
            byte[] version,
            byte[] routingMod,
            byte[] command,
            byte[] route0,
            byte[] route1,
            byte[] route2,
            byte[] route3,
            int sliceCount,
            int sliceSize,
            int count,
            short msgSign,
            short ack
    ) {
        this.virtualHost = virtualHost;
        this.channel = channel;
        this.version = version;
        this.routingMod = routingMod;
        this.command = command;
        this.route0 = route0;
        this.route1 = route1;
        this.route2 = route2;
        this.route3 = route3;
        this.sliceCount = sliceCount;
        this.sliceSize = sliceSize;
        this.count = count;
        this.errCode = msgSign;
        this.ack = ack;
    }

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(256).order(ByteOrder.LITTLE_ENDIAN); // 计算数据总大小
        buffer.put(virtualHost);
        buffer.put(channel);
        buffer.put(version);
        buffer.put(routingMod);
        buffer.put(command);
        buffer.put(route0);
        buffer.put(route1);
        buffer.put(route2);
        buffer.put(route3);
        buffer.putInt(sliceCount);
        buffer.putInt(sliceSize);
        buffer.putInt(count);
        buffer.putShort(errCode);
        buffer.putShort(ack);
        buffer.put(reserved);
        return buffer.array();
    }

    public DataHead deserialize(byte[] data) {
        if (data.length != 256) {
            throw new IllegalArgumentException("Invalid data length");
        }

        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        buffer.get(this.virtualHost);
        buffer.get(this.channel);
        buffer.get(this.version);
        buffer.get(this.routingMod);
        buffer.get(this.command);
        buffer.get(this.route0);
        buffer.get(this.route1);
        buffer.get(this.route2);
        buffer.get(this.route3);
        this.sliceCount = buffer.getInt();
        this.sliceSize = buffer.getInt();
        this.count = buffer.getInt();
        this.errCode = buffer.getShort();
        this.ack = buffer.getShort();
        buffer.get(this.reserved);
        return this;
    }

    public short getAck() {
        return ack;
    }

    public short getErrCode() {
        return errCode;
    }

    public int getCount() {
        return count;
    }

    public int getSliceSize() {
        return sliceSize;
    }

    public int getSliceCount() {
        return sliceCount;
    }

    public byte[] getRoute3() {
        return route3;
    }

    public byte[] getRoute2() {
        return route2;
    }

    public byte[] getRoute1() {
        return route1;
    }

    public byte[] getRoute0() {
        return route0;
    }

    public byte[] getCommand() {
        return command;
    }

    public byte[] getRoutingMod() {
        return routingMod;
    }

    public byte[] getVersion() {
        return version;
    }

    public byte[] getChannel() {
        return channel;
    }

    public String getChannelName() {
        return new String(channel).trim().strip();
    }

    public byte[] getVirtualHost() {
        return virtualHost;
    }

    public String getVirtualHostName() {
        return new String(virtualHost).trim().strip();
    }
}
