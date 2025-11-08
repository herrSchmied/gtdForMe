package jborg.gtdForBash;



import java.sql.SQLException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import allgemein.ExactPeriode;

import allgemein.LittleTimeTools;

import allgemein.SimpleLogger;


import consoleTools.BashSigns;

import consoleTools.InputStreamSession;

import consoleTools.TerminalTableDisplay;


import jborg.gtdForBash.DBIssues.DBSink;
import jborg.gtdForBash.exceptions.CLICMDException;
import static jborg.gtdForBash.ProjectJSONToolbox.*;
import static jborg.gtdForBash.ProjectJSONKeyz.*;


import someMath.NaturalNumberException;

import someMath.exceptions.ConsoleToolsException;



public class SomeCommands
{

	/* Remember: No command can be the beginning of another command.
	 * Write a Method to check that!!!!!!
	 * */

	public static final String oldest = "oldest project";
	public static final String transferSteps = "transfer Steps";
	public static final String transferProjectHeads = "transfer PH";
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

	private static final Set<String> commands = new HashSet<>();

	private static final Set<String> prjctModifierCommands = new HashSet<>();
	
	private static final Set<String> showDataCommands = new HashSet<>();
	
	private static final Set<String> otherCommands = new HashSet<>();

	private final Map<String, CLICommand<?>> commandMap = new HashMap<>();

	private static final String pmcSetName = "Project_Modifier_Cmd_Set";
	private static final String sdcSetName = "Show_Data_Cmd_Set";
	private static final String ocSetName = "Other_Cmd_Set";
	
	private final Map<String, Set<String>> commandSetMap = Map.of(pmcSetName, prjctModifierCommands,
																  sdcSetName, showDataCommands,
																  ocSetName, otherCommands);

	

	
	private final String unknownProject = "Unknown Project!";
	private final String projectIsNotActive = "Project is not active.";
	private final String sorryDeadlineAbuse = "Sorry Deadline Abuse.";
	private final String noPrjctFound = "No Projects found.";
	private final String noMODProjects = "No MOD Projects.";
	private final String noActiveProjects = "No active Projects!";
	private final String noNotActiveProjects = "No not active Projects!";
	private final String noSuchProject = "No such Project: ";
	public final String newPrjctStgClsd = "New Project Stage closed.";
	
	private final String projectStr = "Project";
	private final String nearestDeadlineStr = "nearest Deadline of Last Steps.";
	private final String descStr = "Desc";
	private final String statusStr = "Status";
	private final String deadlineStr = "Deadline";
	
	private final String prjctNameStr = "Project Name";
	private final String adtStr = "NDDT";
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

	private final char wallOfTableChr = '|';
	public final int jsonPrintStyle = 4;
	private List<String> columnList = Arrays.asList("Name", "Status", "ADT", "Age");
	private List<String> stepColumns = Arrays.asList("Desc", "Status", "ADT", "DLDT");


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

    private final InputStreamSession iss;
    private final Map<String, JSONObject> knownProjects;
    
    private final Predicate<String> activePrjctName;
	
	private final Predicate<String> notActivePrjctName;

	public SomeCommands(GTDCLI cli, Map<String, JSONObject> knownProjects, StatusMGMT states, 
    		GTDDataSpawnSession ds, SimpleLogger sLog)
    {

    	this.iss = cli.getInputStreamSession();
    	this.knownProjects = knownProjects;

		activePrjctName = (s)->
		{
			if(!knownProjects.containsKey(s)) return false;
			
			JSONObject pJSON = knownProjects.get(s);
			
			return activeProject.test(pJSON);
		};
		
		notActivePrjctName = (s)->
		{
			if(!knownProjects.containsKey(s)) return false;//Is a must!!
			
			return !activePrjctName.test(s);
		};

		List<Boolean> ioArray;

		/*
		 * MeatOfCLICmd<String> oldestPrjct = (s)-> {
		 * 
		 * StatisticalTools st; try { st = new
		 * StatisticalTools(GTDCLI.loadProjects(GTDCLI.getPathToDataFolder()));
		 * 
		 * String name = st.oldestProjectLDT(ADTKey).getKey(); LocalDateTime oldestADT =
		 * st.oldestProjectLDT(ADTKey).getValue();
		 * 
		 * String output = name + "\nNDT: " + LittleTimeTools.timeString(oldestADT);
		 * System.out.println(output);
		 * 
		 * return name;
		 * 
		 * } catch (URISyntaxException | WeekDataException e) { e.printStackTrace(); }
		 * 
		 * throw new RuntimeException("This should not happen."); };
		 * 
		 * ioArray = new ArrayList<>(Arrays.asList(false, false, true, false));
		 * 
		 * registerCmd(oldest, sdcSetName, ioArray, oldestPrjct);
		 */
	
    	MeatOfCLICmd<String> transSteps = (s)->
		{
			
			sLog.logNow("Transfering Steps.");
			
			
			try
			{
				DBSink db = new DBSink();
				Set<JSONObject> pJSONSet = new HashSet<>(knownProjects.values());
				db.saveStepsOfSet(pJSONSet);
			}
			catch(SQLException sqlExce)
			{
				System.out.println(sqlExce);
			}
			
			return "Oki";
		};
		
		ioArray = new ArrayList<>(Arrays.asList(false, false, false, false));
		
		registerCmd(transferSteps, ocSetName, ioArray, transSteps);

		MeatOfCLICmd<String> transPM = (s)->
		{
			
			sLog.logNow("Transfering PM's.");
			
			
			try
			{
				DBSink db = new DBSink();
				for(JSONObject pJson: knownProjects.values())
				{
					db.save(pJson);
					String name = pJson.getString(ProjectJSONKeyz.nameKey);
					sLog.logNow("Project " + name + " transfered.");
				}
			}
			catch(SQLException sqlExce)
			{
				System.out.println(sqlExce);
			}
			
			return "Oki";
		};
		
		ioArray = new ArrayList<>(Arrays.asList(false, false, false, false));
		
		registerCmd(transferProjectHeads, ocSetName, ioArray, transPM);
		
    	MeatOfCLICmd<JSONObject> newProject = (s)->
		{

			sLog.logNow("Creating new Project.");
			JSONObject pJSON = ds.spawnNewProject(knownProjects.keySet(), states);

			String name = pJSON.getString(ProjectJSONKeyz.nameKey);
			knownProjects.put(name, pJSON);
			sLog.logNow("Project " + name + " created.");
			return pJSON;
		};
		
		ioArray = new ArrayList<>(Arrays.asList(false, false, true, false));
		
		registerCmd(new_Project, pmcSetName, ioArray, newProject);
		
		MeatOfCLICmd<JSONObject> newMODProject = (s)->
		{
			
			sLog.logNow("Attemping to create new MOD Project.");
			
			JSONObject pJSON = ds.spawnMODProject(knownProjects.keySet(), states);

			String name = pJSON.getString(ProjectJSONKeyz.nameKey);
			knownProjects.put(name, pJSON);
			sLog.logNow("New MOD Project " + name + " created.");

			return pJSON;
		};
		
		ioArray.clear();
		ioArray = new ArrayList<>(Arrays.asList(false,false,true,false));
		
		registerCmd(new_MOD, pmcSetName, ioArray, newMODProject);
		
		MeatOfCLICmd<String> leave = (s)->
		{
			
			sLog.logNow("Exit.");
			cli.stop();//Save than exit.
			
			return ""; //Unreachable!!!!
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, false, false));
		
		registerCmd(exit, ocSetName, ioArray, leave);
		
    
		MeatOfCLICmd<String> nearestDeadline = (s)->
		{
			
			sLog.logNow("Nearest Deadline display.");
			LocalDateTime jetzt = LocalDateTime.now();
    		long newMinutes = 1000000;
    		List<String> pList = new ArrayList<>();
    		List<String> dauerList = new ArrayList<>();
    			
			if(knownProjects.isEmpty())
			{
				sLog.logNow("No Projects. No Display");
				throw new CLICMDException(noPrjctFound);
			}

			for(JSONObject pJSON: knownProjects.values())
    		{
    			
				String status = pJSON.getString(ProjectJSONKeyz.statusKey);
				if(status.equals(StatusMGMT.mod))continue;
				
    			String prjctName = pJSON.getString(ProjectJSONKeyz.nameKey);
    				
    			JSONObject lastStep = getLastStep(pJSON);
    				
    			String stepDLDTStr = lastStep.getString(StepJSONKeyz.DLDTKey);
    			if(stepDLDTStr.equals(stepDeadlineNone))continue;
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
    		
    		TerminalTableDisplay ttd;
			try
			{
				ttd = new TerminalTableDisplay(headers, rows,'|', 18);
			}
			catch (ConsoleToolsException e)
			{
				throw new RuntimeException("TerminalTableDisplay did it!");
			}
			
    		System.out.println(ttd);
			
    		return ttd.toString();
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(view_nearest_Deadline, sdcSetName, ioArray, nearestDeadline);
    
		MeatOfCLICmd<String> listNames = (s)->
		{
			
			sLog.logNow("Project Names display.");

			String output = "";
			
			if(knownProjects.isEmpty())
			{
				sLog.logNow("No Projects. No Display.");
				throw new CLICMDException(noPrjctFound);
			}

			for(String name: knownProjects.keySet())output = output + '\n' + name;
			
			System.out.println(output);
			return output;
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(justPrjctNames, sdcSetName, ioArray, listNames);


		MeatOfCLICmd<String> listCmds = (s)->
		{
			
			sLog.logNow("Command list display.");
			String output = "";
			
			List<String> sortedCMDs = new ArrayList<>(commands);
			Collections.sort(sortedCMDs);
			Collections.reverse(sortedCMDs);

			int l = sortedCMDs.size();

			for(int n=0;n<l;n++)
    		{

				String cmdStr = sortedCMDs.get(n);
    			output = "\n" + cmdStr +output;
    		}
    		
    		System.out.println(output + "\n");
    		
    		return output;
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));

		registerCmd(list_commands, sdcSetName, ioArray, listCmds);
		
		
		MeatOfCLICmd<String> projectView = (s)->
		{
			
			sLog.logNow("Trying to view Project Details.");

			if(knownProjects.isEmpty())
			{
				sLog.logNow("No Projects.");
				throw new CLICMDException(noPrjctFound);
			}

			String output = "";

   			System.out.println("");
    		List<String> names = new ArrayList<>();
    		
    		for(String name: knownProjects.keySet())
    		{
    			if(activeProject.test(knownProjects.get(name)))names.add(name);
    		}
    		Collections.sort(names);

    		
	    	String prjct;
	    	if(s.trim().equals(""))prjct=  iss.forcedOutOfList(whichOnePhrase, names);
	    	else prjct = s.trim();
	    		
	    	if(!knownProjects.keySet().contains(prjct))
	    	{
				sLog.logNow("Project " + s + " Does not exist. No Display.");
	    		throw new CLICMDException(noSuchProject+prjct);
	    	}
	    	sLog.logNow("Project " + s + " Exists and details are displayed.");

	    	output = showProjectDetail(knownProjects.get(prjct));
	    	System.out.println(output);


			return output;
		};
		
		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(view_Project, sdcSetName, ioArray, projectView);
		
		MeatOfCLICmd<String> showNotActivePrjcts = (s)->
		{

			sLog.logNow("Inactive Projects display.");
    		Map<String, JSONObject> map = new HashMap<>();
    		List<String> noAPrjcts = findProjectNamesByCondition(notActivePrjctName);
    		
    		if(noAPrjcts.isEmpty())
    		{
    			sLog.logNow("No inactive Projects.");
    			throw new CLICMDException(noNotActiveProjects);
    		}

    		for(String prjctName: noAPrjcts)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			map.put(prjctName, pJSON);
    		}
    		
    		try
			{
				showProjectMapAsTable(map);
			}
    		catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		catch(ConsoleToolsException e)
			{
				throw new RuntimeException("TerminalTableDisplay did it!");
			}
    		
    		return "";
       	};

    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, false, false));
		
		registerCmd(list_not_active_ones, sdcSetName, ioArray, showNotActivePrjcts);
		
		MeatOfCLICmd<String> showActivePrjcts = (s)->
		{
			
			sLog.logNow("Active Projects display.");
			System.out.println("");
			
			Map<String, JSONObject> map = new HashMap<>();
			
			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
			if(aPrjcts.isEmpty())
			{
				sLog.logNow("No active Projects.");
				throw new CLICMDException(noActiveProjects);
			}
			
			for(String prjctName: aPrjcts)
			{
				JSONObject pJSON = knownProjects.get(prjctName);
				map.put(prjctName, pJSON);
			}
			
			try
			{
				showProjectMapAsTable(map);
			}
			catch(JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(ConsoleToolsException e)
			{
				throw new RuntimeException("TerminalTableDisplay did it!");
			}
			
			return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, false, false));
		
		registerCmd(list_active_ones, sdcSetName, ioArray, showActivePrjcts);
		
		MeatOfCLICmd<String> lastSteps = (s)->
		{
			
			sLog.logNow("Last Steps display.");
			List<String> headers = new ArrayList<>(Arrays.asList(projectStr, descStr, statusStr, deadlineStr));
			List<List<String>> rows = new ArrayList<>();

			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
			if(aPrjcts.isEmpty())
			{
				
				sLog.logNow("No active Projects.");
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
			
			
			TerminalTableDisplay ttd;
			try
			{
				ttd = new TerminalTableDisplay(headers, rows,'|', 12);
			}
			catch(ConsoleToolsException e)
			{
				throw new RuntimeException("TerminalTableDisplay did it!");
			}
			
			System.out.println(ttd);
			
			return ttd.toString();
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(view_last_steps_of_Projects, sdcSetName, ioArray, lastSteps);
		
		MeatOfCLICmd<String> stats = (s)->
		{
			
			sLog.logNow("Stats display.");
			if(knownProjects.isEmpty())
			{
				sLog.logNow("No Projects. No Stats.");
				throw new CLICMDException(noPrjctFound);
			}

			int nrOfPrjcts = knownProjects.size();
			int nrOfActivePrjcts = findProjectsByCondition(activeProject).size();
			int nrOfModPrjcts = 0;
		
			
			int nrOfSuccessfulSteps = 0;
			int nrOfSuccessfulPrjcts = 0;
			for(JSONObject pJSON: knownProjects.values())
			{
				if(isMODProject.test(pJSON))nrOfModPrjcts++;
				
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
		
		MeatOfCLICmd<String> saviore = (s)->
		{
			
			sLog.logNow("Saved Data.");
			sLog.saveLog();
			cli.saveAll();
			return "";
		};

    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
		
		registerCmd(save, ocSetName, ioArray, saviore);
		
		MeatOfCLICmd<JSONObject> addNote = (s)->
		{
			
			sLog.logNow("Trying to add a Note to a Project.");

   			System.out.println("");
			List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
			if(aPrjcts.isEmpty())
			{
				sLog.logNow("No Projects. No Note adding.");
				throw new CLICMDException(noActiveProjects);
			}
			
				
			String pName;
			if(s.trim().equals(""))pName = iss.forcedOutOfList(whichOnePhrase, aPrjcts);
			else pName = s.trim();
				
			if(!aPrjcts.contains(pName))
			{
				sLog.logNow("Project " + pName + " don't exist.");
				throw new CLICMDException(noSuchProject + pName);
			}

			JSONObject pJSON = knownProjects.get(pName);

			ds.addNote(pJSON);
			sLog.logNow("Project " + pName + " exists and Note is added.");
			return pJSON;
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
		
		registerCmd(add_Note, pmcSetName, ioArray, addNote);

		MeatOfCLICmd<String> viewNotes = (s)->
		{
	    	
			sLog.logNow("Note display.");
			System.out.println("");
			
			if(knownProjects.isEmpty())throw new CLICMDException(noPrjctFound);
	    	
	    	String prjct;

	    	if(s.trim().equals(""))prjct = iss.forcedOutOfList(notesOfWhichPrjctPhrase, new ArrayList<String>(knownProjects.keySet()));
			else prjct = s.trim();
				
			if(!knownProjects.keySet().contains(prjct))
			{
				sLog.logNow("Project " + prjct + " does not exist. No Note display.");
				throw new CLICMDException(noSuchProject+prjct);
			}
			
    		JSONObject pJSON = knownProjects.get(prjct);
    			
    		JSONArray noteArr;
    		String output = "";
    		if(!pJSON.has(ProjectJSONKeyz.noteArrayKey))	
    		{
    			sLog.logNow(projectStr + " " + prjct + hasNoNotesSuffix);
    			throw new CLICMDException(projectStr + " " + prjct + hasNoNotesSuffix);
    		}
    		sLog.logNow(projectStr + " has notes!");

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
		
		registerCmd(view_Notes, ocSetName, ioArray, viewNotes);

		Supplier<List<String>> listOfMODs = ()->
		{
			
			List<String> modNames = new ArrayList<>();
			
			
			
			for(String prjctName: knownProjects.keySet())
			{
				JSONObject pJSON = knownProjects.get(prjctName);
				
				if(isMODProject.test(pJSON))modNames.add(prjctName);
			}
			
			return modNames;

		};
		
		MeatOfCLICmd<String> listMODs = (s)->
		{
			
			sLog.logNow("MOD-Projects name listing display.");

			Map<String, JSONObject> map = new HashMap<>();
    		
    		List<String>modNames = listOfMODs.get();
    		if(modNames.isEmpty())
    		{
    			sLog.logNow("No MOD-Projects.");
    			throw new CLICMDException(noMODProjects);
    		}
    		
    		for(String prjctName: modNames)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			map.put(prjctName, pJSON);
    		}
    		
    		try
			{
				showProjectMapAsTable(map);
			}
    		catch(JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		catch(ConsoleToolsException e)
			{
				throw new RuntimeException("TerminalTableDisplay did it!");
			}
    
    		return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
	
		registerCmd(list_mod_Projects, sdcSetName, ioArray, listMODs);
		
		MeatOfCLICmd<String> wakeMOD = (s)->
		{
			
			sLog.logNow("Waking MOD-Project.");

			System.out.println("");
    		List<String> modPrjcts = listOfMODs.get();
    		if(modPrjcts.isEmpty())
    		{
    			sLog.logNow("No MOD-Projects.");
    			throw new CLICMDException(noMODProjects);
    		}
   		
    		String prjctName;
    		if(s.trim().equals(""))prjctName = iss.forcedOutOfList(whichOnePhrase, modPrjcts);
    		else prjctName = s.trim();

    		
			if(!modPrjcts.contains(prjctName))
			{
				sLog.logNow("No such MOD-Project. (" + prjctName + ")");
				throw new CLICMDException("No such MOD-Project. (" + prjctName + ")");
			}
				
			JSONObject pJSON = knownProjects.get(prjctName);
			knownProjects.remove(prjctName);
			ds.wakeMODProject(pJSON);
			knownProjects.put(prjctName, pJSON);
			
			return "";
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
	
		registerCmd(wake_MOD, pmcSetName, ioArray, wakeMOD);

		MeatOfCLICmd<JSONObject> nextStep = (s)->
		{

			sLog.logNow("Trying to create next Step for Project");

			System.out.println("");
    		List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
    		if(aPrjcts.isEmpty())
    		{
    			sLog.logNow("No active Projects. No next Step.");
    			throw new CLICMDException(noActiveProjects);
    		}
    		
    		String prjct;
    		if(s.trim().equals(""))prjct = iss.forcedOutOfList(whichOnePhrase, aPrjcts);
    		else prjct = s.trim();
    		
    		if(!knownProjects.keySet().contains(prjct))
    		{
    			sLog.logNow("Project " + prjct + "does not exist. So no next Step.");
    			throw new CLICMDException(noSuchProject);
    		}
    		
    		if(!aPrjcts.contains(prjct))throw new CLICMDException(projectIsNotActive);
    		
    		JSONObject pJSON = knownProjects.get(prjct);
			boolean stepDidIt = checkStepForDeadlineAbuse(pJSON);
			boolean projectDidIt = checkProjectForDeadlineAbuse(pJSON);
    			
    		if(stepDidIt||projectDidIt)
    		{
    			alterProjectAfterDLDTAbuse(pJSON, stepDidIt, projectDidIt);
    			sLog.logNow("Deadline abuse so no next Step for Project " + prjct + ".");

				throw new CLICMDException(sorryDeadlineAbuse);
    		}
 
    		ds.spawnStep(pJSON);
    		sLog.logNow("Step created.");

    		return pJSON;
    		
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
	
		registerCmd(next_Step, pmcSetName, ioArray, nextStep);

		MeatOfCLICmd<JSONObject> killPrjct = (s)->
		{

			sLog.logNow("Trying to kill an active Project.");
			System.out.println("");
    		List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);
    		if(aPrjcts.isEmpty())
    		{
    			sLog.logNow("No active Projects no Kill.");
    			throw new CLICMDException(noActiveProjects);
    		}

    		String pName;
    		if(s.trim().equals(""))pName = iss.forcedOutOfList(whichOnePhrase, aPrjcts);
    		else pName = s.trim();
    			
    		if(!knownProjects.keySet().contains(pName))
    		{
    			sLog.logNow(noSuchProject + pName + ". No Kill.");
    			throw new CLICMDException(noSuchProject + pName);
    		}
    			
    		if(!aPrjcts.contains(pName))
    		{
    			sLog.logNow("Project: " + pName + " is not active. No Kill.");
    			throw new CLICMDException(projectIsNotActive);
    		}
    		
    		JSONObject pJSON = knownProjects.get(pName);
        	boolean projectDidIt = checkProjectForDeadlineAbuse(pJSON);

        	if(projectDidIt)
        	{
        		alterProjectAfterDLDTAbuse(pJSON, false, projectDidIt);
        		if(projectDidIt)sLog.logNow("Project: " + pName + " is already Dead. No Kill");
        		throw new CLICMDException(sorryDeadlineAbuse);
    		}

    		ds.terminateProject(pJSON);
    		sLog.logNow("Terminated Project: " + pName);

    		return pJSON;
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, false, false));
	
		registerCmd(terminate_Project, pmcSetName, ioArray, killPrjct);
		
		MeatOfCLICmd<JSONObject> killStep = (s)->
		{

			sLog.logNow("Terminating Step.");
    		System.out.println("");
    		List<String> aPrjcts = findProjectNamesByCondition(activePrjctName);

    		String pName;
    		if(s.trim().equals(""))pName = iss.forcedOutOfList(whichOnePhrase, aPrjcts);
    		else pName = s.trim();
    			
    		if(!knownProjects.keySet().contains(pName))
    		{
    			sLog.logNow(noSuchProject + pName + ". No Step Termination.");
    			throw new CLICMDException(noSuchProject+pName);
    		}
    		
    		if(!aPrjcts.contains(pName))
    		{
    			sLog.logNow("Project: " + pName + " is not active. No Step Termination.");
    			throw new CLICMDException(projectIsNotActive);
    		}
    		
    		JSONObject pJSON = knownProjects.get(pName);
        	boolean stepDidIt = checkStepForDeadlineAbuse(pJSON);
        	boolean projectDidIt = checkProjectForDeadlineAbuse(pJSON);

    		if(stepDidIt||projectDidIt)
    		{
    			
    			alterProjectAfterDLDTAbuse(pJSON, stepDidIt, projectDidIt);
    			if(stepDidIt)sLog.logNow("Step of Project " + pName + " is already Terminated.");
    			if(projectDidIt)sLog.logNow("Project " + pName + " is Terminated. No Step Termination.");
    			throw new CLICMDException(sorryDeadlineAbuse);
    		}
    			
    		ds.terminateStep(pJSON);  				
   
    		return pJSON;
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
		
		registerCmd(terminate_Step, pmcSetName, ioArray, killStep);
		
		MeatOfCLICmd<String> hilfe = (s)->
		{
			sLog.logNow("Help display.");
			String output = "Not yet Installed.";//TODO:;
			System.out.println(output);
			
			return output;
		};
		
    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, true, true, false));
	
		registerCmd(help, ocSetName, ioArray, hilfe);
		
		MeatOfCLICmd<String> win = (s)->
		{
			
			sLog.logNow("Displaying successes.");
			
    		Map<String, JSONObject> map = new HashMap<>();
    		List<String> noAPrjcts = findProjectNamesByCondition(notActivePrjctName);
    		
    		if(noAPrjcts.isEmpty())
    		{
    			sLog.logNow("No inactive Projects. No Winners.");
    			throw new CLICMDException(noNotActiveProjects);
    		}

    		for(String prjctName: noAPrjcts)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			if(pJSON.getString(ProjectJSONKeyz.statusKey).equals(StatusMGMT.success))
    				map.put(prjctName, pJSON);
    		}
    		
    		try
			{
				showProjectMapAsTable(map);
			}
    		catch(JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		catch(ConsoleToolsException e)
			{
				throw new RuntimeException("TerminalTableDisplay did it!");
			}
    		
    		if(map.isEmpty())sLog.logNow("No Successes.");
    		else sLog.logNow("Displayed Successes.");
    		
    		return "";
		};

    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
	
		registerCmd(successes, sdcSetName, ioArray, win);

		MeatOfCLICmd<String> suckerz = (s)->
		{
			
			sLog.logNow("Display fails.");

    		Map<String, JSONObject> map = new HashMap<>();
    		List<String> noAPrjcts = findProjectNamesByCondition(notActivePrjctName);
    		
    		if(noAPrjcts.isEmpty())
    		{
    			sLog.logNow("No active Projects so no fails. No Display.");
    			throw new CLICMDException(noNotActiveProjects);
    		}

    		for(String prjctName: noAPrjcts)
    		{
    			JSONObject pJSON = knownProjects.get(prjctName);
    			if(pJSON.getString(ProjectJSONKeyz.statusKey).equals(StatusMGMT.failed))
    				map.put(prjctName, pJSON);
    		}
    		
    		try
			{
				showProjectMapAsTable(map);
			}
    		catch(JSONException e)
			{

				e.printStackTrace();
			}
    		catch(ConsoleToolsException e)
			{
				throw new RuntimeException("TerminalTableDisplay did it!");
			}

    		if(map.isEmpty())sLog.logNow("No fails.");
    		else sLog.logNow("Displayed fails.");
 
    		return "";
		};

    	ioArray.clear();
		ioArray.addAll(Arrays.asList(false, false, true, false));
	
		registerCmd(fails, sdcSetName, ioArray, suckerz);
		
		MeatOfCLICmd<String> showSteps = (s)->
		{
    
			sLog.logNow("Showing Steps of Project " + s);
			String t = s.trim();
			if(!knownProjects.containsKey(t))
			{
				sLog.logNow("Project " + s + " does not exist so no display.");
				throw new CLICMDException(unknownProject);
			}
			
			JSONObject pJSON = knownProjects.get(t);

			try
			{
				showProjectStepsAsTable(pJSON);
			}
			catch(ConsoleToolsException e)
			{
				throw new RuntimeException("TerminalTableDisplay did it!");
			}
			sLog.logNow("Displayed Steps of Project: " + s);
			
    		return "";
		};

		ioArray.clear();
		ioArray.addAll(Arrays.asList(true, false, true, false));
		
		registerCmd(show_Steps_of, sdcSetName, ioArray, showSteps);
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
    
    public Map<String, CLICommand<?>> getCommandMap()
    {
    	return commandMap;
    }
    
 
    

        
    
 
    public String showProjectDetail(JSONObject pJSON)
    {
    	String gpx = BashSigns.boldGBCPX;
    	String gsx = BashSigns.boldGBCSX;

    	String name = pJSON.getString(ProjectJSONKeyz.nameKey);
    	String status = pJSON.getString(ProjectJSONKeyz.statusKey);
    	String adt = pJSON.getString(ADTKey);
    	String goal = pJSON.getString(ProjectJSONKeyz.goalKey);

    	int stpNr = 0;
    	if(!isMODProject.test(pJSON))
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
    					gpx + adtStr + ":" + gsx + " " + adt + '\n' +
    					gpx + deadlineStr + ":" + gsx + " " + dldtStr + '\n' +
    					gpx + goalStr + ":" + gsx + " " + goal + '\n' +
    					gpx + stepsStr + ":" + gsx + " " + stpNr + '\n' +
    					gpx + notesStr + ":" + gsx + " " + noteNr;
    	
    	return output;
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

    public void showProjectStepsAsTable(JSONObject pJSON) throws ConsoleToolsException
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
				String ndt = step.getString(StepJSONKeyz.ADTKey);
				String dldt = step.getString(StepJSONKeyz.DLDTKey);

	    		List<String> row = new ArrayList<>();
	    		row.add(desc);
	    		row.add(stepStatus);
	    		row.add(ndt);
	    		row.add(dldt);
	    		
	    		rows.add(row);

			}

			TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows, wallOfTableChr, 20);
			
			System.out.println(ttd.toString());
		}
		
		

    }
    
    public void showProjectMapAsTable(Map<String, JSONObject> map) throws JSONException, NaturalNumberException, ConsoleToolsException
    {
 
		List<String> headers = columnList;
		List<List<String>> rows = new ArrayList<>();
		
    	for(JSONObject jo: map.values())
    	{
    		String name = jo.getString(nameKey);
    		String status = (String) jo.getString(statusKey);
    		String adtStr = (String) jo.getString(ADTKey);

    		LocalDateTime jetzt = LocalDateTime.now();
    		LocalDateTime adt = LittleTimeTools.LDTfromTimeString(adtStr);
    		String age = new ExactPeriode(adt, jetzt).toString();	

    		List<String> row = new ArrayList<>();
    		row.add(name);
    		row.add(status);
    		row.add(adtStr);
    		row.add(age);
    		
    		rows.add(row);
    	}
    	
		TerminalTableDisplay ttd = new TerminalTableDisplay(headers, rows, wallOfTableChr, 20);
		
		System.out.println(ttd.toString());
    }
}
