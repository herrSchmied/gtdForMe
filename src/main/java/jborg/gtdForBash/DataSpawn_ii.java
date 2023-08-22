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
import org.json.JSONObject;


import allgemein.Beholder;

import allgemein.LittleTimeTools;

import allgemein.Subjekt;

import consoleTools.*;


public class DataSpawn_ii implements Subjekt<String>
{
	
	
	//Done->		Eliminate rest of Literals in Code.		<-Done
	
	public static final String styleClassGenericNode = "generic-node";
	
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
	private static Set<String> projectStartStatie = new HashSet<>(Arrays.asList(StatusMGMT.atbd, StatusMGMT.waiting));
	
	public static final String stepStatusPhrase = "Choose Status: ";
	public static final String wantToChangeTDTOfStepQstn = "Wan't to change TDT of Step?";
	public static final String waitingForPhrase = "What u waiting for?";
	
	public static final String infoAlertTxtPhrase = "Remember Step Termination Note is Project Termination Note at this last Step.";
	public static final String deadLinePrjctQstn = "Deadline for Project";
	public static final String unknownDLLblTxt = "Unkown Deadline";
	public static final String makeDLBtnTxt = "Make Deadline";
	
	public static JSONObject spawnNewProject(Map<String, JSONObject> knownProjects, StatusMGMT statusMGMT)
	{
		
		String name = Input.getString(prjctNameQ);
		name = name.trim();
		if(knownProjects.keySet().contains(name)) throw new IllegalArgumentException();

		JSONObject pJson = new JSONObject();
 
		boolean isModProject = false;
		String status = "";
		LocalDateTime bdt = null;
		LocalDateTime nddt = LocalDateTime.now();
		LocalDateTime dldt = null;

		
		if(!isModProject)
		{
			
			
			try 
			{
				
				isModProject = Input.getYesOrNo(isModProjectQ);
				
				if(!isModProject)
				{
					List<String> startStatie = new ArrayList<>();
					startStatie.addAll(projectStartStatie);
					status = Input.getAnswerOutOfList("Choose Project Status", startStatie);
				}
				else status = StatusMGMT.mod;
				
				String goal = Input.getString(goalQ);

				
				boolean changeBDT = Input.getYesOrNo(changeBDTQ);
				System.out.println("changeBDT: " + changeBDT);
				int yearRange = LocalDateTime.now().getYear()-minBDTYear;//must be born before now.

				if(changeBDT)
				{
					bdt = Input.getDateTime(bdtQ, minBDTYear, yearRange, minMonth, minDay, minHour, minMinute);
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
					
					dldt = Input.getDateTime(dldtQ, nddt.getYear(), dldtRange, minMonth, minDay, minHour, minMinute);
					deadLineStr = LittleTimeTools.timeString(dldt);
					pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);//Overwrites current "UNKNOWN" value.

					JSONObject tmp = spawnFirstStep(pJson);
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

	public static JSONObject spawnFirstStep(JSONObject pJson)
	{	
		
		System.out.println("Spawning first Step for Project: " + pJson.getString(ProjectJSONKeyz.nameKey));
		return spawnStep(pJson, firstStepIndex);
	}

	public static void appendStep(JSONObject pJson)
	{
		
		System.out.println("Appending Step to Project: " + pJson.getString(ProjectJSONKeyz.nameKey));
		
		JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
		int length = steps.length();
		
		pJson = spawnStep(pJson, length);
	}
	
	private static JSONObject spawnStep(JSONObject pJson, int index)
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
		int minYear = 0;
		int yearRange = 2100;
		int monthOffset = 1;
		int dayOffset = 1;
		int minHour = 0;
		int minMinute = 0;

		try 
		{
			differentBDT = Input.getYesOrNo("Want to change bdt of Step?");

		
			if(differentBDT)bdtOfStep = Input.getDateTime("DateTime of Step:", minYear, yearRange, monthOffset, dayOffset, minHour, minMinute);
			else bdtOfStep = nddtOfStep;
			
			while(stepStatus.trim().equals("")) 
			{
				List<String> sss = new ArrayList<>();
				sss.addAll(stepStartStatuses);
				stepStatus = Input.getAnswerOutOfList("Choose Step Status", sss);
			}
						
			String phrase;
			if(stepStatus.equals(StatusMGMT.waiting))phrase = waitingForPhrase;
			else phrase = stepDescPhrase;

			String descriptionOfStep = Input.getString(phrase);
			
			String prjctNDDT = pJson.getString(ProjectJSONKeyz.NDDTKey);
			String prjctDeadLine = pJson.getString(ProjectJSONKeyz.DLDTKey);
			
			if(index==firstStepIndex)
			{
				System.out.println("Deadline must be between StepNDDT: " + prjctNDDT + " and Project Deadline: " + prjctDeadLine);
			}
			else
			{
				String oldStepTDT = oldStep.getString(StepJSONKeyz.TDTKey);
				System.out.println("Deadline must be between old-Step TDT: " + oldStepTDT
									+" and Project Deadline: " + prjctDeadLine);
			}
			
			LocalDateTime deadLineLDT = Input.getDateTime("Step DeadLine Please.",minYear, yearRange, monthOffset, dayOffset, minHour, minMinute);
			String deadLineStr = LittleTimeTools.timeString(deadLineLDT);
			
			newStep.put(StepJSONKeyz.DLDTKey, deadLineStr);
			newStep.put(StepJSONKeyz.statusKey, stepStatus);
			newStep.put(StepJSONKeyz.descKey, descriptionOfStep);
			newStep.put(StepJSONKeyz.NDDTKey, LittleTimeTools.timeString(nddtOfStep));
			newStep.put(StepJSONKeyz.BDTKey, LittleTimeTools.timeString(bdtOfStep));
		} 
		catch (InputMismatchException | IOException e) 
		{
			return null;
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
			System.out.println("Neuer Versuch für step Data!!");
			return spawnStep(pJson, index);//Enforced Input!!
		}
	}

	public static boolean stepDataIsValide(JSONObject pJson, JSONObject oldStep, JSONObject newStep, int index)
	{
	
		String msg = stepIsOkToItsSelf(newStep);
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
	
	public void addNote(JSONObject pJson)
	{
		int index = 0;
		String title = "";
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
	public boolean terminateStep(JSONObject pJson)
	{
		/*
		String title = "";
		
		JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
		int index = steps.length();
		
		JSONObject step = steps.getJSONObject(index-1);
		
		String terminalStatus = Input.question.apply(stepSuccesQstn);
		if(terminalStatus.equals(Input.yesAnswerToQuestion)||terminalStatus.equals(Input.noAnswerToQuestion))
		{
			
			String stepStatus = "";
			if(terminalStatus.equals(Input.yesAnswerToQuestion))stepStatus = StatusMGMT.success;
			else stepStatus = StatusMGMT.failed;
			
			String terminalNote = Input.getTextInput(title, stepTerminationNotePhrase, terminalIsInTxtA);
			String whenQuestion = Input.question.apply(wantToChangeTDTOfStepQstn);
			LocalDateTime tdt = LocalDateTime.now();
			
			if(whenQuestion.equals(Input.yesAnswerToQuestion))
			{
				tdt = Input.getDateTimeInput(title, stepWhenTDTQstn);
			}
			
			step.put(StepJSONKeyz.statusKey, stepStatus);//Project Status ain't bothered!!
			String when = LittleTimeTools.timeString(tdt);
			step.put(StepJSONKeyz.TDTKey, when);
			if(!terminalNote.trim().equals(""))step.put(StepJSONKeyz.TDTNoteKey, terminalNote);
			
			return true;
		}
		else return false;
		*/
		
		return false;
	}
	
	public void terminateProject(JSONObject pJson)
	{
		
		/*
		Output.infoAlert(infoAlertTxtPhrase);
		
		if(terminateStep(pJson))
		{
			
			String title = "";
			String prjctStatus = "";
			String terminalStatus = Input.question.apply(prjctSuccessQstn);
			
			if(terminalStatus.equals(Input.cancelAnswerToQuestion))return;
			
			if(terminalStatus.equals(Input.yesAnswerToQuestion)||terminalStatus.equals(Input.noAnswerToQuestion))
			{
				
				if(terminalStatus.equals(Input.yesAnswerToQuestion))prjctStatus = StatusMGMT.success;
				else prjctStatus = StatusMGMT.failed;
					
				
				JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
				int index = steps.length();
				
				JSONObject step = steps.getJSONObject(index-1);				
				String terminalNote = step.getString(StepJSONKeyz.TDTNoteKey);//Step TDT-Note = Project TDT-Note.
				steps.put(index-1, step);
				
				String wantChangeTDTQuestion = Input.question.apply(wantToChangeTDTOfPrjctQstn);
				LocalDateTime tdt = LocalDateTime.now();
				
				if(wantChangeTDTQuestion.equals(Input.yesAnswerToQuestion))
				{
					tdt = Input.getDateTimeInput(title, prjctWhenTDTQstn);
				}
				
				String dldtStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
				LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
				
				if(tdt.isAfter(dldt))
				{
					Output.errorAlert("Termination can't be after Deadline.");
					return;
				}
				
				String nddtStr = pJson.getString(ProjectJSONKeyz.NDDTKey);
				LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
				
				if(tdt.isBefore(nddt))
				{
					Output.errorAlert("Termination can't be before Note-Down-Date-Time");
					return;
				}
				
				pJson.put(ProjectJSONKeyz.statusKey, prjctStatus);
				
				String tdtStr = LittleTimeTools.timeString(tdt);
				pJson.put(ProjectJSONKeyz.TDTKey, tdtStr);
				
				//Step TDT-Note = Project TDT-Note.
				if(!terminalNote.trim().equals(""))pJson.put(ProjectJSONKeyz.TDTNoteKey, terminalNote);	
			}
		}
		*/
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