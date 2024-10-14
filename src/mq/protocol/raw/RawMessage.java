package mq.protocol.raw;

public abstract class RawMessage {
    public static class Push extends RawMessage {
        private byte[] data;

        public Push(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "Push{" + "data=" + new String(data) + '}';
        }
    }

    public static class Fetch extends RawMessage {
        private byte[] data;

        public Fetch(byte[] data) {
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public String toString() {
            return "Fetch{" + "data=" + new String(data) + '}';
        }
    }

    public static class Nop extends RawMessage {
        @Override
        public String toString() {
            return "Nop{}";
        }
    }
}

