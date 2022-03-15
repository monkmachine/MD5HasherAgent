package org.dacogb.MD5HasherAgent;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import com.ibi.config.ExitInfo;
import com.ibi.config.PropertyGroup;
import com.ibi.config.PropertyType;
import com.ibi.config.XDPropertyInfo;
import com.ibi.edaqm.XDAgent;
import com.ibi.edaqm.XDDocument;
import com.ibi.edaqm.XDErrorDocument;
import com.ibi.edaqm.XDException;
import com.ibi.edaqm.XDUtil;
import com.ibi.edaqm.XDVDesc;

import org.dacogb.MD5Hasher.MD5Hasher;

/**
 * This class is a custom agent for iWay Service Manager.
 * <P>
 * xx
 * <P>
 * Consult the iWay Service Manager Developer's Guide to learn more
 * about iWay Service Manager extensions.
 * <P>
 * iWay Software reserves the right to modify iWay Service Manager
 * as necessary without prior notification.
 * Any and all custom agents should be throughly tested when any
 * release/service pack or patch is applied to the Service Manager.
 * <P>
 * The developer of this custom agent assumes <b>all</b> responsibility
 * for maintenance of this agent. The developer also assumes all risks
 * associated with the use of said agent.
 * <br/>
 * Template Version: 8.0.6
 * @since 2022-03-11T17:41:10Z
 * @see com.ibi.common.IComponentManager#addExit(String, String)
 * @see com.ibi.common.IComponentManager#addLanguage(String)
 */
public final class MD5HasherAgent extends XDAgent
{
	// #JsonFileMergerAgent.InputFolder.label=;
	// #JsonFileMergerAgent.InputFolder.desc=;
	private static final XDPropertyInfo fileLocation = new XDPropertyInfo(
		"File Location",
		t("#FileLocation"),
		"",
		PropertyType.STRING,
		true,
		t("#Fully Qualified path of File")
	);

	// #JsonFileMergerAgent.OutputFolder.label=;
	// #JsonFileMergerAgent.OutputFolder.desc=;
	private static final XDPropertyInfo md5Algo = new XDPropertyInfo(
		"MD5Algo",
		t("#CheckSum Algorythym"),
		"",
		PropertyType.STRING,
		true,
		t("#Algorythm for the checksum")
	);

	// #JsonFileMergerAgent.ArrayName.label=;
	// #JsonFileMergerAgent.ArrayName.desc=;


	// register XDPropertyInfo with the correct group
	private static final PropertyGroup main = new PropertyGroup(
		t("#JSON File Merge"),
		new XDPropertyInfo[] {
				fileLocation,
				md5Algo,

		}
	);

	private static final PropertyGroup[] propertyGroups = {
		main,
	};
	
	// create the ExitInfo object with the group of parameters.
	private static final ExitInfo exitInfo = new ExitInfo(propertyGroups);

	/*
	 * Agent variable(s) will be loaded by the setupRuntime method.
	 */
	private String fileLocationValue;
	private String md5AlgoValue;


	protected HashMap<String, String> parmMap;

	/**
	 * Constructor for the JsonFileMergerAgent class.
	 */
	public MD5HasherAgent()
	{
		super(exitInfo);
		// your constructor code goes here
	}

	/* (non-Javadoc)
	 * @see com.ibi.edaqm.XDExitBase#getCategories()
	 */
	@Override
	public String getCategories()
	{
		// Add or remove group values as necessary
		return GROUP_MISC;
	}

	/* (non-Javadoc)
	 * @see com.ibi.edaqm.XDExitBase#getDesc()
	 */
	@Override
	public String getDesc()
	{
		// #JsonFileMergerAgent.desc=xx
		return t("#Merges json files in a folder");
	}

	/* (non-Javadoc)
	 * @see com.ibi.edaqm.XDExitBase#getLabel()
	 */
	@Override
	public String getLabel()
	{
		// #JsonFileMergerAgent.label=JsonFileMergerAgent
		return t("#JSON File Merger");
	}

	/**
	 * Provides metadata to the process flow designer as to which flow edges are expected to
	 * be followed.  If you plan to return fixed names, add them to the end of the array
	 * that this method returns. These names will appear in the designer as standard
	 * returns.
	 * @return an array of strings values (edges) that the agent may return.
	 */
	@Override
	public String[] getOPEdges()
	{
		return new String[] {
			EX_SUCCESS,
			EX_FAIL_PARSE,
		};
	}
	
	@Override
	public void init(String[] parms) throws XDException
	{
		parmMap = initParms();

		// your initialization code here
	}

	/**
	 * The difference between this method and the init method is that the init method runs
	 * when the agent is initialized. The setupRuntime method is called each time that the
	 * agent is invoked. Use the init method if the parameters do not need to be
	 * evaluated at runtime.
	 * @param tMap - variable map passed to the agent.
	 * @throws XDException if an error occurs in the getting of a Service Manager's variable
	 */
	private void setupRuntime(HashMap<String, String> tMap) throws XDException
	{
		// initialize the input parameters
		fileLocationValue = fileLocation.getString(tMap);
		md5AlgoValue = md5Algo.getString(tMap);

		// your runtime setup code here
	}

	/**
	 * Execution of the agent begins at the execute method.
	 * The input document and the output document are passed in.
	 *
	 * @param docIn  Input document.
	 * @param docOut Output document to create.
	 * @exception XDException
	 */
	@Override
	public String execute(XDDocument docIn, XDDocument docOut) throws XDException
	{
		// Evaluate the input parameters
		if (parmMap != null)
		{
			@SuppressWarnings("unchecked")
			HashMap<String, String> tMap = (HashMap<String, String>) parmMap.clone();
			try
			{
				XDUtil.evaluateWithException(tMap, docIn, getSRM(), logger);
			}
			catch (Exception e)
			{
				XDErrorDocument edoc = new XDErrorDocument(worker, docIn, XDErrorDocument.PIPE_AGENT, getNodeName(), tMap, e);
				edoc.moveTo(docOut);
				return EX_FAIL_PARSE;
			}
			
			// initialize the runtime variables
			setupRuntime(tMap);
		}

		MD5Hasher md5Hasher = new MD5Hasher();
		md5Hasher.setFile(fileLocationValue);
		try {
			md5Hasher.setMdigest(md5AlgoValue);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String checkSum = md5Hasher.executeMD5Hasher();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// sample for copy process only; remove this and insert your code here
		docIn.moveTo(docOut);
		return EX_SUCCESS;
	}
}
