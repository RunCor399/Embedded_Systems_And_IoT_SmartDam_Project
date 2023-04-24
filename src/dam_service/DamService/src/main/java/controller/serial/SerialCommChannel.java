package controller.serial;

import java.util.function.Consumer;

import jssc.*;

public class SerialCommChannel implements CommChannel, SerialPortEventListener {

    private SerialPort serialPort;
    private StringBuffer currentMsg = new StringBuffer("");
    private Consumer<String> messageHandler;

    public SerialCommChannel(String port, int rate){

        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.addEventListener(this);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }  
    }

    @Override
    public void sendMsg(String msg) {
        char[] array = (msg + "\n").toCharArray();
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bytes[i] = (byte) array[i];
        }
        try {
            synchronized (serialPort) {
                serialPort.writeBytes(bytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void acceptInput(String input) {
        if (this.messageHandler != null) {
            this.messageHandler.accept(input);
        }
    }


    public void serialEvent(SerialPortEvent event) {
        /* if there are bytes received in the input buffer */
        if (event.isRXCHAR()) {
            try {
                String msg = serialPort.readString(event.getEventValue());

                msg = msg.replaceAll("\r", "");

                currentMsg.append(msg);

                boolean goAhead = true;

                while (goAhead) {
                    String msg2 = currentMsg.toString();
                    int index = msg2.indexOf("\n");
                    if (index >= 0) {
                        this.acceptInput(msg2.substring(0, index));
                        currentMsg = new StringBuffer("");
                        if (index + 1 < msg2.length()) {
                            currentMsg.append(msg2.substring(index + 1));
                        }
                    } else {
                        goAhead = false;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error in receiving string from COM-port: " + ex);
            }
        }
    }

    @Override
    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }
    
    @Override
    public synchronized void close() {
        try {
            if (serialPort != null) {
                serialPort.removeEventListener();
                serialPort.closePort();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
