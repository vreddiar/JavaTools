/*
        Create Zip File From Directory using ZipOutputStream Example
        This Java example shows how create zip file from directory
        using Java ZipOutputStream class.
*/
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
 
 
public class CreateZipFileDirectory {
       
        public static void main(String args[])
        {                
                try
                {
                        String zipFile = "C:/Users/vredd/Work/test-zip/destination/zipdemo.zip";
                        String sourceDirectory = "C:/Users/vredd/Work/test-zip/source";
                       
                        //create byte buffer
                        byte[] buffer = new byte[1024];
                        /*
                         * To create a zip file, use
                         *
                         * ZipOutputStream(OutputStream out)
                         * constructor of ZipOutputStream class.
                         *  
                         */
                         
                         //create object of FileOutputStream
                         FileOutputStream fout = new FileOutputStream(zipFile);
                         
                         //create object of ZipOutputStream from FileOutputStream
                         ZipOutputStream zout = new ZipOutputStream(fout);
                         
                         //create File object from directory name
                         File dir = new File(sourceDirectory);
                         
                         //check to see if this directory exists
                         if(!dir.isDirectory())
                         {
                                System.out.println(sourceDirectory + " is not a directory");
                         }
                         else
                         {
                                File[] files = dir.listFiles();
                               
                                for(int i=0; i < files.length ; i++)
                                {
                                        System.out.println("Adding " + files[i].getName());
                                       
                                        //create object of FileInputStream for source file
                                        FileInputStream fin = new FileInputStream(files[i]);
                         
                                        /*
                                         * To begin writing ZipEntry in the zip file, use
                                         *
                                         * void putNextEntry(ZipEntry entry)
                                         * method of ZipOutputStream class.
                                         *
                                         * This method begins writing a new Zip entry to
                                         * the zip file and positions the stream to the start
                                         * of the entry data.
                                         */
                         
                                        zout.putNextEntry(new ZipEntry(files[i].getName()));
                         
                                        /*
                                         * After creating entry in the zip file, actually
                                         * write the file.
                                         */
                                        int length;
                         
                                        while((length = fin.read(buffer)) > 0)
                                        {
                                           zout.write(buffer, 0, length);
                                        }
                         
                                        /*
                                         * After writing the file to ZipOutputStream, use
                                         *
                                         * void closeEntry() method of ZipOutputStream class to
                                         * close the current entry and position the stream to
                                         * write the next entry.
                                         */
                         
                                         zout.closeEntry();
                         
                                         //close the InputStream
                                         fin.close();
                                }
                         }
                 
                          //close the ZipOutputStream
                          zout.close();
                         
                          System.out.println("Zip file has been created!");
               
                }
                catch(IOException ioe)
                {
                        System.out.println("IOException :" + ioe);
                }
               
        }
}