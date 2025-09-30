package jborg.gtdForBash;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;


import consoleTools.InputStreamSession;

public class SequenzesForISS
{
	
	//Remember: the '\n' are gone!!!
	public static final String wakeProjectName = "Wakeup_MOD_Project";

	public static final String terminatePrjctName = "Terminate_Project";
	
	
	public static final  String killPrjctNameNoDLDT = "Kill_Project_NODLDT";
	
	public static final  String modPrjctName = "MOD_Project";
	public static final  String modPrjctGoal = "MOD-Project Test";
	
	public static final  String newPrjctGoal = "Testing this here";
	
	public static final  String newPrjctNoDLDT = "No_DLDT_Project";
	
	public static final  String stepDesc = "Hello Bello GoodBye!";
	public static final  String stepDesc2 = "Grrrl";
	public static final  String stepDesc3 = "Bla bla";
	
	public static final  String noticeOne = "Note1";
	public static final  String noticeTwo = "Note2";
	
	static final LocalDateTime jetzt = LocalDateTime.now();
	static final LocalDateTime oldestBDT = getBDT(1);
	static final LocalDateTime prjctDLDT = jetzt.plusDays(2);
	static final LocalDateTime stepDLDT = jetzt.plusDays(1);

	public static String sequenzNXTStep(String prjctName)
	{

		String changeStepBDT = "No";
		String chosenFromStatieList = "1";//ATBD
		String dldtQuestion = "yes";
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);

		String data = SomeCommands.next_Step + " " + prjctName + '\n'
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc2 + '\n'
				+ dldtQuestion + '\n'
				+ stepDLDTStr;
		
		return data;
	}
	
	public static String sequenzKillStep(String prjctName)
	{
	
		String stepWasSuccessQstn  = "No";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String data = SomeCommands.terminate_Step + " " + prjctName + '\n'
					+ stepWasSuccessQstn + '\n'
					+ wantToMakeTDTNote + '\n'
					+ wantToChangeTDT + '\n';
		
		return data;
	}

	public static String sequenzNewProject(String prjctName)
	{
		
		String changePrjctBDT = "No";
		String dldtQuestion = "yes";
		String prjctDLDTStr = translateTimeToAnswerString(prjctDLDT);
		String changeStepBDT = "No";
		String chosenFromStatieList = "2";//ATBD//TODO: make it bullet proof. it works for now.
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);
		
		String data = SomeCommands.new_Project + '\n'
				+ prjctName + '\n'
				+ newPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
				+ dldtQuestion + '\n'
				+ prjctDLDTStr
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc + '\n'
				+ dldtQuestion + '\n'
				+ stepDLDTStr;
				
		return data;
	}

	public static String sequenzNewProjectCustomBDT(String prjctName, LocalDateTime bdt)
	{
		
		String bdtStr = translateTimeToAnswerString(bdt);
		String changePrjctBDT = "Yes";
		String dldtQuestion = "yes";
		String prjctDLDTStr = translateTimeToAnswerString(prjctDLDT);
		String changeStepBDT = "No";
		String chosenFromStatieList = "2";//ATBD//TODO: make it bullet proof. it works for now.
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);
		
		String data = SomeCommands.new_Project + '\n'
				+ prjctName + '\n'
				+ newPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
				+ bdtStr + '\n'
				+ dldtQuestion + '\n'
				+ prjctDLDTStr
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc + '\n'
				+ dldtQuestion + '\n'
				+ stepDLDTStr;
				
		return data;
	}
	public static String sequenzNewProjectNoDLDTCustomBDT(String prjctName, LocalDateTime bdt)
	{
		
		String changePrjctBDT = "Yes";
		String bdtStr = translateTimeToAnswerString(bdt);
		String dldtQuestion = "no";
		String changeStepBDT = "No";
		String chosenFromStatieList = "2";//ATBD//TODO: make it bullet proof. it works for now.
		
		String data = SomeCommands.new_Project + '\n'
				+ prjctName + '\n'
				+ newPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
				+ bdtStr + '\n'
				+ dldtQuestion + '\n'
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc + '\n'
				+ dldtQuestion + '\n';
				
		return data;
	}

	public static String sequenzMODProject(String prjctName)
	{
		
		String changePrjctBDT = "No";
		
		String data = SomeCommands.new_MOD + '\n'
				+ prjctName + '\n'
				+ modPrjctGoal + '\n'
				+ changePrjctBDT + '\n';
				
		return data;
	}
	
	public static String sequenzMODProjectCustomBDT(String prjctName, LocalDateTime bdt)
	{
		
		String changePrjctBDT = "Yes";
		String bdtStr = translateTimeToAnswerString(bdt);
		
		String data = SomeCommands.new_MOD + '\n'
				+ prjctName + '\n'
				+ modPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
				+ bdtStr + '\n';
				
		return data;
	}

	public static String sequenzAddNote(String prjctName)
	{

		String data = SomeCommands.add_Note + " " + prjctName + '\n'
				+ noticeOne + "\n"
				+ SomeCommands.add_Note + " " + prjctName + '\n'
				+ noticeTwo + "\n";
				
		return data;
	}
	
	public static String sequenzWakeMODProject(String prjctName)
	{

		String prjctDLDTStr = translateTimeToAnswerString(prjctDLDT);
		String changeStepBDT = "No";
		String chosenFromStatieList = "1";
		String dldtQuestion = "yes";
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);

		String data = SomeCommands.wake_MOD + prjctName + '\n'
					+ dldtQuestion + '\n'
					+ prjctDLDTStr
					+ changeStepBDT + '\n'
					+ chosenFromStatieList + '\n'
					+ stepDesc3 + '\n'
					+ dldtQuestion + '\n'
					+ stepDLDTStr;

		return data;
	}

	public static String sequenzProjectSucceeds(String prjctName)
	{

		String projectWasSuccessQstn  = "Yes";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String data = SomeCommands.terminate_Project + " " + prjctName + '\n'
				+ projectWasSuccessQstn + '\n'
				+ wantToMakeTDTNote + '\n'
				+ wantToChangeTDT + '\n';

		return data;
	}

	public static String sequenzProjectFails(String prjctName)
	{

		String projectWasSuccessQstn  = "No";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String data = SomeCommands.terminate_Project + " " + prjctName + '\n'
				+ projectWasSuccessQstn + '\n'
				+ wantToMakeTDTNote + '\n'
				+ wantToChangeTDT + '\n';

		return data;
	}

	public static String sequenzOfFourNewProjects()
	{
		int s = 4;
		String data = "";
		
		//starts and ends not like usually.
		for(int m=1;m<s+1;m++)
		{
			
			LocalDateTime bdt = getBDT(m);
			String name = getNewProjectName(m);
			data = data + sequenzNewProjectCustomBDT(name, bdt);
		}
		
		return data;
	}
	
	public static LocalDateTime getBDT(int n)
	{
		LocalDate mLDT = StatisticalTools.getLastMonday(jetzt);
		
		return LocalDateTime.of(mLDT, LocalTime.of(0, n)).minusDays(14);
	}

	public static String sequenzManyProjects()
	{

		
		String data = sequenzOfFourNewProjects()
				+ sequenzAddNote(getNewProjectName(2))
				+ sequenzKillStep(getNewProjectName(3))
				+ sequenzProjectFails(getNewProjectName(3))
				+ sequenzKillStep(getNewProjectName(4))
				+ sequenzNXTStep(getNewProjectName(4))
				+ sequenzNewProjectNoDLDTCustomBDT(killPrjctNameNoDLDT, getBDT(5))
				+ sequenzKillStep(killPrjctNameNoDLDT)
				+ sequenzProjectSucceeds(killPrjctNameNoDLDT)
				+ sequenzNewProjectNoDLDTCustomBDT(newPrjctNoDLDT, getBDT(6))
				+ sequenzMODProjectCustomBDT(modPrjctName, getBDT(7))
				+ sequenzMODProjectCustomBDT(wakeProjectName, getBDT(8))
				+ sequenzWakeMODProject(wakeProjectName)
				+ SomeCommands.exit + '\n';

		return data;
	}
	
	public static String getNewProjectName(int n)
	{
		return "New_Project_"+n;
	}
	
	public static String translateTimeToAnswerString(LocalDateTime ldt)
	{
		int day = ldt.getDayOfMonth();
		String dayStr = "" + day;
		if(day<10) dayStr = "0" + day;
		
		int month = ldt.getMonthValue();
		Month m = Month.of(month);
		String monthStr = "";
		for(String s: InputStreamSession.monthMap.keySet())
		{
			Month d = InputStreamSession.monthMap.get(s);
			if(m.equals(d))
			{
				monthStr = s;
				break;
			}
		}

		int hour = ldt.getHour();
		String hourStr = ""+hour;
		if(hour<10) hourStr = "0" + hour;
		
		int year = ldt.getYear();
		String yearStr = year+"";
		if(yearStr.length()==3)yearStr = "0" + yearStr;
		if(yearStr.length()==2)yearStr = "00" + yearStr;
		if(yearStr.length()==1)yearStr = "000" + yearStr;

		int minute = ldt.getMinute();
		String minStr = "" + minute;
		if(minute<10) minStr = "0" + minute;
		
		return dayStr + monthStr + year + "T" + hourStr + ":" + minStr +"\n";
	}
}