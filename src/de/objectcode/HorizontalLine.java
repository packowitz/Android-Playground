package de.objectcode;

import de.objectcode.ShapeTest.RenderView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class HorizontalLine extends Activity implements SensorEventListener {
  RenderView view;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);
    view = new RenderView(this);
    setContentView(view);

    SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
      view.text = "No accelerometer installed";
    } else {
      Sensor accelerometer = manager.getSensorList(
        Sensor.TYPE_ACCELEROMETER).get(0);
      if (!manager.registerListener(this, accelerometer,
            SensorManager.SENSOR_DELAY_GAME)) {
        view.text = "Couldn't register sensor listener";
      }
    }
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    float ax = event.values[0];
    float ay = event.values[1];
    float az = event.values[2];

    double mag = Math.sqrt((ax * ax) + (ay * ay) + az + az);
    view.angel = (int) Math.round((180 / Math.PI) * Math.acos(ay / mag)) - 90;
    view.text = String.format("x: %.1f, y: %.1f, z: %.1f, angel: %d", ax, ay, az,
      view.angel);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // nothing to do here
  }

  class RenderView extends View {
    String text = "";
    int angel = 0;
    Paint paint;

    public RenderView(Context context) {
      super(context);
      paint = new Paint();
      paint.setColor(Color.WHITE);
      paint.setTextSize(14);
      paint.setTextAlign(Paint.Align.LEFT);
    }

    protected void onDraw(Canvas canvas) {
      canvas.drawText(text, 1, 15, paint);

      float middleX = canvas.getWidth() / 2;
      float middleY = canvas.getHeight() / 2;

      double dx = 100 * Math.cos(Math.toRadians(angel));
      double dy = 100 * Math.sin(Math.toRadians(angel));
      float x1 = middleX - (float) dx;
      float y1 = middleY - (float) dy;
      float x2 = middleX + (float) dx;
      float y2 = middleY + (float) dy;

      canvas.drawLine(x1, y1, x2, y2, paint);

      invalidate();
    }
  }
}
