/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class Question3Test
{
	@Test
	public void testCommaInduce()
	{
		for (String prefix : Arrays.asList("-", "+", ""))
		{
			for (String postfix : Arrays.asList("", "f", "i", "l", "d", "s", "F", "I", "L", "D", "S"))
			{
				assertEquals(prefix + "0" + postfix, Commatize.commaInduce(prefix + "0" + postfix));
				assertEquals(prefix + "1" + postfix, Commatize.commaInduce(prefix + "1" + postfix));
				assertEquals(prefix + "999" + postfix, Commatize.commaInduce(prefix + "999" + postfix));
				assertEquals(prefix + "1,000" + postfix, Commatize.commaInduce(prefix + "1000" + postfix));
				assertEquals(prefix + "10,000,000" + postfix, Commatize.commaInduce(prefix + "10000000" + postfix));
				assertEquals(prefix + "999,999,999,999" + postfix, Commatize.commaInduce(prefix + "999999999999" + postfix));
			}
		}
	}

	public void testCommaInduce_null_NaN()
	{
		assertNull(Commatize.commaInduce("1,000"));
	}
}
