import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the RMIThreadServer object, which templates the calls we are
 * exposing over RMI
 */

public interface KeyValueMultithread extends Remote
{

    void update() throws RemoteException;


    int read() throws RemoteException;
}
