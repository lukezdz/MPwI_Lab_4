package pl.zdziarski.lukasz;

import pl.zdziarski.lukasz.generator.CoordGenerator;
import pl.zdziarski.lukasz.generator.IGenerator;

import java.util.Scanner;

public class LabTask
{
	private static final double[][] defaultTable = {
			{0, 0.2, 0, 0},
			{0, 0, 0, 0.05},
			{0.1, 0, 0.3, 0},
			{0.1, 0.2, 0, 0.05}
	};
	private final Scanner scanner = new Scanner(System.in);
	private CoordGenerator generator;

	public void run() {
		System.out.println("Disclaimer: All matrices that are displayed are transposed in regards to the table from instruction");
		System.out.println("Would you like to create generator with custom settings? (y/n)");
		String choice = scanner.next();
		if (choice.equals("y")) {
			try	{
				handleResetting();
			}
			catch (Exception exception) {
				System.out.println("Something went wrong with creating generator. Please read instructions carefully");
				System.out.println("Setting generator to default state. You can change it later by choosing 4 in menu");
				initDefaultGenerator();
			}
		}
		else {
			initDefaultGenerator();
		}

		while (true) {
			menu();
			System.out.println("What would you like to do?");
			int menuChoice = scanner.nextInt();

			switch (menuChoice) {
				case 1: {
					generate();
					break;
				}
				case 2: {
					System.out.println("How many values would you like to generate?");
					int amount = scanner.nextInt();
					System.out.println("Would you like to view stats after generation? (y/n)");
					boolean showLogs = scanner.next().equals("y");
					generateMultiple(amount, showLogs);
					break;
				}
				case 3: {
					performLabTask();
					break;
				}
				case 4: {
					try	{
						handleResetting();
					}
					catch (Exception exception) {
						System.out.println("Last configuration had an invalid option.");
						System.out.println("Please read instructions carefully");
					}
					break;
				}
				case 5: {
					return;
				}
				default: {
					System.out.println("Invalid option.");
				}
			}
		}

	}

	private void menu() {
		System.out.println();
		System.out.println("1. Generate one pair of values");
		System.out.println("2. Generate multiple values");
		System.out.println("3. Generate 1000 values and print logs");
		System.out.println("4. Reset generator with new values");
		System.out.println("5. Quit");
	}

	private void initDefaultGenerator() {
		generator = new CoordGenerator.Builder()
				.withProbabilityTable(defaultTable)
				.build();
	}

	private void handleResetting() throws Exception {
		System.out.println("Please specify width of probability table: (default is 4)");
		int width = scanner.nextInt();
		System.out.println("Please specify height of probability table: (default is 4)");
		int height = scanner.nextInt();
		System.out.println("How many probabilities would you like to enter:");
		int amount = scanner.nextInt();
		System.out.println("Please specify cell index and its probability value:");
		System.out.println("\tx should be from 1 to " + width);
		System.out.println("\ty should be from 1 to " + height);
		System.out.println("\tvalue should be any double with , as separator not a .");

		CoordGenerator.Builder builder = new CoordGenerator.Builder();
		builder.withWidth(width);
		builder.withHeight(height);
		for (int i = 0; i < amount; i++) {
			System.out.print((i+1) + ". ");
			System.out.print("x: ");
			int x = scanner.nextInt();
			System.out.print("y: ");
			int y = scanner.nextInt();
			System.out.print("value: ");
			double value = scanner.nextDouble();
			if (x < 1 || x > width || y < 1 || y > height) {
				throw new Exception();
			}

			builder.withCellValue(x - 1, y - 1, value);
		}
		generator = builder.build();
	}

	private void performLabTask() {
		generator.clearLogs();
		for (int i = 0; i < 1000; i++) {
			generator.generate().log();
		}

		generator.presentLogs(IGenerator.PresentationMode.MATRIX);
	}

	private void generate() {
		generator.generate().print();
	}

	private void generateMultiple(int amount, boolean showLogs) {
		generator.clearLogs();
		for (int i = 0; i < amount; i++) {
			generator.generate().print();
			if (showLogs) {
				generator.log();
			}
		}
		if (showLogs) {
			generator.presentLogs(IGenerator.PresentationMode.RAW);
		}
	}

	public static void main(String[] args)	{
		LabTask task = new LabTask();
		task.run();
	}
}
