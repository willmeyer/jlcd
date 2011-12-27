package com.willmeyer.jlcd;

import org.junit.*;

public class TestLcd 
{
	public SparkFunLcd lcd = null;
	
	private void setupLcd(String port) throws Exception {
		lcd = new SparkFunLcd(port);
		lcd.connect();
    }

	private void shutdownLcd() throws Exception {
		lcd.disconnect();
		lcd = null;
    }

	@Before
    public void beforeTest() throws Exception {
		this.setupLcd("COM1");
    }

	@After
    public void afterTest() throws Exception {
		this.shutdownLcd();
    }

	@Test
	public void testConnectDisconnect() throws Exception 
    {
		// The pre and post test steps do this...
    }

	@Test
	public void testSimpleText() throws Exception 
    {
		try {
			lcd.backlightOff();
			Thread.sleep(500);
			lcd.backlightOn();
			Thread.sleep(500);
			lcd.backlightPercent(30);
			Thread.sleep(500);
			lcd.backlightPercent(70);
			Thread.sleep(500);
			for (int i = 0; i < 32; i++) {
				lcd.clearDisplay();
				lcd.moveCursorTo(i);
				lcd.sendByte(("" + i).charAt(0));
				Thread.sleep(300);
			}
			lcd.clearDisplay();
			lcd.send2LineMessage("boo!", "yah!");
			Thread.sleep(500);
			lcd.clearDisplay();
			lcd.sendWrappedMessage("A wrapped message...Yeah that's it!");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    }

}
