package jborg.gtdForBash;

import static jborg.gtdForBash.SequenzesForISS.sequenzManyProjects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import consoleTools.InputStreamSession;
import someMath.NaturalNumberException;

public class ProjectSetForTesting
{

	public static Set<JSONObject> get() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		String data = sequenzManyProjects(LocalDateTime.now().minusDays(14));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        new GTDCLI(iss);
        Set<JSONObject> prjctSet = GTDCLI.loadProjects();
        
        return prjctSet;
	}
}
