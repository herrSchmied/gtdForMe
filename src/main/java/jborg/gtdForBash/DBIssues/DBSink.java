package jborg.gtdForBash.DBIssues;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONObject;

import jborg.gtdForBash.GTDDataSpawnSession;
import jborg.gtdForBash.ProjectJSONKeyz;

public class DBSink 
{

	private static final String DB_URL = "jdbc:mysql://localhost/";
	private static final String USER = "jbJDBCAccess";
	private static final String PW = "Rooty#2020";
	private static final String DBName = "project";
	
	private static final String projectMainTbl = "projects";

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
}
