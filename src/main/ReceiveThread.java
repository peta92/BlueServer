package main;

import java.io.InputStream;
import javax.microedition.io.StreamConnection;


public class ReceiveThread implements Runnable {

    private StreamConnection mConnection;
    private IChangeState stateChanger;

    // Constant that indicate command from devices
    private static final int EXIT_CMD = -1;
    private static final int START_ACTION = 1;

    public ReceiveThread(StreamConnection connection, IChangeState stateChanger) {
        mConnection = connection;
        this.stateChanger = stateChanger;
    }

    @Override
    public void run() {
        try {

            // prepare to receive data
            InputStream inputStream = mConnection.openInputStream();

            System.out.println("waiting for input");
            stateChanger.addTextToArea("waiting for input");

            while (true) {
                int command = inputStream.read();

                if (command == EXIT_CMD) {
                    System.out.println("finish process");
                    stateChanger.addTextToArea("finish process");
                    stateChanger.changeConnectionState(false);
                    break;
                }

                processCommand(command);
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the command from client
     *
     * @param command the command code
     */
    private void processCommand(int command) {
        try {
            if (command == START_ACTION) {
                stateChanger.changeConnectionState(true);
                System.out.println("Start Shooting");
                stateChanger.addTextToArea("Start Shooting");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
