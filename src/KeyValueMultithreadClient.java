import java.rmi.registry.*;

/**
 * Implementation of the RMIThreadClient application. This code spawns a single
 * client process which connects to the server, tells the server to run a
 * process, then reads a result back from the server and prints it. The result
 * that comes back from the server should ALWAYS be zero.
 *
 * @author Paul Kreiner
 * @author Amit Jain
 *
 */

public class KeyValueMultithreadClient
{

    KeyValueMultithread remObj;


    public static void main(String[] args) {
        KeyValueMultithreadClient myself = new KeyValueMultithreadClient(args[0]);
    }


    public KeyValueMultithreadClient(String host) {
        try {
            int registryPort = 1099;
            Registry registry = LocateRegistry.getRegistry(host, registryPort);
            remObj = (KeyValueMultithread) registry.lookup("KeyValueMultithread");

            System.out.println("[client] (before) counter = " + remObj.read());
            remObj.update();
            System.out.println("[client] (after)  counter = " + remObj.read());

        } catch (java.rmi.NotBoundException e) {
            System.err.println("RMI endpoint not bound: " + e);
            System.exit(2);
        } catch (java.rmi.RemoteException e) {
            System.err.println("RMI RemoteException: " + e);
            System.exit(2);
        }
    }
}
