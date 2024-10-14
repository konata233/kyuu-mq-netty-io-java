package mq.protocol;

public interface ISerializable<T> {
    byte[] serialize();
    T deserialize(byte[] data);
}
