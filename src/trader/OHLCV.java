package trader;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for Open, High, Low, Close, Volume
 * 
 * @author Alex Braithwaite
 *
 */
public class OHLCV {
	
	public double open, high, low, close, volume;

	public OHLCV(double open, double high, double low, double close, double volume) {
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	public OHLCV add(OHLCV other) {
		double open = this.open + other.open;
		double high = this.high + other.high;
		double low = this.low + other.low;
		double close = this.close + other.close;
		double volume = this.volume + other.volume;
		return new OHLCV(open, high, low, close, volume);
	}

	public OHLCV sub(OHLCV other) {
		double open = this.open - other.open;
		double high = this.high - other.high;
		double low = this.low - other.low;
		double close = this.close - other.close;
		double volume = this.volume - other.volume;
		return new OHLCV(open, high, low, close, volume);
	}

	public OHLCV mul(OHLCV other) {
		double open = this.open * other.open;
		double high = this.high * other.high;
		double low = this.low * other.low;
		double close = this.close * other.close;
		double volume = this.volume * other.volume;
		return new OHLCV(open, high, low, close, volume);
	}

	public OHLCV div(OHLCV other) {
		double open = this.open / other.open;
		double high = this.high / other.high;
		double low = this.low / other.low;
		double close = this.close / other.close;
		double volume = this.volume / other.volume;
		return new OHLCV(open, high, low, close, volume);
	}

	public OHLCV div(double s) {
		double open = this.open / s;
		double high = this.high / s;
		double low = this.low / s;
		double close = this.close / s;
		double volume = this.volume / s;
		return new OHLCV(open, high, low, close, volume);
	}

	public List<Double> toList() {
		List<Double> list = new ArrayList<Double>(5);
		list.add(volume);
		list.add(close);
		list.add(low);
		list.add(high);
		list.add(open);
		return list;
	}

	@Override
	public String toString() {
		return "OHLCV [open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", volume=" + volume
				+ "]";
	}
	
}
