package jborg.gtdForBash;


import static jborg.gtdForBash.SequenzesForISS.addNotePrjctName;
import static jborg.gtdForBash.SequenzesForISS.appendStpPrjctName;
import static jborg.gtdForBash.SequenzesForISS.killPrjctName;
import static jborg.gtdForBash.SequenzesForISS.killPrjctNameNoDLDT;
import static jborg.gtdForBash.SequenzesForISS.killStepPrjctName;
import static jborg.gtdForBash.SequenzesForISS.modPrjctName;
import static jborg.gtdForBash.SequenzesForISS.newPrjctName;
import static jborg.gtdForBash.SequenzesForISS.newPrjctNoDLDT;
import static jborg.gtdForBash.SequenzesForISS.sequenzAddNote;
import static jborg.gtdForBash.SequenzesForISS.sequenzKillProject;
import static jborg.gtdForBash.SequenzesForISS.sequenzKillStep;
import static jborg.gtdForBash.SequenzesForISS.sequenzMODProject;
import static jborg.gtdForBash.SequenzesForISS.sequenzNXTStep;
import static jborg.gtdForBash.SequenzesForISS.sequenzNewProject;
import static jborg.gtdForBash.SequenzesForISS.sequenzNewProjectNoDLDT;
import static jborg.gtdForBash.SequenzesForISS.sequenzWakeMODProject;
import static jborg.gtdForBash.SequenzesForISS.wakeProjectName;

import java.time.LocalDateTime;
import java.time.Month;

import consoleTools.InputStreamSession;

public class SequenzesForISS
{
	
	//Remember: the '\n' are gone!!!
	public static final String wakeProjectName = "Wakeup_MOD_Project";

	public static final String terminatePrjctName = "Terminate_Project";
	
	public static final String addNotePrjctName = "Add_Note_Project";
	
	public static final String appendStpPrjctName = "Append_Step_Project";
	
	public static final  String killStepPrjctName = "Kill_Step_Project";
	
	public static final  String killPrjctNameNoDLDT = "Kill_Project_NODLDT";
	public static final  String killPrjctName = "Kill_Project";
	
	public static final  String modPrjctName = "MOD_Project";
	public static final  String modPrjctGoal = "MOD-Project Test";
	
	public static final  String newPrjctName = "Project_Nuovo";
	public static final  String newPrjctGoal = "Testing this here";
	
	public static final  String newPrjctNoDLDT = "No_DLDT_Project";
	
	public static final  String stepDesc = "Hello Bello GoodBye!";
	public static final  String stepDesc2 = "Grrrl";
	public static final  String stepDesc3 = "Bla bla";
	
	public static final  String noticeOne = "Note1";
	public static final  String noticeTwo = "Note2";
	
	static final  LocalDateTime jetzt = LocalDateTime.now();
	static final  LocalDateTime prjctDLDT = jetzt.plusHours(1);
	static final LocalDateTime stepDLDT = jetzt.plusMinutes(30);

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
	public static String sequenzNewProjectNoDLDT(String prjctName)
	{
		
		String changePrjctBDT = "No";
		String dldtQuestion = "no";
		String changeStepBDT = "No";
		String chosenFromStatieList = "2";//ATBD//TODO: make it bullet proof. it works for now.
		
		String data = SomeCommands.new_Project + '\n'
				+ prjctName + '\n'
				+ newPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
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

	public static String sequenzKillProject(String prjctName)
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

	public static String sequenzManyProjects()
	{

		String data = sequenzNewProject(newPrjctName)
				+ sequenzMODProject(wakeProjectName)
				+ sequenzWakeMODProject(wakeProjectName)
				+ sequenzNewProject(addNotePrjctName)
				+ sequenzAddNote(addNotePrjctName)
				+ sequenzNewProjectNoDLDT(killPrjctNameNoDLDT)
				+ sequenzKillStep(killPrjctNameNoDLDT)
				+ sequenzKillProject(killPrjctNameNoDLDT)
				+ sequenzNewProject(killPrjctName)
				+ sequenzKillStep(killPrjctName)
				+ sequenzKillProject(killPrjctName)
				+ sequenzNewProject(killStepPrjctName)
				+ sequenzKillStep(killStepPrjctName)
				+ sequenzNewProject(appendStpPrjctName)
				+ sequenzKillStep(appendStpPrjctName)
				+ sequenzNXTStep(appendStpPrjctName)
				+ sequenzNewProjectNoDLDT(newPrjctNoDLDT)
				+ sequenzMODProject(modPrjctName)
				+ SomeCommands.exit + '\n';

		return data;
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