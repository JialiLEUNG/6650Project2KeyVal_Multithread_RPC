import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class KeyValueMultithreadServer {


    // constructor
    public KeyValueMultithreadServer(){
        try {
            int registryPort = 1099;
            KeyValueMultithreadImpl localObject = new KeyValueMultithreadImpl();
            Registry registry = LocateRegistry.getRegistry(registryPort);
            registry.rebind("KeyValueMultithread", localObject);
            System.err.println("DEBUG: KeyValueMultithreadImpl RMI listener bound\n");
        } catch (RemoteException e) {
            System.err.println("RemoteException: " + e);
        }
    }

    public static void main(String[] args) {
        new KeyValueMultithreadServer();
    }
}
