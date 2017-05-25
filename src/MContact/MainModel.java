package MContact;

import java.net.ServerSocket;

/** Model class of main application */
class MainModel {
    private String name = "John Smith";
    private Integer serverPort = 8420;
    private ServerSocket serverSocket;

    /**
     * Constructs main model and sets proper variables
     * @param name your name
     * @param port port to be used
     */
    MainModel(String name, Integer port) {
        this.name = name;
        this.serverPort = port;
    }

    /**
     * Returns your name/nick
     * @return your name/nick
     */
    public String getName() {
        return name;
    }

    /**
     * Returns port to be used by server
     * @return port to be used by server
     */
    Integer getServerPort() {
        return serverPort;
    }

    /**
     * Sets new port to be used by server
     * @param newPort new port to be used by server
     */
    void setServerPort(Integer newPort) {
        serverPort = newPort;
    }

    /**
     * Sets your new name/nick
     * @param newName your new name/nick
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Sets new server's socket
     * @param newServerSocket new server's socket
     */
    void setServerSocket(ServerSocket newServerSocket) {
        serverSocket = newServerSocket;
    }

    /**
     * Returns server's socket
     * @return server's socket
     */
    ServerSocket getServerSocket() {
        return serverSocket;
    }
}
