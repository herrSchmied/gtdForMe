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
	
	private final String noPrjctFound = "No Projects found.";
	private final String unknownCmdStr = "Unknown command!";
	private final String hereAListOfCmds = "Here a list of Commands.";
	private final String dataFolderFound = "Data Folder found.";
	private final String thereIsNoDataFolder = "There is no Data Folder";
	private final String dataFolderCreated = "Data Folder created successfully.";
	private final String failedToCreateDirectory = "Failed to create the directory.";
	private final String noActivePrjctsStr = "";
	
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
	
	/* Remember: No command can be the beginning of another command.
	 * Write a Method to check that!!!!!!
	 * */
	private final String save = "save";
	private final String exit = "exit";
	private final String list_not_active_ones = "show not active ones";
	private final String list_active_ones = "show active ones";
	private final String list_mod_Projects = "list mod projects";
	//private static final String correct_last_step = "correct last step";//TODO
	private final String view_Project = "view project";
	private final String view_last_steps_of_Projects = "last steps";
	private final String view_nearest_Deadline = "nearest deadline";
	private final String view_statistics = "stats";
	private final String next_Step = "next step";
	private final String tblList = "show projects";
	private final String justPrjctNames = "names";
	private final String help = "help";
	private final String new_Project ="new project";
	private final String new_MOD = "new mod";
	private final String list_commands = "show cmds";
	private final String terminate_Project = "terminate project";
	private final String add_Note = "add note";
	private final String view_Notes = "show notes";
	
	private final Set<String> commands = new HashSet<>();

	private final Set<String> prjctModifierCommands = new HashSet();
	
	private final Set<String> showDataCommands = new HashSet();
	
	private final Set<String> otherCommands = new HashSet();
	
	private final Map<String, CLICommand> commandMap = new HashMap<>();
	
	private final String pmcSetName = "Project_Modifier_Cmd_Set";
	private final String sdcSetName = "Show_Data_Cmd_Set";
	private final String ocSetName = "Other_Cmd_Set";
	
	private final Map<String, Set<String>> commandSetMap = Map.of(pmcSetName, prjctModifierCommands,
																  sdcSetName, showDataCommands,
																  ocSetName, otherCommands);
	
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
			} 
			catch(InputMismatchException | SpawnProjectException | TimeGoalOfProjectException 
					| SpawnStepException | IOException e) 
			{
				e.printStackTrace();
			}
			
			System.out.println(noPrjctStr);
			
			return new JSONObject();//Empty
		};
		
		List<Boolean> ioArray = new ArrayList<>(Arrays.asList(false,false,true,false));
		
		registerCmd(new_Project, pmcSetName, ioArray, newProject);
		
    	Function<String, JSONObject> newMODProject = (s)->
		{
			
			try
			{
				JSONObject pJSON = ds.spawnMODProject(knownProjects.keySet(), states);
				
				
				String name = pJSON.getString(ProjectJSONKeyz.nameKey);
				knownProjects.put(name, pJSON);
				modProjectMap.put(name, pJSON);
				return pJSON;
			} 
			catch(InputMismatchException | SpawnProjectException | IOException e) 
			{
				e.printStackTrace();
			}
			
			System.out.println(noPrjctStr);
			
			return new JSONObject();//Empty
		};
		
		ioArray.clear();
		ioArray = new ArrayList<>(Arrays.asList(false,false,true,false));
		
		registerCmd(new_MOD, pmcSetName, ioArray, newMODProject);
		
		Function<String, String> leave = (s)->
		{
			try 
			{
				stop();//Save than exit.
			} 
			catch (JSONException | IOException | URISyntaxException e)
			{
				System.out.println("something went wrong!!\n" + e);
				System.exit(0);//Bye. Exit maybe without saving.
			}
			
			return ""; //Unreachable!!!!
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, false, false));
		
		registerCmd(exit, ocSetName, ioArray, leave);
    
		Function<String, String> prjctList = (s)->
		{
			
			System.out.println("");
			return showProjectMapAsTable(knownProjects);
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
				
		registerCmd(tblList, sdcSetName, ioArray, prjctList);
    
    
		Function<String, String> nearestDeadline = (s)->
		{
			LocalDateTime jetzt = LocalDateTime.now();
    		long newMinutes = 1000000;
    		List<String> pList = new ArrayList<>();
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
    			row.add(dauerList.get(n));
    				
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
    
		Function<String, String> listNames = (s)->
		{
			
			String output = "";
			
			if(knownProjects.isEmpty())
			{
				System.out.println(this.noPrjctFound);
				return "";
			}
			for(String name: knownProjects.keySet())output = output + '\n' + name;
			
			System.out.println(output);
			return output;
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(this.justPrjctNames, sdcSetName, ioArray, listNames);


		Function<String, String> listCmds = (s)->
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
		
		Function<String, String> lastSteps = (s)->
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
			
			return ttd.toString();
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(view_last_steps_of_Projects, sdcSetName, ioArray, lastSteps);
		
		Function<String, String> projectView = (s)->
		{
			String output = "";

   			System.out.println("");
    		List<String> names = new ArrayList<>();
    		names.addAll(knownProjects.keySet());
    		if(names.isEmpty())
    		{
    			System.out.println(noPrjctFound);
    			return "";
    		}
    		
			try 
			{
	    		String prjct;
	    		if(s.trim().equals(""))prjct=  iss.getAnswerOutOfList(whichOnePhrase, names);
	    		else prjct = s.trim();
	    		
	    		if(knownProjects.keySet().contains(prjct))
	    		{
	    			output =showProjectDetail(knownProjects.get(prjct));
	    			System.out.println(output);
	    		}
	    		else System.out.println(chooseMoreWiselyPreFix + prjct + itsNotOnTheListSuffix);

			} 
			catch (InputMismatchException | IOException e) 
			{
				e.printStackTrace();
				return null;
			}
    		
			return output;
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(view_Project, sdcSetName, ioArray, projectView);
		
		Function<String, String> showNotActivePrjcts = (s)->
		{

    		Map<String, JSONObject> map = new HashMap<>();
    		List<String> noAPrjcts = findProjectNamesByCondition(notActivePrjctName);
    		for(String prjctName: noAPrjcts)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			map.put(prjctName, pJSON);
    		}
    		
    		return showProjectMapAsTable(map);
    	};

    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(list_not_active_ones, sdcSetName, ioArray, showNotActivePrjcts);
		
		Function<String, String> showActivePrjcts = (s)->
		{
			System.out.println("");
			
			Map<String, JSONObject> map = new HashMap<>();
			
			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
			for(String prjctName: aPrjcts)
			{
				JSONObject pJSON = projectMap.get(prjctName);
				map.put(prjctName, pJSON);
			}
			
			return showProjectMapAsTable(map);
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(list_active_ones, sdcSetName, ioArray, showActivePrjcts);
		
		Function<String, String> stats = (s)->
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
		
		Function<String, String> save = (s)->
		{
			try 
			{
				saveAll();
			}
			catch (JSONException | IOException | URISyntaxException e) 
			{
				
				System.out.println("Something went wrong.");
				e.printStackTrace();
			}
			
			return "";

		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(this.save, ocSetName, ioArray, save);
		
		Function<String, String> addNote = (s)->
		{
			
   			System.out.println("");
			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
			if(aPrjcts.isEmpty())
			{
				System.out.println(noActivePrjctsStr);
				return "";
			}
			
			
			
			try
			{
				
				String pName;
				if(s.trim().equals(""))pName = iss.getAnswerOutOfList(whichOnePhrase, aPrjcts);
				else pName = s.trim();
				
				if(aPrjcts.contains(pName))
				{
					JSONObject pJSON = projectMap.get(pName);
					checkForDeadlineAbuse(pJSON);
					ds.addNote(pJSON);
				}
				else 
				{
					System.out.println("No such Project.");
					return "";
				}
			}
			catch(InputMismatchException | IOException e)
			{
				e.printStackTrace();
			}
			
			return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
		
		registerCmd(this.add_Note, ocSetName, ioArray, addNote);

		Function<String, String> viewNotes = (s)->
		{
	    			
			System.out.println("");
			
			if(knownProjects.isEmpty())
			{
				System.out.println(this.noPrjctFound);
				return "";
			}
	    	List<String> names = new ArrayList<>();
	    	names.addAll(knownProjects.keySet());
	 
	    	
	    	String prjct;
			try
			{
				if(s.trim().equals(""))prjct = iss.getAnswerOutOfList(notesOfWhichPrjctPhrase, names);
				else prjct = s.trim();
				
				if(!knownProjects.keySet().contains(prjct))
				{
					System.out.println("No such Project.");
					return "";
				}
				
    			JSONObject pJSON = knownProjects.get(prjct);
    			
    			JSONArray noteArr;
    			String output = "";
    			if(pJSON.has(ProjectJSONKeyz.noteArrayKey))
    			{
    				noteArr = pJSON.getJSONArray(ProjectJSONKeyz.noteArrayKey);
    				int l = noteArr.length();
    				
    				for(int n=0;n<l;n++)
    				{
    					output = output + "--> " + noteArr.get(n);
    				}
    				
    				System.out.println(output);
    				return output;
    			}
    			else System.out.println(projectStr + " " + prjct + hasNoNotesSuffix);

			}
			catch (InputMismatchException | IOException e)
			{
				System.out.println("Something went wrong.");
				e.printStackTrace();
			}

			return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(this.view_Notes, ocSetName, ioArray, viewNotes);

    }

	/** @param ioArray index 0 = mustHaveArgument	*
	 *  @param ioArray index 1 = canHaveArgument	*
	 *  @param ioArray index 2 = mustHaveOutput		*
	 *  @param ioArray index 3 = canHaveOutput		*/
    public <O> void registerCmd(String cmdName, String setName, List<Boolean>ioArray, Function<String, O>action)
    {

    	CLICommand<O> cliCmd = new CLICommand<O>(cmdName, ioArray.get(0), ioArray.get(1), ioArray.get(2), ioArray.get(3), action);
    	commandMap.put(cmdName, cliCmd);
    	commands.add(cmdName);
    	Set<String> cmdSet = commandSetMap.get(setName);
    	cmdSet.add(cmdName);
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
    				
    				Object obj = clicmd.executeCmd(argument);
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
    	catch(CLICMDException e)
    	{
    		e.printStackTrace();
    	}
    	
    	loopForCommands();
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

    public String showProjectMapAsTable(Map<String, JSONObject> map) throws JSONException
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
		return ttd.toString();
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