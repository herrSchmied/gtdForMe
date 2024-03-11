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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.HashMap;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import allgemein.ExactPeriode;
import allgemein.Beholder;

import allgemein.LittleTimeTools;


import consoleTools.BashSigns;
import consoleTools.InputArgumentException;
import consoleTools.InputStreamSession;

import consoleTools.TerminalTableDisplay;
/* */
import someMath.NaturalNumberException;

import static fileShortCuts.TextAndObjSaveAndLoad.*;


public class GTDCLI implements Beholder<String>
{
	
	private final String statesFileName = "statusMGMT.states";
	private final StatusMGMT states = loadStates();
	
	private final String sayGoodBye = "Bye!";
	
	private final String unknownProject = "Unknown Project!";
	private final String projectIsNotActive = "Project is not active.";
	private final String sorryDeadlineAbuse = "Sorry Deadline Abuse.";
	private final String noPrjctFound = "No Projects found.";
	private final String unknownCmdStr = "Unknown command!";
	private final String hereAListOfCmds = "Here a list of Commands.";
	private final String dataFolderFound = "Data Folder found.";
	private final String thereIsNoDataFolder = "There is no Data Folder";
	private final String dataFolderCreated = "Data Folder created successfully.";
	private final String failedToCreateDirectory = "Failed to create the directory.";
	private final String noNotMODProjects = "Not Not Mod Projects.";
	private final String noMODProjects = "No MOD Projects.";
	private final String noActiveProjects = "No active Projects!";
	private final String noNotActiveProjects = "No not active Projects!";
	private final String noSuchProject = "No such Project: ";
	public final String newPrjctStgClsd = "New Project Stage closed.";
	public final String tdtNoteStpDLDTAbuse = "Step Deadline abuse!";
	public final String tdtNotePrjctDLDTAbuse = "Project Deadline abuse!";
	
	private final String projectStr = "Project";
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

	private final String hasNoNotesSuffix = " has no Notes.";
	
	private final String nrOfPrjctsStr = "Nr. of Projects: ";
	private final String nrOfActivePrjctsStr = "Nr. of active Projects: ";
	private final String nrOfMODPrjctsStr = "Nr. of Mod Projects: ";
	private final String nrOfSuccessStpsStr = "Nr. of Success Steps: ";
	private final String nrOfSuccessPrjctsStr = "Nr. of Success Projects: ";
	
	
	private static final String projectDataFolderRelativePath = "projectDATA/";
	
	private final String thereIsAStatesFileLoading = "There is a States File. Trying to Load it";
	private final String somethinWrongStates = "Something is wrong. Using default States.";
	private final String thereAreNoStates = "There is no States File using default States.";
	
	private final char wallOfTableChr = '|';
	public final int jsonPrintStyle = 4;
	public static final String fileMarker = ".prjct";
	
	private Map<String, JSONObject> knownProjects = new HashMap<>();

	public static final String isModProjectQ = "Maybe one Day-Project?(yes) or are we actually try "
			+ "to do it soon enough?(no): ";
	
	private List<String> columnList = Arrays.asList("Name", "Status", "BDT", "Age");
	private List<String> stepColumns = Arrays.asList("Desc", "Status", "BDT", "DLDT");
	
	/* Remember: No command can be the beginning of another command.
	 * Write a Method to check that!!!!!!
	 * */
	public static final String save = "save";
	public static final String exit = "exit";
	public static final String list_not_active_ones = "show not active ones";
	public static final String list_active_ones = "main table";
	public static final String list_mod_Projects = "list mod projects";
	//private static final String correct_last_step = "correct last step";//TODO
	public static final String view_Project = "view project";
	public static final String view_last_steps_of_Projects = "last steps";
	public static final String view_nearest_Deadline = "nearest deadline";
	public static final String view_statistics = "stats";
	public static final String next_Step = "next step";
	public static final String justPrjctNames = "names";
	public static final String help = "help";
	public static final String new_Project ="new project";
	public static final String new_MOD = "new mod";
	public static final String wake_MOD = "wake mod";
	public static final String list_commands = "show cmds";
	public static final String terminate_Project = "terminate project";
	public static final String terminate_Step = "terminate step";
	public static final String add_Note = "add note";
	public static final String view_Notes = "show notes";
	public static final String successes = "successes";
	public static final String fails = "fails";
	public static final String show_Steps_of = "show steps of";
	
	private final Set<String> commands = new HashSet<>();

	private final Set<String> prjctModifierCommands = new HashSet<>();
	
	private final Set<String> showDataCommands = new HashSet<>();
	
	private final Set<String> otherCommands = new HashSet<>();
	
	private final Map<String, CLICommand<?>> commandMap = new HashMap<>();
	
	private final String pmcSetName = "Project_Modifier_Cmd_Set";
	private final String sdcSetName = "Show_Data_Cmd_Set";
	private final String ocSetName = "Other_Cmd_Set";
	
	private final Map<String, Set<String>> commandSetMap = Map.of(pmcSetName, prjctModifierCommands,
																  sdcSetName, showDataCommands,
																  ocSetName, otherCommands);
	
	boolean noMoreLoops = false;
	
	/*TODO: some only need Testing.
 
	stepsView -> show all Last Steps of all Projects or all Steps of one Project
	
	JSONView -> View JSONObject of a Project.
	 */

	private final GTDDataSpawnSession ds;
	
	
	public final Predicate<JSONObject> activeProject = (jo)->
	{
		
		if(!knownProjects.containsValue(jo))return false;
		
		if(projectIsTerminated(jo))return false;
		
		if(isMODProject(jo))return false;
		
		return true;
	};

	public final Predicate<JSONObject> notActiveProject = (jo)-> 
	{
		if(!knownProjects.containsValue(jo))return false;//Is a must!!
		
		return !activeProject.test(jo);
	};
	
	public final Predicate<String> activePrjctName = (s)->
	{
		if(!knownProjects.containsKey(s)) return false;
		
		JSONObject pJSON = knownProjects.get(s);
		
		return activeProject.test(pJSON);
	};
	
	public final Predicate<String> notActivePrjctName = (s)->
	{
		if(!knownProjects.containsKey(s)) return false;//Is a must!!
		
		return !activePrjctName.test(s);
	};

	public final Predicate<String> modPrjctName = (s)->
	{
		if(!knownProjects.containsKey(s))return false;
		
		JSONObject pJSON = knownProjects.get(s);
		
		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		
		return status.equals(StatusMGMT.mod);
	};
	
	public final Predicate<String> noModPrjctName = (s)->
	{
		return !modPrjctName.test(s);
	};

	
	private final InputStreamSession iss;
		
    public GTDCLI(InputStreamSession iss) throws IOException, URISyntaxException, InputArgumentException, JSONException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException, NaturalNumberException
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
	            System.exit(0);
	        }
		}
	}
    
    private void boot() throws IOException, URISyntaxException, NaturalNumberException, InputArgumentException, JSONException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
    {
    	
    	initComands();
    	
		Set<JSONObject> prjctSet = loadProjects();
		for(JSONObject json: prjctSet)
		{
			String pName = json.getString(ProjectJSONKeyz.nameKey);
			knownProjects.put(pName, json);
		}
		
		checkAllForDLDTAbuse();
	
		greetings();
		
		loopForCommands();
    }
    
    private void initComands()
    {

    	MeatOfCLICmd<JSONObject> newProject = (s)->
		{

			JSONObject pJSON = ds.spawnNewProject(knownProjects.keySet(), states);

			String name = pJSON.getString(ProjectJSONKeyz.nameKey);
			knownProjects.put(name, pJSON);

			return pJSON;
		};
		
		List<Boolean> ioArray = new ArrayList<>(Arrays.asList(false, false, true, false));
		
		registerCmd(new_Project, pmcSetName, ioArray, newProject);
		
		MeatOfCLICmd<JSONObject> newMODProject = (s)->
		{
			
			JSONObject pJSON = ds.spawnMODProject(knownProjects.keySet(), states);

			String name = pJSON.getString(ProjectJSONKeyz.nameKey);
			knownProjects.put(name, pJSON);
				
			return pJSON;
		};
		
		ioArray.clear();
		ioArray = new ArrayList<>(Arrays.asList(false,false,true,false));
		
		registerCmd(GTDCLI.new_MOD, pmcSetName, ioArray, newMODProject);
		
		MeatOfCLICmd<String> leave = (s)->
		{
			
			stop();//Save than exit.
			
			return ""; //Unreachable!!!!
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, false, false));
		
		registerCmd(exit, ocSetName, ioArray, leave);
		
    
		MeatOfCLICmd<String> nearestDeadline = (s)->
		{
			LocalDateTime jetzt = LocalDateTime.now();
    		long newMinutes = 1000000;
    		List<String> pList = new ArrayList<>();
    		List<String> dauerList = new ArrayList<>();
    			
			if(knownProjects.isEmpty())throw new CLICMDException(noPrjctFound);

			for(JSONObject pJSON: knownProjects.values())
    		{
    				
    			String prjctName = pJSON.getString(ProjectJSONKeyz.nameKey);
    				
    			JSONObject lastStep = getLastStep(pJSON);
    				
    			String stepDLDTStr = lastStep.getString(StepJSONKeyz.DLDTKey);
    			LocalDateTime stepDLDT = LittleTimeTools.LDTfromTimeString(stepDLDTStr);
    				
    			String dauer = new ExactPeriode(jetzt, stepDLDT).toString();
    				
    			long minutes = jetzt.until(stepDLDT, ChronoUnit.MINUTES);
    			boolean isNearer = (Math.abs(minutes)<Math.abs(newMinutes));
    			boolean isEqual = Math.abs(minutes)==Math.abs(newMinutes);
    				    				
    			if(isEqual)
    			{
    				pList.add(prjctName);
    				dauerList.add(dauer);
    			}

    				
    			if(isNearer)
    			{
    				newMinutes=minutes;
    					
    				pList.clear();
    				pList.add(prjctName);
    				dauerList.clear();
    				dauerList.add(dauer); 
    			}
    		}

    		List<List<String>> rows = new ArrayList<>();
    			
    		int l = pList.size();
    		for(int n=0;n<l;n++)
    		{
    			List<String> row = new ArrayList<>();

    			row.add(pList.get(n));
    			row.add(dauerList.get(n).toString());
    				
    			rows.add(row);
    		}

    		List<String> headers = new ArrayList<>(Arrays.asList(projectStr, nearestDeadlineStr));
    		TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows,'|', 18);
    		System.out.println(ttd);
			
    		return ttd.toString();
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(view_nearest_Deadline, sdcSetName, ioArray, nearestDeadline);
    
		MeatOfCLICmd<String> listNames = (s)->
		{
			
			String output = "";
			
			if(knownProjects.isEmpty())throw new CLICMDException(noPrjctFound);

			for(String name: knownProjects.keySet())output = output + '\n' + name;
			
			System.out.println(output);
			return output;
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(GTDCLI.justPrjctNames, sdcSetName, ioArray, listNames);


		MeatOfCLICmd<String> listCmds = (s)->
		{
			String output = "";

			for(String cmds: commands)
    		{
    			output = "\n" + cmds +output;
    		}
    		
    		System.out.println(output + "\n");
    		
    		return output;
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));

		registerCmd(list_commands, sdcSetName, ioArray, listCmds);
		
		
		MeatOfCLICmd<String> projectView = (s)->
		{
			
			if(knownProjects.isEmpty())throw new CLICMDException(noPrjctFound);

			String output = "";

   			System.out.println("");
    		List<String> names = new ArrayList<>();
    		names.addAll(knownProjects.keySet());

    		
	    	String prjct;
	    	if(s.trim().equals(""))prjct=  iss.getAnswerOutOfList(whichOnePhrase, names);
	    	else prjct = s.trim();
	    		
	    	if(!knownProjects.keySet().contains(prjct))throw new CLICMDException(noSuchProject+prjct);

	    	output =showProjectDetail(knownProjects.get(prjct));
	    	System.out.println(output);


			return output;
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(view_Project, sdcSetName, ioArray, projectView);
		
		MeatOfCLICmd<String> showNotActivePrjcts = (s)->
		{

    		Map<String, JSONObject> map = new HashMap<>();
    		List<String> noAPrjcts = findProjectNamesByCondition(notActivePrjctName);
    		
    		if(noAPrjcts.isEmpty()) throw new CLICMDException(noNotActiveProjects);

    		for(String prjctName: noAPrjcts)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			map.put(prjctName, pJSON);
    		}
    		
    		showProjectMapAsTable(map);
    		
    		return "";
       	};

    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, false, false));
		
		registerCmd(list_not_active_ones, sdcSetName, ioArray, showNotActivePrjcts);
		
		MeatOfCLICmd<String> showActivePrjcts = (s)->
		{
			
			System.out.println("");
			
			Map<String, JSONObject> map = new HashMap<>();
			
			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
			if(aPrjcts.isEmpty())throw new CLICMDException(noActiveProjects);
			
			for(String prjctName: aPrjcts)
			{
				JSONObject pJSON = knownProjects.get(prjctName);
				map.put(prjctName, pJSON);
			}
			
			showProjectMapAsTable(map);
			
			return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, false, false));
		
		registerCmd(list_active_ones, sdcSetName, ioArray, showActivePrjcts);
		
		MeatOfCLICmd<String> lastSteps = (s)->
		{
			
			List<String> headers = new ArrayList<>(Arrays.asList(projectStr, descStr, statusStr, deadlineStr));
			List<List<String>> rows = new ArrayList<>();

			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
			if(aPrjcts.isEmpty())
			{
				System.out.println("No active Projects.");
				return "";
			}
			
			for(String prjctName: aPrjcts)
			{
				
				JSONObject pJSON = knownProjects.get(prjctName);
				
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
			
			return ttd.toString();
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(view_last_steps_of_Projects, sdcSetName, ioArray, lastSteps);
		
		MeatOfCLICmd<String> stats = (s)->
		{
			
			if(knownProjects.isEmpty())throw new CLICMDException(noPrjctFound);

			int nrOfPrjcts = knownProjects.size();
			int nrOfActivePrjcts = findProjectsByCondition(activeProject).size();
			int nrOfModPrjcts = 0;
		
			
			int nrOfSuccessfulSteps = 0;
			int nrOfSuccessfulPrjcts = 0;
			for(JSONObject pJSON: knownProjects.values())
			{
				if(isMODProject(pJSON))nrOfModPrjcts++;
				
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
			
			String output = nrOfPrjctsStr + nrOfPrjcts + '\n' +
			nrOfActivePrjctsStr + nrOfActivePrjcts + '\n' +
			nrOfMODPrjctsStr + nrOfModPrjcts  + '\n' +
			nrOfSuccessStpsStr + nrOfSuccessfulSteps + '\n' +
			nrOfSuccessPrjctsStr + nrOfSuccessfulPrjcts;

			System.out.println(output);
			
			return output;
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(view_statistics, sdcSetName, ioArray, stats);
		
		MeatOfCLICmd<String> save = (s)->
		{
			saveAll();
			return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(GTDCLI.save, ocSetName, ioArray, save);
		
		MeatOfCLICmd<JSONObject> addNote = (s)->
		{
			
   			System.out.println("");
			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
			if(aPrjcts.isEmpty())throw new CLICMDException(noActiveProjects);
			
				
			String pName;
			if(s.trim().equals(""))pName = iss.getAnswerOutOfList(whichOnePhrase, aPrjcts);
			else pName = s.trim();
				
			if(!aPrjcts.contains(pName))throw new CLICMDException(noSuchProject + pName);

			JSONObject pJSON = knownProjects.get(pName);
			boolean stepDidIt = checkStepForDeadlineAbuse(pJSON);
			boolean projectDidIt = checkProjectForDeadlineAbuse(pJSON);
				
			if(stepDidIt||projectDidIt)
			{
				System.out.println("Sorry Deadline abuse.");
				alterProjectAfterDLDTAbuse(pJSON, stepDidIt, projectDidIt);
				return new JSONObject();
			}
			ds.addNote(pJSON);
			return pJSON;
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
		
		registerCmd(GTDCLI.add_Note, pmcSetName, ioArray, addNote);

		MeatOfCLICmd<String> viewNotes = (s)->
		{
	    			
			System.out.println("");
			
			if(knownProjects.isEmpty())throw new CLICMDException(noPrjctFound);
	    	
	    	String prjct;

	    	if(s.trim().equals(""))prjct = iss.getAnswerOutOfList(notesOfWhichPrjctPhrase, new ArrayList<String>(knownProjects.keySet()));
			else prjct = s.trim();
				
			if(!knownProjects.keySet().contains(prjct))throw new CLICMDException(noSuchProject+prjct);
			
    		JSONObject pJSON = knownProjects.get(prjct);
    			
    		JSONArray noteArr;
    		String output = "";
    		if(!pJSON.has(ProjectJSONKeyz.noteArrayKey))throw new CLICMDException(projectStr + " " + prjct + hasNoNotesSuffix);
    		
    		noteArr = pJSON.getJSONArray(ProjectJSONKeyz.noteArrayKey);
    		int l = noteArr.length();
    				
    		for(int n=0;n<l;n++)
    		{
    			output = output + "--> " + noteArr.get(n);
    		}
    				
    		System.out.println(output);
    		return output;
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(GTDCLI.view_Notes, ocSetName, ioArray, viewNotes);

		Supplier<List<String>> listOfMODs = ()->
		{
			
			List<String> modNames = new ArrayList<>();
			
			
			
			for(String prjctName: knownProjects.keySet())
			{
				JSONObject pJSON = knownProjects.get(prjctName);
				
				if(isMODProject(pJSON))modNames.add(prjctName);
			}
			
			return modNames;

		};
		
		MeatOfCLICmd<String> listMODs = (s)->
		{
			
			Map<String, JSONObject> map = new HashMap<>();
    		
    		List<String>modNames = listOfMODs.get();
    		if(modNames.isEmpty())throw new CLICMDException(noMODProjects);
    		
    		for(String prjctName: modNames)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			map.put(prjctName, pJSON);
    		}
    		
    		showProjectMapAsTable(map);
    		return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
	
		registerCmd(GTDCLI.list_mod_Projects, sdcSetName, ioArray, listMODs);
		
		MeatOfCLICmd<String> wakeMOD = (s)->
		{
			
			System.out.println("");
    		List<String> modPrjcts = listOfMODs.get();
    		if(modPrjcts.isEmpty())throw new CLICMDException(noMODProjects);
   		
    		String prjctName;
    		if(s.trim().equals(""))prjctName = iss.getAnswerOutOfList(whichOnePhrase, modPrjcts);
    		else prjctName = s.trim();

    		
			if(!modPrjcts.contains(prjctName))throw new CLICMDException("No such Project.");
				
			JSONObject pJSON = knownProjects.get(prjctName);
			knownProjects.remove(prjctName);
			ds.wakeMODProject(pJSON);
			knownProjects.put(prjctName, pJSON);
			
			return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
	
		registerCmd(GTDCLI.wake_MOD, pmcSetName, ioArray, wakeMOD);

		MeatOfCLICmd<JSONObject> nextStep = (s)->
		{

			System.out.println("");
    		List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
    		if(aPrjcts.isEmpty())throw new CLICMDException(noActiveProjects);
    		
    		String prjct;
    		if(s.trim().equals(""))prjct = iss.getAnswerOutOfList(whichOnePhrase, aPrjcts);
    		else prjct = s.trim();
    		
    		if(!knownProjects.keySet().contains(prjct))throw new CLICMDException(noSuchProject);
    		
    		if(!aPrjcts.contains(prjct))throw new CLICMDException(projectIsNotActive);
    		
    		JSONObject pJSON = knownProjects.get(prjct);
			boolean stepDidIt = checkStepForDeadlineAbuse(pJSON);
			boolean projectDidIt = checkProjectForDeadlineAbuse(pJSON);
    			
    		if(stepDidIt||projectDidIt)
    		{
				alterProjectAfterDLDTAbuse(pJSON, stepDidIt, projectDidIt);
				throw new CLICMDException(sorryDeadlineAbuse);
    		}
 
    		ds.spawnStep(pJSON);
    		return pJSON;
    		
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
	
		registerCmd(GTDCLI.next_Step, pmcSetName, ioArray, nextStep);

		MeatOfCLICmd<JSONObject> killPrjct = (s)->
		{

    		System.out.println("");
    		List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
    		if(aPrjcts.isEmpty())throw new CLICMDException(noActiveProjects);

    		String pName;
    		if(s.trim().equals(""))pName = iss.getAnswerOutOfList(whichOnePhrase, aPrjcts);
    		else pName = s.trim();
    			
    		if(!knownProjects.keySet().contains(pName))throw new CLICMDException(noSuchProject + pName);
    			
    		if(!aPrjcts.contains(pName))throw new CLICMDException(projectIsNotActive);
    		
    		JSONObject pJSON = knownProjects.get(pName);
        	boolean stepDidIt = checkStepForDeadlineAbuse(pJSON);
        	boolean projectDidIt = checkProjectForDeadlineAbuse(pJSON);

        	if(stepDidIt||projectDidIt)
        	{
        		alterProjectAfterDLDTAbuse(pJSON, stepDidIt, projectDidIt);
        		throw new CLICMDException(sorryDeadlineAbuse);
    		}

    		ds.terminateProject(pJSON);
    		return pJSON;
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
	
		registerCmd(GTDCLI.terminate_Project, pmcSetName, ioArray, killPrjct);
		
		MeatOfCLICmd<JSONObject> killStep = (s)->
		{
			
    		System.out.println("");
    		List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);

    		String pName;
    		if(s.trim().equals(""))pName = iss.getAnswerOutOfList(whichOnePhrase, aPrjcts);
    		else pName = s.trim();
    			
    		if(!knownProjects.keySet().contains(pName))throw new CLICMDException(noSuchProject+pName);
    		
    		if(!aPrjcts.contains(pName))throw new CLICMDException(projectIsNotActive);
    		
    		JSONObject pJSON = knownProjects.get(pName);
        	boolean stepDidIt = checkStepForDeadlineAbuse(pJSON);
        	boolean projectDidIt = checkProjectForDeadlineAbuse(pJSON);

    		if(stepDidIt||projectDidIt)
    		{
    			
				alterProjectAfterDLDTAbuse(pJSON, stepDidIt, projectDidIt);
    			throw new CLICMDException(sorryDeadlineAbuse);
    		}
    			
    		ds.terminateStep(pJSON);  				
   
    		return pJSON;
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(GTDCLI.terminate_Step, pmcSetName, ioArray, killStep);
		
		MeatOfCLICmd<String> hilfe = (s)->
		{
			String output = "Not yet Installed.";//TODO:;
			System.out.println(output);
			
			return output;
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
	
		registerCmd(GTDCLI.help, ocSetName, ioArray, hilfe);
		
		MeatOfCLICmd<String> successes = (s)->
		{
    		Map<String, JSONObject> map = new HashMap<>();
    		List<String> noAPrjcts = findProjectNamesByCondition(notActivePrjctName);
    		
    		if(noAPrjcts.isEmpty()) throw new CLICMDException(noNotActiveProjects);

    		for(String prjctName: noAPrjcts)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			if(pJSON.getString(ProjectJSONKeyz.statusKey).equals(StatusMGMT.success))
    				map.put(prjctName, pJSON);
    		}
    		
    		showProjectMapAsTable(map);
    		
    		return "";
		};

    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
	
		registerCmd(GTDCLI.successes, sdcSetName, ioArray, successes);

		MeatOfCLICmd<String> fails = (s)->
		{
    		Map<String, JSONObject> map = new HashMap<>();
    		List<String> noAPrjcts = findProjectNamesByCondition(notActivePrjctName);
    		
    		if(noAPrjcts.isEmpty()) throw new CLICMDException(noNotActiveProjects);

    		for(String prjctName: noAPrjcts)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			if(pJSON.getString(ProjectJSONKeyz.statusKey).equals(StatusMGMT.failed))
    				map.put(prjctName, pJSON);
    		}
    		
    		showProjectMapAsTable(map);
    		
    		return "";
		};

    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
	
		registerCmd(GTDCLI.fails, sdcSetName, ioArray, fails);
		
		MeatOfCLICmd<String> showSteps = (s)->
		{
    
			String t = s.trim();
			if(!knownProjects.containsKey(t))throw new CLICMDException(unknownProject);
			
			JSONObject pJSON = knownProjects.get(t);

			showProjectStepsAsTable(pJSON);

    		return "";
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(true, false, true, false));
		
		registerCmd(GTDCLI.show_Steps_of, sdcSetName, ioArray, showSteps);
	}

	/** @param ioArray index 0 = mustHaveArgument	*
	 *  @param ioArray index 1 = canHaveArgument	*
	 *  @param ioArray index 2 = mustHaveOutput		*
	 *  @param ioArray index 3 = canHaveOutput		*/
    public <O> void registerCmd(String cmdName, String setName, List<Boolean>ioArray, MeatOfCLICmd<O>action)
    {

    	CLICommand<O> cliCmd = new CLICommand<O>(cmdName, ioArray.get(0), ioArray.get(1), ioArray.get(2), ioArray.get(3), action);
    	commandMap.put(cmdName, cliCmd);
    	commands.add(cmdName);
    	Set<String> cmdSet = commandSetMap.get(setName);
    	cmdSet.add(cmdName);
    }
    
    public void greetings() throws IOException
    {
    	LocalDateTime inTheMoment = LocalDateTime.now();
    	String day = inTheMoment.getDayOfWeek().toString();
    	int day2 = inTheMoment.getDayOfMonth();
    	int year = inTheMoment.getYear();
    	String time = LittleTimeTools.timeString(inTheMoment.toLocalTime());
    	
    	System.out.println("Hello, it is " + day + " the " + day2 + " in the Year "+year);
    	System.out.println("Time: " + time + '\n');
    }
    
    public static void main(String... args) throws IOException, URISyntaxException, InputArgumentException, JSONException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException, NaturalNumberException
    {
    	new GTDCLI(new InputStreamSession(System.in));
    }
    
    public void loopForCommands() throws NaturalNumberException, InputArgumentException, IOException, JSONException, URISyntaxException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
    {
    	
    	String px = BashSigns.boldBBCPX;
    	String sx = BashSigns.boldBBCSX;
    	
    	String fullCmdWithOptArgTyped = iss.getString(px + "Type" + sx + " command. (ex. help or exit).");
    	fullCmdWithOptArgTyped = fullCmdWithOptArgTyped.trim();
    	
    	try
    	{
        	int numberOfCmds = commandMap.size();
        	int cmdCounter = 0;

        	for(String commandKnown: commandMap.keySet())
    		{
    			if(fullCmdWithOptArgTyped.startsWith(commandKnown))
    			{
    				CLICommand<?> clicmd = commandMap.get(commandKnown);
    		
    				String argument = getArgumentOfCommand(fullCmdWithOptArgTyped, commandKnown);
    				
    				/*Object obj = */ clicmd.executeCmd(argument);
    				break;
    			}
    			cmdCounter++;
    		}

    		if(cmdCounter==numberOfCmds)
    		{
    			System.out.println('\n'+unknownCmdStr);
    			System.out.println(hereAListOfCmds);
    			CLICommand<?> clicmd = commandMap.get(list_commands);
    			clicmd.executeCmd("");
    		}
    	}
    	catch(CLICMDException | NaturalNumberException | InputArgumentException | SpawnProjectException |
    			SpawnStepException | ProjectTerminationException | StepTerminationException | IOException |
    			TimeGoalOfProjectException e)
    	{
    		System.out.println(e);
    	}
    	
    	if(!noMoreLoops)loopForCommands();
    }

    public String getArgumentOfCommand(String commandTyped, String commandKnown)
    {
    	
    	String argument = "";
   		int l = commandKnown.length();
   		argument = commandTyped.substring(l);

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

    public void showProjectStepsAsTable(JSONObject pJSON)
    {
    	
		List<String> headers = stepColumns;
		List<List<String>> rows = new ArrayList<>();
		
		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		
		JSONArray steps;
		if(!status.equals(StatusMGMT.mod))
		{
			steps = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
			
			int len = steps.length();
			for(int n=0;n<len;n++)
			{
				
				JSONObject step = steps.getJSONObject(n);
				
				String desc = step.getString(StepJSONKeyz.descKey);
				String stepStatus = step.getString(StepJSONKeyz.statusKey);
				String bdt = step.getString(StepJSONKeyz.BDTKey);
				String dldt = step.getString(StepJSONKeyz.DLDTKey);

	    		List<String> row = new ArrayList<>();
	    		row.add(desc);
	    		row.add(stepStatus);
	    		row.add(bdt);
	    		row.add(dldt);
	    		
	    		rows.add(row);

			}

			TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows, wallOfTableChr, 20);
			
			System.out.println(ttd.toString());
		}
		
		

    }
    public void showProjectMapAsTable(Map<String, JSONObject> map) throws JSONException, NaturalNumberException
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
    		String age = new ExactPeriode(ldtBDT, jetzt).toString();	

    		List<String> row = new ArrayList<>();
    		row.add(name);
    		row.add(status);
    		row.add(bdt);
    		row.add(age);
    		
    		rows.add(row);
    	}
    	
		TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows, wallOfTableChr, 20);
		
		System.out.println(ttd.toString());
    }
    
    public static JSONObject getLastStep(JSONObject pJSON)
    {
    	JSONArray stepArr = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
    	int l = stepArr.length();
    	
    	return stepArr.getJSONObject(l-1);
    }
    
    public String showProjectDetail(JSONObject pJSON)
    {
    	String gpx = BashSigns.boldGBCPX;
    	String gsx = BashSigns.boldGBCSX;

    	String name = pJSON.getString(ProjectJSONKeyz.nameKey);
    	String status = pJSON.getString(ProjectJSONKeyz.statusKey);
    	String bdt = pJSON.getString(ProjectJSONKeyz.BDTKey);
    	String nddt = pJSON.getString(ProjectJSONKeyz.NDDTKey);
    	String goal = pJSON.getString(ProjectJSONKeyz.goalKey);

    	int stpNr = 0;
    	if(!isMODProject(pJSON))
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
    	
    	String output = gpx + prjctNameStr + ":" + gsx + " "+ name + '\n' +
    					gpx + statusStr + ":" + gsx + " " + status + '\n' +
    					gpx + bdtStr + ":" + gsx + " " + bdt + '\n' +
    					gpx + nddtStr + ":" + gsx + " " + nddt + '\n' +
    					gpx + deadlineStr + ":" + gsx + " " + dldtStr + '\n' +
    					gpx + goalStr + ":" + gsx + " " + goal + '\n' +
    					gpx + stepsStr + ":" + gsx + " " + stpNr + '\n' +
    					gpx + notesStr + ":" + gsx + " " + noteNr;
    	
    	return output;
    }
    
	public void nxtStp(JSONObject pJSON) throws InputArgumentException, SpawnStepException, IOException
    {
    	ds.spawnStep(pJSON);
    }
  
    private static String getPathToDataFolder()
    {
    	return projectDataFolderRelativePath;
    }
       
    private void checkAllForDLDTAbuse()
    {
    	
    	for(JSONObject pJSON: knownProjects.values())
    	{

    		if(activeProject.test(pJSON))
    		{
    			boolean stepDidIt = checkStepForDeadlineAbuse(pJSON);
    			boolean projectDidIt = checkProjectForDeadlineAbuse(pJSON);
    		
    			if(stepDidIt|| projectDidIt)alterProjectAfterDLDTAbuse(pJSON, stepDidIt, projectDidIt);
    		}
    	}
    }
    
    private boolean checkStepForDeadlineAbuse(JSONObject pJSON)
    {

    	if(projectIsTerminated(pJSON))return false;
    	
    	LocalDateTime jetzt = LocalDateTime.now();
			
		JSONObject step = getLastStep(pJSON);

    	if(stepIsTerminated(step))return false;
    	
    	String dldtStr = step.getString(StepJSONKeyz.DLDTKey);
    	if(dldtStr.equals(GTDDataSpawnSession.stepDeadlineNone))return false;
    	
    	LocalDateTime stepDLDT = LittleTimeTools.LDTfromTimeString(dldtStr);
    			
    	if(stepDLDT.isBefore(jetzt)) return true;//Is Step DLDT abused?
    	
    	return false;
    }
        
    private boolean checkProjectForDeadlineAbuse(JSONObject pJSON)
    {
    		
    	if(projectIsTerminated(pJSON))return false;

    	LocalDateTime jetzt = LocalDateTime.now();

    	String projectDLDTStr = pJSON.getString(ProjectJSONKeyz.DLDTKey);
    	if(projectDLDTStr.equals(GTDDataSpawnSession.prjctDeadlineNone))return false;
    	
    	LocalDateTime projectDLDT = LittleTimeTools.LDTfromTimeString(projectDLDTStr);

    	if(projectDLDT.isBefore(jetzt)) return true;//Is Project DLDT abused?

    	return false;
    }
    
    public void alterProjectAfterDLDTAbuse(JSONObject pJSON, boolean stepDidIt, boolean projectDidIt)
    {
    	
    	JSONObject step = getLastStep(pJSON);
    	
    	
    	if(stepDidIt)
    	{
        	step.put(StepJSONKeyz.statusKey, StatusMGMT.failed);
        	pJSON.put(ProjectJSONKeyz.statusKey, StatusMGMT.needsNewStep);
        			
        	String stepDLDTStr = step.getString(StepJSONKeyz.DLDTKey);
        	step.put(StepJSONKeyz.TDTKey, stepDLDTStr);
        	step.put(StepJSONKeyz.TDTNoteKey, tdtNoteStpDLDTAbuse);
    	}
    	
    	if(projectDidIt)
    	{
    		
        	String projectDLDTStr = pJSON.getString(ProjectJSONKeyz.DLDTKey);
        	
    		pJSON.put(ProjectJSONKeyz.statusKey, StatusMGMT.failed);
    		pJSON.put(ProjectJSONKeyz.TDTKey, projectDLDTStr);
    		pJSON.put(ProjectJSONKeyz.TDTNoteKey, tdtNotePrjctDLDTAbuse);
    		
    		if(!stepIsTerminated(step))//if step is not already Terminal alter step status and TDT(Note) too.
    		{
    			
        		step.put(StepJSONKeyz.TDTKey, projectDLDTStr);
        		step.put(StepJSONKeyz.statusKey, StatusMGMT.failed);
        		step.put(StepJSONKeyz.TDTNoteKey, tdtNotePrjctDLDTAbuse);
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

    public static Set<JSONObject> loadProjects() throws IOException, URISyntaxException
    {
    	
    	Set<JSONObject> prjctSet = new HashSet<>();
    	
    	String path = getPathToDataFolder();

    	File[] listOfFiles = getListOfFilesFromDataFolder();

    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.endsWith(fileMarker))
    		{
    			
    			String joText = loadText(path + name);
    			
    			JSONObject jo = new JSONObject(joText);
    			
    			prjctSet.add(jo);
    		}
    	}
    	
    	return prjctSet;
    }
    
    public static File[] getListOfFilesFromDataFolder()
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
    
    private boolean projectIsTerminated(JSONObject pJSON)
    {
    	String status = pJSON.getString(ProjectJSONKeyz.statusKey);
   
    	Set<String> terminalSet = states.getStatesOfASet(StatusMGMT.terminalSetName);
    	
    	return terminalSet.contains(status);
    }
    
    private boolean stepIsTerminated(JSONObject sJSON)
    {
    	
    	String status = sJSON.getString(StepJSONKeyz.statusKey);
    	   
    	Set<String> terminalSet = states.getStatesOfASet(StatusMGMT.terminalSetName);
    	
    	return terminalSet.contains(status);
    }
    
    private void saveProjects() throws JSONException, IOException
    {
    	for(JSONObject jo: knownProjects.values())
    	{
    		String path = getPathToDataFolder();
    		saveText(path + jo.getString(ProjectJSONKeyz.nameKey)+fileMarker, jo.toString(jsonPrintStyle));
    	}
    }
     
    private void saveStatusMGMT() throws IOException
    {
    	saveObject(getPathToDataFolder()+statesFileName, StatusMGMT.getInstance());
    }

    public void saveAll() throws JSONException, IOException
    { 	
    	saveProjects();
    	saveStatusMGMT();
    }
    
    public void stop() throws JSONException, IOException
    {

    	saveAll();
    	System.out.println(sayGoodBye);
    	noMoreLoops = true;
    }

	@Override
	public void refresh(String arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	
	public Map<String, JSONObject> getProjectMap()
	{
		return knownProjects;
	}
}