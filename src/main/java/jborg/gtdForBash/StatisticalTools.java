package jborg.gtdForBash;

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
	
	public StatisticalTools(Set<JSONObject> prjctSet)
	{
		this.prjctSet = prjctSet;
	}
	
	public List<Pair<LocalDate, LocalDate>> listOfWeeks()
	{


		List<Pair<LocalDate, LocalDate>> listOfWeex = new ArrayList<>();
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
	   		listOfWeex.add(week);
    		currentMonday = currentMonday.plusDays(7);
    		if(currentMonday.isAfter(jetzt))break;
    	}
	    	
    	return listOfWeex;
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

    	if((weekNr<0)&&(weekNr>listOfWeeks().size()-1))throw new RuntimeException("weekNr does not exist.");
    	Pair<LocalDate, LocalDate> week = listOfWeeks().get(weekNr);
    	
    	LocalDate beginLD = week.getKey().minusDays(1);
    	LocalDate endLD = week.getValue().plusDays(1);
    	
    	LocalDate ld = ldt.toLocalDate();
    	
    	return ld.isAfter(beginLD)&&ld.isBefore(endLD);
    }
    
    public int isInWhichWeek(LocalDateTime ldt)
    {
    	List<Pair<LocalDate, LocalDate>> weeks = listOfWeeks();
    	for(int n=0;n<weeks.size();n++)
    	{
    		if(isInThatWeek(n, ldt))return n;
    	}
    	
    	throw new RuntimeException("This should not happen.");
    }

	public boolean pickAndCheckByName(String name, int weekNr, GTDCLI gtdCli) throws IOException, URISyntaxException
	{

        LocalDateTime bdt = extractLDT(name, ProjectJSONKeyz.BDTKey);

		return isInThatWeek(weekNr, bdt);
	}
	
	private LocalDateTime extractLDT(String name, String key) throws IOException, URISyntaxException
	{
		
        Set<JSONObject> jsonSet = GTDCLI.loadProjects();

	    JSONObject pJSON = GTDCLI.pickProjectByName(name, jsonSet);

		return  LittleTimeTools.LDTfromTimeString(pJSON.getString(key));
	}
}