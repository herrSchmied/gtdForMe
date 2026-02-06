package jborg.gtdForBash;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class StatusMGMT implements Serializable
{

	private static final long serialVersionUID = -7547462104106697126L;
	//Because of Serializable.^
	
	public static final Set<String> terminalSet = new HashSet<>();
	public static final Set<String> atStartSet = new HashSet<>();
	public static final Set<String> stepStarterSet = new HashSet<>();
	public static final Set<String> onTheWaySet = new HashSet<>();
	public static final Set<String> onTheWayStepSet = new HashSet<>();
	
	public static final String asap = "As soon as possible";
	public static final String atbd = "About_to_be_Done!";
	public static final String mod = "Maybe_One_Day?";
	public static final String success = "Done!_successfully.";
	public static final String failed = "Done!_No_Success.";
	public static final String needsNewStep = "Needs new Step.";
	
	//Waiting for something.
	public static final String waiting = "Waiting...";

	private static final Set<String> allStates = new HashSet<>();

	private static final HashMap<String, Set<String>> stateSetMap = new HashMap<>();
	
	private static final HashSet<String> belongsToProjects = new HashSet<>();
	private static final HashSet<String> belongsToSteps = new HashSet<>();
	
	//Singleton Design Pattern.
	private static StatusMGMT instance = new StatusMGMT();
	//Singleton Design Pattern.
	public static StatusMGMT getInstance()
	{
		return instance;
	}
	//Singleton Design Pattern.
	private StatusMGMT()
	{		
		
		terminalSet.add(success);
		terminalSet.add(failed);


		atStartSet.add(atbd);
		atStartSet.add(mod);//is not a Step Status.
		atStartSet.add(waiting);
		atStartSet.add(asap);

		stepStarterSet.add(atbd);
		stepStarterSet.add(waiting);
		stepStarterSet.add(asap);

		onTheWaySet.add(atbd);
		onTheWaySet.add(mod);//is not a Step Status.
		onTheWaySet.add(waiting);
		onTheWaySet.add(needsNewStep);
		onTheWaySet.add(asap);
		
		onTheWayStepSet.add(atbd);
		onTheWayStepSet.add(waiting);
		onTheWayStepSet.add(needsNewStep);
		onTheWayStepSet.add(asap);

	}

	public Set<String> getSetOfAllStates()
	{
		return allStates;
	}

	public Set<String> getNameOfAllSets()
	{
		return stateSetMap.keySet();
	}

	public boolean isStepStatus(String stateName)
	{
		return belongsToSteps.contains(stateName);
	}

	public boolean isProjectStatus(String stateName)
	{
		return belongsToProjects.contains(stateName);
	}
}