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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import allgemein.LittleTimeTools;
import consoleTools.InputStreamSession;
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

	@BeforeAll
	static void setup() throws JSONException, IOException, URISyntaxException, NaturalNumberException, InterruptedException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, ClassNotFoundException
	{

    	System.out.println("Test Setup//\\TestingTSDs");
    	Thread.sleep(1000);
    	
    	prjctSet = ProjectSetForTesting.get();

    	assert(!prjctSet.isEmpty());
    	
	}
	
	@Test
	public void pickUpTest() throws JSONException, ClassNotFoundException, IOException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException
	{
	    // Create a guaranteed-empty temp directory for all project data
        Path tempProjectDir = ProjectSetForTesting.getTempProjectDir();
        String tempPrjctDirStr = tempProjectDir.toString();
        System.out.println("Temp Dir: " + tempPrjctDirStr);

		String data = SomeCommands.exit;

		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        GTDCLI cli = new GTDCLI(iss, ProjectSetForTesting.getClock());
	}
	
	@Test
	public void timeSpanTests() throws IOException, URISyntaxException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException
	{

		List<List<TimeSpanData>> empty = new ArrayList<>();
		for(int n=0;n<5;n++)empty.add(new ArrayList<>());
        StatisticalTools st = new StatisticalTools(prjctSet, ProjectSetForTesting.getClock(), empty);
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

		List<List<TimeSpanData>> empty = new ArrayList<>();
		for(int n=0;n<5;n++)empty.add(new ArrayList<>());
        StatisticalTools st = new StatisticalTools(prjctSet, ProjectSetForTesting.getClock(), empty);
        TimeSpanCreator tsc = st.getTimeSpanCreator();
        
        LocalDateTime start = tsc.getBeginAnker();
        LocalDateTime stop = tsc.getEndAnker();
        
        System.out.println("Start Anker: " + start);
        System.out.println("Stop Anker: " + stop);
        //Thread.sleep(5000);

        List<Pair<LocalDateTime, LocalDateTime>> wochen = tsc.createTimeSpanFrames(ChronoUnit.WEEKS, start, stop);
        List<TimeSpanData> weekDatas = tsc.getTimeSpanList(ChronoUnit.WEEKS);
        int cnt=0;
        for(Pair<LocalDateTime, LocalDateTime> pair: wochen)
        {
        	assert(pair.getKey().getDayOfWeek().equals(DayOfWeek.MONDAY));
        	assert(pair.getValue().getDayOfWeek().equals(DayOfWeek.SUNDAY));
        	cnt++;
        }
        
        int weeksSize = wochen.size();
        int firstWeekIndex = 0;
        
        System.out.println("Weeksize: " + weeksSize);
        Thread.sleep(2000);
        assert(weeksSize==cnt);
        assert(weeksSize==3);

        JSONObject pJSON = st.projectJSONObjByName(wakeProjectName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, wakeProjectName, firstWeekIndex, pJSON, NDTKey, st));
		
		pJSON = st.projectJSONObjByName(modPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, modPrjctName, firstWeekIndex, pJSON, NDTKey, st));
		
		String addNotePrjctName = ProjectSetForTesting.getSqzFISS().getNewProjectName(2);
		pJSON = st.projectJSONObjByName(addNotePrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, addNotePrjctName, firstWeekIndex, pJSON, ADTKey, st));

		pJSON = st.projectJSONObjByName(killPrjctNameNoDLDT);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killPrjctNameNoDLDT, firstWeekIndex, pJSON, ADTKey, st));
		
		String killPrjctName = ProjectSetForTesting.getSqzFISS().getNewProjectName(3);
		pJSON = st.projectJSONObjByName(killPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killPrjctName, firstWeekIndex, pJSON, ADTKey, st));
		assert(projectIsTerminated.test(pJSON));
		
		String killStepPrjctName = ProjectSetForTesting.getSqzFISS().getNewProjectName(3);
		pJSON = st.projectJSONObjByName(killStepPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, killStepPrjctName, firstWeekIndex, pJSON, ADTKey, st));

		String appendStpPrjctName = ProjectSetForTesting.getSqzFISS().getNewProjectName(4);
		pJSON = st.projectJSONObjByName(appendStpPrjctName);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, appendStpPrjctName, firstWeekIndex, pJSON, ADTKey, st));
	
		pJSON = st.projectJSONObjByName(newPrjctNoDLDT);
		assert(pickAndCheckByName(ChronoUnit.WEEKS, newPrjctNoDLDT, firstWeekIndex, pJSON, ADTKey, st));

		for(int n=0;n<weeksSize;n++)
		{

			TimeSpanData tsd = weekDatas.get(n);

			List<String> pNames = tsd.mostPressingProjectDeadline()
										.stream()
										.map(p->p.getString(nameKey))
										.toList();

			System.out.println(tsd);
			System.out.println("Most Pressing Deadline: " + pNames + "\n");
			//Thread.sleep(4000);

//			System.out.println("Projects succeeded: " 
//								+ tsd.projectsSucceededThisTimeSpan() + "\n");
//			System.out.println("Projects failed: " 
//								+ tsd.projectsFailedThisTimeSpan() + "\n");
//			if(n==0)
//			{	
//				assert(projectsWritten==8);
//				assert(projectsActive==7);
//				assert(projectsSucceeded==1); 
//				assert(projectsFailed==1);  
//			}
//
//			if(n==1)
//			{
//				assert(projectsWritten==1);
//				assert(projectsActive==0);
//				assert(projectsSucceeded==0);
//				assert(projectsFailed==0);
//			}
		}
	}
	
	public void makeUpProjectWithLaterNDTAndADT()
	{
		
		JSONObject pJSON = new JSONObject();
		
		pJSON.put(nameKey, "One_Week_Later");
		pJSON.put(statusKey, StatusMGMT.atbd);
		pJSON.put(goalKey, "Testing.");
		
		LocalDateTime ldt = LocalDateTime.now(ProjectSetForTesting.getClock()).plusDays(7);
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