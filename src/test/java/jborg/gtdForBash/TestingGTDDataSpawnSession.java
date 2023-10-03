package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import consoleTools.InputStreamSession;


public class TestingGTDDataSpawnSession
{
	
	JSONObject pJSON1;
	JSONObject pJSONMod;
	
	String prjctName = "neueArbeit\n";
	String prjctNameMod = "Maybe Baby\n";
	
	String prjctGoal = "TryToNotFailThisTest\n";
	String stpDesc = "Just do it!\n";
	String stpDesc2 = "another Step.\n";
	

	public GTDDataSpawnSession arrangePrjct()
	{
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		boolean mod = false;
		boolean specificPrjctBDT = false;
		boolean specificStpBDT = false;
		boolean startStatus = false;

		String data = setUpDataNewProjectHappyPath(prjctName, mod, specificPrjctBDT, specificStpBDT, startStatus, jetzt.plusMinutes(10), jetzt.plusMinutes(9));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		pJSON1 = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
		
		return gdss;
	}
	
	
	public GTDDataSpawnSession arrangeMODPrjct()
	{
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		boolean mod = true;
		boolean specificPrjctBDT = false;
		boolean specificStpBDT = false;
		boolean startStatus = false;
		
		LocalDateTime pDLDT = jetzt.plusHours(1);
		LocalDateTime stpDLDT = jetzt.plusMinutes(30);
		String data = setUpDataNewProjectHappyPath(prjctNameMod, mod, specificPrjctBDT, specificStpBDT, startStatus, pDLDT, stpDLDT);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		pJSONMod = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
		
		return gdss;
	}
	
	public String setUpDataNewProjectHappyPath(String prjctName, boolean modQ, boolean pBDTQ, boolean sBDTQ, boolean startStatus, LocalDateTime ldtPrjctDLDT, 
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
		
		String oldStepWasSuccess = "Yes\n";
		String wantToMakeTerminalNote = "Yes\n";
		String terminalNote = "I'm Thru wit it.\n";
		String wantToChangeTDT = "No\n";
		String specialStpBDT2 = "No\n";
		String stpDesc2 = this.stpDesc2;
		String step2DLDT = translateTimeToAnswerString(ldtPrjctDLDT.minusSeconds(1));

		String step2WasSuccess = "No\n";
		String wantToMakeTerminalNote2 = "No\n";
		String wantToChangeTDT2 = "No\n";
		String specialStpBDT3 = "No\n";
		String stpDesc3 = "Step 3\n";
		String step3DLDT = translateTimeToAnswerString(ldtPrjctDLDT.minusSeconds(2));

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
				+ stepDLDT
				+ oldStepWasSuccess
				+ wantToMakeTerminalNote
				+ terminalNote
				+ wantToChangeTDT
				+ specialStpBDT2
				+ chosenFromStatieList
				+ stpDesc2
				+ step2DLDT
				+ step2WasSuccess
				+ wantToMakeTerminalNote2
				+ wantToChangeTDT2
				+ specialStpBDT3
				+ chosenFromStatieList
				+ stpDesc3
				+ step3DLDT;
				
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
	
	@Test
	public void testSpawnNewProject() 
	{
		arrangePrjct();
		String name = pJSON1.getString(ProjectJSONKeyz.nameKey);
		int l = prjctName.length()-1;
		assert(name.equals(prjctName.substring(0, l)));

		String status = pJSON1.getString(ProjectJSONKeyz.statusKey);
		System.out.println(status);
		assert(StatusMGMT.atbd.equals(status));
		
		JSONArray stepArray = pJSON1.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stepArray.getJSONObject(0);
		String stpStatus = step.getString(StepJSONKeyz.statusKey);
		assert(stpStatus.equals(status));
		
		String stpDesc = step.getString(StepJSONKeyz.descKey);
		l = this.stpDesc.length()-1;
		assert(stpDesc.equals(this.stpDesc.substring(0,l)));
		
		String goal = pJSON1.getString(ProjectJSONKeyz.goalKey);
		l = prjctGoal.length()-1;
		assert(goal.equals(prjctGoal.substring(0,l)));
		
		System.out.println(pJSON1.toString(4));
	}

	public void testSpawnMODProject()
	{
		
		arrangeMODPrjct();
		String name = pJSONMod.getString(ProjectJSONKeyz.nameKey);
		int l = prjctNameMod.length()-1;
		assert(name.equals(prjctNameMod.substring(0, l)));

		String status = pJSONMod.getString(ProjectJSONKeyz.statusKey);
		System.out.println(status);
		assert(StatusMGMT.mod.equals(status));
				
		String goal = pJSONMod.getString(ProjectJSONKeyz.goalKey);
		l = prjctGoal.length()-1;
		assert(goal.equals(prjctGoal.substring(0,l)));
		
		System.out.println(pJSONMod.toString(4));
	}
	
	/*
	public void testWakeMOD() 
	{
		//fail("Not yet implemented");
	}
	*/

	@Test
	public void testAppendStep() throws IOException 
	{
		GTDDataSpawnSession gdss = arrangePrjct();
		gdss.appendStep(pJSON1);
		
		JSONArray jArray = pJSON1.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		assert(jArray.length()==2);
		
		JSONObject step2 = jArray.getJSONObject(1);
		String desc2 = step2.getString(StepJSONKeyz.descKey);
		int l = this.stpDesc2.length()-1;
		assert(desc2.equals(this.stpDesc2.substring(0, l)));
		
		gdss.appendStep(pJSON1);
		assert(jArray.length()==3);
		
		System.out.println(pJSON1.toString(4));
	}
	
	/*
	public void testAddNote() {
		fail("Not yet implemented");
	}

	public void testTerminateProject() {
		fail("Not yet implemented");
	}
	*/
}
