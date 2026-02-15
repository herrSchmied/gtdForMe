package jborg.gtdForBash;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.json.JSONObject;

import allgemein.ExactPeriode;
import allgemein.LittleTimeTools;
import someMath.NaturalNumberException;

import static jborg.gtdForBash.ProjectJSONKeyz.*;
import static jborg.gtdForBash.ProjectJSONToolBox.*;

public class PositivityOfATSD
{

	
	private final int newProjects;
	private final int projectsSucceed;
	private final int projectsFailed;
	private final int projectsWithDLDTAbuse;
	
	private final int newSteps;
	private final int stepsSucceed;
	private final int stepsFailed;
	private final int stepsWithDLDTAbuse;


	// #new Step					:	 6 Points.
	// #new Project					:	12 Points.
	// #Step success				:	18 Points.
	// #Project success				:	36 Points.
	// #Step failed					:	-1 Points.
	// #Step failed by DLDT abuse	:	-2 Points.
	// #Project failed				:	-1 Point per active Day. **Fail fast!!!
	// #Project failed by DLDT abuse:	-3 Point per active Day.
	private final Double newProjectsWeight = 12.0;
	private final Double projectsSucceedWeight = 36.0;
	private final Double projectsFailedWeight = -1.0;//per Day!
	private final Double projectsWithDLDTAbuseWeight = -3.0;//per Day!

	private final Double newStepsWeight = 6.0;
	private final Double stepsSucceedWeight = 18.0;
	private final Double stepsFailedWeight = -1.0;
	private final Double stepsWithDLDTAbuseWeight = -2.0;
	
	private final Double newProjectsTerm;
	private final Double projectsSucceedTerm;
	private final Double projectsFailedTerm;
	private final Double sumFailDays;
	private final Double projectsWithDLDTAbuseTerm;
	private final Double sumDLDTAbuseFailDays;
	
	private final Double newStepsTerm;
	private final Double stepsSucceedTerm;
	private final Double stepsFaildTerm;
	private final Double stepsWithDLDTAbuseTerm;
	
	private final Double overAllValue;
	private final TimeSpanData tsd;
	
	public PositivityOfATSD(TimeSpanData tsd) throws IOException, URISyntaxException, NaturalNumberException
	{
		
		this.tsd = tsd;
		
		this.newProjects = tsd.getProjectsWrittenDown().size();
		this.newProjectsTerm = newProjects *newProjectsWeight;
		this.projectsSucceed = tsd.howManyProjectsSuceeded();
		this.projectsSucceedTerm = projectsSucceed*projectsSucceedWeight;

		this.newSteps = tsd.howManyNewStepsInThisTSD();
		this.newStepsTerm = newSteps*newStepsWeight;
		this.stepsSucceed = tsd.howManyStepsSucceededInThisTSD();
		this.stepsSucceedTerm = stepsSucceed*stepsSucceedWeight;

		this.stepsWithDLDTAbuse = tsd.howManyStepsViolatedDLInThisTSD();
		this.stepsFailed = tsd.howManyStepsFailedInThisTSD()-stepsWithDLDTAbuse;

		this.stepsFaildTerm = stepsFailed*stepsFailedWeight;
		this.stepsWithDLDTAbuseTerm = stepsWithDLDTAbuse*stepsWithDLDTAbuseWeight;

		//Project failed by violating DLDT: -3 Point per active Day.
		Double sum = 0.0;
		Double sumDaysDLDTAbuse = 0.0;
		this.projectsWithDLDTAbuse = tsd.projectsViolatedDLThisTimeSpan().size();
		for(String pName: tsd.projectsViolatedDLThisTimeSpan())
		{

			JSONObject pJSON = tsd.projectJSONObjByName(pName);
			
			LocalDateTime adt = extractLDT(pJSON, ADTKey);
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);
			
			ExactPeriode ep = new ExactPeriode(adt, tdt);
			
			sum += ep.getAbsoluteDays()*projectsWithDLDTAbuseWeight;
			sumDaysDLDTAbuse += ep.getAbsoluteDays();
		}
		this.projectsWithDLDTAbuseTerm = sum;
		this.sumDLDTAbuseFailDays = sumDaysDLDTAbuse;

		//Projects that don't violated DLDT and still failed.
		//#Project failed: -1 Point per active Day.
		sum = 0.0;
		Double sumDaysFailed = 0.0;
		this.projectsFailed = tsd.projectsFailedThisTimeSpan().size()-projectsWithDLDTAbuse;
		for(String pName: tsd.projectsFailedThisTimeSpan())
		{
			
			if(tsd.projectsViolatedDLThisTimeSpan().contains(pName))continue;

			JSONObject pJSON = tsd.projectJSONObjByName(pName);

			LocalDateTime adt = extractLDT(pJSON, ADTKey);
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);

			ExactPeriode ep = new ExactPeriode(adt, tdt);

			sum += ep.getAbsoluteDays()*projectsFailedWeight;
			sumDaysFailed += ep.getAbsoluteDays();
		}
		this.projectsFailedTerm = sum;
		this.sumFailDays = sumDaysFailed;
		
		overAllValue = newProjectsTerm
						+ projectsSucceedTerm
						+ projectsFailedTerm
						+ projectsWithDLDTAbuseTerm
						+ newStepsTerm
						+ stepsSucceedTerm
						+ stepsFaildTerm
						+ stepsWithDLDTAbuseTerm;

	}

	public Double getValue()
	{
		return overAllValue;
	}

	public String toString()
	{
		String output = "";
		String beginStr = LittleTimeTools.timeString(tsd.getBegin());
		String endStr = LittleTimeTools.timeString(tsd.getEnd());
		
		output += "Positivity of a TSD:" + "\n";
		output += "Begin: " + beginStr +"   End: " + endStr + "\n";
		output += "TSD Nr.: " + tsd.getTimeNr() + "\n";
		output += "Positivity: " + overAllValue + "\n";
		
		
		output += "New Projects Term: " + newProjectsTerm + "(" + newProjects + ")" + "\n"
				+ "Projects Succeed Term: " + projectsSucceedTerm + "(" + projectsSucceed + ")" + "\n"
				+ "Projects Failed Term(No DLDT Abuse): " + projectsFailedTerm + 
				"(" + projectsFailed + ", " + sumFailDays +" Days)\n"
				+ "Projects violate DLDT Term: " + projectsWithDLDTAbuseTerm + 
				"(" + projectsWithDLDTAbuse + ", " + sumDLDTAbuseFailDays + " Days)\n"
				+ "New Steps Term: " + newStepsTerm + "(" + newSteps + ")"+"\n"
				+ "Steps succeed Term: " + stepsSucceedTerm + "(" + stepsSucceed + ")" + "\n"
				+ "Steps Failed Term(No DLDT Abuse): " + stepsFaildTerm + "(" + stepsFailed + ")"+ "\n"
				+ "Steps violate DLDT Term: " + stepsWithDLDTAbuseTerm + "(" + stepsWithDLDTAbuse + ")"+ "\n";

		return output;
	}
}