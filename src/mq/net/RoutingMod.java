package mq.net;

public class RoutingMod {
    public DataType dataType;
    public CommandType commandType;
    public MessageType messageType;
    public RoutingType routingType;

    public RoutingMod(DataType dataType, CommandType commandType, MessageType messageType, RoutingType routingType) {
        this.dataType = dataType;
        this.commandType = commandType;
        this.messageType = messageType;
        this.routingType = routingType;
    }
}
