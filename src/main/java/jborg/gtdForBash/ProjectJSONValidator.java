package jborg.gtdForBash;



import java.io.InputStream;


import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;

import org.json.JSONObject;
import org.json.JSONTokener;



public class ProjectJSONValidator
{

	private final Schema schema;

	public ProjectJSONValidator()
	{

		String path = "/projectJSONSchema.json";
		try(InputStream schemaStream = getClass().getResourceAsStream(path))
		{
	
			JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
			schema = SchemaLoader.load(rawSchema);
		}
	    catch(Exception e)
	    {
	    	throw new RuntimeException("Failed to load schema", e);
	    }
	 }

	public boolean validate(String jsonString)
	{

		JSONObject json = new JSONObject(jsonString);

		try
		{
			schema.validate(json);
			return true;
		}
		catch(ValidationException vexce)
		{
			return false;
		}
	}
}