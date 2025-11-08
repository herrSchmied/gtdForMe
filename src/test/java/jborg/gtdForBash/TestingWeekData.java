package jborg.gtdForBash;



import org.junit.jupiter.api.Test;



import java.time.DayOfWeek;
import java.time.LocalDate;


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
}