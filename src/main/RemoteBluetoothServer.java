package main;

import java.io.IOException;
import java.util.Observable;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.JTextArea;

public class RemoteBluetoothServer extends Observable implements IChangeState, Runnable{
    private StreamConnection connection;
    
    private boolean isConnected;
    private JTextArea textArea;

    public RemoteBluetoothServer() {
        isConnected = false;
    }

    RemoteBluetoothServer(JTextArea textArea_log) {
        isConnected = false;
        textArea = textArea_log;
    }
    
    @Override
    public void addTextToArea(String message){
        textArea.setText(textArea.getText() + message + "\n");
    }
    
    public void sendStopSignal() {
        Thread sendThread = new Thread(new SendThread(connection, this));
        sendThread.start();
    }
    
    public void sendMessage(String message){
        Thread sendThread = new Thread(new SendThread(connection, message));
        sendThread.start();
    }

    @Override
    public void run() {
        waitForConnection();
    }

    /**
     * Waiting for connection from devices
     */
    private void waitForConnection() {
        // retrieve the local Bluetooth device object
        LocalDevice local = null;
        StreamConnectionNotifier notifier;

        // setup the server to listen for connection
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID("04c6093b00001000800000805f9b34fb", false);
            System.out.println(uuid.toString());
            addTextToArea(uuid.toString());

            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (BluetoothStateException e) {
            System.out.println("Bluetooth is not turned on.");
            addTextToArea("Bluetooth is not turned on.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // waiting for connection
        while (true) {
            try {
                System.out.println("waiting for connection...");
                addTextToArea("waiting for connection...");
                connection = notifier.acceptAndOpen();
                Thread receiveThread = new Thread(new ReceiveThread(connection, this));
                receiveThread.start();

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
    
    @Override
    public void changeConnectionState(boolean connectionState) {
      // if value has changed notify observers
      if(isConnected != connectionState) {
         System.out.println("Connection state has changed");
          addTextToArea("Connection state has changed");
         isConnected = connectionState;
         
         // mark as value changed
         setChanged();
         // trigger notification
         notifyObservers(connectionState);
      }
   }
}
