package pl.zdziarski.lukasz.generator;

import pl.zdziarski.lukasz.exceptions.EmptyArgumentsException;
import pl.zdziarski.lukasz.exceptions.InvalidValueException;
import pl.zdziarski.lukasz.utils.Pair;
import pl.zdziarski.lukasz.utils.Coord;

import java.util.*;

public class CoordGenerator implements IGenerator<Coord> {
	private final static int generationBound = 100;
	private final double[][] probabilityValues;
	private final int width;
	private final int height;

	private Logger logger;
	private Coord lastGenerated;

	private CoordGenerator(Optional<double[][]> probabilityTable,
						   Optional<List<Pair<Coord, Double>>> cellProbabilities,
						   int width, int height) {
		this.width = width;
		this.height = height;

		logger = new Logger();
		lastGenerated = null;

		if (probabilityTable.isPresent()) {
			probabilityValues = probabilityTable.get();
		} else {
			probabilityValues = new double[height][width];

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					setValue(x, y, 0);
				}
			}
		}

		if (cellProbabilities.isPresent()) {
			for (Pair<Coord, Double> pair : cellProbabilities.get()) {
				setValue(pair.getKey(), pair.getValue());
			}
		}

		scaleProbabilities();
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public CoordGenerator generate() {
		Random rand = new Random();
		while (true)
		{
			int x = rand.nextInt(width);
			int generated = rand.nextInt(generationBound);

			for (int y = 0; y < height; y++)
			{
				if (generated < at(x, y))
				{
					lastGenerated = new Coord(x + 1, y + 1);
					return this;
				}
			}
		}
	}

	@Override
	public CoordGenerator print() {
		System.out.println(lastGenerated.toString());
		return this;
	}

	@Override
	public Coord getGenerated() {
		return lastGenerated;
	}

	@Override
	public CoordGenerator log()	{
		logger.addLog(lastGenerated);
		return this;
	}

	@Override
	public CoordGenerator presentLogs(PresentationMode mode) {
		if (mode == PresentationMode.RAW) {
			logger.presentRaw();
		}
		else if (mode == PresentationMode.MATRIX) {
			logger.presentMatrix();
		}

		return this;
	}

	@Override
	public void clearLogs() {
		logger = new Logger();
	}

	private void scaleProbabilities() {
		for (int x = 0; x < width; x++) {
			double rowSum = 0;
			for (int y = 0; y < height; y++) {
				rowSum += at(x, y);
			}

			if (rowSum == 0) {
				continue;
			}

			double tempSum = 0;
			for (int y = 0; y < height; y++) {
				double currCell = at(x, y);
				if (currCell != 0) {
					double scaledValue = currCell / rowSum * 100 + tempSum;
					setValue(x, y, scaledValue);
					tempSum += scaledValue;
				}
			}
		}
	}

	private double at(Coord coord) throws ArrayIndexOutOfBoundsException {
		return at(coord.getX(), coord.getY());
	}

	private double at(int x, int y) throws ArrayIndexOutOfBoundsException {
		return probabilityValues[y][x];
	}

	private void setValue(Coord coord, double value) throws ArrayIndexOutOfBoundsException {
		setValue(coord.getX(), coord.getY(), value);
	}

	private void setValue(int x, int y, double value) throws ArrayIndexOutOfBoundsException {
		probabilityValues[y][x] = value;
	}

	public static class Builder {
		private int width = 4;
		private int height = 4;
		private Optional<double[][]> probabilityTable = Optional.empty();
		private Optional<List<Pair<Coord, Double>>> probabilityValues = Optional.empty();

		private Builder() {}

		public CoordGenerator build() throws EmptyArgumentsException {
			if (probabilityTable.isEmpty() && probabilityValues.isEmpty()) {
				throw new EmptyArgumentsException(
						String.format("No probability values were given. You need to specify at least 1 probability value."));
			}

			return new CoordGenerator(probabilityTable, probabilityValues, width, height);
		}

		public Builder withProbabilityTable(double[][] table) {
			probabilityTable = Optional.of(table);
			return this;
		}

		public Builder withCellValue(int x, int y, double value) throws InvalidValueException {
			if (x <= 0) {
				throw new InvalidValueException("x", "width", false);
			}
			if (x > width) {
				throw new InvalidValueException("x", "width", true);
			}
			if (y <= 0) {
				throw new InvalidValueException("y", "height", false);
			}
			if (y > height) {
				throw new InvalidValueException("y", "height", true);
			}

			if (probabilityValues.isEmpty()) {
				probabilityValues = Optional.of(new ArrayList<>());
			}
			probabilityValues.get().add(new Pair<>(new Coord(x, y), value));

			return this;
		}

		public Builder withWidth(int width) throws InvalidValueException {
			if (width <= 0 ) {
				throw new InvalidValueException("width");
			}
			this.width = width;
			return this;
		}

		public Builder withHeight(int height) throws InvalidValueException {
			if (height <= 0) {
				throw new InvalidValueException("height");
			}
			this.height = height;
			return this;
		}
	}

	private class Logger {
		private final int[][] logs = new int[height][width];
		private int logsAmount = 0;

		public Logger() {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					logs[y][x] = 0;
				}
			}
		}

		public void addLog(Coord log) {
			int x = log.getX() - 1;
			int y = log.getY() - 1;

			logs[y][x] += 1;
			logsAmount++;
		}

		private void presentRaw() {
			StringBuilder builder = new StringBuilder();
			builder.append(initialMessage());
			Map<Integer, Coord> sortedLogs = new TreeMap<>(Collections.reverseOrder());

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int amount = logs[y][x];
					if (amount > 0)	{
						Coord coord = new Coord(x + 1, y + 1);
						sortedLogs.put(amount, coord);
					}
				}
			}

			for (Map.Entry<Integer, Coord> entry : sortedLogs.entrySet()) {
				builder.append(entry.getValue());
				builder.append(": ");
				builder.append(entry.getKey());
				builder.append("\n");
			}

			System.out.println(builder.toString());
		}

		private void presentMatrix() {
			StringBuilder builder = new StringBuilder();
			builder.append(initialMessage());

			builder.append(" yx");
			for (int x = 0; x < width; x++) {
				builder.append(String.format("%6d ", x));
			}
			builder.append("\n");

			for (int y = 0; y < height; y++) {
				builder.append(String.format("%2d ", y));
				for (int x = 0; x < width; x++) {
					builder.append(String.format("%6d ", logs[y][x]));
				}
				builder.append("\n");
			}

			System.out.println(builder.toString());
		}

		private String initialMessage() {
			return String.format("There were %d logs recorded\n", logsAmount);
		}
	}
}
