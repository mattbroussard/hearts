/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Not an assignment.
 ***   Assignment URL: N/A
 ***   Date: October 7, 2010
 ***   Revision: 0.2
 ***/

import java.io.*;

//An input/output utility class to make my life easier in the future.
public class IO {
	
	InputStream inp = null;
	OutputStream out = null;
	BufferedReader br = null;
	PrintStream ps = null;
	String name = "";
	
	//creates an IO instance to read from a file
	public static IO fileInput(File f, String nm) {
	
		if (!f.exists()) return null;
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(f);
		} catch (Exception e) {
			return null;
		}
		
		return new IO(fis, nm);
	
	}
	
	//creates an IO instance to write to a file
	public static IO fileOutput(File f, String nm) {
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(f);
		} catch (Exception e) {
			return null;
		}
		
		return new IO(fos, nm);
	
	}
	
	//shortcut methods so the name can be omitted
	public static IO fileInput(File f) { return fileInput(f, ""); }
	public static IO fileOutput(File f) { return fileOutput(f, ""); }
	
	//no more static methods beyond this point
	
	//creates an IO instance for use with input
	public IO(InputStream is, String nm) {
	
		inp = is;
		br = new BufferedReader(new InputStreamReader(is));
		name = nm;
	
	}
	
	//creates an IO instance for use with output
	public IO(OutputStream os, String nm) {
	
		out = os;
		if (out instanceof PrintStream) ps = (PrintStream)out;
		else ps = new PrintStream(out);
		name = nm;
	
	}
	
	//shortcuts so the name can be omitted
	public IO(OutputStream os) { this(os, ""); }
	public IO(InputStream is) { this(is, ""); }
	
	//create an IO instance for use with stdin and stdout
	public IO() {
	
		inp = System.in;
		br = new BufferedReader(new InputStreamReader(System.in));
		out = System.out;
		ps = System.out;
		name = "stdio";
	
	}
	
	//methods to retrieve the underlying objects in order to bypass the IO wrapper for some operation.
	public InputStream getInputStream() { return inp; }
	public OutputStream getOutputStream() { return out; }
	public BufferedReader getBufferedReader() { return br; }
	public PrintStream getPrintStream() { return ps; }
	
	//gets and sets the name. The name is used to specify what input/output stream in error messages.
	public void setName(String s) { name = s; }
	public String getName() { return name; }
	
	//prints a line to the output stream. Appends a linebreak if println==true
	public void p(String str, boolean println) {
	
		if (out==null) return;
		
		if (println) { ps.println(str); return; }
		ps.print(str);
	
	}
	
	//shortcut methods for p(String, boolean) so the boolean can be omitted.
	public void pl(String str) { p(str, true); }
	public void p(String str) { p(str, false); }
	
	//reads a line from the input stream. Returns null if there's a problem.
	public String readLine() {
		
		String tbr = null;
		
		try {
			tbr = br.readLine();
		} catch (Exception e) {
			System.out.println("Error (" + e.getMessage() + ") occured while trying to receive input from input stream \"" + name + "\".");
		}
		
		return tbr;
	
	}
	
	//reads an double from the input stream. If it's the console, ask the user for another value if it's invalid. If it's not, just just return -1 if invalid.
	public double readDouble() {
		
		double tbr = -1;
		
		while (true) {
		
			String str = readLine();
			boolean valid = true;
			
			try {
				tbr = Double.parseDouble(str);
			} catch (Exception e) {
				if (inp==System.in) {
					pl("Invalid input. Please enter a valid number.");
					valid = false;
				} else { tbr = -1; }
			}
			
			if (valid) break;
		
		}
		
		return tbr;
	
	}
	
	//reads an int from the input stream. If it's the console, ask the user for another value if it's invalid. If it's not, just just return -1 if invalid.
	public int readInt() {
		
		int tbr = -1;
		
		while (true) {
		
			String str = readLine();
			boolean valid = true;
			
			try {
				tbr = Integer.parseInt(str);
			} catch (Exception e) {
				if (inp==System.in) {
					pl("Invalid input. Please enter a valid integer.");
					valid = false;
				} else { tbr = -1; }
			}
			
			if (valid) break;
		
		}
		
		return tbr;
	
	}
	
	//reads 1 byte from the input stream. Returns -1 if there's a problem.
	public int read() {
	
		if (inp==null) return -1;
		
		int tbr = -1;
		
		try {
			tbr = inp.read();
		} catch (Exception e) {
			System.out.println("Error (" + e.getMessage() + ") occurred reading from input stream \"" + name + "\".");
		}
		
		return tbr;
	
	}
	
	//reads up to ba.length bytes into the array ba. Returns the number of bytes read or -1 if there's a problem.
	public int read(byte[] ba) {
	
		if (inp==null) return -1;
		
		int tbr = -1;
		
		try {
			tbr = inp.read(ba);
		} catch (Exception e) {
			System.out.println("Error (" + e.getMessage() + ") occurred reading from input stream \"" + name + "\".");
		}
		
		return tbr;
	
	}
	
	//returns the number of bytes available from the input stream or -1 if there's a problem.
	public int available() {
	
		if (inp==null) return -1;
		
		try {
			return inp.available();
		} catch (Exception e) {
			System.out.println("Error (" + e.getMessage() + ") occurred getting bytes available from input stream \"" + name + "\".");
		}
		
		return -1;
	
	}
	
	//returns true if the BufferedReader is ready to read another line; false otherwise
	public boolean hasLine() {
	
		if (inp==null) return false;
		
		try {
			return br.ready();
		} catch (Exception e) {
			System.out.println("Error (" + e.getMessage() + ") occurred getting ready status from BufferedReader of input stream \"" + name + "\".");
		}
		
		return false;
	
	}
	
	//writes 1 byte to the output stream.
	public void write(int b) {
	
		if (out==null) return;
		
		try {
			out.write(b);
		} catch (Exception e) {
			System.out.println("Error (" + e.getMessage() + ") occurred while writing to output stream \"" + name + "\".");
		}
	
	}
	
	//writes b.length bytes to the output stream.
	public void write(byte[] b) {
	
		if (out==null) return;
		
		try {
			out.write(b);
		} catch (Exception e) {
			System.out.println("Error (" + e.getMessage() + ") occurred while writing to output stream \"" + name + "\".");
		}
	
	}
	
	//closes the input/output stream.
	public void close() {
	
		if (inp==System.in) return;
		
		if (inp!=null) {
			try {
				br.close();
				inp.close();
			} catch (Exception e) {
				System.out.println("Error (" + e.getMessage() + ") occurred while trying to close input stream \"" + name + "\".");
			}
		}
		
		if (out!=null) {
			try {
				ps.close();
				out.close();
			} catch (Exception e) {
				System.out.println("Error (" + e.getMessage() + ") occurred while trying to close output stream \"" + name + "\".");
			}
		}
		
		inp = null;
		out = null;
		br = null;
		ps = null;
	
	}

}