package mq.api;

import mq.net.*;

import java.io.IOException;

public class HostOp {
    public static void createExchange(Channel channel, RoutingChain chain, String exchangeName) throws IOException {
        channel.send(
            channel.getFactory()
                .routingMod(
                    new RoutingModFactory()
                        .dataType(DataType.COMMAND)
                        .commandType(CommandType.NEW_EXCHANGE)
                        .routingType(RoutingType.DIRECT)
                        .build()
                )
                .routingChain(chain)
                .data(exchangeName.getBytes())
                .build()
        );
    }

    public static void createQueue(Channel channel, RoutingChain chain, String queueName) throws IOException {
        channel.send(
            channel.getFactory()
                .routingMod(
                    new RoutingModFactory()
                        .dataType(DataType.COMMAND)
                        .commandType(CommandType.NEW_QUEUE)
                        .routingType(RoutingType.DIRECT)
                        .build()
                )
                .routingChain(chain)
                .data(queueName.getBytes())
                .build()
        );
    }

    public static void dropExchange(Channel channel, RoutingChain chain, String exchangeName) throws IOException {
        channel.send(
            channel.getFactory()
                .routingMod(
                    new RoutingModFactory()
                        .dataType(DataType.COMMAND)
                        .commandType(CommandType.DROP_EXCHANGE)
                        .routingType(RoutingType.DIRECT)
                        .build()
                )
                .routingChain(chain)
                .data(exchangeName.getBytes())
                .build()
        );
    }

    public static void dropQueue(Channel channel, RoutingChain chain, String queueName) throws IOException {
        channel.send(
            channel.getFactory()
                .routingMod(
                    new RoutingModFactory()
                        .dataType(DataType.COMMAND)
                        .commandType(CommandType.DROP_QUEUE)
                        .routingType(RoutingType.DIRECT)
                        .build()
                )
                .routingChain(chain)
                .data(queueName.getBytes())
                .build()
        );
    }
}
