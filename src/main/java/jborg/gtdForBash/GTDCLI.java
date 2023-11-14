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
import java.util.function.Function;
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
	
	private final String statesFileName = "statusMGMT.states";
	private final StatusMGMT states = loadStates();
	
	private final String sayGoodBye = "Bye!";
	
	private final String unknownCmdStr = "Unknown command!";
	private final String dataFolderFound = "Data Folder found.";
	private final String thereIsNoDataFolder = "There is no Data Folder";
	private final String dataFolderCreated = "Data Folder created successfully.";
	private final String failedToCreateDirectory = "Failed to create the directory.";
	
	private final String projectStr = "Project";
	private final String noPrjctStr = "No Project.";
	private final String nearestDeadlineStr = "nearest Deadline of Last Steps.";
	private final String descStr = "Desc";
	private final String statusStr = "Status";
	private final String deadlineStr = "Deadline";
	
	private final String prjctNameStr = "Project Name";
	private final String bdtStr = "BDT";
	private final String nddtStr = "NDDT";
	private final String goalStr = "Goal";
	private final String stepsStr = "Steps";
	private final String notesStr = "Notes";
	
	private final String whichOnePhrase = "Which one?";
	private final String notesOfWhichPrjctPhrase = "Notes of which Project?";

	
	private final String chooseMoreWiselyPreFix = "Please choose more wisely. Because '";
	private final String itsNotOnTheListSuffix = "' is not on the List!";
	private final String hasNoNotesSuffix = " has no Notes.";
	
	private final String nrOfPrjctsStr = "Nr. of Projects: ";
	private final String nrOfActivePrjctsStr = "Nr. of active Projects: ";
	private final String nrOfMODPrjctsStr = "Nr. of Mod Projects: ";
	private final String nrOfSuccessStpsStr = "Nr. of Success Steps: ";
	private final String nrOfSuccessPrjctsStr = "Nr. of Success Projects: ";
	private final String noActivePrjctsStr = "No active Projects.";
	
	
	private static final String projectDataFolderRelativePath = "projectDATA/";
	
	private final String thereIsAStatesFileLoading = "There is a States File. Trying to Load it";
	private final String somethinWrongStates = "Something is wrong. Using default States.";
	private final String thereAreNoStates = "There is no States File using default States.";
	
	private final char wallOfTableChr = '|';
	public final int jsonPrintStyle = 4;
	public final String fileMarker = ".prjct";
	public final String modPrjctMarker = ".MODPrjct";
	
	private Map<String, JSONObject> projectMap = new HashMap<>();
	private Map<String, JSONObject> modProjectMap = new HashMap<>();
	private Map<String, JSONObject> knownProjects = new HashMap<>();

	public static final String isModProjectQ = "Maybe one Day-Project?(yes) or are we actually try "
			+ "to do it soon enough?(no): ";

	public final String newPrjctStgClsd = "New Project Stage closed.";
	public final String tdtNoteStpDLDTAbuse = "Step Deadline abuse!";
	public final String tdtNotePrjctDLDTAbuse = "Project Deadline abuse!";

	private List<String> columnList = Arrays.asList("Name", "Status", "BDT", "Age");
	
	private final String save = "save";
	private final String exit = "exit";
	private final String list_not_active_ones = "list not active ones";
	private final String list_active_ones = "list active ones";
	private final String list_mod_Projects = "list mod projects";
	//private static final String correct_last_step = "correct last step";//TODO
	private final String view_Project = "view Project";
	private final String view_last_steps_of_Projects = "last steps";
	private final String view_nearest_Deadline = "nearest deadline";
	private final String view_statistics = "view stats";
	private final String next_Step = "next step";
	private final String list = "list";
	private final String help = "help";
	private final String new_Project ="new project";
	private final String list_commands = "list cmds";
	private final String terminate_Project = "terminate project";
	private final String add_Note = "add note";
	private final String view_Notes = "show Notes";
	
	private final Set<String> commands = new HashSet<>(Arrays.asList(save, exit, list, help, 
			list_active_ones, list_mod_Projects,/* correct_last_step, */ view_Project, 
			view_last_steps_of_Projects, view_nearest_Deadline, view_statistics, list_not_active_ones,
			new_Project, next_Step, list_commands, terminate_Project, add_Note, view_Notes));

	private final Set<String> prjctModifierCommands = new HashSet(Arrays.asList(new_Project, next_Step, 
			terminate_Project, add_Note));
	
	private final Set<String> showDataCommands = new HashSet(Arrays.asList(list, list_active_ones,
			list_mod_Projects, view_Project, view_last_steps_of_Projects, view_nearest_Deadline, view_statistics,
			list_not_active_ones, list_commands, view_Notes));
	
	private final Set<String> otherCommands = new HashSet(Arrays.asList(save, exit, help));
	
	private final Map<String, CLICommand> commandMap = new HashMap<>();
	
	private CLICommand<String, JSONObject> np;
	private CLICommand<String, String> byebye;
	
	/*TODO: some only need Testing.
	 * 
	 * 


	 * 
	 * 
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
    	initComands();
		loadProjects();
		loadMODProjects();
	
		knownProjects.putAll(projectMap);
		knownProjects.putAll(modProjectMap);
		
		checkAllForDLDTAbuse();
	
		greetings();
		
		loopForCommands();
    }
    
    /* TODO *///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private void initComands()
    {
    
    	Function<String, JSONObject> newProject = (s)->
		{
			
			try
			{
				JSONObject pJSON = ds.spawnNewProject(knownProjects.keySet(), states);
				
				
				String name = pJSON.getString(ProjectJSONKeyz.nameKey);
				knownProjects.put(name, pJSON);
				projectMap.put(name, pJSON);
				
				return pJSON;

				/*
				if(pJSON.getString(ProjectJSONKeyz.statusKey).equals(StatusMGMT.mod))
				{
					modProjectMap.put(name, pJSON);
				}
				*/

			} 
			catch(InputMismatchException e) 
			{
				e.printStackTrace();
			}
			catch(SpawnProjectException e)
			{
				e.printStackTrace();
			}
			catch(TimeGoalOfProjectException e)
			{
				e.printStackTrace();
			}
			catch(SpawnStepException e)
			{
				e.printStackTrace();
			}
			catch(IOException e) 
			{
				e.printStackTrace();
			}
			
			System.out.println(noPrjctStr);
			return null;
		};

		boolean mustHaveArgument = false;
		boolean canHaveArgument = false;
		boolean mustHaveOutput = true;
		boolean canHaveOutput = false;
		
		np = new CLICommand<String, JSONObject>(new_Project, mustHaveArgument, canHaveArgument, mustHaveOutput, canHaveOutput, newProject);
		
		commandMap.put(new_Project, np);
		commands.add(new_Project);
		prjctModifierCommands.add(new_Project);
		
		Function<String, String> leave = (s)->
		{
			try 
			{
				stop();
			} 
			catch (JSONException | IOException | URISyntaxException e)
			{
				System.out.println("Nothing saved!!\n" + e);
				System.exit(0);
			}
			
			return null;
		};

		mustHaveArgument = false;
		canHaveArgument = false;
		mustHaveOutput = false;
		canHaveOutput = false;
		
		byebye = new CLICommand<String, String>(exit, mustHaveArgument, canHaveArgument, mustHaveOutput, canHaveOutput, leave);
		
		commandMap.put(exit, byebye);
		commands.add(exit);
		prjctModifierCommands.add(exit);
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
    	
    	for(String s: commandMap.keySet())
    	{
    		if(command.startsWith(s));
    		CLICommand clicmd = commandMap.get(s);
    		
   			String argument = getArgumentOfCommand(command, s);
   			if(!argument.trim().equals("")&&clicmd.cantHaveArgument)throw new IllegalArgumentException("This command can't have Arguments.");
   			Object obj = clicmd.executeCmd(argument);
    	}
    	
    	loopForCommands();
    }
    
    public String getArgumentOfCommand(String command, String prefix)
    {
    	
    	String argument = "";
    	
   		int l = prefix.length();
   		argument = command.substring(l);
 
    	return argument;
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
    
    public void showProjectDetail(JSONObject pJSON)
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
    
    private StatusMGMT loadStates()
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
    
    public boolean isOtherCommand(String command)
    {
    	
    	for(String s: otherCommands)
    	{
    		if(command.startsWith(s))return true;
    	}
    	
    	return false;
    }
    
    public boolean isModifierCommand(String command)
    {

    	for(String s: prjctModifierCommands)
    	{
    		if(command.startsWith(s))return true;
    	}
    	
    	return false;
    }
    
    public boolean isShowDataCommand(String command)
    {

    	
    	for(String s: showDataCommands)
    	{
    		if(command.startsWith(s))return true;
    	}
    	    	
    	return false;
    }
    
    private boolean isMODProject(JSONObject pJSON)
    {
    	
    	String status = pJSON.getString(ProjectJSONKeyz.statusKey);
    	
    	return status.equals(StatusMGMT.mod);
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