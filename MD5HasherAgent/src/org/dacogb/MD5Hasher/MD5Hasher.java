package org.dacogb.MD5Hasher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
 
public class MD5Hasher {
 
	private File file;
   private MessageDigest mdigest;
    public  String executeMD5Hasher()
        throws IOException, NoSuchAlgorithmException
    {
    	if(mdigest == null){
			

				this.mdigest = MessageDigest.getInstance("MD5");



		}
        // Get the checksum
        String checksum = checksum(mdigest, file);
        return checksum;
    }
 
    // this method return the complete  hash of the file
    // passed
    private static String checksum(MessageDigest digest,
                                   File file)
        throws IOException
    {
        // Get file input stream for reading the file
        // content
        FileInputStream fis = new FileInputStream(file);
 
        // Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
 
        // read the data from file and update that data in
        // the message digest
        while ((bytesCount = fis.read(byteArray)) != -1)
        {
            digest.update(byteArray, 0, bytesCount);
        };
 
        // close the input stream
        fis.close();
 
        // store the bytes returned by the digest() method
        byte[] bytes = digest.digest();
 
        // this array of bytes has bytes in decimal format
        // so we need to convert it into hexadecimal format
 
        // for this we create an object of StringBuilder
        // since it allows us to update the string i.e. its
        // mutable
        StringBuilder sb = new StringBuilder();
       
        // loop through the bytes array
        for (int i = 0; i < bytes.length; i++) {
           
            // the following line converts the decimal into
            // hexadecimal format and appends that to the
            // StringBuilder object
            sb.append(Integer
                    .toString((bytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
 
        // finally we return the complete hash
        return sb.toString();
    }

	public void setFile(String file) {
		this.file = new File(file);;
	}

	public void setMdigest(String mdigest) throws NoSuchAlgorithmException {
		

			this.mdigest = MessageDigest.getInstance(mdigest);

	}
}