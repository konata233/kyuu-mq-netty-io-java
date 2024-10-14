package mq.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mq.net.Routing;
import mq.protocol.DataHead;

public class MessageFactory {
    private String host;
    private String channel;
    private byte[] version = {1, 0, 0, 0};
    private RoutingMod routingMod;
    private int command;
    private List<Routing> route = new ArrayList<>();
    private String queueName = "";
    private byte[] data = new byte[256];

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
        byte[] channelBytes = channel.getBytes(StandardCharsets.UTF_8);
        buffer.put(channelBytes, 0, Math.min(channelBytes.length, 32));
        for (int i = channelBytes.length; i < 32; i++) {
            buffer.put((byte) 0);
        }
        channelBytes = new byte[32];
        buffer.slice(0, 32).get(channelBytes);
        buffer.clear();

        byte[] hostBytes = host.getBytes(StandardCharsets.UTF_8);
        buffer.put(hostBytes, 0, Math.min(hostBytes.length, 32));
        for (int i = hostBytes.length; i < 32; i++) {
            buffer.put((byte) 0);
        }
        hostBytes = new byte[32];
        buffer.slice(0, 32).get(hostBytes);
        buffer.clear();

        // Serializing the routingMod (4 bytes)
        buffer.put(routingMod.dataType.getValue());
        if (routingMod.messageType != MessageType.NOP) {
            buffer.put(routingMod.messageType.getValue());
        } else if (routingMod.commandType != CommandType.NOP) {
            buffer.put(routingMod.commandType.getValue());
        } else {
            buffer.put((byte) 0);
        }
        buffer.put(routingMod.routingType.getValue());
        buffer.put((byte) 0);
        byte[] routingModBytes = new byte[4];
        buffer.slice(0, 4).get(routingModBytes);
        buffer.clear();


        // Serializing the command (24 bytes)
        byte[] commandBytes = new byte[24];
        if (command == Command.CLOSE_CHANNEL) {
            byte[] closeCommandBytes = "CLOSE-CH".getBytes();
            System.arraycopy(closeCommandBytes, 0, commandBytes, 0, closeCommandBytes.length);
        }
        buffer.put(commandBytes);
        commandBytes = new byte[24];
        buffer.slice(0, 24).get(commandBytes);
        buffer.clear();

        // Serializing the route (128 bytes)
        byte[][] routeArray = new byte[3][32];
        for (int i = 0; i < route.size() && i < 3; i++) {
            Routing routing = route.get(i);
            if (routing.getType() == Routing.RoutingType.ROUTE) {
                byte[] routeBytes = routing.getRoute().getBytes();
                System.arraycopy(routeBytes, 0, routeArray[i], 0, Math.min(routeBytes.length, 32));
            } else if (routing.getType() == Routing.RoutingType.STOP) {
                routeArray[i][0] = (byte) '!';
            }
        }

        // Serializing the queue name (32 bytes)
        byte[] queueBytes = queueName.getBytes();
        buffer.put(queueBytes, 0, Math.min(queueBytes.length, 32));

        queueBytes = new byte[32];
        buffer.slice(0, 32).get(queueBytes);
        buffer.clear();

        byte[][] routeArrayComplete = new byte[4][32];
        for (int i = 0; i < routeArray.length; i++) {
            System.arraycopy(routeArray[i], 0, routeArrayComplete[i], 0, 32);
        }
        System.arraycopy(queueBytes, 0, routeArrayComplete[3], 0, 32);

        DataHead head = new DataHead(
                hostBytes,
                channelBytes,
                version,
                routingModBytes,
                commandBytes,
                routeArrayComplete[0],
                routeArrayComplete[1],
                routeArrayComplete[2],
                routeArrayComplete[3],
                1,
                data.length,
                1,
                (short) 0,
                (short) 0
        );
        byte[] headBytes = head.serialize();
        buffer.put(headBytes);

        // Adding data
        buffer.put(data);

        int size = this.data.length + 256;
        byte[] result = new byte[size];
        buffer.slice(0, size).get(result);
        return result;
    }
}

