package jborg.gtdForBash;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.function.Function;

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
	private static final Double newProjectsWeight = 12.0;
	private static final Double projectsSucceedWeight = 36.0;
	private static final Double projectsFailedWeight = -1.0;//per Day!
	private static final Double projectsWithDLDTAbuseWeight = -3.0;//per Day!

	private static final Double newStepsWeight = 6.0;
	private static final Double stepsSucceedWeight = 18.0;
	private static final Double stepsFailedWeight = -1.0;
	private static final Double stepsWithDLDTAbuseWeight = -2.0;
	
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
	
	public static final Function<TimeSpanData, Double> posiValue = (tsd)->
	{
		try
		{
			return new PositivityOfATSD(tsd).getValue();
		}
		catch (IOException | URISyntaxException | NaturalNumberException e)
		{
			e.printStackTrace();
		}

		throw new RuntimeException("What the Heck!");
	};

	public static final Function<TimeSpanData, Double> negPosiValue = (tsd)->
	{

		try
		{
			return -(new PositivityOfATSD(tsd).getValue());
		}
		catch (IOException | URISyntaxException | NaturalNumberException e)
		{
			e.printStackTrace();
		}

		throw new RuntimeException("What the Heck!");
	};

	
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

	public static Double getNewProjectsWeight()
	{
		return newProjectsWeight;
	}

	public static Double getProjectsSucceedWeight()
	{
		return projectsSucceedWeight;
	}

	public static Double getProjectsFailedWeight()
	{
		return projectsFailedWeight;
	}

	public static Double getProjectsWithDLDTAbuseWeight()
	{
		return projectsWithDLDTAbuseWeight;
	}

	public static Double getNewStepsWeight()
	{
		return newStepsWeight;
	}

	public static Double getStepsSucceedWeight()
	{
		return stepsSucceedWeight;
	}

	public static Double getStepsFailedWeight()
	{
		return stepsFailedWeight;
	}

	public static Double getStepsWithDLDTAbuseWeight()
	{
		return stepsWithDLDTAbuseWeight;
	}

	public Double getNewProjectsTerm()
	{
		return newProjectsTerm;
	}

	public Double getProjectsSucceedTerm()
	{
		return projectsSucceedTerm;
	}

	public Double getProjectsFailedTerm()
	{
		return projectsFailedTerm;
	}

	public Double getProjectsWithDLDTAbuseTerm()
	{
		return projectsWithDLDTAbuseTerm;
	}

	public Double getNewStepsTerm()
	{
		return newStepsTerm;
	}

	public Double getStepsSucceedTerm()
	{
		return stepsSucceedTerm;
	}

	public Double getStepsFaildTerm()
	{
		return stepsFaildTerm;
	}

	public Double getStepsWithDLDTAbuseTerm()
	{
		return stepsWithDLDTAbuseTerm;
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
		
		
		output += "New Projects Term: " + newProjectsTerm + "(" + newProjects + " Projects) * (" + newProjectsWeight + " per project)" + "\n"
				+ "Projects Succeed Term: " + projectsSucceedTerm + "(" + projectsSucceed + " Projects) * (" + projectsSucceedWeight + " per project)" + "\n"
				+ "Projects Failed Term(No DLDT Abuse): " + projectsFailedTerm + 
				"(" + projectsFailed + ", " + sumFailDays +" Days)\n"
				+ "Projects violate DLDT Term: " + projectsWithDLDTAbuseTerm + 
				"(" + projectsWithDLDTAbuse + ", " + sumDLDTAbuseFailDays + " Days)\n"
				+ "New Steps Term: " + newStepsTerm + "(" + newSteps + " Steps) * (" + newStepsWeight + " per Step)" + "\n"
				+ "Steps succeed Term: " + stepsSucceedTerm + "(" + stepsSucceed + " Steps) * (" + stepsSucceedWeight + " per Step)" + "\n"
				+ "Steps Failed Term(No DLDT Abuse): " + stepsFaildTerm + "(" + stepsFailed + " Steps) * ("+ stepsFailedWeight + " per Step)" + "\n"
				+ "Steps violate DLDT Term: " + stepsWithDLDTAbuseTerm + "(" + stepsWithDLDTAbuse + " Steps) * (" + stepsWithDLDTAbuseWeight + " per Step)" + "\n";

		return output;
	}
}