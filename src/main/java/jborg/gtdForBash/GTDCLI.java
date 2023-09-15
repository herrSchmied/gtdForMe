package jborg.gtdForBash;


import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;

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
import consoleTools.Input;
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
	
	private static final String exit = "exit";
	private static final String list_not_active_ones = "list not active ones";
	private static final String list_active_ones = "list active ones";
	private static final String next_Step = "next Step";
	private static final String list = "list";
	private static final String help = "help";
	private static final String new_Project ="new Project";
	private static final String list_commands = "list commands";
	private static final String terminate_Project = "terminate Project";
	private static final String add_Note = "add Note";
	
	private static final Set<String> commands = new HashSet<>(Arrays.asList(exit, list, help, 
			list_active_ones, list_not_active_ones, new_Project, next_Step, list_commands, terminate_Project,
			add_Note));

 
	private final DataSpawn_ii ds;
	
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

	/*
	private EventHandler<MouseEvent> stepsView = (mouseEvent)->
	{
		
		ProjectTableViewModel ptvm = detectedSelection();

		if(!tableIsShowingMODProjects)
		{
			if(ptvm!=null)
			{
				
	    		String prjctName = ptvm.getProjectName();
	    		JSONObject jo = projectMap.get(prjctName);
	    		JSONArray ja = jo.getJSONArray(ProjectJSONKeyz.stepArrayKey);//Can't not have no stepArrayJSON
	    		
	    		String s = "";
	    		int size = ja.length();
	    		for(int n=0;n<size;n++)
	    		{
	    			
	    			JSONObject step = (JSONObject)ja.get(n);
	    			String desc = step.getString(StepJSONKeyz.descKey);
	    			String stepStatus = step.getString(StepJSONKeyz.statusKey);
	    			String dldtStr = step.getString(StepJSONKeyz.DLDTKey);
	    			
	    			s = s + "StepNr.: " + n + "\n" 
	    			+ "Status: \n" + stepStatus + "\n"
	    			+ "Description: \n" + desc + "\n"
	    			+ "Deadline: " + dldtStr+"\n\n";
	    		}
	    		
	    		tStage.setInfoText(s);
			}
		}
		else
		{
			tStage.setInfoText("No Steps in MOD-Project.");
		}
	};

	
	private EventHandler<MouseEvent> JSONView = (mouseEvent)->
	{
		
    	ProjectTableViewModel ptvm = detectedSelection();
    	if(ptvm!=null)
    	{
    		String prjctName = ptvm.getProjectName();
    		JSONObject jo;
    		
    		if(!tableIsShowingMODProjects)jo = projectMap.get(prjctName);
    		else jo = modProjectMap.get(prjctName);
    		
    		tStage.setInfoText(jo.toString(jsonPrintStyle));
    	}
	};
	
	private EventHandler<MouseEvent> stndrtView = (mouseEvent)->
    {
    	
    	ProjectTableViewModel ptvm = detectedSelection();
    	if(ptvm!=null)
    	{
    		String prjctName = ptvm.getProjectName();
    		JSONObject jo;
    		
    		String nxt = "";
    		if(!tableIsShowingMODProjects)
    		{
    			jo = projectMap.get(prjctName);
    			JSONArray stepArray = jo.getJSONArray(ProjectJSONKeyz.stepArrayKey);
    			
    			int index = stepArray.length()-1;
    			JSONObject step = stepArray.getJSONObject(index);
    			
    			nxt = "Next Step: " + "\n" + step.getString(StepJSONKeyz.descKey);
    			
    		}
    		else jo = modProjectMap.get(prjctName);
    		
    		String goal = jo.getString(ProjectJSONKeyz.goalKey);
    		String prjctDeadline = jo.getString(ProjectJSONKeyz.DLDTKey);

    		String infoText = "Project: " + prjctName + "\n"
    				+ "Deadline: " + prjctDeadline +"\n\n"
					+ "Goal: " + goal + "\n\n" 
					+ nxt;
    		
    		tStage.setInfoText(infoText);
    	}
    };

    private EventHandler<MouseEvent> currentView = stndrtView;
    */
	
    public GTDCLI() throws ClassNotFoundException, IOException, URISyntaxException
	{
    	
    	ds = new DataSpawn_ii(System.in);
    	
		//states = loadStates();
		if(states==null)states = StatusMGMT.getInstance();
		String javaVersion = SystemInfo.javaVersion();
		System.out.println("Java Version: " + javaVersion);
		
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
    	new GTDCLI();
    }
    
    public void loopForCommands() throws JSONException, IOException, URISyntaxException
    {
    	
    	Input inTaker = new Input(System.in);
    	String px = BashSigns.boldBBCPX;
    	String sx = BashSigns.boldBBCSX;
    	
    	System.out.println("");
    	String command = inTaker.getString(px + "Type" + sx + " command. (ex. help or exit).");
    	
    	System.out.println("Trying to excecute: '" + command.trim() + "'");
    	switch(command.trim())
    	{
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
    			
    			String pName = inTaker.getAnswerOutOfList("Which one?", aPrjcts);
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
    			
    			String pName = inTaker.getAnswerOutOfList("Which one?", aPrjcts);
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
    			List<String> noAPrjcts = findProjectNames(notActiveProject);
    			for(String prjctName: noAPrjcts)System.out.println(prjctName);
    			
    			break;
    		}
 
    		case list_active_ones:
    		{
    			System.out.println("");
    			List<String> aPrjcts = findProjectNames(activeProject);
    			for(String prjctName: aPrjcts)System.out.println(prjctName);
    			
    			break;
    		}

    		case next_Step://Test! Test!
    		{
    			System.out.println("");
    			List<String> aPrjcts = findProjectNames(activeProject);
 
    			String choosenOne = inTaker.getAnswerOutOfList("Which one?", aPrjcts);
    			String prjct = choosenOne.trim();
    			if(aPrjcts.contains(prjct))nxtStp(prjct);
    			else System.out.println("Please choose more wise. Because '" + choosenOne + "' is not"
    					+ " on the List!");
    			break;
    		}
    
    		case list: 
    		{
    			System.out.println("");
    			showProjectTable();
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
    
    public void showProjectTable() throws JSONException
    {
		List<String> headers = columnList;
		List<List<String>> rows = new ArrayList<>();
		
    	for(JSONObject jo: projectMap.values())
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
    
	public void nxtStp(String projectName) throws IOException
    {

		JSONObject jo = projectMap.get(projectName);
    	if(ds.terminateStep(jo))ds.appendStep(jo);
    	else System.out.println("Insufficient Data. Couldn't Terminate Step.");
    }
    	

	/*
    	showMODPrjctsBtn.setOnAction((event)->
    	{
    		
    		tStage.getObservableList().clear();
    		if(!tableIsShowingMODProjects)
    		{
    			showMODPrjctsBtn.setText(modHideTextState);
    		
    			for(JSONObject jo: modProjectMap.values())
    			{
    				ProjectTableViewModel ptvm = createPTVMFromJSON(jo);
    				tStage.getObservableList().add(ptvm);
    			}
    		}
    		else
    		{
    			showMODPrjctsBtn.setText(modShowTextState);
    			
    			for(JSONObject jo: projectMap.values())
    			{
    				ProjectTableViewModel ptvm = createPTVMFromJSON(jo);
    				tStage.getObservableList().add(ptvm);
    			}
    		}
    		
    		tableIsShowingMODProjects = !(tableIsShowingMODProjects);
    	});
    	
    	wakeMODPrjctBtn.setOnAction((event)->
    	{
    		
    		ProjectTableViewModel ptvm = detectedSelection();
    		String prjctName = ptvm.getProjectName();
    		
    		if(!tableIsShowingMODProjects||ptvm==null)Output.errorAlert("U didn't choose a MOD-Project!");
			else
			{
				JSONObject jo = modProjectMap.get(prjctName);
				modProjectMap.remove(prjctName);
				tStage.getObservableList().remove(ptvm);//Clearly the Table is in showing MOD-Project Mode.
				ds.spawnFirstStep(jo);
				projectMap.put(prjctName, jo);
				eraseMODProjectFile(prjctName);
			}
    	});
    	
    	noteViewBtn.setOnAction((event)->
    	{
    		currentView = notesView;
    	});
    	
    	stepsViewBtn.setOnAction((event)->
    	{
    		currentView = stepsView;
    	});
    	
    	JSONViewBtn.setOnAction((event)->
    	{
    		currentView = JSONView;
    	});
    	
    	stndrtViewBtn.setOnAction((event)->
    	{
    		currentView = stndrtView;
    	});
    	
    	addNoteBtn.setOnAction((event)->
    	{
    		guiUtils.doCollectionOfNodes(btnList, disableBtns);
    		
    		ProjectTableViewModel ptvm = detectedSelection();
    		
    		if(ptvm!=null)
    		{
    			
    			JSONObject jo;
    			
    			if(!tableIsShowingMODProjects)jo = projectMap.get(ptvm.getProjectName());
    			else jo = modProjectMap.get(ptvm.getProjectName());

    			ds.addNote(jo);
    		}
    		
    		guiUtils.doCollectionOfNodes(btnList, enableBtns);    		
    	});
    	
    	statsBtn.setOnAction((event)->
    	{
    		int a = 0;
    		int nrOfAllSteps = 0;
    		int successfullSteps = 0;
    		
    		for(JSONObject jo: projectMap.values())
    		{
    			String status = jo.getString(ProjectJSONKeyz.statusKey);
    			boolean isAlive = !states.getStatesOfASet(StatusMGMT.terminalSetName).contains(status);
    			if(isAlive)a++;
    			
    			JSONArray steps = jo.getJSONArray(ProjectJSONKeyz.stepArrayKey);
    			nrOfAllSteps += steps.length();
    			
    			for(int n=0;n<steps.length();n++)
    			{
    				JSONObject step = steps.getJSONObject(n);
    				String stepStatus = step.getString(StepJSONKeyz.statusKey);
    				
    				if(stepStatus.equals(StatusMGMT.success))successfullSteps++;
    			}
    		}
    		
    		int modPrjcts = modProjectMap.size();
    		int n = projectMap.size() + modProjectMap.size();
    		
    		String infoText = "Projects: " + n + "\n"
    						+ "Active: " + a + "\n"
    						+ "MOD-Projects: " + modPrjcts + "\n"
    						+ "Nr. of all Steps: " + nrOfAllSteps +"\n"
    						+ "Successfull Steps: " + successfullSteps;
    		
    		tStage.setInfoText(infoText);
    	});
    	*/
  
    private String getPathOfClass()
    {
    	return "projectDATA/";
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
 
    		
			JSONArray steps = jo.getJSONArray(ProjectJSONKeyz.stepArrayKey);
			int index = steps.length()-1;
			
			JSONObject step = steps.getJSONObject(index);

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
    	String path = getPathOfClass();
    	
    	File folder = new File(path);
    	File[] listOfFiles = folder.listFiles();

    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile()&&file.getName().equals(prjctName+modPrjctMarker))
    			file.delete();
    	}
    }
    
    private void loadMODProjects() throws IOException
    {
    	
    	String path = getPathOfClass();
    	
    	File folder = new File(path);
    	File[] listOfFiles = folder.listFiles();

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
    	String path = getPathOfClass();
    	
    	File folder = new File(path);
    	File[] listOfFiles = folder.listFiles();

    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.equals(statesFileName))
    		{
    			
    	    	return (StatusMGMT) LoadAndSave.loadObject(getPathOfClass()+statesFileName);
    		}
    	}

    	return (StatusMGMT) null;
    }

    private void loadProjects() throws IOException, URISyntaxException
    {
    	
    	String path = getPathOfClass();
    	
    	File folder = new File(path);
    	File[] listOfFiles = folder.listFiles();

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
    		String path = getPathOfClass();
    		LoadAndSave.saveText(path, jo.getString(ProjectJSONKeyz.nameKey)+modPrjctMarker, jo.toString(jsonPrintStyle));
    	}
    }
    
    private void saveProjects() throws JSONException, IOException, URISyntaxException
    {
    	for(JSONObject jo: projectMap.values())
    	{
    		String path = getPathOfClass();
    		LoadAndSave.saveText(path, jo.getString(ProjectJSONKeyz.nameKey)+fileMarker, jo.toString(jsonPrintStyle));
    	}
    }
     
    private void saveStatusMGMT() throws IOException
    {
    	LoadAndSave.saveObject(getPathOfClass()+statesFileName, StatusMGMT.getInstance());
    }

    public void stop() throws JSONException, IOException, URISyntaxException
    {
    	saveProjects();
    	saveMODProjects();
    	saveStatusMGMT();
    	System.out.println("Bye!");
    	System.exit(0);
    }

	@Override
	public void refresh(String arg0) 
	{
		// TODO Auto-generated method stub
		
	}
    
}