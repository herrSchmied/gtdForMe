package jborg.gtdForBash;

import static jborg.gtdForBash.SequenzesForISS.sequenzManyProjects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import allgemein.LittleTimeTools;
import consoleTools.InputStreamSession;
import someMath.NaturalNumberException;

public class ProjectSetForTesting
{

	static Set<JSONObject> prjctSet = new HashSet<>();
	
	public static Set<JSONObject> get() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		String data = sequenzManyProjects(LocalDateTime.now().minusDays(14));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        new GTDCLI(iss);
        prjctSet = GTDCLI.loadProjects();
        
        for(int m=1;m<5;m++)
        {
            JSONObject pJSON = pickProjectByName(SequenzesForISS.getNewProjectName(m));
            String bdtStr = pJSON.getString(ProjectJSONKeyz.BDTKey);
            LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
            alterProjectNDDT(pJSON, bdt);
        }
        
        return prjctSet;
	}
	
	public static void alterNthStepNDDT(JSONObject pJSON, LocalDateTime newNDDT, int n)
	{
		JSONArray stpArr = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stpArr.getJSONObject(n);
	
		String nddtStr = LittleTimeTools.timeString(newNDDT);
		step.put(StepJSONKeyz.NDDTKey, nddtStr);

	}

	public static void alterProjectNDDT(JSONObject pJSON, LocalDateTime newNDDT)
	{
		String newNDDTStr = LittleTimeTools.timeString(newNDDT);
		pJSON.put(ProjectJSONKeyz.NDDTKey, newNDDTStr);
	}
	
	public static JSONObject pickProjectByName(String pName)
	{

		for(JSONObject pJSON: prjctSet)
		{
			if(pJSON.getString(ProjectJSONKeyz.nameKey).equals(pName))return pJSON;
		}
		throw new IllegalArgumentException("No Project JSON Object by that Name.");
	}
}