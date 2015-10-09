
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commatize
{
	public static void main(String[] args)
	{
		if (parseHelp(args))
			return;

		String number = parseNumber(args);
		String numberWithComma = commaInduce(number);
		if (numberWithComma != null)
			System.out.println(numberWithComma);
		else
			System.out.println("Input not recognized as number");
	}

	static String commaInduce(String number)
	{
		Pattern numberPattern = Pattern.compile("^\\s*([-+]?)(\\d*)(\\.\\d*)?([iIfFdDlLsS]?)\\s*$");
		Matcher numberMatcher = numberPattern.matcher(number);
		if (numberMatcher.matches())
		{
			String integerComponent = numberMatcher.group(2);
			String commaInducedString = commatizeKnownInteger(integerComponent);
			String optionalSign = numberMatcher.group(1);
			String decimalPortion = numberMatcher.group(3);
			if (decimalPortion == null)
				decimalPortion = "";
			String optionalPrimitiveQualifier = numberMatcher.group(4);

			return optionalSign + commaInducedString + decimalPortion + optionalPrimitiveQualifier;
		}
		else return null;
	}

	private static String commatizeKnownInteger(String integerComponent)
	{
		StringBuilder commaInducedIntegerBuilder = new StringBuilder();
		String reversedInteger = new StringBuilder(integerComponent).reverse().toString();
		for (int i = 0; i < reversedInteger.length(); i++)
		{
			if (i != 0 && i % 3 == 0)
				commaInducedIntegerBuilder.append(",");
			commaInducedIntegerBuilder.append(reversedInteger.charAt(i));
		}
		String commaInducedString = commaInducedIntegerBuilder.reverse().toString();
		return commaInducedString;
	}

	private static String parseNumber(String[] args)
	{
		if (args.length == 0)
			throw new IllegalArgumentException("Number required (positional arguement 1)");
		else return args[0];
	}

	private static boolean parseHelp(String[] args)
	{
		for (String arg : args)
		{
			if (arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-h"))
			{
				System.out.println("\nThis program takes a number and outputs a string with commas.\n"
										   + " positional arguments:\n"
										   + "1.           the number to comma induce\n"
										   + "\n"
										   + "optional arguments:\n"
										   + " -h, --help  show this help message and exit\n");
				return true;
			}
		}
		return false;
	}
}
