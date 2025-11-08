package jborg.gtdForBash;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProjectJSONKeyz
{

	public static final String goalKey = "Goal";
	public static final String noteArrayKey = "NoteArray";
	public static final String stepArrayKey = "Steps";
	
	public static final String nameKey = "Project_Name";
	public static final String statusKey = "Project_Status";
	
	public static final String NDTKey = "NDT_of_Project";				//Noted Date Time.
	public static final String ADTKey = "ADT_of_Project";				//Activate Date Time.
	public static final String DLDTKey = "DLDT_of_Project";				//Deadline Date Time.
	public static final String TDTKey = "TDT_of_Project";				//Termination Date Time.

	public static final String TDTNoteKey = "ProjectTDTNote";

	private static final Set<String> availableKeyz = new HashSet<>(Arrays.asList(goalKey,
			noteArrayKey, stepArrayKey, nameKey, statusKey, NDTKey, ADTKey, DLDTKey,
			TDTKey, TDTNoteKey));
	
	public static boolean isKnownKey(String jsonKey)
	{
		return availableKeyz.contains(jsonKey);
	}
}
