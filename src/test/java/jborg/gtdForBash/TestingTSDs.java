package jborg.gtdForBash;

import static jborg.gtdForBash.ProjectJSONKeyz.ADTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.NDTKey;
import static jborg.gtdForBash.ProjectJSONKeyz.goalKey;
import static jborg.gtdForBash.ProjectJSONKeyz.nameKey;
import static jborg.gtdForBash.ProjectJSONKeyz.statusKey;
import static jborg.gtdForBash.ProjectJSONKeyz.stepArrayKey;
import static jborg.gtdForBash.ProjectJSONToolBox.extractLDT;
import static jborg.gtdForBash.ProjectJSONToolBox.projectIsTerminated;
import static jborg.gtdForBash.ProjectJSONToolBox.stepDeadlineNone;
import static jborg.gtdForBash.SequenzesForISS.killPrjctNameNoDLDT;
import static jborg.gtdForBash.SequenzesForISS.modPrjctName;
import static jborg.gtdForBash.SequenzesForISS.newPrjctNoDLDT;
import static jborg.gtdForBash.SequenzesForISS.wakeProjectName;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import allgemein.LittleTimeTools;
import javafx.util.Pair;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;
import someMath.NaturalNumberException;

public class TestingTSDs
{

	
    static GTDCLI gtdCli;
    static Set<JSONObject> prjctSet;

	@BeforeEach
	void setup() throws JSONException, IOException, URISyntaxException, NaturalNumberException, InterruptedException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException
	{

		prjctSet = ProjectSetForTesting.get();

    	assert(!prjctSet.isEmpty());
	}

	@Test
	public void testOne()
	{
		
	}
	
	@Test
	public void timeSpanTests() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException
	{

        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();

        List<TimeSpanData> tspdListHours = tsc.getTimeSpanList(ChronoUnit.HOURS);
        List<TimeSpanData> tspdListDays = tsc.getTimeSpanList(ChronoUnit.DAYS);
        List<TimeSpanData> tspdListWeeks = tsc.getTimeSpanList(ChronoUnit.WEEKS);
        List<TimeSpanData> tspdListMonth = tsc.getTimeSpanList(ChronoUnit.MONTHS);
        List<TimeSpanData> tspdListYears = tsc.getTimeSpanList(ChronoUnit.YEARS);
 
        assert(tspdListHours.size()>=337&&tspdListHours.size()<=338);
        assert(tspdListDays.size()>=15&&tspdListDays.size()<=16);
        assert(tspdListWeeks.size()>=3&&tspdListWeeks.size()<=4);
        assert(tspdListMonth.size()>=1&&tspdListMonth.size()<=2);
        //printTSPList(tspdListMonth);
        assert(tspdListYears.size()>=1&&tspdListYears.size()<=2);     
        //printTSPList(tspdListYears);
	}
	
	@Test
	public void areWeeksWherePlacedRightTest() throws WeekDataException, IOException, URISyntaxException, NaturalNumberException, TimeSpanException, ToolBoxException, StatisticalToolsException, InterruptedException, TimeSpanCreatorException
	{
		makeUpProjectWithLaterNDTAndADT();

        StatisticalTools st = new StatisticalTools(prjctSet);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
        
        List<Pair<LocalDateTime, LocalDateTime>> wochen = tsc.createTimeSpanFrames(ChronoUnit.WEEKS);
        List<TimeSpanData> weekDatas = tsc.getTimeSpanList(ChronoUnit.WEEKS);
        
        for(Pair<LocalDateTime, LocalDateTime> pair: wochen)
        {
        	assert(pair.getKey().getDayOfWeek().equals(DayOfWeek.MONDAY));
        	assert(pair.getValue().getDayOfWeek().equals(DayOfWeek.SUNDAY));
        }
        
        int weeksSize = wochen.size();
        int firstWeekIndex = 0;
        assert((weeksSize==3)||(weeksSize==4));

        JSONObject pJSON = st.projectJSONObjByName(wakeProjectName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, wakeProjectName, firstWeekIndex, pJSON, NDTKey, st));
		
		pJSON = st.projectJSONObjByName(modPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, modPrjctName, firstWeekIndex, pJSON, NDTKey, st));
		
		String addNotePrjctName = SequenzesForISS.getNewProjectName(2);
		pJSON = st.projectJSONObjByName(addNotePrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, addNotePrjctName, firstWeekIndex, pJSON, ADTKey, st));

		pJSON = st.projectJSONObjByName(killPrjctNameNoDLDT);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killPrjctNameNoDLDT, firstWeekIndex, pJSON, ADTKey, st));
		
		String killPrjctName = SequenzesForISS.getNewProjectName(3);
		pJSON = st.projectJSONObjByName(killPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killPrjctName, firstWeekIndex, pJSON, ADTKey, st));
		assert(projectIsTerminated.test(pJSON));
		
		String killStepPrjctName = SequenzesForISS.getNewProjectName(3);
		pJSON = st.projectJSONObjByName(killStepPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killStepPrjctName, firstWeekIndex, pJSON, ADTKey, st));

		String appendStpPrjctName = SequenzesForISS.getNewProjectName(4);
		pJSON = st.projectJSONObjByName(appendStpPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, appendStpPrjctName, firstWeekIndex, pJSON, ADTKey, st));
	
		pJSON = st.projectJSONObjByName(newPrjctNoDLDT);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, newPrjctNoDLDT, firstWeekIndex, pJSON, ADTKey, st));

		for(int n=0;n<weeksSize;n++)
		{
	
			int projectsWritten =  weekDatas.get(n).getProjectsWrittenDown().size();
			int projectsActive = weekDatas.get(n).getActiveProjects().size();
			int projectsSucceeded = weekDatas.get(n).projectsSucceededThisTimeSpan().size();
			int projectsFailed =  weekDatas.get(n).projectsFailedThisTimeSpan().size();
			TimeSpanData tsd = weekDatas.get(n);
			
			System.out.println(tsd);
			System.out.println("Projects succeeded: " 
								+ tsd.projectsSucceededThisTimeSpan() + "\n");
			System.out.println("Projects failed: " 
								+ tsd.projectsFailedThisTimeSpan() + "\n");
			System.out.println("Most Pressing Deadline: " 
								+ tsd.mostPressingProjectDeadline() + "\n"
								+ tsd.toString());
			Thread.sleep(4000);

			if(n==0)
			{	
				assert(projectsWritten==8);
				assert(projectsActive==7);
				assert(projectsSucceeded==1); 
				assert(projectsFailed==1);  
			}

			if(n==1)
			{
				assert(projectsWritten==1);
				assert(projectsActive==0);
				assert(projectsSucceeded==0);
				assert(projectsFailed==0);
			}
		}
	}
	
	public void makeUpProjectWithLaterNDTAndADT()
	{
		
		JSONObject pJSON = new JSONObject();
		
		pJSON.put(nameKey, "One_Week_Later");
		pJSON.put(statusKey, StatusMGMT.atbd);
		pJSON.put(goalKey, "Testing.");
		
		LocalDateTime ldt = LocalDateTime.now().plusDays(7);
		String ldtStr =  LittleTimeTools.timeString(ldt);
		pJSON.put(NDTKey, ldtStr);
		pJSON.put(ADTKey, ldtStr);


		JSONObject sJSON = new JSONObject();
		sJSON.put(StepJSONKeyz.descKey, "Step by Step .. ooh baby!");
		sJSON.put(StepJSONKeyz.statusKey, StatusMGMT.atbd);
		sJSON.put(StepJSONKeyz.DLDTKey, stepDeadlineNone);
		ldt = ldt.plusSeconds(70);
		ldtStr = LittleTimeTools.timeString(ldt);
		sJSON.put(StepJSONKeyz.ADTKey, ldtStr);
		
		JSONArray steps = new JSONArray();
		steps.put(0, sJSON);
		pJSON.put(stepArrayKey, steps);

		prjctSet.add(pJSON);
		
	}
	public boolean pickAndCheckByName(ChronoUnit cu, String name, int unitNr, JSONObject pJSON, String jsonKey, StatisticalTools st) throws IOException, URISyntaxException, TimeSpanException
	{

		LocalDateTime ldt = extractLDT(pJSON, jsonKey);
		TimeSpanData tsd = st.tsc.getTimeSpanList(cu).get(unitNr);

		return tsd.isInThisTimeSpan(ldt);
	}
	
	public void printTSDList(List<TimeSpanData> list) throws InterruptedException
	{
        for(int n=0;n<list.size();n++)
        {
        	TimeSpanData tsd = list.get(n);
        	System.out.println(tsd);
        }
	}
}
