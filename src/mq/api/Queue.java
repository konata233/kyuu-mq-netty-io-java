package mq.api;

import mq.net.*;
import mq.protocol.DataHead;
import mq.protocol.raw.DataPack;
import mq.protocol.raw.ErrorCode;

import java.io.IOException;

public class Queue {
    private final Channel channel;
    private final RoutingChain chain;

    public Queue(Channel channel, RoutingChain chain) {
        this.channel = channel;
        this.chain = chain;
    }

    public Queue push(byte[] data) throws IOException {
        channel.send(
            channel.getFactory()
                .routingMod(
                    new RoutingModFactory()
                        .dataType(DataType.MESSAGE)
                        .messageType(MessageType.PUSH)
                        .routingType(RoutingType.DIRECT)
                        .build()
                )
                .routingChain(chain)
                .data(data)
                .build()
        );
        return this;
    }

    public DataPack fetch() throws IOException {
        return channel.sendAndRead(
            channel.getFactory()
                .routingMod(
                    new RoutingModFactory()
                        .dataType(DataType.MESSAGE)
                        .messageType(MessageType.FETCH)
                        .routingType(RoutingType.DIRECT)
                        .build()
                )
                .routingChain(chain)
                .build()
        );
    }

    public FetchResult fetchFast() throws IOException {
        DataPack dataPack = null;
        try {
            dataPack = this.fetch();
        } catch (IOException e) {
            return new FetchResult.Failed(e);
        }
        DataHead head = dataPack.head;
        if (head.getErrCode() == ErrorCode.toInt(ErrorCode.EMPTY)) {
            return new FetchResult.Empty();
        }
        return new FetchResult.Success(dataPack.data);
    }

    public FetchResult fetchString() throws IOException
    {
        FetchResult f = this.fetchFast();
        if (f instanceof FetchResult.Success) {
            return new FetchResult.SuccessString(new String(((FetchResult.Success)f).data));
        } else {
            return f;
        }
    }
}
