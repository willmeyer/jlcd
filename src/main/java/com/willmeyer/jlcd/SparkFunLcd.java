package com.willmeyer.jlcd;

import java.io.*;

import com.willmeyer.jrs232.*;

/**
 * Represents a SparkFun LCD with a connected SerLCD controller.  The controller accepts the 
 * standard display commands and passes them to the display, and has a few commands of its own
 * to manage the backlight, splashscreen, and more.
 * 
 * This class works with the controller connected via a USB virtual COM port (the FTDI 232 chip) and
 * the com.willmeyer.jrs232 base device API. 
 */
public final class SparkFunLcd extends Rs232Device {

	protected static final int BACKLIGHT_MIN = 128;
	protected static final int BACKLIGHT_MAX = 157;
	
	/**
	 * Puts the cursor at line 0 char 0.
	 */
	public void moveCursorToLine0() throws IOException { 
		this.moveCursorTo(0); 
	}

	/**
	 * Puts the cursor at line 1 char 0.
	 */
	public void moveCursorToLine1() throws IOException { 
		this.moveCursorTo(16); 
	}

	/**
	 * Sends a line of text wrapped across two lines, truncated as necessary so it doesn't overflow
	 * the display.
	 */
	public void sendWrappedMessage(String message) throws IOException {
		if (message.length() > 32) {
			message = message.substring(0, 32);
		}
		this.writeCharSequence(message, 0);
	}
	
	/**
	 * @param position 0-31, defaults back to 0 if out of range
	 */
	public void moveCursorTo(int position) throws IOException { 
		
		// internal positions:
		//   line 1: 0-15, 
		//   line 2: 64-83, 84+
		if (position < 16) {
			this.sendLcdCmdFlag();
			this.sendByte(position + 128); // position masked
		} else if (position < 32) {
			this.sendLcdCmdFlag();
			this.sendByte(position + 48 + 128); // position masked
		} else {
			moveCursorTo(0);
		}
	}

	public void clearDisplay() throws IOException {
		this.sendLcdCmdFlag();
		this.sendByte(0x01); // clear
	}

	public void backlightOn() throws IOException {
		this.backlightPercent(100);
	}

	public void splashScreenToggle() throws IOException {
		this.sendSerLcdCmdFlag();
		this.sendByte(9); 
	}

	public void splashScreenSet(String line1, String line2) throws IOException {
		this.clearDisplay();
		this.send2LineMessage(line1, line2);
		this.sendSerLcdCmdFlag();
		this.sendBytes("\nj".getBytes("utf-8")); 
	}
	
	public void backlightOff() throws IOException {
		this.backlightPercent(0);
	}

	public void backlightPercent(int percent) throws IOException {
		this.sendSerLcdCmdFlag();
		int backlight = (BACKLIGHT_MAX-BACKLIGHT_MIN) * percent / 100 + BACKLIGHT_MIN;
		this.sendByte(backlight); 
	}

	/**
	 * Sends the flag that precedes a command for the LCD (not the SerLCD).
	 */
	public void sendLcdCmdFlag() throws IOException { 
		this.sendByte(0xFE);
	}

	/**
	 * Sends the flag that precedes a command for the SerLCD (not the actual LCD).
	 */
	public void sendSerLcdCmdFlag() throws IOException { 
		this.sendByte(0x7C);
	}

	/**
	 * Sends 2 lines of text, one on each line.  Truncates lines as necessary.
	 */
	public void send2LineMessage(String line1, String line2) throws IOException {
		this.clearDisplay();
		if (line1.length() > 16) {
			line1 = line1.substring(0, 16);
		}
		this.writeCharSequence(line1, 0);
		if (line2.length() > 16) {
			line2 = line2.substring(0, 16);
		}
		this.writeCharSequence(line2, 16);
	}

	/**
	 * Sends 1 lines of text, on the top line.  Truncates as necessary.
	 */
	public void send1LineMessage(String message) throws IOException {
		if (message.length() > 16) {
			message = message.substring(0, 16);
		}
		this.writeCharSequence(message, 0);
	}

	public SparkFunLcd(String comPortName) throws Exception {
		super(comPortName, 9600, true);
	}

	/**
	 * No wrapping, just starts writing chars at the specified position until its out of chars.  
	 * This will wrap around on the display if its long enough.
	 */
	protected void writeCharSequence(String str, int startPos) throws IOException {
		this.moveCursorTo(startPos);
		this.sendBytes(str.getBytes("utf-8"));
	}
	
	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("[comport] [text]");
	}
	
	public static void main(String[] params) {
		SparkFunLcd lcd = null;
		if (params.length != 2) {
			printUsage();
			return;
		}
		String comPortName = params[0];
		String text = params[1];
		for (int i = 1; i < (params.length-1); i++) {
			text += params[i+1];
		}
		System.out.println("Going to write '" + text + "' to LCD on port '" + comPortName + "'");
		try {
			lcd = new SparkFunLcd(comPortName);
			lcd.connect();
			Thread.sleep(500);
			lcd.backlightPercent(90);
			Thread.sleep(500);
			lcd.clearDisplay();
			lcd.sendWrappedMessage(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (lcd != null)
			lcd.disconnect();
	}

}