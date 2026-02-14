package jborg.gtdForBash;




import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;


import java.util.Map;
import java.util.Set;


import org.json.JSONObject;


import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;




public class StatisticalTools
{

	final Set<JSONObject> prjctSet;
	final TimeSpanCreator tsc;

	public StatisticalTools(Set<JSONObject> prjctSet) throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException
	{

		if(prjctSet==null)throw new NullPointerException("Argument is null.");
		this.prjctSet = prjctSet;
		tsc = new TimeSpanCreator(prjctSet);
	}

 

	public void printMap(Map<Integer, Map<String, LocalDateTime>> map)
	{
		int s = map.size();
		for(int n=0;n<s;n++)
		{
			int ds = map.get(n).size();
			System.out.println("WeekNr: " + n + ". LDTs: " + ds);
			if(ds>0)
			{
				Map<String, LocalDateTime> innerMap = map.get(n);
				for(String pName: innerMap.keySet())
				{
					LocalDateTime ldt = innerMap.get(pName);
					System.out.println("Project Name: " + pName+ ". LDT: " + ldt);
				}
			}
		}
	}

	public Set<JSONObject> getPrjctSet()
	{
		return prjctSet;
	}
	
	public TimeSpanCreator getTimeSpanCreator()
	{
		return tsc;
	}

	public JSONObject projectJSONObjByName(String name)
	{

		JSONObject pJSON;
		pJSON = ProjectJSONToolBox.pickProjectByName(name, prjctSet);

		return pJSON;
	}
	
}