import mq.net.*;
import mq.protocol.DataHead;
import mq.protocol.raw.DataPack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Program launch!");
        Session session = new Session("MQ_HOST", "127.0.0.1", 11451);
        session.init(session);
        System.out.println("Connection established!");
        session.setReadTimeout(500);
        Channel channelProducer = session.createChannel("test");
        Channel channelConsumer = session.createChannel("read");
        Channel channelConsumer2 = session.createChannel("read2");

        channelProducer.send(
            channelProducer.getFactory()
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

        channelProducer.send(
            channelProducer.getFactory()
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

        channelProducer.send(
            channelProducer.getFactory()
                .routingMod(
                    new RoutingModFactory()
                        .dataType(DataType.COMMAND)
                        .commandType(CommandType.NEW_QUEUE)
                        .routingType(RoutingType.DIRECT)
                        .build()
                )
                .route(Routing.Route("base_exc"))
                .route(Routing.Stop())
                .data("base_queue2".getBytes(StandardCharsets.UTF_8))
                .build()
        );

        Thread producer = new Thread(
            () -> {
                for (int i = 0; i < 6; i++) {
                    try {
                        sendMessage(channelProducer, "Test message from producer 1 :: %d.".formatted(i), "base_queue");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.printf("Sent message: %d from producer 1. \n", i);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        );

        Thread producer2 = new Thread(
            () -> {
                for (int i = 0; i < 6; i++) {
                    try {
                        sendMessage(channelProducer, "Test message from producer 2 :: %d.".formatted(i), "base_queue2");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.printf("Sent message: %d from producer 2. \n", i);
                }
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        );

        Thread consumer = new Thread(
            () -> {
                for (int i = 0; i < 30; i++) {
                    try {
                        DataPack data = sendAndReadMessage(channelConsumer, "base_queue");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        );

        Thread consumer2 = new Thread(
            () -> {
                for (int i = 0; i < 30; i++) {
                    try {
                        DataPack data = sendAndReadMessage(channelConsumer2, "base_queue2");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        );

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        executor.execute(producer);
        executor.execute(producer2);
        executor.execute(consumer);
        executor.execute(consumer2);
        executor.awaitTermination(10, TimeUnit.SECONDS);
        executor.shutdown();

        channelProducer.close();
        session.close();
        System.out.println("Connection closed!");
    }

    public static void sendMessage(Channel channel, String message, String queueName) throws IOException {
        channel.send(
            channel.getFactory()
                .routingMod(
                    new RoutingModFactory()
                        .dataType(DataType.MESSAGE)
                        .messageType(MessageType.PUSH)
                        .routingType(RoutingType.DIRECT)
                        .build()
                )
                .data(message.getBytes())
                .route(Routing.Route("base_exc"))
                .route(Routing.Stop())
                .queueName(queueName)
                .build()
        );
    }

    public static DataPack sendAndReadMessage(Channel channel, String queueName) throws IOException {
        DataPack data = null;
        try {
            data = channel.sendAndRead(
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
                    .queueName(queueName)
                    .build()
            );

            String message = new String(data.data, StandardCharsets.UTF_8).trim().strip();
            if (!data.isEmpty) {
                if (data.head.getErrCode() == ((short) 0xf)) {
                    System.out.printf("Error code: %d, empty queue. \n", data.head.getErrCode());
                    Thread.sleep(500);
                } else {
                    System.out.printf("Read message: \"%s\" from consumer %s. \n", message, channel.getName());
                }
            }
        } catch (IOException e) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Unable to fetch message!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}