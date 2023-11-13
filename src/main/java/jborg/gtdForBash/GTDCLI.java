package jborg.gtdForBash;


import java.io.File;
import java.io.IOException;


import java.net.URISyntaxException;


import java.nio.file.Files;
import java.nio.file.Path;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.function.Predicate;
import java.util.HashMap;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import allgemein.Beholder;

import allgemein.LittleTimeTools;


import consoleTools.BashSigns;

import consoleTools.InputStreamSession;

import consoleTools.TerminalTableDisplay;
/* */

import static fileShortCuts.TextAndObjSaveAndLoad.*;


public class GTDCLI implements Beholder<String>
{
	
	private static final String statesFileName = "statusMGMT.states";
	private static final StatusMGMT states = loadStates();
	
	private static final String sayGoodBye = "Bye!";
	
	private static final String unknownCmdStr = "Unknown command!";
	private static final String dataFolderFound = "Data Folder found.";
	private static final String thereIsNoDataFolder = "There is no Data Folder";
	private static final String dataFolderCreated = "Data Folder created successfully.";
	private static final String failedToCreateDirectory = "Failed to create the directory.";
	
	private static final String projectStr = "Project";
	private static final String noPrjctStr = "No Project.";
	private static final String nearestDeadlineStr = "nearest Deadline of Last Steps.";
	private static final String descStr = "Desc";
	private static final String statusStr = "Status";
	private static final String deadlineStr = "Deadline";
	
	private static final String prjctNameStr = "Project Name";
	private static final String bdtStr = "BDT";
	private static final String nddtStr = "NDDT";
	private static final String goalStr = "Goal";
	private static final String stepsStr = "Steps";
	private static final String notesStr = "Notes";
	
	private static final String whichOnePhrase = "Which one?";
	private static final String notesOfWhichPrjctPhrase = "Notes of which Project?";

	
	private static final String chooseMoreWiselyPreFix = "Please choose more wisely. Because '";
	private static final String itsNotOnTheListSuffix = "' is not on the List!";
	private static final String hasNoNotesSuffix = " has no Notes.";
	
	private static final String nrOfPrjctsStr = "Nr. of Projects: ";
	private static final String nrOfActivePrjctsStr = "Nr. of active Projects: ";
	private static final String nrOfMODPrjctsStr = "Nr. of Mod Projects: ";
	private static final String nrOfSuccessStpsStr = "Nr. of Success Steps: ";
	private static final String nrOfSuccessPrjctsStr = "Nr. of Success Projects: ";
	private static final String noActivePrjctsStr = "No active Projects.";
	
	
	private static final String projectDataFolderRelativePath = "projectDATA/";
	
	private static final String thereIsAStatesFileLoading = "There is a States File. Trying to Load it";
	private static final String somethinWrongStates = "Something is wrong. Using default States.";
	private static final String thereAreNoStates = "There is no States File using default States.";
	
	private static final char wallOfTableChr = '|';
	public final static int jsonPrintStyle = 4;
	public final static String fileMarker = ".prjct";
	public final static String modPrjctMarker = ".MODPrjct";
	
	private Map<String, JSONObject> projectMap = new HashMap<>();
	private Map<String, JSONObject> modProjectMap = new HashMap<>();
	private Map<String, JSONObject> knownProjects = new HashMap<>();

	public static final String isModProjectQ = "Maybe one Day-Project?(yes) or are we actually try "
			+ "to do it soon enough?(no): ";

	public final static String newPrjctStgClsd = "New Project Stage closed.";
	public final static String tdtNoteStpDLDTAbuse = "Step Deadline abuse!";
	public final static String tdtNotePrjctDLDTAbuse = "Project Deadline abuse!";

	private List<String> columnList = Arrays.asList("Name", "Status", "BDT", "Age");
	
	private static final String save = "save";
	private static final String exit = "exit";
	private static final String list_not_active_ones = "list not active ones";
	private static final String list_active_ones = "list active ones";
	private static final String list_mod_Projects = "list mod projects";
	//private static final String correct_last_step = "correct last step";//TODO
	private static final String view_Project = "view Project";
	private static final String view_last_steps_of_Projects = "last steps";
	private static final String view_nearest_Deadline = "nearest deadline";
	private static final String view_statistics = "view stats";
	private static final String next_Step = "next step";
	private static final String list = "list";
	private static final String help = "help";
	private static final String new_Project ="new project";
	private static final String list_commands = "list cmds";
	private static final String terminate_Project = "terminate project";
	private static final String add_Note = "add note";
	private static final String view_Notes = "show Notes";
	
	private static final Set<String> commands = new HashSet<>(Arrays.asList(save, exit, list, help, 
			list_active_ones, list_mod_Projects,/* correct_last_step, */ view_Project, view_last_steps_of_Projects, 
			view_nearest_Deadline, view_statistics, list_not_active_ones, new_Project, next_Step, list_commands, 
			terminate_Project, add_Note, view_Notes));


	/*TODO: some only need Testing.
	stepsView -> show all Last Steps or all Steps of one Project
	
	JSONView -> View JSONObject of a Project.
		
	noteView -> view Notes of a Prjct.
    	
    Statistics -> Think about Stats.
	 */

	private final GTDDataSpawnSession ds;
	
	
	public final Predicate<JSONObject> activeProject = (jo)->
	{
		
		if(!projectMap.containsValue(jo))return false;
		
		String status = jo.getString(ProjectJSONKeyz.statusKey);
		
		if(!states.getStatesOfASet(StatusMGMT.terminalSetName).contains(status))return true;

		return false;
	};

	public final Predicate<JSONObject> notActiveProject = (jo)-> 
	{
		if(!projectMap.containsValue(jo))return false;//Is a must!!
		
		return !activeProject.test(jo);
	};
	
	public final Predicate<String> activePrjctName = (s)->
	{
		if(!projectMap.containsKey(s)) return false;
		
		JSONObject pJSON = projectMap.get(s);
		
		return activeProject.test(pJSON);
	};
	
	public final Predicate<String> notActivePrjctName = (s)->
	{
		if(!projectMap.containsKey(s)) return false;//Is a must!!
		
		return !activePrjctName.test(s);
	};

	private final InputStreamSession iss;
		
    public GTDCLI(InputStreamSession iss) throws IOException, URISyntaxException, InputMismatchException, JSONException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
	{
    	
    	this.iss = iss;

    	ds = new GTDDataSpawnSession(this.iss);
    	Path p = Path.of(getPathToDataFolder());
    	
		boolean isThereDataFolder = Files.exists(p)&&Files.isDirectory(p);

		if(isThereDataFolder)
		{
			System.out.println(dataFolderFound);
			boot();
		}
		else 
		{
			System.out.println(thereIsNoDataFolder);
			String directoryPath = getPathToDataFolder();

	        File directory = new File(directoryPath);

	        // Create the directory
	        if (directory.mkdir())
	        {
	            System.out.println(dataFolderCreated);
	            boot();
	        }
	        else
	        {
	            System.out.println(failedToCreateDirectory);
	            System.out.println(sayGoodBye);
	        }
		}
	}
    
    private void boot() throws IOException, URISyntaxException, InputMismatchException, JSONException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
    {
    	
		loadProjects();
		loadMODProjects();
	
		knownProjects.putAll(projectMap);
		knownProjects.putAll(modProjectMap);
		
		checkAllForDLDTAbuse();
	
		greetings();
		
		loopForCommands();
    }
    
    public void greetings()
    {
    	LocalDateTime inTheMoment = LocalDateTime.now();
    	String day = inTheMoment.getDayOfWeek().toString();
    	int day2 = inTheMoment.getDayOfMonth();
    	int year = inTheMoment.getYear();
    	String time = LittleTimeTools.timeString(inTheMoment.toLocalTime());
    	
    	System.out.println("Hello, it is " + day + " the " + day2 + " in the Year "+year);
    	System.out.println("Time: " + time + '\n');
    }
    
    public static void main(String... args) throws IOException, URISyntaxException, InputMismatchException, JSONException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
    {
    	new GTDCLI(new InputStreamSession(System.in));
    }
    
    public void loopForCommands() throws InputMismatchException, IOException, JSONException, URISyntaxException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
    {
    	
    	String px = BashSigns.boldBBCPX;
    	String sx = BashSigns.boldBBCSX;
    	
    	String command = iss.getString(px + "Type" + sx + " command. (ex. help or exit).");
    	command = command.trim();
    	
    	switch(command)
    	{
    	
    		case view_nearest_Deadline:
    		{
    			LocalDateTime jetzt = LocalDateTime.now();
    			long newMinutes = 1000000;
    			List<String> prjctList = new ArrayList<>();
    			List<String> dauerList = new ArrayList<>();
    			
    			for(JSONObject pJSON: projectMap.values())
    			{
    				
    				String prjctName = pJSON.getString(ProjectJSONKeyz.nameKey);
    				
    				JSONObject lastStep = getLastStep(pJSON);
    				
    				String dldt = lastStep.getString(StepJSONKeyz.DLDTKey);
    				LocalDateTime ldtDLDT = LittleTimeTools.LDTfromTimeString(dldt);
    				
    				String dauer = LittleTimeTools.fullLDTBetweenLDTs(ldtDLDT, jetzt);
    				//if("".equals(dauer.trim()))dauer = LittleTimeTools.fullLDTBetweenLDTs(jetzt, ldtDLDT);
    				
    				long minutes = jetzt.until(ldtDLDT, ChronoUnit.MINUTES);
    				boolean isNearer = (Math.abs(minutes)<Math.abs(newMinutes));
    				boolean isEqual = Math.abs(minutes)==Math.abs(newMinutes);
    				    				
    				if(isEqual)
    				{
    					prjctList.add(prjctName);
    					dauerList.add(dauer);
    				}

    				
    				if(isNearer)
    				{
    					newMinutes=minutes;
    					
    					prjctList.clear();
    					prjctList.add(prjctName);
    					dauerList.clear();
    					dauerList.add(dauer); 
    				}
    			}
    			
    			List<List<String>> rows = new ArrayList<>();
    			
    			int l = prjctList.size();
    			for(int n=0;n<l;n++)
    			{
    				List<String> row = new ArrayList<>();

    				row.add(prjctList.get(n));
    				row.add(dauerList.get(n));
    				
    				rows.add(row);
    			}

    			List<String> headers = new ArrayList<>(Arrays.asList(projectStr, nearestDeadlineStr));
    			TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows,'|', 18);
    			System.out.println(ttd);
    			
    			break;
    		}
    		
    		case view_last_steps_of_Projects:
    		{
    			
    			List<String> headers = new ArrayList<>(Arrays.asList(projectStr, descStr, statusStr, deadlineStr));
    			List<List<String>> rows = new ArrayList<>();

    			for(JSONObject pJSON: projectMap.values())
    			{
    				String prjctName = pJSON.getString(ProjectJSONKeyz.nameKey);
    				
    				JSONObject lastStep = getLastStep(pJSON);
    				String desc = lastStep.getString(StepJSONKeyz.descKey);
    				String status = lastStep.getString(StepJSONKeyz.statusKey);
    				String dldt = lastStep.getString(StepJSONKeyz.DLDTKey);
    				
    				List<String> row = new ArrayList<>();
    				row.add(prjctName);
    				row.add(desc);
    				row.add(status);
    				row.add(dldt);
    				
    				rows.add(row);
    			}
    			
    			
    			TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows,'|', 12);
    			System.out.println(ttd);
    			
    			break;

    		}
    		
    		case view_Project:
    		{
    			System.out.println("");
    			List<String> names = new ArrayList<>();
    			names.addAll(knownProjects.keySet());
 
    			String choosenOne = iss.getAnswerOutOfList(whichOnePhrase, names);
    			String prjct = choosenOne.trim();
    			if(knownProjects.keySet().contains(prjct))showProjectDetail(knownProjects.get(prjct));
    			else System.out.println(chooseMoreWiselyPreFix + choosenOne + itsNotOnTheListSuffix);
    			break;

    		}
    		
    		case view_statistics:
    		{
    			int nrOfPrjcts = knownProjects.size();
    			int nrOfActivePrjcts = findProjectsByCondition(activeProject).size();
    			int nrOfModPrjcts = modProjectMap.size();
    			
    			int nrOfSuccessfulSteps = 0;
    			int nrOfSuccessfulPrjcts = 0;
    			for(JSONObject pJSON: projectMap.values())
    			{
    				
    				String prjctStatus = pJSON.getString(ProjectJSONKeyz.statusKey);
    				if(prjctStatus.equals(StatusMGMT.success))nrOfSuccessfulPrjcts++;
    				
    				JSONArray steps = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
    				int i = steps.length();
    				for(int n=0;n<i;n++)
    				{
    					JSONObject step = steps.getJSONObject(n);
    					String stepStatus = step.getString(StepJSONKeyz.statusKey);
    					
    					if(stepStatus.equals(StatusMGMT.success))nrOfSuccessfulSteps++;
    				}
    			}
    			
    			System.out.println(nrOfPrjctsStr + nrOfPrjcts);
    			System.out.println(nrOfActivePrjctsStr + nrOfActivePrjcts);
    			System.out.println(nrOfMODPrjctsStr + nrOfModPrjcts);
    			System.out.println(nrOfSuccessStpsStr + nrOfSuccessfulSteps);
    			System.out.println(nrOfSuccessPrjctsStr + nrOfSuccessfulPrjcts);
    		}
    		
    		case save:
    		{
    			saveAll();
    			break;
    		}
    			
    		case exit: stop();//No break needed.
    		
    		case add_Note:
    		{
    			
    			System.out.println("");
    			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
    			if(aPrjcts.isEmpty())
    			{
    				System.out.println(noActivePrjctsStr);
    				break;
    			}
    			
    			String pName = iss.getAnswerOutOfList(whichOnePhrase, aPrjcts);
    			if(aPrjcts.contains(pName))
    			{
    				JSONObject pJSON = projectMap.get(pName);
    				checkForDeadlineAbuse(pJSON);
    				ds.addNote(pJSON);
    			}
    			break;
    		}
    		
    		case view_Notes:
    		{
    			System.out.println("");
    			List<String> names = new ArrayList<>();
    			names.addAll(knownProjects.keySet());
 
    			String choosenOne = iss.getAnswerOutOfList(notesOfWhichPrjctPhrase, names);
    			
    			JSONObject pJSON = knownProjects.get(choosenOne);
    			
    			JSONArray noteArr;
    			if(pJSON.has(ProjectJSONKeyz.noteArrayKey))
    			{
    				noteArr = pJSON.getJSONArray(ProjectJSONKeyz.noteArrayKey);
    				int l = noteArr.length();
    				
    				for(int n=0;n<l;n++)
    				{
    					System.out.println("--> " + noteArr.get(n));
    				}
    			}
    			else System.out.println(projectStr + " " + choosenOne + hasNoNotesSuffix);
    			
    			break;
    		}
    		
    		case terminate_Project:
    		{
    			
    			System.out.println("");
    			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
    			if(aPrjcts.isEmpty())
    			{
    				System.out.println(noActivePrjctsStr);
    				break;
    			}
    			
    			String pName = iss.getAnswerOutOfList(whichOnePhrase, aPrjcts);
    			if(aPrjcts.contains(pName))
    			{
    				JSONObject pJSON = projectMap.get(pName);
    				checkForDeadlineAbuse(pJSON);
    				JSONObject sJSON = ds.getLastStepOfProject(pJSON);
    				ds.terminateStep(sJSON);  				
    				ds.terminateProject(pJSON);
    			}
    			break;
    		}
    		
    		case list_commands:
    		{
    			System.out.println("");
    			for(String cmds: commands)System.out.println(cmds);
    			System.out.println("");
    			break;
    		}
    		
    		case list_not_active_ones:
    		{
    			System.out.println("");
    			
    			Map<String, JSONObject> map = new HashMap<>();
    			List<String> noAPrjcts = findProjectNamesByCondition(notActivePrjctName);
    			for(String prjctName: noAPrjcts)
    			{
    				JSONObject pJSON = knownProjects.get(prjctName);
    				map.put(prjctName, pJSON);
    			}
    			showProjectMapAsTable(map);
    			
    			break;
    		}
    		
    		case list_mod_Projects:
    		{
    			showProjectMapAsTable(modProjectMap);
    			break;
    		}
 
    		case list_active_ones:
    		{
    			System.out.println("");
    			showProjectMapAsTable(projectMap);
    			
    			Map<String, JSONObject> map = new HashMap<>();
    			
    			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
    			for(String prjctName: aPrjcts)
    			{
    				JSONObject pJSON = projectMap.get(prjctName);
    				map.put(prjctName, pJSON);
    			}

    			showProjectMapAsTable(map);
    			
    			break;
    		}

    		case next_Step:
    		{
    			System.out.println("");
    			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
    			if(aPrjcts.isEmpty())
    			{
    				System.out.println(noActivePrjctsStr);
    				break;
    			}
    			String choosenOne = iss.getAnswerOutOfList(whichOnePhrase, aPrjcts);
    			String prjct = choosenOne.trim();
    			if(aPrjcts.contains(prjct))
    			{
    				JSONObject pJSON = knownProjects.get(prjct);
    				checkForDeadlineAbuse(pJSON);
    				nxtStp(pJSON);
    			}
    			else System.out.println(chooseMoreWiselyPreFix + choosenOne + itsNotOnTheListSuffix);
    			break;
    		}
    
    		case list: 
    		{
    			System.out.println("");
    			showProjectMapAsTable(knownProjects);
    			break;
    		}
    	
    		case help: 
    		{
    			System.out.println("");
    			System.out.println("Not yet Installed.");//TODO:
    			break;
    		}
    		
    		case new_Project:
    		{
    			System.out.println("");
    			boolean isModProject = iss.getYesOrNo(isModProjectQ);		

    			
    			JSONObject pJson;
    			if(isModProject)pJson= ds.spawnMODProject(knownProjects.keySet(), states);
    			pJson= ds.spawnNewProject(knownProjects.keySet(), states);
    			
    			if(pJson!=null)
    			{
    				String name = pJson.getString(ProjectJSONKeyz.nameKey);
    				knownProjects.put(name, pJson);
    				
    				if(pJson.getString(ProjectJSONKeyz.statusKey).equals(StatusMGMT.mod))
					{
    					modProjectMap.put(name, pJson);
					}
    				else projectMap.put(name, pJson);
    			}
    			else System.out.println(noPrjctStr);
    			break;
    		}    		
    		default:
    		{
    			System.out.println(unknownCmdStr);
    		}
    	}
    	
    	loopForCommands();
    }
    

    public List<String> findProjectNamesByCondition(Predicate<String> condition)
    {
    	List<String> output = new ArrayList<>();
    	
    	for(String projectName: knownProjects.keySet())if(condition.test(projectName))output.add(projectName);
    	
    	return output;
    }
    
    public List<JSONObject> findProjectsByCondition(Predicate<JSONObject> condition)
    {
    	
    	List<JSONObject> output = new ArrayList<>();
    	
    	for(JSONObject pJSON: knownProjects.values())if(condition.test(pJSON))output.add(pJSON);
    	
    	return output;
    }

    public void showProjectMapAsTable(Map<String, JSONObject> map) throws JSONException
    {
		List<String> headers = columnList;
		List<List<String>> rows = new ArrayList<>();
		
    	for(JSONObject jo: map.values())
    	{
    		String name = jo.getString(ProjectJSONKeyz.nameKey);
    		String status = (String) jo.getString(ProjectJSONKeyz.statusKey);
    		String bdt = (String) jo.getString(ProjectJSONKeyz.BDTKey);

    		LocalDateTime jetzt = LocalDateTime.now();
    		LocalDateTime ldtBDT = LittleTimeTools.LDTfromTimeString(bdt);
    		String age = LittleTimeTools.fullLDTBetweenLDTs(ldtBDT, jetzt);	

    		List<String> row = new ArrayList<>();
    		row.add(name);
    		row.add(status);
    		row.add(bdt);
    		row.add(age);
    		
    		rows.add(row);
    	}
    	
		TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows, wallOfTableChr, 20);
		System.out.println(ttd);
    }
    
    public static JSONObject getLastStep(JSONObject pJSON)
    {
    	JSONArray stepArr = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
    	int l = stepArr.length();
    	
    	return stepArr.getJSONObject(l-1);
    }
    
    public static void showProjectDetail(JSONObject pJSON)
    {
    	String gpx = BashSigns.boldGBCPX;
    	String gsx = BashSigns.boldGBCSX;

    	String name = pJSON.getString(ProjectJSONKeyz.nameKey);
    	String status = pJSON.getString(ProjectJSONKeyz.statusKey);
    	String bdt = pJSON.getString(ProjectJSONKeyz.BDTKey);
    	String nddt = pJSON.getString(ProjectJSONKeyz.NDDTKey);
    	String goal = pJSON.getString(ProjectJSONKeyz.goalKey);

    	int stpNr = 0;
    	if(!status.equals(StatusMGMT.mod))
    	{
    		JSONArray jArray = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
    		stpNr = jArray.length();
    	}
    	
    	int noteNr = 0;
    	if(pJSON.has(ProjectJSONKeyz.noteArrayKey))
    	{
    		JSONArray jArray = pJSON.getJSONArray(ProjectJSONKeyz.noteArrayKey);
    		noteNr = jArray.length();
    	}
    	
    	
    	String dldtStr = pJSON.getString(ProjectJSONKeyz.DLDTKey);

    	System.out.println(gpx + prjctNameStr + ":" + gsx + " "+ name);
    	System.out.println(gpx + statusStr + ":" + gsx + " " + status);
    	System.out.println(gpx + bdtStr + ":" + gsx + " " + bdt);
    	System.out.println(gpx + nddtStr + ":" + gsx + " " + nddt);
    	System.out.println(gpx + deadlineStr + ":" + gsx + " " + dldtStr);
    	System.out.println(gpx + goalStr + ":" + gsx + " " + goal);
    	System.out.println(gpx + stepsStr + ":" + gsx + " " + stpNr);
    	System.out.println(gpx + notesStr + ":" + gsx + " " + noteNr);
    	
    }
    
	public void nxtStp(JSONObject pJSON) throws InputMismatchException, SpawnStepException, IOException
    {
    	ds.spawnStep(pJSON);
    }
  
    private static String getPathToDataFolder()
    {
    	return projectDataFolderRelativePath;
    }
       
    private void checkAllForDLDTAbuse()
    {
    	for(JSONObject pJSON: projectMap.values())
    	{
    		checkForDeadlineAbuse(pJSON);
    	}
    }
    
    private void checkForDeadlineAbuse(JSONObject pJSON)
    {
    		
    	StatusMGMT sm = StatusMGMT.getInstance();

    	String prjctStatus = pJSON.getString(ProjectJSONKeyz.statusKey);
    	Set<String> terminalSet = sm.getStatesOfASet(StatusMGMT.terminalSetName);
    		
    	if(terminalSet.contains(prjctStatus))return;

    	LocalDateTime jetzt = LocalDateTime.now();
			
		JSONObject step = getLastStep(pJSON);
			
    	String stepStatus = pJSON.getString(ProjectJSONKeyz.statusKey);
    		
    	String dldtStr = step.getString(StepJSONKeyz.DLDTKey);
    	LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
    			
    	if(dldt.isBefore(jetzt)&&!terminalSet.contains(stepStatus))//Is Step DLDT abused?
    	{
        	step.put(StepJSONKeyz.statusKey, StatusMGMT.failed);
        	pJSON.put(ProjectJSONKeyz.statusKey, StatusMGMT.needsNewStep);
        			
        	step.put(StepJSONKeyz.TDTKey, dldtStr);
        	step.put(StepJSONKeyz.TDTNoteKey, tdtNoteStpDLDTAbuse);    
    	}   					

    	String projectDLDTStr = pJSON.getString(ProjectJSONKeyz.DLDTKey);
    	LocalDateTime projectDLDT = LittleTimeTools.LDTfromTimeString(projectDLDTStr);

    	if(projectDLDT.isBefore(jetzt))//Is Project DLDT abused?
    	{
    		step.put(StepJSONKeyz.statusKey, StatusMGMT.failed);
    		pJSON.put(ProjectJSONKeyz.statusKey, StatusMGMT.failed);
    		step.put(StepJSONKeyz.TDTKey, projectDLDTStr);
    		pJSON.put(ProjectJSONKeyz.TDTKey, projectDLDTStr);
    			
    		step.put(StepJSONKeyz.TDTNoteKey, tdtNotePrjctDLDTAbuse);
    		pJSON.put(ProjectJSONKeyz.TDTNoteKey, tdtNotePrjctDLDTAbuse);
    	}
    }


    /* TODO
    private void eraseMODProjectFile(String prjctName)
    {

    	File[] listOfFiles = getListOfFilesFromDataFolder();
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile()&&file.getName().equals(prjctName+modPrjctMarker))
    			file.delete();
    	}
    }
    */
    
    private void loadMODProjects() throws IOException
    {
    	
    	String path = getPathToDataFolder();

    	File[] listOfFiles = getListOfFilesFromDataFolder();

    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.endsWith(modPrjctMarker))
    		{
    			
    			String joText = loadText(path + name);
    			
    			JSONObject jo = new JSONObject(joText);
    			
    			modProjectMap.put(jo.getString(ProjectJSONKeyz.nameKey), jo);
    		}
    	}
    }
    
    private static StatusMGMT loadStates()
    {
    	
    	Path p = Path.of(getPathToDataFolder()+statesFileName);
    	
    	boolean thereAreStates = (Files.exists(p));
    	StatusMGMT tmpStates;
    	
    	if(thereAreStates)
    	{
    		
    		System.out.println(thereIsAStatesFileLoading);
    		
    		try
    		{
				tmpStates =(StatusMGMT) loadObject(p);
				return tmpStates;
			}
    		catch (ClassNotFoundException | IOException e)
    		{
				e.printStackTrace();
				System.out.println(somethinWrongStates);
				return StatusMGMT.getInstance();
			}
    	}
    	
    	System.out.println(thereAreNoStates);
    	return StatusMGMT.getInstance();
    }

    private void loadProjects() throws IOException, URISyntaxException
    {
    	
    	String path = getPathToDataFolder();

    	File[] listOfFiles = getListOfFilesFromDataFolder();

    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.endsWith(fileMarker))
    		{
    			
    			String joText = loadText(path + name);
    			
    			JSONObject jo = new JSONObject(joText);
    			
    			projectMap.put(jo.getString(ProjectJSONKeyz.nameKey), jo);
    		}
    	}
    }
    
    private File[] getListOfFilesFromDataFolder()
    {
    	
    	String path = getPathToDataFolder();
    	
    	File folder = new File(path);

    	File[] listOfFiles = folder.listFiles();
    	
    	return listOfFiles;
    }
    
    private void saveMODProjects() throws JSONException, IOException
    {
    	
    	for(JSONObject jo: modProjectMap.values())
    	{
    		String path = getPathToDataFolder();
    		saveText(path + jo.getString(ProjectJSONKeyz.nameKey)+modPrjctMarker, jo.toString(jsonPrintStyle));
    	}
    }
    
    private void saveProjects() throws JSONException, IOException, URISyntaxException
    {
    	for(JSONObject jo: projectMap.values())
    	{
    		String path = getPathToDataFolder();
    		saveText(path + jo.getString(ProjectJSONKeyz.nameKey)+fileMarker, jo.toString(jsonPrintStyle));
    	}
    }
     
    private void saveStatusMGMT() throws IOException
    {
    	saveObject(getPathToDataFolder()+statesFileName, StatusMGMT.getInstance());
    }

    public void saveAll() throws JSONException, IOException, URISyntaxException
    { 	
    	saveProjects();
    	saveMODProjects();
    	saveStatusMGMT();
    }
    
    public void stop() throws JSONException, IOException, URISyntaxException
    {

    	saveAll();
    	System.out.println(sayGoodBye);
    	System.exit(0);
    }

	@Override
	public void refresh(String arg0) 
	{
		// TODO Auto-generated method stub
		
	}
}