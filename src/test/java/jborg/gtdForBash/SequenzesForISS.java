package jborg.gtdForBash;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import someMath.NaturalNumberException;

import static consoleTools.InputStreamSession.*;

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

	public SequenzesForISS()
	{

	}
	
	public String [] sequenzNXTStep(String prjctName) throws NaturalNumberException
	{

		LocalDateTime stepDLDT = GTDCLI.now().plusDays(7);

		String chosenFromStatieList = "1";//ATBD
		String dldtQuestion = "yes";
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);

		String[] data = new String[5];
		data[0] = SomeCommands.next_Step + " " + prjctName;
		data[1] = chosenFromStatieList;
		data[2] = stepDesc2;
		data[3] = dldtQuestion;
		data[4] = stepDLDTStr;
		
		return data;
	}
	
	public String [] sequenzKillStep(String prjctName)
	{
	
		String stepWasSuccessQstn  = "No";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String[] data = new String[4]; 
		data[0] = SomeCommands.terminate_Step + " " + prjctName;
		data[1] = stepWasSuccessQstn;
		data[2] = wantToMakeTDTNote;
		data[3] = wantToChangeTDT;
		
		return data;
	}

	public String[] sequenzNewProject(String prjctName) throws NaturalNumberException
	{
		LocalDateTime prjctDLDT = GTDCLI.now().plusDays(14);
		LocalDateTime stepDLDT = GTDCLI.now().plusDays(7);

		String dldtQuestion = "yes";
		String prjctDLDTStr = translateTimeToAnswerString(prjctDLDT);
		String chosenFromStatieList = "2";//ATBD//TODO: make it bullet proof. it works for now.
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);
		
		String[] data = new String[9];
		data[0] = SomeCommands.new_Project;
		data[1] = prjctName;
		data[2] = newPrjctGoal;
		data[3] = dldtQuestion;
		data[4] = prjctDLDTStr;
		data[5] = chosenFromStatieList;
		data[6] = stepDesc;
		data[7] = dldtQuestion;
		data[8] = stepDLDTStr;
				
		return data;
	}


	public String[] sequenzNewProjectNoDLDT(String prjctName)
	{
		
		String dldtQuestion = "no";
		String chosenFromStatieList = "2";//ATBD//TODO: make it bullet proof. it works for now.
		
		String[] data = new String[7]; 
		data[0] = SomeCommands.new_Project;
		data[1] = prjctName;
		data[2] = newPrjctGoal;
		data[3] = dldtQuestion;
		data[4] = chosenFromStatieList;
		data[5] = stepDesc;
		data[6] = dldtQuestion;
				
		return data;
	}

	public String[] sequenzMODProject(String prjctName)
	{

		String[] data = new String[3];
		data[0] = SomeCommands.new_MOD;
		data[1] = prjctName;
		data[2] = modPrjctGoal;
				
		return data;
	}
	

	public String[] sequenzAddNote(String prjctName)
	{

		String[] data = new String[4];
		data[0] = SomeCommands.add_Note + " " + prjctName;
		data[1] = noticeOne;
		data[2] = SomeCommands.add_Note + " " + prjctName;
		data[3] = noticeTwo;
				
		return data;
	}
	
	public String[] sequenzWakeMODProject(String prjctName) throws NaturalNumberException
	{

		LocalDateTime prjctDLDT = GTDCLI.now().plusDays(14);
		LocalDateTime stepDLDT = GTDCLI.now().plusDays(7);

		String prjctDLDTStr = translateTimeToAnswerString(prjctDLDT);
		String chosenFromStatieList = "1";
		String dldtQuestion = "yes";
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);

		String[] data = new String[7];
		data[0] = SomeCommands.wake_MOD + prjctName;
		data[1] = dldtQuestion;
		data[2] = prjctDLDTStr;
		data[3] = chosenFromStatieList;
		data[4] = stepDesc3;
		data[5] = dldtQuestion;
		data[6] = stepDLDTStr;

		return data;
	}

	public String[] sequenzProjectSucceeds(String prjctName)
	{

		String projectWasSuccessQstn  = "Yes";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String[] data = new String[4];
		data[0] = SomeCommands.terminate_Project + " " + prjctName;
		data[1] = projectWasSuccessQstn;
		data[2] = wantToMakeTDTNote;
		data[3] = wantToChangeTDT;

		return data;
	}

	public String[] sequenzProjectFails(String prjctName)
	{

		String projectWasSuccessQstn  = "No";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String[] data = new String[4];
		data[0] = SomeCommands.terminate_Project + " " + prjctName;
		data[1] = projectWasSuccessQstn;
		data[2] = wantToMakeTDTNote;
		data[3] = wantToChangeTDT;

		return data;
	}

	public String[] sequenzOfFourNewProjects() throws NaturalNumberException
	{

		int s = 4;
		String[] data = new String[0];

		//starts and ends not like usually.
		for(int m=1;m<s+1;m++)
		{
			String name = getNewProjectName(m);
			data = append(data, sequenzNewProject(name));
		}

		return data;
	}
	
	public LocalDateTime getBDT(int n) throws NaturalNumberException
	{
		
		LocalDateTime jetzt = GTDCLI.now();

		LocalDate mLDT = TimeSpanCreator.getLastMonday(jetzt).toLocalDate();
		
		return LocalDateTime.of(mLDT, LocalTime.of(0, n)).minusDays(14);
	}

	public String[] sequenzManyProjects() throws NaturalNumberException
	{

		String[] data = new String[0];
		data = append(data, sequenzOfFourNewProjects());
		data = append(data,  sequenzAddNote(getNewProjectName(2)));
		data = append(data, sequenzKillStep(getNewProjectName(3)));
		data = append(data,  sequenzProjectFails(getNewProjectName(3)));
		data = append(data,  sequenzKillStep(getNewProjectName(4)));
		data = append(data,  sequenzNXTStep(getNewProjectName(4)));
		data = append(data,  sequenzNewProjectNoDLDT(killPrjctNameNoDLDT));
		data = append(data,  sequenzKillStep(killPrjctNameNoDLDT));
		data = append(data,  sequenzProjectSucceeds(killPrjctNameNoDLDT));
		data = append(data,  sequenzNewProjectNoDLDT(newPrjctNoDLDT));
		data = append(data,  sequenzMODProject(modPrjctName));
		data = append(data,  sequenzMODProject(wakeProjectName));
		data = append(data,  sequenzWakeMODProject(wakeProjectName));
		
		String[] exit = new String[1];
		exit[0] = SomeCommands.exit;
		data = append(data,  exit);

		return data;
	}

	public String getNewProjectName(int n)
	{
		return "New_Project_"+n;
	}
	
	public static String[] append(String[] a, String[] b)
	{
	    String[] result = new String[a.length + b.length];

	    System.arraycopy(a, 0, result, 0, a.length);
	    System.arraycopy(b, 0, result, a.length, b.length);

	    return result;
	}
}