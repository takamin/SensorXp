package jp.coocan.life.bicycle;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView values;
	private SensorManager sensorManager;
	private SensorEventListener sensorListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) { }

		@SuppressWarnings("deprecation")
		@Override
		public void onSensorChanged(SensorEvent event)  {
			if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				String str = "加速度センサー値:"
					+ "\nX軸:" + event.values[0]
					+ "\nY軸:" + event.values[1] 
					+ "\nZ軸:" + event.values[2];
				values.setText(str);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		values = (TextView)findViewById(R.id.textView1);
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);		
	}
	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors.size() > 0) {
			Sensor accMeter = sensors.get(0);
			sensorManager.registerListener(sensorListener, accMeter, SensorManager.SENSOR_DELAY_UI);
		}
	}

	@Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		sensorManager.unregisterListener(sensorListener);
		super.onStop();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

}
