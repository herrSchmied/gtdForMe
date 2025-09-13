package jborg.gtdForBash;

import java.awt.Point;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import allgemein.LittleTimeTools;
import javafx.util.Pair;
import jborg.gtdForBash.exceptions.WeekDataException;

public class StatisticalTools
{

	final Set<JSONObject> prjctSet;
	final List<Pair<LocalDate, LocalDate>> weekSpans;
	final List<WeekData> weekDatas;

	public StatisticalTools(Set<JSONObject> prjctSet) throws IOException, URISyntaxException, WeekDataException
	{

		this.prjctSet = prjctSet;
		weekSpans = computeWeekSpans();
		weekDatas = getWeekDataList();
	}
	
	public List<Pair<LocalDate, LocalDate>> computeWeekSpans() throws IOException, URISyntaxException
	{

		List<Pair<LocalDate, LocalDate>> weekSpans = new ArrayList<>();
		
	    LocalDateTime old = oldestLDT(ProjectJSONKeyz.BDTKey).getValue();
	    	
	   	DayOfWeek dow = old.getDayOfWeek();
	   	int dowNr = dow.getValue();
	    	
	   	LocalDate mondayOne = old.minusDays(dowNr-1).toLocalDate();
	   	LocalDate jetzt = LocalDate.now();
    	LocalDate currentMonday = mondayOne;
	    	
    	while(true)
	   	{
	   		LocalDate currentSunday = currentMonday.plusDays(6);
	   		Pair<LocalDate, LocalDate> week = new Pair<>(currentMonday, currentSunday);
	   		weekSpans.add(week);
    		currentMonday = currentMonday.plusDays(7);
    		if(currentMonday.isAfter(jetzt))break;
    	}
	    	
    	return weekSpans;
	}
	
	public List<WeekData> getWeekDataList() throws IOException, URISyntaxException, WeekDataException
	{

		List<WeekData> outputList = new ArrayList<>();
		int s = weekSpans.size();

		for(int n=0;n<s;n++)
		{

			Pair<LocalDate, LocalDate> span = weekSpans.get(n);

			Set<String> activeProjects = new HashSet<>();
			Set<String> bdtProjects = new HashSet<>();
			Set<String> nddtProjects = new HashSet<>();
			Set<String> terminatedProjects = new HashSet<>();
			WeekData wd = new WeekData(span.getKey(), n);
			if(span.getValue().isAfter(LocalDate.now()))continue;

			for(JSONObject pJSON: prjctSet)
			{
				String name = pJSON.getString(ProjectJSONKeyz.nameKey);
				LocalDateTime bdt = extractLDT(pJSON, ProjectJSONKeyz.BDTKey);
				if(isInThatWeek(n, bdt))
				{
					bdtProjects.add(name);
				}

				LocalDateTime nddt = extractLDT(pJSON, ProjectJSONKeyz.NDDTKey);
				if(isInThatWeek(n, nddt))
				{
					nddtProjects.add(name);
				}

				String status = pJSON.getString(ProjectJSONKeyz.statusKey);
				
				StatusMGMT statusMGMT = StatusMGMT.getInstance();
				if(statusMGMT.getStatesOfASet(StatusMGMT.terminalSetName).contains(status))
				{
					
					LocalDateTime tdt = extractLDT(pJSON, ProjectJSONKeyz.TDTKey);
					if(isInThatWeek(n, tdt))
					{
						terminatedProjects.add(name);
					}
				}
				else
				{
					if(span.getValue().isAfter(bdt.toLocalDate()))activeProjects.add(name);
				}
			}
			
			wd.setProjectsActive(activeProjects);
			wd.setProjectsBorn(bdtProjects);
			wd.setProjectsWrittenDown(nddtProjects);
			wd.setProjectsTerminated(terminatedProjects);
			
			outputList.add(wd);
		}

		return outputList;
	}
	   
    public Pair<String, LocalDateTime> oldestLDT(String jsonKey) throws IOException, URISyntaxException
    {
		LocalDateTime oldestLDT = LocalDateTime.now();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{

			LocalDateTime bdt = extractLDT(pJSON, jsonKey);
			if(bdt.isBefore(oldestLDT))
			{
				oldestLDT = bdt;
				name = pJSON.getString(ProjectJSONKeyz.nameKey);
			}
		}
		
    	Pair<String, LocalDateTime> output = new Pair<>(name, oldestLDT);
    	
    	return output;
    }
    
    public Pair<String, LocalDateTime> youngestLDT(String jsonKey) throws IOException, URISyntaxException
    {

		LocalDateTime youngest = oldestLDT(jsonKey).getValue();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{

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

    public List<LocalDateTime> allLDTs(String jsonKey) throws IOException, URISyntaxException
    {

    	List<LocalDateTime> list = new ArrayList<>();
		
		for(JSONObject pJSON: prjctSet)
		{

			if(jsonKey.equals(ProjectJSONKeyz.TDTKey)&&
					!SomeCommands.projectIsTerminated.test(pJSON))
						continue;

			LocalDateTime ldt = extractLDT(pJSON, jsonKey);
			list.add(ldt);
		}

    	return list;
    }

    public boolean isInThatWeek(int weekNr, LocalDateTime ldt)
    {

    	if((weekNr<0)&&(weekNr>weekSpans.size()-1))throw new RuntimeException("weekNr does not exist.");
    	Pair<LocalDate, LocalDate> week = weekSpans.get(weekNr);
    	
    	LocalDate beginLD = week.getKey().minusDays(1);
    	LocalDate endLD = week.getValue().plusDays(1);
    	
    	LocalDate ld = ldt.toLocalDate();
    	
    	return ld.isAfter(beginLD)&&ld.isBefore(endLD);
    }
    
    public int isInWhichWeek(LocalDateTime ldt) throws IOException, URISyntaxException
    {
    	List<Pair<LocalDate, LocalDate>> weeks = computeWeekSpans();
    	for(int n=0;n<weeks.size();n++)
    	{
    		if(isInThatWeek(n, ldt))return n;
    	}
    	
    	throw new RuntimeException("This should not happen.");
    }

    //Remember: should this Method be here?
	public boolean pickAndCheckByName(String name, int weekNr, GTDCLI gtdCli) throws IOException, URISyntaxException
	{

        LocalDateTime bdt = extractLDT(name, ProjectJSONKeyz.BDTKey);

		return isInThatWeek(weekNr, bdt);
	}
	
	private LocalDateTime extractLDT(String name, String key) throws IOException, URISyntaxException
	{
		
        Set<JSONObject> jsonSet = GTDCLI.loadProjects();

	    JSONObject pJSON = GTDCLI.pickProjectByName(name, jsonSet);

		return  extractLDT(pJSON, key);
	}
	
	private LocalDateTime extractLDT(JSONObject pJSON, String key) throws IOException, URISyntaxException
	{
		return  LittleTimeTools.LDTfromTimeString(pJSON.getString(key));
	}

	public Point weekWithMostBDTs() throws IOException, URISyntaxException
	{
		int bdt[] = new int[weekSpans.size()];
		
		for(int n=0;n<weekSpans.size();n++)bdt[n]=0;

		for(JSONObject pJSON: prjctSet)
		{
			
			LocalDateTime ldt = extractLDT(pJSON, ProjectJSONKeyz.BDTKey);
			bdt[isInWhichWeek(ldt)]++;
		}
		
		int howMany = 0;
		int weekNr = 0;
		for(int n=0;n<weekSpans.size();n++)
		{
			if(bdt[n]>howMany)
			{
				howMany = bdt[n];
				weekNr = n;
			}
		}

		return new Point(weekNr, howMany);
	}
	
	public String periodeResume(ChronoUnit cu, int unitNr)
	{

		
		int stepBDTs = 0;
		int stepNDDTs = 0;
		int stepsSucceded = 0;
		int stepsFailed = 0;
		int stepDeadlineViolation = 0;
		int openStepDeadlines = 0;
		int stepDeadlinesHere = 0;
		String stepMostPressingDeadline = "";

		int prjctBDTs = 0;
		int prjctNDDTs = 0;
		int prjctsSucceded = 0;
		int prjctsFailed = 0;
		int prjctDeadlineViolation = 0;
		int openPrjctDeadlines = 0;
		int prjctDeadlinesHere = 0;
		String prjctMostPressingDeadline = "";
		
		int l = cu.toString().length()-1;
		String unit = cu.toString().substring(l);
		
		if(cu.equals(ChronoUnit.WEEKS))
		{
			
		}
		
		String s = "\nProjects:"
				 + "\nBorn Projects: " + prjctBDTs
				 + "\nNew Projects written: " + prjctNDDTs
				 + "\nSuccesses: " + prjctsSucceded
				 + "\nFailed: " + prjctsFailed
				 + "\nDeadlineViolations: " + prjctDeadlineViolation
				 + "\nOpen Deadlines: " + openPrjctDeadlines
				 + "\nDeadlines this " + unit + ": " + prjctDeadlinesHere
				 + "\nMost pressing Deadline: " + prjctMostPressingDeadline
				 + "\n"
				 + "\nSteps:"
				 + "\nBorn Steps: " + stepBDTs
				 + "\nNew Steps written: " + stepNDDTs
				 + "\nSuccesses: " + stepsSucceded
				 + "\nFailed: " + stepsFailed
				 + "\nDeadlineViolations: " + stepDeadlineViolation
				 + "\nOpen Deadlines: " + openStepDeadlines
				 + "\nDeadlines this " + unit + ": " + stepDeadlinesHere
		 		 + "\nMost pressing Deadline: " + stepMostPressingDeadline;

		return s;
	}
	
	public List<Pair<LocalDate, LocalDate>> getWeekSpans()
	{
		return weekSpans;
	}
	
	public Set<JSONObject> getPrjctSet()
	{
		return prjctSet;
	}
}