package jborg.gtdForBash.DBIssues;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import jborg.gtdForBash.GTDDataSpawnSession;
import jborg.gtdForBash.ProjectJSONKeyz;
import jborg.gtdForBash.StepJSONKeyz;

public class DBSink 
{

	private static final String DB_URL = "jdbc:mysql://localhost/";
	private static final String USER = "jbJDBCAccess";
	private static final String PW = "Rooty#2020";
	private static final String DBName = "project";
	
	private static final String projectMainTbl = "projects";
	private static final String stepsTbl = "steps";

	private final Connection conn;

	private Connection establishConnection(String DB_URL, String USER, String PW, String DBName) throws SQLException
	{
		// Open a connection
		Connection conn = DriverManager.getConnection(DB_URL, USER, PW);		
		
		return conn;
	}

	public DBSink()throws SQLException
	{
		this.conn = establishConnection(DB_URL, USER, PW, DBName);
	}

	private void useDB() throws SQLException
	{

		Statement stmt = conn.createStatement();
		stmt.execute("USE " + DBName + ";");
	}

	public void save(JSONObject pJSON)throws SQLException
	{

		useDB();

		String name = pJSON.getString(ProjectJSONKeyz.nameKey);
		String status = pJSON.getString(ProjectJSONKeyz.statusKey);
		String goal = pJSON.getString(ProjectJSONKeyz.goalKey);
		String nddtStr = pJSON.getString(ProjectJSONKeyz.NDDTKey);
		String bdtStr = pJSON.getString(ProjectJSONKeyz.BDTKey);
		
		String dldtStr = GTDDataSpawnSession.deadLineUnknownStr;
		if(pJSON.has(ProjectJSONKeyz.DLDTKey))dldtStr = pJSON.getString(ProjectJSONKeyz.DLDTKey);

		String tdtStr="";
		if(pJSON.has(ProjectJSONKeyz.TDTKey))tdtStr = pJSON.getString(ProjectJSONKeyz.TDTKey);
		
		
		String SQL = "INSERT INTO " + projectMainTbl + 
					"( name, status, goal, nddt, bdt, dldt, tdt)" +
					" VALUES(?,?,?,?,?,?,?); ";
		System.out.println(SQL);

		
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		
		pStmt.setString(1, name);
		pStmt.setString(2, status);
		pStmt.setString(3, goal);
		pStmt.setString(4, nddtStr);
		pStmt.setString(5, bdtStr);
		pStmt.setString(6, dldtStr);
		pStmt.setString(7, tdtStr);
		
		pStmt.addBatch();
		
		pStmt.executeBatch();
		
	}

	public boolean validatePJSON(JSONObject pJSON)
	{
		return false;
	}
	
	public void saveStepsOfSet(Set<JSONObject> projects) throws SQLException
	{
		for(JSONObject pJSON: projects)
		{
			saveStepsOfJSONObject(pJSON);
		}
	}
	
	public void saveStepsOfJSONObject(JSONObject pJSON) throws SQLException
	{
		
		String project_Name = pJSON.getString(ProjectJSONKeyz.nameKey);
		int project_ID = getProjectIDByName(project_Name);
		JSONArray steps = pJSON.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		
		for(int n=0;n< steps.length();n++)
		{
			JSONObject step = steps.getJSONObject(n);
			String step_Status = step.getString(StepJSONKeyz.statusKey);
			String step_nddt = step.getString(StepJSONKeyz.NDDTKey);
			String step_bdt = step.getString(StepJSONKeyz.BDTKey);
			String step_desc = step.getString(StepJSONKeyz.descKey);
			
			String step_dldt=null;
			if(step.has(StepJSONKeyz.DLDTKey))
			{
				step_dldt = step.getString(StepJSONKeyz.DLDTKey);
			}

			String step_tdt=null;
			if(step.has(StepJSONKeyz.TDTKey))
			{
				step_tdt = step.getString(StepJSONKeyz.TDTKey);
			}

			String SQL = "INSERT INTO " + stepsTbl + 
					"( project_ID, status, description, nddt, bdt, dldt, tdt)" +
					" VALUES(?,?,?,?,?,?,?); ";

			PreparedStatement pStmt = conn.prepareStatement(SQL);
			pStmt.setInt(1, project_ID);
			pStmt.setString(2, step_Status);
			pStmt.setString(3, step_desc);
			pStmt.setString(4, step_nddt);
			pStmt.setString(5, step_bdt);
			pStmt.setString(6, step_dldt);
			pStmt.setString(7, step_tdt);
			
			pStmt.addBatch();
			pStmt.executeBatch();
		}
	}
	
	public int getProjectIDByName(String name) throws SQLException
	{
		
		useDB();
		
		String SQL = "SELECT id FROM projects WHERE name=?";
		PreparedStatement pStmt = conn.prepareStatement(SQL);
		pStmt.setString(1, name);
		
		ResultSet rs = pStmt.executeQuery();
		if(rs.getFetchSize()>1)throw new IllegalArgumentException("More than one Project with that Name");
		else
		{
			rs.next();
			return rs.getInt(1);
		}

	}
}
