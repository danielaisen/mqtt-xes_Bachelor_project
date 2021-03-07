//package requestRespond;
//
//import java.io.*;
//
///**
// * @author Daniel Max Aisen (s171206)
// **/
//
//public class WriteAndRead {
//
//
//    static void readingAndWritingUsingBuffers(File inputFile, File outPutFile) {
////        method 2: BufferedReader
//        BufferedReader bufferedReader = null;
//        BufferedWriter bufferedWriter = null;
//        try {
//            bufferedReader = new BufferedReader(new FileReader(inputFile));
//            bufferedWriter = new BufferedWriter(new FileWriter(outPutFile));
//
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                System.out.println(line);
//                bufferedWriter.write(line);
//                bufferedWriter.newLine();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                bufferedReader.close();
//                bufferedWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    static void readingAndWritingUsingChar(File inputFile, File outPutFile) {
//        //method 1 : fileReader / File writer
//        FileReader fr = null;
//        FileWriter fw = null;
//        try {
//            fr = new FileReader(inputFile);
//            fw = new FileWriter(outPutFile);
//            int i;
//            while ((i = fr.read()) != -1) {
//                System.out.print((char) i);
//                fw.write((char) i);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fr.close();
//                fw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }
//
//
//
//
//
//}
