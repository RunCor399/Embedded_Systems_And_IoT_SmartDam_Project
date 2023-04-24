package unibo.btclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.UUID;

import unibo.btlib.BluetoothChannel;
import unibo.btlib.BluetoothUtils;
import unibo.btlib.ConnectToBluetoothServerTask;
import unibo.btlib.ConnectionTask;
import unibo.btlib.RealBluetoothChannel;
import unibo.btlib.exceptions.BluetoothDeviceNotFound;
import unibo.btclient.utils.C;

public class MainActivity extends AppCompatActivity {

    private BluetoothChannel btChannel;
    private Boolean manualMode = false;
    private MqttAndroidClient mqttAndroidClient;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null && !btAdapter.isEnabled()){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), C.bluetooth.ENABLE_BT_REQUEST);
        }

        //Connect to MQTT
        this.mqttAndroidClient = new MqttAndroidClient(getApplicationContext(),"tcp://79.40.140.154", MqttClient.generateClientId());
        this.mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                ((TextView) findViewById(R.id.lblWaterLevel)).setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            IMqttToken token = this.mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    ((TextView) findViewById(R.id.lblWaterLevel)).setText("Connesso");
                    String topic = "water_level";
                    int qos = 1;
                    try {
                        IMqttToken subToken = mqttAndroidClient.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {}

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                ((TextView) findViewById(R.id.lblWaterLevel)).setText("Livello dell'acqua non disponibile");
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    initUI();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    exception.printStackTrace();
                    ((TextView) findViewById(R.id.lblWaterLevel)).setText("Non connesso");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        findViewById(R.id.btnConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    connectToBTServer();
                } catch (BluetoothDeviceNotFound bluetoothDeviceNotFound) {
                    bluetoothDeviceNotFound.printStackTrace();
                }
            }
        });

        //Listener dei bottoni nella view
        findViewById(R.id.btnManualMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manualMode) {
                    setDisabledManualMode();
                }else{
                    setEnabledManualMode();
                }
            }
        });



        ((SeekBar) findViewById(R.id.sliderDamOpening)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int actualValue = progress - (progress % 20);
                ((TextView) findViewById(R.id.lblSliderLevel)).setText(String.valueOf(actualValue));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int actualValue = seekBar.getProgress() - (seekBar.getProgress() % 20);
                seekBar.setProgress(actualValue);
                btChannel.sendDamLevel(actualValue);
            }
        });
    }

    private void setDisabledManualMode(){
        ((TextView) findViewById(R.id.lblManualState)).setText("Disabled");
        findViewById(R.id.sliderDamOpening).setVisibility(View.INVISIBLE);
        findViewById(R.id.lblSliderLevel).setVisibility(View.INVISIBLE);
        manualMode = false;
        btChannel.sendAlarmState();
    }

    private void setEnabledManualMode(){
        btChannel.sendManualState();
        ((TextView) findViewById(R.id.lblManualState)).setText("Enabled");
        String actualLevel = ((TextView) findViewById(R.id.lblDamLevel)).getText().toString();
        NumberFormat nf = NumberFormat.getInstance();
        int startValue = 0;
        try {
            startValue = nf.parse(actualLevel).intValue();
        } catch (ParseException e) { e.printStackTrace();}
        ((SeekBar) findViewById(R.id.sliderDamOpening)).setProgress(startValue);
        ((TextView) findViewById(R.id.lblSliderLevel)).setText(actualLevel);
        findViewById(R.id.sliderDamOpening).setVisibility(View.VISIBLE);
        findViewById(R.id.lblSliderLevel).setVisibility(View.VISIBLE);
        manualMode = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(btChannel != null) {
            btChannel.close();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if(requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_OK){
            Log.d(C.APP_LOG_TAG, "Bluetooth enabled!");
        }

        if(requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_CANCELED){
            Log.d(C.APP_LOG_TAG, "Bluetooth not enabled!");
        }
    }

    private void connectToBTServer() throws BluetoothDeviceNotFound {
        final BluetoothDevice serverDevice = BluetoothUtils.getPairedDeviceByName(C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME);

        final UUID uuid = BluetoothUtils.getEmbeddedDeviceDefaultUuid();

        new ConnectToBluetoothServerTask(serverDevice, uuid, new ConnectionTask.EventListener() {
            @Override
            public void onConnectionActive(final BluetoothChannel channel) {
                ((TextView) findViewById(R.id.lblConnect)).setText(String.format("Connesso a %s", serverDevice.getName()));

                findViewById(R.id.btnConnect).setEnabled(false);
                final MessageParser parser = new MessageParser();
                btChannel = channel;
                btChannel.registerListener(new RealBluetoothChannel.Listener() {
                    @Override
                    public void onMessageReceived(String receivedMessage) {
                        parser.parseMessage(receivedMessage);
                    }

                    @Override
                    public void onMessageSent(String sentMessage) {

                    }
                });
            }

            @Override
            public void onConnectionCanceled() {
                ((TextView) findViewById(R.id.lblConnect)).setText(String.format("Impossibile connettersi a %s", C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME));
            }
        }).execute();
    }

    public class MessageParser {

        public MessageParser(){
        }

        public void  parseMessage(final String message){
            String[] splittedMessage = message.split(";");
            switch (splittedMessage[0]){
                case "DAM":
                    ((TextView) findViewById(R.id.lblDamLevel)).setText(splittedMessage[1]);
                    break;

                case "STATE":
                    this.switchStates(splittedMessage[1].trim());
                    break;
            }
        }

        private void switchStates(final String state){
            ((TextView) findViewById(R.id.lblState)).setText(state);

            switch (state){
                case "PREALARM":
                    findViewById(R.id.lblWaterLevel).setVisibility(View.VISIBLE);
                    findViewById(R.id.lblWaterLevelText).setVisibility(View.VISIBLE);
                    findViewById(R.id.lblDamLevel).setVisibility(View.INVISIBLE);
                    findViewById(R.id.lblDamText).setVisibility(View.INVISIBLE);
                    findViewById(R.id.lblManualState).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btnManualMode).setVisibility(View.INVISIBLE);
                    setDisabledManualMode();
                    break;

                case "ALARM":
                    findViewById(R.id.lblWaterLevel).setVisibility(View.VISIBLE);
                    findViewById(R.id.lblWaterLevelText).setVisibility(View.VISIBLE);
                    findViewById(R.id.lblDamLevel).setVisibility(View.VISIBLE);
                    findViewById(R.id.lblDamText).setVisibility(View.VISIBLE);
                    findViewById(R.id.lblManualState).setVisibility(View.VISIBLE);
                    findViewById(R.id.btnManualMode).setVisibility(View.VISIBLE);
                    break;

                case "NORMAL":
                    findViewById(R.id.lblWaterLevel).setVisibility(View.INVISIBLE);
                    findViewById(R.id.lblWaterLevelText).setVisibility(View.INVISIBLE);
                    findViewById(R.id.lblDamLevel).setVisibility(View.INVISIBLE);
                    findViewById(R.id.lblDamText).setVisibility(View.INVISIBLE);
                    findViewById(R.id.lblManualState).setVisibility(View.INVISIBLE);
                    findViewById(R.id.btnManualMode).setVisibility(View.INVISIBLE);
                    setDisabledManualMode();
                    break;
            }

        }
    }
}


