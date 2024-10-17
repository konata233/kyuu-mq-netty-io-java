package mq.protocol.raw;

public enum ErrorCode {
    SUCCESS(0),
    EMPTY(0xf);

    ErrorCode(int i) {

    }

    public static ErrorCode fromInt(int i) {
        return ErrorCode.values()[i];
    }

    public static int toInt(ErrorCode e) {
        return e.ordinal();
    }
}
