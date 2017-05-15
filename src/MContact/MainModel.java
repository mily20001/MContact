package MContact;

import java.net.ServerSocket;

class MainModel {
    private String name = "John Smith";
    private Integer serverPort = 8420;
    private ServerSocket serverSocket;

    public MainModel(String name, Integer port) {
        this.name = name;
        this.serverPort = port;
    }

    public String getName() {
        return name;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer newPort) {
        serverPort = newPort;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setServerSocket(ServerSocket newServerSocket) {
        serverSocket = newServerSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
