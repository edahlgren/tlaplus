/*******************************************************************************
 * Copyright (c) 2018 Microsoft Research. All rights reserved. 
 *
 * The MIT License (MIT)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *   Markus Alexander Kuppe - initial API and implementation
 ******************************************************************************/
package tla2sany.st;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

import org.junit.Test;

public class LocationTest {
	
	@Test
	public void testComparator() {
		final Location[] parsedLocations = Location.getParsedLocations(
				  "line 15, col 9 to line 15, col 9 of module CostMetrics\n"
				+ "line 15, col 9 to line 15, col 17 of module CostMetrics\n"
				+ "line 8, col 11 to line 8, col 11 of module CostMetrics\n"
				+ "line 8, col 13 to line 8, col 13 of module CostMetrics\n"
				+ "line 8, col 9 to line 8, col 15 of module CostMetrics\n"
				+ "line 14, col 15 to line 14, col 17 of module CostMetrics\n"
				+ "line 15, col 15 to line 15, col 17 of module CostMetrics\n"
				+ "line 16, col 34 to line 16, col 52 of module CostMetrics\n"
				+ "line 8, col 9 to line 8, col 15 of module CostMetrics\n"
				+ "line 16, col 42 to line 16, col 51 of module CostMetrics\n"
				+ "line 16, col 42 to line 16, col 50 of module CostMetrics\n"
				+ "line 16, col 46 to line 16, col 46 of module CostMetrics\n"
				+ "line 16, col 46 to line 16, col 50 of module CostMetrics\n"
				+ "line 16, col 46 to line 16, col 50 of module CostMetrics\n"
				+ "line 23, col 6 to line 25, col 18 of module CostMetrics\n"
				+ "line 18, col 9 to line 18, col 9 of module CostMetrics");
		assertEquals(16, parsedLocations.length);
		
		final TreeSet<Location> locations = new TreeSet<>(Arrays.asList(parsedLocations));
		assertEquals(14, locations.size());
		
		final Iterator<Location> iterator = locations.iterator();
		Location l = iterator.next();
		for (int i = 1; i < locations.size(); i++) {
			final Location next = iterator.next();
			assertTrue(l.bLine <= next.bLine);
			if (l.bLine == next.bLine) {
				assertTrue(l.bColumn <= next.bColumn);
				if (l.bColumn == next.bColumn) {
					assertTrue(l.eLine <= next.eLine);
					if (l.eLine == next.eLine) {
						assertTrue(l.eColumn < next.eColumn);
					}
				}
			}
			l = next;
		}
	}
}
