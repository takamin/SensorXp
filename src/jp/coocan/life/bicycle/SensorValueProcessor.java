package jp.coocan.life.bicycle;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

public class SensorValueProcessor implements SensorEventListener {
	interface OnSensorValueChangeListener {
		public void onSensorValueChange(SensorValueProcessor sensorValueProcessor);
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	static class SensorProperties {
		@SuppressLint("InlinedApi")
		public static SensorProperties[] LIST = new SensorProperties[] {
			//new SensorProperties(Sensor.TYPE_ORIENTATION, 3),
			//new SensorProperties(Sensor.TYPE_TEMPERATURE, 3),
			
			//detail => http://developer.android.com/reference/android/hardware/SensorEvent.html#values
			new SensorProperties(Sensor.TYPE_ACCELEROMETER, 3),//values[0..2]: Acceleration minus Gx.Gy.Gz on the x,y,z-axis
			new SensorProperties(Sensor.TYPE_MAGNETIC_FIELD, 3),//All values are in micro-Tesla (uT) and measure the ambient magnetic field in the X, Y and Z axis.	
			new SensorProperties(Sensor.TYPE_GYROSCOPE, 3),	//values[0..2] Angular speed around the x,y,z-axis
			new SensorProperties(Sensor.TYPE_LIGHT, 1),		//values[0]: Ambient light level in SI lux units
			new SensorProperties(Sensor.TYPE_PRESSURE, 1),//values[0]: Atmospheric pressure in hPa (millibar)
			new SensorProperties(Sensor.TYPE_PROXIMITY, 1),//values[0]: Proximity sensor distance measured in centimeters
			
			//API9
			new SensorProperties(Sensor.TYPE_GRAVITY, 3),//A three dimensional vector indicating the direction and magnitude of gravity. Units are m/s^2. The coordinate system is the same as is used by the acceleration sensor.					
			new SensorProperties(Sensor.TYPE_LINEAR_ACCELERATION, 3),//A three dimensional vector indicating acceleration along each device axis, not including gravity. All values have units of m/s^2. The coordinate system is the same as is used by the acceleration sensor
			new SensorProperties(Sensor.TYPE_ROTATION_VECTOR, 5),
			//values[0]: x*sin(É∆/2)
			//values[1]: y*sin(É∆/2)
			//values[2]: z*sin(É∆/2)
			//values[3]: cos(É∆/2)
			//values[4]: estimated heading Accuracy (in radians) (-1 if unavailable)
			//	values[3], originally optional, will always be present from SDK Level 18 onwards.
			//	values[4] is a new value that has been added in SDK Level 18.
			
			//API14
			/**/new SensorProperties(Sensor.TYPE_AMBIENT_TEMPERATURE, 1),//values[0]: ambient (room) temperature in degree Celsius.
			/**/new SensorProperties(Sensor.TYPE_RELATIVE_HUMIDITY, 3),
			
			//API18
			/**/new SensorProperties(Sensor.TYPE_GAME_ROTATION_VECTOR, 5),			
			/**/new SensorProperties(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, 6),
			/**/new SensorProperties(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, 6),
			/**/new SensorProperties(Sensor.TYPE_SIGNIFICANT_MOTION, 3),
			
			//API19
			/**/new SensorProperties(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, 3),
			/**/new SensorProperties(Sensor.TYPE_STEP_COUNTER, 1),
			/**/new SensorProperties(Sensor.TYPE_STEP_DETECTOR, 1),
		};
		private int sensorType = 0;
		private int countOfValues = 0;
		public SensorProperties(int sensorType, int countOfValues) {
			this.sensorType = sensorType;
			this.countOfValues = countOfValues;
		}
		public int getSensorType() { return sensorType; }
		public int getCountOfValues() { return countOfValues; }
	}
	
	private SensorManager sensorManager;
	private SensorProperties sensorProperties = null;
	private OnSensorValueChangeListener onSensorValueChangeListener = null;
	public void setSensorManager(SensorManager sensorManager) {
		this.sensorManager = sensorManager;
	}
	public void registerListener(OnSensorValueChangeListener onSensorValueChangeListener, int sensorType, int rate) {
		for(SensorProperties sensorProperties : SensorProperties.LIST) {
			if(sensorProperties.getSensorType() == sensorType) {
				List<Sensor> sensors = sensorManager.getSensorList(sensorType);
				if(sensors.size() > 0) {
					this.sensorProperties = sensorProperties;
					this.onSensorValueChangeListener = onSensorValueChangeListener;
					int countOfValues = this.sensorProperties.getCountOfValues();
					rawValues = new float[countOfValues];
					averageValues = new float[countOfValues];
					
					Sensor sensor = sensors.get(0);
					sensorManager.registerListener(this, sensor, rate);
					averageCount = 0;
				}
				break;
			}
		}
	}
	public void unregisterListener() {
		sensorManager.unregisterListener(this);
		this.sensorProperties = null;
		rawValues = null;
		averageValues = null;
	}
	private float[] rawValues = null;
	private int averageCount = 0;
	private int averageCountMax = 100;
	private float[] averageValues = null;
	public int getSensorType() {
		assert(sensorProperties != null);
		return sensorProperties.getSensorType();
	}
	public int getCountOfValues() {
		assert(sensorProperties != null);
		return sensorProperties.getCountOfValues();
	}
	public float getRawValue(int index) {
		assert(sensorProperties != null);
		assert(index < sensorProperties.getCountOfValues());
		return rawValues[index];
	}
	public float getAverageValue(int index) {
		assert(sensorProperties != null);
		assert(index < sensorProperties.getCountOfValues());
		return averageValues[index];
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(sensorProperties != null) {
			if(averageCount < averageCountMax) {
				averageCount++;
			}
			int countOfValues = sensorProperties.getCountOfValues();
			for(int i = 0; i < countOfValues; i++) {
				rawValues[i] = event.values[i];
				averageValues[i] = (averageValues[i] * (averageCount - 1) + rawValues[i]) / averageCount;
			}
			onSensorValueChangeListener.onSensorValueChange(this);
		}
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
