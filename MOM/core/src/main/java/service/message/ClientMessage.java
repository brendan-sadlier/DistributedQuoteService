package service.message;

import service.core.ClientInfo;

import java.io.Serializable;

public class ClientMessage implements Serializable {

    private long token;
    private ClientInfo info;

    public ClientMessage(long token, ClientInfo clientInfo) {
        this.token = token;
        this.info = clientInfo;
    }

    public long getToken() {
        return token;
    }

    public ClientInfo getInfo() {
        return info;
    }
}
