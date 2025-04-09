public class Worker {
    private static String MASTER_IP;
    private static String MASTER_PORT;

    private String IP;
    private String PORT;

    public Worker() {}

    public Worker(String IP, String PORT) {
        this.IP = IP;
        this.PORT = PORT;
    }

    public Worker(String masterIP, String masterPort, String IP, String PORT) {
        MASTER_IP = masterIP;
        MASTER_PORT = masterPort;
        this.IP = IP;
        this.PORT = PORT;
    }

}
