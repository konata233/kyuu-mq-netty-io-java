package mq.net;

public class RoutingMod {
    public DataType dataType = DataType.MESSAGE;
    public CommandType commandType = CommandType.NOP;
    public MessageType messageType = MessageType.NOP;
    public RoutingType routingType = RoutingType.NOP;

    public RoutingMod(DataType dataType, CommandType commandType, MessageType messageType, RoutingType routingType) {
        this.dataType = dataType;
        this.commandType = commandType;
        this.messageType = messageType;
        this.routingType = routingType;
    }
}
