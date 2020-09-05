package ru.redguy.tftpserver;

/////////////////////////////////////////////////////////
//READ packet: put the right opcode and filename, mode //
//in the 'message'                                     //
/////////////////////////////////////////////////////////
public final class TFTPread extends TFTPpacket {


    // Constructors
    protected TFTPread() {
    }

    //specify the filename and transfer mode
    public TFTPread(String filename, String dataMode) {
        length = 2 + filename.length() + 1 + dataMode.length() + 1;
        message = new byte[length];

        put(opOffset, tftpRRQ);
        put(fileOffset, filename, (byte) 0);
        put(fileOffset + filename.length() + 1, dataMode, (byte) 0);
    }

// Accessors

    public String fileName() {
        return this.get(fileOffset, (byte) 0);
    }

    public String requestType() {
        String fname = fileName();
        return this.get(fileOffset + fname.length() + 1, (byte) 0);
    }
}
