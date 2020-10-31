package edu.ucdenver.domain.client;

public class ClientError extends Exception {
    ClientErrorType type;
    public ClientError(ClientErrorType type,String additional) {
            super(type.toString()+": "+additional);
        this.type = type;
    }
    public ClientError(ClientErrorType type) {
        super(type.toString());
    }
    public ClientError() {
        super("UNKOWN");
        this.type = ClientErrorType.UNKNOWN;
    }
    public ClientErrorType getType() {
        return type;
    }
}
