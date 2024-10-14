package mq.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import mq.net.Routing;

public class MessageFactory {
    private String host;
    private String channel;
    private byte[] version = {1, 0, 0, 0};
    private RoutingMod routingMod;
    private int command;
    private List<Routing> route = new ArrayList<>();
    private String queueName = "";
    private byte[] data;

    public MessageFactory(String host, String channel) {
        this.host = host;
        this.channel = channel;
        this.routingMod = new RoutingMod(DataType.MESSAGE, CommandType.NOP, MessageType.NOP, RoutingType.DIRECT);
        this.command = 0;
    }

    public MessageFactory routingMod(RoutingMod routingMod) {
        this.routingMod = routingMod;
        return this;
    }

    public MessageFactory command(int command) {
        this.command = command;
        return this;
    }

    public MessageFactory route(Routing routing) {
        this.route.add(routing);
        return this;
    }

    public MessageFactory queueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    public MessageFactory data(byte[] data) {
        if (data.length % 256 != 0) {
            int delta = 256 - data.length % 256;
            byte[] newData = new byte[data.length + delta];
            System.arraycopy(data, 0, newData, 0, data.length);
            this.data = newData;
        } else {
            this.data = data;
        }
        return this;
    }

    public byte[] build() {
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Serializing the channel name (32 bytes)
        byte[] channelBytes = channel.getBytes();
        buffer.put(channelBytes, 0, Math.min(channelBytes.length, 32));

        // Padding channelBytes to 32 bytes if needed
        for (int i = channelBytes.length; i < 32; i++) {
            buffer.put((byte) 0);
        }

        // Serializing the routingMod (4 bytes)
        buffer.put(routingMod.dataType.getValue());
        buffer.put(routingMod.messageType != null ? routingMod.messageType.getValue() : (byte) 0);
        buffer.put(routingMod.routingType.getValue());

        // Serializing the command (24 bytes)
        byte[] commandBytes = new byte[24];
        if (command == Command.CLOSE_CHANNEL) {
            byte[] closeCommandBytes = "CLOSE-CH".getBytes();
            System.arraycopy(closeCommandBytes, 0, commandBytes, 0, closeCommandBytes.length);
        }
        buffer.put(commandBytes);

        // Serializing the route (128 bytes)
        byte[][] routeArray = new byte[4][32];
        for (int i = 0; i < route.size() && i < 3; i++) {
            Routing routing = route.get(i);
            if (routing.getType() == Routing.RoutingType.ROUTE) {
                byte[] routeBytes = routing.getRoute().getBytes();
                System.arraycopy(routeBytes, 0, routeArray[i], 0, Math.min(routeBytes.length, 32));
            } else if (routing.getType() == Routing.RoutingType.STOP) {
                routeArray[i][0] = (byte) '!';
            }
        }
        for (byte[] r : routeArray) {
            buffer.put(r);
        }

        // Serializing the queue name (32 bytes)
        byte[] queueBytes = queueName.getBytes();
        buffer.put(queueBytes, 0, Math.min(queueBytes.length, 32));

        for (int i = queueBytes.length; i < 32; i++) {
            buffer.put((byte) 0);
        }

        // Adding data
        buffer.put(data);

        return buffer.array();
    }
}

