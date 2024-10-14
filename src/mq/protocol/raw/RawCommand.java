package mq.protocol.raw;

public abstract class RawCommand {
    public static class NewQueue extends RawCommand {
        private byte[] data;

        public NewQueue(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "NewQueue{" + "data=" + new String(data) + '}';
        }
    }

    public static class NewExchange extends RawCommand {
        private byte[] data;

        public NewExchange(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "NewExchange{" + "data=" + new String(data) + '}';
        }
    }

    public static class NewBinding extends RawCommand {
        private byte[] data;

        public NewBinding(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "NewBinding{" + "data=" + new String(data) + '}';
        }
    }

    public static class DropQueue extends RawCommand {
        private byte[] data;

        public DropQueue(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "DropQueue{" + "data=" + new String(data) + '}';
        }
    }

    public static class DropExchange extends RawCommand {
        private byte[] data;

        public DropExchange(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "DropExchange{" + "data=" + new String(data) + '}';
        }
    }

    public static class DropBinding extends RawCommand {
        private byte[] data;

        public DropBinding(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "DropBinding{" + "data=" + new String(data) + '}';
        }
    }

    public static class Nop extends RawCommand {
        @Override
        public String toString() {
            return "Nop{}";
        }
    }
}

