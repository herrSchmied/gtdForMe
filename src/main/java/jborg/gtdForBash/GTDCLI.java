package jborg.gtdForBash;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.HashMap;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import allgemein.Beholder;

import allgemein.LittleTimeTools;
import consoleTools.InputStreamSession;
import consoleTools.TerminalTableDisplay;
import fileShortCuts.LoadAndSave;


public class GTDCLI implements Beholder<String>
{
	
	private static final String statesFileName = "statusMGMT.states";
	private StatusMGMT states;
	
	public final static int jsonPrintStyle = 4;
	public final static String fileMarker = ".prjct";
	public final static String modPrjctMarker = ".MODPrjct";
	
	private Map<String, JSONObject> projectMap = new HashMap<>();
	private Map<String, JSONObject> modProjectMap = new HashMap<>();
	private Map<String, JSONObject> knownProjects = new HashMap<>();

	public final static String newPrjctStgClsd = "New Project Stage closed.";

	private List<String> columnList = Arrays.asList("Name", "Status", "BDT", "Age");
	
	private static final String save = "save";
	private static final String exit = "exit";
	private static final String list_not_active_ones = "list not active ones";
	private static final String list_active_ones = "list active ones";
	private static final String list_mod_Projects = "list mod projects";
	//private static final String correct_last_step = "correct last step";//TODO
	private static final String view_Project = "view Project";
	private static final String view_last_steps_of_Projects = "last steps";
	private static final String view_most_near_Deadline = "most near deadline";//TODO
	private static final String view_statistics = "view stats";
	private static final String next_Step = "next step";
	private static final String list = "list";
	private static final String help = "help";
	private static final String new_Project ="new project";
	private static final String list_commands = "list cmds";
	private static final String terminate_Project = "terminate project";
	private static final String add_Note = "add note";
	
	private static final Set<String> commands = new HashSet<>(Arrays.asList(save, exit, list, help, 
			list_active_ones, list_mod_Projects,/* correct_last_step, */ view_Project, view_last_steps_of_Projects, 
			view_most_near_Deadline, view_statistics, list_not_active_ones, new_Project, next_Step, list_commands, 
			terminate_Project, add_Note));


	/*TODO: some only need Testing.
	stepsView -> show all Last Steps or all Steps of one Project
	
	JSONView -> View JSONObject of a Project.
		
	noteView -> view Notes of a Prjct.
    	
    Statistics -> Think about Stats.
	 */

	private final GTDDataSpawnSession ds;
	
	public final Predicate<String> activeProject = (n)->
	{
		JSONObject jo = null;
		
		if(projectMap.containsKey(n))jo = projectMap.get(n);
		else return false;
		
		String status = jo.getString(ProjectJSONKeyz.statusKey);
		
		if(!states.getStatesOfASet(StatusMGMT.terminalSetName).contains(status))return true;

		return false;
	};

	public final Predicate<String> notActiveProject = (n)-> !activeProject.test(n);
	
	private final InputStreamSession iss;
		
    public GTDCLI(InputStreamSession iss) throws ClassNotFoundException, IOException, URISyntaxException
	{
    	
    	this.iss = iss;

    	ds = new GTDDataSpawnSession(this.iss);
    	
		//states = loadStates();
		if(states==null)states = StatusMGMT.getInstance();
		
		if(isThereADataFolder())
		{
			boot();
		}
		else 
		{
			System.out.println("There is no DataFolder");
			String directoryPath = getPathToDataFolder();

	        File directory = new File(directoryPath);

	        // Create the directory
	        if (directory.mkdir())
	        {
	            System.out.println("Directory created successfully.");
	            boot();
	        }
	        else
	        {
	            System.out.println("Failed to create the directory.");
	            System.out.println("Bye!");
	        }
		}
	}
    
    private void boot() throws IOException, URISyntaxException
    {
    	
		loadProjects();
		loadMODProjects();
	
		knownProjects.putAll(projectMap);
		knownProjects.putAll(modProjectMap);

		//states = loadStates();
		checkForDeadlineAbuse();
	
		loopForCommands();
    }
    
    public static void main(String... args) throws ClassNotFoundException, IOException, URISyntaxException
    {
    	new GTDCLI(new InputStreamSession(System.in));
    }
    
    public void loopForCommands() throws JSONException, IOException, URISyntaxException
    {
    	
    	String px = BashSigns.boldBBCPX;
    	String sx = BashSigns.boldBBCSX;
    	    	
    	System.out.println("");
    	String command = iss.getString(px + "Type" + sx + " command. (ex. help or exit).");
    	command = command.trim();
    	
    	System.out.println("Trying to excecute: '" + command.trim() + "'");
    	
    	switch(command)
    	{
    	
    		case view_most_near_Deadline:
    		{
    			LocalDateTime jetzt = LocalDateTime.now();
    			long newHours = 100000;
    			List<String> prjctList = new ArrayList<>();
    			List<Long> hourList = new ArrayList<>();
    			
    			for(JSONObject pJSON: projectMap.values())
    			{
    				
    				String prjctName = pJSON.getString(ProjectJSONKeyz.nameKey);
    				
    				JSONObject lastStep = getLastStep(pJSON);
    				
    				String dldt = lastStep.getString(StepJSONKeyz.DLDTKey);
    				LocalDateTime ldtDLDT = LittleTimeTools.LDTfromTimeString(dldt);
    				System.out.println(dldt);
    				
    				long hours = jetzt.until(ldtDLDT, ChronoUnit.HOURS);
    				boolean isNearer = (Math.abs(hours)<Math.abs(newHours));
    				
    				System.out.println(hours + "** is more near: " + isNearer);
    				
    				if(isNearer)
    				{
    					newHours=hours;
    					prjctList.clear();
    					prjctList.add(prjctName);
    					hourList.clear();
    					hourList.add(newHours);
    				}
		
    				if(Math.abs(hours)==Math.abs(newHours))
    				{
    					prjctList.add(prjctName);
    					hourList.add(newHours);
    				}
    			}
    			
    			List<String> headers = new ArrayList<>(Arrays.asList("Project", "Until or since Deadline of Last Step(Hours)"));
    			List<List<String>> rows = new ArrayList<>();

    			for(String prjctName: prjctList)
    			{
    				List<String> row = new ArrayList<>();
    				int i = prjctList.indexOf(prjctName);
    				
    				row.add(prjctName);
    				row.add(hourList.get(i).toString());
    				
    				rows.add(row);
    			}

    			TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows,'|', 18);
    			System.out.println(ttd);
    			
    			break;
    		}
    		
    		case view_last_steps_of_Projects:
    		{
    			
    			List<String> headers = new ArrayList<>(Arrays.asList("Project", "Desc", "Status", "Deadline"));
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
 
    			String choosenOne = iss.getAnswerOutOfList("Which one?", names);
    			String prjct = choosenOne.trim();
    			if(knownProjects.keySet().contains(prjct))showProjectDetail(knownProjects.get(prjct));
    			else System.out.println("Please choose more wise. Because '" + choosenOne + "' is not"
    					+ " on the List!");
    			break;

    		}
    		
    		case view_statistics:
    		{
    			int nrOfPrjcts = knownProjects.size();
    			int nrOfActivePrjcts = findProjectNames(activeProject).size();
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
    			
    			System.out.println("Nr. of Projects: "+ nrOfPrjcts);
    			System.out.println("Nr. of active Projects: " + nrOfActivePrjcts);
    			System.out.println("Nr. of Mod Projects: " + nrOfModPrjcts);
    			
    			System.out.println("Nr. of Success Steps: " + nrOfSuccessfulSteps);
    			System.out.println("Nr. of Success Projects: " + nrOfSuccessfulPrjcts);
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
    			List<String> aPrjcts = findProjectNames(activeProject);
    			if(aPrjcts.isEmpty())
    			{
    				System.out.println("No active Projects.");
    				break;
    			}
    			
    			String pName = iss.getAnswerOutOfList("Which one?", aPrjcts);
    			if(aPrjcts.contains(pName))
    			{
    				JSONObject pJSON = projectMap.get(pName);
    				ds.addNote(pJSON);
    			}
    			break;
    		}

    		case terminate_Project:
    		{
    			
    			System.out.println("");
    			List<String> aPrjcts = findProjectNames(activeProject);
    			if(aPrjcts.isEmpty())
    			{
    				System.out.println("No active Projects.");
    				break;
    			}
    			
    			String pName = iss.getAnswerOutOfList("Which one?", aPrjcts);
    			if(aPrjcts.contains(pName))
    			{
    				JSONObject pJSON = projectMap.get(pName);
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
    			List<String> noAPrjcts = findProjectNames(notActiveProject);
    			for(String prjctName: noAPrjcts)
    			{
    				JSONObject pJSON = knownProjects.get(prjctName);
    				map.put(prjctName, pJSON);
    			}
    			showProjectTable(map);
    			
    			break;
    		}
    		
    		case list_mod_Projects:
    		{
    			showProjectTable(modProjectMap);
    			break;
    		}
 
    		case list_active_ones:
    		{
    			System.out.println("");
    			showProjectTable(projectMap);
    			
    			Map<String, JSONObject> map = new HashMap<>();
    			
    			List<String> aPrjcts = findProjectNames(activeProject);
    			for(String prjctName: aPrjcts)
    			{
    				JSONObject pJSON = projectMap.get(prjctName);
    				map.put(prjctName, pJSON);
    			}

    			showProjectTable(map);
    			
    			break;
    		}

    		case next_Step:
    		{
    			System.out.println("");
    			List<String> aPrjcts = findProjectNames(activeProject);
    			if(aPrjcts.isEmpty())
    			{
    				System.out.println("Sorry no active Project!");
    				break;
    			}
    			String choosenOne = iss.getAnswerOutOfList("Which one?", aPrjcts);
    			String prjct = choosenOne.trim();
    			if(aPrjcts.contains(prjct))nxtStp(prjct);
    			else System.out.println("Please choose more wise. Because '" + choosenOne + "' is not"
    					+ " on the List!");
    			break;
    		}
    
    		case list: 
    		{
    			System.out.println("");
    			showProjectTable(knownProjects);
    			break;
    		}
    	
    		case help: 
    		{
    			System.out.println("");
    			System.out.println("Not yet Installed.");
    			break;
    		}
    		
    		case new_Project:
    		{
    			System.out.println(""); 			
    			JSONObject pJson = ds.spawnNewProject(knownProjects, states);
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
    			else System.out.println("No Project.");
    			break;
    		}    		
    		default:
    		{
    			System.out.println("Unknown command!");
    		}
    	}
    	
    	loopForCommands();
    }

    public List<String> findActiveProjectsNames()
    {
    	
    	List<String> activeProjectNames = new ArrayList<>();
    	
    	for(JSONObject jo: projectMap.values())
    	{
    		String name = jo.getString(ProjectJSONKeyz.nameKey);
    		String status = (String) jo.getString(ProjectJSONKeyz.statusKey);
    		
    		if(!states.getStatesOfASet(StatusMGMT.terminalSetName).contains(status))
    		{
    			activeProjectNames.add(name);
    		}
    	}

    	return activeProjectNames;
    }
    
    public void listActiveProjectNames()
    {
		
    	int i = 1;
    	
    	for(String projectName: findActiveProjectsNames())
    	{
    		System.out.println(i + ".)" + projectName);
    		i++;
    	}
    }
       
    public List<String> findProjectNames(Predicate<String> condition)
    {
    	List<String> output = new ArrayList<>();
    	
    	for(String projectName: knownProjects.keySet())if(condition.test(projectName))output.add(projectName);
    	
    	return output;
    }
    
    public void showProjectTable(Map<String, JSONObject> map) throws JSONException
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
    	
		TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows,'|', 20);
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
    	
    	
    	System.out.println("");
    	System.out.println(gpx + "Project Name:" + gsx + " "+ name);
    	System.out.println(gpx + "Status:" + gsx + " " + status);
    	System.out.println(gpx + "BDT:" + gsx + " " + bdt);
    	System.out.println(gpx + "NDDT:" + gsx + " " + nddt);
    	System.out.println(gpx + "Deadline:" + gsx + " " + dldtStr);
    	System.out.println(gpx + "Goal:" + gsx + " " + goal);
    	System.out.println(gpx + "Steps:" + gsx + " " + stpNr);
    	System.out.println(gpx + "Notes:" + gsx + " " + noteNr);
    	
    }
    
	public void nxtStp(String projectName) throws IOException
    {
		JSONObject jo = projectMap.get(projectName);
    	ds.appendStep(jo);
    }
  
    private String getPathToDataFolder()
    {
    	return "projectDATA/";
    }
    
    private boolean isThereADataFolder()
    {
    	  String folderPath = getPathToDataFolder();

          Path path = Paths.get(folderPath);

          if (Files.exists(path) && Files.isDirectory(path))
          {
              System.out.println("The folder exists.");
              return true;
          }
          else 
          {
              System.out.println("The folder does not exist.");
              return false;
          }
    }
    
    private void checkForDeadlineAbuse()
    {
    	
    	for(JSONObject jo: projectMap.values())
    	{
    		
    		StatusMGMT sm = StatusMGMT.getInstance();

    		String prjctStatus = jo.getString(ProjectJSONKeyz.statusKey);
    		Set<String> terminalSet = sm.getStatesOfASet(StatusMGMT.terminalSetName);
    		
    		if(terminalSet.contains(prjctStatus))continue;

    		LocalDateTime jetzt = LocalDateTime.now();
    		String deadLineStr = jo.getString(ProjectJSONKeyz.DLDTKey);
    		LocalDateTime deadLine = LittleTimeTools.LDTfromTimeString(deadLineStr);
			
			JSONObject step = getLastStep(jo);

			if(deadLine.isBefore(jetzt))
    		{
    			step.put(StepJSONKeyz.statusKey, StatusMGMT.failed);
    			jo.put(ProjectJSONKeyz.statusKey, StatusMGMT.failed);
    			
    			step.put(StepJSONKeyz.TDTNoteKey, "Project Deadline abuse!");
    			jo.put(ProjectJSONKeyz.TDTNoteKey, "Project Deadline abuse!");
    			
    			continue;
    		}
			
    		String stepStatus = jo.getString(ProjectJSONKeyz.statusKey);
    		
    		if(terminalSet.contains(stepStatus))continue;
    		else
    		{
    			String dldtStr = step.getString(StepJSONKeyz.DLDTKey);
    			LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
    			
    			if(dldt.isBefore(jetzt))
    			{
        			step.put(StepJSONKeyz.statusKey, StatusMGMT.failed);
        			jo.put(ProjectJSONKeyz.statusKey, StatusMGMT.needsNewStep);
        			
        			step.put(StepJSONKeyz.TDTNoteKey, "Step Deadline abuse!");    
    			}
    		}    					
    	}
    }


    private void eraseMODProjectFile(String prjctName)
    {
    	String path = getPathToDataFolder();
    	
    	File folder = new File(path);
    	if(folder==null)
    	{
    		System.out.println("Folder is Null.");
    		System.exit(0);
    	}

    	File[] listOfFiles = folder.listFiles();
    	if(listOfFiles==null)
    	{		
    		System.out.println("listOfFiles is Null.");
    		System.exit(0);
    	}
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile()&&file.getName().equals(prjctName+modPrjctMarker))
    			file.delete();
    	}
    }
    
    private void loadMODProjects() throws IOException
    {
    	
    	String path = getPathToDataFolder();
    	
    	File folder = new File(path);
    	if(folder==null)
    	{
    		System.out.println("Folder is Null.");
    		System.exit(0);
    	}

    	File[] listOfFiles = folder.listFiles();
    	if(listOfFiles==null)
    	{		
    		System.out.println("listOfFiles is Null.");
    		System.exit(0);
    	}

    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.endsWith(modPrjctMarker))
    		{
    			
    			String joText = LoadAndSave.loadText(path, name);
    			
    			JSONObject jo = new JSONObject(joText);
    			
    			modProjectMap.put(jo.getString(ProjectJSONKeyz.nameKey), jo);
    		}
    	}
    }
    
    
    private StatusMGMT loadStates() throws ClassNotFoundException, IOException
    {
    	String path = getPathToDataFolder();
    	
    	File folder = new File(path);
    	if(folder==null)
    	{
    		System.out.println("Folder is Null.");
    		System.exit(0);
    	}

    	File[] listOfFiles = folder.listFiles();
    	if(listOfFiles==null)
    	{		
    		System.out.println("listOfFiles is Null.");
    		System.exit(0);
    	}

    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.equals(statesFileName))
    		{
    			
    	    	return (StatusMGMT) LoadAndSave.loadObject(getPathToDataFolder()+statesFileName);
    		}
    	}

    	return (StatusMGMT) null;
    }

    private void loadProjects() throws IOException, URISyntaxException
    {
    	
    	String path = getPathToDataFolder();
    	
    	File folder = new File(path);
    	if(folder==null)
    	{
    		System.out.println("Folder is Null.");
    		System.exit(0);
    	}

    	File[] listOfFiles = folder.listFiles();
    	if(listOfFiles==null)
    	{		
    		System.out.println("listOfFiles is Null.");
    		System.exit(0);
    	}

    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.endsWith(fileMarker))
    		{
    			
    			String joText = LoadAndSave.loadText(path, name);
    			
    			JSONObject jo = new JSONObject(joText);
    			
    			projectMap.put(jo.getString(ProjectJSONKeyz.nameKey), jo);
    		}
    	}
    }
    
    private void saveMODProjects() throws JSONException, IOException
    {
    	
    	for(JSONObject jo: modProjectMap.values())
    	{
    		String path = getPathToDataFolder();
    		LoadAndSave.saveText(path, jo.getString(ProjectJSONKeyz.nameKey)+modPrjctMarker, jo.toString(jsonPrintStyle));
    	}
    }
    
    private void saveProjects() throws JSONException, IOException, URISyntaxException
    {
    	for(JSONObject jo: projectMap.values())
    	{
    		String path = getPathToDataFolder();
    		LoadAndSave.saveText(path, jo.getString(ProjectJSONKeyz.nameKey)+fileMarker, jo.toString(jsonPrintStyle));
    	}
    }
     
    private void saveStatusMGMT() throws IOException
    {
    	LoadAndSave.saveObject(getPathToDataFolder()+statesFileName, StatusMGMT.getInstance());
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
    	System.out.println("Bye!");
    	System.exit(0);
    }

	@Override
	public void refresh(String arg0) 
	{
		// TODO Auto-generated method stub
		
	}
    
}