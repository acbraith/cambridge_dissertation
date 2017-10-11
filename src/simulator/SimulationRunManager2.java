package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.distribution.TDistribution;

import learner.Settings;

/**
 * Class to manage execution and data of a large number of Simulations, each
 * consisting of a sequence of Games.
 * 
 * @author Alex Braithwaite
 *
 */
public class SimulationRunManager2 {

	private String simulationName;

	/**
	 * Return all the possible combinations of Settings between a minimum and
	 * maximum, using a given divisor.
	 * 
	 * @param min
	 * @param max
	 * @param divisor
	 * @return
	 */
	public List<Settings> settingCombinations(Settings min, Settings max, int divisor) {
		List<Settings> settingsList = new ArrayList<Settings>();

		// hiddenNodes;
		// alpha;
		// gamma;
		// lambda;
		// T;
		// epsilon;
		// alphaDecayRate;
		// TDecayRate;
		// epsilonDecayRate;
		// numAgents;
		// numInputs;
		// numOutputs;
		// memoryLength;
		// memoryInterval;

		Settings range = max.sub(min);

		for (int h = 1; h <= divisor; h++) {
			for (int a = 1; a <= divisor; a++) {
				for (int g = 1; g <= divisor; g++) {
					for (int l = 1; l <= divisor; l++) {
						for (int t = 1; t <= divisor; t++) {
							for (int e = 1; e <= divisor; e++) {
								for (int ad = 1; ad <= divisor; ad++) {
									for (int ld = 1; ld <= divisor; ld++) {
										for (int td = 1; td <= divisor; td++) {
											for (int ed = 1; ed <= divisor; ed++) {
												for (int na = 1; na <= divisor; na++) {
													for (int ni = 1; ni <= divisor; ni++) {
														for (int no = 1; no <= divisor; no++) {
															for (int mi = 1; mi <= divisor; mi++) {
																for (int ml = 1; ml <= divisor; ml++) {
																	if (min.hiddenNodes == max.hiddenNodes)
																		h = divisor + 1;
																	if (min.alpha == max.alpha)
																		a = divisor + 1;
																	if (min.gamma == max.gamma)
																		g = divisor + 1;
																	if (min.lambda == max.lambda)
																		l = divisor + 1;
																	if (min.T == max.T)
																		t = divisor + 1;
																	if (min.epsilon == max.epsilon)
																		e = divisor + 1;
																	if (min.alphaDecayRate == max.alphaDecayRate)
																		ad = divisor + 1;
																	if (min.lambdaDecayRate == max.lambdaDecayRate)
																		ld = divisor + 1;
																	if (min.TDecayRate == max.TDecayRate)
																		td = divisor + 1;
																	if (min.epsilonDecayRate == max.epsilonDecayRate)
																		ed = divisor + 1;
																	if (min.numAgents == max.numAgents)
																		na = divisor + 1;
																	if (min.numInputs == max.numInputs)
																		ni = divisor + 1;
																	if (min.numOutputs == max.numOutputs)
																		no = divisor + 1;
																	if (min.memoryInterval == max.memoryInterval)
																		mi = divisor + 1;
																	if (min.memoryLength == max.memoryLength)
																		ml = divisor + 1;

																	Settings delta = range
																			.scaleElements(h, a, g, l, t, e, ad, ld, td,
																					ed, na, ni, no, mi, ml)
																			.scale(1 / (divisor + 1.));
																	Settings s = min.add(delta);
																	settingsList.add(s);

																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return settingsList;
	}

	/**
	 * Output data to a time-stamped file in /log for a simulation to search for
	 * optimal hyperparameter settings.
	 * 
	 * @param min
	 * @param max
	 * @param simulationLength
	 * @param gameType
	 * @throws InterruptedException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unused")
	public void optimise(Settings min, Settings max, int simulationLength, String gameType)
			throws InterruptedException, FileNotFoundException, UnsupportedEncodingException {

		String startTime = SimulationRunManager2.getDateTime().replace(" ", "-").replace(":", "-");
		simulationName = "log/" + gameType + "_" + startTime + "_OPTIMISE";

		PrintWriter logFile = new PrintWriter(simulationName + ".log", "UTF-8");
		@SuppressWarnings("resource")
		PrintWriter resultFile = new PrintWriter(simulationName + ".result", "UTF-8");

		int depth = 0;
		int repeats = 0;
		int totalTrials = 0;
		int divisor = 4;

		Settings range = max.sub(min);
		Settings delta = range.scale(1. / (1 + divisor));

		logFile.println("OPTIMISE SIMULATION");
		logFile.println("Game Type: \n\t" + gameType);
		logFile.println("Initial Min: \n\t" + min);
		logFile.println("Initial Max: \n\t" + max);
		logFile.println("Simulation length: \n\t" + simulationLength);
		logFile.println("Divisor: \n\t" + divisor);
		logFile.flush();
		resultFile.println("OPTIMISE SIMULATION");
		resultFile.println("Initial Min: \n\t" + min);
		resultFile.println("Initial Max: \n\t" + max);
		resultFile.println("Simulation length: \n\t" + simulationLength);
		resultFile.println("Divisor: \n\t" + divisor);
		resultFile.flush();

		// 1:

		while (true) {

			depth++;
			repeats = 0;

			// initialise priority queue with all settings combinations
			List<Settings> settingsList = settingCombinations(min, max, divisor);
			PriorityBlockingQueue<OptimiseSimulationData> pbq = new PriorityBlockingQueue<OptimiseSimulationData>(
					settingsList.size());
			Map<Settings, OptimiseSimulationData> osdMap = new HashMap<Settings, OptimiseSimulationData>(
					settingsList.size());
			for (Settings s : settingsList) {
				OptimiseSimulationData osd = new OptimiseSimulationData(s, 0, 0, Integer.MAX_VALUE);
				pbq.add(osd);
				osdMap.put(s, osd);
			}

			// 2:

			// select best 2 final rewards
			OptimiseSimulationData best = pbq.take();
			OptimiseSimulationData secondBest = pbq.peek();
			pbq.add(best);

			// exit the loop when best is significantly better than second best
			// second best being the best non-adjacent result
			// or (if divisor > 2) when best and second best are adjacent (so
			// optimum is likely between them)
			while ((best.finalReward - best.confidenceInterval / 2) < (secondBest.finalReward
					+ secondBest.confidenceInterval / 2)) {

				// do a batch of repeats before considering leaving loop
				for (int i = 0; i < 10; i++) {

					repeats++;

					logFile.println(getDateTime());
					logFile.println("* * * * * * * * * * * * * * * * * * * * * * * * ");
					logFile.println("* * * * * * * * * * * * * * * * * * * * * * * * ");
					logFile.println("\nDepth " + depth + ": Round " + repeats);
					logFile.flush();

					// XXX OPTIMISE processors
					int processors = 4;// Runtime.getRuntime().availableProcessors();
					ExecutorService executor = Executors.newFixedThreadPool(processors);
					List<SimulationRunner> workers = new ArrayList<SimulationRunner>(settingsList.size());

					// run trial for every settings combination
					for (Settings s : settingsList) {
						totalTrials++;
						SimulationRunner worker = new SimulationRunner(simulationLength, simulationLength, s, gameType,
								logFile);
						workers.add(worker);
						executor.execute(worker);
					}

					// 3:

					// wait for trials to complete
					executor.shutdown();

					try {
						executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 4:

					// gather results
					// maths is from CompSysModelling notes slides ~80-83 and
					// question i.4
					// use t distribution to calculate t_a, where P(T > t) = a
					double v = repeats - 1;
					TDistribution tDist = new TDistribution(v < 1 ? 1 : v);
					// 90 percent confidence interval, so 0.95
					double t_a = tDist.cumulativeProbability(0.95);

					for (SimulationRunner sr : workers) {
						int n = repeats;

						Settings s = sr.settings;
						List<Double> rewards = sr.simResult;
						double X_n = rewards.get(rewards.size() - 1);

						// get data to be changed
						OptimiseSimulationData osd = osdMap.get(s);

						// update mean (Y = X bar)
						// Yn = Yn-1 + (Xn - Yn-1) / n
						double XBar_n_1 = osd.finalReward;
						double XBar_n = XBar_n_1 + (X_n - XBar_n_1) / n;
						osd.finalReward = XBar_n;

						// update variance
						// Sn = Sn−1 + (Xn − Yn−1)(Xn − Yn)
						double S_n_1 = osd.sumSquareDif;
						double S_n = S_n_1 + (X_n - XBar_n_1) * (X_n - XBar_n);
						osd.sumSquareDif = S_n;
						double variance = S_n / ((n - 1) < 1 ? 1 : (n - 1));

						// update error in estimate of mean
						// t * S / sqrt(n)
						osd.confidenceInterval = t_a * Math.sqrt(variance) / Math.sqrt(n);
						// too few samples; make error MAX_INTEGER
						if (v < 1)
							osd.confidenceInterval = Integer.MAX_VALUE;

						// update priority queue
						pbq.remove(osd);
						pbq.add(osd);
					}

					// select best 2 final rewards
					best = pbq.take();
					secondBest = pbq.take();
					// ie best one, then find first non-adjacent to be second
					// best (unless we use divisor 2)
					List<OptimiseSimulationData> bestList = new ArrayList<OptimiseSimulationData>();
					bestList.add(best);
					bestList.add(secondBest);
					int iterations = 2;
					// check to see if adjacent (scale delta up a bit due to
					// floating point rounding errors)
					// XXX currently disabled
					while (false && divisor > 2 && iterations < settingsList.size()
							&& (best.settings.sub(secondBest.settings).magnitudeLessThanEqualTo(delta.scale(1.5)))) {
						iterations++;
						secondBest = pbq.take();
						bestList.add(secondBest);
					}

					// put them back into priority queue
					for (OptimiseSimulationData osd : bestList) {
						pbq.add(osd);
					}

					logFile.println("Best " + best);
					logFile.println("Second Best " + secondBest);
					logFile.flush();

				}

				// END WHILE, goto 2
			}

			// 5:
			// exited while loop
			// so we have a significant result, can stop running repeats

			// recalculate min and max
			min = best.settings.add(delta.scale(-1));
			max = best.settings.add(delta);
			range = max.sub(min);
			delta = range.scale(1. / (1 + divisor));
			// output details
			resultFile.println();
			resultFile.println(getDateTime());
			resultFile.println(
					totalTrials + " trials: Finished Round " + depth + ". " + repeats + " repeats this round.");
			resultFile.println("Best Settings: \n\t" + best.settings);
			resultFile.println("Uncertainty: \n\t" + range.scale(0.5));
			resultFile.println("Next Round Min: \n\t" + min);
			resultFile.println("Next Round Max: \n\t" + max);
			resultFile.println();
			resultFile.flush();

			logFile.println();
			logFile.println("********************************************************************************");
			logFile.println("********************************************************************************");
			logFile.println("********************************************************************************");
			logFile.println();
			logFile.flush();

			// END WHILE, goto 1
		}
	}

	/**
	 * Output data to a time-stamped file in /log/ for a simulation to explore
	 * performance between minimum and maximum settings.
	 * 
	 * @param min
	 * @param max
	 * @param divisor
	 * @param simulationLength
	 * @param repeats
	 * @param gameType
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void explore(Settings min, Settings max, int divisor, int simulationLength, int repeats, String gameType)
			throws FileNotFoundException, UnsupportedEncodingException {

		String startTime = SimulationRunManager2.getDateTime().replace(" ", "-").replace(":", "-");
		simulationName = "log/" + gameType + "_" + startTime + "_EXPLORE";

		int totalDataPoints = Math.min(100, simulationLength);

		PrintWriter logFile = new PrintWriter(simulationName + ".log", "UTF-8");
		PrintWriter resultFile = new PrintWriter(simulationName + ".result", "UTF-8");

		logFile.println("EXPLORE SIMULATION");
		logFile.println("Game Type: \n\t" + gameType);
		logFile.println("Initial Min: \n\t" + min);
		logFile.println("Initial Max: \n\t" + max);
		logFile.println("Simulation length: \n\t" + simulationLength);
		logFile.println("Divisor: \n\t" + divisor);
		logFile.println("Repeats: \n\t" + repeats);
		logFile.flush();
		resultFile.println("EXPLORE SIMULATION");
		resultFile.println("Initial Min: \n\t" + min);
		resultFile.println("Initial Max: \n\t" + max);
		resultFile.println("Simulation length: \n\t" + simulationLength);
		resultFile.println("Divisor: \n\t" + divisor);
		resultFile.println("Repeats: \n\t" + repeats);
		resultFile.flush();

		List<Settings> settingsList = settingCombinations(min, max, divisor);

		// use t distribution to calculate t_a, where P(T > t) = a
		// for use later in calculating confidence intervals
		double v = repeats - 1;
		TDistribution tDist = new TDistribution(v < 1 ? 1 : v);
		// 95 percent confidence interval, so 0.975
		double t_a = tDist.cumulativeProbability(0.975);

		// XXX EXPLORE processors
		int processors = 1;// Runtime.getRuntime().availableProcessors() / 2;
		// ExecutorService executor = Executors.newFixedThreadPool(processors);

		ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors, 60, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>());

		Map<Settings, List<SimulationRunner>> workers = new HashMap<Settings, List<SimulationRunner>>(
				settingsList.size());

		// run trials for every settings combination
		for (Settings s : settingsList) {
			List<SimulationRunner> settingWorkers = new ArrayList<SimulationRunner>(repeats);

			for (int i = 0; i < repeats; i++) {
				SimulationRunner worker = new SimulationRunner(simulationLength, totalDataPoints, s, gameType, logFile);
				executor.execute(worker);
				settingWorkers.add(worker);
			}

			workers.put(s, settingWorkers);
		}

		executor.shutdown();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		// wait for trials to complete
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(1000);
				// read stdin for anything
				if (br.ready()) {
					String line = br.readLine();
					int i = 0;
					try {
						i = Integer.parseInt(line);
					} catch (Exception e) {
					}
					System.out.println("You requested " + i + " threads");
					// set number of threads to integer given
					if (i > 0 && i < Runtime.getRuntime().availableProcessors() + 1) {
						executor.setCorePoolSize(i);
						executor.setMaximumPoolSize(i);
					}
					System.out.println("Set to " + executor.getCorePoolSize() + " threads");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logFile.println("* * * * * * * * * * * * * * * * * * * * * * * * ");
		logFile.println("* * * * * * * * * * * * * * * * * * * * * * * * ");
		logFile.println();
		logFile.println("Finished");
		logFile.println(

				getDateTime());
		logFile.println();

		// map settings to a pair of lists, one being means and the other
		// standard deviations
		Map<Settings, List<List<Double>>> results = new HashMap<Settings, List<List<Double>>>(settingsList.size());
		// average results, getting mean and variance of each data point
		for (Settings s : settingsList) {

			List<SimulationRunner> settingWorkers = workers.get(s);
			List<Double> means = new ArrayList<Double>(totalDataPoints);
			List<Double> stdevs = new ArrayList<Double>(totalDataPoints);
			List<Double> confidenceIntervals = new ArrayList<Double>(totalDataPoints);

			for (int i = 0; i < totalDataPoints; i++) {
				means.add(0.);
				stdevs.add(0.);
				confidenceIntervals.add(0.);
			}

			// calculate means
			for (SimulationRunner sr : settingWorkers) {
				List<Double> data = sr.simResult;

				logFile.println();
				logFile.println(s);
				logFile.println("Results: \n\t" + data);
				logFile.println();
				logFile.flush();

				for (int i = 0; i < data.size(); i++) {
					double x = data.get(i);
					means.set(i, means.get(i) + x);
				}
			}
			for (int i = 0; i < totalDataPoints; i++) {
				means.set(i, means.get(i) / repeats);
			}

			// calculate standard deviations
			for (SimulationRunner sr : settingWorkers) {
				List<Double> data = sr.simResult;

				for (int i = 0; i < data.size(); i++) {
					double x = data.get(i);
					stdevs.set(i, stdevs.get(i) + Math.pow(x - means.get(i), 2));
				}
			}
			for (int i = 0; i < totalDataPoints; i++) {
				stdevs.set(i, Math.sqrt(stdevs.get(i) / (repeats - 1)));
			}

			// calculate confidence intervals
			// 95% confidence interval to plot error bars
			for (int i = 0; i < totalDataPoints; i++) {
				double ci = t_a * stdevs.get(i) / Math.sqrt(repeats);
				confidenceIntervals.set(i, ci);
			}

			// store these into results
			List<List<Double>> meanStdev = new ArrayList<List<Double>>(3);
			meanStdev.add(means);
			meanStdev.add(stdevs);
			meanStdev.add(confidenceIntervals);

			results.put(s, meanStdev);

		}

		// output results
		for (Map.Entry<Settings, List<List<Double>>> e : results.entrySet()) {
			Settings s = e.getKey();
			List<Double> means = e.getValue().get(0);
			List<Double> stdevs = e.getValue().get(1);
			List<Double> confidenceIntervals = e.getValue().get(2);
			resultFile.println();
			resultFile.println(s);
			resultFile.println("Means: \n\t" + means);
			resultFile.println("Standard deviations: \n\t" + stdevs);
			resultFile.println("Confidence intervals: \n\t" + confidenceIntervals);
		}

		resultFile.flush();

		logFile.close();
		resultFile.close();
	}

	/**
	 * Get a formatted data and time.
	 * 
	 * @return
	 */
	public static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("d MMM HH:mm:ss");
		// get current date time
		Date date = new Date();
		return dateFormat.format(date);
	}

}
