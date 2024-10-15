package mq.net;

import mq.protocol.raw.DataPack;

import java.io.*;

public class Channel {
    private final String hostName;

    public String getHostName() {
        return hostName;
    }

    public String getName() {
        return name;
    }

    public Session getSession() {
        return session;
    }

    private final String name;
    private boolean closed;
    private final Session session;

    public Channel(String hostName, String name, Session session) {
        this.hostName = hostName;
        this.name = name;
        this.session = session;
        this.closed = false;
    }

    public boolean isClosed() {
        return closed;
    }

    public MessageFactory getFactory() {
        return new MessageFactory(this.hostName, this.name);
    }

    public void close() throws IOException {
        MessageFactory factory = this.getFactory();
        byte[] data = factory.command(Command.CLOSE_CHANNEL).build();
        this.send(data);
        this.closed = true;
    }

    public void send(byte[] data) throws IOException {
        session.send(data);
    }

    public DataPack read() throws IOException {
        return session.read(name);
    }

    public DataPack sendAndRead(byte[] data) throws IOException {
        session.send(data);
        return session.read(name);
    }
}

