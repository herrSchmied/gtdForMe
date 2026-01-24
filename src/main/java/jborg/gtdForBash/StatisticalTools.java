package jborg.gtdForBash;



import java.awt.Point;

import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import allgemein.ExactPeriode;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;
import static jborg.gtdForBash.ProjectJSONToolbox.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;


import someMath.NaturalNumberException;



public class StatisticalTools
{

	final Set<JSONObject> prjctSet;
	final TimeSpanCreator tsc;

	public StatisticalTools(Set<JSONObject> prjctSet) throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException
	{

		if(prjctSet==null||prjctSet.isEmpty())throw new StatisticalToolsException("No Projects.");
		this.prjctSet = prjctSet;
		tsc = new TimeSpanCreator(prjctSet);
	}

 
	// <#1 how Many, < #2 project Name, LDT>
	public Map<Integer, Map<String, LocalDateTime>> timeSpansLDTs(ChronoUnit cu, String jsonKey) throws IOException, URISyntaxException, StatisticalToolsException, TimeSpanException
	{

		
		Map<Integer, Map<String, LocalDateTime>> map = new HashMap<>();
		List<Map<String, LocalDateTime>> listOfMaps = new ArrayList<>();
		List<TimeSpanData> timeSpans = tsc.getTimeSpanList(cu);

		for(int n=0;n<timeSpans.size();n++)
		{
			listOfMaps.add(new HashMap<>());
		}

		for(JSONObject pJSON: prjctSet)
		{
			
			String pName = pJSON.getString(nameKey);
			if((jsonKey.equals(TDTKey))&&(!projectIsTerminated.test(pJSON)))continue;
			if((jsonKey.equals(DLDTKey))&&(projectHasNoDLDT.test(pJSON)))continue;
			if((jsonKey.equals(ADTKey))&&(isMODProject.test(pJSON)))continue;
			
			LocalDateTime ldt = extractLDT(pJSON, jsonKey);
			Map<String, LocalDateTime> innerMap = listOfMaps.get(tsc.isInWhichTimeSpan(cu, ldt));
			//DLDT might be in no week but to far in the future.

			innerMap.put(pName, ldt);
		}

		for(int n=0;n<timeSpans.size();n++)
		{
			map.put(n, listOfMaps.get(n));
		}

		return map;
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

	public Point timeSpanWithMostLDTs(ChronoUnit cu, String jsonKey) throws IOException, URISyntaxException, StatisticalToolsException, TimeSpanException
	{

		Map<Integer, Map<String, LocalDateTime>> map = timeSpansLDTs(cu, jsonKey);
		if(map.isEmpty())return null;
		
		int whichOne = 0;
		int howMany = 0;
		for(Integer n: map.keySet())
		{
			Map<String, LocalDateTime> set = map.get(n);
			int size = set.size();
			if(size>howMany)
			{
				howMany=size;
				whichOne=n;
			}
		}
		return new Point(whichOne, howMany);
	}
	
	public Set<JSONObject> getPrjctSet()
	{
		return prjctSet;
	}
	
	public TimeSpanCreator getTimeSpanCreator()
	{
		return tsc;
	}

	public JSONObject pickByName(String name)
	{

		for(JSONObject pJSON: prjctSet)
		{
			String pName = pJSON.getString(nameKey);
			if(name.equals(pName))return pJSON;
		}

		return null;
	}
	
	// #new Step					:	 6 Points.
	// #new Project					:	12 Points.
	// #Step success				:	18 Points.
	// #Project success				:	36 Points.
	// #Step failed					:	-1 Points.
	// #Step failed by DLDT abuse	:	-2 Points.
	// #Project failed				:	-1 Point per active Day. **Fail fast!!!
	// #Project failed by DLDT abuse:	-4 Point per active Day.
	public int positivityIndexTimeSpan(TimeSpanData tsd) throws IOException, URISyntaxException, NaturalNumberException
	{
		int sum = 0;
		
		sum += tsd.howManyNewStepsInThisTSD()*6;
		sum += tsd.getProjectsWrittenDown().size()*12;
		sum += tsd.howManyStepsSucceededInThisTSD()*18;
		sum += tsd.projectsSucceededThisTimeSpan().size()*36;
		sum += tsd.howManyStepsFailedInThisTSD()*(-1);
		sum += tsd.howManyStepsViolatedDLInThisTSD()*(-2);
		
		// #Project failed				:	-1 Point per active Day.
		for(String pName: tsd.projectsFailedThisTimeSpan())
		{
			JSONObject pJSON = tsd.pickProjectByName(pName);
			
			LocalDateTime adt = extractLDT(pJSON, ADTKey);
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);
			
			ExactPeriode ep = new ExactPeriode(adt, tdt);
			
			sum += ep.getAbsoluteDays()*(-1);
		}
		
		// #Project failed by DLDT abuse:	-4 Point per active Day.
		for(String pName: tsd.projectsViolatedDLThisTimeSpan())
		{
			JSONObject pJSON = tsd.pickProjectByName(pName);
			
			LocalDateTime adt = extractLDT(pJSON, ADTKey);
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);
			
			ExactPeriode ep = new ExactPeriode(adt, tdt);
			
			sum += ep.getAbsoluteDays()*(-4);
		}

		return sum;
	}
}