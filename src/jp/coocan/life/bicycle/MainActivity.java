package jp.coocan.life.bicycle;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView values;
	private SensorValueProcessor sensorValueProcessor = new SensorValueProcessor();
	private SensorValueProcessor.OnSensorValueChangeListener sensorValueChnageListener =
			new SensorValueProcessor.OnSensorValueChangeListener() {

				@Override
				public void onSensorValueChange(
						SensorValueProcessor sensorValueProcessor) {
					if(sensorValueProcessor.getSensorType() == Sensor.TYPE_ACCELEROMETER) {
						StringBuilder sb = new StringBuilder();
						sb.append("加速度センサー値:")
						.append("\nX軸(RAW):").append(Float.toString(sensorValueProcessor.getRawValue(0)))
						.append("\nY軸(RAW):").append(Float.toString(sensorValueProcessor.getRawValue(1)))
						.append("\nZ軸(RAW):").append(Float.toString(sensorValueProcessor.getRawValue(2)))
						.append("\nX軸(AVG):").append(Float.toString(sensorValueProcessor.getAverageValue(0)))
						.append("\nY軸(AVG):").append(Float.toString(sensorValueProcessor.getAverageValue(1)))
						.append("\nZ軸(AVG):").append(Float.toString(sensorValueProcessor.getAverageValue(2)));
						String str = sb.toString();
						values.setText(str);
					}
				}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		values = (TextView)findViewById(R.id.textView1);
		sensorValueProcessor.setSensorManager((SensorManager)getSystemService(SENSOR_SERVICE));
	}
	@Override
	protected void onResume() {
		super.onResume();
		sensorValueProcessor.registerListener(
				sensorValueChnageListener, 
				Sensor.TYPE_ACCELEROMETER, 
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onStop() {
		sensorValueProcessor.unregisterListener();
		super.onStop();
	}
}
