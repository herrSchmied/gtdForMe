package jborg.gtdForBash;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.json.JSONObject;

import allgemein.ExactPeriode;
import someMath.NaturalNumberException;

import static jborg.gtdForBash.ProjectJSONKeyz.*;
import static jborg.gtdForBash.ProjectJSONToolBox.*;

public class PositivityOfATSD
{

	// #new Step					:	 6 Points.
	// #new Project					:	12 Points.
	// #Step success				:	18 Points.
	// #Project success				:	36 Points.
	// #Step failed					:	-1 Points.
	// #Step failed by DLDT abuse	:	-2 Points.
	// #Project failed				:	-1 Point per active Day. **Fail fast!!!
	// #Project failed by DLDT abuse:	-3 Point per active Day.

	private static final Double newStepFactor = 6.0;
	private static final Double newProjectFactor = 12.0;
	private static final Double stepSuccessFactor = 18.0;
	private static final Double projectSuccessFactor = 36.0;
	private static final Double stepFailedFactor = -1.0;
	private static final Double stepFailedByDLDTAbuseFactor = -2.0;
	private static final Double projectFailedFactor = -1.0;//per Day!
	private static final Double projectFailedByDLDTAbuseFactor = -3.0;//per Day!

	public static Double positivityIndexTimeSpan(TimeSpanData tsd) throws IOException, URISyntaxException, NaturalNumberException
	{
		Double sum = 0.0;
		
		Double stepsFailed = (double) tsd.howManyStepsFailedInThisTSD();
		Double stepsWithDLDTAbuse = (double) tsd.howManyStepsViolatedDLInThisTSD();

		sum += tsd.howManyNewStepsInThisTSD()*newStepFactor;
		sum += tsd.getProjectsWrittenDown().size()*newProjectFactor;
		sum += tsd.howManyStepsSucceededInThisTSD()*stepSuccessFactor;
		sum += tsd.projectsSucceededThisTimeSpan().size()*projectSuccessFactor;
		sum += (stepsFailed-stepsWithDLDTAbuse)*stepFailedFactor;
		sum += stepsWithDLDTAbuse*stepFailedByDLDTAbuseFactor;

		//Project failed by violating DLDT: -3 Point per active Day.
		for(String pName: tsd.projectsViolatedDLThisTimeSpan())
		{

			JSONObject pJSON = tsd.projectJSONObjByName(pName);
			
			LocalDateTime adt = extractLDT(pJSON, ADTKey);
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);
			
			ExactPeriode ep = new ExactPeriode(adt, tdt);
			
			sum += ep.getAbsoluteDays()*projectFailedByDLDTAbuseFactor;
		}

		//Projects that don't violated DLDT and still failed.
		//#Project failed: -1 Point per active Day.
		for(String pName: tsd.projectsFailedThisTimeSpan())
		{
			
			if(tsd.projectsViolatedDLThisTimeSpan().contains(pName))continue;

			JSONObject pJSON = tsd.projectJSONObjByName(pName);

			LocalDateTime adt = extractLDT(pJSON, ADTKey);
			LocalDateTime tdt = extractLDT(pJSON, TDTKey);

			ExactPeriode ep = new ExactPeriode(adt, tdt);

			sum += ep.getAbsoluteDays()*projectFailedFactor;
		}

		return sum;
	}
}