package org.dacogb.MD5HasherAgent;

import java.io.File;
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
import com.ibi.edaqm.XDNode;

import org.dacogb.MD5Hasher.MD5Hasher;

/**
 * This class is a custom agent for iWay Service Manager.
 * <P>
 * xx
 * <P>
 * Consult the iWay Service Manager Developer's Guide to learn more about iWay
 * Service Manager extensions.
 * <P>
 * iWay Software reserves the right to modify iWay Service Manager as necessary
 * without prior notification. Any and all custom agents should be throughly
 * tested when any release/service pack or patch is applied to the Service
 * Manager.
 * <P>
 * The developer of this custom agent assumes <b>all</b> responsibility for
 * maintenance of this agent. The developer also assumes all risks associated
 * with the use of said agent. <br/>
 * Template Version: 8.0.6
 * 
 * @since 2022-03-11T17:41:10Z
 * @see com.ibi.common.IComponentManager#addExit(String, String)
 * @see com.ibi.common.IComponentManager#addLanguage(String)
 */
public final class MD5HasherAgent extends XDAgent {

	private static final XDPropertyInfo fileLocation = new XDPropertyInfo("File Location", t("#FileLocation"), "",
			PropertyType.STRING, true, t("#Fully Qualified path of File"));


	private static final XDPropertyInfo md5Algo = new XDPropertyInfo("MD5Algo", t("#CheckSum Algorythym"), "",
			PropertyType.STRING, true, t("#Algorythm for the checksum"));


	// register XDPropertyInfo with the correct group
	private static final PropertyGroup main = new PropertyGroup(t("#CheckSum Agent"),
			new XDPropertyInfo[] { fileLocation, md5Algo,

			});

	private static final PropertyGroup[] propertyGroups = { main, };

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
	public MD5HasherAgent() {
		super(exitInfo);
		// your constructor code goes here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibi.edaqm.XDExitBase#getCategories()
	 */
	@Override
	public String getCategories() {
		// Add or remove group values as necessary
		return GROUP_MISC;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibi.edaqm.XDExitBase#getDesc()
	 */
	@Override
	public String getDesc() {

		return t("#Returns a Checksum and file size for a file");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibi.edaqm.XDExitBase#getLabel()
	 */
	@Override
	public String getLabel() {

		return t("#CheckSum Stream Agent");
	}

	/**
	 * Provides metadata to the process flow designer as to which flow edges are
	 * expected to be followed. If you plan to return fixed names, add them to the
	 * end of the array that this method returns. These names will appear in the
	 * designer as standard returns.
	 * 
	 * @return an array of strings values (edges) that the agent may return.
	 */
	@Override
	public String[] getOPEdges() {
		return new String[] { EX_SUCCESS, EX_FAIL_PARSE, EX_FAIL_FORMAT, EX_FAIL_OPERATION,

		};
	}

	@Override
	public void init(String[] parms) throws XDException {
		parmMap = initParms();

		// your initialization code here
	}

	/**
	 * The difference between this method and the init method is that the init
	 * method runs when the agent is initialized. The setupRuntime method is called
	 * each time that the agent is invoked. Use the init method if the parameters do
	 * not need to be evaluated at runtime.
	 * 
	 * @param tMap - variable map passed to the agent.
	 * @throws XDException if an error occurs in the getting of a Service Manager's
	 *                     variable
	 */
	private void setupRuntime(HashMap<String, String> tMap) throws XDException {
		// initialize the input parameters
		fileLocationValue = fileLocation.getString(tMap);
		md5AlgoValue = md5Algo.getString(tMap);

		// your runtime setup code here
	}

	/**
	 * Execution of the agent begins at the execute method. The input document and
	 * the output document are passed in.
	 *
	 * @param docIn  Input document.
	 * @param docOut Output document to create.
	 * @exception XDException
	 */
	@Override
	public String execute(XDDocument docIn, XDDocument docOut) throws XDException {
		// Evaluate the input parameters
		if (parmMap != null) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> tMap = (HashMap<String, String>) parmMap.clone();
			try {
				XDUtil.evaluateWithException(tMap, docIn, getSRM(), logger);
			} catch (Exception e) {
				XDErrorDocument edoc = new XDErrorDocument(worker, docIn, XDErrorDocument.PIPE_AGENT, getNodeName(),
						tMap, e);
				edoc.moveTo(docOut);
				return EX_FAIL_PARSE;
			}

			// initialize the runtime variables
			setupRuntime(tMap);
		}
		String checkSum;
		logger.debug("File Location is: " + fileLocationValue);
		logger.debug("Algothrym is: " + md5AlgoValue);
		XDNode resultsNode = new XDNode("results");
		docOut.setRoot(resultsNode);
		XDNode checkSumNode = new XDNode("checkSum");
		XDNode fileSizeNode = new XDNode("fileSize");
		XDNode errorNode = new XDNode("error");
		MD5Hasher md5Hasher = new MD5Hasher();
		md5Hasher.setFile(fileLocationValue);
		try {
			md5Hasher.setMdigest(md5AlgoValue);
		} catch (NoSuchAlgorithmException e) {

			logger.error("Error " + md5AlgoValue
					+ " is not in the list of supported alorythms which are: MD2, MD5, SHA-1, SHA-224,SHA-256, SHA-384, SHA-512/224, SHA-512/256, SHA3-224, SHA3-256, SHA3-384, SHA3-512");
			errorNode.setValue(e.toString());
			docOut.getRoot().setLastChild(errorNode);
			return EX_FAIL_FORMAT;
		}

		try {
			checkSum = md5Hasher.executeMD5Hasher();
		} catch (NoSuchAlgorithmException e) {
			logger.error("Error " + md5AlgoValue
					+ " is not in the list of supported alorythms which are: MD2, MD5, SHA-1, SHA-224,SHA-256, SHA-384, SHA-512/224, SHA-512/256, SHA3-224, SHA3-256, SHA3-384, SHA3-512");
			return EX_FAIL_FORMAT;
		} catch (IOException e) {
			logger.error("Error is :" + e.toString());
			errorNode.setValue(e.toString());
			docOut.getRoot().setLastChild(errorNode);
			return EX_FAIL_OPERATION;
		}
		File file = new File(fileLocationValue);
		String fileSize = String.valueOf(file.length());
		checkSumNode.setValue(checkSum);
		docOut.getRoot().setLastChild(checkSumNode);
		fileSizeNode.setValue(fileSize);
		docOut.getRoot().setLastChild(fileSizeNode);

		return EX_SUCCESS;
	}
}
