
package com.yyg.schoolsports.utils;

import android.util.Log;
import android.view.KeyEvent;

public class Scanner {

	private onScanResultListener mListener;
	private StringBuffer mResult = new StringBuffer();

	private final static char[] numMap = {')','!','@','#','$','%','^','&','*','('};
	private final static char[] symShiftMap = {'~','_','+','{','}','|',':','"','?'};
	private final static char[] symMap      = {'`','-','=','[',']','\\',';','\'','/'};


	private void report() {
		if (mListener != null) {
			mListener.onScanResult(mResult.toString());
			mResult.setLength(0);
		}
	}

	private boolean parseKey(KeyEvent event) {
		boolean shift = event.isShiftPressed()?true:false;
		int keyCode = event.getKeyCode();
		char key;
		if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
			if (shift)
				key = numMap[keyCode - KeyEvent.KEYCODE_0];
			else
				key = (char) ('0' + keyCode - KeyEvent.KEYCODE_0);
			mResult.append(key);
		} else if (keyCode >= KeyEvent.KEYCODE_GRAVE && keyCode <= KeyEvent.KEYCODE_SLASH) {
			if (shift)
				key = symShiftMap[keyCode - KeyEvent.KEYCODE_GRAVE];
			else
				key = symMap[keyCode - KeyEvent.KEYCODE_GRAVE];
			mResult.append(key);
		} else if (keyCode == KeyEvent.KEYCODE_COMMA) {
			key = shift?'<':',';
			mResult.append(key);
		} else if (keyCode == KeyEvent.KEYCODE_PERIOD) {
			key = shift?'>':'.';
			mResult.append(key);
		} else if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
			if (shift)
				key = (char)('A' + keyCode - KeyEvent.KEYCODE_A);
			else
				key = (char) ('a' + keyCode - KeyEvent.KEYCODE_A);
			mResult.append(key);
		} else {
			return false;
		}
		return true;
	}

	public Scanner(onScanResultListener listener) {
		mListener = listener;
	}

	public boolean handleKey(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				report();
				return true;
			} else {
				return parseKey(event);
			}
		}
		return false;
	}

	public void release() {
		mListener = null;
	}

	public interface onScanResultListener {
		public void onScanResult(String result);
	}
}
