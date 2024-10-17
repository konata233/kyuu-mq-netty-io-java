package mq.api;

public class FetchResult {
    public static class Success extends FetchResult {
        public final byte[] data;

        public Success(byte[] data) {
            this.data = data;
        }
    }

    public static class SuccessString extends FetchResult {
        public final String data;

        public SuccessString(String data) {
            this.data = data;
        }
    }

    public static class Empty extends FetchResult {
        public Empty() {}
    }

    public static class Failed extends FetchResult {
        public final Exception exception;

        public Failed(Exception e) {
            this.exception = e;
        }
    }
}
