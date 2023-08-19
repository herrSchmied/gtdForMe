package jborg.gtdForBash;

import java.time.LocalDateTime;

import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

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

	private final boolean nameIsNotEditable = true;
	private final boolean stepDescInTxtA = true;
	private final boolean statusRangeIsEditable = false;
	private final boolean terminalIsInTxtA = true;

	public static final String prjctNmLblTxt = "Project_Name";
	public static final String modPrjctNmLblQstn = "MOD Project?";
	public static final String sttsNmLblTxt = "Status";
	public static final String goalNmLblTxt = "Goal";
	public static final String bdtNowLblTxt = "BDT(Now)";
	public static final String bdtThenLblTxt = "BDT(Then)";
	public final static String prjctWhenTDTQstn = "When took the Termination of this Project place?";
	public final static String wantToChangeTDTOfPrjctQstn = "Wan't to change TDT of Project?";
	public final static String prjctSuccessQstn = "Was Project a Success?";
	
	public static final String btnTxtChangeBDT = "Change BDT";
	public static final String changingBDTInputPhrase = "Determining BDT";
	
	public final static String stepWhenTDTQstn = "When took the Termination of this Step place?";
	public static final String stepDescPhrase = "Describe Step:";
	public static final String descStepInputTitle = "Description";
	public static final String stepSuccesQstn = "Was Step a Success?";
	
	public static final String bdtInputTitle = "BDT";
	public static final String bdtInputPhrase = "When?";
	
	public static final String btnTxtCancel = "Cancel";
	public static final String btnTxtCreate = "Create";
	
	public static final String illAExceMsg = "Don't know that Beholder.";
	
	public static final String differentBDTQstn = "Do You want change BDT of Step?";

	private ArrayList<Beholder<String>> observer = new ArrayList<Beholder<String>>();
	
	public static final String noteAddPhrase = "Write Note:";
	public static final boolean notTxtAQstn = true;
	
	private Set<String> stepStartStatuses = new HashSet<>(Arrays.asList(StatusMGMT.atbd, StatusMGMT.waiting));
	public static final String stepStatusPhrase = "Choose Status: ";
	public static final String wantToChangeTDTOfStepQstn = "Wan't to change TDT of Step?";
	public static final String waitingForPhrase = "What u waiting for?";
	
	public static final String infoAlertTxtPhrase = "Remember Step Termination Note is Project Termination Note at this last Step.";
	public static final String deadLinePrjctQstn = "Deadline for Project";
	public static final String unknownDLLblTxt = "Unkown Deadline";
	public static final String makeDLBtnTxt = "Make Deadline";
	
	public JSONObject spawnNewProject(String name)
	{

		/*
		System.out.println("Spawning Project: "+name);
		
		JSONObject pJson[] = new JSONObject[1];
		pJson[0] = null;
		
		Stage stage = new Stage();
		stage.initStyle(StageStyle.UNDECORATED);
		
		VBox root = new VBox();
		root.getStyleClass().add(styleClassGenericNode);
		
		Scene scene = new Scene(root,400,400);
		stage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
		
		Label pNameLabel = new Label(prjctNmLblTxt+":");
		pNameLabel.getStyleClass().add(styleClassGenericNode);
		
		TextField pName = new TextField();
		pName.setText(name);
		pName.setDisable(nameIsNotEditable);
		pName.getStyleClass().add(styleClassGenericNode);
		
		HBox nameBox = new HBox();
		nameBox.getChildren().addAll(pNameLabel, pName);
		
		Label modLbl = new Label(modPrjctNmLblQstn);
		modLbl.getStyleClass().add(styleClassGenericNode);
		
		CheckBox modCheck = new CheckBox();
		modCheck.getStyleClass().add(styleClassGenericNode);
		
		Label statusLbl = new Label(sttsNmLblTxt + ": " +StatusMGMT.atbd);
		statusLbl.getStyleClass().add(styleClassGenericNode);
		
		HBox modBox = new HBox();
		modBox.getChildren().addAll(modLbl, modCheck, statusLbl);
		
		Label goalLbl = new Label(goalNmLblTxt+":");
		goalLbl.getStyleClass().add(styleClassGenericNode);
		
		TextArea goalTxtA = new TextArea();
		goalTxtA.getStyleClass().add(styleClassGenericNode);
		goalTxtA.setText("");
		
		VBox goalBox = new VBox();
		goalBox.getChildren().addAll(goalLbl, goalTxtA);

		LocalDateTime now = LocalDateTime.now();
		String[] bdt = new String[1];
		bdt[0] = LittleTimeTools.timeString(now);
		Label bdtLbl = new Label(bdtNowLblTxt + ": " + bdt[0]);
		bdtLbl.getStyleClass().add(styleClassGenericNode);
		
		Button changeBDTBtn = new Button(btnTxtChangeBDT);
		
		HBox bdtBox = new HBox();
		bdtBox.getChildren().addAll(bdtLbl, changeBDTBtn);
		
		Button deadLineBtn = new Button(makeDLBtnTxt);
		
		String[] deadLineStr = new String[1];
		deadLineStr[0] = "";
		Label deadLineLbl = new Label(unknownDLLblTxt);
		deadLineLbl.getStyleClass().add(styleClassGenericNode);
		
		HBox deadLineBox = new HBox();
		deadLineBox.getChildren().addAll(deadLineLbl, deadLineBtn);

		Button cancel = new Button(btnTxtCancel);
		Button create = new Button(btnTxtCreate);
		
		HBox submitOrNotBox = new HBox();
		submitOrNotBox.getChildren().addAll(cancel, create);
		
		String []status = new String[1];
		status[0] = StatusMGMT.atbd;
		modCheck.setOnAction((event)->
		{
			if(modCheck.isSelected())
			{
				statusLbl.setText(sttsNmLblTxt + ": " + StatusMGMT.mod);
				status[0] = StatusMGMT.mod;
			}
			else 
			{
				statusLbl.setText(sttsNmLblTxt + ": " + StatusMGMT.atbd);
				status[0] = StatusMGMT.atbd;
			}
		});

		Supplier<Boolean> dataIsValide = ()->
		{
			/*String n = pName.getText().trim();/Name already checked/
			String g = goalTxtA.getText().trim();
			
			if(deadLineStr[0].equals(""))
			{
				Output.errorAlert(prjctNotValide[0]);
				return false;
			}
			
			LocalDateTime prjctBorn = LittleTimeTools.LDTfromTimeString(bdt[0]);
			LocalDateTime prjctDeadline = LittleTimeTools.LDTfromTimeString(deadLineStr[0]);
			
			if(prjctBorn.isAfter(prjctDeadline))
			{
				Output.errorAlert(prjctNotValide[1]);
				return false;
			}
			
			LocalDateTime jetzt = LocalDateTime.now();
			
			if(jetzt.isBefore(prjctBorn))
			{
				Output.errorAlert(prjctNotValide[2]);
				return false;
			}
			
			if(g.trim().equals(""))
			{
				Output.errorAlert(prjctNotValide[3]);
				return false;
			}
			
			return true;
		};
		
		deadLineBtn.setOnAction((event)->
		{
			
			LocalDateTime deadLineLDT = Input.getDateTimeInput("", deadLinePrjctQstn);
			deadLineStr[0] = LittleTimeTools.timeString(deadLineLDT);
			deadLineLbl.setText("DLDT: " + deadLineStr[0]);
		});
		
		create.setOnAction((event)->
		{
			
			pJson[0] = new JSONObject();
			
			changeBDTBtn.setDisable(true);
			create.setDisable(true);
			cancel.setDisable(true);
			
			pJson[0].put(ProjectJSONKeyz.nameKey, pName.getText());
			pJson[0].put(ProjectJSONKeyz.goalKey, goalTxtA.getText());
			pJson[0].put(ProjectJSONKeyz.statusKey, status[0]);//overwrites!!!by Step spawn.(wait:<->:status)
			pJson[0].put(ProjectJSONKeyz.DLDTKey, deadLineStr[0]);
			pJson[0].put(ProjectJSONKeyz.BDTKey, bdt[0]);
			pJson[0].put(ProjectJSONKeyz.NDDTKey, LittleTimeTools.timeString(now));

			if(dataIsValide.get()&&!status[0].equals(StatusMGMT.mod))spawnFirstStep(pJson[0]);
			
			changeBDTBtn.setDisable(false);
			create.setDisable(false);
			cancel.setDisable(false);
			
			if(dataIsValide.get())stage.close();
		});
		
		changeBDTBtn.setOnAction((event)->
		{
			LocalDateTime ldt = Input.getDateTimeInput(time, changingBDTInputPhrase);
			bdt[0]= LittleTimeTools.timeString(ldt);
			bdtLbl.setText(bdtThenLblTxt + ": " + bdt[0]);
		});
		
		cancel.setOnAction((event)->
		{
			informBeholders(GTDCLI.newPrjctStgClsd);
			stage.close();
		});

		root.getChildren().addAll(nameBox, modBox, goalBox, bdtBox, deadLineBox, submitOrNotBox);

		stage.showAndWait();

		return pJson[0];
		*/
		
		return null;
	}
	
	public void spawnFirstStep(JSONObject pJson)
	{	
		
		System.out.println("Spawning first Step for Project: " + pJson.getString(ProjectJSONKeyz.nameKey));
		pJson = spawnStep(pJson, firstStepIndex);
	}

	public void appendStep(JSONObject pJson)
	{
		
		System.out.println("Appending Step to Project: " + pJson.getString(ProjectJSONKeyz.nameKey));
		
		JSONArray steps = (JSONArray) pJson.get(ProjectJSONKeyz.stepArrayKey);
		int length = steps.length();
		
		pJson = spawnStep(pJson, length);
	}
	
	private JSONObject spawnStep(JSONObject pJson, int index)
	{

		/*
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
		
		String differentBDT = Input.question.apply(differentBDTQstn);
		//?TODO:if(differentBDT.equals(Input.cancelAnswerToQuestion))//
		if(differentBDT.equals(Input.yesAnswerToQuestion))bdtOfStep = Input.getDateTimeInput(bdtInputTitle, bdtInputPhrase);
		else bdtOfStep = nddtOfStep;
		
		String stepStatus = "";
		String title = "";
		while(stepStatus.trim().equals("")) 
		{
			stepStatus = Input.getRangeInput(stepStartStatuses, title, stepStatusPhrase, statusRangeIsEditable);
		}
					
		String phrase;
		if(stepStatus.equals(StatusMGMT.waiting))phrase = waitingForPhrase;
		else phrase = stepDescPhrase;

		String descriptionOfStep = Input.getTextInput(descStepInputTitle, phrase, stepDescInTxtA);
		
		String prjctNDDT = pJson.getString(ProjectJSONKeyz.NDDTKey);
		String prjctDeadLine = pJson.getString(ProjectJSONKeyz.DLDTKey);
		if(index==firstStepIndex)Output.infoAlert("Deadline must be between StepNDDT: " + prjctNDDT
													+ " and Project Deadline: " + prjctDeadLine);
		else
		{
			String oldStepTDT = oldStep.getString(StepJSONKeyz.TDTKey);
			Output.infoAlert("Deadline must be between old-Step TDT: " + oldStepTDT
								+" and Project Deadline: " + prjctDeadLine);
		}
		
		LocalDateTime deadLineLDT = Input.getDateTimeInput(title, "Step DeadLine Please.");
		String deadLineStr = LittleTimeTools.timeString(deadLineLDT);
		
		newStep.put(StepJSONKeyz.DLDTKey, deadLineStr);
		newStep.put(StepJSONKeyz.statusKey, stepStatus);
		newStep.put(StepJSONKeyz.descKey, descriptionOfStep);
		newStep.put(StepJSONKeyz.NDDTKey, LittleTimeTools.timeString(nddtOfStep));
		newStep.put(StepJSONKeyz.BDTKey, LittleTimeTools.timeString(bdtOfStep));

		Supplier<Boolean> stepDataIsValide = ()->
		{
		
			String msg = stepIsOkToItsSelf(newStep);
			if(!msg.equals("OK"))
			{
				Output.errorAlert(msg);
				return false;
			}
			else Output.infoAlert(msg);

			msg = stepIsNotViolatingTimeframeOfProject(newStep, pJson);
			if(!msg.equals("OK"))
			{
				Output.errorAlert(msg);
				return false;
			}
			else Output.infoAlert(msg);

			if(index>firstStepIndex)
			{
				msg = stepIsNotViolatingTimeframeOfFormerStep(oldStep, newStep);
				if(!msg.equals("OK"))
				{
					Output.errorAlert(msg);
					return false;
				}
				else Output.infoAlert(msg);
			}
			
			return true;
		};
		
		if(stepDataIsValide.get())
		{
			pJson.put(ProjectJSONKeyz.statusKey, stepStatus);//this overwrites old status!
						
			steps.put(index, newStep);
			
			pJson.put(ProjectJSONKeyz.stepArrayKey, steps);
			
			return pJson;
		}
		else return spawnStep(pJson, index);
		*/
		
		return null;
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