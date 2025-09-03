package jborg.gtdForBash;

import java.awt.Point;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import allgemein.LittleTimeTools;
import javafx.util.Pair;

public class StatisticalTools
{

	final Set<JSONObject> prjctSet;
	final List<Pair<LocalDate, LocalDate>> weekSpans;

	public StatisticalTools(Set<JSONObject> prjctSet)
	{

		this.prjctSet = prjctSet;
		weekSpans = computeWeekSpans();
	}
	
	public List<Pair<LocalDate, LocalDate>> computeWeekSpans()
	{

		List<Pair<LocalDate, LocalDate>> weekSpans = new ArrayList<>();
		
	    LocalDateTime old = oldestProject().getValue();
	    	
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
	   
    public Pair<String, LocalDateTime> oldestProject()
    {

		LocalDateTime oldestBDT = LocalDateTime.now();
		
		String name = "";
		
		for(JSONObject pJSON: prjctSet)
		{
			String bdtStr = pJSON.getString(ProjectJSONKeyz.BDTKey);
			LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
			if(bdt.isBefore(oldestBDT))
			{
				oldestBDT = bdt;
				name = pJSON.getString(ProjectJSONKeyz.nameKey);
			}
		}
		
    	Pair<String, LocalDateTime> output = new Pair<>(name, oldestBDT);
    	
    	return output;
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
    
    public int isInWhichWeek(LocalDateTime ldt)
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
	
	public List<Pair<LocalDate, LocalDate>> getWeekSpans()
	{
		return weekSpans;
	}
	
	public Set<JSONObject> getPrjctSet()
	{
		return prjctSet;
	}
}