package jborg.gtdForBash;

import static jborg.gtdForBash.ProjectJSONKeyz.ADTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.DLDTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.TDTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.nameKey;
import static jborg.gtdForBash.ProjectJSONToolbox.extractLDT;
import static jborg.gtdForBash.ProjectJSONToolbox.isMODProject;
import static jborg.gtdForBash.ProjectJSONToolbox.projectHasNoDLDT;
import static jborg.gtdForBash.ProjectJSONToolbox.projectIsTerminated;

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

import org.json.JSONObject;

import javafx.util.Pair;
import jborg.gtdForBash.exceptions.TimeSpanException;

import static jborg.gtdForBash.ProjectJSONToolbox.*;

public class TimeSpanCreator
{

	private final LocalDateTime beginAnker;
	private final LocalDateTime endAnker;
	
	private LocalDateTime begin;
	private LocalDateTime end;

	Set<JSONObject> prjctSet = new HashSet<>();
	
	Map<ChronoUnit, List<ChronoUnit>> chronoUnitTimeSpanMap = new HashMap<>();
	
	public TimeSpanCreator(LocalDateTime beginAnker, LocalDateTime endAnker, Set<JSONObject> prjctSet)
	{

		this.beginAnker = beginAnker;
		this.endAnker = endAnker;
	}
	
	
	private List<ChronoUnit> createListOfChronoUnitTimeSpan(ChronoUnit cu)
	{
		List<ChronoUnit> list = new ArrayList<>();
		
		return list;
	}
	
	public void createListsOfAllChronoUnitTimeSpans()
	{
		
		Map<ChronoUnit, List<ChronoUnit>> outputMap = new HashMap<>();
		
		List<ChronoUnit> yearList = createListOfChronoUnitTimeSpan(ChronoUnit.YEARS);
		List<ChronoUnit> monthList = createListOfChronoUnitTimeSpan(ChronoUnit.MONTHS);
		List<ChronoUnit> weekList = createListOfChronoUnitTimeSpan(ChronoUnit.WEEKS);
		List<ChronoUnit> dayList = createListOfChronoUnitTimeSpan(ChronoUnit.DAYS);
		List<ChronoUnit> hourList = createListOfChronoUnitTimeSpan(ChronoUnit.HOURS);
		
		outputMap.put(ChronoUnit.YEARS, yearList);
		outputMap.put(ChronoUnit.MONTHS, monthList);
		outputMap.put(ChronoUnit.WEEKS, weekList);
		outputMap.put(ChronoUnit.DAYS, dayList);
		outputMap.put(ChronoUnit.HOURS, hourList);

		chronoUnitTimeSpanMap = outputMap;
	}
	
	public boolean oldestLDTIsStep(String jsonKey) throws IOException, URISyntaxException
	{
		Pair<String, LocalDateTime> oldestProjectLDTpair = oldestProjectLDT(jsonKey); 
		Pair<String, LocalDateTime> oldestStepLDTpair = oldestStepLDT(jsonKey); 
		
		String oldProjectName = oldestProjectLDTpair.getKey();
		String oldStepProjectName = oldestStepLDTpair.getKey();
		
		
		if(oldStepProjectName.trim().equals(""))
			return false;
		
		if(oldProjectName.trim().equals(""))
			return true;

		LocalDateTime oldPrjctLDT = oldestProjectLDTpair.getValue();
		LocalDateTime oldStepLDT = oldestStepLDTpair.getValue();
		
		if(oldStepLDT.isBefore(oldPrjctLDT))return true;
		
		return false;
	}


	public Pair<String, LocalDateTime> oldestProjectAndStepLDT(String jsonKey) throws IOException, URISyntaxException, TimeSpanException
	{
		Pair<String, LocalDateTime> oldestProjectLDTPair = oldestProjectLDT(jsonKey); 
		Pair<String, LocalDateTime> oldestStepLDTPair = oldestStepLDT(jsonKey); 
		
		String oldProjectName = oldestProjectLDTPair.getKey();
		String oldStepProjectName = oldestStepLDTPair.getKey();
		
		if(oldProjectName.trim().equals("")&&oldStepProjectName.trim().equals(""))
			return new Pair<>("", LocalDateTime.now());

		if(oldProjectName.trim().equals(""))
			return new Pair<>(oldStepProjectName, oldestStepLDTPair.getValue());
		
		if(oldStepProjectName.trim().equals(""))
			return new Pair<>(oldProjectName, oldestProjectLDTPair.getValue());

		throw new TimeSpanException("This should not happen.");
	}

	public Pair<String, LocalDateTime> oldestStepLDT(String jsonKey) throws IOException, URISyntaxException
	{

		LocalDateTime oldestLDT = LocalDateTime.now();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{

			if((isMODProject.test(pJSON)))continue;
			JSONObject step = getStepOfIndexN(0, pJSON);
			
			LocalDateTime ldt = extractLDT(step, jsonKey);
			if(ldt.isBefore(oldestLDT))
			{
				oldestLDT = ldt;
				name = pJSON.getString(nameKey);
			}
		}
		
    	Pair<String, LocalDateTime> output = new Pair<>(name, oldestLDT);
    	
    	return output;

	}

	public Pair<String, LocalDateTime> oldestProjectLDT(String jsonKey) throws IOException, URISyntaxException
    {

		LocalDateTime oldestLDT = LocalDateTime.now();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{

			if((projectHasNoDLDT.test(pJSON))&&(jsonKey.equals(DLDTKey)))continue;
			if((!projectIsTerminated.test(pJSON))&&(jsonKey.equals(TDTKey)))continue;
			if((isMODProject.test(pJSON))&&(jsonKey.equals(ADTKey)))continue;
			
			LocalDateTime ldt = extractLDT(pJSON, jsonKey);
			if(ldt.isBefore(oldestLDT))
			{
				oldestLDT = ldt;
				name = pJSON.getString(nameKey);
			}
		}
		
    	Pair<String, LocalDateTime> output = new Pair<>(name, oldestLDT);
    	
    	return output;
    }
	
    public boolean youngestLDTIsStep(String jsonKey) throws IOException, URISyntaxException
    {
		Pair<String, LocalDateTime> youngestProjectLDTpair = youngestProjectLDT(jsonKey); 
		Pair<String, LocalDateTime> youngestStepLDTpair = youngestStepLDT(jsonKey); 
		
		String youngProjectName = youngestProjectLDTpair.getKey();
		String youngStepProjectName = youngestStepLDTpair.getKey();
		
		
		if(youngStepProjectName.trim().equals(""))
			return false;
		
		if(youngProjectName.trim().equals(""))
			return true;

		LocalDateTime youngPrjctLDT = youngestProjectLDTpair.getValue();
		LocalDateTime youngStepLDT = youngestStepLDTpair.getValue();
		
		if(youngStepLDT.isAfter(youngPrjctLDT))return true;
		
		return false;
    }

    public Pair<String, LocalDateTime> youngestProjectAndStepLDT(String jsonKey) throws IOException, URISyntaxException, TimeSpanException
    {
    	
    	Pair<String, LocalDateTime> projectLDTPair = youngestProjectLDT(jsonKey);
    	Pair<String, LocalDateTime> stepLDTPair = youngestStepLDT(jsonKey);
    	
		String youngProjectName = projectLDTPair.getKey();
		String youngStepProjectName = stepLDTPair.getKey();
		
		if(youngProjectName.trim().equals("")&&youngStepProjectName.trim().equals(""))
			return new Pair<>("", LocalDateTime.now());

		if(youngProjectName.trim().equals(""))
			return new Pair<>(youngStepProjectName, stepLDTPair.getValue());
		
		if(youngStepProjectName.trim().equals(""))
			return new Pair<>(youngProjectName, projectLDTPair.getValue());

		throw new TimeSpanException("This should not happen.");
    }

    public Pair<String, LocalDateTime> youngestStepLDT(String jsonKey) throws IOException, URISyntaxException
    {

		LocalDateTime youngestLDT = oldestStepLDT(jsonKey).getValue();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{

			if((isMODProject.test(pJSON)))continue;
			JSONObject step = getStepOfIndexN(0, pJSON);
			
			LocalDateTime ldt = extractLDT(step, jsonKey);
			if(ldt.isAfter(youngestLDT))
			{
				youngestLDT = ldt;
				name = pJSON.getString(nameKey);
			}
		}

    	Pair<String, LocalDateTime> output = new Pair<>(name, youngestLDT);

    	return output;
    }

    public Pair<String, LocalDateTime> youngestProjectLDT(String jsonKey) throws IOException, URISyntaxException
    {

		LocalDateTime youngest = oldestProjectLDT(jsonKey).getValue();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{
			if((jsonKey.equals(DLDTKey)&&projectHasNoDLDT.test(pJSON)))continue;
			if((jsonKey.equals(ADTKey)&&isMODProject.test(pJSON)))continue;
			if((jsonKey.equals(TDTKey)&&!projectIsTerminated.test(pJSON)))continue;
				
			LocalDateTime ldt = extractLDT(pJSON, jsonKey);
			if(ldt.isAfter(youngest))
			{
				youngest = ldt;
				name = pJSON.getString(ProjectJSONKeyz.nameKey);
			}
		}
		
    	Pair<String, LocalDateTime> output = new Pair<>(name, youngest);
    	
    	return output;
    }
}