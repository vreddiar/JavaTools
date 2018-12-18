/***
 * This is an FTPSClient Utility class that provides convenient functions to use Apache FTPSClient
***/

package ftps.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

/**
 * @author Vijay Reddiar
 *
 */
public final class FTPSUtil
{

    private String server; 
    private String userName; 
    private String password;
    private boolean isBinaryTransfer; 
    private String dataChannelProtectionLevel;
    private FTPSClient ftpsClient = null;

    
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
    	try{
        	if(this.ftpsClient !=null && this.ftpsClient.isConnected()){
    			ftpsClient.disconnect();
        	}
    	}
    	catch (IOException e)
    	{
    		//Ignore any error
    	}
    	super.finalize();
    }
    
	/**
	 * @param server
	 * @param userName
	 * @param password
	 */
	public FTPSUtil(
			String server,
			int port,
			String userName, 
			String password) {
		this.initiaize(server, port, userName, password, "SSL", "P", true);
	}    
   
	/**
	 * @param server
	 * @param userName
	 * @param password
	 * @param protocol
	 * @param dataChannelProtectionLevel
	 * @param isBinaryTransfer
	 */
	public FTPSUtil(
			String server, 
			int port,
			String userName, 
			String password, 
			String protocol,
			String dataChannelProtectionLevel,
			boolean isBinaryTransfer) {
		this.initiaize(server, port, userName, password, protocol, dataChannelProtectionLevel, isBinaryTransfer);
	}
	
	/**
	 * @param inputLocation
	 * @param remoteLocation
	 */
	public void storeFile(String inputLocation, String remoteFolder, String remoteFileName){
        try {
            if (loginToFTPSServer())
            {
                InputStream input = new FileInputStream(inputLocation);
                System.out.println(inputLocation);
                System.out.println(remoteFolder);
                System.out.println(this.ftpsClient.getStatus(remoteFolder));
                System.out.println(this.ftpsClient.cwd(remoteFolder));
                System.out.println("After changing to remote folder ");
                System.out.println(this.ftpsClient.getStatus("."));
                System.out.println(this.ftpsClient.listNames());
                System.out.println(this.ftpsClient.getStatus());
                System.out.println(this.ftpsClient.getReplyString());
                System.out.println();
                System.out.println(this.ftpsClient.storeFile(remoteFileName, input));
                System.out.println(this.ftpsClient.getStatus());
                System.out.println(this.ftpsClient.getReplyCode());
                System.out.println(this.ftpsClient.getStatus(remoteFolder));
            }
            else {
                System.err.println("storeFile: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("storeFile: Could not connect to server.");
			e.printStackTrace();
		}
	}

	/**
	 * @param inputStream
	 * @param remoteLocation
	 */
	public void storeFile(InputStream inputStream, String remoteFolder, String remoteFileName){
        try {
            if (loginToFTPSServer())
            {
                this.ftpsClient.cwd(remoteFolder);
                this.ftpsClient.storeFile(remoteFileName, inputStream);
            }
            else {
                System.err.println("storeFile: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("storeFile: Could not connect to server.");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param inputLocations
	 * @param remoteLocations
	 */
	public void storeFiles(String[] inputLocations, String remoteFolder, String[] remoteFileNames){
        try {
            if (loginToFTPSServer())
            {
            	this.ftpsClient.cwd(remoteFolder);
            	for (int i=0; i < inputLocations.length && i < remoteFileNames.length; i++){
                    InputStream input = new FileInputStream(inputLocations[i]);
                    this.ftpsClient.storeFile(remoteFileNames[i], input);
                    input.close();
            	}
            }
            else {
                System.err.println("storeFiles: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("storeFiles: Could not connect to server.");
			e.printStackTrace();
		}
	}

	/**
	 * @param inputFolder
	 * @param remoteFolder
	 */
	public void storeAllFiles(String inputFolder, String remoteFolder){
		this.storeAllFiles(inputFolder, remoteFolder, ".+");
	}

	
	/**
	 * @param inputFolder
	 * @param remoteFolder
	 * @param regPattern
	 */
	public void storeAllFiles(String inputFolder, String remoteFolder, String regPattern){
        try {
            if (loginToFTPSServer())
            {
            	File[] files = new java.io.File(inputFolder).listFiles(this.getFileFilter(regPattern));
            	this.ftpsClient.cwd(remoteFolder);
                for (File file : files){
                	if (file.isFile()){
                    	String inputLocation = inputFolder + "/" + file.getName();
                        InputStream input = new FileInputStream(inputLocation);
                        this.ftpsClient.storeFile(file.getName(), input);
                        input.close();
                	}
                }
            }
            else {
                System.err.println("storeAllFiles: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("storeAllFiles: Could not connect to server.");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param outputLocation
	 * @param remoteLocation
	 */
	public void retrieveFile(String outputLocation, String remoteLocation){
		this.retrieveFiles(new String[] {outputLocation}, new String[] {remoteLocation});
	}

	
	/**
	 * @param outputLocations
	 * @param remoteLocations
	 */
	public void retrieveFiles(String[] outputLocations, String[] remoteLocations){
        try {
            if (loginToFTPSServer())
            {
            	for (int i=0; i < outputLocations.length && i < remoteLocations.length; i++){
                    OutputStream output = new FileOutputStream(outputLocations[i]);
                    this.ftpsClient.retrieveFile(remoteLocations[i], output);
                    output.close();
            	}
            }
            else {
                System.err.println("retrieveFiles: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("retrieveFiles: Could not connect to server.");
			e.printStackTrace();
		}
	}
	
	/**
	 * @param outputFolder
	 * @param remoteFolder
	 */
	public void retrieveAllFiles(String outputFolder, String remoteFolder){
		this.retrieveAllFiles(outputFolder, remoteFolder, ".+");
	}
	
	
	public void retrieveAllFiles(String outputFolder, String remoteFolder, String regPattern){
		try{
            if (loginToFTPSServer())
            {
                FTPFile[] files = this.ftpsClient.listFiles(remoteFolder, this.getFTPFileFilter(regPattern));
                for (FTPFile file : files){
                	String outputLocation = outputFolder + "/" + file.getName();
                	if (file.isFile()){
                        OutputStream output = new FileOutputStream(outputLocation);
                        this.ftpsClient.retrieveFile(remoteFolder + "/" + file.getName(), output);
                        output.close();
                	}
                }
            }
            else {
                System.err.println("retrieveAllFiles: Could not login to server.");
            }
		}
		catch (IOException e){
            System.err.println("retrieveAllFiles: Could not connect to server.");
			e.printStackTrace();
		}
	}

	/**
	 * @param remoteSourceLocation
	 * @param remoteDestinationLocation
	 */
	public void moveRemoteFile(String remoteSourceLocation, String remoteDestinationLocation){
        try {
            if (loginToFTPSServer())
            {
           		this.ftpsClient.rename(remoteSourceLocation, remoteDestinationLocation);
            }
            else {
                System.err.println("moveRemoteFile: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("moveRemoteFile: Could not connect to server.");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param remoteSourceFolder
	 * @param remoteDestinationFolder
	 */
	public void moveRemoteFiles(String remoteSourceFolder, String remoteDestinationFolder){
        try {
            if (loginToFTPSServer())
            {
                FTPFile[] files = this.ftpsClient.listFiles(remoteSourceFolder);
                for (FTPFile file : files){
                	if (file.isFile()){
                   		this.ftpsClient.rename(remoteSourceFolder + "/" + file.getName(), remoteDestinationFolder + "/" + file.getName());
                	}
                }
            }
            else {
                System.err.println("moveRemoteFiles: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("moveRemoteFiles: Could not connect to server.");
			e.printStackTrace();
		}
	}
	
	/**
	 * @param remoteFileLocation
	 */
	public void deleteRemoteFile(String remoteFileLocation){
        try {
            if (loginToFTPSServer())
            {
            	this.ftpsClient.deleteFile(remoteFileLocation);
            }
            else {
                System.err.println("deleteRemoteFile: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("deleteRemoteFile: Could not connect to server.");
			e.printStackTrace();
		}
	}

	
	/**
	 * @param remoteFolder
	 */
	public void deleteRemoteFiles(String remoteFolder){
        try {
            if (loginToFTPSServer())
            {
                FTPFile[] files = this.ftpsClient.listFiles(remoteFolder);
                for (FTPFile file : files){
                	if (file.isFile()){
                		this.ftpsClient.deleteFile(remoteFolder + "/" + file.getName());
                	}
                }
            }
            else {
                System.err.println("deleteRemoteFiles: Could not login to server.");
            }
		} catch (IOException e) {
            System.err.println("deleteRemoteFiles: Could not connect to server.");
			e.printStackTrace();
		}
	}
	
	/**
	 * @param server
	 * @param userName
	 * @param password
	 * @param protocol
	 * @param dataChannelProtectionLevel
	 * @param isBinaryTransfer
	 */
	private void initiaize(
			String server, 
			int port,
			String userName, 
			String password, 
			String protocol,
			String dataChannelProtectionLevel,
			boolean isBinaryTransfer) {
		this.server = server;
		this.userName = userName;
		this.password = password;
		this.dataChannelProtectionLevel = dataChannelProtectionLevel;
		this.isBinaryTransfer = isBinaryTransfer;
		try {
			this.ftpsClient = new FTPSClient(protocol);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ftpsClient.setDefaultPort(port);
	}    
	
	/**
	 * @return
	 * @throws IOException
	 */
	private boolean loginToFTPSServer() throws IOException{
		boolean loginStatus = true;
		if (false == this.ftpsClient.isConnected()){
			this.ftpsClient.connect(server);
	        loginStatus = this.ftpsClient.login(this.userName, this.password);
	        System.out.println("loginStatus = " + loginStatus);
	        System.out.println("Connected to " + server + ".");
		}
		
        // After connection attempt, you should check the reply code to verify
        // success.
        int reply = this.ftpsClient.getReplyCode();
        
        if (this.dataChannelProtectionLevel != null){
        	this.ftpsClient.execPROT(this.dataChannelProtectionLevel);
        }
        
        if (false == FTPReply.isPositiveCompletion(reply))
        {
            this.ftpsClient.disconnect();
            System.err.println("FTP server refused connection.");
            loginStatus = false;
        }
        else{
	        if (this.isBinaryTransfer){
	        	this.ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
	        }
	        // Use passive mode as default because most of us are
	        // behind firewalls these days.
	        this.ftpsClient.enterLocalPassiveMode();
	        
	        
	        this.ftpsClient.setBufferSize(1024);
        }
		return loginStatus;
	}
	
	private FTPFileFilter getFTPFileFilter(String regPattern){
		return new FTPFileFilter() {
			private Pattern filePattern = Pattern.compile(regPattern); 
			public boolean accept(FTPFile file){
				return filePattern.matcher(file.getName()).matches();
			}
		};
	}

	private FileFilter getFileFilter(String regPattern){
		return new FileFilter() {
			private Pattern filePattern = Pattern.compile(regPattern); 
			public boolean accept(File file){
				return filePattern.matcher(file.getName()).matches();
			}
		};
	}
	
}
