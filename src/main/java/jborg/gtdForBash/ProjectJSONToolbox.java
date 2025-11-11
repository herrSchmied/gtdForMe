package jborg.gtdForBash;



import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;


import org.json.JSONArray;
import org.json.JSONObject;


import allgemein.LittleTimeTools;
import jborg.gtdForBash.exceptions.ToolBoxException;

import static jborg.gtdForBash.ProjectJSONKeyz.*;

public class ProjectJSONToolbox
{

	/** first Step Index.*/
	public static final int firstStepIndex = 0;
	/** Project time Error index, Project needs DLDT*/
	public static final int indexOfPrjctTimeErrorNeedsDLDT = 0;
	/** Project time Error index, Project dead before born.*/
	public static final int indexOfPrjctTimeErrorDeadBeforeBorn = 1;
	/** Project time Error index, Project born after now.*/
	public static final int indexOfPrjctTimeErrorBornAfterNow = 2;
	/** Project time Error index, Project needs valid Goal.*/
	public static final int indexOfPrjctTimeErrorNeedsGoal = 3;
	/** Project time Error Array.*/
	public static final String prjctTimeError[] = new String[] 
			{"Project needs Deadline.", "Project dead before Living.", "Project Born after Now.", 
					"Project needs a Goal."};

	public static final String stepIsViolatingTimeframeOfFormerStepMsg = "Step is violating timeframe of former Step";
	public static final String stepIsNotViolatingTimeframeOfFormerStepMsg = "Step is not violating timeframe of former Step";

	public static final String stepIsNotViolatingTimeframeOfProjectMsg = "Step is not violating Timeframe of Project.";
	public static final String stepIsViolatingTimeframeOfProjectMsg = "Step is violating timeframe of former Step";

	public static final List<String> stepIsNotOkayToItSelfMsgList = 
			new ArrayList<>(Arrays.asList("Step is Born after Now.",
							"Step has no Description. Please write a Description",
							"Deadline of Step can't be before Step Born.", 
							"Step can't be noted down before Born.",
							"Step can't have deadline before noted down."
							));
	
	public static final int indexOfStpIsBornAfterNow = 0;
	public static final int indexOfStpHasNoDesc = 1;
	public static final int indexOfStpDLDTBeforeBDT = 2;
	public static final int indexOfStpNotedBeforeBorn = 3;
	public static final int indexOfStpDLDTBeforeNDDT = 4;


	/** Possible Project Deadline Status.*/
	public static final String prjctDeadlineNone = "No_got_no_Deadline!";
	/** Possible Status for Step Deadline*/
	public final static String stepDeadlineNone = "No_Step_Deadline!";	
	public static final String deadLineUnknownStr = "UNKNOWN";//If MOD-Project

	public static final String stepIsOkToItSelfMsg = "Step is Ok to it Self.";

	public static final String stepTimeDataIsValide = "Step Time Data is valide.";

	
	public static boolean stepIsTerminated(JSONObject sJSON)
	{
		return sJSON.has(StepJSONKeyz.TDTKey);
	}

	public static boolean stepHasDLDT(JSONObject sJSON)
	{
		if(!sJSON.has(StepJSONKeyz.DLDTKey))return false;

		String dldtStr = sJSON.getString(StepJSONKeyz.DLDTKey);
		if(dldtStr.equals(stepDeadlineNone))return false;
		
		return true;
	}
	/**
	 * Returns the Index of last(youngest) Step JSON Object in
	 * Project JSON Object. If there is no StepArray or no Steps
	 * it returns a negative value;
	 * 
	 * @param pJson Project-Data.
	 * 
	 * @return int index of last Step.
	 */
	public static int getIndexOfLastStepInPrjct(JSONObject pJson)
	{
		JSONArray stepArray;
		
		if(pJson.has(ProjectJSONKeyz.stepArrayKey))
		{
			stepArray = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);
			return stepArray.length()-1;
		}
		
		return firstStepIndex-1;
	}

	/**
	 * This gets u the last (youngest) step of the Project in question.
	 * If for some Reason the Project has no StepArray or no Step JSON
	 * Objects in that Array it returns null.
	 * 
	 * @param pJson Project JSONObject
	 * 
	 * @return step JSONObject
	 */
	public static JSONObject getLastStepOfProject(JSONObject pJson)
	{
		int indexOfLastStep = getIndexOfLastStepInPrjct(pJson);
		
		JSONArray stepArray = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);
	
		return stepArray.getJSONObject(indexOfLastStep);
	}

	public static JSONObject getStepOfIndexN(int n, JSONObject pJson) throws ToolBoxException
	{
		int indexOfLastStep = getIndexOfLastStepInPrjct(pJson);
		if(n>indexOfLastStep)throw new ToolBoxException("Index of Step request to big.");
		if(n<0)throw new ToolBoxException("Index of Step smaller than Zero.");
		

		JSONArray stepArray = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);
	
		return stepArray.getJSONObject(n);
	}

	public static boolean checkProjectForDeadlineAbuse(JSONObject pJSON)
	{
			
		if(projectIsTerminated.test(pJSON))return false;
	
		LocalDateTime jetzt = LocalDateTime.now();
	
		String projectDLDTStr = pJSON.getString(ProjectJSONKeyz.DLDTKey);
		if(projectDLDTStr.equals(prjctDeadlineNone))return false;
		
		LocalDateTime projectDLDT = LittleTimeTools.LDTfromTimeString(projectDLDTStr);
	
		if(projectDLDT.isBefore(jetzt)) return true;//Is Project DLDT abused?
	
		return false;
	}

	public static boolean checkStepForDeadlineAbuse(JSONObject pJSON)
	{
	
		if(projectIsTerminated.test(pJSON))return false;
		
		LocalDateTime jetzt = LocalDateTime.now();
			
		JSONObject step = getLastStepOfProject(pJSON);
	
		if(stepIsTerminated.test(step))return false;
		
		String dldtStr = step.getString(StepJSONKeyz.DLDTKey);
		if(dldtStr.equals(stepDeadlineNone))return false;
		
		LocalDateTime stepDLDT = LittleTimeTools.LDTfromTimeString(dldtStr);
				
		if(stepDLDT.isBefore(jetzt)) return true;//Is Step DLDT abused?
		
		return false;
	}

	public static JSONObject pickProjectByName(String pName, Set<JSONObject> projects)
	{
	
		for(JSONObject pJSON: projects)
		{
			assert(pJSON.has(ProjectJSONKeyz.nameKey));
			String name = pJSON.getString(ProjectJSONKeyz.nameKey);
			if(name.equals(pName)) return pJSON;
		}
		
		return null;
	}

	public static void alterProjectAfterDLDTAbuse(JSONObject pJSON, boolean stepDidIt, boolean projectDidIt)
	{
		
		JSONObject step = getLastStepOfProject(pJSON);
		
		
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
			
			if(!stepIsTerminated.test(step))//if step is not already Terminal alter step status and TDT(Note) too.
			{
				
	    		step.put(StepJSONKeyz.TDTKey, projectDLDTStr);
	    		step.put(StepJSONKeyz.statusKey, StatusMGMT.failed);
	    		step.put(StepJSONKeyz.TDTNoteKey, tdtNotePrjctDLDTAbuse);
			}
		}
	}

	public static final String tdtNotePrjctDLDTAbuse = "Project Deadline abuse!";
	public static final String tdtNoteStpDLDTAbuse = "Step Deadline abuse!";

	public static final Predicate<JSONObject> isMODProject = (pJSON)->
	{
		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		return status.equals(StatusMGMT.mod);
	};

	public static final Predicate<JSONObject> projectIsTerminated = (jo)->
	{
	
	    String status = jo.getString(ProjectJSONKeyz.statusKey);
	    
	    StatusMGMT states = StatusMGMT.getInstance();
	
	    Set<String> terminalSet = states.getStatesOfASet(StatusMGMT.terminalSetName);
	    	
	    return terminalSet.contains(status);
	};

	public static final Predicate<JSONObject> lastStepIsTerminated = (jo)->
	{
	
		JSONObject sJSON = getLastStepOfProject(jo);
		
	    String status = sJSON.getString(StepJSONKeyz.statusKey);
	    
	    StatusMGMT states = StatusMGMT.getInstance();
	
	    Set<String> terminalSet = states.getStatesOfASet(StatusMGMT.terminalSetName);
	    	
	    return terminalSet.contains(status);
	};

	public static final Predicate<JSONObject> projectHasNoDLDT = (jo)->
	{
		if(isMODProject.test(jo))return true;
		if(!jo.has(ProjectJSONKeyz.DLDTKey))return true;
		
		String dldtStr = jo.getString(ProjectJSONKeyz.DLDTKey);
		if(dldtStr.equals(prjctDeadlineNone))return true;
	
		return false;
	};

	public static final Predicate<JSONObject> activeProject = (jo)->
	{
		if(projectIsTerminated.test(jo))return false;
		
		if(isMODProject.test(jo))return false;
		
		return true;
	};

	/**
	 * Simple Check does what the Name suggests.
	 *
	 * @param pJson Project-Data.
	 *
	 * @return the answer to the question: "Is Project Terminated".
	 */
	public static boolean projectIsAlreadyTerminated(JSONObject pJson)
	{
		
		String status = pJson.getString(ProjectJSONKeyz.statusKey);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		String terminalSetName = StatusMGMT.terminalSetName;		
		Set<String> terminalSet = statusMGMT.getStatesOfASet(terminalSetName);
		
		if(terminalSet.contains(status))return true;
		
		return false;
	}

	/**
	 * Simple check if Status of Step is a terminal one.
	 * 
	 * @param sJson Step Data.
	 * 
	 * @return true if Status of Step is a terminal one.
	 */
	public static boolean stepIsAlreadyTerminated(JSONObject sJson)
	{
		
		String status = sJson.getString(StepJSONKeyz.statusKey);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		String terminalSetName = StatusMGMT.terminalSetName;
		
		Set<String> terminalSet = statusMGMT.getStatesOfASet(terminalSetName);
		if(terminalSet.contains(status))return true;
		
		return false;
	}

	/**
	 * Checks if Goal and the DateTimes make Sense.
	 * The oldest DateTime is BirthDateTime (bdt). The equally old or younger NoteDownDateTime (nddt).
	 * The youngest always younger than nddt is if exists DeadlineDateTime (dldt).
	 * 
	 * @param nddt NoteDownDateTime. Always close to now.
	 * @param bdt BirthDateTime. Supposed to be the Time when Project was born.
	 * @param dldt DeadlineDateTime. If exists it is the in the Future and will be checked from Time to time. If a "Check"
	 * finds out dldt of Project X is no longer in the Future it will Terminate this Project.
	 * @param goal. The Goal of Project can't be just whitespace or just nothing.
	 * 
	 * @return true if DateTimes make Sense and the Goal is not nothing or whitespace.
	 */
	public static boolean timeAndGoalOfActiveProjectIsValide(JSONObject pJSON)
	{

		String adtStr = pJSON.getString(ADTKey);
		LocalDateTime adt = LittleTimeTools.LDTfromTimeString(adtStr);
		String goal = pJSON.getString(goalKey);
		LocalDateTime dldt = null;
		
		if(pJSON.has(DLDTKey))
		{
			String dldtStr = pJSON.getString(DLDTKey);
			if((dldtStr!=prjctDeadlineNone)&&(dldtStr!=deadLineUnknownStr))
			{
				dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
			}
		}
		
		if((dldt!=null)&&(adt.isAfter(dldt)))//Maybe it is mod Project!!!
		{
			System.out.println(prjctTimeError[indexOfPrjctTimeErrorDeadBeforeBorn]);
			return false;
		}
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		if(jetzt.isBefore(adt))
		{
			System.out.println(prjctTimeError[indexOfPrjctTimeErrorBornAfterNow]);
			return false;
		}
		
		if(goal.trim().equals(""))
		{
			System.out.println(prjctTimeError[indexOfPrjctTimeErrorNeedsGoal]);
			return false;
		}
		
		return true;
	}

	/**
	 * A Simple Test if oldStep is terminated before newStep is born.
	 * 
	 * @param oldStep former Step of a Project
	 * @param newStep successor of oldStep.
	 *
	 * @return stepIsNotViolatingTimeframeOfFormerStepMsg if DateTimes are Okay.
	 * else stepIsViolatingTimeframeOfFormerStepMsg.
	 */
	public static String stepIsNotViolatingTimeframeOfFormerStep(JSONObject oldStep, JSONObject newStep)
	{
		
		String tdtOfOldStepStr = oldStep.getString(StepJSONKeyz.TDTKey);
		LocalDateTime tdtOfOldStep = LittleTimeTools.LDTfromTimeString(tdtOfOldStepStr);
		
		String adtOfNewStepStr = newStep.getString(StepJSONKeyz.ADTKey);
		LocalDateTime adtOfNewStep = LittleTimeTools.LDTfromTimeString(adtOfNewStepStr);
		
		if(adtOfNewStep.isBefore(tdtOfOldStep))return stepIsViolatingTimeframeOfFormerStepMsg;
		
		return stepIsNotViolatingTimeframeOfFormerStepMsg;
	}

	/**
	 * As the name suggest this Method checks if the Step given does not violate
	 * Project Time Frame.
	 * 
	 * @param step not yet added to Project.
	 * @param pJson Project-Data.
	 *
	 * @return boolean if and only if step does not violate the given Projects 
	 * Time Frame by adding it to it.
	 */
	public static String stepIsNotViolatingTimeframeOfProject(JSONObject step, JSONObject pJson)
	{
		
	
			
		String prjctDLStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
		if(prjctDLStr.equals(prjctDeadlineNone))return stepIsNotViolatingTimeframeOfProjectMsg;
	
		LocalDateTime prjctDeadLine = LittleTimeTools.LDTfromTimeString(prjctDLStr);
	
		String dldtOfStepStr = step.getString(StepJSONKeyz.DLDTKey);
		LocalDateTime dldtOfStep = LittleTimeTools.LDTfromTimeString(dldtOfStepStr);
		
		if(prjctDeadLine.isBefore(dldtOfStep))return stepIsViolatingTimeframeOfProjectMsg;
	
		return stepIsNotViolatingTimeframeOfProjectMsg;
	}

	/**
	 * Checks if bdt, nddt and dldt make sense.
	 * 
	 * @param step Data in question.
	 * 
	 * @return if everything is alright: stepIsOkToItSelfMsg.
	 * else some: stepIsNotOkayToItSelfMsgList.get(x).
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static String stepIsOkToItsSelf(JSONObject step) throws IOException, URISyntaxException
	{

		LocalDateTime adt = extractLDT(step, StepJSONKeyz.ADTKey);
		
		if(LocalDateTime.now().isBefore(adt))return stepIsNotOkayToItSelfMsgList.get(indexOfStpIsBornAfterNow);
	
		String deadLineStr = step.getString(StepJSONKeyz.DLDTKey);
		String desc= step.getString(StepJSONKeyz.descKey);
	
		if(desc.equals(""))return stepIsNotOkayToItSelfMsgList.get(indexOfStpHasNoDesc);
		
		if(!deadLineStr.equals(stepDeadlineNone)&&!deadLineStr.equals(deadLineUnknownStr))
		{
			LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(deadLineStr);
			
			if(dldt.isBefore(adt)) return stepIsNotOkayToItSelfMsgList.get(indexOfStpDLDTBeforeBDT);
		}
	
		return stepIsOkToItSelfMsg;
	}

	/**
	 * Checks if the DateTimes of the Project and the last two Steps make Sense.
	 * 
	 * @param pJson Project-Data. Before the new Step is noted in Project-JSON.
	 * @param newStep last Step. Not noted in Project-JSON yet. Here to test if
	 * that is Okay.
	 * 
	 * @return true if adding the new Step to Project doesn't create a Time Paradox.
	 * @throws URISyntaxException
	 * @throws IOException 
	 */
	public static boolean stepDataIsValide(JSONObject pJson, JSONObject newStep) throws IOException, URISyntaxException
	{
	
		int index = getIndexOfLastStepInPrjct(pJson);
		JSONObject oldStep = null;
		if(index>firstStepIndex)oldStep = getLastStepOfProject(pJson);
			
		String msg = stepIsOkToItsSelf(newStep);
		System.out.println("");
		if(!msg.equals(stepIsOkToItSelfMsg))
		{
			System.out.println(msg);
			return false;
		}
		else System.out.println(msg);
	
		msg = stepIsNotViolatingTimeframeOfProject(newStep, pJson);
		if(msg.equals(stepIsViolatingTimeframeOfProjectMsg))
		{
			System.out.println(msg);
			return false;
		}
		else System.out.println(msg);
	
		if(index>firstStepIndex)
		{
			msg = stepIsNotViolatingTimeframeOfFormerStep(oldStep, newStep);
			if(msg.equals(stepIsViolatingTimeframeOfFormerStepMsg))
			{
				System.out.println(msg);
				return false;
			}
			else System.out.println(msg);
		}
		
		System.out.println(stepTimeDataIsValide);
		return true;
	}

	public static LocalDateTime getLastDateTimeOfProject(JSONObject pJson)
	{
		JSONObject step = getLastStepOfProject(pJson);
		String tdtStr = step.getString(StepJSONKeyz.TDTKey);
		
		return LittleTimeTools.LDTfromTimeString(tdtStr);
	}

	public static JSONObject getLastStep(JSONObject pJSON)
	{
		JSONArray stepArr = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		int l = stepArr.length();
		
		return stepArr.getJSONObject(l-1);
	}

	public static final Predicate<JSONObject> stepIsTerminated = (step)->
	{
	
	   	String status = step.getString(StepJSONKeyz.statusKey);
	   	
	   	StatusMGMT states = StatusMGMT.getInstance();
	
	   	Set<String> terminalSet = states.getStatesOfASet(StatusMGMT.terminalSetName);
	    	
	   	return terminalSet.contains(status);
	};
	
	public LocalDateTime extractLDT(String name, String key) throws IOException, URISyntaxException
	{
		
        Set<JSONObject> jsonSet = GTDCLI.loadProjects(GTDCLI.getPathToDataFolder());

	    JSONObject pJSON = pickProjectByName(name, jsonSet);

		return  extractLDT(pJSON, key);
	}
	
	public static LocalDateTime extractLDT(JSONObject pJSON, String key) throws IOException, URISyntaxException
	{
		return  LittleTimeTools.LDTfromTimeString(pJSON.getString(key));
	}
	
	public static void iterateOverSteps(JSONObject pJSON, Consumer<JSONObject> jsonConsumer)
	{
		JSONArray stepArray = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		int s = stepArray.length();
		
		for(int n=0;n<s;n++)
		{
			JSONObject step = stepArray.getJSONObject(n);
			jsonConsumer.accept(step);
		}
	}
}