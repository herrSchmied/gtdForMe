package jborg.gtdForBash;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.net.URISyntaxException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


import org.json.JSONException;
import org.json.JSONObject;


import someMath.NaturalNumberException;


import allgemein.SimpleLogger;

import allgemein.Beholder;

import allgemein.LittleTimeTools;


import consoleTools.BashSigns;

import consoleTools.InputArgumentException;

import consoleTools.InputStreamSession;


import jborg.gtdForBash.exceptions.CLICMDException;
import jborg.gtdForBash.exceptions.StatisticalToolsException;
import jborg.gtdForBash.exceptions.TimeSpanCreatorException;
import jborg.gtdForBash.exceptions.TimeSpanException;
import jborg.gtdForBash.exceptions.ToolBoxException;
import jborg.gtdForBash.exceptions.WeekDataException;
import static jborg.gtdForBash.ProjectJSONKeyz.*;


import static fileShortCuts.TextAndObjSaveAndLoad.*;



public class GTDCLI implements Beholder<String>
{

	static Clock realClock = Clock.systemDefaultZone();

	Clock useThisClock;

//	private final static String projectSchemaPath = "/projectJSONSchema.json";
//	private final static String modProjectSchemaPath = "/modProjectJSONSchema.json";
//
//	private final static ProjectJSONValidator pjv = new ProjectJSONValidator();;

	private final String statesFileName = "statusMGMT.states";

	public static final String hourListFileName = "hourList.tsdList";
	public static final int hourListIndex = 0;
	public static final String dayListFileName = "dayList.tsdList";
	public static final int dayListIndex = 1;
	public static final String weekListFileName = "weekList.tsdList";
	public static final int weekListIndex = 2;
	public static final String monthListFileName = "monthList.tsdList";
	public static final int monthListIndex = 3;
	public static final String yearListFileName = "yearList.tsdList";
	public static final int yearListIndex = 4;

	Map<ChronoUnit, String> chronoMap = Map.of(ChronoUnit.HOURS, hourListFileName, 
											   ChronoUnit.DAYS, dayListFileName,
											   ChronoUnit.WEEKS, weekListFileName,
											   ChronoUnit.MONTHS, monthListFileName,
											   ChronoUnit.YEARS, yearListFileName);

	
	private final StatusMGMT states = StatusMGMT.getInstance();
	
	//private final List<String> history = new ArrayList<>();
	
	private static Path projectDataFolderRelativePath = Paths.get("projectDATA/");
	private static final String actionLog = "activityLog";
	private final SimpleLogger sLog;
	
	public static final String fileMarker = ".prjct";

	private Map<String, JSONObject> knownProjects = new HashMap<>();

	public static final String isModProjectQ = "Maybe one Day-Project?(yes) or are we actually try "
			+ "to do it soon enough?(no): ";	

	private final Map<String, CLICommand<?>> commandMap;

	boolean noMoreLoops = false;
	
	/*TODO: some only need Testing.
 
	stepsView -> show all Last Steps of all Projects or all Steps of one Project
	
	JSONView -> View JSONObject of a Project.
	 */
 
	private final GTDDataSpawnSession ds;
	private final InputStreamSession iss;
	private final SomeCommands scds;
	
	private final String sayGoodBye = "Bye!";

	//Command related
	private final String hereAListOfCmds = "Here a list of Commands.";
	private final String unknownCmdStr = "Unknown command!";

	
	//File related.
	private final String dataFolderCreated = "Data Folder created successfully.";
	private final String dataFolderFound = "Data Folder found.";
	private final String thereIsNoDataFolder = "There is no Data Folder";
	private final String failedToCreateDirectory = "Failed to create the directory.";
	
	public final int jsonPrintStyle = 4;
	
	public GTDCLI(InputStreamSession iss, Clock clock) throws JSONException, IOException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException, ClassNotFoundException
	{

		this.useThisClock = clock;

    	this.iss = iss;
    	this.sLog =  new SimpleLogger(projectDataFolderRelativePath.toAbsolutePath()+actionLog, "Log of GTD ");
    	System.out.println(sLog.getSessionString());

    	ds = new GTDDataSpawnSession(this.iss, this.useThisClock);
    	Path dataFolder = getDataFolder();

		boolean isThereDataFolder = Files.exists(dataFolder)&&Files.isDirectory(dataFolder);

		if(isThereDataFolder)
		{

			System.out.println(dataFolderFound);

			Set<JSONObject> prjctSet = loadProjects();
			for(JSONObject json: prjctSet)
			{
				String pName = json.getString(ProjectJSONKeyz.nameKey);
				knownProjects.put(pName, json);
			}
			
			scds = new SomeCommands(this, knownProjects, states, ds, sLog, useThisClock, loadTSDLists(getDataFolder()));
			commandMap = scds.getCommandMap();
		}
		else 
		{
			System.out.println(thereIsNoDataFolder);

	        File directory = dataFolder.toFile();

	        // Create the directory
	        if(Files.notExists(dataFolder))
	        {	
	        	if(directory.mkdir())
	        	{
	        		System.out.println(dataFolderCreated);
	        		scds = new SomeCommands(this, knownProjects, states, ds, sLog, useThisClock, loadTSDLists(getDataFolder()));
	        		commandMap = scds.getCommandMap();
	        	}	    				        
	        	else
	        	{
	        	
	        		scds = null;
	        		commandMap = null;
	        		System.out.println(failedToCreateDirectory);
	        		System.out.println(sayGoodBye);
	        		System.exit(0);
	        	}
	        }
	        else
	        {
        		scds = null;
        		commandMap = null;
        		System.out.println(failedToCreateDirectory);
        		System.out.println(sayGoodBye);
        		System.exit(0);
	        }
		}
		
        greetings();

        loopForCommands();

	}
	
    public GTDCLI(InputStreamSession iss) throws JSONException, IOException, URISyntaxException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException, ClassNotFoundException
	{
    	this(iss, realClock);
	}
         
    public void greetings() throws IOException
    {

    	LocalDateTime inTheMoment = LocalDateTime.now(useThisClock);
    	String day = inTheMoment.getDayOfWeek().toString();
    	int day2 = inTheMoment.getDayOfMonth();
    	int year = inTheMoment.getYear();
    	String time = LittleTimeTools.timeString(inTheMoment.toLocalTime());

    	System.out.println("Hello, it is " + day + " the " + day2 + " in the Year "+year);
    	System.out.println("Time: " + time + '\n');
    }

    public static void main(String... args) throws IOException, URISyntaxException, JSONException, NaturalNumberException, WeekDataException, TimeSpanException, ToolBoxException, StatisticalToolsException, TimeSpanCreatorException, InterruptedException, ClassNotFoundException
    {
    	new GTDCLI(new InputStreamSession(System.in));
    }

    public void loopForCommands() throws NaturalNumberException, IOException, JSONException, URISyntaxException, TimeSpanException
    {

    	String px = BashSigns.boldBBCPX;
    	String sx = BashSigns.boldBBCSX;

    	System.out.println("\n");
    	String fullCmdWithOptArgTyped = iss.getString(px + "Type" + sx + " command. (ex. help or exit).");
    	fullCmdWithOptArgTyped = fullCmdWithOptArgTyped.trim();

    	checkAllForDLDTAbuse();
    	saveAll();

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
    				
    				Object obj =  clicmd.executeCmd(argument);
    				if(obj instanceof JSONObject)
    				{
    					
    				}
    				break;
    			}
    			cmdCounter++;
    		}

    		if(cmdCounter==numberOfCmds)
    		{
    			System.out.println('\n'+unknownCmdStr);
    			System.out.println(hereAListOfCmds);
    			CLICommand<?> clicmd = commandMap.get(SomeCommands.list_commands);
    			clicmd.executeCmd("");
    		}
    	}
    	catch(CLICMDException | NaturalNumberException | IOException e)
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

	public void nxtStp(JSONObject pJSON) throws InputArgumentException, IOException, JSONException, URISyntaxException
    {
    	ds.spawnStep(pJSON);
    }

    public void checkAllForDLDTAbuse()
    {
    	
    	System.out.println(BashSigns.boldYBCPX + "Checking for DLDT-Abuse." + BashSigns.boldYBCSX);
    	for(JSONObject pJSON: knownProjects.values())
    	{

    		if(ProjectJSONToolBox.activeProject.test(pJSON))
    		{
    			boolean stepDidIt = ProjectJSONToolBox.checkStepForDeadlineAbuse(pJSON, useThisClock);
    			boolean projectDidIt = ProjectJSONToolBox.checkProjectForDeadlineAbuse(pJSON, useThisClock);
    		
    			if(stepDidIt|| projectDidIt)ProjectJSONToolBox.alterProjectAfterDLDTAbuse(pJSON, stepDidIt, projectDidIt);
    		}
    	}
    }

    public Set<JSONObject> loadProjects() throws IOException, URISyntaxException, InterruptedException
    {

    	Path path = getDataFolder();

    	return loadProjects(path);
    }

    public static Set<JSONObject> loadProjects(Path path) throws IOException, URISyntaxException, InterruptedException
    {

    	Set<JSONObject> prjctSet = new HashSet<>();

    	File[] listOfFiles = getListOfFilesFromDataFolder();

    	if(listOfFiles==null)return prjctSet;
    	
    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.endsWith(fileMarker))
    		{

    			String joText = loadText(path +"/" + name);
    			
    			JSONObject jo = new JSONObject(joText);
        		prjctSet.add(jo);
    		}
  
    	}
    	
    	return prjctSet;
    }

    @SuppressWarnings({ "unchecked"})
	public static List<List<TimeSpanData>> loadTSDLists(Path path) throws IOException, URISyntaxException, InterruptedException, ClassNotFoundException
    {

    	List<List<TimeSpanData>> output = new ArrayList<>();

    	File[] listOfFiles = getListOfFilesFromDataFolder();

    	if(listOfFiles==null)return output;
    	Map<String, File> fileNames = new HashMap<>();
    	
    	for(File file: listOfFiles)
    	{
    		if(file.isFile())fileNames.put(file.getName(), file);
    	}

    	if(fileNames.keySet().contains(hourListFileName))
    	{
    		
    		output.add((List<TimeSpanData>)loadObject(path+"/"+hourListFileName));
    	}
    	else 
    	{
    		List<TimeSpanData> empty = new ArrayList<>();
    		output.add(empty);
    	}
    	
    	if(fileNames.keySet().contains(dayListFileName))
    	{
    		
    		output.add((List<TimeSpanData>)loadObject(path+"/"+dayListFileName));
    	}
    	else 
    	{
    		List<TimeSpanData> empty = new ArrayList<>();
    		output.add(empty);
    	}

    	if(fileNames.keySet().contains(weekListFileName))
    	{
    		
    		output.add((List<TimeSpanData>)loadObject(path+"/"+weekListFileName));
    	}
    	else 
    	{
    		List<TimeSpanData> empty = new ArrayList<>();
    		output.add(empty);
    	}

    	if(fileNames.keySet().contains(monthListFileName))
    	{
    		
    		output.add((List<TimeSpanData>)loadObject(path+"/"+monthListFileName));
    	}
    	else 
    	{
    		List<TimeSpanData> empty = new ArrayList<>();
    		output.add(empty);
    	}
    	
    	if(fileNames.keySet().contains(yearListFileName))
    	{
    		
    		output.add((List<TimeSpanData>)loadObject(path+"/"+yearListFileName));
    	}
    	else 
    	{
    		List<TimeSpanData> empty = new ArrayList<>();
    		output.add(empty);
    	}

    	assert(output.size()==5);
    	
    	return output;
    }

    public static File[] getListOfFilesFromDataFolder()
    {

    	File folder = projectDataFolderRelativePath.toFile();
    	return folder.listFiles();
    }

    public void saveTSDLists() throws TimeSpanException, IOException
    {

	   	List<TimeSpanData> tsdList;
    	for(ChronoUnit cu: chronoMap.keySet())
    	{
    		tsdList = scds.getTSDList(cu);
        	removeTheFutureAndSaveThePast(tsdList, cu);
    	}
    }
    
    public void removeTheFutureAndSaveThePast(List<TimeSpanData> tsdList, ChronoUnit cu) throws IOException
    {

    	String fileName = chronoMap.get(cu);
    	List<TimeSpanData> toBeSaved = new ArrayList<>();
    	for(TimeSpanData tsd: tsdList)
    	{
    		if(tsd.timeSpanIsInThePast())toBeSaved.add(tsd);
    	}

    	saveObject(getDataFolder()+fileName, toBeSaved);
    }

    public void saveProjects() throws JSONException, IOException
    {
    	
    	
    	for(JSONObject jo: knownProjects.values())
    	{
    		saveText(getDataFolder().toString() + "/" + jo.getString(nameKey)+fileMarker, jo.toString(jsonPrintStyle));
    	}
    }

    private void saveStatusMGMT() throws IOException
    {
    	saveObject(getDataFolder()+statesFileName, StatusMGMT.getInstance());
    }

    public void saveAll() throws JSONException, IOException, TimeSpanException
    {

    	saveProjects();
    	saveStatusMGMT();
    	saveTSDLists();
    }
    
    public void stop() throws JSONException, IOException, TimeSpanException
    {

    	saveAll();
    	System.out.println(sayGoodBye);
    	sLog.saveLog();
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
  
    public InputStreamSession getInputStreamSession()
    {
    	return iss;
    }

    public static void setDataFolder(Path newDataFolder)
    {
    	projectDataFolderRelativePath = newDataFolder;
    }
    
    public static Path getDataFolder()
    {
    	return projectDataFolderRelativePath;
    }
    
    public void redirectinStndrtOut(PrintStream os)
    {
    	
    	System.setOut(os);
    	
    	/*
    	 * TODO:
    	 * I don't wan't to see all the output!!!!!!!!!!
    	 * Is this a Solution????????????
    	 */
    }
 }