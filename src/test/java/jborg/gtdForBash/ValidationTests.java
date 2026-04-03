package jborg.gtdForBash;




import java.io.IOException;

import java.net.URISyntaxException;

import java.util.Set;


import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jborg.gtdForBash.exceptions.CLICMDException;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;
import static jborg.gtdForBash.ProjectJSONToolBox.*;


import someMath.NaturalNumberException;




public class ValidationTests
{

	static GTDCLI gtdCli;
	static Set<JSONObject> projects;
	
	@BeforeEach
	public void setDataFolder() throws JSONException, IOException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException, ClassNotFoundException, CLICMDException
	{

		projects = ProjectSetForTesting.get();

		assert(!projects.isEmpty());
	}

	@Test
	public void test()
	{
		JSONObject pJSON = pickProjectByName(ProjectSetForTesting.getSqzFISS().getNewProjectName(1), projects);
		ProjectJSONValidator pjv = new ProjectJSONValidator();
		
		assert(pjv.validate(pJSON.toString()));
		
		pJSON = pickProjectByName("MOD_Project", projects);
		
		pjv.setSchema("/modProjectJSONSchema.json");
		
		assert(pjv.validate(pJSON.toString()));
	}
}
