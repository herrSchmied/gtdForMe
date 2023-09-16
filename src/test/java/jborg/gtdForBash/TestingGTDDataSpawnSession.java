package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import consoleTools.InputStreamSession;
import junit.framework.TestCase;

public class TestingGTDDataSpawnSession extends TestCase {

	public void testSpawnNewProject() 
	{

		String prjctName = "neueArbeit\n";
		String modPrjct = "No\n";
		String prjctGoal = "TryToNotFailThisTest\n";
		String specialPrjctBDT = "No\n";
		String prjctDLDT = "0\n 0\n 2024\n 1\n 1\n";
		String specialStpBDT = "No\n";
		String choosenFromStatieList = "1\n";
		String stepDesc = "Just do it!\n";
		String stepDLDT = "0\n 0\n 2023\n 12\n 12\n";
		
		String data = prjctName
				+ modPrjct
				+ prjctGoal
				+ specialPrjctBDT
				+ prjctDLDT
				+ specialStpBDT
				+ choosenFromStatieList
				+ stepDesc
				+ stepDLDT;

		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		GTDDataSpawnSession gdss = new GTDDataSpawnSession(iss);
		JSONObject pJSON = gdss.spawnNewProject(new HashMap<String, JSONObject>(), StatusMGMT.getInstance());
	
		String name = pJSON.getString(ProjectJSONKeyz.nameKey);
		assert(name.equals("neueArbeit"));

		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		assert(StatusMGMT.atbd.equals(status));
		
		JSONArray stepArray = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stepArray.getJSONObject(0);
		String stpStatus = step.getString(StepJSONKeyz.statusKey);
		assert(stpStatus.equals(status));
		
		
		System.out.println(pJSON.toString(4));
	}
	/*
	public void testSpawnFirstStep() {
		fail("Not yet implemented");
	}

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
