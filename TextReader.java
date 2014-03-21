import java.io.*;

public class TextReader  {

    protected static final int OPENERROR = 1;
    protected static final int CLOSEERROR = 2;
    protected static final int READERROR = 4;
    protected static final int EOF = 256;
    protected String myFileName;
    protected PushbackReader myInFile;
    protected int myErrorFlags;
    private boolean rePrompting;

    public TextReader()
    {
        myErrorFlags = 0;
        myFileName = null;
        myErrorFlags = 0;
        rePrompting = true;
        myInFile = new PushbackReader(new 
        InputStreamReader(System.in));
    }

    public TextReader(String s)
    {
        myErrorFlags = 0;
        myFileName = s;
        myErrorFlags = 0;
        do
            try
            {
                myInFile = new PushbackReader(
                new FileReader(s), 1024);
                rePrompting = false;
                break;
            }
            catch(Exception exception)
            {
                myErrorFlags |= 0x1;
                myFileName = null;
                System.out.println("Can't open input file '" + 
                s + "'");
                TextReader textreader = new TextReader();
                System.out.print(
                "Enter new file name or press enter to end program: ");
                s = textreader.readLine();
                if(s.length() == 0)
                    System.exit(1);
            }
        while(true);
    }

    public void close()
    {
        if(myFileName == null)
            return;
        try
        {
            myInFile.close();
        }
        catch(IOException ioexception)
        {
            System.err.println("Error closing " + 
            myFileName + "\n");
            myErrorFlags |= 0x2;
        }
    }

    public boolean fail()
    {
        return myErrorFlags != 0;
    }

    public boolean eof()
    {
        return (myErrorFlags & 0x100) != 0;
    }

    public byte readByte()
    {
        return (byte)(int)readInteger(-128L, 127L);
    }

    public short readShort()
    {
        return (short)(int)readInteger(-32768L, 32767L);
    }

    public int readInt()
    {
        return (int)readInteger(0xffffffff80000000L, 
        0x7fffffffL);
    }

    public long readLong()
    {
        return readInteger(0x8000000000000000L, 
        0x7fffffffffffffffL);
    }

    public char readlnChar()
    {
        char c = readChar();
        readLine();
        return c;
    }

    public byte readlnByte()
    {
        byte byte0 = readByte();
        readLine();
        return byte0;
    }

    public short readlnShort()
    {
        short word0 = readShort();
        readLine();
        return word0;
    }

    public int readlnInt()
    {
        int i = readInt();
        readLine();
        return i;
    }

    public long readlnLong()
    {
        long l = readLong();
        readLine();
        return l;
    }

    public float readlnFloat()
    {
        float f = readFloat();
        readLine();
        return f;
    }

    public double readlnDouble()
    {
        double d = readDouble();
        readLine();
        return d;
    }

    public boolean readlnBoolean()
    {
        boolean flag = readBoolean();
        readLine();
        return flag;
    }

    public String readlnWord()
    {
        String s = readWord();
        readLine();
        return s;
    }

    public char readChar()
    {
        char c = '\0';
        char c1 = ' ';
        int i = -1;
        try
        {
            for(; ready() && 
            Character.isWhitespace(c1); c1 = (char)i)
            {
                i = myInFile.read();
                if(i != -1)
                    continue;
                myErrorFlags |= 0x100;
                break;
            }

            if(!Character.isWhitespace(c1) && i != -1)
                c = c1;
        }
        catch(IOException ioexception)
        {
            myErrorFlags |= 0x4;
            error("read");
        }
        if(c == 0)
            myErrorFlags |= 0x100;
        return c;
    }

    public char readAnyChar()
    {
        char c = '\0';
        try
        {
            if(ready())
            {
                int i = myInFile.read();
                if(i == -1)
                    myErrorFlags |= 0x100;
                else
                    c = (char)i;
            }
        }
        catch(IOException ioexception)
        {
            myErrorFlags |= 0x4;
            error("read");
        }
        if(c == 0)
            myErrorFlags |= 0x100;
        return c;
    }

    public void unread(char c)
    {
        try
        {
            myInFile.unread((byte)c);
        }
        catch(IOException ioexception)
        {
            myErrorFlags |= 0x4;
            error("unread");
        }
    }

    public char peek()
    {
        int i = 0;
        try
        {
            i = myInFile.read();
        }
        catch(IOException ioexception)
        {
            myErrorFlags |= 0x4;
            error("peek");
        }
        if(i != -1)
            unread((char)i);
        return (char)i;
    }

    public String readLine()
    {
        String s = "";
        try
        {
            do
            {
                int i;
                do
                    i = myInFile.read();
                while(i == 13);
                if(i == -1)
                {
                    myErrorFlags |= 0x100;
                    break;
                }
                if(i == 10)
                    break;
                s = s + (char)i;
            } while(true);
        }
        catch(IOException ioexception)
        {
            myErrorFlags |= 0x4;
            error("readLine");
        }
        if(s.length() == 0)
            s = null;
        return s;
    }

    public String readWord()
    {
        StringBuffer stringbuffer = new StringBuffer(128);
        int i = 0;
        byte byte0 = 32;
        String s = null;
        try
        {
            int j;
            do
                j = myInFile.read();
            while(ready() && j != -1 && 
            Character.isWhitespace((char)j));
            for(; ready() && 
            !Character.isWhitespace((char)j); j = myInFile.read())
            {
                i++;
                stringbuffer.append((char)j);
            }

            if(i > 0)
            {
                unread((char)j);
                s = stringbuffer.toString();
            } else
            {
                myErrorFlags |= 0x100;
            }
        }
        catch(IOException ioexception)
        {
            myErrorFlags |= 0x4;
            error("readWord");
        }
        return s;
    }

    public double readDouble()
    {
        String s = readWord();
        if(s != null)
            return Double.parseDouble(s);
        else
            return 0.0D;
    }

    public float readFloat()
    {
        String s = readWord();
        if(s != null)
            return Float.parseFloat(s);
        else
            return 0.0F;
    }

    public boolean readBoolean()
    {
        boolean flag = false;
        String s = readWord();
        if(s != null)
            if(s.equalsIgnoreCase("true") || 
            s.equalsIgnoreCase("t") || 
            s.equalsIgnoreCase("yes") || 
            s.equalsIgnoreCase("y") || s.equals("1"))
                flag = true;
            else
            if(s.equalsIgnoreCase("false") || 
            s.equalsIgnoreCase("f") || 
            s.equalsIgnoreCase("no") || 
            s.equalsIgnoreCase("n") || s.equals("0"))
                flag = false;
        return flag;
    }

    private void error(String s)
    {
        System.out.println("\n***Failure in " + 
        s + " message.");
        if(!rePrompting)
        {
            System.out.println(
            "Press enter to terminate program . . . ");
            char c = readAnyChar();
        }
        System.out.println(
        "Program terminating . . . ");
        System.exit(1);
    }

    private boolean ready()
        throws IOException
    {
        return myFileName == null || myInFile.ready();
    }

    private long readInteger(long l, long l1)
    {
        long l2 = 0L;
        String s = readWord();
        if(s != null)
            l2 = Long.parseLong(s);
        else
            l2 = 0L;
        if(l2 < l || l2 > l1)
            throw new NumberFormatException(s);
        else
            return l2;
    }
}

