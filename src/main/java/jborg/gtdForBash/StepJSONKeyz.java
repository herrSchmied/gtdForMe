package jborg.gtdForBash;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StepJSONKeyz 
{

	public static final String descKey = "Description_of_Step";
	public static final String statusKey ="Status_of_Step";
	public static final String ADTKey = "ADT_of_Step";		//Activated Date Time of Step.
	public static final String DLDTKey = "DLDT_of_Step";	//Deadline Date Time of Step. Old->"Step_Deadline";
	public static final String TDTKey = "TDT_of_Step";		//Termination Date Time of Step. Old->"StepTerminalDateTime";
	public static final String TDTNoteKey = "StepTDTNote";
	
	private static final Set<String> availableKeyz = new HashSet<>(Arrays.asList(
			descKey, statusKey, ADTKey, DLDTKey, TDTKey, TDTNoteKey));
	
	public static boolean isKnownKey(String jsonKey)
	{
		return availableKeyz.contains(jsonKey);
	}
}