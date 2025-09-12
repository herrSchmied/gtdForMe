package jborg.gtdForBash;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


import jborg.gtdForBash.exceptions.WeekDataException;




public class TestingWeekData
{

	@Test
	public void test() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()-1);
		
		WeekData wd = new WeekData(ldMonday, 0);
		
		LocalDate ldSunday = ldMonday.plusDays(6);
		
		assert(ldSunday.equals(wd.getEnd()));
		assert(ldSunday.getDayOfWeek().equals(DayOfWeek.SUNDAY));
	}
	
	@Test
	public void test2() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);

		Exception exception = assertThrows(WeekDataException.class, 
		()->
		{
		    Set<String> activeProjects = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
		    wd.setProjectsActive(activeProjects);
		    wd.setProjectsActive(activeProjects);
		});
		
	    String expectedMessage = WeekData.alreadySetExceptionMsg;
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.equals(expectedMessage));
	}

	@Test
	public void exceptionTest() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));

		WeekData wd = new WeekData(ldMonday, 0);

	    Exception exception = assertThrows(WeekDataException.class,
	    () ->
	    {
	    	Set<String> activeProjects = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
	    	wd.setProjectsActive(activeProjects);
	    });

	    String expectedMessage = WeekData.cantConcludeExceptionMsg;
	    String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.equals(expectedMessage));
	}
}