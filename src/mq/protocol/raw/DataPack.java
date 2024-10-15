package mq.protocol.raw;

import mq.protocol.DataHead;

public class DataPack {
    public DataHead head;
    public byte[] data;
    public boolean isEmpty;

    public DataPack(DataHead head, byte[] data) {
        this.head = head;
        this.data = data;
        this.isEmpty = false;
    }

    public DataPack() {
        this.isEmpty = true;
    }
}
