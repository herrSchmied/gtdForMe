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

		Exception exception = assertThrows(WeekDataException.class, 
		()->
		{
		    Set<String> activeProjects = new HashSet<>();
		    activeProjects.add("P1");
		    activeProjects.add("P2");
		    activeProjects.add("P3");
		    wd.setProjectsActive(activeProjects);
		    wd.setProjectsActive(activeProjects);
		});
		
	    String expectedMessage = WeekData.alreadySetExceptionMsg;
	    String actualMessage = exception.getMessage();
	    assert(actualMessage.equals(expectedMessage));
	    
		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));

		WeekData wd2 = new WeekData(ldMonday, 0);

	    exception = assertThrows(WeekDataException.class,
	    () ->
	    {
	    	Set<String> activeProjects = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
	    	wd2.setProjectsActive(activeProjects);
	    });

	    expectedMessage = WeekData.cantConcludeExceptionMsg;
	    actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));
	}

	@Test
	public void testNewProjectsWrittenDown() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);

		Exception exception = assertThrows(WeekDataException.class, 
		()->
		{
		    Set<String> newProjectsWrittenDown = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
		    wd.setProjectsWrittenDown(newProjectsWrittenDown);
		    wd.setProjectsWrittenDown(newProjectsWrittenDown);
		});
		
	    String expectedMessage = WeekData.alreadySetExceptionMsg;
	    String actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));

		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));

		WeekData wd2 = new WeekData(ldMonday, 0);

	    exception = assertThrows(WeekDataException.class,
	    () ->
	    {
	    	Set<String> newProjectsWrittenDown = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
	    	wd2.setProjectsWrittenDown(newProjectsWrittenDown);
	    });

	    expectedMessage = WeekData.cantConcludeExceptionMsg;
	    actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));
	}

	@Test
	public void testProjectsBorn() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);

		Exception exception = assertThrows(WeekDataException.class, 
		()->
		{
		    Set<String> projectsBorn = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
		    wd.setProjectsBorn(projectsBorn);
		    wd.setProjectsBorn(projectsBorn);
		});
		
	    String expectedMessage = WeekData.alreadySetExceptionMsg;
	    String actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));

		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));

		WeekData wd2 = new WeekData(ldMonday, 0);

	    exception = assertThrows(WeekDataException.class,
	    () ->
	    {
	    	Set<String> projectsBorn = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
	    	wd2.setProjectsBorn(projectsBorn);
	    });

	    expectedMessage = WeekData.cantConcludeExceptionMsg;
	    actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));
	}

	@Test
	public void testProjectsTerminated() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);

		Exception exception = assertThrows(WeekDataException.class, 
		()->
		{
		    Set<String> projectsTerminated = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
		    wd.setProjectsTerminated(projectsTerminated);
		    wd.setProjectsTerminated(projectsTerminated);
		});
		
	    String expectedMessage = WeekData.alreadySetExceptionMsg;
	    String actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));

		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));

		WeekData wd2 = new WeekData(ldMonday, 0);

	    exception = assertThrows(WeekDataException.class,
	    () ->
	    {
	    	Set<String> projectsTerminated = new HashSet<>(Arrays.asList("P1", "P2", "P3"));
	    	wd2.setProjectsTerminated(projectsTerminated);
	    });

	    expectedMessage = WeekData.cantConcludeExceptionMsg;
	    actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));
	}

	@Test
	public void testStepNr() throws WeekDataException
	{

		LocalDate ld = LocalDate.now();
		DayOfWeek dow = ld.getDayOfWeek();
		LocalDate ldMonday = ld.minusDays(dow.getValue()+6);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));
		
		WeekData wd = new WeekData(ldMonday, 0);

		Exception exception = assertThrows(WeekDataException.class, 
		()->
		{
		    int stepNr = 1;
		    wd.setHowManyStepsDone(stepNr);
		    wd.setHowManyStepsDone(stepNr);
		});
		
	    String expectedMessage = WeekData.alreadySetExceptionMsg;
	    String actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));

		ldMonday = ld.minusDays(dow.getValue()-1);
		assert(ldMonday.getDayOfWeek().equals(DayOfWeek.MONDAY));

		WeekData wd2 = new WeekData(ldMonday, 0);

	    exception = assertThrows(WeekDataException.class,
	    () ->
	    {
	    	int stepNr = 1;
	    	wd2.setHowManyStepsDone(stepNr);
	    });

	    expectedMessage = WeekData.cantConcludeExceptionMsg;
	    actualMessage = exception.getMessage();

	    assert(actualMessage.equals(expectedMessage));
	}
}