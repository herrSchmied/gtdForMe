package jborg.gtdForBash;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import allgemein.LittleTimeTools;


import consoleTools.*;

/**
 * @author John Bernard
 * @author johnbernard@gmx.net
 * 
 * This class is to get the Information needed to Setup
 * Projects or to note progress in those Projects. It
 * also has Methods to note termination of "Steps taken
 * in a Project. Or to Terminate a hole Progress. In 
 * both cases of termination it asks if it was a success
 * or a fail. Further more there it is possible to 
 * attache notes to a Project. And it's all done via
 * Console.
 */
public class GTDDataSpawnSession
{

	public static final int minMinutesInFutureDLDT = 5;
	public static final int maxYearsInFutureDLDT = 100;
	
	public static final int firstStepIndex = 0;
	
	public static final String time = "Time";

	public static final String prjctDeadlineQuestion = "Project got Deadline?";
	public static final String prjctDeadlineNone = "No got no Deadline!";
	
	public static final String prjctTimeOrGoalNotValide = "Time and/or Goal of Project not valide.";
	public static final String prjctDLDTHintPrefix = "Project Deadline. Min.: ";
	public static final String prjctDLDTHintMid = " Minutes in Future. Max.: ";
	public static final String prjctDLDTHintSuffix = " Years in Future.";
	
	public static final String stpDLDTHintPrefix = "\nDeadline must be between Projec NDDT: ";
	public static final String stpDLDTHintMid = " and Project Deadline: ";
	
	public static final String extrStpDLDTHintPrefix = "Deadline must be between old-Step TDT: ";
	public static final String extrStpDLDTHintMid = " and Project Deadline: ";
	
	public static final String stpTDTHintPrefix = "TDT must be between ";
	public static final String stpTDTHintMid = " and ";
	
	public static final String prjctNotValide[] = new String[] 
			{"Project needs Deadline.", "Project dead before Living.", "Project Born after Now.", 
					"Project needs a Goal."};
	public static final int indexOfPrjctNeedsDLDT = 0;
	public static final int indexOfPrjctDeadBeforeBorn = 1;
	public static final int indexOfPrjctBornAfterNow = 2;
	public static final int indexOfPrjctNeedsGoal = 3;
	
	public static final String stepTerminationNotePhrase = "Termination Note:";
	public static final String wantToMakeTerminalNotePhrase = "want to make a Terminal note?";
	
	public static final String notValide = "Name or Goal not valide.";

	public static final String prjctNameQ = "Project Name: ";
	public static final String prjctNameError = "Name already in use.";	
	public static final String goalQ = "Goal of Project: ";
	public static final String changeBDTQ = "Want to change Birthdatetime of Project? ";
	public static final String bdtQ = "BDT of Project:";
	
	public static final int maxDLDTYear = 2020;
	public static final int minBDTYear = 1800;
	public static final int minMonth = 1;
	public static final int minDay = 1;
	public static final int minHour = 0;
	public static final int minMinute = 0;
	public static final LocalDateTime ancient = LocalDateTime.of(minBDTYear, minMonth, minDay, minHour, minMinute);
	public static final LocalDateTime farInFuture = LocalDateTime.of(maxDLDTYear, minMonth, minDay, minHour, minMinute);

	public static final String dldtQ = "Deadline for Project?";
	public static final int dldtRange = 100;
	
	public final static String prjctTDTNoteQstn = "Please type ur TDT-Note?";
	public final static String prjctWhenTDTQstn = "When took the Termination of this Project place?";
	public final static String wantToChangeTDTOfPrjctQstn = "Wan't to change TDT of Project?";
	public final static String prjctSuccessQstn = "Was Project a Success?";
	
	public static final String btnTxtChangeBDT = "Change BDT";
	public static final String changingBDTInputPhrase = "Determining BDT";
	
	public final static String stepDeadlineQuestion = "Step got Deadline?";
	public final static String stepDeadlineNone = "No Step Deadline!";
	
	public final static String stepWhenTDTQstn = "When took the Termination of this Step place?";
	public static final String stepDescPhrase = "Describe Step:";
	public static final String descStepInputTitle = "Description";
	public static final String stepSuccesQstn = "Was Step a Success?";
	public static final String stpDeadlinePleasePhrase = "Step DeadLine Please.";
	
	
	public static final String illAExceMsg = "Don't know that Beholder.";
	
	public static final String differentBDTQstn = "Do You want change BDT of Step?";
	
	public static final String noteAddPhrase = "Write Note:";
	public static final boolean notTxtAQstn = true;
	
	private static StatusMGMT statusMGMT = StatusMGMT.getInstance();
	private static Set<String> stepStartStatuses = statusMGMT.getStatesOfASet(StatusMGMT.atStartSetName);
		
	public static final String stepStatusPhrase = "Choose Status: ";
	
	public static final String wantToChangeBDTOfStepQstn = "Want to change bdt of Step?";
	public static final String stepBDTMsgPrefix = "BDT of Project(";
	public static final String stepBDTMsgMid = ") - Now!(";
	public static final String stepBDTMsgSuffix = ") Step BDT must be in that Range.";
	
	public static final String stepDateTimeQstn = "DateTime of Step BDT: ";
	public static final String stepChooseStatusQstn = "Choose Step Status";
	
	public static final String wantToChangeTDTOfStepQstn = "Wan't to change TDT of Step?";
	public static final String wantToMakeTDTNoteQstn = "Wan't to make a TDT Note for the Projekt?";
	public static final String waitingForPhrase = "What u waiting for?";
	
	public static final String infoAlertTxtPhrase = "Remember Step Termination Note is Project Termination Note at this last Step.";
	public static final String deadLinePrjctQstn = "Deadline for Project";
	public static final String unknownDLLblTxt = "Unkown Deadline";
	public static final String makeDLBtnTxt = "Make Deadline";

	public static final String invalidePrjctName = "There is already a Project with that Name.";
	public static final String invalideStep = "Invalide Step.";
	
	public static final String deadLineUnknownStr = "UNKNOWN";
	public static final String prjctTimeOrGoalInvalidMsg = "Time and/or Goal ain't valide for this Project.";
	public static final String stepSpawnExceptionFormerStepIsntTerminated = "Sorry former Step isn't Terminated.";
	public static final String stepSpawnExceptionStepAintValide = "Step ain't valide";

	public static final String stepIsNotViolatingTimeframeOfProjectMsg = "Step is not violating Timeframe of Project.";
	public static final String stepIsViolatingTimeframeOfProjectMsg = "Step is violating timeframe of former Step";

	public static final String stepIsViolatingTimeframeOfFormerStepMsg = "Step is violating timeframe of former Step";
	public static final String stepIsNotViolatingTimeframeOfFormerStepMsg = "Step is not violating timeframe of former Step";
	
	public static final String stepTExcIllglArmntPrefix = "IllegalArgument!!!";
	public static final String stepTExcJSONErrorMsg = "JSON macht Probleme";
	public static final String stepTimeDataIsValide = "Step Time Data is valide.";
	
	public static final String prjctTExcAllreadyDeadMsg = "Project Already Terminated.";
	public static final String prjctTDTAfterDLDTMsg = "Termination can't be after Deadline.";
	public static final String prjctTDTBeforeNDDT = "Termination can't be before Note-Down-Date-Time";
	public static final String prjctTDTAfterNow = "TDT can't be after now.";
	
	public static final String stepIsOkToItSelfMsg = "Step is Ok to it Self.";
	
	List<String> stepIsNotOkayToItSelfMsgList = 
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
	
	public static final String stpTerminationExceptionMsg = "Sorry Step is Already Terminated.";
	
	public static final String modifierSetName = "Project_modifier";
	public static final String showDataSetName = "Data_view";
	public static final String otherSetName = "other";

	public static final Set<String> commands = new HashSet<>();


	
	final InputStreamSession iss;
	
	/**
	 * The Constructor
	 * 
	 * @param iss InputStreamSession which is a wrapped up InputStream
	 * from System.in.
	 */
	public GTDDataSpawnSession(InputStreamSession iss)
	{
		this.iss = iss;
	}
	
	/**
	 * This Method asks for Information for a Maybe-one-day(MOD) Project. It forces valid Data.
	 * It receives Data via InputStreamSession It returns than a JSONObject with that Data. 
	 * MOD-Projects don't have any Steps or Deadline. It is possible to make an MOD-Project to
	 * an active Project by "Waking" it up There is Method that does that. The parameter: 
	 * knownProjectNames is the list of forbidden Project Names. Otherwise it would cause 
	 * problems when saving it. 
	 * 
	 * @param knownProjectsNames forbidden Names.
	 * @param statusMGMT allowed Statuses for Projects.
	 * @return JSONObject MOD-Project-Data.
	 * @throws IOException only when something with the InputStreamSession goes wrong.
	 */
	public JSONObject spawnMODProject(Set<String> knownProjectsNames, StatusMGMT statusMGMT) throws IOException 
	{

		System.out.println("");
		String name = iss.forcedString(prjctNameQ);
		name = name.trim();
		if(knownProjectsNames.contains(name))
		{
			System.out.println(invalidePrjctName);
			return spawnMODProject(knownProjectsNames, statusMGMT);
		}

		JSONObject pJson = new JSONObject();
 
		String status = StatusMGMT.mod;
		LocalDateTime bdt = null;
		LocalDateTime nddt = LocalDateTime.now();

			
		System.out.println("");
		String goal = iss.getString(goalQ);
			
		System.out.println("");
		boolean changeBDT = iss.forcedYesOrNo(changeBDTQ);

		LocalDateTime jetzt = LocalDateTime.now();
		if(changeBDT)
		{
			System.out.println("");
			bdt = iss.forcedDateTimeInOneLine(bdtQ, ancient, jetzt);//must be born before now.
		}
		else bdt = nddt;
				
		pJson.put(ProjectJSONKeyz.nameKey, name);
		pJson.put(ProjectJSONKeyz.goalKey, goal);
		pJson.put(ProjectJSONKeyz.statusKey, status);
			
		pJson.put(ProjectJSONKeyz.DLDTKey, deadLineUnknownStr);
				
		String bdtStr = LittleTimeTools.timeString(bdt);
		pJson.put(ProjectJSONKeyz.BDTKey, bdtStr);
			
		String nddtStr = LittleTimeTools.timeString(nddt);
		pJson.put(ProjectJSONKeyz.NDDTKey, nddtStr);
		
		return pJson;

	}
	
	/**
	 * Creates an active Project from Data it receives from The User via InputStreamSession.
	 * It forces valid Data.
	 * 
	 * @param knownProjectsNames forbidden Names for the Project.
	 * @param statusMGMT allowed Statuses for the Project.
	 * @return JSONObject Project-Data.
	 * @throws IOException only if something with the InputStreamSession goes wrong.
	 */
	public JSONObject spawnNewProject(Set<String> knownProjectsNames, StatusMGMT statusMGMT) throws IOException
	{
		
		System.out.println("");
		String name = iss.forcedString(prjctNameQ);
		name = name.trim();
		if(knownProjectsNames.contains(name))
		{
			System.out.println(invalidePrjctName);
			return spawnNewProject(knownProjectsNames, statusMGMT);//Force valide name.
		}

		JSONObject pJson = new JSONObject();
 
		String status = "";
		LocalDateTime bdt = null;
		LocalDateTime nddt = LocalDateTime.now();
		LocalDateTime dldt = null;
			
		System.out.println("");
		String goal = iss.forcedString(goalQ);

		System.out.println("");
		boolean changeBDT = iss.forcedYesOrNo(changeBDTQ);

		if(changeBDT)
		{
			
			System.out.println("");
			bdt = iss.forcedDateTimeInOneLine(bdtQ, ancient, LocalDateTime.now());//must be born before now.
		}
		else bdt = nddt;
				
		pJson.put(ProjectJSONKeyz.nameKey, name);
		pJson.put(ProjectJSONKeyz.goalKey, goal);
		pJson.put(ProjectJSONKeyz.statusKey, status);

		pJson.put(ProjectJSONKeyz.DLDTKey, deadLineUnknownStr);

		String bdtStr = LittleTimeTools.timeString(bdt);
		pJson.put(ProjectJSONKeyz.BDTKey, bdtStr);
			
		String nddtStr = LittleTimeTools.timeString(nddt);
		pJson.put(ProjectJSONKeyz.NDDTKey, nddtStr);

		boolean gotDLDT = iss.forcedYesOrNo(prjctDeadlineQuestion);
		
		if(gotDLDT)
		{
			System.out.println("");
			System.out.println(prjctDLDTHintPrefix + minMinutesInFutureDLDT + prjctDLDTHintMid + maxYearsInFutureDLDT + prjctDLDTHintSuffix);
			
			dldt = iss.forcedDateTimeInOneLine(dldtQ, LocalDateTime.now().plusMinutes(minMinutesInFutureDLDT), LocalDateTime.now().plusYears(maxYearsInFutureDLDT));
			String deadLineStr = LittleTimeTools.timeString(dldt);
			pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);//Overwrites current "UNKNOWN" value.
		}
		else pJson.put(ProjectJSONKeyz.DLDTKey, prjctDeadlineNone);

		if(timeAndGoalOfActiveProjectIsValide(nddt, bdt, dldt, goal))
		{
			
			spawnStep(pJson);//Here status will be overwritten. Here step status will be equal project status.
			
			return pJson;
		}
		else
		{
			TimeGoalOfProjectException tgope = new TimeGoalOfProjectException(prjctTimeOrGoalInvalidMsg);
			System.out.println(tgope.getMessage());

			return spawnNewProject(knownProjectsNames, statusMGMT);//Force valide Time.;
		}
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
	private boolean timeAndGoalOfActiveProjectIsValide(LocalDateTime nddt, LocalDateTime bdt, 
			LocalDateTime dldt, String goal)
	{
		
		if(dldt!=null&&bdt.isAfter(dldt))//Maybe it is mod Project!!!
		{
			System.out.println(prjctNotValide[indexOfPrjctDeadBeforeBorn]);
			return false;
		}
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		if(jetzt.isBefore(bdt))
		{
			System.out.println(prjctNotValide[indexOfPrjctBornAfterNow]);
			return false;
		}
		
		if(goal.trim().equals(""))
		{
			System.out.println(prjctNotValide[indexOfPrjctNeedsGoal]);
			return false;
		}
		
		return true;
	}

	public void spawnStep(JSONObject pJson) throws IOException
	{


		JSONObject newStep = new JSONObject();
		int index = getIndexOfLastStepInPrjct(pJson);
		JSONObject oldStep;
		
		
		JSONArray steps;
		if(index== firstStepIndex-1)
		{
			steps = new JSONArray();
			oldStep = null;
		}
		else
		{
			steps = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);
			oldStep = getLastStepOfProject(pJson);
			if(!stepIsAlreadyTerminated(oldStep))
			{
				System.out.println(stepSpawnExceptionFormerStepIsntTerminated);
				spawnStep(pJson);
				return;
			}
		}
		
		LocalDateTime nddtOfStep = LocalDateTime.now();
		LocalDateTime bdtOfStep;
		
		boolean differentBDT;

		String stepStatus = "";

			
		String bdtOfPrj = pJson.getString(ProjectJSONKeyz.BDTKey);
		LocalDateTime ldtBDTOfPrj = LittleTimeTools.LDTfromTimeString(bdtOfPrj);
		String jetzt = LittleTimeTools.timeString(LocalDateTime.now());
			
		System.out.println("");
		differentBDT = iss.forcedYesOrNo(wantToChangeBDTOfStepQstn);
	
		System.out.println("");
		
		if(differentBDT)
		{
					
			System.out.println(stepBDTMsgPrefix + bdtOfPrj + stepBDTMsgMid + jetzt + stepBDTMsgSuffix);
			bdtOfStep = iss.forcedDateTimeInOneLine(stepDateTimeQstn, ldtBDTOfPrj, LocalDateTime.now());
		}
		else bdtOfStep = nddtOfStep;
			
		List<String> sss = new ArrayList<>();
		sss.addAll(stepStartStatuses);
		System.out.println("");
		stepStatus = iss.forcedOutOfList(stepChooseStatusQstn, sss);
					
		String phrase;
		if(stepStatus.equals(StatusMGMT.waiting))phrase = waitingForPhrase;
		else phrase = stepDescPhrase;

		String descriptionOfStep = iss.getString(phrase);
		
		String prjctNDDT = pJson.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime ldtNDDTOfPrjct = LittleTimeTools.LDTfromTimeString(prjctNDDT);
		
		String prjctDeadLine = pJson.getString(ProjectJSONKeyz.DLDTKey);
		String deadLineStr = "";
		
		LocalDateTime prjctDLDTYear;
		if(!prjctDeadLine.equals(prjctDeadlineNone))prjctDLDTYear = LittleTimeTools.LDTfromTimeString(prjctDeadLine);
		else prjctDLDTYear = farInFuture;
		
		boolean gotDeadline = iss.forcedYesOrNo(stepDeadlineQuestion);
		
		if(gotDeadline)
		{
			if(index==firstStepIndex-1)
			{
				System.out.println(stpDLDTHintPrefix + prjctNDDT + stpDLDTHintMid  + prjctDeadLine);
				LocalDateTime deadLineLDT = iss.forcedDateTimeInOneLine(stpDeadlinePleasePhrase, ldtNDDTOfPrjct, prjctDLDTYear);
				deadLineStr = LittleTimeTools.timeString(deadLineLDT);
			}
			else
			{
				System.out.println("");
				String oldStepTDT = oldStep.getString(StepJSONKeyz.TDTKey);
				System.out.println(extrStpDLDTHintPrefix + oldStepTDT + extrStpDLDTHintMid + prjctDeadLine);
				LocalDateTime ldtOldStepTDT = LittleTimeTools.LDTfromTimeString(oldStepTDT);
				LocalDateTime deadLineLDT = iss.forcedDateTimeInOneLine(stpDeadlinePleasePhrase, ldtOldStepTDT, prjctDLDTYear);
				deadLineStr = LittleTimeTools.timeString(deadLineLDT);
			}
		}
		else 
		{	
			if(!prjctDeadLine.equals(prjctDeadlineNone))deadLineStr = prjctDeadLine;
			else deadLineStr = stepDeadlineNone;
		}
		
		newStep.put(StepJSONKeyz.DLDTKey, deadLineStr);
		newStep.put(StepJSONKeyz.statusKey, stepStatus);
		newStep.put(StepJSONKeyz.descKey, descriptionOfStep);
		newStep.put(StepJSONKeyz.NDDTKey, LittleTimeTools.timeString(nddtOfStep));
		newStep.put(StepJSONKeyz.BDTKey, LittleTimeTools.timeString(bdtOfStep));
		
		if(stepDataIsValide(pJson, oldStep, newStep, index))
		{
			pJson.put(ProjectJSONKeyz.statusKey, stepStatus);//this overwrites old status!
						
			steps.put(index + 1, newStep);
			
			pJson.put(ProjectJSONKeyz.stepArrayKey, steps);
			
		}
		else
		{
			System.out.println(stepSpawnExceptionStepAintValide);
			spawnStep(pJson);
		}
	}

	public boolean stepDataIsValide(JSONObject pJson, JSONObject oldStep, JSONObject newStep, int index)
	{
	
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
	
	public String stepIsNotViolatingTimeframeOfProject(JSONObject step, JSONObject pJson)
	{
		

			
		String prjctDLStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
		if(prjctDLStr.equals(prjctDeadlineNone))return stepIsNotViolatingTimeframeOfProjectMsg;

		LocalDateTime prjctDeadLine = LittleTimeTools.LDTfromTimeString(prjctDLStr);

		String dldtOfStepStr = step.getString(StepJSONKeyz.DLDTKey);
		LocalDateTime dldtOfStep = LittleTimeTools.LDTfromTimeString(dldtOfStepStr);
		
		if(prjctDeadLine.isBefore(dldtOfStep))return stepIsViolatingTimeframeOfProjectMsg;

		return stepIsNotViolatingTimeframeOfProjectMsg;
	}

	public String stepIsNotViolatingTimeframeOfFormerStep(JSONObject oldStep, JSONObject newStep)
	{
		
		String tdtOfOldStepStr = oldStep.getString(StepJSONKeyz.TDTKey);
		LocalDateTime tdtOfOldStep = LittleTimeTools.LDTfromTimeString(tdtOfOldStepStr);
		
		String bdtOfNewStepStr = newStep.getString(StepJSONKeyz.BDTKey);
		LocalDateTime bdtOfNewStep = LittleTimeTools.LDTfromTimeString(bdtOfNewStepStr);
		
		if(bdtOfNewStep.isBefore(tdtOfOldStep))return stepIsViolatingTimeframeOfFormerStepMsg;
		
		return stepIsNotViolatingTimeframeOfFormerStepMsg;
	}
	
	public String stepIsOkToItsSelf(JSONObject step)
	{

		//stepIsNotOkayToitSelfMsgSet
		String bdtOfStepStr = step.getString(StepJSONKeyz.BDTKey);
		LocalDateTime bdtOfStep = LittleTimeTools.LDTfromTimeString(bdtOfStepStr);
		
		if(LocalDateTime.now().isBefore(bdtOfStep))return stepIsNotOkayToItSelfMsgList.get(indexOfStpIsBornAfterNow);

		String deadLineStr = step.getString(StepJSONKeyz.DLDTKey);
		String desc= step.getString(StepJSONKeyz.descKey);
		String nddtStr = step.getString(StepJSONKeyz.NDDTKey);
		String bdtStr = step.getString(StepJSONKeyz.BDTKey);

		if(desc.equals(""))return stepIsNotOkayToItSelfMsgList.get(indexOfStpHasNoDesc);

		LocalDateTime born = LittleTimeTools.LDTfromTimeString(bdtStr);
		
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		
		if(nddt.isBefore(born)) return stepIsNotOkayToItSelfMsgList.get(indexOfStpNotedBeforeBorn);
		
		if(!deadLineStr.equals(stepDeadlineNone))
		{
			LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(deadLineStr);
			
			if(dldt.isBefore(born)) return stepIsNotOkayToItSelfMsgList.get(indexOfStpDLDTBeforeBDT);
			if(dldt.isBefore(nddt)) return stepIsNotOkayToItSelfMsgList.get(indexOfStpDLDTBeforeNDDT);
		}

		return stepIsOkToItSelfMsg;
	}
	
	public void addNote(JSONObject pJson)
	{
		JSONArray ja;
		boolean hasNoteArray = pJson.has(ProjectJSONKeyz.noteArrayKey);
		
		if(hasNoteArray)ja = pJson.getJSONArray(ProjectJSONKeyz.noteArrayKey);
		else ja = new JSONArray();
		
		
		String noteTxt = iss.getString(noteAddPhrase);
		if(!noteTxt.trim().equals(""))
		{
			ja.put(noteTxt);
			pJson.put(ProjectJSONKeyz.noteArrayKey, ja);
		}
	}
	
	public void wakeMODProject(JSONObject pJson) throws IOException, InputArgumentException
	{
		
		LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(pJson.getString(ProjectJSONKeyz.BDTKey));
		LocalDateTime nddt = LocalDateTime.now();
		LocalDateTime dldt = null;
				
		String nddtStr = LittleTimeTools.timeString(nddt);

		System.out.println("");
		boolean gotDLDT = iss.getYesOrNo(prjctDeadlineQuestion);
		
		String deadLineStr = "";
		if(gotDLDT)
		{
			dldt = iss.getDateTimeInOneLine(dldtQ, LocalDateTime.now().plusMinutes(minMinutesInFutureDLDT), LocalDateTime.now().plusYears(maxYearsInFutureDLDT));
			deadLineStr = LittleTimeTools.timeString(dldt);
		}
		else deadLineStr = prjctDeadlineNone;
		
		String goal = pJson.getString(ProjectJSONKeyz.goalKey);
		if(timeAndGoalOfActiveProjectIsValide(nddt, bdt, dldt, goal))
		{
			
			pJson.put(ProjectJSONKeyz.NDDTKey, nddtStr);
			pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);//Overwrites current "UNKNOWN" value.

			spawnStep(pJson);//Here status will be overwritten.;
		}
		else
		{
			System.out.println(prjctTimeOrGoalNotValide);
			wakeMODProject(pJson);
		}
	}
	
	public boolean stepIsAlreadyTerminated(JSONObject sJson)
	{
		
		String status = sJson.getString(StepJSONKeyz.statusKey);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		String terminalSetName = StatusMGMT.terminalSetName;
		
		Set<String> terminalSet = statusMGMT.getStatesOfASet(terminalSetName);
		if(terminalSet.contains(status))return true;
		
		return false;
	}

	public void terminateStep(JSONObject pJson) throws IOException, InputArgumentException, StepTerminationException
	{

		JSONObject sJson = getLastStepOfProject(pJson);
		
		if(stepIsAlreadyTerminated(sJson))throw new StepTerminationException(stpTerminationExceptionMsg);

		LocalDateTime jetzt = LocalDateTime.now();
		String jetztStr = LittleTimeTools.timeString(jetzt);
		
		String nddtOfStepStr = sJson.getString(StepJSONKeyz.NDDTKey);
		LocalDateTime nddtOfStep = LittleTimeTools.LDTfromTimeString(nddtOfStepStr);
		
		boolean wasItASuccess = iss.getYesOrNo(stepSuccesQstn);

		String stepStatus;
		if(wasItASuccess)stepStatus = StatusMGMT.success;
		else stepStatus = StatusMGMT.failed;
		
		try
		{
			String terminalNote = "";
			boolean thereIsATerminalNote = iss.getYesOrNo(wantToMakeTerminalNotePhrase);
			if(thereIsATerminalNote)terminalNote = iss.getString(stepTerminationNotePhrase);
			
			LocalDateTime tdt = LocalDateTime.now();
			boolean wantToChangeTDTOfStep = iss.getYesOrNo(wantToChangeTDTOfStepQstn);
			if(wantToChangeTDTOfStep)
			{
				System.out.println(stpTDTHintPrefix + nddtOfStepStr + stpTDTHintMid + jetztStr);
				tdt = iss.getDateTimeInOneLine(stepWhenTDTQstn, nddtOfStep, jetzt);
			}
			
			sJson.put(StepJSONKeyz.statusKey, stepStatus);
			pJson.put(ProjectJSONKeyz.statusKey, StatusMGMT.needsNewStep);
			String when = LittleTimeTools.timeString(tdt);
			sJson.put(StepJSONKeyz.TDTKey, when);
			if(!terminalNote.trim().equals(""))sJson.put(StepJSONKeyz.TDTNoteKey, terminalNote);
		}
		catch(IllegalArgumentException exc) { throw new StepTerminationException(stepTExcIllglArmntPrefix + exc); }
		catch(JSONException jsonExc) { throw new StepTerminationException(stepTExcJSONErrorMsg); }
	}

	public boolean projectIsAlreadyTerminated(JSONObject pJson)
	{
		
		String status = pJson.getString(ProjectJSONKeyz.statusKey);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		String terminalSetName = StatusMGMT.terminalSetName;		
		Set<String> terminalSet = statusMGMT.getStatesOfASet(terminalSetName);
		
		if(terminalSet.contains(status))return true;
		
		return false;
	}
	
	public void terminateProject(JSONObject pJson) throws InputArgumentException, JSONException, IOException, ProjectTerminationException
	{
		
		if(projectIsAlreadyTerminated(pJson)) throw new ProjectTerminationException(prjctTExcAllreadyDeadMsg);

		System.out.println(infoAlertTxtPhrase);
		
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		String prjctStatus = "";
		boolean success = iss.getYesOrNo(prjctSuccessQstn);
			
		if(success)prjctStatus = StatusMGMT.success;
		else prjctStatus = StatusMGMT.failed;

			
		String terminalNote = "";
		boolean wantToMakeTDTNoteQuestion = iss.getYesOrNo(wantToMakeTDTNoteQstn);
		if(wantToMakeTDTNoteQuestion) terminalNote = iss.getString(prjctTDTNoteQstn);
				
		boolean wantChangeTDTQuestion = iss.getYesOrNo(wantToChangeTDTOfPrjctQstn);
		LocalDateTime tdt = jetzt;
		if(wantChangeTDTQuestion)tdt = iss.getDateTimeInOneLine(prjctWhenTDTQstn,ancient, jetzt);

		String dldtStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
		LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
				
		if(tdt.isAfter(dldt))throw new ProjectTerminationException(prjctTDTAfterDLDTMsg);
				
		String nddtStr = pJson.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
				
		if(tdt.isBefore(nddt))throw new ProjectTerminationException(prjctTDTBeforeNDDT);
			
		if(tdt.isAfter(jetzt))throw new ProjectTerminationException(prjctTDTAfterNow);
				
		pJson.put(ProjectJSONKeyz.statusKey, prjctStatus);
				
		String tdtStr = LittleTimeTools.timeString(tdt);
		pJson.put(ProjectJSONKeyz.TDTKey, tdtStr);

		if(!terminalNote.trim().equals(""))pJson.put(ProjectJSONKeyz.TDTNoteKey, terminalNote);
	}
	
	public int getIndexOfLastStepInPrjct(JSONObject pJson)
	{
		JSONArray stepArray;
		
		if(pJson.has(ProjectJSONKeyz.stepArrayKey))
		{
			stepArray = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);
			return stepArray.length()-1;
		}
		
		return firstStepIndex-1;
	}
	
	public JSONObject getLastStepOfProject(JSONObject pJson)
	{
		JSONArray stepArray = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		int indexOfLastStep = getIndexOfLastStepInPrjct(pJson);

		return stepArray.getJSONObject(indexOfLastStep);
	}
}