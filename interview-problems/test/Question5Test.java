/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Chad
 */
public class Question5Test
{

	public Question5Test()
	{
	}

	/**
	 * Test of fastCountCumulativeDigits method, of class CountDigits.
	 */
	@Test
	public void testCountCumulativeDigits()
	{
		Map<Integer, Integer> fastCountCumulativeDigits = CountDigits.fastCountCumulativeDigits(1l);
		assertMapState(fastCountCumulativeDigits, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);

		fastCountCumulativeDigits = CountDigits.fastCountCumulativeDigits(12l);
		assertMapState(fastCountCumulativeDigits, 1, 5, 2, 1, 1, 1, 1, 1, 1, 1);
		
		fastCountCumulativeDigits = CountDigits.fastCountCumulativeDigits(20l);
		assertMapState(fastCountCumulativeDigits, 2, 12, 3, 2, 2, 2, 2, 2, 2, 2);
	}

	/**
	 * Test of fastCountCumulativeDigits method, of class CountDigits.
	 */
	@Test
	public void testCountCumulativeDigitsCompare()
	{
		for (int i = 10; i < 10000000; i *= 10)
		{
			double sample = i * Math.random();
			Map<Integer, Integer> slowCountCumulativeDigits = CountDigits.slowCountCumulativeDigits((long) sample);
			Map<Integer, Integer> fastCountCumulativeDigits = CountDigits.fastCountCumulativeDigits((long) sample);
			assertEquals("both maps should contain all digits", slowCountCumulativeDigits.size(), fastCountCumulativeDigits.size());
			for (Map.Entry<Integer, Integer> entry : slowCountCumulativeDigits.entrySet())
			{
				Integer fastSolution = fastCountCumulativeDigits.get(entry.getKey());
				Integer slowSolution = entry.getValue();
				assertEquals("Slow and fast alogirthm should return same answer for digit: " + entry.getKey(), slowSolution, fastSolution);
			}
		}
	}

	/**
	 * Test of breakIntoDigits method, of class CountDigits.
	 */
	@Test
	public void testBreakIntoDigits()
	{
		List<Integer> expResult = Arrays.asList(0);
		List<Integer> result = CountDigits.breakIntoDigits(0L);
		assertEquals(expResult, result);

		expResult = Arrays.asList(1, 4, 4, 4, 3, 6, 0, 5, 4, 9, 9, 7, 3);
		result = CountDigits.breakIntoDigits(1444360549973L);
		assertEquals(expResult, result);
	}

	private void assertMapState(Map<Integer, Integer> fastCumulativeDigits, int count0, int count1, int count2, int count3, int count4, int count5, int count6, int count7, int count8, int count9)
	{
		assertEquals("Unexpected value for digit " + 0, (long) count0, (long) fastCumulativeDigits.get(0));
		assertEquals("Unexpected value for digit " + 1, (long) count1, (long) fastCumulativeDigits.get(1));
		assertEquals("Unexpected value for digit " + 2, (long) count2, (long) fastCumulativeDigits.get(2));
		assertEquals("Unexpected value for digit " + 3, (long) count3, (long) fastCumulativeDigits.get(3));
		assertEquals("Unexpected value for digit " + 4, (long) count4, (long) fastCumulativeDigits.get(4));
		assertEquals("Unexpected value for digit " + 5, (long) count5, (long) fastCumulativeDigits.get(5));
		assertEquals("Unexpected value for digit " + 6, (long) count6, (long) fastCumulativeDigits.get(6));
		assertEquals("Unexpected value for digit " + 7, (long) count7, (long) fastCumulativeDigits.get(7));
		assertEquals("Unexpected value for digit " + 8, (long) count8, (long) fastCumulativeDigits.get(8));
	}
}
