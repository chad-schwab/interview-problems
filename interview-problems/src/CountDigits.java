
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * How many times would each digit (0-9) show up if you counted from one to the current Unix epoch? Write a program that will give the answers to the above. It
 * should take in an optional timestamp. If one is given, use it to do the above calculation. If none is given, assume local time.
 *
 *
 * Ex: If you counted from 1 to 20 the digit 1 shows up 12 times, the digit 2 shows up 3 times and the digit 3 shows up 2 times.
 *
 * @author Chad
 */
public class CountDigits
{
	public static void main(String[] args)
	{
		if (parseHelp(args))
			return;

		Long timestamp = parseTimestampOrDefault(args);
		boolean useSlow = parseSlowOption(args);
		if (timestamp != null)
		{
			Map<Integer, Integer> countMap = useSlow ? slowCountCumulativeDigits(timestamp) : fastCountCumulativeDigits(timestamp);
			countMap.forEach((k, v) -> System.out.println(k + " - " + v));
		}
	}

	static Map<Integer, Integer> fastCountCumulativeDigits(Long timestamp)
	{
		if (timestamp < 1)
			throw new IllegalArgumentException("timestamp should be a positive integer greater than zero");
		List<Integer> digits = breakIntoDigits(timestamp);
		List<NumberPattern> patterns = deriveNumberPatterns(digits);
		HashMap<Integer, Integer> countMap = new HashMap<>();
		for (int i = 0; i < 10; i++)
		{
			int digitCount = 0;
			for (NumberPattern pattern : patterns)
				digitCount += pattern.countOccurances(i);
			countMap.put(i, digitCount);
		}
		//the number 0 is always counted, but we're counting from one.
		countMap.merge(0, -1, Integer::sum);
		return countMap;
	}

	private static List<NumberPattern> deriveNumberPatterns(List<Integer> digits)
	{
		if (digits.isEmpty())
			return Collections.EMPTY_LIST;
		List<NumberPattern> spanningPatterns = new LinkedList<>();
		//include all patterns for numbers with less digits than timestamp. ex 315 -> [1-9][0-9], [1-9]
		for (int i = 1; i < digits.size(); i++)
			spanningPatterns.add(NumberPattern.allIncluded(digits.size() - i, false));
		spanningPatterns.addAll(findPatternsFromLead(digits, false));
		return spanningPatterns;
	}

	private static List<NumberPattern> findPatternsFromLead(List<Integer> remaining, boolean allowLeadingZeros)
	{
		int leadDigit = remaining.get(0);
		if (remaining.size() == 1)
			return Collections.singletonList(NumberPattern.allIncluded(leadDigit, 1, allowLeadingZeros));
		else
		{
			ArrayList<NumberPattern> subPatterns = new ArrayList<>();
			//include all patterns for leadingDigit > digit > 0. ex 315->[1-2][0-9][0-9]
			if (allowLeadingZeros && leadDigit > 0 || !allowLeadingZeros && leadDigit > 1)
				subPatterns.add(NumberPattern.allIncluded(leadDigit - 1, remaining.size(), allowLeadingZeros));
			NumberPattern leadPattern = new NumberPattern(new DigitPattern(leadDigit));
			subPatterns.addAll(
					findPatternsFromLead(remaining.subList(1, remaining.size()), true)
					.stream()
					.map(e -> leadPattern.concat(e))
					.collect(Collectors.toList()));
			return subPatterns;
		}
	}

	static Map<Integer, Integer> slowCountCumulativeDigits(Long timestamp)
	{
		if (timestamp < 1)
			throw new IllegalArgumentException("timestamp should be a positive integer greater than zero");
		ConcurrentHashMap<Integer, Integer> digitCounts = new ConcurrentHashMap<>(10);
		LongStream.range(1, timestamp + 1).parallel().forEach(e ->
		{
			Map<Integer, Integer> newCounts = countDigits(e);
			for (Map.Entry<Integer, Integer> entry : newCounts.entrySet())
				digitCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
		});
		for (int i = 0; i < 10; i++)
			digitCounts.merge(i, 0, Integer::sum);
		return digitCounts;
	}

	private static Map<Integer, Integer> countDigits(long num)
	{
		return breakIntoDigits(num)
				.stream()
				.collect(Collectors.toMap(i -> i, e -> 1, Integer::sum));
	}

	public static List<Integer> breakIntoDigits(long num)
	{
		List<Integer> reverseDigits = new ArrayList<>();
		do
		{
			long digit = num % 10;
			reverseDigits.add((int) digit);
			num /= 10;
		}
		while (num > 0);

		List<Integer> digits = new ArrayList<>(reverseDigits.size());
		for (int i = reverseDigits.size() - 1; i >= 0; i--)
			digits.add(reverseDigits.get(i));
		return digits;
	}

	private static boolean parseHelp(String[] args)
	{
		for (String arg : args)
		{
			if (arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-h"))
			{
				System.out.println("\nThis program takes an optional timestamp and outputs the number of times a digit appears appears if we were to count up to that number by one from zero.\n"
								   + "\n"
										   + "optional arguments:\n"
										   + " -h, --help  show this help message and exit\n"
										   + " -t, --timestamp  the timestamp you wish to count up to. defaults to current time\n"
										   + " -s, --slow  use the slower method for calculating");
				return true;
			}
		}
		return false;
	}

	private static Long parseTimestampOrDefault(String[] args)
	{
		boolean tFound = false;
		for (String arg : args)
		{
			if (tFound)
			{
				try
				{
					return Long.parseLong(arg);
				}
				catch (Exception e)
				{
					System.out.println("Unknown long");
					return null;
				}
			}

			if (arg.equalsIgnoreCase("--timestamp") || arg.equalsIgnoreCase("-t"))
				tFound = true;
		}
		return new Date().getTime();
	}

	private static boolean parseSlowOption(String[] args)
	{
		for (String arg : args)
		{
			if (arg.equalsIgnoreCase("--s") || arg.equalsIgnoreCase("-s"))
				return true;
		}
		return false;
	}

	private static class NumberPattern
	{
		private static final DigitPattern ALL_INCLUDED = new DigitPattern(0, 9);
		private final List<DigitPattern> includedDigits;

		public NumberPattern(DigitPattern includedDigit)
		{
			this(Collections.singletonList(includedDigit));
		}

		public NumberPattern(List<DigitPattern> includedDigits)
		{
			this.includedDigits = includedDigits;
		}

		public int countOccurances(int digit)
		{
			int occuranceCount = 0;
			for (int i = 0; i < includedDigits.size(); i++)
			{
				DigitPattern currentGroup = includedDigits.get(i);
				if (currentGroup.contains(digit))
				{
					int groupCount = 1;
					for (int j = 0; j < includedDigits.size(); j++)
					{
						if (i == j)
							continue;
						groupCount *= includedDigits.get(j).getNumPossibleDigits();
					}
					occuranceCount += groupCount;
				}
			}
			return occuranceCount;
		}

		public NumberPattern concat(NumberPattern pattern)
		{
			List<DigitPattern> newPatterns = new ArrayList<>(includedDigits.size() + pattern.includedDigits.size());
			newPatterns.addAll(includedDigits);
			newPatterns.addAll(pattern.includedDigits);
			return new NumberPattern(newPatterns);
		}

		public static NumberPattern allIncluded(int leadingDigit, int totalNumberDigits, boolean allowLeadingZero)
		{
			List<DigitPattern> includedNumbers = new ArrayList<>(totalNumberDigits);
			if (totalNumberDigits == 1 || allowLeadingZero)
				includedNumbers.add(new DigitPattern(0, leadingDigit)); //include 0 for the last digit
			else
				includedNumbers.add(new DigitPattern(1, leadingDigit));
			for (int i = 0; i < totalNumberDigits - 1; i++)
				includedNumbers.add(ALL_INCLUDED);
			return new NumberPattern(includedNumbers);
		}

		public static NumberPattern allIncluded(int totalNumberDigits, boolean allowLeadingZero)
		{
			return allIncluded(9, totalNumberDigits, allowLeadingZero);
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			for (DigitPattern includedDigit : includedDigits)
				builder.append("[").append(includedDigit).append("]");
			return builder.toString();
		}
	}

	private static class DigitPattern
	{
		private final int start;
		private final int end;

		public DigitPattern(int solo)
		{
			this(solo, solo);
		}

		public DigitPattern(int start, int end)
		{
			if (start < 0 || start > end || end > 9)
				throw new IllegalArgumentException("Digit Pattern only supports range from 0 to 9. " + start + "-" + end + " not supported");
			this.start = start;
			this.end = end;
		}

		public int getNumPossibleDigits()
		{
			return end - start + 1;
		}

		private boolean contains(int digit)
		{
			return digit >= start && digit <= end;
		}

		@Override
		public String toString()
		{
			if (start == end)
				return Integer.toString(start);
			else return start + "-" + end;
		}

	}
}
