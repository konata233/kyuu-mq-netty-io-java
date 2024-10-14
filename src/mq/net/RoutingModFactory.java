package mq.net;

public class RoutingModFactory {
    private DataType dataType;
    private CommandType commandType;
    private MessageType messageType;
    private RoutingType routingType;

    public RoutingModFactory() {
        this.dataType = DataType.MESSAGE;
        this.commandType = CommandType.NOP;
        this.messageType = MessageType.NOP;
        this.routingType = RoutingType.DIRECT;
    }

    public RoutingModFactory dataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    public RoutingModFactory commandType(CommandType commandType) {
        this.commandType = commandType;
        return this;
    }

    public RoutingModFactory messageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public RoutingModFactory routingType(RoutingType routingType) {
        this.routingType = routingType;
        return this;
    }

    public RoutingMod build() {
        return new RoutingMod(dataType, commandType, messageType, routingType);
    }
}
