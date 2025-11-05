package jborg.gtdForBash;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class StatusMGMT implements Serializable
{

	private static final long serialVersionUID = -7547462104106697126L;
	//Because of Serializable.^
	
	public static final String terminalSetName = "terminalSet";
	public static final String atStartSetName = "starterSet";
	public static final String onTheWaySetName = "onTheWay";
	
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

		Set<String> terminalSet = new HashSet<>();
		addNewStateSet(terminalSetName, terminalSet);
		
		addState(terminalSetName, success, true, true);
		addState(terminalSetName, failed, true, true);

		Set<String> startSet = new HashSet<>();
		addNewStateSet(atStartSetName, startSet);

		addState(atStartSetName, atbd, true, true);
		addState(atStartSetName, mod, true, false);//is not a Step Status.
		addState(atStartSetName, waiting, true, true);
		addState(atStartSetName, asap, true, true);
		
		Set<String> onTheWaySet = new HashSet<>();
		addNewStateSet(onTheWaySetName, onTheWaySet);

		addState(onTheWaySetName, atbd, true, true);
		addState(onTheWaySetName, mod, true, false);//is not a Step Status.
		addState(onTheWaySetName, waiting, true, true);
		addState(onTheWaySetName, needsNewStep, true, false);
		addState(onTheWaySetName, asap, true, true);
	}

	/*
	 * this is not allowed because every State should belong to at least 
	 * one stateSet. So u have to first install a new stateSet or use the
	 * other Method directly. allState Set is *not* included.
	public void addNewState(String name)
	{
		allStates.add(name);
	}
	*/

	public void addNewStateSet(String name, Set<String> stateSet)
	{
		stateSetMap.put(name, stateSet);
	}

	public void addState(String setName, String stateName, boolean isProjectStatus, boolean isStepStatus)
	{
		
		if(!stateSetMap.containsKey(setName))throw new IllegalArgumentException("Unknown State Set.");
		
		Set<String> set = stateSetMap.get(setName);
		set.add(stateName);
		stateSetMap.put(setName, set);
		
		if(isProjectStatus)belongsToProjects.add(stateName);
		if(isStepStatus)belongsToSteps.add(stateName);

		allStates.add(stateName);
	}

	public Set<String> getSetOfAllStates()
	{
		return allStates;
	}

	public Set<String> getNameOfAllSets()
	{
		return stateSetMap.keySet();
	}

	public Set<String> getStatesOfASet(String setName)
	{
		return stateSetMap.get(setName);
	}

	public boolean isKnownAsState(String stateName)
	{
		return allStates.contains(stateName);
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