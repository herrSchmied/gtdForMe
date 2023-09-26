package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import consoleTools.InputStreamSession;
import junit.framework.TestCase;

public class TestingGTDDataSpawnSession extends TestCase 
{
	
	JSONObject pJSON;
	
	String prjctName = "neueArbeit\n";

	String prjctGoal = "TryToNotFailThisTest\n";
	String stpDesc = "Just do it!\n";

	public String setUpDataNewProjectHappyPath(boolean modQ, boolean pBDTQ, boolean sBDTQ, boolean startStatus, LocalDateTime ldtPrjctDLDT, 
			LocalDateTime ldtStpDLDT)
	{
		
		String modPrjct;
		if(!modQ)modPrjct= "No\n";
		else modPrjct = "Yes\n";

		String specialPrjctBDT;
		String prjctBDT = "";
		if(!pBDTQ)specialPrjctBDT= "No\n";
		else 
		{
			specialPrjctBDT = "Yes\n";
			LocalDateTime oneHourBefore = LocalDateTime.now().minusHours(1);
			prjctBDT = translateTimeToAnswerString(oneHourBefore);
		}
		
		String setup = prjctName + modPrjct +prjctGoal + specialPrjctBDT;
		if(modQ&&!pBDTQ) return setup;
		if(modQ&&pBDTQ)
		{
			LocalDateTime tenBeforeNow = LocalDateTime.now().minusMinutes(10);
			String ldtString = translateTimeToAnswerString(tenBeforeNow);
			
			return setup + ldtString;
		}
		
		String prjctDLDT = translateTimeToAnswerString(ldtPrjctDLDT);
		
		String specialStpBDT;
		String stpBDT = "";
		if(!sBDTQ)specialStpBDT= "No\n";
		else 
		{
			specialStpBDT = "Yes\n";
			LocalDateTime oneHourBefore = LocalDateTime.now().minusHours(1);
			stpBDT = translateTimeToAnswerString(oneHourBefore);
		}
		
		String chosenFromStatieList;
		if(!startStatus)chosenFromStatieList = "1\n";//ATBD
		else chosenFromStatieList = "2\n";
				
		String stepDLDT = translateTimeToAnswerString(ldtStpDLDT);

		String data = prjctName
				+ modPrjct
				+ prjctGoal
				+ specialPrjctBDT
				+ prjctBDT
				+ prjctDLDT
				+ specialStpBDT
				+ stpBDT
				+ chosenFromStatieList
				+ stpDesc
				+ stepDLDT;
		
		return data;
	}
	
	public String translateTimeToAnswerString(LocalDateTime ldt)
	{
		
		int hour = ldt.getHour();
		int minute = ldt.getMinute();
		int year = ldt.getYear();
		int month = ldt.getMonthValue();
		int day = ldt.getDayOfMonth();
		
		return hour + "\n" + minute +"\n" + year + "\n" + month + "\n" + day +"\n";

	}
	
	public void testSpawnNewProject() 
	{

		prjctName = "neueArbeit\n";
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		String data = setUpDataNewProjectHappyPath(false, false, false, false,jetzt.plusMinutes(10), jetzt.plusMinutes(9));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		pJSON = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
	
		String name = pJSON.getString(ProjectJSONKeyz.nameKey);
		int l = prjctName.length()-1;
		assert(name.equals(prjctName.substring(0, l)));

		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		System.out.println(status);
		assert(StatusMGMT.atbd.equals(status));
		
		JSONArray stepArray = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stepArray.getJSONObject(0);
		String stpStatus = step.getString(StepJSONKeyz.statusKey);
		assert(stpStatus.equals(status));
		
		String stpDesc = step.getString(StepJSONKeyz.descKey);
		l = this.stpDesc.length()-1;
		assert(stpDesc.equals(this.stpDesc.substring(0,l)));
		
		String goal = pJSON.getString(ProjectJSONKeyz.goalKey);
		l = prjctGoal.length()-1;
		assert(goal.equals(prjctGoal.substring(0,l)));
		
		System.out.println(pJSON.toString(4));
	}

	public void testSpawnMODProject()
	{
		
		boolean mod = true;
		boolean specificPrjctBDT = false;
		boolean specificStpBDT = false;
		boolean startStatus = false;
		
		prjctName = "Maybe Baby\n";
		
		LocalDateTime jetzt = LocalDateTime.now();
		LocalDateTime pDLDT = jetzt.plusHours(1);
		LocalDateTime stpDLDT = jetzt.plusMinutes(30);
		String data = setUpDataNewProjectHappyPath(mod, specificPrjctBDT, specificStpBDT, startStatus, pDLDT, stpDLDT);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		pJSON = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
	
		String name = pJSON.getString(ProjectJSONKeyz.nameKey);
		int l = prjctName.length()-1;
		assert(name.equals(prjctName.substring(0, l)));

		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		System.out.println(status);
		assert(StatusMGMT.mod.equals(status));
				
		String goal = pJSON.getString(ProjectJSONKeyz.goalKey);
		l = prjctGoal.length()-1;
		assert(goal.equals(prjctGoal.substring(0,l)));
		
		System.out.println(pJSON.toString(4));
	}
	
	/*
	public void testWakeMOD() 
	{
		//fail("Not yet implemented");
	}

	/*
	public void testAppendStep() {
		fail("Not yet implemented");
	}

	public void testAddNote() {
		fail("Not yet implemented");
	}

	public void testTerminateProject() {
		fail("Not yet implemented");
	}
	*/
}
