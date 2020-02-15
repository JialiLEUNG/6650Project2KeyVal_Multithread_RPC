
/** Implementation of the RMIThreadServer interface for RMI.
 Change the read() and update() functions from synchronized to
 unsynchronized to watch how you can create race conditions and
 clobber data using RMI threads.

 How it works: We have a global variable ("counter"), which is incremented
 then decremented by a call to update().  This should appear atomic to
 clients, but if there are multiple clients connecting simultaneously, each
 client potentially could read the variable at any stage when another
 thread is executing the update() method.  By synchronizing both the
 read() and update() methods, only one client is allowed to execute those
 methods at a time.  The result is that each client sees counter==0, both
 before and after executing the update() method.

 Author: Jiali Liang
 */

import java.io.*;
import java.lang.Thread;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.HashMap;
import java.util.Map;

public class KeyValueMultithreadImpl extends UnicastRemoteObject implements KeyValueMultithread
{
//    private static final long serialVersionUID = -7574367988592496327L;
//    private volatile int counter = 0;
//    private final int MAXCOUNT = 900000;

    /* Server information */
    protected boolean       isStopped = false; // whether the server is terminated.
    protected Thread        runningThread = null;
    private Map<String, String> store = new HashMap<>();


    public KeyValueMultithreadImpl() throws RemoteException {
        super();
    }

    public void start(){
        // only one thread can access the resource at a given point of time
        synchronized (this){
            // Thread.currentThread() returns a reference to the currently executing thread object.
            this.runningThread = Thread.currentThread();
        }

        while(!isStopped()){
            try{
                System.out.println("Server starts to process request...");
                processClientRequest();
                try {
                    Thread.sleep (1 + (int) (Math.random () * 1000));
                } catch (InterruptedException e) {
                    /* If we were interrupted, go ahead and end thread */
                    break;
                }
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
    }

    private synchronized boolean isStopped(){
        return this.isStopped;
    }

    private synchronized void processClientRequest(String input) throws Exception{
        // once client is connected, use socket stream to send a prompt to client
//        OutputStream output = clientSocket.getOutputStream();
//        PrintWriter writer = new PrintWriter(output, true);
//        // Prompt for client to enter something.
//        writer.println("Please type your request and enter: \n");

        // Create a InputStream  and BufferedReader for reading from socket
//        InputStream input = clientSocket.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader((input)));
//        while(clientSocket.isConnected()){
//            String res = reader.readLine().trim();
//            System.out.println("===== Client: " + res); // print message from client.
//
//            // echo client message to client.
//            InetAddress clientAddress = clientSocket.getInetAddress();
//            int clientPort = clientSocket.getPort();
//            String[] requestArr = res.split(" ");
//            keyValService(writer, requestArr, clientAddress, clientPort);
//        }
        while(true){
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String res = input.trim();
            System.out.println("===== Client: " + res);
            String[] requestArr = res.split(" ");
            keyValService(requestArr);

        }

    }


    /**
     * keyValService() implements protocol for client's request:
     * Client should follow the format: <operation> <key> for get and delete.
     * For example, get apple, delete apple.
     * Client should follow the format: <operation> <parameter> <parameter> for put.
     * For example, put apple 10.
//     * @param writer socket stream for sending message to client.
     * @param requestArr String[] client's request sentence.
//     * @param clientAddress InetAddress client's IP address.
//     * @param clientPort int client's port.
     * @throws IOException
     */
    private String keyValService(String[] requestArr) throws IOException {
        if (requestArr.length < 2){
            String msg = "Error: Malformed Request from [consumer ]." +
                    " Syntax: <operation> <key> OR <operation> <key> <value>. For example: get apple";
         return msg + " at time: " + System.currentTimeMillis();
        }

        String action = requestArr[0]; // get, put, delete
        String key = requestArr[1];

        String msg;

        switch(action.toLowerCase()) { // normalize operation to lowercase.
            case "get":
                if(store.containsKey(key)){
                    String price = store.get(key);
                    msg = "Price of " + key + ": " + price + " at time " + System.currentTimeMillis();
                    break;
                }
                msg = "Error + " + key + " not found. Malformed Request from [consumer ]. at time " + System.currentTimeMillis();
                break;
            case "delete":
                if (!store.containsKey(key)) {
                    msg = key + " not found. Malformed Request from [consumer ]. at time " + System.currentTimeMillis();
                    break;
                }

                store.remove(key);

                msg = "Delete " + key + " succeed. " + "at time " + System.currentTimeMillis();
                break;

            case "put":
                if (requestArr.length == 3) {
                    if (isNumeric(requestArr[2])){
                        store.put(key, requestArr[2]);
                        msg =  "Put [" + key + ", " + requestArr[2] + "] in store succeed. " + "at time " + System.currentTimeMillis();
                        break;
                    }
                    msg = "Error: Value should be numeric. At time: " + System.currentTimeMillis();
                    break;
                    }
                else{
                    msg = "Error: Malformed Request from [consumer ]." +
                            "Syntax of put: <operation> <key> <value>. For example: put apple 10. At time " + System.currentTimeMillis();
                    break;
                }

            default:
                msg = "Error: Malformed Request from [consumer "  +
                        "Syntax: <operation> <key>.... At time " + System.currentTimeMillis();
                break;
        }
        return msg;
    }


    /**
     * TEST function to practice looking at thread synchronization. This
     * function increments a counter, then decrements it back to zero. When a
     * client tries to read the counter, they should always get zero (if threads
     * are synchronized properly).
     */
    public synchronized void update() {
        // public void update() {
        int i;
        Thread p = Thread.currentThread();

        System.out.println("[server] Entering critical section: " + p.getName());
//        for (i = 0; i < MAXCOUNT; i++)
//            this.counter++;
//        for (i = 0; i < MAXCOUNT; i++)
//            this.counter--;
        System.out.println("[server] Leaving critical section: " + p.getName());

    }


    /**
     * TEST function to practice looking at thread synchronization. This allows
     * a client to read the value of the "counter" variable.
     */
    public synchronized int read() {
        // public int read() {
//        return this.counter;
    }


    /**
     * isNumeric() checks if user request of "PUT" contains numeric value.
     * For example, "put apple 0" is valid, whereas "put apple zero" is invalid.
     * @param strNum
     * @return
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
