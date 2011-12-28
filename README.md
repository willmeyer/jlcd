# jLCD

A Java interface for controlling simple LCD displays.

This library makes it easier to communicate with simple LCD text displays like [this one](http://www.sparkfun.com/products/9394).

### Supported LCD Devices

- This library currently supports devices controlled via the [SparkFun SerLCD v. 2.5](http://www.sparkfun.com/products/258) RS-232 control board

## Requirements and Setup

To get going, you'll need:

- A connected virtual serial port (likely via a USB FTDI chip)
- A SparkFun SerLCD controller and LCD display
- the [jRS232 package](http://www.github.com/willmeyer/jrs232) and its `com.willmeyer.jrs232`, with its install requirements

## Using the Library

See `com.willmeyer.jlcd.SparkFunLcd`.

For more information on the command-set, see this [datasheet](https://github.com/willmeyer/jlcd/blob/master/docs/SerLCD_V2_5.pdf).