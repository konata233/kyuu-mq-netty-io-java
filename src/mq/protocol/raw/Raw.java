package mq.protocol.raw;

public abstract class Raw {
    public static class Message extends Raw {
        private RawMessage message;

        public Message(RawMessage message) {
            this.message = message;
        }

        public RawMessage getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "Message{" + "message=" + message + '}';
        }
    }

    public static class Command extends Raw {
        private RawCommand command;

        public Command(RawCommand command) {
            this.command = command;
        }

        public RawCommand getCommand() {
            return command;
        }

        @Override
        public String toString() {
            return "Command{" + "command=" + command + '}';
        }
    }

    public static class Nop extends Raw {
        @Override
        public String toString() {
            return "Nop{}";
        }
    }
}

