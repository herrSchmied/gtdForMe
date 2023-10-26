package jborg.gtdForBash;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import allgemein.Beholder;

import allgemein.LittleTimeTools;

import allgemein.Subjekt;

import consoleTools.*;


public class GTDDataSpawnSession implements Subjekt<String>
{
	
	
	//TODO:->Eliminate rest of Literals in Code.		<-
	public static final int minMinutesInFutureDLDT = 5;
	public static final int maxYearsInFutureDLDT = 100;
	
	public static final int firstStepIndex = 0;
	
	public static final String time = "Time";

	public static final String prjctNotValide[] = new String[] 
			{"Project needs Deadline.", "Project dead before Living.", "Project Noted before Born.", 
					"Project needs a Goal."};
		
	public static final String stepTerminationNotePhrase = "Termination Note:";
	
	
	public static final String notValide = "Name or Goal not valide.";

	public static final String prjctNameQ = "Project Name: ";
	public static final String prjctNameError = "Name already in use.";	
	public static final String isModProjectQ = "Maybe one Day-Project?(yes) or are we actually try "
			+ "to do it soon enough?(no): ";
	public static final String goalQ = "Goal of Project: ";
	public static final String changeBDTQ = "Want to change Birthdatetime of Project? ";
	public static final String bdtQ = "BDT of Project:";
	public static final int minBDTYear = 1800;
	public static final int minMonth = 1;
	public static final int minDay = 1;
	public static final int minHour = 0;
	public static final int minMinute = 0;
	public static final LocalDateTime ancient = LocalDateTime.of(minBDTYear, minMonth, minDay, minHour, minMinute);

	public static final String dldtQ = "Deadline for Project?";
	public static final int dldtRange = 100;
	
	public final static String prjctTDTNoteQstn = "Please type ur TDT-Note?";
	public final static String prjctWhenTDTQstn = "When took the Termination of this Project place?";
	public final static String wantToChangeTDTOfPrjctQstn = "Wan't to change TDT of Project?";
	public final static String prjctSuccessQstn = "Was Project a Success?";
	
	public static final String btnTxtChangeBDT = "Change BDT";
	public static final String changingBDTInputPhrase = "Determining BDT";
	
	public final static String stepWhenTDTQstn = "When took the Termination of this Step place?";
	public static final String stepDescPhrase = "Describe Step:";
	public static final String descStepInputTitle = "Description";
	public static final String stepSuccesQstn = "Was Step a Success?";
	
	
	public static final String illAExceMsg = "Don't know that Beholder.";
	
	public static final String differentBDTQstn = "Do You want change BDT of Step?";

	private ArrayList<Beholder<String>> observer = new ArrayList<Beholder<String>>();
	
	public static final String noteAddPhrase = "Write Note:";
	public static final boolean notTxtAQstn = true;
	
	private static Set<String> stepStartStatuses = new HashSet<>(Arrays.asList(StatusMGMT.atbd, StatusMGMT.waiting));
		
	public static final String stepStatusPhrase = "Choose Status: ";
	public static final String wantToChangeTDTOfStepQstn = "Wan't to change TDT of Step?";
	public static final String wantToMakeTDTNoteQstn = "Wan't to make a TDT Note for the Projekt?";
	public static final String waitingForPhrase = "What u waiting for?";
	
	public static final String infoAlertTxtPhrase = "Remember Step Termination Note is Project Termination Note at this last Step.";
	public static final String deadLinePrjctQstn = "Deadline for Project";
	public static final String unknownDLLblTxt = "Unkown Deadline";
	public static final String makeDLBtnTxt = "Make Deadline";

	public static final String invalidePrjctName = "There is already a Project with that Name.";

	public static final String deadLineUnknownStr = "UNKNOWN";
	
	final InputStreamSession iss;
	
	public GTDDataSpawnSession(InputStreamSession iss)
	{
		this.iss = iss;
	}
	
	public JSONObject spawnNewProject(Map<String, JSONObject> knownProjects, StatusMGMT statusMGMT)
	{
		
		System.out.println("");
		String name = iss.getString(prjctNameQ);
		name = name.trim();
		if(knownProjects.keySet().contains(name))
		{
			System.out.println(invalidePrjctName);
			return null;
		}

		JSONObject pJson = new JSONObject();
 
		String status = "";
		LocalDateTime bdt = null;
		LocalDateTime nddt = LocalDateTime.now();
		LocalDateTime dldt = null;


		try 
		{
			System.out.println("");
			boolean isModProject = iss.getYesOrNo(isModProjectQ);		
			if(isModProject)status = StatusMGMT.mod;
			
			System.out.println("");
			String goal = iss.getString(goalQ);
			
			System.out.println("");
			boolean changeBDT = iss.getYesOrNo(changeBDTQ);

			if(changeBDT)
			{
				System.out.println("");
				bdt = iss.getDateTime(bdtQ, ancient, LocalDateTime.now());//must be born before now.
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

			if(!isModProject)
			{
				
				
				System.out.println("");
				System.out.println("Project Deadline. Min.: " + minMinutesInFutureDLDT + " Minutes in Future. Max.: " + maxYearsInFutureDLDT + " Years in Future.");
				//Deadline should be at least five Minutes in the Future. But not more than 20 Years.
				dldt = iss.getDateTime(dldtQ, LocalDateTime.now().plusMinutes(minMinutesInFutureDLDT), LocalDateTime.now().plusYears(maxYearsInFutureDLDT));
				String deadLineStr = LittleTimeTools.timeString(dldt);
				pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);//Overwrites current "UNKNOWN" value.
				
				JSONObject tmp = spawnFirstStep(pJson);//Here status will be overwritten.
				if(tmp==null)return null;
				else pJson = tmp;
				if(!timeAndGoalOfActiveProjectIsValide(nddt, bdt, dldt, goal))return null;
			}

			} 
			catch (IllegalArgumentException | InputMismatchException e) 
			{
				e.printStackTrace();
				return null;
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				return null;
			}
				
		return pJson;
	}
	
	private boolean timeAndGoalOfActiveProjectIsValide(LocalDateTime nddt, LocalDateTime bdt, LocalDateTime dldt, String goal)
	{
		
		if(dldt==null)
		{
			System.out.println(prjctNotValide[0]);
			return false;
		}
		
		if(dldt!=null&&bdt.isAfter(dldt))//Maybe it is mod Project!!!
		{
			System.out.println(prjctNotValide[1]);
			return false;
		}
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		if(jetzt.isBefore(bdt))
		{
			System.out.println(prjctNotValide[2]);
			return false;
		}
		
		if(goal.trim().equals(""))
		{
			System.out.println(prjctNotValide[3]);
			return false;
		}
		
		return true;
	};

	public JSONObject spawnFirstStep(JSONObject pJson) throws IOException
	{	
		
		System.out.println("Spawning first Step for Project: " + pJson.getString(ProjectJSONKeyz.nameKey));
		return spawnStep(pJson, firstStepIndex);
	}

	public void appendStep(JSONObject pJson) throws IOException
	{
		
		System.out.println("Appending Step to Project: " + pJson.getString(ProjectJSONKeyz.nameKey));
		
		JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
		int length = steps.length();
		
		pJson = spawnStep(pJson, length);
	}
	
	private JSONObject spawnStep(JSONObject pJson, int index) throws IOException
	{

		if(index<firstStepIndex) throw new IllegalArgumentException("Index too Small.");

		JSONObject newStep = new JSONObject();
		JSONObject oldStep;
		
		JSONArray steps;
		if(index==firstStepIndex)
		{
			steps = new JSONArray();
			oldStep = null;
		}
		else
		{
			steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
			oldStep = steps.getJSONObject(index-1);
			String oldStepStatus = oldStep.getString(StepJSONKeyz.statusKey);
			String tSetName = StatusMGMT.terminalSetName;
			StatusMGMT statusMGMT = StatusMGMT.getInstance();
			Set<String> tSet = statusMGMT.getStatesOfASet(tSetName);
			if(!tSet.contains(oldStepStatus))terminateStep(pJson);    //Just in case
		}
		
		LocalDateTime nddtOfStep = LocalDateTime.now();
		LocalDateTime bdtOfStep;
		
		boolean differentBDT;

		String stepStatus = "";

		try 
		{
			
			String bdtOfPrj = pJson.getString(ProjectJSONKeyz.BDTKey);
			LocalDateTime ldtBDTOfPrj = LittleTimeTools.LDTfromTimeString(bdtOfPrj);
			String jetzt = LittleTimeTools.timeString(LocalDateTime.now());
			
			System.out.println("");
			differentBDT = iss.getYesOrNo("Want to change bdt of Step?");
			System.out.println("");
			System.out.println("BDT of Project(" + bdtOfPrj + ") - Now!(" + jetzt + ") Step BDT must be in that Range.");
			if(differentBDT)bdtOfStep = iss.getDateTime("DateTime of Step BDT: ", ldtBDTOfPrj, LocalDateTime.now());
			else bdtOfStep = nddtOfStep;
			
			while(stepStatus.trim().equals("")) 
			{
				List<String> sss = new ArrayList<>();
				sss.addAll(stepStartStatuses);
				System.out.println("");
				stepStatus = iss.getAnswerOutOfList("Choose Step Status", sss);
			}
						
			String phrase;
			if(stepStatus.equals(StatusMGMT.waiting))phrase = waitingForPhrase;
			else phrase = stepDescPhrase;

			String descriptionOfStep = iss.getString(phrase);
			
			String prjctNDDT = pJson.getString(ProjectJSONKeyz.NDDTKey);
			LocalDateTime ldtNDDTOfPrjct = LittleTimeTools.LDTfromTimeString(prjctNDDT);
			
			String prjctDeadLine = pJson.getString(ProjectJSONKeyz.DLDTKey);
			String deadLineStr = "";
			LocalDateTime prjctDLDTYear = LittleTimeTools.LDTfromTimeString(prjctDeadLine);
			if(index==firstStepIndex)
			{
				System.out.println("");
				System.out.println("Deadline must be between Projec NDDT: " + prjctNDDT + " and Project Deadline: " + prjctDeadLine);
				LocalDateTime deadLineLDT = iss.getDateTime("Step DeadLine Please.", ldtNDDTOfPrjct, prjctDLDTYear);
				deadLineStr = LittleTimeTools.timeString(deadLineLDT);
			}
			else
			{
				System.out.println("");
				String oldStepTDT = oldStep.getString(StepJSONKeyz.TDTKey);
				System.out.println("Deadline must be between old-Step TDT: " + oldStepTDT
									+" and Project Deadline: " + prjctDeadLine);
				LocalDateTime ldtOldStepTDT = LittleTimeTools.LDTfromTimeString(oldStepTDT);
				LocalDateTime deadLineLDT = iss.getDateTime("Step DeadLine Please.", ldtOldStepTDT, prjctDLDTYear);
				deadLineStr = LittleTimeTools.timeString(deadLineLDT);
			}
			
			newStep.put(StepJSONKeyz.DLDTKey, deadLineStr);
			newStep.put(StepJSONKeyz.statusKey, stepStatus);
			newStep.put(StepJSONKeyz.descKey, descriptionOfStep);
			newStep.put(StepJSONKeyz.NDDTKey, LittleTimeTools.timeString(nddtOfStep));
			newStep.put(StepJSONKeyz.BDTKey, LittleTimeTools.timeString(bdtOfStep));
		} 
		catch (InputMismatchException | IOException e) 
		{
			System.out.println("");
			System.out.println("Sometin went wrong! Do it again.");
			return spawnStep(pJson, index);//Enforced!!!
		}
		
		if(stepDataIsValide(pJson, oldStep, newStep, index))
		{
			pJson.put(ProjectJSONKeyz.statusKey, stepStatus);//this overwrites old status!
						
			steps.put(index, newStep);
			
			pJson.put(ProjectJSONKeyz.stepArrayKey, steps);
			
			return pJson;
		}
		else 
		{
			System.out.println("");
			System.out.println("Neuer Versuch für step Data!!");
			return spawnStep(pJson, index);//Enforced Input!!
		}
	}

	public boolean stepDataIsValide(JSONObject pJson, JSONObject oldStep, JSONObject newStep, int index)
	{
	
		String msg = stepIsOkToItsSelf(newStep);
		System.out.println("");
		if(!msg.equals("OK"))
		{
			System.out.println(msg);
			return false;
		}
		else System.out.println(msg);

		msg = stepIsNotViolatingTimeframeOfProject(newStep, pJson);
		if(!msg.equals("OK"))
		{
			System.out.println(msg);
			return false;
		}
		else System.out.println(msg);

		if(index>firstStepIndex)
		{
			msg = stepIsNotViolatingTimeframeOfFormerStep(oldStep, newStep);
			if(!msg.equals("OK"))
			{
				System.out.println(msg);
				return false;
			}
			else System.out.println(msg);
		}
		
		System.out.println("Step Data is Ok");
		return true;
	}
	
	public String stepIsNotViolatingTimeframeOfProject(JSONObject step, JSONObject pJson)
	{
		


		String bdtOfStepStr = step.getString(StepJSONKeyz.BDTKey);
		LocalDateTime bdtOfStep = LittleTimeTools.LDTfromTimeString(bdtOfStepStr);
		
		if(LocalDateTime.now().isBefore(bdtOfStep))return "Step is Born after Now.";
			
		String prjctDLStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
		LocalDateTime prjctDeadLine = LittleTimeTools.LDTfromTimeString(prjctDLStr);

		String dldtOfStepStr = step.getString(StepJSONKeyz.DLDTKey);
		LocalDateTime dldtOfStep = LittleTimeTools.LDTfromTimeString(dldtOfStepStr);
		
		if(prjctDeadLine.isBefore(dldtOfStep))return "Step is Violating Project Time Frame";

		return "OK";
	}

	public String stepIsNotViolatingTimeframeOfFormerStep(JSONObject oldStep, JSONObject newStep)
	{
		
		String tdtOfOldStepStr = oldStep.getString(StepJSONKeyz.TDTKey);
		LocalDateTime tdtOfOldStep = LittleTimeTools.LDTfromTimeString(tdtOfOldStepStr);
		
		String bdtOfNewStepStr = newStep.getString(StepJSONKeyz.BDTKey);
		LocalDateTime bdtOfNewStep = LittleTimeTools.LDTfromTimeString(bdtOfNewStepStr);
		
		if(bdtOfNewStep.isBefore(tdtOfOldStep))return "Step is violating timeframe of former Step";
		
		return "OK";
	}
	
	public String stepIsOkToItsSelf(JSONObject step)
	{
		
		
		String deadLineStr = step.getString(StepJSONKeyz.DLDTKey);
		String desc= step.getString(StepJSONKeyz.descKey);
		String nddtStr = step.getString(StepJSONKeyz.NDDTKey);
		String bdtStr = step.getString(StepJSONKeyz.BDTKey);

		if(desc.equals(""))return "Please write a Description";

		LocalDateTime born = LittleTimeTools.LDTfromTimeString(bdtStr);
			
		LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(deadLineStr);
			
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		
		if(dldt.isBefore(born)) return "Deadline of Step can't be before Step Born.";
		if(nddt.isBefore(born)) return "Step can't be noted down before Born.";
		if(dldt.isBefore(nddt)) return "Step can't have deadline before noted down.";
		
		return "OK";
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
	
	public void wakeMODProject(JSONObject pJson) throws IOException
	{
		
		LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(pJson.getString(ProjectJSONKeyz.BDTKey));
		LocalDateTime nddt = LocalDateTime.now();
		LocalDateTime dldt = null;
				
		String nddtStr = LittleTimeTools.timeString(nddt);
		pJson.put(ProjectJSONKeyz.NDDTKey, nddtStr);

		System.out.println("");
		//Deadline should be at least five Minutes in the Future. But not more than 20 Years.
		dldt = iss.getDateTime(dldtQ, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusYears(20));
		String deadLineStr = LittleTimeTools.timeString(dldt);
		pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);//Overwrites current "UNKNOWN" value.
	
		JSONObject tmp = spawnFirstStep(pJson);//Here status will be overwritten.
		if(tmp==null)return;
		else pJson = tmp;
			
		String goal = pJson.getString(ProjectJSONKeyz.goalKey);
		if(!timeAndGoalOfActiveProjectIsValide(nddt, bdt, dldt, goal))return;

	}
	
	//Can only terminate last Step of Project JSONObject.
	public void terminateStep(JSONObject pJson) throws IOException
	{
		
		LocalDateTime jetzt = LocalDateTime.now();
		String jetztStr = LittleTimeTools.timeString(jetzt);
		JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
		int index = steps.length();
		
		JSONObject step = steps.getJSONObject(index-1);
		String nddtOfStepStr = step.getString(StepJSONKeyz.NDDTKey);
		LocalDateTime nddtOfStep = LittleTimeTools.LDTfromTimeString(nddtOfStepStr);
		
		boolean wasItASuccess = iss.getYesOrNo(stepSuccesQstn);

		String stepStatus;
		if(wasItASuccess)stepStatus = StatusMGMT.success;
		else stepStatus = StatusMGMT.failed;
		
		try
		{
			String terminalNote = "";
			boolean thereIsATerminalNote = iss.getYesOrNo("want to make a Terminal note?");
			if(thereIsATerminalNote)terminalNote = iss.getString(stepTerminationNotePhrase);
			
			LocalDateTime tdt = LocalDateTime.now();
			boolean wantToChangeTDTOfStep = iss.getYesOrNo(wantToChangeTDTOfStepQstn);
			if(wantToChangeTDTOfStep)
			{
				System.out.println("TDT must be between " + nddtOfStepStr + " and " + jetztStr);
				tdt = iss.getDateTime(stepWhenTDTQstn, nddtOfStep, jetzt);
			}
			
			step.put(StepJSONKeyz.statusKey, stepStatus);//Project Status ain't bothered!!
			String when = LittleTimeTools.timeString(tdt);
			step.put(StepJSONKeyz.TDTKey, when);
			if(!terminalNote.trim().equals(""))step.put(StepJSONKeyz.TDTNoteKey, terminalNote);
		}
		catch(IllegalArgumentException exc)
		{
			System.out.println("IllegalArgument!!!" + exc);
			terminateStep(pJson);
		}
		catch(JSONException jsonExc)
		{
			System.out.println("JSON macht Probleme");
		}
	
	}
	
	public void terminateProject(JSONObject pJson) throws InputMismatchException, JSONException, IOException
	{
		
		System.out.println(infoAlertTxtPhrase);
		
		terminateStep(pJson);
		
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
		if(wantChangeTDTQuestion)tdt = iss.getDateTime(prjctWhenTDTQstn,ancient, jetzt);

		String dldtStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
		LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
				
		if(tdt.isAfter(dldt))
		{
			System.out.println("Termination can't be after Deadline.");
			terminateProject(pJson);
			return;
		}
				
		String nddtStr = pJson.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
				
		if(tdt.isBefore(nddt))
		{
			System.out.println("Termination can't be before Note-Down-Date-Time");
			terminateProject(pJson);
			return;
		}
			
		if(tdt.isAfter(jetzt))
		{
			System.out.println("TDT can't be after now.");
			terminateProject(pJson);
			return;
		}
				
		pJson.put(ProjectJSONKeyz.statusKey, prjctStatus);
				
		String tdtStr = LittleTimeTools.timeString(tdt);
		pJson.put(ProjectJSONKeyz.TDTKey, tdtStr);

		if(!terminalNote.trim().equals(""))pJson.put(ProjectJSONKeyz.TDTNoteKey, terminalNote);
			
	}
	
	@Override
	public void addBeholders(Beholder<String> b) 
	{
		observer.add(b);
	}

	@Override
	public void informBeholders(String msg) 
	{
		for(Beholder<String> b: observer)
		{
			b.refresh(msg);
		}
	}

	@Override
	public void removeBeholders(Beholder<String> b) 
	{
		if(!observer.contains(b))throw new IllegalArgumentException(illAExceMsg);
		else observer.remove(b);
	}
}