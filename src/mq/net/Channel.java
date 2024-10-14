package mq.net;

import java.io.*;
import java.util.concurrent.locks.*;

public class Channel {
    private String hostName;
    private String name;
    private boolean closed;
    private Session session;

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

    public byte[] read() throws IOException {
        return session.read(name);
    }

    public byte[] sendAndRead(byte[] data) throws IOException {
        session.send(data);
        return session.read(name);
    }
}

