package trader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import learner.Learner;
import learner.SarsaLambda;
import simulator.Game;

/**
 * Trading game, using OHLC + Volume data from Yahoo Finance, loading it from a
 * .csv file.
 * 
 * @author Alex Braithwaite
 *
 */
public class TradingGame implements Game {

	private Learner learner;
	private List<String> inputFiles = new ArrayList<String>();
	private List<List<OHLCV>> data = new ArrayList<List<OHLCV>>();
	private List<List<OHLCV>> deltas = new ArrayList<List<OHLCV>>();
	private double predictRate = 50;

	public TradingGame() {
		inputFiles.add("barc.csv");
		inputFiles.add("bp.csv");
		inputFiles.add("glen.csv");
		inputFiles.add("ibm.csv");
		inputFiles.add("itv.csv");
		inputFiles.add("lloy.csv");
		inputFiles.add("rbs.csv");
		inputFiles.add("tsco.csv");
		inputFiles.add("vod.csv");
	}

	@Override
	public void setLeaner(Learner brain) {
		this.learner = brain;
	}

	@Override
	public void setupGame() {
		try {
			for (String file : inputFiles) {

				// open file
				InputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);

				// load data from file

				// skip first line
				br.readLine();

				String line;
				List<OHLCV> fileData = new LinkedList<OHLCV>();
				List<OHLCV> fileDeltas = new LinkedList<OHLCV>();

				OHLCV deltaSum = new OHLCV(0, 0, 0, 0, 0);

				while ((line = br.readLine()) != null) {
					String[] ohlcvString = line.split(",");
					// ohlcvString[0] is date, don't want that
					// ohlcvString[6] is adjusted close, let's just use close
					double o = Double.parseDouble(ohlcvString[1]);
					double h = Double.parseDouble(ohlcvString[2]);
					double l = Double.parseDouble(ohlcvString[3]);
					double c = Double.parseDouble(ohlcvString[4]);
					double v = Double.parseDouble(ohlcvString[5]);
					OHLCV ohlcv = new OHLCV(o, h, l, c, v);
					((LinkedList<OHLCV>) fileData).addFirst(ohlcv);
					// load delta for each line too
					if (fileData.size() > 1) {
						OHLCV prev = fileData.get(1);
						OHLCV delta = prev.sub(ohlcv);
						((LinkedList<OHLCV>) fileDeltas).addFirst(delta);
						OHLCV posDelta = new OHLCV(delta.open > 0 ? delta.open : -delta.open,
								delta.high > 0 ? delta.high : -delta.high, delta.low > 0 ? delta.low : -delta.low,
								delta.close > 0 ? delta.close : -delta.close,
								delta.volume > 0 ? delta.volume : -delta.volume);
						deltaSum = deltaSum.add(posDelta);
					}
				}
				((LinkedList<OHLCV>) fileDeltas).addFirst(new OHLCV(0, 0, 0, 0, 0));

				deltaSum = deltaSum.div(fileData.size());

				for (int i = 0; i < fileDeltas.size(); i++) {
					fileDeltas.set(i, fileDeltas.get(i).div(deltaSum));
				}

				br.close();

				data.add(fileData);
				deltas.add(fileDeltas);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void teardown() {
		// nothing to do here
	}

	@Override
	public double runTrial() {

		int timeToFillMemory = ((SarsaLambda) learner).settings.memoryLength
				* ((SarsaLambda) learner).settings.memoryInterval;
		
		List<OHLCV> trialData = new ArrayList<OHLCV>();
		List<OHLCV> trialDeltas = new ArrayList<OHLCV>();

		int dataToUse = -1;
		while (trialData.size() < timeToFillMemory+365 || dataToUse == -1) {
			dataToUse = new Random().nextInt(data.size());
			trialData = data.get(dataToUse);
			trialDeltas = deltas.get(dataToUse);
		}

		int interval = 5;
		int totalReward = 0;

		learner.reset();
		int reward = 0;

		int startPos = new Random().nextInt(trialData.size() - 365 - interval - timeToFillMemory);
		for (int j = 0; j < 365 + timeToFillMemory; j++) {
			OHLCV curr = trialData.get(j + startPos);
			OHLCV currDelta = trialDeltas.get(j + startPos);
			int output = learner.getAction(currDelta.toList(), reward);

			boolean predictRise = output == 0;

			if (j > timeToFillMemory) {
				OHLCV next = trialData.get(j + startPos + interval);
				boolean rise = next.close > curr.close;
				reward = ((predictRise && rise) || (!predictRise && !rise)) ? 1 : 0;
				totalReward += reward;
			}
		}

		predictRate += (100 * (totalReward / 365.) - predictRate) / 10;
		return predictRate;
	}

}
