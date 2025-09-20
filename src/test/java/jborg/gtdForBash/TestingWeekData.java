package jborg.gtdForBash;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
	public void testActiveProjects() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);

		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
	}

	@Test
	public void testNewProjectsWrittenDown() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);

		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
	}

	@Test
	public void testProjectsBorn() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);


		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
	}

	@Test
	public void testProjectsTerminated() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);


		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
	}

	@Test
	public void testStepNr() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);
		
		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
	}
}