package jborg.gtdForBash;

import java.io.IOException;
import java.io.InputStream;
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


public class DataSpawn_ii implements Subjekt<String>
{
	
	
	//TODO:->		Eliminate rest of Literals in Code.		<-
	
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
	public static final String waitingForPhrase = "What u waiting for?";
	
	public static final String infoAlertTxtPhrase = "Remember Step Termination Note is Project Termination Note at this last Step.";
	public static final String deadLinePrjctQstn = "Deadline for Project";
	public static final String unknownDLLblTxt = "Unkown Deadline";
	public static final String makeDLBtnTxt = "Make Deadline";
	
	public static JSONObject spawnNewProject(InputStream is, Map<String, JSONObject> knownProjects, StatusMGMT statusMGMT)
	{
		
		System.out.println("");
		String name = Input.getString(prjctNameQ);
		name = name.trim();
		if(knownProjects.keySet().contains(name))
		{
			System.out.println("There is already a Project with that Name.");
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
			boolean isModProject = Input.getYesOrNo(isModProjectQ);		
			if(isModProject)status = StatusMGMT.mod;
			
			System.out.println("");
			String goal = Input.getString(goalQ);
			
			System.out.println("");
			boolean changeBDT = Input.getYesOrNo(changeBDTQ);
			int yearRange = LocalDateTime.now().getYear()-minBDTYear;//must be born before now.

			if(changeBDT)
			{
				System.out.println("");
				bdt = Input.getDateTime(is, bdtQ, ancient, yearRange);
			}
			else bdt = nddt;
				
			pJson.put(ProjectJSONKeyz.nameKey, name);
			pJson.put(ProjectJSONKeyz.goalKey, goal);
			pJson.put(ProjectJSONKeyz.statusKey, status);
			
			String deadLineStr = "UNKNOWN";
			pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);
				
			String bdtStr = LittleTimeTools.timeString(bdt);
			pJson.put(ProjectJSONKeyz.BDTKey, bdtStr);
			
			String nddtStr = LittleTimeTools.timeString(nddt);
			pJson.put(ProjectJSONKeyz.NDDTKey, nddtStr);

			if(!isModProject)
			{
				
				int minYear = nddt.getYear();
				int minMonth = nddt.getMonthValue();
				int minDay = nddt.getDayOfMonth();
				int minHour = nddt.getHour();
				int minMinute = nddt.plusMinutes(5).getMinute();//Deadline should be at least five Minutes in the Future.		
				LocalDateTime timeOffset = LocalDateTime.of(minYear, minMonth, minDay, minHour, minMinute);
				
				System.out.println("");
				dldt = Input.getDateTime(is, dldtQ, timeOffset, dldtRange);
				deadLineStr = LittleTimeTools.timeString(dldt);
				pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);//Overwrites current "UNKNOWN" value.
				
				JSONObject tmp = spawnFirstStep(is, pJson);//Here status will be overwritten.
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
	
	private static boolean timeAndGoalOfActiveProjectIsValide(LocalDateTime nddt, LocalDateTime bdt, LocalDateTime dldt, String goal)
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

	public static JSONObject spawnFirstStep(InputStream is, JSONObject pJson)
	{	
		
		System.out.println("Spawning first Step for Project: " + pJson.getString(ProjectJSONKeyz.nameKey));
		return spawnStep(is, pJson, firstStepIndex);
	}

	public static void appendStep(InputStream is, JSONObject pJson)
	{
		
		System.out.println("Appending Step to Project: " + pJson.getString(ProjectJSONKeyz.nameKey));
		
		JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
		int length = steps.length();
		
		pJson = spawnStep(is, pJson, length);
	}
	
	private static JSONObject spawnStep(InputStream is, JSONObject pJson, int index)
	{

		System.out.println("Spawning Step. Index: " + index + " Project: "+pJson.toString(4));

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
			differentBDT = Input.getYesOrNo("Want to change bdt of Step?");
			int yearRangeStepBDT = LocalDateTime.now().getYear()-ldtBDTOfPrj.getYear();
			System.out.println("");
			System.out.println("BDT of Project(" + bdtOfPrj + ") - Now!(" + jetzt + ") Step BDT must be in that Range.");
			if(differentBDT)bdtOfStep = Input.getDateTime(is, "DateTime of Step BDT: ", ldtBDTOfPrj, yearRangeStepBDT);
			else bdtOfStep = nddtOfStep;
			
			while(stepStatus.trim().equals("")) 
			{
				List<String> sss = new ArrayList<>();
				sss.addAll(stepStartStatuses);
				System.out.println("");
				stepStatus = Input.getAnswerOutOfList(is, "Choose Step Status", sss);
			}
						
			String phrase;
			if(stepStatus.equals(StatusMGMT.waiting))phrase = waitingForPhrase;
			else phrase = stepDescPhrase;

			String descriptionOfStep = Input.getString(phrase);
			
			String prjctNDDT = pJson.getString(ProjectJSONKeyz.NDDTKey);
			LocalDateTime ldtNDDTOfPrjct = LittleTimeTools.LDTfromTimeString(prjctNDDT);
			
			String prjctDeadLine = pJson.getString(ProjectJSONKeyz.DLDTKey);
			String deadLineStr = "";
			LocalDateTime prjctDLDTYear = LittleTimeTools.LDTfromTimeString(prjctDeadLine);
			if(index==firstStepIndex)
			{
				System.out.println("");
				System.out.println("Deadline must be between Projec NDDT: " + prjctNDDT + " and Project Deadline: " + prjctDeadLine);
				LocalDateTime deadLineLDT = Input.getDateTime(is, "Step DeadLine Please.", ldtNDDTOfPrjct, prjctDLDTYear);
				deadLineStr = LittleTimeTools.timeString(deadLineLDT);
			}
			else
			{
				System.out.println("");
				String oldStepTDT = oldStep.getString(StepJSONKeyz.TDTKey);
				System.out.println("Deadline must be between old-Step TDT: " + oldStepTDT
									+" and Project Deadline: " + prjctDeadLine);
				LocalDateTime ldtOldStepTDT = LittleTimeTools.LDTfromTimeString(oldStepTDT);
				LocalDateTime deadLineLDT = Input.getDateTime(is, "Step DeadLine Please.", ldtOldStepTDT, prjctDLDTYear);
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
			return spawnStep(is, pJson, index);//Enforced!!!
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
			return spawnStep(is, pJson, index);//Enforced Input!!
		}
	}

	public static boolean stepDataIsValide(JSONObject pJson, JSONObject oldStep, JSONObject newStep, int index)
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
	
	public static String stepIsNotViolatingTimeframeOfProject(JSONObject step, JSONObject pJson)
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

	public static String stepIsNotViolatingTimeframeOfFormerStep(JSONObject oldStep, JSONObject newStep)
	{
		
		String tdtOfOldStepStr = oldStep.getString(StepJSONKeyz.TDTKey);
		LocalDateTime tdtOfOldStep = LittleTimeTools.LDTfromTimeString(tdtOfOldStepStr);
		
		String bdtOfNewStepStr = newStep.getString(StepJSONKeyz.BDTKey);
		LocalDateTime bdtOfNewStep = LittleTimeTools.LDTfromTimeString(bdtOfNewStepStr);
		
		if(bdtOfNewStep.isBefore(tdtOfOldStep))return "Step is violating timeframe of former Step";
		
		return "OK";
	}
	
	public static String stepIsOkToItsSelf(JSONObject step)
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
	
	public static void addNote(JSONObject pJson)
	{
		int index = 0;
		JSONArray ja;
		boolean hasNoteArray = pJson.has(ProjectJSONKeyz.noteArrayKey);
		
		if(hasNoteArray)
		{
			ja = pJson.getJSONArray(ProjectJSONKeyz.noteArrayKey);
			index = ja.length();
		}
		else ja = new JSONArray();
		
		
		String noteTxt = Input.getString(noteAddPhrase);
		if(!noteTxt.trim().equals(""))
		{
			ja.put(index, noteTxt);
			pJson.put(ProjectJSONKeyz.noteArrayKey, ja);
		}
	}
	
	//Can only terminate last Step of Project JSONObject.
	public static boolean terminateStep(InputStream is, JSONObject pJson) throws IOException
	{
		
		JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
		int index = steps.length();
		
		JSONObject step = steps.getJSONObject(index-1);
		String nddtOfStepStr = step.getString(StepJSONKeyz.NDDTKey);
		LocalDateTime nddtOfStep = LittleTimeTools.LDTfromTimeString(nddtOfStepStr);
		
		boolean wasItASuccess = Input.getYesOrNo(stepSuccesQstn);
		if(wasItASuccess||!wasItASuccess)
		{
			
			int tdtYearRangeMax = 1;
			String stepStatus = "";
			if(wasItASuccess)stepStatus = StatusMGMT.success;
			else stepStatus = StatusMGMT.failed;
			
			String terminalNote = "";
			boolean thereIsATerminalNote = Input.getYesOrNo("want to make a Terminal note?");
			if(thereIsATerminalNote)terminalNote = Input.getString(stepTerminationNotePhrase);
			
			LocalDateTime tdt = LocalDateTime.now();
			boolean wantToChangeTDTOfStep = Input.getYesOrNo(wantToChangeTDTOfStepQstn);	
			if(wantToChangeTDTOfStep)tdt = Input.getDateTime(is, stepWhenTDTQstn, nddtOfStep, tdtYearRangeMax);
			
			step.put(StepJSONKeyz.statusKey, stepStatus);//Project Status ain't bothered!!
			String when = LittleTimeTools.timeString(tdt);
			step.put(StepJSONKeyz.TDTKey, when);
			if(!terminalNote.trim().equals(""))step.put(StepJSONKeyz.TDTNoteKey, terminalNote);
			
			return true;
		}
		else return false;
	}
	
	public static boolean terminateProject(InputStream is, JSONObject pJson) throws InputMismatchException, JSONException, IOException
	{
		
		System.out.println(infoAlertTxtPhrase);
		
		if(terminateStep(is, pJson))
		{
			
			String title = "";
			String prjctStatus = "";
			boolean success = Input.getYesOrNo(prjctSuccessQstn);
			
			
			if(success)prjctStatus = StatusMGMT.success;
			else prjctStatus = StatusMGMT.failed;

			JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
			int index = steps.length();
				
			JSONObject step = steps.getJSONObject(index-1);
			String terminalNote = "";
			//Step TDT-Note = Project TDT-Note. Step might not have a TDT-Note.
			if(step.has(StepJSONKeyz.TDTNoteKey))terminalNote = step.getString(StepJSONKeyz.TDTNoteKey);
			
			boolean wantChangeTDTQuestion = Input.getYesOrNo(wantToChangeTDTOfPrjctQstn);
			LocalDateTime tdt = LocalDateTime.now();
			int yearRange = 1;	
			if(wantChangeTDTQuestion)tdt = Input.getDateTime(is, prjctWhenTDTQstn,ancient, yearRange);
				
			String dldtStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
			LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
				
			if(tdt.isAfter(dldt))
			{
				System.out.println("Termination can't be after Deadline.");
				return false;
			}
				
			String nddtStr = pJson.getString(ProjectJSONKeyz.NDDTKey);
			LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
				
			if(tdt.isBefore(nddt))
			{
				System.out.println("Termination can't be before Note-Down-Date-Time");
				return false;
			}
			
			LocalDateTime jetzt = LocalDateTime.now();
			if(tdt.isAfter(jetzt))
			{
				System.out.println("TDT can't be after now.");
				return false;
			}
				
			pJson.put(ProjectJSONKeyz.statusKey, prjctStatus);
				
			String tdtStr = LittleTimeTools.timeString(tdt);
			pJson.put(ProjectJSONKeyz.TDTKey, tdtStr);
				
			//Step TDT-Note = Project TDT-Note.
			if(!terminalNote.trim().equals(""))pJson.put(ProjectJSONKeyz.TDTNoteKey, terminalNote);
			
			return true;
		}
		else
		{
			System.out.println("Can't Terminate last Step.");
			return false;
		}
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