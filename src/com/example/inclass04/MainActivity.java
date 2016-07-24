/**
 * MainActivity.java
 * A Yang
 * Ajay Vijayakumaran Nair
 * Nachiket Doke
 */
package com.example.inclass04;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Handler handler;
	private int progress;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.buttonAsync).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (MainActivity.this.progress == 0) {
					Toast.makeText(MainActivity.this, "Please choose complexity >= 1", Toast.LENGTH_LONG).show();
					return;
				}
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setTitle("Retrieving the number");
				progressDialog.setMax(MainActivity.this.progress);
				progressDialog.setProgress(0);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setCancelable(false);
				progressDialog.show();
				new InCl4().execute();
			}
		});

		handler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.getData().containsKey("progress")) {
					if (msg.getData().getInt("progress") == MainActivity.this.progress + 1) {
						progressDialog.dismiss();
					} else {
						progressDialog.setProgress(msg.getData().getInt("progress"));
					}
				} else if (msg.getData().containsKey("result")) {
					((TextView) findViewById(R.id.textViewResult)).setText(msg.getData().getDouble("result") + "");
					progressDialog.dismiss();
				}
				return false;
			}
		});
		((SeekBar) findViewById(R.id.seekBar1)).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				if (fromUser) {
					MainActivity.this.progress = progress;
					((TextView) findViewById(R.id.textViewCompexityCount)).setText(progress + " Times");
				}
			}
		});
	}

	public void threadBtnClicked(View view) {
		if (MainActivity.this.progress == 0) {
			Toast.makeText(MainActivity.this, "Please choose complexity >= 1", Toast.LENGTH_LONG).show();
			return;
		}
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Retrieving the number");
		progressDialog.setMax(this.progress);
		progressDialog.setProgress(0);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(false);
		progressDialog.show();
		executorService.execute(new DoWork(MainActivity.this.progress));
	}

	private class DoWork implements Runnable {
		private int maxValue;

		private void sendProgressMsg(Integer progress) {
			Bundle bundle = new Bundle();
			bundle.putInt("progress", progress);
			Message message = new Message();
			message.setData(bundle);
			handler.sendMessage(message);
		}

		private void sendResultMsg(double result) {
			Bundle bundle = new Bundle();
			bundle.putDouble("result", result);
			Message message = new Message();
			message.setData(bundle);
			handler.sendMessage(message);
		}

		public DoWork(int maxValue) {
			super();
			this.maxValue = maxValue;
		}

		@Override
		public void run() {
			double sum = 0.0;
			for (int i = 0; i < maxValue; i++) {
				sum += HeavyWork.getNumber();
				sendProgressMsg(i + 1);
			}
			double avg = sum / maxValue;
			sendResultMsg(avg);
		}
	}

	private class InCl4 extends AsyncTask<Integer, Integer, Double> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Double doInBackground(Integer... params) {
			double sum = 0;
			for (int i = 1; i <= progress; i++) {
				sum = sum + HeavyWork.getNumber();
				publishProgress(i);
			}
			return (sum / progress);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			MainActivity.this.progressDialog.setProgress(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Double result) {
			MainActivity.this.progressDialog.dismiss();
			((TextView) findViewById(R.id.textViewResult)).setText(result + "");
			super.onPostExecute(result);
		}
	}
}
