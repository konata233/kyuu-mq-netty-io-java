import mq.net.*;
import mq.protocol.DataHead;
import mq.protocol.raw.RawCommand;
import mq.protocol.raw.RawData;
import mq.protocol.raw.RawMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Program launch!");
        Session session = new Session("MQ_HOST", "127.0.0.1", 11451);
        session.init(session);
        System.out.println("Connection established!");
        session.setReadTimeout(1024 * 120);
        Channel channel = session.createChannel("test");

        byte[] test = channel.getFactory()
                .routingMod(
                        new RoutingModFactory()
                                .dataType(DataType.COMMAND)
                                .commandType(CommandType.NEW_EXCHANGE)
                                .routingType(RoutingType.DIRECT)
                                .build()
                )
                .route(Routing.Stop())
                .data("base_exc".getBytes(StandardCharsets.UTF_8))
                .build();

        channel.send(
                channel.getFactory()
                        .routingMod(
                                new RoutingModFactory()
                                        .dataType(DataType.COMMAND)
                                        .commandType(CommandType.NEW_EXCHANGE)
                                        .routingType(RoutingType.DIRECT)
                                        .build()
                        )
                        .route(Routing.Stop())
                        .data("base_exc".getBytes(StandardCharsets.UTF_8))
                        .build()
        );

        channel.send(
                channel.getFactory()
                        .routingMod(
                                new RoutingModFactory()
                                        .dataType(DataType.COMMAND)
                                        .commandType(CommandType.NEW_QUEUE)
                                        .routingType(RoutingType.DIRECT)
                                        .build()
                        )
                        .route(Routing.Route("base_exc"))
                        .route(Routing.Stop())
                        .data("base_queue".getBytes(StandardCharsets.UTF_8))
                        .build()
        );

        for (int i = 0; i < 16; i++) {
            channel.send(
                    channel.getFactory()
                            .routingMod(
                                    new RoutingModFactory()
                                            .dataType(DataType.MESSAGE)
                                            .messageType(MessageType.PUSH)
                                            .routingType(RoutingType.DIRECT)
                                            .build()
                            )
                            .data("Test message from producer %d.".formatted(i).getBytes())
                            .route(Routing.Route("base_exc"))
                            .route(Routing.Stop())
                            .queueName("base_queue")
                            .build()
            );
            System.out.printf("Sent message: %d from producer. \n", i);
        }

        for (int i = 0; i < 16; i++) {
            byte[] data = channel.sendAndRead(
                    channel.getFactory()
                            .routingMod(
                                    new RoutingModFactory()
                                            .dataType(DataType.MESSAGE)
                                            .messageType(MessageType.FETCH)
                                            .routingType(RoutingType.DIRECT)
                                            .build()
                            )
                            .data("".getBytes())
                            .route(Routing.Route("base_exc"))
                            .route(Routing.Stop())
                            .queueName("base_queue")
                            .build()
            );
            ByteBuffer buffer = ByteBuffer.wrap(data);
            byte[] head_bytes = new byte[256];
            buffer.slice(0, 256).get(head_bytes);
            DataHead head = new DataHead().deserialize(head_bytes);

            ByteBuffer message_buffer = buffer.slice(256, head.getSliceCount() * head.getSliceSize());
            String message = new String(message_buffer.array(), StandardCharsets.UTF_8).trim().strip();
            System.out.printf("Read message: \"%s\" from consumer. \n", message);
        }

        session.dropChannel("test");
        session.close();
        System.out.println("Connection closed!");
    }
}