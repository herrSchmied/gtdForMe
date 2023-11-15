package jborg.gtdForBash;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.json.JSONArray;
import org.json.JSONObject;

import allgemein.LittleTimeTools;
import consoleTools.TerminalTableDisplay;



public class CLICommand <I, O>
{

	
	public final static char optionA = 'a';
	public final static char optionB = 'b';
	public final static char optionC = 'c';
	private final Set<Character> argumentSettings = new HashSet(Arrays.asList(optionA, optionB, optionC));	
	public final boolean mustHaveArgument;
	public final boolean canHaveArgument;
	public final boolean cantHaveArgument;
	
	private final Set<Character> OutputSettings = new HashSet(Arrays.asList(optionA, optionB, optionC));
	public final boolean mustHaveOutput;
	public final boolean canHaveOutput;
	public final boolean cantHaveOutput;
	
	private final Function<I, O> action;
	
	private final String cmdName;
	
	public CLICommand(String command, boolean mustHaveArgument, boolean canHaveArgument, boolean mustHaveOutput,
			boolean canHaveOutput, Function<I, O> action)
	{
		/*
		 * only one of the Argument booleans can be true!!
		 * if you know mustHaveArgument and canHaveArgumet
		 * then you now cantHaveArgument.!!!
		 */
		this.mustHaveArgument = mustHaveArgument;
		this.canHaveArgument = canHaveArgument;

		boolean wrongBothTrue = mustHaveArgument&&canHaveArgument;
		if(wrongBothTrue)throw new IllegalArgumentException("'Must have'-Argument and 'can have'-Argument can't be both true!");
				
		cantHaveArgument = !(mustHaveArgument||canHaveArgument);
		
		/*
		 * only one of the output booleans can be true!!
		 * if you know mustHaveArgument and canHaveArgumet
		 * then you now cantHaveArgument.!!!
		 */
		this.mustHaveOutput = mustHaveOutput;
		this.canHaveOutput = canHaveOutput;

		wrongBothTrue = mustHaveOutput&&canHaveOutput;
		if(wrongBothTrue)throw new IllegalArgumentException("'Must have'-Output and 'can have'-Output can't be both true!");
				
		cantHaveOutput = !(mustHaveOutput||canHaveOutput);
		
		
		this.action = action;
		this.cmdName = command;
	}
	

    public void inCase(String command)
    {
    	/*
    	 *     	switch(command)
    	{
    	
     		
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
    
    	
    		case help: 
    		{
    			System.out.println("");
    			System.out.println("Not yet Installed.");//TODO:
    			break;
    		}
  
    	 */
    }
    
    public boolean hasArgument()
    {
    	return false;
    }
    
    public String getName()
    {
    	return cmdName;
    }
    
    public O executeCmd(I i)
    {
    	return action.apply(i);
    }
}
