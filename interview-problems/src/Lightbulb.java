
/**
 * I've seen this question phrased with lightbulbs and cell phones. To spice it up a bit, I'm going to explain it in terms of bio-engineered chickens.
 *
 * Goal: Given two egg producing chickens and a building with x number floors, minimize the (worst case) number of tests to see how far an egg can be dropped
 * before breaking. When an egg breaks the chicken that produced it becomes angry and flies away.
 * 
 * Bonus points: minimized the expected value of drops.
 *
 * @author Chad
 */
public class Lightbulb
{

	public static void main(String[] args)
	{
		if (parseHelp(args))
			return;

		Integer numFloors = parseNumFloors(args);
		if (numFloors != null)
		{
			TestStrategy optimalTestStrategy = createOptimalTestStrategy(numFloors);
			System.out.println(optimalTestStrategy.toDescription());
		}
	}

	/**
	 *
	 * @param numFloors
	 * @return
	 */
	private static TestStrategy createOptimalTestStrategy(int numFloors)
	{
		if (numFloors == 0)
			return null;
		if (numFloors <= 3)
			return new TestStrategy(numFloors, 1, null);
		TestStrategy currentOptimalStrategy = null;
		//heuristic...sqrt is the sweet spot between case first test fails and worst case all succeed.
		for (int floorsBetweenTests = (int) Math.floor(Math.pow(numFloors, .4)); floorsBetweenTests <= Math.ceil(Math.pow(numFloors, .6)); floorsBetweenTests++)
		{
			int remainderFloors = numFloors % floorsBetweenTests;
			int floorsBeforeRemainder = numFloors - remainderFloors;
			int numTestsBeforeRemainder = floorsBeforeRemainder / floorsBetweenTests;
			TestStrategy remainderStrategy = createOptimalTestStrategy(remainderFloors);
			TestStrategy testingStrategy = new TestStrategy(floorsBetweenTests, numTestsBeforeRemainder, remainderStrategy);
			if (testingStrategy.betterThan(currentOptimalStrategy))
				currentOptimalStrategy = testingStrategy;
		}
		return currentOptimalStrategy;
	}

	private static boolean parseHelp(String[] args)
	{
		for (String arg : args)
		{
			if (arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-h"))
			{
				System.out.println("\nThis program finds the optimal plan for testing the durability of genetically modified chicken eggs. Given two chickens and a building with a set "
								   + " number of floors (position argument 1) we can devise a strategy to minimize the number of drops in the best and worst case.\n"
								   + "Since we're trying to minimize the worst case and we know the worst case would be for the chicken egg to break "
										   + "on the first floor after a large skip in floors, we can reasonably discern we're looking for a consistent skip in floors.\n"
								   + "Since we're trying to optimize the best case we need to make sure the egg not breaking doesn't require more drops than the egg breaking on the first drop. \n"
								   + " positional arguments:\n"
										   + "1.           the number of floors\n"
										   + "\n"
										   + "optional arguments:\n"
										   + " -h, --help  show this help message and exit\n");
				return true;
			}
		}
		return false;
	}

	private static Integer parseNumFloors(String[] args)
	{
		if (args.length > 0)
		{
			try
			{
				int parseInt = Integer.parseInt(args[0]);
				if (parseInt > 0)
					return parseInt;
			}
			catch (NumberFormatException e)
			{
			}
			System.out.println("The first argument is required and should be a positive integer");
		}
		return null;
	}

	private static class TestStrategy
	{
		private final int floorsBetweenTests;
		private final int numTestsBeforeRemainder;
		private final TestStrategy remainderStrategy;
		private Double lazyExpectedValue;

		public TestStrategy(int floorsBetweenTests, int numTestsBeforeRemainder, TestStrategy remainderStrategy)
		{
			this.floorsBetweenTests = floorsBetweenTests;
			this.numTestsBeforeRemainder = numTestsBeforeRemainder;
			this.remainderStrategy = remainderStrategy;
		}

		public int calculateWorstCase()
		{
			return Math.max(calculateCaseLastTestBreaks(),
							calculateCaseMyTestSucceed() + (remainderStrategy == null ? 0 : remainderStrategy.calculateWorstCase()));
		}

		private int calculateCaseLastTestBreaks()
		{
			return numTestsBeforeRemainder //all tests succeed until the last one
						   + floorsBetweenTests - 1; // count up from the last known good to this one
		}

		private int calculateCaseMyTestSucceed()
		{
			return numTestsBeforeRemainder;
		}

		public boolean betterThan(TestStrategy otherStrategy)
		{
			if (otherStrategy == null)
				return true;
			else if (calculateWorstCase() < otherStrategy.calculateWorstCase())
				return true;
			else if (calculateWorstCase() == otherStrategy.calculateWorstCase() && calculateExpectedValue() < otherStrategy.calculateExpectedValue())
				return true;
			else return false;
		}

		private double calculateExpectedValue()
		{
			if (lazyExpectedValue == null)
			{
				int sum = 0;
				int count = 0;
				TestStrategy currentStrategy = this;
				while (currentStrategy != null)
				{
					//capture all tests succeed case
					sum += currentStrategy.numTestsBeforeRemainder;
					count += 1;
					//capture test fail cases
					for (int breakingTest = 1; breakingTest <= currentStrategy.numTestsBeforeRemainder; breakingTest++)
					{
						for (int finalBreakingTest = 1; finalBreakingTest < currentStrategy.floorsBetweenTests; finalBreakingTest++)
						{
							sum += breakingTest + finalBreakingTest;
							count++;
						}
					}
					currentStrategy = currentStrategy.remainderStrategy;
				}
				lazyExpectedValue = sum / (double) count;
			}
			return lazyExpectedValue;
		}

		public String toDescription()
		{
			StringBuilder descriptionBuilder = new StringBuilder();
			descriptionBuilder.append("Skip ").append(this.floorsBetweenTests).append(" between each test.");
			TestStrategy priorStrategy = this;
			TestStrategy currentRemainderStrategy = this.remainderStrategy;
			while (currentRemainderStrategy != null)
			{
				descriptionBuilder.append(" If that succeeds for ")
						.append(priorStrategy.numTestsBeforeRemainder)
						.append(" iterations,")
						.append(" skip ")
						.append(currentRemainderStrategy.floorsBetweenTests)
						.append(" between each additional test.");
				priorStrategy = currentRemainderStrategy;
				currentRemainderStrategy = currentRemainderStrategy.remainderStrategy;
			}
			descriptionBuilder.append(" This provides a worst case of: ").append(this.calculateWorstCase())
					.append(" tests and an expected value of: ")
					.append(this.calculateExpectedValue())
					.append(" tests.");
			return descriptionBuilder.toString();
		}
	}

}
