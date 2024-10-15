package mq.net;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

import mq.protocol.DataHead;
import mq.protocol.raw.DataPack;

public class Session {
    private final ReentrantReadWriteLock streamLock = new ReentrantReadWriteLock();
    private final Socket stream;
    private final String host;
    private final Map<String, ReentrantReadWriteLock> channels = new ConcurrentHashMap<>();
    private final Map<String, Deque<byte[]>> cache = new ConcurrentHashMap<>();

    public Session(String host, String address, int port) throws IOException {
        this.host = host;
        this.stream = new Socket(address, port);
    }

    public synchronized void init(Session selfRef) throws SocketException {
        this.setWriteTimeout(1024);
        this.setReadTimeout(1024);
    }

    public void setReadTimeout(long millis) throws SocketException {
        streamLock.writeLock().lock();
        try {
            stream.setSoTimeout((int) millis);
        } finally {
            streamLock.writeLock().unlock();
        }
    }

    public void setWriteTimeout(long millis) throws SocketException {
        streamLock.writeLock().lock();
        try {
            stream.setSoTimeout((int) millis);
        } finally {
            streamLock.writeLock().unlock();
        }
    }

    public synchronized Channel createChannel(String name) {
        if (channels.containsKey(name)) {
            return null;
        }

        Channel channel = new Channel(host, name, this);
        cache.put(name, new LinkedList<>());
        channels.put(name, new ReentrantReadWriteLock());
        return channel;
    }

    public synchronized void dropChannel(String name) {
        ReentrantReadWriteLock channelLock = channels.get(name);
        if (channelLock != null) {
            channelLock.writeLock().lock();
            try {
                // Close the channel
                Channel channel = new Channel(host, name, this);
                if (!channel.isClosed()) {
                    channel.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                channelLock.writeLock().unlock();
            }
        }
        cache.remove(name);
        channels.remove(name);
    }

    public synchronized void close() throws IOException {
        channels.clear();
        streamLock.writeLock().lock();
        try {
            stream.close();
        } finally {
            streamLock.writeLock().unlock();
        }
    }

    public void send(byte[] data) throws IOException {
        streamLock.writeLock().lock();
        try {
            OutputStream out = stream.getOutputStream();
            out.write(data);
            out.flush();
        } finally {
            streamLock.writeLock().unlock();
        }
    }

    public DataPack read(String channelName) throws IOException {
        streamLock.writeLock().lock();
        try {
            InputStream in = stream.getInputStream();
            byte[] buffer = new byte[256];
            if (in.read(buffer) != -1) {
                // Deserialize the DataHead
                DataHead head = new DataHead().deserialize(buffer);

                byte[] message = new byte[head.getSliceCount() * head.getSliceSize()];
                int bytes_read = in.read(message);
                // todo: cache.

                if (!Objects.equals(channelName, head.getChannelName())) {
                    this.cache.get(head.getChannelName()).add(message);
                    if (!this.cache.get(channelName).isEmpty()) {
                        byte[] message_cached = this.cache.get(head.getChannelName()).poll();
                        return new DataPack(head, message_cached);
                    }
                    return new DataPack();
                } else {
                    if (!this.cache.get(channelName).isEmpty()) {
                        this.cache.get(channelName).add(message);
                        byte[] message_cached = this.cache.get(channelName).poll();
                        return new DataPack(head, message_cached);
                    }
                    return new DataPack(head, message);
                }
            } else {
                return null;
            }
        } finally {
            streamLock.writeLock().unlock();
        }
    }
}

